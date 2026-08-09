CREATE TABLE `devices` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL,
    `type` VARCHAR(100) NOT NULL,
    `user_id` BIGINT NOT NULL,
    `location` VARCHAR(255),
    PRIMARY KEY (`id`),
    KEY `idx_device_user_id` (`user_id`),
    CONSTRAINT `fk_device_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
);