-- Drop objects if they exist. Errors here are normal if objects don't exist yet.
DROP TABLE LoanPayment CASCADE CONSTRAINTS;
DROP TABLE CardTransaction CASCADE CONSTRAINTS;
DROP TABLE AccountTransaction CASCADE CONSTRAINTS;
DROP TABLE CCPayment CASCADE CONSTRAINTS;
DROP TABLE Unsecured CASCADE CONSTRAINTS;
DROP TABLE Mortgages CASCADE CONSTRAINTS;
DROP TABLE Debit CASCADE CONSTRAINTS;
DROP TABLE Credit CASCADE CONSTRAINTS;
DROP TABLE Cards CASCADE CONSTRAINTS;
DROP TABLE Investment CASCADE CONSTRAINTS;
DROP TABLE Savings CASCADE CONSTRAINTS;
DROP TABLE Checking CASCADE CONSTRAINTS;
DROP TABLE CustomerAccount CASCADE CONSTRAINTS;
DROP TABLE Loans CASCADE CONSTRAINTS;
DROP TABLE Transaction CASCADE CONSTRAINTS;
DROP TABLE Account CASCADE CONSTRAINTS;
DROP TABLE Branch CASCADE CONSTRAINTS;
DROP TABLE Customer CASCADE CONSTRAINTS;

DROP SEQUENCE customer_seq;
DROP SEQUENCE account_seq;
DROP SEQUENCE transaction_seq;
DROP SEQUENCE loan_seq;

-- 1. Customer
CREATE TABLE Customer (
    customerID INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    dob DATE NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address VARCHAR(255) NOT NULL,
    join_date DATE DEFAULT SYSDATE NOT NULL
);

-- 11. Branch
CREATE TABLE Branch (
    branchID INT PRIMARY KEY,
    location VARCHAR(255) NOT NULL,
    atm_id VARCHAR(50) NOT NULL
);

-- 2. Account (status with default and check as table-level)
CREATE TABLE Account (
    accountID INT PRIMARY KEY,
    balance DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE' NOT NULL,
    branchID INT NOT NULL,
    FOREIGN KEY (branchID) REFERENCES Branch(branchID) ON DELETE CASCADE
    
);

ALTER TABLE Account
ADD CONSTRAINT chk_account_status
CHECK (status IN ('ACTIVE','CLOSED','FROZEN'));

-- 3. CustomerAccount
-- This references Customer and Account, which now exist
CREATE TABLE CustomerAccount (
    customerID INT,
    accountID INT,
    PRIMARY KEY (customerID, accountID),
    FOREIGN KEY (customerID) REFERENCES Customer(customerID) ON DELETE CASCADE,
    FOREIGN KEY (accountID) REFERENCES Account(accountID) ON DELETE CASCADE
);

-- 4. Checking
CREATE TABLE Checking (
    accountID INT PRIMARY KEY,
    FOREIGN KEY (accountID) REFERENCES Account(accountID) ON DELETE CASCADE
);

-- 5. Savings
CREATE TABLE Savings (
    accountID INT PRIMARY KEY,
    min_balance DECIMAL(10,2) NOT NULL,
    penalty_rate DECIMAL(5,4) NOT NULL,
    FOREIGN KEY (accountID) REFERENCES Account(accountID) ON DELETE CASCADE
);

-- 6. Investment
CREATE TABLE Investment (
    accountID INT PRIMARY KEY,
    asset_list VARCHAR(1000) NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    FOREIGN KEY (accountID) REFERENCES Account(accountID) ON DELETE CASCADE
);

ALTER TABLE Investment
ADD CONSTRAINT chk_investment_risk
CHECK (risk_level IN ('LOW','MEDIUM','HIGH'));

