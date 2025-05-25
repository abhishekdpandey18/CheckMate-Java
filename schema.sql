-- schema.sql

-- Step 1: Create Database
CREATE DATABASE IF NOT EXISTS chess_game;

-- Step 2: Use the database
USE chess_game;

-- Step 3: Table to store games
CREATE TABLE IF NOT EXISTS Game (
    game_id INT AUTO_INCREMENT PRIMARY KEY,
    white_player VARCHAR(50),
    black_player VARCHAR(50),
    winner VARCHAR(10),  -- 'White', 'Black', 'Draw'
    start_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    end_time DATETIME
);

-- Step 4: Table to store move history for each game
CREATE TABLE IF NOT EXISTS MoveHistory (
    move_id INT AUTO_INCREMENT PRIMARY KEY,
    game_id INT,
    move_number INT,
    move_notation VARCHAR(10),    -- e.g., e4, Nf3
    from_position VARCHAR(5),     -- e.g., e2
    to_position VARCHAR(5),       -- e.g., e4
    piece_moved VARCHAR(10),
    piece_captured VARCHAR(10),
    move_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (game_id) REFERENCES Game(game_id) ON DELETE CASCADE
);
