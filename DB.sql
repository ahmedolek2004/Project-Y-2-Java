-- SQL schema for student_management_system

CREATE DATABASE IF NOT EXISTS student_management_system;

USE student_management_system;

DROP TABLE IF EXISTS enrollments;

DROP TABLE IF EXISTS subjects;

DROP TABLE IF EXISTS students;

DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

CREATE TABLE students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(255) NOT NULL
);

CREATE TABLE subjects (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(100) NOT NULL,
    credits INT NOT NULL
);

CREATE TABLE enrollments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    subject_id INT NOT NULL,
    grade DOUBLE,
    FOREIGN KEY (student_id) REFERENCES students (id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects (id) ON DELETE CASCADE,
    UNIQUE (student_id, subject_id)
);

-- sample data
INSERT IGNORE INTO
    users (username, password, role)
VALUES ('admin', 'admin', 'ADMIN');

INSERT IGNORE INTO
    users (username, password, role)
VALUES ('user', 'user', 'USER');

INSERT IGNORE INTO
    students (name, email, phone)
VALUES (
        'Alice',
        'alice@example.com',
        '555-0100'
    );

INSERT IGNORE INTO
    students (name, email, phone)
VALUES (
        'Bob',
        'bob@example.com',
        '555-0101'
    );

INSERT IGNORE INTO
    subjects (name, code, credits)
VALUES ('Math', 'MATH101', 3);

INSERT IGNORE INTO
    subjects (name, code, credits)
VALUES ('History', 'HIST205', 2);

INSERT IGNORE INTO
    enrollments (student_id, subject_id, grade)
VALUES (1, 1, 95.0);

INSERT IGNORE INTO
    enrollments (student_id, subject_id, grade)
VALUES (1, 2, 88.0);

INSERT IGNORE INTO
    enrollments (student_id, subject_id, grade)
VALUES (2, 1, 85.0);

SELECT * FROM users;
SELECT * FROM students;
SELECT * FROM subjects;
SELECT * FROM enrollments;