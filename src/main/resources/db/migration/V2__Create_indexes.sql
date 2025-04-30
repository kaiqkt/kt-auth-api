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
