package com.home_energy_tracker.ingestion_service.service;

import com.home_energy_tracker.kafka.event.EnergyUsageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class KafkaPublisher {
    private final KafkaTemplate<String, EnergyUsageEvent> kafkaTemplate;
    private final ConcurrentLinkedQueue<RetryItem> retryQueue = new ConcurrentLinkedQueue<>();

    private static final int MAX_RETRIES = 5;
    private static final int MAX_QUEUE_SIZE = 10_000;

    public KafkaPublisher(KafkaTemplate<String, EnergyUsageEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(EnergyUsageEvent event) {
        try {
            var future = kafkaTemplate.send("energy-usage-events", event);
            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to publish event asynchronously, enqueueing for retry", ex);
                    enqueue(event);
                } else if (result != null && result.getRecordMetadata() != null) {
                    log.info("Message sent: topic={} partition={} offset={}", result.getRecordMetadata().topic(), result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                } else {
                    log.warn("Message sent but no metadata returned");
                }
            });
        } catch (Exception ex) {
            log.error("Publish failed synchronously, enqueueing", ex);
            enqueue(event);
        }
    }

    private void enqueue(EnergyUsageEvent event) {
        if (retryQueue.size() >= MAX_QUEUE_SIZE) {
            log.warn("Retry queue full, dropping event");
            return;
        }
        retryQueue.add(new RetryItem(event));
    }

    @Scheduled(fixedDelayString = "${kafka.retry-interval-ms:10000}")
    public void retryFailed() {
        int processed = 0;
        for (int i = 0; i < 1000; i++) {
            RetryItem item = retryQueue.poll();
            if (item == null) break;

            if (item.attempts.incrementAndGet() > MAX_RETRIES) {
                log.error("Dropping event after {} attempts: {}", item.attempts.get(), item.event);
                continue;
            }

            try {
                var f = kafkaTemplate.send("energy-usage-events", item.event);
                f.whenComplete((res, ex) -> {
                    if (ex != null) {
                        log.error("Retry failed for event, re-enqueueing", ex);
                        enqueue(item.event);
                    } else {
                        log.info("Retry succeeded for event");
                    }
                });
            } catch (Exception ex) {
                log.error("Retry send exception, re-enqueueing", ex);
                enqueue(item.event);
            }
            processed++;
        }
        if (processed > 0) log.info("Processed {} retry items", processed);
    }

    private static class RetryItem {
        final EnergyUsageEvent event;
        final AtomicInteger attempts = new AtomicInteger(0);

        RetryItem(EnergyUsageEvent e) { this.event = e; }
    }
}
