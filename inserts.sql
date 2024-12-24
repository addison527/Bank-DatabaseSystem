INSERT INTO Customer (name,dob,phone,address) VALUES ('Suki Spadotto', TO_DATE('1975-08-18','YYYY-MM-DD'), '407-692-9638', '"48 Tennyson Court');
INSERT INTO Customer (name,dob,phone,address) VALUES ('Fergus Marrion', TO_DATE('1939-03-17','YYYY-MM-DD'), '909-501-3430', '"159 Fairfield Avenue');
INSERT INTO Customer (name,dob,phone,address) VALUES ('Nikaniki Dmitrichenko', TO_DATE('1966-04-27','YYYY-MM-DD'), '317-251-6259', '"18 Blackbird Place');
INSERT INTO Customer (name,dob,phone,address) VALUES ('Melvyn Beadham', TO_DATE('1998-12-13','YYYY-MM-DD'), '318-204-1263', '"715 East Lane');
INSERT INTO Customer (name,dob,phone,address) VALUES ('Caroline Ondricek', TO_DATE('1999-09-07','YYYY-MM-DD'), '864-578-7479', '"5146 Farwell Parkway');
INSERT INTO Customer (name,dob,phone,address) VALUES ('Giffie Batters', TO_DATE('1976-02-23','YYYY-MM-DD'), '540-325-2241', '"617 Lerdahl Place');
INSERT INTO Customer (name,dob,phone,address) VALUES ('Sheree Pancast', TO_DATE('1985-01-04','YYYY-MM-DD'), '919-155-5714', '"680 Bowman Plaza');
INSERT INTO Customer (name,dob,phone,address) VALUES ('Granger Tomisch', TO_DATE('1953-09-14','YYYY-MM-DD'), '763-960-3620', '"6 Westridge Place');
INSERT INTO Customer (name,dob,phone,address) VALUES ('Wanids Adamolli', TO_DATE('1941-03-02','YYYY-MM-DD'), '309-301-7933', '"1446 Roth Avenue');
INSERT INTO Customer (name,dob,phone,address) VALUES ('Donnie DeSousa', TO_DATE('1971-10-02','YYYY-MM-DD'), '480-137-5566', '"4 Declaration Court');

-- Insert branches
INSERT INTO Branch (branchID, location, atm_id) VALUES (1, 'Downtown Branch', 'ATM123');
INSERT INTO Branch (branchID, location, atm_id) VALUES (2, 'Uptown Branch', 'ATM456');

-- Insert accounts
INSERT INTO Account (balance, status, branchID) VALUES (1000.00, 'ACTIVE', 1);
INSERT INTO Account (balance, status, branchID) VALUES (2500.50, 'ACTIVE', 2);
INSERT INTO Account (balance, status, branchID) VALUES (150.00, 'ACTIVE', 1);
INSERT INTO Account (balance, status, branchID) VALUES (780.00, 'FROZEN', 2);
INSERT INTO Account (balance, status, branchID) VALUES (0.00, 'CLOSED', 2);
INSERT INTO Account (balance, status, branchID) VALUES (8900.00, 'ACTIVE', 1);
INSERT INTO Account (balance, status, branchID) VALUES (1200.00, 'ACTIVE', 2);
INSERT INTO Account (balance, status, branchID) VALUES (560.00, 'ACTIVE', 1);
INSERT INTO Account (balance, status, branchID) VALUES (3100.00, 'ACTIVE', 2);
INSERT INTO Account (balance, status, branchID) VALUES (6700.00, 'ACTIVE', 1);
INSERT INTO Account (balance, status, branchID) VALUES (46000.00, 'ACTIVE', 1);
INSERT INTO Account (balance, status, branchID) VALUES (12000.00, 'ACTIVE', 2);
INSERT INTO Account (balance, status, branchID) VALUES (0.00, 'CLOSED', 2);
INSERT INTO Account (balance, status, branchID) VALUES (5600.00, 'ACTIVE', 2);
INSERT INTO Account (balance, status, branchID) VALUES (31050.00, 'ACTIVE', 1);
INSERT INTO Account (balance, status, branchID) VALUES (67560.00, 'ACTIVE', 1);
INSERT INTO Account (balance, status, branchID) VALUES (4065.00, 'ACTIVE', 1);

