SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE concert_schedule;
TRUNCATE concert;
SET FOREIGN_KEY_CHECKS = 1;

SET SESSION cte_max_recursion_depth = 1000000;

-- concert 테이블에 100만개의 데이터 생성
INSERT INTO concert (title, reservation_open_date_time, created_at, updated_at)
WITH RECURSIVE cte (n) AS
(
  SELECT 1
  UNION ALL
  SELECT n + 1 FROM cte WHERE n < 1000000 -- 100만개의 데이터 생성
)
SELECT
    CONCAT('Title', LPAD(n, 7, '0')) AS title,
    DATE_FORMAT(NOW() + INTERVAL FLOOR(RAND() * 30) DAY, '%y-%m-%d %11:00:00'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM cte;


-- concert_schedule 테이블에 100만개의 데이터 생성
INSERT INTO concert_schedule (concert_id, event_date, total_seat_count, created_at, updated_at)
WITH RECURSIVE cte (n) AS
(
    SELECT 1
    UNION ALL
    SELECT n + 1 FROM cte WHERE n < 1000000
)
SELECT
    FLOOR(RAND() * 100000) + 1 AS concert_id,  -- concert_id는 1부터 100000까지 랜덤 생성
    DATE_FORMAT(NOW() + INTERVAL FLOOR(RAND() * 30) DAY + INTERVAL FLOOR(RAND() * 24) HOUR, '%y-%m-%d %H:00:00'),  -- 오늘 이후의 날짜를 랜덤 생성
    50,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM cte;
