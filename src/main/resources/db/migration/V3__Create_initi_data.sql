INSERT INTO roles (id, name, description, created_at) VALUES
    ('01J2MXVHQCARKGCJ0ZQ2YRD1PR', 'ROLE_ADMIN', 'Administrator with full access', NOW()),
    ('01J2MXW00K7X30XXAK7JB45PGY', 'ROLE_USER', 'Standard user with limited access', NOW());

INSERT INTO users (id, first_name, last_name, is_verified, email, created_at, updated_at) VALUES
    ('01J2N1XP9M1Q4YK2YPMQE78V1E', 'Admin', null, true, '${ADMIN_EMAIL}', NOW(), null);

INSERT INTO credentials (id, hash, user_id, created_at, updated_at) VALUES
    ('02J3O2YP9N2Q5ZL3ZPMQE78V2F', '${ADMIN_PASSWORD}', '01J2N1XP9M1Q4YK2YPMQE78V1E', NOW(), null);

INSERT INTO user_roles (user_id, role_id) VALUES
    ('01J2N1XP9M1Q4YK2YPMQE78V1E', '01J2MXVHQCARKGCJ0ZQ2YRD1PR');