-- Link customers to accounts
INSERT INTO CustomerAccount (customerID, accountID) VALUES (1, 1);
INSERT INTO CustomerAccount (customerID, accountID) VALUES (2, 2);
INSERT INTO CustomerAccount (customerID, accountID) VALUES (3, 3);
INSERT INTO CustomerAccount (customerID, accountID) VALUES (4, 4);
INSERT INTO CustomerAccount (customerID, accountID) VALUES (5, 5);
INSERT INTO CustomerAccount (customerID, accountID) VALUES (6, 6);
INSERT INTO CustomerAccount (customerID, accountID) VALUES (7, 7);
INSERT INTO CustomerAccount (customerID, accountID) VALUES (8, 8);
INSERT INTO CustomerAccount (customerID, accountID) VALUES (9, 9);
INSERT INTO CustomerAccount (customerID, accountID) VALUES (10, 10);
INSERT INTO CustomerAccount (customerID, accountID) VALUES (1, 11);
INSERT INTO CustomerAccount (customerID, accountID) VALUES (2, 12);
INSERT INTO CustomerAccount (customerID, accountID) VALUES (4, 13);
INSERT INTO CustomerAccount (customerID, accountID) VALUES (6, 14);
INSERT INTO CustomerAccount (customerID, accountID) VALUES (8, 15);
INSERT INTO CustomerAccount (customerID, accountID) VALUES (10, 16);
INSERT INTO CustomerAccount (customerID, accountID) VALUES (1, 17);

-- Insert savings accounts
INSERT INTO Savings (accountID, min_balance, penalty_rate) VALUES (11, 500.00, 0.02);
INSERT INTO Savings (accountID, min_balance, penalty_rate) VALUES (12, 1000.00, 0.03);
INSERT INTO Savings (accountID, min_balance, penalty_rate) VALUES (13, 200.00, 0.015);

-- Insert checking accounts
INSERT INTO Checking (accountID) VALUES (1);
INSERT INTO Checking (accountID) VALUES (2);
INSERT INTO Checking (accountID) VALUES (3);
INSERT INTO Checking (accountID) VALUES (4);
INSERT INTO Checking (accountID) VALUES (5);
INSERT INTO Checking (accountID) VALUES (6);
INSERT INTO Checking (accountID) VALUES (7);
INSERT INTO Checking (accountID) VALUES (8);
INSERT INTO Checking (accountID) VALUES (9);
INSERT INTO Checking (accountID) VALUES (10);

-- Insert investment accounts
INSERT INTO Investment (accountID, asset_list, risk_level) VALUES (14, 'Stocks, Bonds', 'MEDIUM');
INSERT INTO Investment (accountID, asset_list, risk_level) VALUES (15, 'Real Estate, ETFs', 'LOW');
INSERT INTO Investment (accountID, asset_list, risk_level) VALUES (16, 'Cryptocurrency', 'HIGH');
INSERT INTO Investment (accountID, asset_list, risk_level) VALUES (17, 'Monkey NFT', 'MEDIUM');

-- Insert loans
INSERT INTO Loans (customerID, amount, interest_rate, term_months, monthly_payment, remaining_balance, status, branchID)
VALUES (1, 50000.00, 0.035, 60, 920.93, 50000.00, 'ACTIVE', 1);
INSERT INTO Loans (customerID, amount, interest_rate, term_months, monthly_payment, remaining_balance, status, branchID)
VALUES (2, 200000.00, 0.04, 360, 954.83, 195000.00, 'ACTIVE', 2);
INSERT INTO Loans (customerID, amount, interest_rate, term_months, monthly_payment, remaining_balance, status, branchID)
VALUES (3, 15000.00, 0.05, 36, 432.86, 8000.00, 'PAID', 1);

