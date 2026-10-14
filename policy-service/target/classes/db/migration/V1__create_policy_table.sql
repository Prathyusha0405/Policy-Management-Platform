CREATE TABLE policies (
    id UUID PRIMARY KEY,

    policy_number VARCHAR(20) NOT NULL UNIQUE,

    policyholder_name VARCHAR(255) NOT NULL,

    line_of_business VARCHAR(30) NOT NULL,

    status VARCHAR(30) NOT NULL,

    premium_amount DECIMAL(19,2) NOT NULL,

    currency VARCHAR(3) NOT NULL,

    effective_date DATE NOT NULL,

    expiry_date DATE NOT NULL,

    region VARCHAR(50) NOT NULL,

    underwriter VARCHAR(255) NOT NULL,

    flagged_for_review BOOLEAN NOT NULL DEFAULT FALSE,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_policy_status
    ON policies(status);

CREATE INDEX idx_policy_lob
    ON policies(line_of_business);

CREATE INDEX idx_policy_region
    ON policies(region);

CREATE INDEX idx_policy_effective_date
    ON policies(effective_date);

CREATE INDEX idx_policy_expiry_date
    ON policies(expiry_date);