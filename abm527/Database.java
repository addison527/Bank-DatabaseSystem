import java.io.Console;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Database {
    private static final String DB_URL = "jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        Console console = System.console();
        if (console == null) {
            System.out.println("No console available. Password input will not be hidden.");
        }

        System.out.print("Enter Oracle user id: ");
        String userId = scanner.nextLine();

        String password = getPassword(console, userId, scanner);

        try (Connection connection = DriverManager.getConnection(DB_URL, userId, password)) {
            DatabaseMetaData metaData = connection.getMetaData();
            System.out.println("\nConnected to database:");
            System.out.println("URL: " + metaData.getURL());
            System.out.println("Username: " + metaData.getUserName());
            System.out.println("Database: " + metaData.getDatabaseProductName());

            System.out.println("\nWelcome to LUBank!");

            while (true) {
                System.out.println("\nMain Menu - Please select your role:");
                System.out.println("1. Customer (Access your accounts, make payments, etc.)");
                System.out.println("2. Bank Employee (View totals, reports, etc.)");
                System.out.println("3. Exit the application");
                //System.out.println("Or Enter 0 at ANY prompt to return.");

                int roleChoice = safeReadInt(scanner, "Enter your choice: ");
                if (roleChoice == -1) { // user entered 0 at main menu choice
                    continue;
                }

                if (roleChoice == 3) {
                    System.out.println("Thank you for using LUBank. Goodbye!");
                    break;
                }

                switch (roleChoice) {
                    case 1:
                        handleCustomerLogin(connection, scanner);
                        break;
                    case 2:
                        // Bank employee interface (no customer name needed)
                        BankInterface bmi = new BankManagementInterface(connection, 0, null);
                        bmi.start();
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }

    private static String getPassword(Console console, String uid, Scanner s) {
        if (console != null) {
            char[] passArr = console.readPassword("Enter Oracle password for %s: ", uid);
            return new String(passArr);
        } else {
            System.out.print("Enter Oracle password for " + uid + ": ");
            return s.nextLine();
        }
    }

    private static void handleCustomerLogin(Connection connection, Scanner scanner) {
        printAllCustomersPaginated(connection);

        while (true) {
            int customerId = safeReadInt(scanner, "\nEnter customer ID (or 0 to go back to main menu): ");
            if (customerId == -1) {
                // user entered 0 here means return to main menu
                return;
            }

            if (customerId == 0) {
                // same as above, just return
                return;
            }

            if (!validateCustomer(connection, customerId)) {
                System.out.println("No customer found with ID: " + customerId);
                System.out.println("Please choose a valid ID from the list above, or 0 to return.");
                continue;
            }

            String customerName = greetCustomer(connection, customerId);

            while (true) {
                System.out.println("\nCustomer Interface Menu for " + customerName + ":");
                System.out.println("1. Deposit/Withdrawal (Manage funds in your accounts)");
                System.out.println("2. Make Purchase (Use your cards for transactions)");
                System.out.println("3. Make Payment (Pay down loans or credit card balances)");
                System.out.println("4. Open a New Account (Checking, Savings, Investment)");
                System.out.println("5. Obtain a New or Replacement Card (Debit or Credit)");
                System.out.println("6. Take out a New Loan (Unsecured or Mortgage)");
                System.out.print("7. Return to Main Menu");
                System.out.println(" Or Enter 0 at ANY prompt to return.");

                int choice = safeReadInt(scanner, "Enter your choice: ");
                if (choice == -1) {
                    // user entered 0 at this prompt
                    // just break to go back to main menu
                    break;
                }

                if (choice == 7 || choice == 0) {
                    break;
                }

                BankInterface bankInterface = null;
                switch (choice) {
                    case 1:
                        bankInterface = new DepositWithdrawalInterface(connection, customerId, customerName);
                        break;
                    case 2:
                        bankInterface = new PurchaseInterface(connection, customerId, customerName);
                        break;
                    case 3:
                        bankInterface = new PaymentInterface(connection, customerId, customerName);
                        break;
                    case 4:
                        bankInterface = new NewAccountInterface(connection, customerId, customerName);
                        break;
                    case 5:
                        bankInterface = new NewCardInterface(connection, customerId, customerName);
                        break;
                    case 6:
                        bankInterface = new NewLoanInterface(connection, customerId, customerName);
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }

                if (bankInterface != null) {
                    bankInterface.start();
                }
            }

            break;
        }
    }

    private static boolean validateCustomer(Connection connection, int customerId) {
        String query = "SELECT customerID FROM Customer WHERE customerID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error validating customer: " + e.getMessage());
            return false;
        }
    }

    private static String greetCustomer(Connection connection, int customerId) {
        String q = "SELECT name, dob, phone, address FROM Customer WHERE customerID = ?";
        String cName = "Customer";
        try (PreparedStatement stmt = connection.prepareStatement(q)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    cName = name;
                    Date dob = rs.getDate("dob");
                    String phone = rs.getString("phone");
                    String address = rs.getString("address");

                    System.out.println("\nWELCOME " + name.toUpperCase() + "!");
                    System.out.println("DOB: " + dob);
                    System.out.println("Phone: " + phone);
                    System.out.println("Address: " + address);

                    printCustomerAccountsSummary(connection, customerId);
                    printCustomerCardsSummary(connection, customerId);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error greeting customer: " + e.getMessage());
        }
        return cName;
    }

    private static void printCustomerAccountsSummary(Connection connection, int customerId) throws SQLException {
        String q = "SELECT a.accountID, a.balance, " +
                   "CASE WHEN i.accountID IS NOT NULL THEN 'Investment' " +
                   "     WHEN s.accountID IS NOT NULL THEN 'Savings' " +
                   "     WHEN c.accountID IS NOT NULL THEN 'Checking' END as account_type " +
                   "FROM Account a " +
                   "JOIN CustomerAccount ca ON a.accountID = ca.accountID " +
                   "LEFT JOIN Investment i ON a.accountID = i.accountID " +
                   "LEFT JOIN Savings s ON a.accountID = s.accountID " +
                   "LEFT JOIN Checking c ON a.accountID = c.accountID " +
                   "WHERE ca.customerID = ? AND a.status = 'ACTIVE' " +
                   "ORDER BY a.accountID";

        try (PreparedStatement stmt = connection.prepareStatement(q)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("\nActive Accounts Summary:");
                System.out.println("ID\tType\tBalance");
                System.out.println("------------------------------------");
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    System.out.printf("%d\t%-8s\t$%.2f%n",
                                      rs.getInt("accountID"),
                                      rs.getString("account_type"),
                                      rs.getDouble("balance"));
                }
                if (!found) {
                    System.out.println("(No active accounts)");
                }
            }
        }
    }

    private static void printCustomerCardsSummary(Connection connection, int customerId) throws SQLException {
        // Debit Cards
        String dq = "SELECT c.card_number, a.balance " +
                    "FROM Cards c " +
                    "JOIN Debit d ON c.card_number = d.card_number " +
                    "JOIN Checking ch ON d.checking_account = ch.accountID " +
                    "JOIN Account a ON ch.accountID = a.accountID " +
                    "WHERE c.customerID = ? AND c.status = 'ACTIVE' " +
                    "ORDER BY c.card_number";
        try (PreparedStatement stmt = connection.prepareStatement(dq)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("\nActive Debit Cards:");
                System.out.println("CardNumber\tBalance");
                System.out.println("---------------------------");
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    String cardNum = rs.getString("card_number");
                    double bal = rs.getDouble("balance");
                    System.out.println(cardNum + "\t$" + bal);
                }
                if (!found) {
                    System.out.println("(No active debit cards)");
                }
            }
        }

        String cq = "SELECT c.card_number, cr.credit_limit, cr.current_balance " +
                    "FROM Cards c " +
                    "JOIN Credit cr ON c.card_number = cr.card_number " +
                    "WHERE c.customerID = ? AND c.status = 'ACTIVE' " +
                    "ORDER BY c.card_number";
        try (PreparedStatement stmt = connection.prepareStatement(cq)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("\nActive Credit Cards:");
                System.out.println("CardNumber\tCreditLimit\tAvailableCredit");
                System.out.println("----------------------------------------------");
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    String cardNum = rs.getString("card_number");
                    double limit = rs.getDouble("credit_limit");
                    double currBal = rs.getDouble("current_balance");
                    double available = limit - currBal;
                    System.out.printf("%s\t$%.2f\t$%.2f%n", cardNum, limit, available);
                }
                if (!found) {
                    System.out.println("(No active credit cards)");
                }
            }
        }
    }

    private static void printAllCustomersPaginated(Connection connection) {
        String q = "SELECT customerID, name FROM Customer ORDER BY customerID";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(q)) {

            System.out.println("\nAvailable Customers (Paginated):");

            List<String> headers = new ArrayList<>();
            headers.add("ID");
            headers.add("Name");

            List<String> columns = new ArrayList<>();
            columns.add("customerID");
            columns.add("name");

            // Create a dummy interface for pagination:
            BankInterface dummy = new BankInterface(connection,0,null) {
                @Override public void start() {}
                @Override protected void displayMenu() {}
                @Override protected void processOption(int option) {}
            };

            dummy.printInPages(rs, headers, columns, 20);

        } catch (SQLException e) {
            System.out.println("Error retrieving customers: " + e.getMessage());
        }
    }

    // Modified safeReadInt to handle 0 as abort by returning -1
    private static int safeReadInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) {
                int val = scanner.nextInt();
                scanner.nextLine();
                if (val == 0) {
                    return -1; // abort signal
                }
                return val;
            } else {
                System.out.println("Invalid input. Please enter a valid integer or 0 to return.");
                scanner.nextLine();
            }
        }
    }
}
