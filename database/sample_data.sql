-- ============================================================
-- Sample Data for Student Academic Performance Management System
-- Run this AFTER running schema.sql
-- ============================================================
USE student_grade_tracker;

-- Clear existing data (in reverse FK order)
DELETE FROM grades;
DELETE FROM subjects;
DELETE FROM students;
DELETE FROM teachers;
DELETE FROM users WHERE username != 'admin';

-- ============================================================
-- TEACHER USERS
-- ============================================================
INSERT INTO users (username, password, role) VALUES
('teacher1', 'teacher123', 'TEACHER'),
('teacher2', 'teacher123', 'TEACHER');

-- ============================================================
-- TEACHERS (linked to user accounts)
-- ============================================================
INSERT INTO teachers (name, email, department, user_id) VALUES
('Dr. Ramesh Kumar',  'ramesh.kumar@college.edu',  'Computer Science', (SELECT id FROM users WHERE username='teacher1')),
('Prof. Anita Sharma','anita.sharma@college.edu',  'Mathematics',      (SELECT id FROM users WHERE username='teacher2'));

-- ============================================================
-- SUBJECTS (assigned to teachers)
-- ============================================================
INSERT INTO subjects (subject_code, subject_name, credits, teacher_id) VALUES
('CS101', 'Introduction to Programming',  4, (SELECT id FROM teachers WHERE email='ramesh.kumar@college.edu')),
('CS102', 'Data Structures & Algorithms', 4, (SELECT id FROM teachers WHERE email='ramesh.kumar@college.edu')),
('CS103', 'Database Management Systems',  3, (SELECT id FROM teachers WHERE email='ramesh.kumar@college.edu')),
('MA101', 'Engineering Mathematics I',    4, (SELECT id FROM teachers WHERE email='anita.sharma@college.edu')),
('MA102', 'Discrete Mathematics',         3, (SELECT id FROM teachers WHERE email='anita.sharma@college.edu'));

-- ============================================================
-- STUDENT USERS
-- ============================================================
INSERT INTO users (username, password, role) VALUES
('student1', 'student123', 'STUDENT'),
('student2', 'student123', 'STUDENT'),
('student3', 'student123', 'STUDENT');

-- ============================================================
-- STUDENTS (linked to user accounts)
-- ============================================================
INSERT INTO students (roll_number, name, email, phone, course, semester, user_id) VALUES
('CS2024001', 'Arjun Patel',    'arjun.patel@student.edu',    '9876543201', 'B.Tech Computer Science', 3, (SELECT id FROM users WHERE username='student1')),
('CS2024002', 'Priya Singh',    'priya.singh@student.edu',    '9876543202', 'B.Tech Computer Science', 3, (SELECT id FROM users WHERE username='student2')),
('CS2024003', 'Rohit Sharma',   'rohit.sharma@student.edu',   '9876543203', 'B.Tech Computer Science', 3, (SELECT id FROM users WHERE username='student3'));

-- ============================================================
-- GRADES
-- ============================================================
-- Arjun Patel grades
INSERT INTO grades (student_id, subject_id, marks, grade) VALUES
((SELECT id FROM students WHERE roll_number='CS2024001'), (SELECT id FROM subjects WHERE subject_code='CS101'), 88, 'A'),
((SELECT id FROM students WHERE roll_number='CS2024001'), (SELECT id FROM subjects WHERE subject_code='CS102'), 76, 'B+'),
((SELECT id FROM students WHERE roll_number='CS2024001'), (SELECT id FROM subjects WHERE subject_code='CS103'), 92, 'A+'),
((SELECT id FROM students WHERE roll_number='CS2024001'), (SELECT id FROM subjects WHERE subject_code='MA101'), 65, 'B'),
((SELECT id FROM students WHERE roll_number='CS2024001'), (SELECT id FROM subjects WHERE subject_code='MA102'), 72, 'B+');

-- Priya Singh grades
INSERT INTO grades (student_id, subject_id, marks, grade) VALUES
((SELECT id FROM students WHERE roll_number='CS2024002'), (SELECT id FROM subjects WHERE subject_code='CS101'), 95, 'A+'),
((SELECT id FROM students WHERE roll_number='CS2024002'), (SELECT id FROM subjects WHERE subject_code='CS102'), 89, 'A'),
((SELECT id FROM students WHERE roll_number='CS2024002'), (SELECT id FROM subjects WHERE subject_code='CS103'), 78, 'B+'),
((SELECT id FROM students WHERE roll_number='CS2024002'), (SELECT id FROM subjects WHERE subject_code='MA101'), 82, 'A'),
((SELECT id FROM students WHERE roll_number='CS2024002'), (SELECT id FROM subjects WHERE subject_code='MA102'), 91, 'A+');

-- Rohit Sharma grades
INSERT INTO grades (student_id, subject_id, marks, grade) VALUES
((SELECT id FROM students WHERE roll_number='CS2024003'), (SELECT id FROM subjects WHERE subject_code='CS101'), 55, 'C'),
((SELECT id FROM students WHERE roll_number='CS2024003'), (SELECT id FROM subjects WHERE subject_code='CS102'), 48, 'D'),
((SELECT id FROM students WHERE roll_number='CS2024003'), (SELECT id FROM subjects WHERE subject_code='CS103'), 62, 'B'),
((SELECT id FROM students WHERE roll_number='CS2024003'), (SELECT id FROM subjects WHERE subject_code='MA101'), 38, 'F'),
((SELECT id FROM students WHERE roll_number='CS2024003'), (SELECT id FROM subjects WHERE subject_code='MA102'), 51, 'C');

SELECT 'Sample data inserted successfully!' AS status;
