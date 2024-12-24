import java.sql.*;

public class PaymentInterface extends BankInterface {

    public PaymentInterface(Connection connection, int customerId, String customerName) {
        super(connection, customerId, customerName);
    }

    @Override
    public void start() {
        try {
            while (true) {
                displayPaymentOptions();
                displayMenu();
                int option = safeReadInt("Enter your choice: ");
                if (checkAbortInt(option)) return;

                if (option == 3) {
                    System.out.println("Returning to Customer Menu...");
                    break;
                }

                processOption(option);
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            System.out.println("If the error persists, verify account/loan/card IDs or try again.");
        }
    }

    @Override
    protected void displayMenu() {
        System.out.println("\nPayment Menu for " + customerName + ":");
        System.out.println("1. Make Loan Payment (Pay down your loans)");
        System.out.println("2. Make Credit Card Payment (Reduce your CC balance)");
        System.out.println("3. Return to Customer Menu");
        System.out.println("Enter 0 at any prompt to return at any step.");
    }

    private void displayPaymentOptions() throws SQLException {
        System.out.println("\nYour Active Loans:");
        String loanQuery =
            "SELECT l.loanID, l.amount, l.remaining_balance, l.monthly_payment, " +
            "CASE WHEN mo.loanID IS NOT NULL THEN 'Mortgage' WHEN u.loanID IS NOT NULL THEN 'Unsecured' END as loan_type " +
            "FROM Loans l " +
            "LEFT JOIN Mortgages mo ON l.loanID = mo.loanID " +
            "LEFT JOIN Unsecured u ON l.loanID = u.loanID " +
            "WHERE l.customerID = ? AND l.status = 'ACTIVE' ORDER BY l.loanID";
        
        try (PreparedStatement stmt = connection.prepareStatement(loanQuery)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.printf("%-10s%-15s%-15s%-15s%-15s%n", "LoanID", "Type","Total Amount","Remaining","Monthly Payment");
                System.out.println("------------------------------------------------------------------");
                boolean foundLoan = false;
                while (rs.next()) {
                    foundLoan = true;
                    System.out.printf("%-10d%-15s$%-14.2f$%-14.2f$%-14.2f%n",
                        rs.getInt("loanID"),
                        rs.getString("loan_type"),
                        rs.getDouble("amount"),
                        rs.getDouble("remaining_balance"),
                        rs.getDouble("monthly_payment"));
                }
                if (!foundLoan) {
                    System.out.println("(No active loans)");
                }
            }
        }

        System.out.println("\nYour Credit Card Balances (Active cards with balance):\n");
        String cardQuery =
            "SELECT c.card_number, cr.current_balance, cr.due_balance, cr.payment_due_date " +
            "FROM Cards c " +
            "JOIN Credit cr ON c.card_number = cr.card_number " +
            "WHERE c.customerID = ? AND c.status = 'ACTIVE' AND cr.current_balance > 0 ORDER BY c.card_number";
        try (PreparedStatement stmt = connection.prepareStatement(cardQuery)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.printf("%-20s%-15s%-15s%-15s%n", "CardNumber","CurrentBalance","AmountDue","DueDate");
                System.out.println("----------------------------------------------------------");
                boolean foundCard = false;
                while (rs.next()) {
                    foundCard = true;
                    System.out.printf("%-20s$%-14.2f$%-14.2f%-15s%n",
                        rs.getString("card_number"),
                        rs.getDouble("current_balance"),
                        rs.getDouble("due_balance"),
                        rs.getDate("payment_due_date"));
                }
                if (!foundCard) {
                    System.out.println("(No active balances on credit cards)");
                }
            }
        }
    }

    @Override
    protected void processOption(int option) {
        try {
            switch (option) {
                case 1:
                    handleLoanPayment();
                    break;
                case 2:
                    handleCreditCardPayment();
                    break;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        } catch (SQLException e) {
            System.out.println("Error processing payment: " + e.getMessage());
            System.out.println("Check the loan/card and account IDs carefully.");
        }
    }

    private void handleLoanPayment() throws SQLException {
        System.out.println("Enter a Loan ID from the Active Loans list above. (0 to return)");
        int loanId = safeReadInt("Enter loan ID: ");
        if (checkAbortInt(loanId)) return;

        String validateQuery =
            "SELECT remaining_balance, monthly_payment, interest_rate, term_months " +
            "FROM Loans " +
            "WHERE loanID = ? AND customerID = ? AND status = 'ACTIVE'";

        double remainingBalance, monthlyPayment, interestRate;
        int termMonths;

        try (PreparedStatement stmt = connection.prepareStatement(validateQuery)) {
            stmt.setInt(1, loanId);
            stmt.setInt(2, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Invalid or inactive loan. Please pick a valid loan ID or 0 to return.");
                    return;
                }
                remainingBalance = rs.getDouble("remaining_balance");
                monthlyPayment = rs.getDouble("monthly_payment");
                interestRate = rs.getDouble("interest_rate");
                termMonths = rs.getInt("term_months");
            }
        }

        System.out.printf("Monthly payment due: $%.2f%n", monthlyPayment);
        double amount = safeReadDouble("Enter payment amount: $");
        if (checkAbortDouble(amount)) return;

        if (!validateAmount(amount)) {
            System.out.println("Invalid amount. Must be positive. Or 0 to return.");
            return;
        }

        if (amount > remainingBalance) {
            System.out.println("Payment exceeds remaining balance. Adjusting to full remaining: $" + remainingBalance);
            amount = remainingBalance;
        }

        // Show accounts before asking:
        displayCustomerAccounts(customerId);

        System.out.println("Enter the account ID from which to pay this loan. Or 0 to return.");
        int accountId = safeReadInt("Enter account ID to pay from: ");
        if (checkAbortInt(accountId)) return;

        if (!validateCustomerOwnsAccount(accountId, customerId)) {
            System.out.println("You do not own this account or it doesn't exist. Try again.");
            return;
        }

        if (!validateAccountActive(accountId)) {
            System.out.println("This account is not active. Cannot use it for payment.");
            return;
        }

        double accountBalance = getAccountBalance(accountId);
        if (accountBalance < amount) {
            System.out.println("Insufficient funds in that account. Deposit more funds or use another account.");
            return;
        }

        connection.setAutoCommit(false);
        try {
            String updateLoanQuery = "UPDATE Loans SET remaining_balance = remaining_balance - ? WHERE loanID = ?";
            try (PreparedStatement stmt = connection.prepareStatement(updateLoanQuery)) {
                stmt.setDouble(1, amount);
                stmt.setInt(2, loanId);
                stmt.executeUpdate();
            }

            updateAccountBalance(accountId, accountBalance - amount);
            recordTransaction("PAYMENT", amount, "Loan payment", accountId, null);
            recalculateLoanMonthlyPayment(loanId);
            connection.commit();
            String afterQ = "SELECT remaining_balance, monthly_payment FROM Loans WHERE loanID = ?";
            double newRemaining;
            double newMonthlyPayment;
            try (PreparedStatement aft = connection.prepareStatement(afterQ)) {
                aft.setInt(1, loanId);
                try (ResultSet rs = aft.executeQuery()) {
                    rs.next();
                    newRemaining = rs.getDouble("remaining_balance");
                    newMonthlyPayment = rs.getDouble("monthly_payment");
                }
            }

            System.out.printf("Payment successful. Remaining loan balance: $%.2f%n", newRemaining);
            System.out.printf("New monthly payment: $%.2f%n", newMonthlyPayment);

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private void handleCreditCardPayment() throws SQLException {
        System.out.println("Enter the full credit card number from those listed above (or 0 to return).");
        String cardNumber = safeReadLine("Enter card number: ");
        if (checkAbortLine(cardNumber)) return;

        String validateQuery =
            "SELECT cr.current_balance, cr.due_balance " +
            "FROM Cards c " +
            "JOIN Credit cr ON c.card_number = cr.card_number " +
            "WHERE c.card_number = ? AND c.customerID = ? AND c.status = 'ACTIVE'";

        double currentBalance, dueBalance;

        try (PreparedStatement stmt = connection.prepareStatement(validateQuery)) {
            stmt.setString(1, cardNumber);
            stmt.setInt(2, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Invalid or inactive credit card. Check the card list and try again.");
                    return;
                }
                currentBalance = rs.getDouble("current_balance");
                dueBalance = rs.getDouble("due_balance");
            }
        }

        System.out.printf("Current CC balance: $%.2f%n", currentBalance);
        System.out.printf("Amount due: $%.2f%n", dueBalance);
        double amount = safeReadDouble("Enter payment amount: $");
        if (checkAbortDouble(amount)) return;

        if (!validateAmount(amount)) {
            System.out.println("Invalid amount. Must be positive.");
            return;
        }

        if (amount > currentBalance) {
            System.out.println("Payment exceeds current balance. Adjusting to full balance: $" + currentBalance);
            amount = currentBalance;
        }

        displayCustomerAccounts(customerId);
        System.out.println("Enter the account ID to pay from (or 0 to return).");
        int accountId = safeReadInt("Enter account ID: ");
        if (checkAbortInt(accountId)) return;

        if (!validateCustomerOwnsAccount(accountId, customerId)) {
            System.out.println("You do not own this account. Try again.");
            return;
        }

        if (!validateAccountActive(accountId)) {
            System.out.println("This account is not active.");
            return;
        }

        double accountBalance = getAccountBalance(accountId);
        if (accountBalance < amount) {
            System.out.println("Insufficient funds. Try another account or deposit first.");
            return;
        }

        connection.setAutoCommit(false);
        try {
            String updateCardQuery =
                "UPDATE Credit SET current_balance = current_balance - ?, " +
                "due_balance = CASE WHEN ? >= due_balance THEN 0 ELSE due_balance - ? END " +
                "WHERE card_number = ?";
            try (PreparedStatement stmt = connection.prepareStatement(updateCardQuery)) {
                stmt.setDouble(1, amount);
                stmt.setDouble(2, amount);
                stmt.setDouble(3, amount);
                stmt.setString(4, cardNumber);
                stmt.executeUpdate();
            }

            updateAccountBalance(accountId, accountBalance - amount);
            recordTransaction("PAYMENT", amount, "Credit card payment", accountId, null);

            String ccPaymentQuery = "INSERT INTO CCPayment (transactionID, paymentMethod) VALUES (transaction_seq.CURRVAL, 'VISA')";
            try (PreparedStatement cpStmt = connection.prepareStatement(ccPaymentQuery)) {
                cpStmt.executeUpdate();
            }

            connection.commit();
            double newBalance = currentBalance - amount;
            System.out.printf("Payment successful. New card balance: $%.2f%n", newBalance);
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
}
