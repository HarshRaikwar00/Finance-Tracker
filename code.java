import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/*
==============================================================================
PERSONAL FINANCE TRACKER – Simplified Version (Matches Steps 1–5)
------------------------------------------------------------------------------
Step 1: System Design → Model real-world system
Step 2: Inheritance & Polymorphism → Transaction, Income, Expense
Step 3: Encapsulation → Private data, controlled access
Step 4: Abstraction → Report generation hides complexity
Step 5: User Interface → Command-line menu
==============================================================================
*/

public class FinanceTracker {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Account account = new Account();
        ReportGenerator reportGen = new ReportGenerator();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        while (true) {
            System.out.println("\n--- Personal Finance Tracker ---");
            System.out.println("1. Add Income");
            System.out.println("2. Add Expense");
            System.out.println("3. View Report");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            if (!sc.hasNextInt()) {
                System.out.println("❌ Invalid input! Please enter a number between 1 and 4.");
                sc.nextLine();
                continue;
            }

            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1 -> { // Add Income
                    double incAmt = readPositiveDouble(sc, "Enter income amount: ");
                    System.out.print("Enter category: ");
                    String incCat = sc.nextLine().trim();
                    LocalDate incDate = readDate(sc, "Enter date (DD/MM/YYYY): ", formatter);
                    account.addTransaction(new Income(incAmt, incCat, incDate));
                    System.out.println("✅ Income added successfully!");
                }
                case 2 -> { // Add Expense
                    double expAmt = readPositiveDouble(sc, "Enter expense amount: ");
                    System.out.print("Enter category: ");
                    String expCat = sc.nextLine().trim();
                    LocalDate expDate = readDate(sc, "Enter date (DD/MM/YYYY): ", formatter);
                    account.addTransaction(new Expense(expAmt, expCat, expDate));
                    System.out.println("✅ Expense added successfully!");
                }
                case 3 -> reportGen.generateReport(account);
                case 4 -> {
                    System.out.println("👋 Exiting... Thank you for using the Finance Tracker!");
                    sc.close();
                    System.exit(0);
                }
                default -> System.out.println("❌ Invalid choice! Please enter 1–4.");
            }
        }
    }

    // Helper: read positive double with validation
    private static double readPositiveDouble(Scanner sc, String prompt) {
        double value;
        while (true) {
            System.out.print(prompt);
            if (sc.hasNextDouble()) {
                value = sc.nextDouble();
                sc.nextLine();
                if (value <= 0) {
                    System.out.println("❌ Amount must be positive.");
                    continue;
                }
                return value;
            } else {
                System.out.println("❌ Invalid input! Please enter a numeric value.");
                sc.nextLine();
            }
        }
    }

    // Helper: read date in DD/MM/YYYY format
    private static LocalDate readDate(Scanner sc, String prompt, DateTimeFormatter formatter) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                return LocalDate.parse(input, formatter);
            } catch (Exception e) {
                System.out.println("❌ Invalid date format! Use DD/MM/YYYY.");
            }
        }
    }
}

/* ==========================================================================
   Step 2: Class Hierarchy (Inheritance & Polymorphism)
   Base class: Transaction
   Derived classes: Income, Expense
   ========================================================================== */
abstract class Transaction {
    protected double amount;
    protected String category;
    protected LocalDate date;

    public Transaction(double amount, String category, LocalDate date) {
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public LocalDate getDate() { return date; }

    public abstract void apply(Account account);  // Polymorphic behavior
}

class Income extends Transaction {
    public Income(double amount, String category, LocalDate date) {
        super(amount, category, date);
    }

    @Override
    public void apply(Account account) {
        account.updateBalance(amount);
    }
}

class Expense extends Transaction {
    public Expense(double amount, String category, LocalDate date) {
        super(amount, category, date);
    }

    @Override
    public void apply(Account account) {
        account.updateBalance(-amount);
    }
}

/* ==========================================================================
   Step 3: Transaction Management (Encapsulation)
   Account class encapsulates balance and transaction history
   ========================================================================== */
class Account {
    private double balance;
    private List<Transaction> transactions;

    public Account() {
        this.balance = 0.0;
        this.transactions = new ArrayList<>();
    }

    public void addTransaction(Transaction t) {
        t.apply(this);
        transactions.add(t);
    }

    protected void updateBalance(double amount) {
        this.balance += amount;
    }

    public double getBalance() { return balance; }
    public List<Transaction> getTransactions() { return transactions; }
}

/* ==========================================================================
   Step 4: Report Generation (Abstraction & Polymorphism)
   Generates simple financial report: income, expense, and net total
   ========================================================================== */
class ReportGenerator {
    public void generateReport(Account account) {
        List<Transaction> transactions = account.getTransactions();

        if (transactions.isEmpty()) {
            System.out.println("\nNo transactions available to report.");
            return;
        }

        double totalIncome = 0.0;
        double totalExpense = 0.0;

        System.out.println("\n--- Transaction Report ---");
        for (Transaction t : transactions) {
            String type = (t instanceof Income) ? "Income" : "Expense";
            System.out.printf("%-10s | %-8s | %-10s | $%.2f\n",
                    t.getDate(), type, t.getCategory(), t.getAmount());

            if (t instanceof Income)
                totalIncome += t.getAmount();
            else
                totalExpense += t.getAmount();
        }

        double net = totalIncome - totalExpense;
        System.out.println("----------------------------");
        System.out.printf("Total Income : $%.2f\n", totalIncome);
        System.out.printf("Total Expense: $%.2f\n", totalExpense);
        System.out.printf("Net Savings  : $%.2f\n", net);
        System.out.println("----------------------------");
    }
}