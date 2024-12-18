# MyProject > src > det > Game.java 


# Guessing Game

A Java-based game where players guess a 4-digit number with feedback after each attempt. Scores are stored in a MySQL database, and the best score is displayed after each game.

---

## Features
- **Random 4-Digit Number**: A new number is generated for each game.
- **Feedback System**:
  - `+`: Correct digit in the correct position.
  - `-`: Correct digit in the wrong position.
- **Score Tracking**: Scores are saved and ranked in a database.
- **Leaderboard**: Displays the top score.

---

## Requirements
- **Java JDK**: Version 8 or later.
- **MySQL Database**:
  - Database: `guessing_game`
  - Table:
    ```sql
    CREATE TABLE scores (
        id INT AUTO_INCREMENT PRIMARY KEY,
        name VARCHAR(50),
        guesses INT,
        time_taken DOUBLE,
        score DOUBLE
    );
    ```
- **MySQL JDBC Driver**.

---

## Setup & Run
1. Clone the repository:
   ```bash
   git clone https://github.com/VISION-17/guessing_game.git
   cd guessing_game
