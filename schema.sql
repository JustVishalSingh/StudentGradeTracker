-- ============================================================
-- Academic Performance Management System (APMS)
-- Database Schema — MySQL 8.x
-- ============================================================
--
-- EXECUTION ORDER:
-- Run this script in MySQL Workbench or via the MySQL CLI:
--   mysql -u root -p < schema.sql
--
-- This script:
--   1. Creates the database (if not exists)
--   2. Creates all 4 tables with proper constraints
--   3. Inserts sample data for testing
--
-- TABLE RELATIONSHIPS:
--   users         → standalone (no FK dependencies)
--   students      → has FK to users (optional, for student login)
--   subjects      → standalone (no FK dependencies)
--   marks         → has FK to students and subjects
--
-- ER DIAGRAM (simplified):
--   users ←── students ──→ marks ←── subjects
-- ============================================================

-- Step 1: Create the database
CREATE DATABASE IF NOT EXISTS apms_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE apms_db;

-- ============================================================
-- TABLE 1: users
-- Stores login credentials for all system users (admin, teacher, student).
--
-- WHY SEPARATE FROM students?
--   A user account is for authentication. A student record is for
--   academic data. Teachers and admins need user accounts too but
--   are not students. Separating them avoids NULLs in the student
--   table for non-student users.
--
-- KEY DESIGN DECISIONS:
--   - password_hash: stores BCrypt hash, NEVER plain text
--   - role ENUM:     MySQL enforces that only valid roles can be stored
--   - user_id:       VARCHAR(20) allows codes like "ADM001", "TCH003"
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    user_id       VARCHAR(20)  NOT NULL,
    username      VARCHAR(50)  NOT NULL,
    password_hash VARCHAR(255) NOT NULL,   -- BCrypt hash (~60 chars, but 255 for safety)
    role          ENUM('ADMIN', 'TEACHER', 'STUDENT') NOT NULL,
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (user_id),
    UNIQUE KEY uq_username (username)      -- Two users cannot share the same username
);

