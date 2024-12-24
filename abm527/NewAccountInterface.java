import java.sql.*;

public class NewAccountInterface extends BankInterface {

    public NewAccountInterface(Connection connection, int customerId, String customerName) {
        super(connection, customerId, customerName);
    }

    @Override
    public void start() {
        while (true) {
            displayMenu();
            int option = safeReadInt("Enter your choice: ");
            if (option == 4) {
                System.out.println("Returning to Customer Menu...");
                break;
            }

            processOption(option);
        }
    }

    @Override
    protected void displayMenu() {
        System.out.println("\nOpen New Account Menu for " + customerName + ":");
        System.out.println("1. Open Checking Account");
        System.out.println("2. Open Savings Account");
        System.out.println("3. Open Investment Account");
        System.out.println("4. Return to Customer Menu");
    }

    @Override
    protected void processOption(int option) {
        try {
            switch (option) {
                case 1:
                    openChecking();
                    break;
                case 2:
                    openSavings();
                    break;
                case 3:
                    openInvestment();
                    break;
                default:
                    System.out.println("Invalid option. Please choose a correct number.");
            }
        } catch (SQLException e) {
            System.out.println("Error opening account: " + e.getMessage());
            System.out.println("Try different parameters or ensure validity of inputs.");
        }
    }

    private void openChecking() throws SQLException {
        double initialBalance = safeReadDouble("Enter initial deposit amount for checking: $");
        if (!validateAmount(initialBalance)) {
            System.out.println("Invalid amount. Must be positive.");
            return;
        }

        connection.setAutoCommit(false);
        try {
            int newAcctId = insertAccount(initialBalance);
            insertChecking(newAcctId);
            linkCustomerAccount(newAcctId, customerId);
            connection.commit();
            System.out.printf("Checking account opened successfully. Account ID: %d%n", newAcctId);
            System.out.printf("Starting Balance: $%.2f%n", initialBalance);
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private void openSavings() throws SQLException {
        double initialBalance = safeReadDouble("Enter initial deposit amount for savings: $");
        if (!validateAmount(initialBalance)) {
            System.out.println("Invalid amount. Must be positive.");
            return;
        }
        double minBalance = safeReadDouble("Enter minimum balance requirement: $");
        double penaltyRate = safeReadDouble("Enter penalty rate (e.g. 0.02 for 2%): ");

        connection.setAutoCommit(false);
        try {
            int newAcctId = insertAccount(initialBalance);
            insertSavings(newAcctId, minBalance, penaltyRate);
            linkCustomerAccount(newAcctId, customerId);
            connection.commit();
            System.out.printf("Savings account opened successfully. Account ID: %d%n", newAcctId);
            System.out.printf("Starting Balance: $%.2f, Min Balance: $%.2f, Penalty Rate: %.2f%%%n",
                              initialBalance, minBalance, penaltyRate*100);
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private void openInvestment() throws SQLException {
        double initialBalance = safeReadDouble("Enter initial amount invested: $");
        if (!validateAmount(initialBalance)) {
            System.out.println("Invalid amount. Must be positive.");
            return;
        }
        String assetList = safeReadLine("Enter asset list (comma-separated): ");
        String riskLevel = "";
        while (true) {
            riskLevel = safeReadLine("Enter risk level (LOW, MEDIUM, HIGH): ").toUpperCase();
            if (riskLevel.equals("LOW") || riskLevel.equals("MEDIUM") || riskLevel.equals("HIGH")) break;
            System.out.println("Invalid risk level. Try again.");
        }

        connection.setAutoCommit(false);
        try {
            int newAcctId = insertAccount(initialBalance);
            insertInvestment(newAcctId, assetList, riskLevel);
            linkCustomerAccount(newAcctId, customerId);
            connection.commit();
            System.out.printf("Investment account opened successfully. Account ID: %d%n", newAcctId);
            System.out.printf("Starting Balance: $%.2f, Assets: %s, Risk Level: %s%n",
                              initialBalance, assetList, riskLevel);
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private int insertAccount(double balance) throws SQLException {
        String insert = "INSERT INTO Account (balance, status, branchID) VALUES (?, 'ACTIVE', 1)";
        try (PreparedStatement stmt = connection.prepareStatement(insert, new String[] {"accountID"})) {
            stmt.setDouble(1, balance);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    return fetchLatestAccountID();
                }
            }
        }
    }

    private int fetchLatestAccountID() throws SQLException {
        String q = "SELECT account_seq.CURRVAL FROM dual";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(q)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("Could not fetch newly created account ID");
        }
    }

    private void insertChecking(int accountId) throws SQLException {
        String q = "INSERT INTO Checking (accountID) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(q)) {
            stmt.setInt(1, accountId);
            stmt.executeUpdate();
        }
    }

    private void insertSavings(int accountId, double minBalance, double penaltyRate) throws SQLException {
        String q = "INSERT INTO Savings (accountID, min_balance, penalty_rate) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(q)) {
            stmt.setInt(1, accountId);
            stmt.setDouble(2, minBalance);
            stmt.setDouble(3, penaltyRate);
            stmt.executeUpdate();
        }
    }

    private void insertInvestment(int accountId, String assetList, String riskLevel) throws SQLException {
        String q = "INSERT INTO Investment (accountID, asset_list, risk_level) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(q)) {
            stmt.setInt(1, accountId);
            stmt.setString(2, assetList);
            stmt.setString(3, riskLevel);
            stmt.executeUpdate();
        }
    }

    private void linkCustomerAccount(int accountId, int custId) throws SQLException {
        String q = "INSERT INTO CustomerAccount (customerID, accountID) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(q)) {
            stmt.setInt(1, custId);
            stmt.setInt(2, accountId);
            stmt.executeUpdate();
        }
    }
}
