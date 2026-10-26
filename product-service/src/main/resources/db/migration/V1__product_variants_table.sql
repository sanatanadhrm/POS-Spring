CREATE TABLE products (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255),
    qty INTEGER,
    store_id VARCHAR(255) NOT NULL,
    status VARCHAR(255),
    price DECIMAL(12, 2),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL

);

CREATE TABLE product_variants (
    id VARCHAR(255) PRIMARY KEY,
    product_id VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    additional_price DECIMAL(12, 2),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    
    CONSTRAINT fk_product_variants_product FOREIGN KEY(product_id) REFERENCES products(id) ON DELETE CASCADE
);