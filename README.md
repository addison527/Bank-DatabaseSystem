# LUBank Database System

**Name:** Addison Man  
**Lehigh ID:** abm527 

## LUBANK Overview
Addison Man database system for LUBank, a fictional online bank institution. The system manages retail customers with accounts, loans, debit cards, and credit cards. It has multiple interfaces to handle different banking operations such as deposits, withdrawals, purchases, payments, account creation, card creation, and loan management. The system is designed to interact with a database and provide a user-friendly interface for testing the functionalities.

## Instructions for setting up the database
1. **Create/Reset Schema:**
   - ensure you are connected to `edgar1.cse.lehigh.edu` as user `abm527` or equivalent.
   - open & run `Tables.sql` in sql dev

3. **Clearing Customers:**
   - if you inserted customers multiple times, use `clear_customers.sql` to deletes all customers & linked accounts and commits.:
     ```sql
     @clear_customers.sql
     ```
   - run `@insert_customers.sql` to reload a clean set.

## Running the Application
1. **Compile:**
   - ensure `ojdbc11.jar` is in the same directory as `build.sh`.
   - run `./build.sh` or `bash build.sh` in GIT BASH
   - if successful, `abm527.jar` is created.

2. **Run:**
   - `java -jar abm527.jar`
   - then you can enter your oracle account log in details

## Navigation
- **Main Menu:**
  - customer role: Enter ID (listed from the database with pagination). Once logged in, I, the bank master, will welcome you with your options.
  - bank Employee role: View totals, reports, blah blah blah, etc.

- **Customer Interface Menu Options:**
  1. Deposit/Withdrawal: Manage funds, show accounts, deposit or withdraw.
  2. Make Purchase: Use debit or credit cards, check balances, make a purchase.
  3. Make Payment: Pay loans or credit cards, view active loans and card balances.
  4. Open a New Account: Create checking/savings/investment accounts.
  5. Obtain a New or Replacement Card: Create new debit/credit cards.
  6. Take out a New Loan: Create unsecured or mortgage loans.
  7. Return to Main Menu: Go back to role selection.

- **Within Each Sub-Menu:**
  - You’ll see instructions, for example in Deposit/Withdrawal menu, how to enter account IDs.
  - Input validation: If you enter something invalid, you’ll get a clear error message and guidance.
  - After successful transactions (deposit, withdrawal, payment, purchase), you see updated balances.
  - After creating accounts/cards/loans, you see a summary of what was created. Sometimes you have to scroll up.

- **Bank Employee Menu Options:**
  1. Compute total loans outstanding
  2. Compute total account balances
  3. Transaction frequency/volume report (last 7 days)
  4. Totals per customer (top 10 by balance)
  5. Return to Main Menu

- **Within Each Sub-Menu:**
  - You’ll see instructions, might ask you for trivial info, go crazy.

## **Interfaces and How to Use/Test Them**

### **1. Deposit and Withdrawal Interface**  
- **Purpose:** Handle deposits and withdrawals for an existing customer.  


### **2. Purchase Interface**  
- **Purpose:** Manage purchases made by customers using their accounts.  


### **3. Payment Interface**  
- **Purpose:** Process payments for purchases or loans.  


### **4. New Account Interface**  
- **Purpose:** Create new bank accounts for customers.  


### **5. New Card Interface**  
- **Purpose:** Issue a new credit or debit card.  


### **6. New Loan Interface**  
- **Purpose:** Approve or manage customer loans.  


### **TESTING**


## Additional Notes
- Triggers:
  - `check_savings_balance` trigger enforces penalty on savings if below min balance, keeps the min that the user inputs: will enforce good user financial habits.
  - `check_credit_limit` trigger enforces the user to spend under their credit limmit and not ruin their credit score.
  
## Assumptions:
1. customers in this world make their own credit scores. (exciting! and I wish)

## Cool things
1. can enter '0' at anytime to return to previous EXCEPT on main menu

## Sources
https://stackoverflowcomquestions8138411masking-password-input-from-the-console-java


## ER - DESIGN
![ABM527_ER_DESIGN](https://github.com/[addison527]/[Bank_DatabaseSystem]/blob/[main]/ABM527_ER_DESIGN.pdf?raw=true)