-- Insert unsecured loans
INSERT INTO Unsecured (loanID, credit_score) VALUES (1, 750);
INSERT INTO Unsecured (loanID, credit_score) VALUES (3, 680);

-- Insert mortgages
INSERT INTO Mortgages (loanID, property_address, property_value, ltv_ratio) 
VALUES (2, '123 Elm Street', 250000.00, 0.8);

-- Insert cards
INSERT INTO Cards (card_number, customerID, issue_date, expiry_date, cvv, status) 
VALUES ('8758582304046808', 1, SYSDATE, ADD_MONTHS(SYSDATE, 48), '612', 'ACTIVE');
INSERT INTO Cards (card_number, customerID, issue_date, expiry_date, cvv, status) 
VALUES ('8751377543332884', 2, SYSDATE, ADD_MONTHS(SYSDATE, 48), '363', 'ACTIVE');
INSERT INTO Cards (card_number, customerID, issue_date, expiry_date, cvv, status) 
VALUES ('8754303970563211', 3, SYSDATE, ADD_MONTHS(SYSDATE, 48), '539', 'ACTIVE');
INSERT INTO Cards (card_number, customerID, issue_date, expiry_date, cvv, status) 
VALUES ('8764288933699454', 4, SYSDATE, ADD_MONTHS(SYSDATE, 48), '803', 'ACTIVE');
INSERT INTO Cards (card_number, customerID, issue_date, expiry_date, cvv, status) 
VALUES ('8796795893543805', 5, SYSDATE, ADD_MONTHS(SYSDATE, 48), '771', 'ACTIVE');
INSERT INTO Cards (card_number, customerID, issue_date, expiry_date, cvv, status) 
VALUES ('8742375999854302', 6, SYSDATE, ADD_MONTHS(SYSDATE, 48), '659', 'ACTIVE');
INSERT INTO Cards (card_number, customerID, issue_date, expiry_date, cvv, status) 
VALUES ('8702246358014335', 7, SYSDATE, ADD_MONTHS(SYSDATE, 48), '234', 'ACTIVE');
INSERT INTO Cards (card_number, customerID, issue_date, expiry_date, cvv, status) 
VALUES ('8730060947660930', 8, SYSDATE, ADD_MONTHS(SYSDATE, 48), '975', 'ACTIVE');
INSERT INTO Cards (card_number, customerID, issue_date, expiry_date, cvv, status) 
VALUES ('8732624387350365', 9, SYSDATE, ADD_MONTHS(SYSDATE, 48), '042', 'ACTIVE');
INSERT INTO Cards (card_number, customerID, issue_date, expiry_date, cvv, status) 
VALUES ('8719730807158351', 10, SYSDATE, ADD_MONTHS(SYSDATE, 48), '182', 'ACTIVE');

INSERT INTO Cards (card_number, customerID, issue_date, expiry_date, cvv, status) 
VALUES ('8767195673954125', 1, SYSDATE, ADD_MONTHS(SYSDATE, 48), '713', 'ACTIVE');
INSERT INTO Cards (card_number, customerID, issue_date, expiry_date, cvv, status) 
VALUES ('8789385066246886', 3, SYSDATE, ADD_MONTHS(SYSDATE, 48), '043', 'ACTIVE');
INSERT INTO Cards (card_number, customerID, issue_date, expiry_date, cvv, status) 
VALUES ('8762407989705153', 5, SYSDATE, ADD_MONTHS(SYSDATE, 48), '420', 'ACTIVE');
INSERT INTO Cards (card_number, customerID, issue_date, expiry_date, cvv, status) 
VALUES ('8748771542197073', 7, SYSDATE, ADD_MONTHS(SYSDATE, 48), '682', 'ACTIVE');
INSERT INTO Cards (card_number, customerID, issue_date, expiry_date, cvv, status) 
VALUES ('8768631142795124', 9, SYSDATE, ADD_MONTHS(SYSDATE, 48), '928', 'ACTIVE');

