import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DepositWithdrawalInterface extends BankInterface {

    public DepositWithdrawalInterface(Connection connection, int customerId, String customerName) {
        super(connection, customerId, customerName);
    }

    @Override
    public void start() {
        try {
            while (true) {
                displayCustomerAccounts(customerId);
                displayMenu();
                int option = safeReadInt("Enter your choice: ");

                if (option == 4) {
                    System.out.println("Returning to Customer Menu...");
                    break;
                }

                processOption(option);
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            System.out.println("Please try again or return to previous menu.");
        }
    }

    @Override
    protected void displayMenu() {
        System.out.println("\nDeposit/Withdrawal Menu for " + customerName + ":");
        System.out.println("1. Make a Deposit (Add funds to one of your accounts)");
        System.out.println("2. Make a Withdrawal (ATM - from checking/savings only)");
        System.out.println("3. Check Balance of a specific account");
        System.out.println("4. Return to Customer Menu");
    }

    @Override
    protected void processOption(int option) {
        try {
            switch (option) {
                case 1:
                    handleDeposit();
                    break;
                case 2:
                    handleWithdrawal();
                    break;
                case 3:
                    handleBalanceCheck();
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    System.out.println("Choose a valid number from the menu above.");
            }
        } catch (SQLException e) {
            System.out.println("Error processing transaction: " + e.getMessage());
            System.out.println("Please verify account IDs and try again.");
        }
    }

    private void handleDeposit() throws SQLException {
        System.out.println("Please enter an account ID from your active accounts listed above.");
        int accountId = safeReadInt("Enter account ID for deposit: ");
        if (!validateCustomerOwnsAccount(accountId, customerId)) {
            System.out.println("You do not own this account or it does not exist. Check the list above and try again.");
            return;
        }

        if (!validateAccountActive(accountId)) {
            System.out.println("This account is not active. Cannot deposit.");
            return;
        }

        double amount = safeReadDouble("Enter deposit amount: $");
        if (!validateAmount(amount)) {
            System.out.println("Invalid amount. Must be positive.");
            return;
        }

        connection.setAutoCommit(false);
        try {
            double currentBalance = getAccountBalance(accountId);
            double newBalance = currentBalance + amount;
            updateAccountBalance(accountId, newBalance);
            recordTransaction("DEPOSIT", amount, "Online deposit", null, accountId);
            connection.commit();
            System.out.printf("Successfully deposited $%.2f. New balance: $%.2f%n", amount, newBalance);
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private void handleWithdrawal() throws SQLException {
        System.out.println("Please choose a checking or savings account from the active accounts above.");
        int accountId = safeReadInt("Enter account ID for withdrawal: ");
        if (!validateCustomerOwnsAccount(accountId, customerId)) {
            System.out.println("You do not own this account or it doesn't exist. Try again.");
            return;
        }

        if (!validateAccountActive(accountId)) {
            System.out.println("This account is not active. Cannot withdraw.");
            return;
        }

        if (isInvestmentAccount(accountId)) {
            System.out.println("Withdrawals are not allowed from investment accounts.");
            return;
        }

        double amount = safeReadDouble("Enter withdrawal amount: $");
        if (!validateAmount(amount)) {
            System.out.println("Invalid amount. Must be positive.");
            return;
        }

        connection.setAutoCommit(false);
        try {
            double currentBalance = getAccountBalance(accountId);
            if (currentBalance < amount) {
                System.out.println("Insufficient funds in this account.");
                connection.rollback();
                connection.setAutoCommit(true);
                return;
            }

            double newBalance = currentBalance - amount;
            updateAccountBalance(accountId, newBalance);
            recordTransaction("WITHDRAWAL", amount, "ATM withdrawal", accountId, null);
            connection.commit();
            System.out.printf("Successfully withdrew $%.2f. New balance: $%.2f%n", amount, newBalance);
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private void handleBalanceCheck() throws SQLException {
        System.out.println("Check Balance: Please enter an account ID from your active accounts above.");
        int accountId = safeReadInt("Enter account ID to check balance: ");
        if (!validateCustomerOwnsAccount(accountId, customerId)) {
            System.out.println("You do not own this account or it doesn't exist. Try again.");
            return;
        }

        if (!validateAccountExists(accountId) || !validateAccountActive(accountId)) {
            System.out.println("Invalid or inactive account.");
            return;
        }

        double balance = getAccountBalance(accountId);
        System.out.printf("Current balance in account %d: $%.2f%n", accountId, balance);
    }
}
