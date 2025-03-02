drop table if exists point_history;
drop table if exists point;
drop table if exists payment;
drop table if exists reservation;
drop table if exists seat;
drop table if exists concert_schedule;
drop table if exists concert;
drop table if exists queue_token;
drop table if exists users;
drop table if exists payment_completed_event_outbox;


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
    `reservation_open_date_time` datetime NOT NULL,
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE INDEX idx_reservation_seat_id ON concert(reservation_open_date_time);

--

CREATE TABLE `concert_schedule` (
    `id` bigint PRIMARY KEY AUTO_INCREMENT,
    `concert_id` bigint NOT NULL,
    `event_date` datetime NOT NULL,
    `total_seat_count` integer NOT NULL DEFAULT 0,
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_concert_id_event_date ON concert_schedule(concert_id, event_date);
ALTER TABLE `concert_schedule` ADD FOREIGN KEY (`concert_id`) REFERENCES `concert` (`id`);

--

CREATE TABLE `seat` (
    `id` bigint PRIMARY KEY AUTO_INCREMENT,
    `concert_schedule_id` bigint NOT NULL,
    `seat_no` varchar(255) NOT NULL,
    `is_available` boolean NOT NULL DEFAULT true,
    `price` integer NOT NULL,
    `temp_reservation_expired_at` timestamp(6),
    `version` bigint NOT NULL DEFAULT 0,
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
    `temp_reservation_expired_at` timestamp(6) NOT NULL,
    `confirmed_at` timestamp(6),
    `version` bigint NOT NULL DEFAULT 0,
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_reservation_seat_id ON reservation(seat_id);
ALTER TABLE `reservation` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--

CREATE TABLE `payment` (
    `id` bigint PRIMARY KEY AUTO_INCREMENT,
    `user_id` bigint NOT NULL,
    `reservation_id` bigint NOT NULL,
    `amount` integer NOT NULL,
    `status` ENUM('COMPLETED', 'CANCELED', 'IN_PROGRESS') NOT NULL DEFAULT 'IN_PROGRESS',
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_payment_user_id ON payment(user_id);
CREATE INDEX idx_payment_reservation_id ON payment(reservation_id);

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

CREATE INDEX idx_point_history_ref_id ON point_history(ref_id);
CREATE INDEX idx_point_history_point_id ON point_history(point_id);
ALTER TABLE `point_history` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--

create table `payment_completed_event_outbox`
(
    `id` bigint PRIMARY KEY AUTO_INCREMENT,
    `retry_count` int NOT NULL DEFAULT 0,
    `payment_id` bigint NOT NULL,
    `payload` json NOT NULL,
    `status` ENUM('FAILED', 'INIT', 'PUBLISHED') NOT NULL DEFAULT 'INIT',
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_payment_completed_event_outbox_payment_id on payment_completed_event_outbox (payment_id);
CREATE INDEX idx_payment_completed_event_outbox_status on payment_completed_event_outbox (status);
