CREATE TABLE IF NOT EXISTS roles (
    id VARCHAR(26) PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(26) PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255),
    is_verified BOOLEAN NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS credentials (
    id VARCHAR(26) PRIMARY KEY,
    hash VARCHAR(255) NOT NULL,
    user_id VARCHAR(26) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT fk_user_credentials
        FOREIGN KEY(user_id)
        REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS sessions (
    id VARCHAR(26) PRIMARY KEY,
    ip VARCHAR(255),
    refresh_token VARCHAR(255) NOT NULL,
    user_id VARCHAR(26) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expire_at TIMESTAMP NOT NULL,
    revoked_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_user
        FOREIGN KEY(user_id)
        REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id VARCHAR(26) NOT NULL,
    role_id VARCHAR(26) NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles
        FOREIGN KEY(user_id)
        REFERENCES users(id),
    CONSTRAINT fk_role
        FOREIGN KEY(role_id)
        REFERENCES roles(id)
);

CREATE TABLE IF NOT EXISTS verifications (
    id VARCHAR(26) PRIMARY KEY,
    code VARCHAR(255) NOT NULL,
    user_id VARCHAR(26) NOT NULL,
    type VARCHAR(50) NOT NULL,
    expire_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_user_verification
        FOREIGN KEY(user_id)
        REFERENCES users(id)
);

CREATE INDEX idx_roles_name ON roles(name);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_is_verified ON users(is_verified);

CREATE INDEX idx_credentials_user_id ON credentials(user_id);

CREATE INDEX idx_sessions_refresh_token ON sessions(refresh_token);
CREATE INDEX idx_sessions_user_id ON sessions(user_id);
CREATE INDEX idx_sessions_expire_at ON sessions(expire_at);
CREATE INDEX idx_sessions_revoked_at ON sessions(revoked_at);

CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);

CREATE INDEX idx_verifications_user_id ON verifications(user_id);
CREATE INDEX idx_verifications_code ON verifications(code);
CREATE INDEX idx_verifications_expire_at ON verifications(expire_at);

INSERT INTO roles (id, name, description, created_at) VALUES ('01J2MXVHQCARKGCJ0ZQ2YRD1PR', 'ROLE_ADMIN', 'Administrator with full access', NOW());
INSERT INTO roles (id, name, description, created_at) VALUES ('01J2MXW00K7X30XXAK7JB45PGY', 'ROLE_USER', 'Standard user with limited access', NOW());

INSERT INTO users (id, first_name, last_name, is_verified, email, created_at, updated_at) VALUES
    ('01J2N1XP9M1Q4YK2YPMQE78V1E', 'Admin', null, true, '${ADMIN_EMAIL}', NOW(), null);

INSERT INTO credentials (id, hash, user_id, created_at, updated_at) VALUES
    ('02J3O2YP9N2Q5ZL3ZPMQE78V2F', '${ADMIN_PASSWORD}', '01J2N1XP9M1Q4YK2YPMQE78V1E', NOW(), null);

INSERT INTO user_roles (user_id, role_id) VALUES
    ('01J2N1XP9M1Q4YK2YPMQE78V1E', '01J2MXVHQCARKGCJ0ZQ2YRD1PR');