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

