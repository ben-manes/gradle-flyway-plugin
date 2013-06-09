CREATE TABLE user_txn (
  id INT PRIMARY KEY AUTO_INCREMENT,
  first_name VARCHAR(255) NOT NULL,
  last_name VARCHAR(255) NOT NULL
);

CREATE UNIQUE INDEX name_idx ON user_txn (first_name, last_name);
