-- Insert into user
INSERT INTO `users` (`id`, `username`, `created_at`, `updated_at`) VALUES
    (1, 'test_user', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert into concert
INSERT INTO `concert` (`id`, `title`, `reservation_open_date_time`, `created_at`, `updated_at`) VALUES
    (1, 'Test Concert1', '2024-12-01 11:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO `concert` (`id`, `title`, `reservation_open_date_time`, `created_at`, `updated_at`) VALUES
    (2, 'Test Concert2', '2025-05-01 11:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO `concert` (`id`, `title`, `reservation_open_date_time`, `created_at`, `updated_at`) VALUES
    (3, 'Test Concert3', '2026-12-01 11:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert into concert_schedule
INSERT INTO `concert_schedule` (`id`, `concert_id`, `event_date`, `total_seat_count`, `created_at`, `updated_at`) VALUES
(1, 1, '2025-03-30 03:00:00', 50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 1, '2025-03-30 11:00:00', 50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 1, '2025-03-31 11:00:00', 50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert into seat for concert_schedule_id = 1
INSERT INTO `seat` (`id`, `concert_schedule_id`, `seat_no`, `is_available`, `price`, `temp_reservation_expired_at` ,`created_at`, `updated_at`) VALUES
(1, 1, '1', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 1, '2', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 1, '3', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 1, '4', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 1, '5', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 1, '6', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 1, '7', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 1, '8', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 1, '9', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 1, '10', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(11, 1, '11', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(12, 1, '12', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(13, 1, '13', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(14, 1, '14', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(15, 1, '15', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(16, 1, '16', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(17, 1, '17', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(18, 1, '18', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(19, 1, '19', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(20, 1, '20', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(21, 1, '21', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(22, 1, '22', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(23, 1, '23', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(24, 1, '24', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(25, 1, '25', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(26, 1, '26', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(27, 1, '27', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(28, 1, '28', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(29, 1, '29', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(30, 1, '30', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(31, 1, '31', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(32, 1, '32', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(33, 1, '33', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(34, 1, '34', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(35, 1, '35', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(36, 1, '36', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(37, 1, '37', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(38, 1, '38', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(39, 1, '39', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(40, 1, '40', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(41, 1, '41', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(42, 1, '42', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(43, 1, '43', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(44, 1, '44', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(45, 1, '45', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(46, 1, '46', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(47, 1, '47', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(48, 1, '48', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(49, 1, '49', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(50, 1, '50', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert into seat for concert_schedule_id = 2
INSERT INTO `seat` (`id`, `concert_schedule_id`, `seat_no`, `is_available`, `price`, `temp_reservation_expired_at` ,`created_at`, `updated_at`) VALUES
(51, 2, '1', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(52, 2, '2', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(53, 2, '3', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(54, 2, '4', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(55, 2, '5', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(56, 2, '6', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(57, 2, '7', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(58, 2, '8', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(59, 2, '9', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(60, 2, '10', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(61, 2, '11', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(62, 2, '12', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(63, 2, '13', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(64, 2, '14', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(65, 2, '15', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(66, 2, '16', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(67, 2, '17', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(68, 2, '18', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(69, 2, '19', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(70, 2, '20', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(71, 2, '21', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(72, 2, '22', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(73, 2, '23', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(74, 2, '24', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(75, 2, '25', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(76, 2, '26', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(77, 2, '27', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(78, 2, '28', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(79, 2, '29', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(80, 2, '30', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(81, 2, '31', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(82, 2, '32', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(83, 2, '33', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(84, 2, '34', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(85, 2, '35', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(86, 2, '36', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(87, 2, '37', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(88, 2, '38', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(89, 2, '39', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(90, 2, '40', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(91, 2, '41', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(92, 2, '42', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(93, 2, '43', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(94, 2, '44', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(95, 2, '45', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(96, 2, '46', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(97, 2, '47', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(98, 2, '48', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(99, 2, '49', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(100, 2, '50', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert into seat for concert_schedule_id = 3
INSERT INTO `seat` (`id`, `concert_schedule_id`, `seat_no`, `is_available`, `price`, `temp_reservation_expired_at` ,`created_at`, `updated_at`) VALUES
(101, 3, '1', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(102, 3, '2', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(103, 3, '3', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(104, 3, '4', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(105, 3, '5', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(106, 3, '6', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(107, 3, '7', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(108, 3, '8', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(109, 3, '9', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(110, 3, '10', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(111, 3, '11', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(112, 3, '12', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(113, 3, '13', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(114, 3, '14', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(115, 3, '15', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(116, 3, '16', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(117, 3, '17', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(118, 3, '18', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(119, 3, '19', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(120, 3, '20', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(121, 3, '21', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(122, 3, '22', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(123, 3, '23', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(124, 3, '24', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(125, 3, '25', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(126, 3, '26', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(127, 3, '27', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(128, 3, '28', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(129, 3, '29', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(130, 3, '30', true, 100, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(131, 3, '31', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(132, 3, '32', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(133, 3, '33', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(134, 3, '34', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(135, 3, '35', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(136, 3, '36', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(137, 3, '37', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(138, 3, '38', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(139, 3, '39', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(140, 3, '40', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(141, 3, '41', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(142, 3, '42', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(143, 3, '43', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(144, 3, '44', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(145, 3, '45', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(146, 3, '46', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(147, 3, '47', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(148, 3, '48', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(149, 3, '49', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(150, 3, '50', false, 100, DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
