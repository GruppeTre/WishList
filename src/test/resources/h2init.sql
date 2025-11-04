DROP ALL OBJECTS;

CREATE TABLE account (
                    id INT NOT NULL UNIQUE AUTO_INCREMENT,
                    password VARCHAR(255) NOT NULL,
                    mail VARCHAR(100) NOT NULL UNIQUE,
                    firstName VARCHAR(50) NOT NULL,
                    lastName VARCHAR(50) NOT NULL,

                    PRIMARY KEY(id)
);

Create TABLE Wish (
                    id INT NOT NULL UNIQUE AUTO_INCREMENT,
                    name VARCHAR(75) NOT NULL,
                    link VARCHAR(255) NOT NULL,
                    isReserved BIT NOT NULL,

                    PRIMARY KEY(id)
);

CREATE TABLE Wishlist(
                    user_id INT NOT NULL,
                    wish_id INT NOT NULL,

                    PRIMARY KEY(user_id, wish_id),
                    FOREIGN KEY(user_id) REFERENCES account(id)
		                ON DELETE CASCADE,
	                FOREIGN KEY(wish_id) REFERENCES Wish(id)
		                ON DELETE CASCADE
);

CREATE TABLE Reservation (
                    user_id INT NOT NULL,
                    wish_id INT NOT NULL UNIQUE,

                    PRIMARY KEY(user_id, wish_id),
                    FOREIGN KEY(user_id) REFERENCES account(id)
                        ON DELETE CASCADE,
                    FOREIGN KEY(wish_id) REFERENCES Wish(id)
                        ON DELETE CASCADE
);

INSERT INTO account (password, mail, firstname, lastName, refString)
       VALUES ('adam1234', 'adam@mail.dk', 'Adam', 'Adamsen', '80ee1e11b3b393efd0872de7'),
	('erik1234', 'Erik@gmail.com', 'Erik', 'Eriksen','0a0609f01458afb981cfeead');

INSERT INTO Wish (name, link, isReserved) VALUES ('Uldsokker 5stk', 'http://www.etgenerisklink.com', 1),
                                                 ('The Kopper', 'http://www.togenerisklink.com', 0),
                                                 ('Fjernsyn', 'http://www.tregenerisklink.com', 0),
                                                 ('Tegneblok', 'http://www.firegenerisklink.com', 1);

INSERT INTO Reservation (user_id, wish_id) VALUES (2, 1), (1, 4);

INSERT INTO Wishlist (user_id, wish_id) VALUES (1, 1), (1, 2), (2, 3), (2, 4);