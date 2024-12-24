import java.sql.*;
import java.util.Random;

public class NewCardInterface extends BankInterface {

    public NewCardInterface(Connection connection, int customerId, String customerName) {
        super(connection, customerId, customerName);
    }

    @Override
    public void start() {
        while (true) {
            displayMenu();
            int option = safeReadInt("Enter your choice: ");
            if (option == 3) {
                System.out.println("Returning to Customer Menu...");
                break;
            }

            processOption(option);
        }
    }

    @Override
    protected void displayMenu() {
        System.out.println("\nNew Card Menu for " + customerName + ":");
        System.out.println("1. Obtain New Debit Card (Link to your checking account)");
        System.out.println("2. Obtain New Credit Card (Specify credit limit, etc.)");
        System.out.println("3. Return to Customer Menu");
    }

    @Override
    protected void processOption(int option) {
        try {
            switch (option) {
                case 1:
                    newDebitCard();
                    break;
                case 2:
                    newCreditCard();
                    break;
                default:
                    System.out.println("Invalid option. Please enter a valid number.");
            }
        } catch (SQLException e) {
            System.out.println("Error obtaining new card: " + e.getMessage());
            System.out.println("Try valid accounts and amounts.");
        }
    }

    private void newDebitCard() throws SQLException {
        System.out.println("You must link a debit card to an existing checking account you own.");
        System.out.println("Check your active accounts in Deposit/Withdrawal menu if unsure.");
        int accountId = safeReadInt("Enter checking account ID: ");
        if (!validateCustomerOwnsAccount(accountId, customerId) || !isCheckingAccount(accountId)) {
            System.out.println("Invalid checking account ID. Ensure it’s a checking account you own.");
            return;
        }

        String cardNumber = generateCardNumber();
        String cvv = generateCVV();
        connection.setAutoCommit(false);
        try {
            insertCard(cardNumber, customerId, cvv);
            insertDebit(cardNumber, accountId);
            connection.commit();
            System.out.println("New debit card issued: " + cardNumber);
            System.out.println("Linked to checking account: " + accountId);
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private void newCreditCard() throws SQLException {
        double creditLimit = safeReadDouble("Enter desired credit limit: $");
        if (!validateAmount(creditLimit)) {
            System.out.println("Invalid credit limit.");
            return;
        }

        double interestRate = safeReadDouble("Enter interest rate (e.g. 0.02 for 2%): ");
        if (interestRate <= 0) {
            System.out.println("Interest rate must be positive.");
            return;
        }

        String cardNumber = generateCardNumber();
        String cvv = generateCVV();
        connection.setAutoCommit(false);
        try {
            insertCard(cardNumber, customerId, cvv);
            insertCredit(cardNumber, creditLimit, interestRate);
            connection.commit();
            System.out.println("New credit card issued: " + cardNumber);
            System.out.printf("Credit Limit: $%.2f, Interest Rate: %.2f%%%n", creditLimit, interestRate*100);
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
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

    private boolean isCheckingAccount(int accountId) throws SQLException {
        String q = "SELECT 1 FROM Checking WHERE accountID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(q)) {
            stmt.setInt(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void insertCard(String cardNumber, int custId, String cvv) throws SQLException {
        String insertCard = "INSERT INTO Cards (card_number, customerID, expiry_date, cvv, status) " +
                             "VALUES (?, ?, ADD_MONTHS(SYSDATE, 48), ?, 'ACTIVE')";
        try (PreparedStatement stmt = connection.prepareStatement(insertCard)) {
            stmt.setString(1, cardNumber);
            stmt.setInt(2, custId);
            stmt.setString(3, cvv);
            stmt.executeUpdate();
        }
    }

    private void insertDebit(String cardNumber, int accountId) throws SQLException {
        String q = "INSERT INTO Debit (card_number, checking_account) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(q)) {
            stmt.setString(1, cardNumber);
            stmt.setInt(2, accountId);
            stmt.executeUpdate();
        }
    }

    private void insertCredit(String cardNumber, double creditLimit, double interestRate) throws SQLException {
        String q = "INSERT INTO Credit (card_number, credit_limit, current_balance, due_balance, payment_due_date, interest_rate) " +
                   "VALUES (?, ?, 0, 0, ADD_MONTHS(SYSDATE, 1), ?)";
        try (PreparedStatement stmt = connection.prepareStatement(q)) {
            stmt.setString(1, cardNumber);
            stmt.setDouble(2, creditLimit);
            stmt.setDouble(3, interestRate);
            stmt.executeUpdate();
        }
    }

    private String generateCardNumber() {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<16; i++) {
            sb.append(r.nextInt(10));
        }
        return sb.toString();
    }

    private String generateCVV() {
        Random r = new Random();
        int cvvNum = 100 + r.nextInt(900);
        return String.valueOf(cvvNum);
    }
}
