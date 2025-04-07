package Services;

import Utils.DataBaseManager;
import Utils.LoggedInUser;

import java.sql.*;
import java.util.Scanner;
public class Home {
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
        Connection conn = DataBaseManager.ConnectToDataBase();
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT BALANCE FROM users WHERE DEBITCARDNUMBER = ?");
            pstmt.setString(1, debitCardNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("BALANCE");  // or rs.getInt("BALANCE") if it's stored as INT
                System.out.println("✅ Current Balance: ₹" + balance);
            } else {
                System.out.println("❌ No user found with that debit card number.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void menu(){
        clearScreen();
        Scanner sc = new Scanner(System.in);
        while(true){
            Home.printUserInfo();
            System.out.println("1. Enter 1 to View Balance");
            int ch = sc.nextInt();
            int k = 0;
            switch (ch){
                case 1 :
                    k = 1;
                    Home.getBalance();
                    break;
                default :
                    System.out.println("Enter a valid Choice");
            }
            if(k == 1)break;
        }
    }
}
