DROP ALL OBJECTS;

DROP DATABASE IF EXISTS wishlist_project;
CREATE DATABASE wishlist_project
DEFAULT CHARACTER SET utf8mb4;

USE wishlist_project;

CREATE TABLE User (
                      id INT NOT NULL UNIQUE AUTO_INCREMENT,
                      password VARCHAR(255) NOT NULL,
                      mail VARCHAR(100) NOT NULL UNIQUE,
                      firstName VARCHAR(50) NOT NULL,
                      lastName VARCHAR(50) NOT NULL,

                      PRIMARY KEY(id)
);

INSERT IGNORE INTO User (password, mail, firstname, lastName)
       VALUES ("adam@1234", "adam@mail.dk", "Adam", "Adamsen"),
	("erik@1234", "Erik@gmail.com", "Erik", "Eriksen");