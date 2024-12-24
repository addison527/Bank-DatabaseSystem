import java.sql.*;

public class NewLoanInterface extends BankInterface {
    public NewLoanInterface(Connection connection, int customerId, String customerName) {
        super(connection, customerId, customerName);
    }

    @Override
    public void start() {
        while (true) {
            displayMenu();
            int option = safeReadInt("Enter your choice: ");
            if (checkAbortInt(option)) {
                return; // abort
            }
            if (option == 3) {
                System.out.println("Returning to Customer Menu...");
                break;
            }

            processOption(option);
        }
    }

    @Override
    protected void displayMenu() {
        System.out.println("\nNew Loan Menu for " + customerName + ":");
        System.out.println("1. Unsecured Loan (No collateral, based on credit score)");
        System.out.println("2. Mortgage Loan (Secured by property)");
        System.out.println("3. Return to Customer Menu");
        System.out.println("Enter 0 at any prompt to return at any step.");
    }

    @Override
    protected void processOption(int option) {
        try {
            switch (option) {
                case 1:
                    createUnsecuredLoan();
                    break;
                case 2:
                    createMortgageLoan();
                    break;
                default:
                    System.out.println("Invalid option. Please choose a valid number.");
            }
        } catch (SQLException e) {
            System.out.println("Error creating loan: " + e.getMessage());
            System.out.println("Check your inputs and try again.");
        }
    }

    private void createUnsecuredLoan() throws SQLException {
        double amount = safeReadDouble("Enter loan amount: $");
        if (checkAbortDouble(amount)) return;

        double interestRate = safeReadDouble("Enter interest rate (e.g. 0.05 for 5%): ");
        if (checkAbortDouble(interestRate)) return;

        int termMonths = safeReadInt("Enter term (months): ");
        if (checkAbortInt(termMonths)) return;

        int creditScore = safeReadInt("Enter your credit score: ");
        if (checkAbortInt(creditScore)) return;

        // compute monthlyPayment
        double monthlyPayment = calculateMonthlyPayment(amount, interestRate, termMonths);

        connection.setAutoCommit(false);
        try {
            int loanId = insertLoan(amount, interestRate, termMonths, monthlyPayment, amount);
            insertUnsecured(loanId, creditScore);
            connection.commit();
            System.out.println("Unsecured loan application created. Loan ID: " + loanId);
            System.out.printf("Amount: $%.2f, Interest: %.2f%%, Term: %d months, Monthly: $%.2f, Credit Score: %d%n",
                              amount, interestRate*100, termMonths, monthlyPayment, creditScore);
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private void createMortgageLoan() throws SQLException {
        double amount = safeReadDouble("Enter loan amount: $");
        if (checkAbortDouble(amount)) return;

        double interestRate = safeReadDouble("Enter interest rate (e.g. 0.04 for 4%): ");
        if (checkAbortDouble(interestRate)) return;

        int termMonths = safeReadInt("Enter term (months): ");
        if (checkAbortInt(termMonths)) return;

        // monthlyPayment no longer asked from user
        double monthlyPayment = calculateMonthlyPayment(amount, interestRate, termMonths);

        String propertyAddress = safeReadLine("Enter property address: ");
        if (checkAbortLine(propertyAddress)) return;

        double propertyValue = safeReadDouble("Enter property value: $");
        if (checkAbortDouble(propertyValue)) return;

        double ltvRatio = amount / propertyValue;

        connection.setAutoCommit(false);
        try {
            int loanId = insertLoan(amount, interestRate, termMonths, monthlyPayment, amount);
            insertMortgage(loanId, propertyAddress, propertyValue, ltvRatio);
            connection.commit();
            System.out.println("Mortgage loan application created. Loan ID: " + loanId);
            System.out.printf("Amount: $%.2f, Interest: %.2f%%, Term: %d months, Monthly: $%.2f%n",
                              amount, interestRate*100, termMonths, monthlyPayment);
            System.out.printf("Property: %s, Value: $%.2f, LTV Ratio: %.2f%n",
                              propertyAddress, propertyValue, ltvRatio);
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private int insertLoan(double amount, double interestRate, int termMonths, double monthlyPayment, double remainingBalance) throws SQLException {
        String q = "INSERT INTO Loans (customerID, amount, interest_rate, start_date, term_months, monthly_payment, remaining_balance, status, branchID) " +
                   "VALUES (?, ?, ?, SYSDATE, ?, ?, ?, 'PENDING', 1)";

        try (PreparedStatement stmt = connection.prepareStatement(q, new String[]{"loanID"})) {
            stmt.setInt(1, customerId);
            stmt.setDouble(2, amount);
            stmt.setDouble(3, interestRate);
            stmt.setInt(4, termMonths);
            stmt.setDouble(5, monthlyPayment);
            stmt.setDouble(6, remainingBalance);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    return fetchLatestLoanID();
                }
            }
        }
    }

    private int fetchLatestLoanID() throws SQLException {
        String q = "SELECT loan_seq.CURRVAL FROM dual";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(q)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("Could not fetch newly created loan ID");
        }
    }

    private void insertUnsecured(int loanId, int creditScore) throws SQLException {
        String q = "INSERT INTO Unsecured (loanID, credit_score) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(q)) {
            stmt.setInt(1, loanId);
            stmt.setInt(2, creditScore);
            stmt.executeUpdate();
        }
    }

    private void insertMortgage(int loanId, String propertyAddress, double propertyValue, double ltvRatio) throws SQLException {
        String q = "INSERT INTO Mortgages (loanID, property_address, property_value, ltv_ratio) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(q)) {
            stmt.setInt(1, loanId);
            stmt.setString(2, propertyAddress);
            stmt.setDouble(3, propertyValue);
            stmt.setDouble(4, ltvRatio);
            stmt.executeUpdate();
        }
    }
}