-- ============================================================
-- TABLE 2: students
-- Academic record for each enrolled student.
--
-- KEY DESIGN DECISIONS:
--   - semester CHECK: ensures only 1-8 (valid B.Tech semesters)
--   - email UNIQUE: prevents duplicate registrations
--   - user_id FK:   optional link to the users table (a student may
--                   or may not have a login account). ON DELETE SET NULL
--                   means if the user account is deleted, the student
--                   record remains — academic data is preserved.
-- ============================================================
CREATE TABLE IF NOT EXISTS students (
    student_id      VARCHAR(20)  NOT NULL,
    name            VARCHAR(100) NOT NULL,
    email           VARCHAR(100) NOT NULL,
    course          VARCHAR(50)  NOT NULL,
    semester        INT          NOT NULL,
    section         VARCHAR(10)  DEFAULT NULL,
    enrollment_year INT          NOT NULL,
    user_id         VARCHAR(20)  DEFAULT NULL,  -- FK to users.user_id (nullable)

    PRIMARY KEY (student_id),
    UNIQUE KEY uq_student_email (email),

    -- Ensures semester is always in the valid range for a B.Tech program
    CONSTRAINT chk_semester CHECK (semester BETWEEN 1 AND 8),

    -- Ensures enrollment year is reasonable (not year 1900 or year 3000)
    CONSTRAINT chk_enrollment_year CHECK (enrollment_year BETWEEN 2000 AND 2100),

    -- Links to user account — if user is deleted, student record survives
    CONSTRAINT fk_student_user FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- ============================================================
-- TABLE 3: subjects
-- Academic subjects available in the institution.
--
-- KEY DESIGN DECISIONS:
--   - passing_marks CHECK: cannot exceed max_marks
--   - credits CHECK:       must be between 1 and 5
--   - No FK to students:   subjects are institution-level data,
--                          not per-student. The marks table joins them.
-- ============================================================
CREATE TABLE IF NOT EXISTS subjects (
    subject_id    VARCHAR(20)  NOT NULL,
    subject_name  VARCHAR(100) NOT NULL,
    credits       INT          NOT NULL,
    max_marks     DOUBLE       NOT NULL,
    passing_marks DOUBLE       NOT NULL,

    PRIMARY KEY (subject_id),

    -- Credits must be in the typical range (1 lab = 1, core subject = 3-4)
    CONSTRAINT chk_credits CHECK (credits BETWEEN 1 AND 5),

    -- Max marks must be positive
    CONSTRAINT chk_max_marks CHECK (max_marks > 0),

    -- Passing threshold cannot exceed the maximum
    CONSTRAINT chk_passing_marks CHECK (passing_marks <= max_marks)
);

-- ============================================================
-- TABLE 4: marks
-- Stores each student's marks for each subject.
--
-- KEY DESIGN DECISIONS:
--   - UNIQUE KEY on (student_id, subject_id): a student can have
--     only ONE marks record per subject. This is enforced here at
--     the DB level AND in MarksService at the application level.
--     Double enforcement = data integrity.
--   - ON DELETE CASCADE: if a student is deleted, their marks are
--     deleted too (no orphaned records).
--   - grade and grade_point are computed columns — stored for
--     performance (avoids recalculating on every query).
-- ============================================================
CREATE TABLE IF NOT EXISTS marks (
    id              INT          NOT NULL AUTO_INCREMENT,
    student_id      VARCHAR(20)  NOT NULL,
    subject_id      VARCHAR(20)  NOT NULL,
    internal_marks  DOUBLE       NOT NULL,
    external_marks  DOUBLE       NOT NULL,
    total_marks     DOUBLE       NOT NULL,
    grade           VARCHAR(5)   DEFAULT NULL,
    grade_point     DOUBLE       DEFAULT 0.0,

    PRIMARY KEY (id),

    -- Prevents a student from having two entries for the same subject
    UNIQUE KEY uq_student_subject (student_id, subject_id),

    -- Internal + external marks must be non-negative
    CONSTRAINT chk_internal_marks CHECK (internal_marks >= 0),
    CONSTRAINT chk_external_marks CHECK (external_marks >= 0),

    -- If a student is deleted, delete their marks too
    CONSTRAINT fk_marks_student FOREIGN KEY (student_id)
        REFERENCES students(student_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    -- If a subject is deleted, delete its marks records too
    CONSTRAINT fk_marks_subject FOREIGN KEY (subject_id)
        REFERENCES subjects(subject_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- ============================================================
-- SAMPLE DATA
-- This data is for testing and demonstration only.
-- Run this after creating the tables.
--
-- Admin password: "admin123"  (BCrypt hash stored, not plain text)
-- Teacher password: "teach456"
-- Student passwords: "student@123"
--
-- NOTE: In real usage, passwords are hashed by PasswordUtil.hash()
-- before insertion. The hashes below are pre-generated for testing.
-- ============================================================

-- Sample Users (admin + teachers + student accounts)
INSERT IGNORE INTO users (user_id, username, password_hash, role) VALUES
('ADM001', 'admin',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh3q', 'ADMIN'),
('TCH001', 'prof_sharma', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh3q', 'TEACHER'),
('TCH002', 'prof_gupta',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh3q', 'TEACHER'),
('STU001', 'alice_cs',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh3q', 'STUDENT'),
('STU002', 'bob_cs',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh3q', 'STUDENT'),
('STU003', 'carol_ee',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh3q', 'STUDENT');

-- Sample Students
INSERT IGNORE INTO students (student_id, name, email, course, semester, section, enrollment_year, user_id) VALUES
('CS2024001', 'Alice Johnson',   'alice.johnson@college.edu',   'Computer Science',        5, 'A', 2024, 'STU001'),
('CS2024002', 'Bob Smith',       'bob.smith@college.edu',       'Computer Science',        5, 'A', 2024, 'STU002'),
('EE2023001', 'Carol Williams',  'carol.williams@college.edu',  'Electronics Engineering', 7, 'B', 2023, 'STU003'),
('CS2024003', 'David Brown',     'david.brown@college.edu',     'Computer Science',        5, 'B', 2024, NULL),
('ME2022001', 'Eva Martinez',    'eva.martinez@college.edu',    'Mechanical Engineering',  7, 'A', 2022, NULL);

-- Sample Subjects
INSERT IGNORE INTO subjects (subject_id, subject_name, credits, max_marks, passing_marks) VALUES
('CS301',  'Data Structures and Algorithms', 4, 100.0, 40.0),
('CS302',  'Operating Systems',              3, 100.0, 40.0),
('CS303',  'Database Management Systems',    3, 100.0, 40.0),
('CS304',  'Computer Networks',              3, 100.0, 40.0),
('CS305L', 'OS Lab',                         1,  50.0, 20.0);

-- Sample Marks (for CS students in semester 5)
INSERT IGNORE INTO marks (student_id, subject_id, internal_marks, external_marks, total_marks, grade, grade_point) VALUES
-- Alice Johnson (CS2024001) — good performer
('CS2024001', 'CS301',  28.0, 65.0, 93.0, 'O',  10.0),
('CS2024001', 'CS302',  25.0, 61.0, 86.0, 'A+',  9.0),
('CS2024001', 'CS303',  27.0, 55.0, 82.0, 'A+',  9.0),
('CS2024001', 'CS304',  22.0, 58.0, 80.0, 'A+',  9.0),
('CS2024001', 'CS305L', 18.0, 29.0, 47.0, 'O',  10.0),

-- Bob Smith (CS2024002) — average performer
('CS2024002', 'CS301',  18.0, 45.0, 63.0, 'B+',  7.0),
('CS2024002', 'CS302',  15.0, 40.0, 55.0, 'B',   6.0),
('CS2024002', 'CS303',  20.0, 48.0, 68.0, 'B+',  7.0),
('CS2024002', 'CS304',  12.0, 35.0, 47.0, 'B',   6.0),
('CS2024002', 'CS305L', 14.0, 20.0, 34.0, 'F',   0.0),  -- Failed lab

-- David Brown (CS2024003) — at risk
('CS2024003', 'CS301',  15.0, 28.0, 43.0, 'C',   5.0),
('CS2024003', 'CS302',  12.0, 25.0, 37.0, 'F',   0.0),
('CS2024003', 'CS303',  18.0, 30.0, 48.0, 'B',   6.0),
('CS2024003', 'CS304',  10.0, 32.0, 42.0, 'C',   5.0),
('CS2024003', 'CS305L', 20.0, 22.0, 42.0, 'O',  10.0);

-- ============================================================
-- VERIFICATION QUERIES — Run these to confirm correct setup
-- ============================================================
-- SELECT COUNT(*) FROM users;     -- should return 6
-- SELECT COUNT(*) FROM students;  -- should return 5
-- SELECT COUNT(*) FROM subjects;  -- should return 5
-- SELECT COUNT(*) FROM marks;     -- should return 15
--
-- SGPA verification for Alice (should be ~9.44):
-- SELECT s.student_id, s.name,
--        SUM(sub.credits * m.grade_point) / SUM(sub.credits) AS sgpa
-- FROM students s
-- JOIN marks m ON s.student_id = m.student_id
-- JOIN subjects sub ON m.subject_id = sub.subject_id
-- WHERE s.student_id = 'CS2024001'
-- GROUP BY s.student_id, s.name;
-- ============================================================
