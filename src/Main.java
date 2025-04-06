import Services.AuthServices;
import Utils.DataBaseManager;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            int k = 0;
            System.out.println("1. Press 1 for Sign Up");
            System.out.println("2. Press 2 for Log in");
            System.out.print("Enter Your Choice : ");
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
        }
    }
}
