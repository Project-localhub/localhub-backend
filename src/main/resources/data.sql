--메시지 PROCEDURE

DELIMITER $$

CREATE PROCEDURE insert_messages(IN total INT)
BEGIN
  DECLARE i INT DEFAULT 1;

  WHILE i <= total DO
    INSERT INTO message (content, sender, user_id, chatroom_id)
    VALUES (
      CONCAT('테스트 메시지 ', i),
      'owner',
      4,
      33
    );

    SET i = i + 1;
END WHILE;
END$$

DELIMITER ;



-- 레스토랑 PROCEDURE


DELIMITER $$

CREATE PROCEDURE insert_restaurants_gangnam_random(IN total INT)
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE rand_lat DECIMAL(10,7);
    DECLARE rand_lng DECIMAL(10,7);

    WHILE i <= total DO

        SET rand_lat = 37.495000 + (RAND() * (37.525000 - 37.495000));
        SET rand_lng = 127.020000 + (RAND() * (127.060000 - 127.020000));

        INSERT INTO restaurant (
            name,
            business_number,
            description,
            category,
            phone,
            address,
            latitude,
            longitude,
            divide,
            open_time,
            close_time,
            has_break_time,
            break_start_time,
            break_end_time,
            owner_id
        )
        VALUES (
            CONCAT('강남테스트식당_', i),
            LPAD(i, 10, '0'),
            CONCAT('강남 랜덤 테스트 식당 ', i),
            '한식',
            CONCAT('02-555-', LPAD(i, 4, '0')),
            CONCAT('서울시 강남구 랜덤로 ', i),
            rand_lat,
            rand_lng,
            '강남구',
            '09:00',
            '22:00',
            IF(i % 2 = 0, 1, 0),
            IF(i % 2 = 0, '15:00', NULL),
            IF(i % 2 = 0, '16:00', NULL),
            CONCAT('restaurant/gangnam_', i, '.jpg'),
            4
        );

        SET i = i + 1;
    END WHILE;
END$$

DELIMITER ;



