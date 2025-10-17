ALTER TABLE products
ADD COLUMN user_Id BIGINT;

ALTER TABLE products
ALTER COLUMN updated_at DROP NOT NULL;

ALTER TABLE products
ADD CONSTRAINT fk_products_user
FOREIGN KEY (user_Id) REFERENCES users(user_id);

CREATE INDEX idx_products_user_id ON products(user_Id);