-- Insert debit cards
INSERT INTO Debit (card_number, checking_account) VALUES ('8758582304046808', 1);
INSERT INTO Debit (card_number, checking_account) VALUES ('8751377543332884', 2);
INSERT INTO Debit (card_number, checking_account) VALUES ('8754303970563211', 3);
INSERT INTO Debit (card_number, checking_account) VALUES ('8764288933699454', 4);
INSERT INTO Debit (card_number, checking_account) VALUES ('8796795893543805', 5);
INSERT INTO Debit (card_number, checking_account) VALUES ('8742375999854302', 6);
INSERT INTO Debit (card_number, checking_account) VALUES ('8702246358014335', 7);
INSERT INTO Debit (card_number, checking_account) VALUES ('8730060947660930', 8);
INSERT INTO Debit (card_number, checking_account) VALUES ('8732624387350365', 9);
INSERT INTO Debit (card_number, checking_account) VALUES ('8719730807158351', 10);

-- Insert credit cards
INSERT INTO Credit (card_number, credit_limit, current_balance, due_balance, payment_due_date, interest_rate) 
VALUES ('8767195673954125', 2000.00, 450.00, 150.00, ADD_MONTHS(SYSDATE, 1), 0.40);
INSERT INTO Credit (card_number, credit_limit, current_balance, due_balance, payment_due_date, interest_rate) 
VALUES ('8789385066246886', 5000.00, 1500.00, 500.00, ADD_MONTHS(SYSDATE, 1), 0.45);
INSERT INTO Credit (card_number, credit_limit, current_balance, due_balance, payment_due_date, interest_rate) 
VALUES ('8762407989705153', 10000.00, 380.00, 140.00, ADD_MONTHS(SYSDATE, 1), 0.35);
INSERT INTO Credit (card_number, credit_limit, current_balance, due_balance, payment_due_date, interest_rate) 
VALUES ('8748771542197073', 15000.00, 900.00, 400.00, ADD_MONTHS(SYSDATE, 1), 0.38);
INSERT INTO Credit (card_number, credit_limit, current_balance, due_balance, payment_due_date, interest_rate) 
VALUES ('8768631142795124', 25000.00, 14500.00, 2000.00, ADD_MONTHS(SYSDATE, 1), 0.25);

-- 1. AccountTransaction (DEPOSIT)
INSERT INTO AccountTransaction (transactionID, type)
VALUES (1, 'DEPOSIT');

-- 2. AccountTransaction (WITHDRAWAL)
INSERT INTO AccountTransaction (transactionID, type)
VALUES (2, 'WITHDRAWAL');

-- 3. AccountTransaction (TRANSFER)
INSERT INTO AccountTransaction (transactionID, type)
VALUES (3, 'TRANSFER');

-- 4. AccountTransaction (DEPOSIT)
INSERT INTO AccountTransaction (transactionID, type)
VALUES (5, 'DEPOSIT');

-- 5. AccountTransaction (FEE)
INSERT INTO AccountTransaction (transactionID, type)
VALUES (6, 'FEE');

-- 1. CardTransaction (PURCHASE)
INSERT INTO CardTransaction (transactionID, cardNumber, cardExpiry)
VALUES (4, '1234567890123456', TO_DATE('2026-06-01','YYYY-MM-DD'));

-- 2. CardTransaction (PURCHASE)
INSERT INTO CardTransaction (transactionID, cardNumber, cardExpiry)
VALUES (8, '2345678901234567', TO_DATE('2027-06-01','YYYY-MM-DD'));

-- 3. CardTransaction (PURCHASE)
INSERT INTO CardTransaction (transactionID, cardNumber, cardExpiry)
VALUES (16, '3456789012345678', TO_DATE('2025-12-01','YYYY-MM-DD'));

