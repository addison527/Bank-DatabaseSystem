import java.sql.*;

public class BankManagementInterface extends BankInterface {

    public BankManagementInterface(Connection connection, int customerId, String customerName) {
        super(connection, customerId, customerName);
    }

    @Override
    public void start() {
        try {
            while (true) {
                displayMenu();
                int option = safeReadInt("Enter your choice: ");
                if (checkAbortInt(option)) return;

                if (option == 5) {
                    System.out.println("Returning to main menu...");
                    break;
                }

                processOption(option);
            }
        } catch (Exception e) {
            System.out.println("Error in bank management interface: " + e.getMessage());
        }
    }

    @Override
    protected void displayMenu() {
        System.out.println("\nBank Management Menu (Employee Interface):");
        System.out.println("1. Compute total loans outstanding");
        System.out.println("2. Compute total balances in accounts");
        System.out.println("3. Transaction frequency and volume report (choose timeframe)");
        System.out.println("4. Totals per customer (top 10)");
        System.out.println("5. Return to Main Menu");
        System.out.println("Enter 0 at any prompt to return.");
    }

    @Override
    protected void processOption(int option) {
        try {
            switch (option) {
                case 1:
                    totalLoans();
                    break;
                case 2:
                    totalAccounts();
                    break;
                case 3:
                    transactionReport();
                    break;
                case 4:
                    totalsPerCustomer();
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        } catch (SQLException e) {
            System.out.println("Error processing option: " + e.getMessage());
        }
    }

    private void totalLoans() throws SQLException {
        String q = "SELECT SUM(remaining_balance) as total_outstanding FROM Loans WHERE status = 'ACTIVE'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(q)) {
            if (rs.next()) {
                double total = rs.getDouble("total_outstanding");
                System.out.printf("Total Outstanding Loans: $%.2f%n", total);
            }
        }
    }

    private void totalAccounts() throws SQLException {
        String q = "SELECT SUM(balance) as total_deposits FROM Account WHERE status = 'ACTIVE'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(q)) {
            if (rs.next()) {
                double total = rs.getDouble("total_deposits");
                System.out.printf("Total Deposits in Active Accounts: $%.2f%n", total);
            }
        }
    }

    private void transactionReport() throws SQLException {
        System.out.println("\nChoose timeframe for transaction report:");
        System.out.println("1. Hourly");
        System.out.println("2. Daily");
        System.out.println("3. Weekly");
        System.out.println("4. Monthly");
        System.out.println("0. Return to previous menu");
        int choice = safeReadInt("Enter your choice: ");
        if (checkAbortInt(choice)) return;

        String truncFormat;
        switch (choice) {
            case 1:
                truncFormat = "HH"; // hourly
                break;
            case 2:
                truncFormat = ""; // daily default (no format = TRUNC(date))
                break;
            case 3:
                truncFormat = "IW"; // weekly
                break;
            case 4:
                truncFormat = "MM"; // monthly
                break;
            case 0:
                System.out.println("Returning to previous menu...");
                return;
            default:
                System.out.println("Invalid option. Returning to previous menu...");
                return;
        }

        String groupExp;
        if (truncFormat.equals("")) {
            groupExp = "TRUNC(transaction_date)";
        } else {
            groupExp = "TRUNC(transaction_date,'" + truncFormat + "')";
        }

        String q = "SELECT " + groupExp + " as tgrp, COUNT(*) as cnt, SUM(amount) as sum_amt " +
                   "FROM Transaction " +
                   "GROUP BY " + groupExp + " " +
                   "ORDER BY " + groupExp + " DESC FETCH FIRST 10 ROWS ONLY";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(q)) {
            System.out.println("TimeGroup\t\tCount\tTotal Amount");
            System.out.println("--------------------------------------------");
            while (rs.next()) {
                System.out.printf("%s\t%d\t$%.2f%n",
                    rs.getTimestamp("tgrp"),
                    rs.getInt("cnt"),
                    rs.getDouble("sum_amt"));
            }
        }
    }

    private void totalsPerCustomer() throws SQLException {
        String q = "SELECT c.customerID, c.name, SUM(a.balance) as total_balance " +
                   "FROM Customer c " +
                   "JOIN CustomerAccount ca ON c.customerID = ca.customerID " +
                   "JOIN Account a ON ca.accountID = a.accountID " +
                   "WHERE a.status = 'ACTIVE' " +
                   "GROUP BY c.customerID, c.name " +
                   "ORDER BY total_balance DESC FETCH FIRST 10 ROWS ONLY";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(q)) {
            System.out.println("CustomerID\tName\t\tTotal Balance");
            System.out.println("--------------------------------------------");
            while (rs.next()) {
                System.out.printf("%d\t\t%-15s\t$%.2f%n",
                    rs.getInt("customerID"),
                    rs.getString("name"),
                    rs.getDouble("total_balance"));
            }
        }
    }
}
