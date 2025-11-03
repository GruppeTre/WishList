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

Create Wish (
                    id INT NOT NULL UNIQUE AUTO_INCREMENT,
                    name VARCHAR(75) NOT NULL,
                    link VARCHAR(255) NOT NULL,
                    isReserved BIT NOT NULL,

                    PRIMARY KEY(id)
);

CREATE Wishlist(
                    user_id INT NOT NULL,
                    wish_id INT NOT NULL
)

INSERT IGNORE INTO User (password, mail, firstname, lastName)
       VALUES ("adam@1234", "adam@mail.dk", "Adam", "Adamsen"),
	("erik@1234", "Erik@gmail.com", "Erik", "Eriksen");