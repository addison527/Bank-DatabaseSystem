-- First verify if data exists
SELECT * FROM Customer;

-- If no data exists, insert test data
INSERT INTO Customer (customerID, name, dob, phone, address, join_date)
SELECT 1, 'John Doe', TO_DATE('1990-05-15', 'YYYY-MM-DD'), '555-1234', '1234 Elm St, Springfield, IL', TO_DATE('2024-12-05', 'YYYY-MM-DD')
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM Customer WHERE customerID = 1);

-- Verify the insert
SELECT * FROM Customer;

-- Make sure to commit
COMMIT;