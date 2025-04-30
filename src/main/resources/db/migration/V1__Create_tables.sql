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
