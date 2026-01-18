CREATE TABLE house_images (
    id UUID PRIMARY KEY,
    house_id UUID NOT NULL REFERENCES houses(id) ON DELETE CASCADE,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(50) NOT NULL,
    file_size BIGINT,
    storage_path VARCHAR(255) NOT NULL,
    thumbnail_path VARCHAR(255),
    caption VARCHAR(255),
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    display_order INTEGER NOT NULL DEFAULT 0,
    uploaded_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_house_images_house_id ON house_images(house_id);
