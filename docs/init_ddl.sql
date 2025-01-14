CREATE TABLE `users` (
    `id` bigint PRIMARY KEY AUTO_INCREMENT,
    `username` varchar(255) NOT NULL,
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

--

CREATE TABLE `queue_token` (
    `id` bigint PRIMARY KEY AUTO_INCREMENT,
    `token_uuid` binary(16) UNIQUE NOT NULL,
    `user_id` bigint NOT NULL,     -- FK를 걸지 않음
    `concert_id` bigint NOT NULL,  -- FK를 걸지 않음
    `is_active` boolean NOT NULL DEFAULT false,
    `created_at` timestamp(6) NOT NULL,
    `expired_at` timestamp(6) NOT NULL
);

CREATE INDEX idx_queue_token_user_id ON queue_token(user_id);
CREATE INDEX idx_queue_token_concert_id ON queue_token(concert_id);

--

CREATE TABLE `concert` (
    `id` bigint PRIMARY KEY AUTO_INCREMENT,
    `title` varchar(255) NOT NULL,
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

--

CREATE TABLE `concert_schedule` (
    `id` bigint PRIMARY KEY AUTO_INCREMENT,
    `concert_id` bigint NOT NULL,
    `event_date` datetime NOT NULL,
    `total_seat_count` integer NOT NULL DEFAULT 0,
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

ALTER TABLE `concert_schedule` ADD FOREIGN KEY (`concert_id`) REFERENCES `concert` (`id`);

--

CREATE TABLE `seat` (
    `id` bigint PRIMARY KEY AUTO_INCREMENT,
    `concert_schedule_id` bigint NOT NULL,
    `seat_no` varchar(255) NOT NULL,
    `is_available` boolean NOT NULL DEFAULT true,
    `price` integer NOT NULL,
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

ALTER TABLE `seat` ADD FOREIGN KEY (`concert_schedule_id`) REFERENCES `concert_schedule` (`id`);

--

CREATE TABLE `reservation` (
    `id` bigint PRIMARY KEY AUTO_INCREMENT,
    `user_id` bigint NOT NULL,
    `seat_id` bigint NOT NULL,
    `status` ENUM('PENDING_PAYMENT', 'CONFIRMED', 'CANCELED') NOT NULL DEFAULT 'PENDING_PAYMENT',
    `payment_price` integer NOT NULL,
    `temp_reservation_expired_at` timestamp NOT NULL,
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

ALTER TABLE `reservation` ADD FOREIGN KEY (`seat_id`) REFERENCES `seat` (`id`);
ALTER TABLE `reservation` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--

CREATE TABLE `point` (
    `id` bigint PRIMARY KEY AUTO_INCREMENT,
    `user_id` bigint NOT NULL,
    `balance` integer NOT NULL DEFAULT 0,
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

ALTER TABLE `point` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--

CREATE TABLE `point_history` (
    `id` bigint PRIMARY KEY AUTO_INCREMENT,
    `point_id` bigint NOT NULL,
    `user_id` bigint NOT NULL,
    `transaction_type` ENUM('USE', 'CHARGE') NOT NULL,
    `amount` integer NOT NULL,
    `balance` integer NOT NULL,
    `ref_id` bigint,
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

ALTER TABLE `point_history` ADD FOREIGN KEY (`ref_id`) REFERENCES `reservation` (`id`);
ALTER TABLE `point_history` ADD FOREIGN KEY (`point_id`) REFERENCES `point` (`id`);
ALTER TABLE `point_history` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
