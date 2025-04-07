package Services;

import MainPackage.Main;
import Utils.DataBaseManager;
import Utils.LoggedInUser;

import java.sql.*;
import java.util.Random;
import java.util.InputMismatchException;
import java.util.Scanner;

public class AuthServices {
    public static int length = 16;
    private static void saveToLoggedInUser(String card){
        try {
            Connection conn = DataBaseManager.ConnectToDataBase();
            PreparedStatement pstmt = conn.prepareStatement("SELECT name,age,email,mobile_number,debit_card_number,balance FROM users WHERE debit_card_number = ?");
            pstmt.setString(1, card);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                LoggedInUser.setName(rs.getString("name"));
                LoggedInUser.setEmail(rs.getString("email"));
                LoggedInUser.setMobileNumber(rs.getLong("mobile_number"));
                LoggedInUser.setAge(rs.getInt("age"));
                LoggedInUser.setBalance(rs.getDouble("balance"));
                LoggedInUser.setDebitCardNumber(rs.getString("debit_card_number"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static boolean validateUser(String name,String Card,long phone){
        Scanner sc = new Scanner(System.in);
        try {
            Connection conn = DataBaseManager.ConnectToDataBase();

            // matching names and phone number as well
            PreparedStatement match_name_phone = conn.prepareStatement("SELECT name,mobile_number FROM users WHERE debit_card_number = ?");
            match_name_phone.setString(1,Card);
            ResultSet name_phone = match_name_phone.executeQuery();
            if(name_phone.next()){
                String name_db = name_phone.getString("name");
                long phone_db = name_phone.getLong("mobile_number");
                if(!name_db.equals(name))return false;
                if(phone_db != phone)return false;
            }
            else{
                return false;
            }



            PreparedStatement pstmt = conn.prepareStatement("SELECT atm_pin FROM users WHERE debit_card_number = ?");
            pstmt.setString(1, Card);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                String pin_db = rs.getString("atm_pin");
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
                System.out.println(pin.equals(pin_db));
                return pin.equals(pin_db);
            }
            return false;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    private static void InsertUserInfo(){
        Scanner sc = new Scanner(System.in);
        Connection conn = DataBaseManager.ConnectToDataBase();
        try{
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users VALUES (?,?,?,?,?,?,?)");
            pstmt.setString(1,LoggedInUser.getName());
            pstmt.setInt(2,LoggedInUser.getAge());
            pstmt.setString(3,LoggedInUser.getEmail());
            pstmt.setLong(4,LoggedInUser.getMobileNumber());
            pstmt.setString(5,LoggedInUser.getDebitCardNumber());
            pstmt.setDouble(7,LoggedInUser.getBalance());
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
            pstmt.setString(6, pin);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private static long userCount = 0;
    private static boolean checkValidName(String name) {
        for (int i = 0; i < name.length(); i++) {
            if ((name.charAt(i) < 'a' && name.charAt(i) > 'z') || (name.charAt(i) < 'A' && name.charAt(i) > 'Z')) {
                if (name.charAt(i) == ' ') continue;
                return false;
            }
        }
        return true;
    }
    private static boolean checkDebitCardNumber(String card){
        if(card.length() != AuthServices.length)return false;
        for(int i = 0;i<card.length();i++){
            if(card.charAt(i)<'0' || card.charAt(i)>'9')return false;
        }
        return true;
    }
    private static boolean checkValidAge(int age) {
        if (age < 0) return false;
        return age <= 130;
    }

    private static boolean checkValidEmail(String email) {
        String temp = "";
        int k = 0;
        for (int i = 0; i < email.length(); i++) {
            if (k == 1) {
                temp = temp + email.charAt(i);
            }
            if (email.charAt(i) == '@') {
                k = 1;
            }
        }
        if (k == 0) return false;
        else {
            int g = 0;
            String[] validDomains = {"gmail.com", "yahoo.com", "outlook.com", "protonmail.com", "hotmail.com"};
            for (int i = 0; i < validDomains.length; i++) {
                if (validDomains[i].equals(temp)) {
                    g = 1;
                    break;
                }
            }
            if (g == 0) return false;
        }
        return true;
    }

    private static boolean checkValidNum(long num) {
        if (num < 1000000000) return false;
        return true;
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void pause() {
        System.out.println("\nPress Enter to continue...");
        try {
            System.in.read();  // waits for the user to press Enter
        } catch (Exception e) {
            // ignored
        }
    }
    private static String generateRandom(){
        if(userCount > Math.pow(10,16)) {
            AuthServices.length = AuthServices.length * 2;
            // modify() modify all the users debit card number to a higher number of digit from database
        }
        while(true) {
        String number = "";
            for (int i = 0; i < AuthServices.length; i++) {
                Random rand = new Random();
                int RandomNum = rand.nextInt(10);
                number = number + Integer.toString(RandomNum);
            }
            return number;
            // if(isUnique(number))break;
            // we will be checking form database that if the random number is unique;
        }
    }
    public static void Signup() {
        Scanner sc = new Scanner(System.in);
        String name;
        String email;
        int age;
        long num;
        double balance;
        while (true) {
            clearScreen();
            System.out.println("=== SIGN UP ===");
            System.out.print("Enter Your Name: ");
            name = sc.nextLine();
            if (!checkValidName(name)) {
                System.out.println("⚠️  Invalid Name! Name should only contain alphabets and spaces.");
                pause();
            } else break;
        }

        while (true) {
            try {
                clearScreen();
                System.out.println("=== SIGN UP ===");
                System.out.print("Enter Your Age: ");
                age = sc.nextInt();
                if (!checkValidAge(age)) {
                    System.out.println("⚠️  Invalid Age! Please enter an age between 0 and 130.");
                    pause();
                } else break;
            } catch (InputMismatchException e) {
                System.out.println("⚠️  Invalid Input! Age must be a number.");
                sc.nextLine();
                pause();
            }
        }

        sc.nextLine(); // clear buffer before nextLine()
        while (true) {
            clearScreen();
            System.out.println("=== SIGN UP ===");
            System.out.print("Enter Your Email: ");
            email = sc.nextLine();
            if (!checkValidEmail(email)) {
                System.out.println("⚠️  Invalid Email! Please enter a valid email like example@gmail.com");
                pause();
            } else break;
        }

        while (true) {
            try {
                clearScreen();
                System.out.println("=== SIGN UP ===");
                System.out.print("Enter Your Mobile Number: ");
                num = sc.nextLong();
                if (!checkValidNum(num)) {
                    System.out.println("⚠️  Invalid Number! Mobile number should be 10 digits.");
                    pause();
                } else break;
            } catch (InputMismatchException e) {
                System.out.println("⚠️  Invalid Input! Mobile number must be numeric.");
                sc.nextLine();
                pause();
            }
        }
        while(true){
            try {
                System.out.println("=== SIGN UP ===");
                System.out.print("Enter The Amount To Be initaly deposited : ");
                balance = sc.nextDouble();
                if(balance < 0){
                    System.out.println("⚠️  Invalid Number! Balance Can't be negative|");
                    pause();
                } else break;
            } catch (InputMismatchException e) {
                System.out.println("⚠️  Invalid Input! Mobile number must be numeric.");
                sc.nextLine();
                pause();
            }
        }
        // we have all the user related info here now we have to save that in database and save that
        // in session also
        String DebitCardNumber = generateRandom();
        LoggedInUser.setName(name);
        LoggedInUser.setAge(age);
        LoggedInUser.setEmail(email);
        LoggedInUser.setMobileNumber(num);
        LoggedInUser.setDebitCardNumber(DebitCardNumber);
        LoggedInUser.setBalance(balance);
        userCount++;
        AuthServices.InsertUserInfo();
        Home.menu();
    }

    public static void Login() {
        Scanner sc = new Scanner(System.in);
        String name;
        String card;
        long num;
        while (true) {
            clearScreen();
            System.out.println("=== LOGIN ===");
            System.out.print("Enter Your Name: ");
            name = sc.nextLine();
            if (!checkValidName(name)) {
                System.out.println("⚠️  Invalid Name! Name should only contain alphabets and spaces.");
                pause();
            } else break;
        }

        while (true) {
                clearScreen();
                System.out.println("=== LOGIN ===");
                System.out.print("Enter Your Debit Card Number: ");
                card = sc.nextLine();
                if (!checkDebitCardNumber(card)) {
                    System.out.println("⚠️  Invalid Card Number! It should be 16 digits.");
                    pause();
                } else break;
        }

        while (true) {
            try {
                clearScreen();
                System.out.println("=== LOGIN ===");
                System.out.print("Enter Your Mobile Number: ");
                num = sc.nextLong();
                if (!checkValidNum(num)) {
                    System.out.println("⚠️  Invalid Number! Mobile number should be 10 digits.");
                    pause();
                } else break;
            } catch (InputMismatchException e) {
                System.out.println("⚠️  Invalid Input! Mobile number must be numeric.");
                sc.nextLine();
                pause();
            }
        }

        if(validateUser(name,card,num)){
            saveToLoggedInUser(card);
            clearScreen();
            System.out.println("✅ Logged in successfully!");
            Home.menu();
        }
        else {
            System.out.println(" Credentailas Mismatched ");
            Main.main(new String[0]);
        }

    }

}
