create table accounts(
ID        INT            NOT NULL PRIMARY KEY AUTO_INCREMENT,
Name      VARCHAR(50)    NOT NULL,
Password  VARCHAR(18)    NOT NULL,
Amount    DOUBLE         NOT NULL
);

create table sessions(
ID                VARCHAR(100)    NOT NULL PRIMARY KEY,
Name              VARCHAR(50)     NOT NULL,
CreationDate      DATETIME        NOT NULL
);

CREATE TABLE transaction_history(
Date        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
Name        VARCHAR(50)      NOT NULL,
Operation   VARCHAR(10)      NOT NULL,
Amount      DOUBLE           NOT NULL
);

DELIMITER $$
CREATE TRIGGER transaction_history BEFORE UPDATE ON accounts
FOR EACH ROW
BEGIN
IF (NEW.Amount > OLD.Amount) THEN
            SET @Operation = "Deposit";
            SET @OpValue = NEW.Amount - OLD.Amount;
      ELSE
            SET @Operation = "Withdraw";
            SET @OpValue = OLD.Amount - NEW.Amount;
      END IF;
INSERT INTO transaction_history VALUES(NULL , OLD.Name, @Operation, @OpValue);
end$$
DELIMITER ;