-- 4. CardTransaction (PURCHASE)
INSERT INTO CardTransaction (transactionID, cardNumber, cardExpiry)
VALUES (23, '4567890123456789', TO_DATE('2026-05-01','YYYY-MM-DD'));

-- 5. CardTransaction (PURCHASE)
INSERT INTO CardTransaction (transactionID, cardNumber, cardExpiry)
VALUES (23, '5678901234567890', TO_DATE('2025-11-01','YYYY-MM-DD'));

-- 1. CCPayment (Payment)
INSERT INTO CCPayment (transactionID, amount)
VALUES (12, 150.00);

-- 2. CCPayment (Payment)
INSERT INTO CCPayment (transactionID, amount)
VALUES (17, 250.00);

-- 3. CCPayment (Payment)
INSERT INTO CCPayment (transactionID, amount)
VALUES (26, 300.00);

-- 4. CCPayment (Payment)
INSERT INTO CCPayment (transactionID, amount)
VALUES (27, 100.00);

-- 5. CCPayment (Payment)
INSERT INTO CCPayment (transactionID, amount)
VALUES (25, 200.00);

-- 1. Loan Payment (Payment)
INSERT INTO LoanPayment (transactionID, amount)
VALUES (7, 150.00);

-- 2. Loan Payment (Payment)
INSERT INTO LoanPayment (transactionID, amount)
VALUES (17, 250.00);

-- 3. Loan Payment (Payment)
INSERT INTO LoanPayment (transactionID, amount)
VALUES (25, 300.00);

-- 4. Loan Payment (Payment)
INSERT INTO LoanPayment (transactionID, amount)
VALUES (21, 200.00);

-- 5. Loan Payment (Payment)
INSERT INTO LoanPayment (transactionID, amount)
VALUES (24, 400.00);

-- 1. Transaction (DEPOSIT)
INSERT INTO Transaction (type, amount, description, transaction_date, status, from_account, to_account)
VALUES ('DEPOSIT', 1000.00, 'Deposit to checking account', SYSDATE, 'COMPLETED', NULL, 1);
-- 1. AccountTransaction (DEPOSIT)
INSERT INTO AccountTransaction (transactionID, type)
VALUES (1, 'DEPOSIT');

-- 2. Transaction (WITHDRAWAL)
INSERT INTO Transaction (type, amount, description, transaction_date, status, from_account, to_account)
VALUES ('WITHDRAWAL', 200.00, 'Withdrawal from checking account', SYSDATE, 'COMPLETED', 1, NULL);
-- 2. AccountTransaction (WITHDRAWAL)
INSERT INTO AccountTransaction (transactionID, type)
VALUES (2, 'WITHDRAWAL');

-- 3. Transaction (TRANSFER)
INSERT INTO Transaction (type, amount, description, transaction_date, status, from_account, to_account)
VALUES ('TRANSFER', 300.00, 'Transfer from checking to savings', SYSDATE, 'COMPLETED', 1, 2);
-- 3. AccountTransaction (TRANSFER)
INSERT INTO AccountTransaction (transactionID, type)
VALUES (3, 'TRANSFER');

-- 4. Transaction (PURCHASE)
INSERT INTO Transaction (type, amount, description, transaction_date, status, from_account, to_account)
VALUES ('PURCHASE', 50.00, 'Purchase at Amazon', SYSDATE, 'COMPLETED', NULL, 3);
-- 4. CardTransaction (PURCHASE)
INSERT INTO CardTransaction (transactionID, cardNumber, cardExpiry)
VALUES (4, '1234567890123456', TO_DATE('2026-06-01','YYYY-MM-DD'));

-- 5. Transaction (DEPOSIT)
INSERT INTO Transaction (type, amount, description, transaction_date, status, from_account, to_account)
VALUES ('DEPOSIT', 500.00, 'Deposit to savings account', SYSDATE, 'COMPLETED', NULL, 2);
-- 5. AccountTransaction (DEPOSIT)
INSERT INTO AccountTransaction (transactionID, type)
VALUES (5, 'DEPOSIT');

