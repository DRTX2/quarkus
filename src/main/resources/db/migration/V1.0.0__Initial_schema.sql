-- Initial schema creation
CREATE TABLE IF NOT EXISTS myentity (
    id BIGSERIAL PRIMARY KEY,
    field VARCHAR(255)
);

-- Insert some sample data
INSERT INTO myentity (field) VALUES
    ('Sample 1'),
    ('Sample 2'),
    ('Sample 3');

