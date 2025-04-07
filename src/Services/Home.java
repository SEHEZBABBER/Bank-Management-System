package Services;

import MainPackage.Main;
import Utils.DataBaseManager;
import Utils.LoggedInUser;

import java.sql.*;
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
            System.out.println("============================");
            int ch = sc.nextInt();
            switch (ch){
                case 1 :
                    Home.getBalance();
                    break;
                case 2 :
                    Home.printUserInfo();
                    break;
                case 3 :
//                    Home.sendMoney();
                    break;
                case 4 :
//                    Home.showTransactions();
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
