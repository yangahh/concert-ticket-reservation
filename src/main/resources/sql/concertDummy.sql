SET SESSION cte_max_recursion_depth = 1000000;

INSERT INTO concert (title, reservation_open_date_time, created_at, updated_at)
WITH RECURSIVE cte (n) AS
(
  SELECT 1
  UNION ALL
  SELECT n + 1 FROM cte WHERE n < 500000 -- 50만개의 데이터 생성
)
SELECT
    CONCAT('Title', LPAD(n, 6, '0')) AS title,
    DATE_FORMAT(NOW() + INTERVAL FLOOR(RAND() * 30) DAY, '%y-%m-%d %11:00:00'),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM cte;


INSERT INTO concert_schedule (concert_id, event_date, total_seat_count, created_at, updated_at)
WITH RECURSIVE cte (n) AS
(
    SELECT 1
    UNION ALL
    SELECT n + 1 FROM cte WHERE n < 10
)
SELECT
    1 AS concert_id,  -- concert_id는 1로 고정
    DATE_FORMAT(NOW() + INTERVAL FLOOR(RAND() * 30) DAY + INTERVAL FLOOR(RAND() * 24) HOUR, '%y-%m-%d %H:00:00'),  -- 오늘 이후의 날짜를 랜덤 생성
    50,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM cte;
