import java.sql.*;

public class PurchaseInterface extends BankInterface {

    public PurchaseInterface(Connection connection, int customerId, String customerName) {
        super(connection, customerId, customerName);
    }

    @Override
    public void start() {
        try {
            while (true) {
                displayCards();
                displayMenu();
                int option = safeReadInt("Enter your choice: ");
                if (option == 3) {
                    System.out.println("Returning to Customer Menu...");
                    break;
                }

                processOption(option);
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            System.out.println("Check card numbers and try again.");
        }
    }

    @Override
    protected void displayMenu() {
        System.out.println("\nPurchase Menu for " + customerName + ":");
        System.out.println("1. Make Purchase with Debit Card (Use checking account funds)");
        System.out.println("2. Make Purchase with Credit Card (Use available credit)");
        System.out.println("3. Return to Customer Menu");
    }

    private void displayCards() throws SQLException {
        System.out.println("\nYour Debit Cards (Active):");
        String debitQuery = 
            "SELECT c.card_number, ch.accountID, a.balance " +
            "FROM Cards c " +
            "JOIN Debit d ON c.card_number = d.card_number " +
            "JOIN Checking ch ON d.checking_account = ch.accountID " +
            "JOIN Account a ON ch.accountID = a.accountID " +
            "WHERE c.customerID = ? AND c.status = 'ACTIVE'";
        
        try (PreparedStatement stmt = connection.prepareStatement(debitQuery)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("Card Number\tLinked Account\tAvailable Balance");
                System.out.println("------------------------------------------------");
                boolean foundDebit = false;
                while (rs.next()) {
                    foundDebit = true;
                    String cardNum = rs.getString("card_number");
                    System.out.printf("%s\t%d\t\t$%.2f%n",
                        cardNum,
                        rs.getInt("accountID"),
                        rs.getDouble("balance"));
                }
                if (!foundDebit) {
                    System.out.println("(No active debit cards)");
                }
            }
        }

        System.out.println("\nYour Credit Cards (Active):");
        String creditQuery = 
            "SELECT c.card_number, cr.credit_limit, cr.current_balance " +
            "FROM Cards c " +
            "JOIN Credit cr ON c.card_number = cr.card_number " +
            "WHERE c.customerID = ? AND c.status = 'ACTIVE'";
        
        try (PreparedStatement stmt = connection.prepareStatement(creditQuery)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("Card Number\tCredit Limit\tAvailable Credit");
                System.out.println("------------------------------------------------");
                boolean foundCredit = false;
                while (rs.next()) {
                    foundCredit = true;
                    String cardNum = rs.getString("card_number");
                    double creditLimit = rs.getDouble("credit_limit");
                    double currentBalance = rs.getDouble("current_balance");
                    double availableCredit = creditLimit - currentBalance;
                    System.out.printf("%s\t$%.2f\t$%.2f%n",
                        cardNum,
                        creditLimit,
                        availableCredit);
                }
                if (!foundCredit) {
                    System.out.println("(No active credit cards)");
                }
            }
        }
    }

    @Override
    protected void processOption(int option) {
        try {
            switch (option) {
                case 1:
                    handleDebitPurchase();
                    break;
                case 2:
                    handleCreditPurchase();
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } catch (SQLException e) {
            System.out.println("Error processing purchase: " + e.getMessage());
            System.out.println("Ensure the card number is correct and you have enough funds/credit.");
        }
    }

    private void handleDebitPurchase() throws SQLException {
        System.out.println("Enter the full debit card number from the list above. For example: 7281922980134806 (not just masked).");
        String cardNumber = safeReadLine("Enter debit card number: ");
        
        String validateQuery = 
            "SELECT d.checking_account, a.balance " +
            "FROM Cards c " +
            "JOIN Debit d ON c.card_number = d.card_number " +
            "JOIN Checking ch ON d.checking_account = ch.accountID " +
            "JOIN Account a ON ch.accountID = a.accountID " +
            "WHERE c.card_number = ? AND c.customerID = ? AND c.status = 'ACTIVE'";
        
        int accountId;
        double currentBalance;
        
        try (PreparedStatement stmt = connection.prepareStatement(validateQuery)) {
            stmt.setString(1, cardNumber);
            stmt.setInt(2, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Invalid or inactive debit card. Make sure you typed the full card number.");
                    return;
                }
                
                accountId = rs.getInt("checking_account");
                currentBalance = rs.getDouble("balance");
            }
        }

        double amount = safeReadDouble("Enter purchase amount: $");
        if (!validateAmount(amount)) {
            System.out.println("Invalid amount. Must be positive.");
            return;
        }

        if (currentBalance < amount) {
            System.out.println("Insufficient funds in the linked checking account.");
            return;
        }

        connection.setAutoCommit(false);
        try {
            double newBalance = currentBalance - amount;
            updateAccountBalance(accountId, newBalance);
            recordTransaction("PURCHASE", amount, "Debit card purchase", accountId, null);

            String cardTxnQuery = "INSERT INTO CardTransaction (transactionID, cardNumber, cardExpiry) " +
                                   "SELECT transaction_seq.CURRVAL, c.card_number, c.expiry_date " +
                                   "FROM Cards c WHERE c.card_number = ?";
            try (PreparedStatement cStmt = connection.prepareStatement(cardTxnQuery)) {
                cStmt.setString(1, cardNumber);
                cStmt.executeUpdate();
            }

            connection.commit();
            System.out.printf("Purchase successful. Remaining checking balance: $%.2f%n", newBalance);
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private void handleCreditPurchase() throws SQLException {
        System.out.println("Enter the full credit card number from the list above.");
        String cardNumber = safeReadLine("Enter credit card number: ");
        
        String validateQuery = 
            "SELECT credit_limit, current_balance " +
            "FROM Cards c " +
            "JOIN Credit cr ON c.card_number = cr.card_number " +
            "WHERE c.card_number = ? AND c.customerID = ? AND c.status = 'ACTIVE'";
        
        double creditLimit;
        double currentBalance;
        
        try (PreparedStatement stmt = connection.prepareStatement(validateQuery)) {
            stmt.setString(1, cardNumber);
            stmt.setInt(2, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Invalid or inactive credit card. Check the card list and try again.");
                    return;
                }
                
                creditLimit = rs.getDouble("credit_limit");
                currentBalance = rs.getDouble("current_balance");
            }
        }

        double amount = safeReadDouble("Enter purchase amount: $");
        if (!validateAmount(amount)) {
            System.out.println("Invalid amount. Must be positive.");
            return;
        }

        if (currentBalance + amount > creditLimit) {
            System.out.println("Purchase would exceed credit limit. Transaction cancelled.");
            return;
        }

        connection.setAutoCommit(false);
        try {
            String updateQuery = "UPDATE Credit SET current_balance = current_balance + ?, " +
                                  "due_balance = due_balance + ? " +
                                  "WHERE card_number = ?";
            try (PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
                stmt.setDouble(1, amount);
                stmt.setDouble(2, amount);
                stmt.setString(3, cardNumber);
                stmt.executeUpdate();
            }

            recordTransaction("PURCHASE", amount, "Credit card purchase", null, null);

            String cardTxnQuery = "INSERT INTO CardTransaction (transactionID, cardNumber, cardExpiry) " +
                                   "SELECT transaction_seq.CURRVAL, c.card_number, c.expiry_date FROM Cards c WHERE c.card_number = ?";
            try (PreparedStatement cStmt = connection.prepareStatement(cardTxnQuery)) {
                cStmt.setString(1, cardNumber);
                cStmt.executeUpdate();
            }

            connection.commit();
            double newBalance = currentBalance + amount;
            System.out.printf("Purchase successful. New credit card balance: $%.2f%n", newBalance);
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
}
