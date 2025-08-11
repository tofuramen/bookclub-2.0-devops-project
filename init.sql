-- BookClub Database Schema
-- Generated from JPA entities

-- Create database (this is done by Docker, but keeping for reference)
-- CREATE DATABASE bookclub;

-- Use the database
\c bookclub;

-- Create app_user table
CREATE TABLE app_user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    bio VARCHAR(500),
    books_read_count INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_username_length CHECK (LENGTH(username) >= 3),
    CONSTRAINT chk_password_length CHECK (LENGTH(password) >= 6)
);

-- Create bookspace table
CREATE TABLE bookspace (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_bookspace_user FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    CONSTRAINT chk_title_not_empty CHECK (LENGTH(TRIM(title)) > 0)
);

-- Create post table
CREATE TABLE post (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    author_id BIGINT NOT NULL,
    bookspace_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_post_author FOREIGN KEY (author_id) REFERENCES app_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_post_bookspace FOREIGN KEY (bookspace_id) REFERENCES bookspace(id) ON DELETE CASCADE,
    CONSTRAINT chk_post_title_not_empty CHECK (LENGTH(TRIM(title)) > 0),
    CONSTRAINT chk_post_content_not_empty CHECK (LENGTH(TRIM(content)) > 0)
);

-- Create comment table
CREATE TABLE comment (
    id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    author_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_comment_author FOREIGN KEY (author_id) REFERENCES app_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_post FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE,
    CONSTRAINT chk_comment_content_not_empty CHECK (LENGTH(TRIM(content)) > 0),
    CONSTRAINT chk_comment_length CHECK (LENGTH(content) <= 1000)
);

-- Create friendship table (bidirectional relationships)
CREATE TABLE friendship (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_friendship_user FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_friendship_friend FOREIGN KEY (friend_id) REFERENCES app_user(id) ON DELETE CASCADE,
    CONSTRAINT chk_no_self_friendship CHECK (user_id != friend_id),
    CONSTRAINT uk_friendship_pair UNIQUE (user_id, friend_id)
);

-- Create indexes for better performance
CREATE INDEX idx_bookspace_user_id ON bookspace(user_id);
CREATE INDEX idx_bookspace_created_at ON bookspace(created_at DESC);

CREATE INDEX idx_post_author_id ON post(author_id);
CREATE INDEX idx_post_bookspace_id ON post(bookspace_id);
CREATE INDEX idx_post_created_at ON post(created_at DESC);

CREATE INDEX idx_comment_author_id ON comment(author_id);
CREATE INDEX idx_comment_post_id ON comment(post_id);
CREATE INDEX idx_comment_created_at ON comment(created_at DESC);

CREATE INDEX idx_friendship_user_id ON friendship(user_id);
CREATE INDEX idx_friendship_friend_id ON friendship(friend_id);

CREATE INDEX idx_user_username ON app_user(username);
CREATE INDEX idx_user_email ON app_user(email);

-- Create a function to update updated_at timestamps
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers to automatically update updated_at
CREATE TRIGGER update_bookspace_updated_at BEFORE UPDATE ON bookspace 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_post_updated_at BEFORE UPDATE ON post 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_comment_updated_at BEFORE UPDATE ON comment 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Insert some sample data for testing
INSERT INTO app_user (username, email, password, bio, books_read_count) VALUES
('john_doe', 'john@example.com', '$2a$10$rB8VB6cY1k3aF9XhZxE0t.QcQwGbJBKY3TKlBgF8HkpSqK9vV2lXO', 'Love reading fantasy novels!', 25),
('jane_smith', 'jane@example.com', '$2a$10$rB8VB6cY1k3aF9XhZxE0t.QcQwGbJBKY3TKlBgF8HkpSqK9vV2lXO', 'Sci-fi enthusiast and book blogger.', 18),
('book_lover', 'books@example.com', '$2a$10$rB8VB6cY1k3aF9XhZxE0t.QcQwGbJBKY3TKlBgF8HkpSqK9vV2lXO', 'Reading is my passion!', 42);

-- Note: The password above is bcrypt hash for "password123" - change in production!

COMMIT;