-- 7. Loans (status check table-level)
CREATE TABLE Loans (
    loanID INT PRIMARY KEY,
    customerID INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    interest_rate DECIMAL(5,4) NOT NULL,
    start_date DATE DEFAULT SYSDATE NOT NULL,
    term_months INT NOT NULL,
    monthly_payment DECIMAL(10,2) NOT NULL,
    remaining_balance DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING' NOT NULL,
    branchID INT NOT NULL,
    FOREIGN KEY (customerID) REFERENCES Customer(customerID) ON DELETE CASCADE,
    FOREIGN KEY (branchID) REFERENCES Branch(branchID) ON DELETE CASCADE
);

ALTER TABLE Loans
ADD CONSTRAINT chk_loans_status
CHECK (status IN ('PENDING','ACTIVE','PAID','DEFAULT'));

-- 8. Mortgages
CREATE TABLE Mortgages (
    loanID INT PRIMARY KEY,
    property_address VARCHAR(255) NOT NULL,
    property_value DECIMAL(12,2) NOT NULL,
    ltv_ratio DECIMAL(5,2) NOT NULL,
    FOREIGN KEY (loanID) REFERENCES Loans(loanID) ON DELETE CASCADE
);

-- 9. Unsecured
CREATE TABLE Unsecured (
    loanID INT PRIMARY KEY,
    credit_score INT NOT NULL,
    FOREIGN KEY (loanID) REFERENCES Loans(loanID) ON DELETE CASCADE
);

-- 10. Transaction (type and status checks table-level)
CREATE TABLE Transaction (
    transactionID INT PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    description VARCHAR(255),
    transaction_date TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING' NOT NULL,
    from_account INT,
    to_account INT,
    FOREIGN KEY (from_account) REFERENCES Account(accountID) ON DELETE SET NULL,
    FOREIGN KEY (to_account) REFERENCES Account(accountID) ON DELETE SET NULL
);

ALTER TABLE Transaction
ADD CONSTRAINT chk_transaction_type
CHECK (type IN ('DEPOSIT','WITHDRAWAL','TRANSFER','PAYMENT','PURCHASE','FEE','INTEREST'));

ALTER TABLE Transaction
ADD CONSTRAINT chk_transaction_status
CHECK (status IN ('PENDING','COMPLETED','FAILED','REVERSED'));

-- 12. Cards (status check table-level)
CREATE TABLE Cards (
    card_number VARCHAR(16) PRIMARY KEY,
    customerID INT NOT NULL,
    issue_date DATE DEFAULT SYSDATE NOT NULL,
    expiry_date DATE NOT NULL,
    cvv VARCHAR(3) NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE' NOT NULL,
    FOREIGN KEY (customerID) REFERENCES Customer(customerID) ON DELETE CASCADE
);

ALTER TABLE Cards
ADD CONSTRAINT chk_cards_status
CHECK (status IN ('ACTIVE','BLOCKED','EXPIRED'));

-- 13. Debit
CREATE TABLE Debit (
    card_number VARCHAR(16) PRIMARY KEY,
    checking_account INT NOT NULL,
    FOREIGN KEY (card_number) REFERENCES Cards(card_number) ON DELETE CASCADE,
    FOREIGN KEY (checking_account) REFERENCES Checking(accountID) ON DELETE CASCADE
);

-- 14. Credit
CREATE TABLE Credit (
    card_number VARCHAR(16) PRIMARY KEY,
    credit_limit DECIMAL(10,2) NOT NULL,
    current_balance DECIMAL(10,2) DEFAULT 0 NOT NULL,
    due_balance DECIMAL(10,2) DEFAULT 0 NOT NULL,
    payment_due_date DATE,
    interest_rate DECIMAL(5,4) NOT NULL,
    FOREIGN KEY (card_number) REFERENCES Cards(card_number) ON DELETE CASCADE
);

-- 15. CCPayment (paymentMethod check)
CREATE TABLE CCPayment (
    transactionID INT PRIMARY KEY,
    paymentMethod VARCHAR(50) NOT NULL,
    FOREIGN KEY (transactionID) REFERENCES Transaction(transactionID) ON DELETE CASCADE
);

