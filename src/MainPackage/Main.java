package MainPackage;

import Services.AuthServices;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
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

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            Main.clearScreen();
            int k = 0;
            System.out.println("1. Press 1 for Sign Up");
            System.out.println("2. Press 2 for Log in");
            System.out.print("Enter Your Choice : ");
            try {
                int inp = sc.nextInt();
                switch (inp) {
                    case 1: {
                        AuthServices.Signup();
                        k = 1;
                        break;
                    }
                    case 2: {
                        AuthServices.Login();
                        k = 1;
                        break;
                    }
                    default: {
                        k = 0;
                        System.out.println("Enter a Valid input");
                    }
                }
                if (k == 1) return;
            } catch (InputMismatchException e) {
                System.out.println("Only Numeric type data accepted");
                pause();
            }
        }
    }
}
