CREATE DATABASE IF NOT EXISTS dears;
USE dears;

CREATE TABLE Users(
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(25) NOT NULL,
    password VARCHAR(25) NOT NULL,
    avatar VARCHAR(25) NOT NULL,
    birthday DATE NOT NULL
);

CREATE TABLE PetType(
	type_id INT AUTO_INCREMENT PRIMARY KEY,
    type_name VARCHAR(25) NOT NULL
);
INSERT INTO PetType(type_name) VALUES
    ('Deer'),
    ('Bear');

CREATE TABLE Food(
    food_id INT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(25) NOT NULL,
    food_points INT NOT NULL
);
INSERT INTO Food(type, food_points) VALUES
    ('tree bark', 5),
    ('berries', 10),
    ('mushroom', 20),
    ('honey', 5),
    ('salmon', 20);

CREATE TABLE PetFood(
	type_id INT NOT NULL, 
    food_id INT NOT NULL, 
    PRIMARY KEY (type_id, food_id), 
    FOREIGN KEY (type_id) REFERENCES PetTYpe(type_id), 
    FOREIGN KEY (food_id) REFERENCES Food(food_id)
); 
INSERT INTO PetFood(type_id, food_id) VALUES 
	(1, 1),
    (1, 2), 
    (1, 3),
    (2, 2), 
    (2, 4), 
    (2, 5); 

CREATE TABLE AgeStage(
    age_id INT AUTO_INCREMENT PRIMARY KEY,
    age_stage VARCHAR(25) NOT NULL,
    meter_max INT NOT NULL
);
INSERT INTO AgeStage(age_stage, meter_max) VALUES
    ('baby', 10),
    ('teen', 20),
    ('adult', 40);

CREATE TABLE Pet(
    pet_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    type_id INT NOT NULL,
    growth_points INT NOT NULL,
    age_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Users(user_id),
    FOREIGN KEY (type_id) REFERENCES PetType(type_id),
    FOREIGN KEY (age_id) REFERENCES AgeStage(age_id)
);

CREATE TABLE Happiness(
    happiness_id INT AUTO_INCREMENT PRIMARY KEY,
    age_id INT NOT NULL,
    meter_max INT NOT NULL,
    FOREIGN KEY (age_id) REFERENCES AgeStage(age_id)
);
INSERT INTO Happiness(meter_max, age_id) VALUES
    (10, 1),
    (20, 2),
    (40, 3);

CREATE TABLE Energy(
    energy_id INT AUTO_INCREMENT PRIMARY KEY,
    age_id INT NOT NULL,
    meter_max INT NOT NULL,
    FOREIGN KEY (age_id) REFERENCES AgeStage(age_id)
);
INSERT INTO Energy(meter_max, age_id) VALUES
    (10, 1),
    (20, 2),
    (40, 3);

CREATE TABLE Hunger(
    hunger_id INT AUTO_INCREMENT PRIMARY KEY,
    age_id INT NOT NULL,
    meter_max INT NOT NULL,
    FOREIGN KEY (age_id) REFERENCES AgeStage(age_id)
);
INSERT INTO Hunger(meter_max, age_id) VALUES
    (10, 1),
    (20, 2),
    (40, 3);

CREATE TABLE Journal(
    journal_id INT AUTO_INCREMENT PRIMARY KEY,
    pet_id INT NOT NULL,
    name VARCHAR(25) NOT NULL,
    FOREIGN KEY (pet_id) REFERENCES Pet(pet_id)
);

CREATE TABLE Entry(
    entry_id INT AUTO_INCREMENT PRIMARY KEY,
    journal_id INT NOT NULL,
    date DATE NOT NULL,
    summary VARCHAR(150) NOT NULL,
    mood VARCHAR(150) NOT NULL,
    FOREIGN KEY (journal_id) REFERENCES Journal(journal_id)
);

CREATE TABLE ChatChoices(
    chat_id INT AUTO_INCREMENT PRIMARY KEY,
    choice VARCHAR(150) NOT NULL
);
INSERT INTO ChatChoices(choice) VALUE
    ('Hello'),
    ('How are you?'),
    ('Tell me a joke!');

CREATE TABLE History(
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    chat_id INT NOT NULL,
    history VARCHAR(255) NOT NULL,
    FOREIGN KEY(chat_id) REFERENCES ChatChoices(chat_id)
);
