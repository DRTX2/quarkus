-- =====================================================
-- V1: Schema inicial del sistema de usuarios y autenticación
-- =====================================================

-- Tabla de usuarios
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT true,
    locked BOOLEAN NOT NULL DEFAULT false,
    failed_login_attempts INT NOT NULL DEFAULT 0,
    last_login_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices para mejorar búsquedas
CREATE INDEX idx_users_uuid ON users(uuid);
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
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);

-- Tabla de refresh tokens
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(512) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT false,
    revoked_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Índices para refresh tokens
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);
CREATE INDEX idx_refresh_tokens_revoked ON refresh_tokens(revoked);

-- Tabla de auditoría de autenticación
CREATE TABLE auth_audit_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    username VARCHAR(50),
    event_type VARCHAR(50) NOT NULL, -- LOGIN_SUCCESS, LOGIN_FAILED, LOGOUT, PASSWORD_CHANGE, etc.
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    details TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Índices para auditoría
CREATE INDEX idx_auth_audit_user_id ON auth_audit_log(user_id);
CREATE INDEX idx_auth_audit_event_type ON auth_audit_log(event_type);
CREATE INDEX idx_auth_audit_created_at ON auth_audit_log(created_at);

-- Insertar usuario admin por defecto (password: Admin@123)
-- Hash generado con BCrypt (12 rounds)
INSERT INTO users (uuid, username, email, password, enabled, locked, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'admin',
    'admin@example.com',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5OQKV9r.cj5Nm',
    true,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Asignar roles al admin
INSERT INTO user_roles (user_id, role)
VALUES
    ((SELECT id FROM users WHERE username = 'admin'), 'ADMIN'),
    ((SELECT id FROM users WHERE username = 'admin'), 'USER');

-- Insertar usuario normal por defecto (password: User@123)
INSERT INTO users (uuid, username, email, password, enabled, locked, created_at, updated_at)
VALUES (
    gen_random_uuid(),
    'user',
    'user@example.com',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5OQKV9r.cj5Nm',
    true,
    false,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Asignar rol USER al usuario normal
INSERT INTO user_roles (user_id, role)
VALUES ((SELECT id FROM users WHERE username = 'user'), 'USER');

