import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class BankInterface {
    protected Connection connection;
    protected Scanner scanner;
    protected String customerName; 
    protected int customerId;

    public BankInterface(Connection connection, int customerId, String customerName) {
        this.connection = connection;
        this.scanner = new Scanner(System.in);
        this.customerName = customerName;
        this.customerId = customerId;
    }

    protected int safeReadInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) {
                int val = scanner.nextInt();
                scanner.nextLine(); 
                if (val == 0) {
                    return -1;
                }
                return val;
            } else {
                System.out.println("Invalid input. Please enter a valid number (no letters). Or enter 0 to return.");
                scanner.nextLine(); 
            }
        }
    }

    protected double safeReadDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextDouble()) {
                double val = scanner.nextDouble();
                scanner.nextLine();
                if (val == 0.0) {
                    return -1.0;
                }
                return val;
            } else {
                System.out.println("Invalid input. Enter a valid numeric value, or 0 to return.");
                scanner.nextLine();
            }
        }
    }
    protected String safeReadLine(String prompt) {
        System.out.print(prompt);
        String line = scanner.nextLine().trim();
        if (line.equals("0")) {
            return null; // abort
        }
        return line;
    }

    protected boolean validateAmount(double amount) {
        return amount > 0;
    }

    protected boolean validateAccountExists(int accountId) throws SQLException {
        String query = "SELECT accountID FROM Account WHERE accountID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    protected boolean validateAccountActive(int accountId) throws SQLException {
        String query = "SELECT status FROM Account WHERE accountID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return "ACTIVE".equals(rs.getString("status"));
                }
                return false;
            }
        }
    }

    protected double getAccountBalance(int accountId) throws SQLException {
        String query = "SELECT balance FROM Account WHERE accountID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("balance");
                }
                throw new SQLException("Account not found.");
            }
        }
    }

    protected void updateAccountBalance(int accountId, double newBalance) throws SQLException {
        String query = "UPDATE Account SET balance = ? WHERE accountID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDouble(1, newBalance);
            stmt.setInt(2, accountId);
            stmt.executeUpdate();
        }
    }

    protected void recordTransaction(String type, double amount, String description, 
                                      Integer fromAccount, Integer toAccount) throws SQLException {
        String query = "INSERT INTO Transaction (type, amount, description, status, from_account, to_account) " +
                        "VALUES (?, ?, ?, 'COMPLETED', ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, type);
            stmt.setDouble(2, amount);
            stmt.setString(3, description);
            if (fromAccount == null) {
                stmt.setNull(4, Types.INTEGER);
            } else {
                stmt.setInt(4, fromAccount);
            }
            if (toAccount == null) {
                stmt.setNull(5, Types.INTEGER);
            } else {
                stmt.setInt(5, toAccount);
            }
            stmt.executeUpdate();
        }
    }

    protected void displayCustomerAccounts(int customerId) throws SQLException {
        String query = "SELECT a.accountID, a.balance, " +
                       "CASE " +
                       "  WHEN i.accountID IS NOT NULL THEN 'Investment' " +
                       "  WHEN s.accountID IS NOT NULL THEN 'Savings' " +
                       "  WHEN c.accountID IS NOT NULL THEN 'Checking' " +
                       "END as account_type " +
                       "FROM Account a " +
                       "JOIN CustomerAccount ca ON a.accountID = ca.accountID " +
                       "LEFT JOIN Investment i ON a.accountID = i.accountID " +
                       "LEFT JOIN Savings s ON a.accountID = s.accountID " +
                       "LEFT JOIN Checking c ON a.accountID = c.accountID " +
                       "WHERE ca.customerID = ? AND a.status = 'ACTIVE' " +
                       "ORDER BY a.accountID";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<String> headers = new ArrayList<>();
                headers.add("ID");
                headers.add("Type");
                headers.add("Balance");

                List<String> columns = new ArrayList<>();
                columns.add("accountID");
                columns.add("account_type");
                columns.add("balance");

                System.out.println("\nYour Active Accounts (Enter 0 at any prompt to return):");
                printInPages(rs, headers, columns, 20);
            }
        }
    }

    protected double calculateMonthlyPayment(double principal, double annualInterestRate, int termMonths) {
        double monthlyRate = annualInterestRate / 12.0;
        if (monthlyRate == 0) {
            // no interest case:
            return principal / termMonths;
        }
        double r = monthlyRate;
        double n = termMonths;
        double numerator = r * Math.pow(1+r, n);
        double denominator = Math.pow(1+r, n) - 1;
        double m = principal * (numerator/denominator);
        return m;
    }

    protected void recalculateLoanMonthlyPayment(int loanId) throws SQLException {
        String q = "SELECT amount, interest_rate, term_months, remaining_balance FROM Loans WHERE loanID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(q)) {
            stmt.setInt(1, loanId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double remaining_balance = rs.getDouble("remaining_balance");
                    double interest_rate = rs.getDouble("interest_rate");
                    int term_months = rs.getInt("term_months");

                    double newMonthlyPayment = calculateMonthlyPayment(remaining_balance, interest_rate, term_months);

                    String uq = "UPDATE Loans SET monthly_payment = ? WHERE loanID = ?";
                    try (PreparedStatement ustmt = connection.prepareStatement(uq)) {
                        ustmt.setDouble(1, newMonthlyPayment);
                        ustmt.setInt(2, loanId);
                        ustmt.executeUpdate();
                    }
                }
            }
        }
    }

    protected boolean validateCustomerOwnsAccount(int accountId, int custId) throws SQLException {
        String query = "SELECT 1 FROM CustomerAccount WHERE customerID = ? AND accountID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, custId);
            stmt.setInt(2, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    protected boolean isInvestmentAccount(int accountId) throws SQLException {
        String q = "SELECT 1 FROM Investment WHERE accountID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(q)) {
            stmt.setInt(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    protected void printInPages(ResultSet rs, List<String> headers, List<String> columns, int pageSize) throws SQLException {
        List<List<String>> rows = new ArrayList<>();
        int colCount = columns.size();
        while (rs.next()) {
            List<String> row = new ArrayList<>();
            for (int i = 0; i < colCount; i++) {
                String col = columns.get(i);
                Object val = rs.getObject(col);
                row.add(val == null ? "NULL" : val.toString());
            }
            rows.add(row);
        }

        if (rows.isEmpty()) {
            System.out.println("(No records found.)");
            return;
        }

        for (String h : headers) {
            System.out.printf("%-15s", h);
        }
        System.out.println();
        for (int i=0; i<colCount*15; i++) System.out.print("-");
        System.out.println();

        int total = rows.size();
        int start = 0;

        while (start < total) {
            int end = Math.min(start + pageSize, total);
            for (int i = start; i < end; i++) {
                for (int c = 0; c < colCount; c++) {
                    System.out.printf("%-15s", rows.get(i).get(c));
                }
                System.out.println();
            }
            start = end;
            if (start < total) {
                System.out.println("Press ENTER to see next " + pageSize + " rows, or type 0 to stop listing.");
                String input = scanner.nextLine().trim();
                if ("0".equals(input)) {
                    break;
                }
            }
        }
    }

    protected boolean checkAbortInt(int val) {
        if (val == -1) {
            System.out.println("Returning to previous menu...");
            return true;
        }
        return false;
    }

    protected boolean checkAbortDouble(double val) {
        if (val == -1.0) {
            System.out.println("Returning to previous menu...");
            return true;
        }
        return false;
    }

    protected boolean checkAbortLine(String line) {
        if (line == null) {
            System.out.println("Returning to previous menu...");
            return true;
        }
        return false;
    }

    public abstract void start();
    protected abstract void displayMenu();
    protected abstract void processOption(int option);
}
