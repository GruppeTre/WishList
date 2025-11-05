INSERT IGNORE INTO account (password, mail, firstName, lastName, refString)
       VALUES ('$argon2id$v=19$m=16384,t=2,p=1$Hkk7F7M71WIJjuw8uym1wg$lMWg2Vv6DnISI6YlpKkC6CjzcF45iBKbfHck+kORYmg', 'admin@admin', 'Test', 'Name', '63985ae4a6f75b68256b7a12'),
       ('$argon2id$v=19$m=16384,t=2,p=1$Hkk7F7M71WIJjuw8uym1wg$lMWg2Vv6DnISI6YlpKkC6CjzcF45iBKbfHck+kORYmg', 'admin2@admin', 'Test2', 'Name', '0a0609f01458afb981cfeead'),
       ('$argon2id$v=19$m=16384,t=2,p=1$Hkk7F7M71WIJjuw8uym1wg$lMWg2Vv6DnISI6YlpKkC6CjzcF45iBKbfHck+kORYmg', 'admin3@admin', 'Test3', 'Name', '80ee1e11b3b393efd0872de7');
