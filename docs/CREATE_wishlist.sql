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

CREATE TABLE wish (
	id INT NOT NULL UNIQUE AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    link VARCHAR(255) NOT NULL,
    
    PRIMARY KEY(id)
);

CREATE TABLE wishlist (
	user_id INT NOT NULl,
    wish_id INT NOT NULL UNIQUE,
    
    PRIMARY KEY(user_id, wish_id),
    FOREIGN KEY(user_id) REFERENCES User(id)
		ON DELETE CASCADE,
	FOREIGN KEY(wish_id) REFERENCES Wish(id)
		ON DELETE CASCADE
);
