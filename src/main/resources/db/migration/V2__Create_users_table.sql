-- Tabla de usuarios
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices para mejorar búsquedas
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_enabled ON users(enabled);

-- Tabla de roles (relación many-to-many)
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Índice para búsquedas por rol
CREATE INDEX idx_user_roles_role ON user_roles(role);

-- Insertar usuario admin por defecto (password: admin123)
INSERT INTO users (username, email, password_hash, enabled, created_at, updated_at)
VALUES ('admin', 'admin@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5OQKV9r.cj5Nm', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Asignar roles al admin
INSERT INTO user_roles (user_id, role)
VALUES
    ((SELECT id FROM users WHERE username = 'admin'), 'ADMIN'),
    ((SELECT id FROM users WHERE username = 'admin'), 'USER');

-- Insertar usuario normal por defecto (password: user123)
INSERT INTO users (username, email, password_hash, enabled, created_at, updated_at)
VALUES ('user', 'user@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5OQKV9r.cj5Nm', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Asignar rol USER al usuario normal
INSERT INTO user_roles (user_id, role)
VALUES ((SELECT id FROM users WHERE username = 'user'), 'USER');