ALTER TABLE CCPayment
ADD CONSTRAINT chk_ccpayment_method
CHECK (paymentMethod IN ('VISA','MASTERCARD','AMEX','DISCOVER'));

-- 16. AccountTransaction (type check)
CREATE TABLE AccountTransaction (
    transactionID INT PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    FOREIGN KEY (transactionID) REFERENCES Transaction(transactionID) ON DELETE CASCADE
);

ALTER TABLE AccountTransaction
ADD CONSTRAINT chk_accounttxn_type
CHECK (type IN ('DEPOSIT','WITHDRAWAL','TRANSFER','PAYMENT','PURCHASE','FEE','INTEREST'));

-- 17. CardTransaction
CREATE TABLE CardTransaction (
    transactionID INT PRIMARY KEY,
    cardNumber VARCHAR(16) NOT NULL,
    cardExpiry DATE NOT NULL,
    FOREIGN KEY (transactionID) REFERENCES Transaction(transactionID) ON DELETE CASCADE
);

-- 18. LoanPayment
CREATE TABLE LoanPayment (
    transactionID INT PRIMARY KEY,
    loanID INT NOT NULL,
    paymentAmount DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (transactionID) REFERENCES Transaction(transactionID) ON DELETE CASCADE
);

-- Create sequences
CREATE SEQUENCE customer_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE account_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE transaction_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE loan_seq START WITH 1 INCREMENT BY 1;

-- Triggers for automatic IDs
CREATE OR REPLACE TRIGGER customer_id_trigger
BEFORE INSERT ON Customer
FOR EACH ROW
BEGIN
    SELECT customer_seq.NEXTVAL INTO :NEW.customerID FROM dual;
END;
/

CREATE OR REPLACE TRIGGER account_id_trigger
BEFORE INSERT ON Account
FOR EACH ROW
BEGIN
    SELECT account_seq.NEXTVAL INTO :NEW.accountID FROM dual;
END;
/

CREATE OR REPLACE TRIGGER transaction_id_trigger
BEFORE INSERT ON Transaction
FOR EACH ROW
BEGIN
    SELECT transaction_seq.NEXTVAL INTO :NEW.transactionID FROM dual;
END;
/

CREATE OR REPLACE TRIGGER loan_id_trigger
BEFORE INSERT ON Loans
FOR EACH ROW
BEGIN
    SELECT loan_seq.NEXTVAL INTO :NEW.loanID FROM dual;
END;
/

-- Trigger for savings penalty
CREATE OR REPLACE TRIGGER check_savings_balance
BEFORE UPDATE OF balance ON Account
FOR EACH ROW
DECLARE
    v_min_balance Savings.min_balance%TYPE;
    v_penalty_rate Savings.penalty_rate%TYPE;
    v_penalty_amount NUMBER;
BEGIN
    BEGIN
        SELECT min_balance, penalty_rate
        INTO v_min_balance, v_penalty_rate
        FROM Savings
        WHERE accountID = :NEW.accountID;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN; -- Not a savings account, do nothing
    END;

    IF :NEW.balance < v_min_balance THEN
        v_penalty_amount := :NEW.balance * v_penalty_rate;
        :NEW.balance := :NEW.balance - v_penalty_amount;

        INSERT INTO Transaction (type, amount, description, status, from_account)
        VALUES ('FEE', v_penalty_amount, 'Minimum balance penalty', 'COMPLETED', :NEW.accountID);
    END IF;
END;
/

-- Trigger for credit limit check
CREATE OR REPLACE TRIGGER check_credit_limit
BEFORE UPDATE OF current_balance ON Credit
FOR EACH ROW
BEGIN
    IF :NEW.current_balance > :NEW.credit_limit THEN
        RAISE_APPLICATION_ERROR(-20001, 'Transaction would exceed credit limit');
    END IF;
END;
/

COMMIT;