-- 6. Transaction (FEE)
INSERT INTO Transaction (type, amount, description, transaction_date, status, from_account, to_account)
VALUES ('FEE', 10.00, 'Account maintenance fee', SYSDATE, 'COMPLETED', 2, NULL);
-- 6. AccountTransaction (FEE)
INSERT INTO AccountTransaction (transactionID, type)
VALUES (6, 'FEE');

-- 7. Transaction (PAYMENT)
INSERT INTO Transaction (type, amount, description, transaction_date, status, from_account, to_account)
VALUES ('PAYMENT', 150.00, 'Loan payment', SYSDATE, 'COMPLETED', 4, NULL);
-- 7. LoanPayment (Payment)
INSERT INTO LoanPayment (transactionID, amount)
VALUES (7, 150.00);

-- 8. Transaction (PURCHASE)
INSERT INTO Transaction (type, amount, description, transaction_date, status, from_account, to_account)
VALUES ('PURCHASE', 200.00, 'Purchase at BestBuy', SYSDATE, 'COMPLETED', NULL, 5);
-- 8. CardTransaction (PURCHASE)
INSERT INTO CardTransaction (transactionID, cardNumber, cardExpiry)
VALUES (8, '2345678901234567', TO_DATE('2027-06-01','YYYY-MM-DD'));

-- 9. Transaction (DEPOSIT)
INSERT INTO Transaction (type, amount, description, transaction_date, status, from_account, to_account)
VALUES ('DEPOSIT', 750.00, 'Deposit to checking account', SYSDATE, 'COMPLETED', NULL, 1);
-- 9. AccountTransaction (DEPOSIT)
INSERT INTO AccountTransaction (transactionID, type)
VALUES (9, 'DEPOSIT');

-- 10. Transaction (WITHDRAWAL)
INSERT INTO Transaction (type, amount, description, transaction_date, status, from_account, to_account)
VALUES ('WITHDRAWAL', 300.00, 'Withdrawal from savings account', SYSDATE, 'COMPLETED', 2, NULL);
-- 10. AccountTransaction (WITHDRAWAL)
INSERT INTO AccountTransaction (transactionID, type)
VALUES (10, 'WITHDRAWAL');

-- 11. Transaction (INTEREST)
INSERT INTO Transaction (type, amount, description, transaction_date, status, from_account, to_account)
VALUES ('INTEREST', 5.00, 'Interest added to savings account', SYSDATE, 'COMPLETED', NULL, 2);
-- 11. AccountTransaction (INTEREST)
INSERT INTO AccountTransaction (transactionID, type)
VALUES (11, 'INTEREST');

-- 12. Transaction (FEE)
INSERT INTO Transaction (type, amount, description, transaction_date, status, from_account, to_account)
VALUES ('FEE', 3.00, 'Card annual fee', SYSDATE, 'COMPLETED', NULL, 6);
-- 12. CCPayment (Payment)
INSERT INTO CCPayment (transactionID, amount)
VALUES (12, 150.00);

-- 13. Transaction (DEPOSIT)
INSERT INTO Transaction (type, amount, description, transaction_date, status, from_account, to_account)
VALUES ('DEPOSIT', 200.00, 'Deposit to checking account', SYSDATE, 'COMPLETED', NULL, 1);
-- 13. AccountTransaction (DEPOSIT)
INSERT INTO AccountTransaction (transactionID, type)
VALUES (13, 'DEPOSIT');

-- 14. Transaction (TRANSFER)
INSERT INTO Transaction (type, amount, description, transaction_date, status, from_account, to_account)
VALUES ('TRANSFER', 50.00, 'Transfer from checking to savings', SYSDATE, 'COMPLETED', 1, 2);
-- 14. Account
