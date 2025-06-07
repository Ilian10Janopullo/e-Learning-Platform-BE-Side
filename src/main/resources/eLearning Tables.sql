CREATE USER 'admin'@'localhost' IDENTIFIED BY 'admin';
GRANT ALL PRIVILEGES ON *.* TO 'admin'@'localhost' WITH GRANT OPTION;
FLUSH PRIVILEGES;

create database eLearning;
use eLearning;

CREATE TABLE users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    username VARCHAR(20) UNIQUE NOT NULL,
    user_password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    enrollment_date DATE NOT NULL,
    profile_picture LONGBLOB,
    account_status VARCHAR(50) NOT NULL
);

CREATE TABLE admins (
    admin_id BIGINT PRIMARY KEY NOT NULL,
    FOREIGN KEY (admin_id) REFERENCES users(user_id)
);


CREATE TABLE students (
    student_id BIGINT PRIMARY KEY NOT NULL,
    FOREIGN KEY (student_id) REFERENCES users(user_id)
);

CREATE TABLE lecturers (
    lecturer_id BIGINT PRIMARY KEY NOT NULL,
    lecturer_description TEXT NOT NULL,
    stripe_account_id TEXT,
    FOREIGN KEY (lecturer_id) REFERENCES users(user_id)
);

CREATE TABLE courses (
    course_id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    course_name VARCHAR(255) NOT NULL,
    lecturer_id BIGINT,
    price DOUBLE NOT NULL,
    course_description TEXT,
    date_created DATE NOT NULL,
    course_cover LONGBLOB,
    FOREIGN KEY (lecturer_id) REFERENCES lecturers(lecturer_id)
);

CREATE TABLE student_courses (
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    PRIMARY KEY (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

CREATE TABLE lessons (
    lesson_id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    title VARCHAR(255) NOT NULL,
    lesson_description TEXT NOT NULL,
    lesson_status VARCHAR(50) NOT NULL,
    video VARCHAR(100),
    course_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

CREATE TABLE resources (
    resource_id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    resource_name VARCHAR(255) NOT NULL,
    resource_file LONGBLOB,
    lesson_id BIGINT NOT NULL,
    FOREIGN KEY (lesson_id) REFERENCES lessons(lesson_id)
);

CREATE TABLE reviews (
    review_id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    review TEXT,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

CREATE TABLE progress (
    progress_id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    progress_percentage DOUBLE CHECK (progress_percentage BETWEEN 0 AND 100),
    is_completed BOOLEAN DEFAULT FALSE,
    started_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

CREATE TABLE shopping_cart (
    shopping_cart_id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    student_id BIGINT UNIQUE NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(student_id)
);

CREATE TABLE cart_courses (
    cart_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    PRIMARY KEY (cart_id, course_id),
    FOREIGN KEY (cart_id) REFERENCES shopping_cart(shopping_cart_id),
    FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

CREATE TABLE orders (
    order_id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    student_id BIGINT NOT NULL,
    price_total DOUBLE NOT NULL,
    session_id VARCHAR(100) NOT NULL,
    order_status VARCHAR(10) NOT NULL,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id)
);

CREATE TABLE order_courses (
    order_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    PRIMARY KEY (order_id, course_id),
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

CREATE TABLE news (
    news_id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    title VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    news_status VARCHAR(50) NOT NULL,
    news_category VARCHAR(50) NOT NULL,
    admin_id BIGINT NOT NULL,
    image LONGBLOB,
    source_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_id) REFERENCES admins(admin_id)
);

CREATE TABLE comments (
    comment_id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    parent_id BIGINT,
    lesson_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    date_commented TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (lesson_id) REFERENCES lessons(lesson_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id)
);

CREATE TABLE flags (
    flag_id BIGINT NOT NULL AUTO_INCREMENT NOT NULL,
    flagged_by_id BIGINT NOT NULL,
    reason VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    content_type VARCHAR(255) NOT NULL,
    object_id BIGINT NOT NULL,
    PRIMARY KEY (flag_id)
);

CREATE TABLE coupons (
    coupon_id CHAR(36) NOT NULL,
    discount_percentage DOUBLE NOT NULL,
    course_id BIGINT NOT NULL,
    lecturer_id BIGINT NOT NULL,
    student_id BIGINT,
    expiration_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (coupon_id),
    CONSTRAINT fk_coupon_course FOREIGN KEY (course_id) REFERENCES courses(course_id),
    CONSTRAINT fk_coupon_lecturer FOREIGN KEY (lecturer_id) REFERENCES lecturers(lecturer_id),
    CONSTRAINT fk_coupon_student FOREIGN KEY (student_id) REFERENCES students(student_id)
);

CREATE TABLE certificates (
    certificate_id CHAR(36) NOT NULL,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    issuedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    certificate_pdf LONGBLOB,
    PRIMARY KEY (certificate_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

CREATE TABLE completed_lessons (
    progress_id BIGINT NOT NULL,
    lesson_id BIGINT NOT NULL,
    PRIMARY KEY (progress_id, lesson_id),
    FOREIGN KEY (progress_id) REFERENCES progress(progress_id)
);

SET GLOBAL max_allowed_packet = 268435456;

INSERT INTO users (username, user_password, email, first_name, last_name, enrollment_date, profile_picture, account_status)
VALUES ('admin_user', '$2a$12$2gPuVBzs0nG6YaAXEwvJyeZiAObGHc7lE9ujyo24rF2ll0G74G0ru', 'admin@epoka.edu.al', 'Admin', 'User', '2025-04-05', NULL, 'ENABLED');

INSERT INTO admins (admin_id)
VALUES (LAST_INSERT_ID());

#Admin password is : @Admin2003