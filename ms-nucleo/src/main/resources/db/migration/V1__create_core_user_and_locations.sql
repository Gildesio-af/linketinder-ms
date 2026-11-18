CREATE TABLE countries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(75) NOT NULL,
    code CHAR(2) UNIQUE NOT NULL
);

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL CHECK (LENGTH(password) >= 6),
    description TEXT NOT NULL,
    country_id UUID NOT NULL,
    CONSTRAINT fk_users_country FOREIGN KEY (country_id) REFERENCES countries(id)
);

CREATE TABLE addresses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID,
    cep VARCHAR(20),
    street VARCHAR(100),
    number VARCHAR(10),
    complement VARCHAR(100),
    neighborhood VARCHAR(100),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    country_id UUID NOT NULL,
    CONSTRAINT fk_addresses_country FOREIGN KEY (country_id) REFERENCES countries(id),
    CONSTRAINT fk_addresses_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE companies (
    user_id UUID PRIMARY KEY,
    cnpj VARCHAR(18) NOT NULL UNIQUE,
    CONSTRAINT fk_companies_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE candidates (
    user_id UUID PRIMARY KEY,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    last_name VARCHAR(100) NOT NULL,
    birth_date DATE NOT NULL,
    CONSTRAINT fk_candidates_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);