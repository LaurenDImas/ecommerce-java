ALTER TABLE orders
ADD COLUMN xendit_invoice_id varchar(255),
ADD COLUMN xendit_payment_method varchar(255),
ADD COLUMN xendit_payment_status varchar(255);