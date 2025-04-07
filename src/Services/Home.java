package Services;

import MainPackage.Main;
import Utils.DataBaseManager;
import Utils.LoggedInUser;
import com.mysql.cj.x.protobuf.MysqlxPrepare;

import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;
public class Home {
    public static Connection conn = DataBaseManager.ConnectToDataBase();
    public static String debitCardNumber = LoggedInUser.getDebitCardNumber();
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    private static void printUserInfo() {
        String name = LoggedInUser.getName();
        String email = LoggedInUser.getEmail();
        int age = LoggedInUser.getAge();
        long mobileNumber = LoggedInUser.getMobileNumber();

        System.out.println("======================================");
        System.out.println("         👤 Logged In User Info       ");
        System.out.println("======================================");
        System.out.printf("Name             : %s\n", name);
        System.out.printf("Age              : %d\n", age);
        System.out.printf("Email            : %s\n", email);
        System.out.printf("Mobile Number    : %d\n", mobileNumber);
        System.out.printf("Debit Card Number: %s\n", debitCardNumber);
        System.out.println("======================================");
    }
    private static void getBalance(){
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT balance FROM users WHERE debit_card_number = ?");
            pstmt.setString(1, LoggedInUser.getDebitCardNumber());
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                double balance = rs.getDouble("balance");
                System.out.println("Your Current Balance in Rupees is : " + balance);
                pause();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private static void pause() {
        System.out.println("\nPress Enter to continue...");
        try {
            System.in.read();  // waits for the user to press Enter
        } catch (Exception e) {
            // ignored
        }
    }
    private static boolean checkDebitCardNumber(String card){
        if(card.length() != AuthServices.length)return false;
        for(int i = 0;i<card.length();i++){
            if(card.charAt(i)<'0' || card.charAt(i)>'9')return false;
        }
        return true;
    }
    private static void sendMoney(){
        // first we will be taking all the inputs needed
        // we need the input of other persons debit card number
        // we need to see the amount
        // then we will take input of pin and match it max 3 times
        String card;
        Scanner sc = new Scanner(System.in);
        while (true) {
            clearScreen();
            System.out.print("Enter Reciver's Debit Card Number : ");
            card = sc.nextLine();
            if (!checkDebitCardNumber(card)) {
                System.out.println("⚠️  Invalid Card Number! It should be 16 digits.");
                pause();
            }
            if(card.equals(LoggedInUser.getDebitCardNumber())){
                System.out.println("You cant Send Money to Yourselve .");
                pause();
            }
            else break;
        }
        double bal = 0.0;
        double amount;
        while(true){
            clearScreen();
            System.out.print("Enter the amount that you want to send :  ");
            try {
                amount = sc.nextDouble();
                try {
                    PreparedStatement pstmt = conn.prepareStatement("SELECT balance FROM users WHERE debit_card_number = ?");
                    pstmt.setString(1,LoggedInUser.getDebitCardNumber());
                    ResultSet rs = pstmt.executeQuery();
                    if(rs.next()){
                        bal = rs.getDouble("balance");
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                if(amount<0){
                    System.out.println("⚠️ Amount can't be negative : ");
                    pause();
                }
                else if(amount>bal){
                    System.out.println("You cant Send Money More than Balance : ");
                    pause();
                }
                else break;
            } catch (InputMismatchException e){
                System.out.println(" ⚠️ Only Numberic type data can be enterd here : ");
                pause();
            }
        }
        try {
            String pin_db = null;
            PreparedStatement pstmt = conn.prepareStatement("SELECT atm_pin FROM users WHERE debit_card_number = ?");
            pstmt.setString(1,LoggedInUser.getDebitCardNumber());
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                pin_db = rs.getString("atm_pin");
            }
            int count = 0;
            while(true){
                count++;
                clearScreen();
                String pin;
                while (true) {
                    System.out.print("Enter a 4-digit ATM PIN: ");
                    pin = sc.nextLine();

                    if (!pin.matches("\\d{4}")) {
                        System.out.println("⚠️  Invalid Input! PIN must be exactly 4 digits and numeric.");
                        pause();
                    } else {
                        break;
                    }
                }
                if(pin.equals(pin_db)){
                    // adding a transactions
                    try {
                        PreparedStatement insert_transactions = conn.prepareStatement("INSERT INTO transactions VALUES (?,?,?,?,?)");
                        insert_transactions.setString(1, LoggedInUser.getDebitCardNumber());
                        insert_transactions.setString(2, card);
                        insert_transactions.setDouble(3, bal);
                        insert_transactions.setDouble(4, bal - amount);
                        insert_transactions.setDouble(5, amount);
                        insert_transactions.executeUpdate();
                        // cahnges in user database

                        PreparedStatement update_sender = conn.prepareStatement("UPDATE users SET balance = ? WHERE debit_card_number = ?");
                        update_sender.setDouble(1, bal - amount);
                        update_sender.setString(2, LoggedInUser.getDebitCardNumber());
                        update_sender.executeUpdate();

                        // update balance in reciver side
                        // first we need to get the current recivers balance from the data base

                        PreparedStatement reciver_balance = conn.prepareStatement("SELECT balance FROM users WHERE debit_card_number = ? ");
                        reciver_balance.setString(1, card);
                        ResultSet reciver_b = reciver_balance.executeQuery();
                        double Reciver_Balance = 0.0;
                        if(reciver_b.next()){
                            Reciver_Balance = reciver_b.getDouble("balance");
                        }
                        // updating reciver side balance now
                        PreparedStatement update_reciver_balance = conn.prepareStatement("UPDATE users SET balance = ? WHERE debit_card_number = ? ");
                        update_reciver_balance.setDouble(1,Reciver_Balance+amount);
                        update_reciver_balance.setString(2,card);
                        update_reciver_balance.executeUpdate();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println(" Money Sent Successfully ");
                    pause();
                    break;
                }
                else {
                    System.out.println("The Pin Enterd Is Wrong : ");
                    pause();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private static void LogOut() {
        LoggedInUser.setName(null);
        LoggedInUser.setEmail(null);
        LoggedInUser.setAge(0);
        LoggedInUser.setMobileNumber(0L);
        LoggedInUser.setDebitCardNumber(null);
        LoggedInUser.setBalance(0.0);

        System.out.println("\n✅ You have been successfully logged out.");
        pause();
        try {
            Main.main(new String[0]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static void showTransactions(){
        try {
            PreparedStatement transactions = conn.prepareStatement("SELECT * FROM transactions WHERE sender_debit_card_number = ?");
            transactions.setString(1, LoggedInUser.getDebitCardNumber());
            ResultSet rs = transactions.executeQuery();

            System.out.println("=== Transaction History ===");
            while (rs.next()) {
                String sender = rs.getString("sender_debit_card_number");
                String receiver = rs.getString("receiver_debit_card_number");
                double before = rs.getDouble("sender_balance_before");
                double after = rs.getDouble("sender_balance_after");
                double amount = rs.getDouble("amount_sent");

                System.out.println("To: " + receiver);
                System.out.println("Sent: ₹" + amount);
                System.out.println("Balance Before: ₹" + before);
                System.out.println("Balance After: ₹" + after);
                System.out.println("----------------------------------");
            }
            pause();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void menu(){
        clearScreen();
        Scanner sc = new Scanner(System.in);
        while(true){
            System.out.println("============ Home ============");
            System.out.println("1. Enter 1 to View Balance");
            System.out.println("2. Enter 2 to View your Info");
            System.out.println("3. Enter 3 to Send Money");
            System.out.println("4. Enter 4 to Show Transactions");
            System.out.println("5. Enter 5 to Logout");
            System.out.println("===============================");
            int ch = sc.nextInt();
            switch (ch){
                case 1 :
                    Home.getBalance();
                    break;
                case 2 :
                    Home.printUserInfo();
                    break;
                case 3 :
                    Home.sendMoney();
                    break;
                case 4 :
                    Home.showTransactions();
                    break;
                case 5 :
                    Home.LogOut();
                    break;
                default :
                    System.out.println("Enter a valid Choice");
            }
        }
    }
}
