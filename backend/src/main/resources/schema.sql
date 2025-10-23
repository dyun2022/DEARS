CREATE DATABASE IF NOT EXISTS dears;
USE dears;

CREATE TABLE Users(
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(25) NOT NULL,
    password VARCHAR(25) NOT NULL,
    avatar VARCHAR(25) NOT NULL
);

CREATE TABLE Pet(
    pet_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(25) NOT NULL,
    type VARCHAR(25) NOT NULL,
    hunger INT NOT NULL,
    happiness INT NOT NULL,
    energy INT NOT NULL,
    growthPoints INT NOT NULL,
    ageStage VARCHAR(25) NOT NULL
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

CREATE TABLE Food(
    food_id INT AUTO_INCREMENT PRIMARY KEY
    type VARCHAR(25) NOT NULL,
    food_points INT NOT NULL,
    FOREIGN KEY (pet_id) REFERENCES Pet(pet_id)
);
INSERT INTO Food(type, food_points) VALUES
    ('tree bark', 5)
    ('berries', 10)
    ('mushroom', 20)
    ('honey', 5)
    ('salmon', 20);

CREATE TABLE AgeStage(
    age_id INT AUTO_INCREMENT PRIMARY KEY,
    age_stage VARCHAR(25) NOT NULL,
    meter_max INT NOT NULL,
    FOREIGN KEY (pet_id) REFERENCES Pet(pet_id)
);
INSERT INTO AgeStage(age_stage, meter_max) VALUES
    ('baby', 10)
    ('teen' 20)
    ('adult' 40);

CREATE TABLE Happiness(
    happiness_id INT AUTO_INCREMENT PRIMARY KEY,
    happiness_points INT NOT NULL,
    FOREIGN KEY (age_id) REFERENCES AgeStage(age_id)
);
INSERT INTO Happiness(happiness_points) VALUES
    (5)
    (10)
    (20);

CREATE TABLE Energy(
    energy_id INT AUTO_INCREMENT PRIMARY KEY,
    energy_points INT NOT NULL,
    FOREIGN KEY (age_id) REFERENCES AgeStage(age_id)
);
INSERT INTO Happiness(happiness_points) VALUES
    (5)
    (10)
    (20);

CREATE TABLE Hunger(
    hunger_id INT AUTO_INCREMENT PRIMARY KEY,
    hunger_points INT NOT NULL,
    FOREIGN KEY (age_id) REFERENCES AgeStage(age_id)
);
INSERT INTO Happiness(happiness_points) VALUES
    (5)
    (10)
    (20);

CREATE TABLE Journal(
    journal_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(25) NOT NULL
    FOREIGN KEY (pet_id) REFERENCES Pet(pet_id)
);

CREATE TABLE Entry(
    entry_id INT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    summary VARCHAR(150) NOT NULL,
    mood VARCHAR(150) NOT NULL,
    FOREIGN KEY (journal_id) REFERENCES Journal(journal_id)
);

CREATE TABLE ChatChoices(
    chat_id INT AUTO_INCREMENT PRIMARY KEY,
    choice VARCHAR(150) NOT NULL,
    FOREIGN KEY (pet_id) REFERENCES Pet(pet_id)
);
INSERT INTO ChatChoices(choice) VALUE
    ('Hello')
    ('How are you?')
    ('Tell me a joke!')

CREATE TABLE History(
    history_id AUTO_INCREMENT PRIMARY KEY,
    history VARCHAR(255) NOT NULL,
    FOREIGN KEY(chat_id) REFERENCES ChatChoices(chat_id)
);
