package Utils;

public class LoggedInUser {
    private static String name;
    private static String email;
    private static long MobileNumber;
    private static String DebitCardNumber;
    private static int Age;
    private static double balance;
    // we will be fetching balance from the database itself
    public static void setAge(int age){
        LoggedInUser.Age = age;
    }
    public static void setName(String name){
        LoggedInUser.name = name;
    }
    public static  void setEmail(String email){
        LoggedInUser.email = email;
    }
    public static void setMobileNumber(long number){
        LoggedInUser.MobileNumber = number;
    }
    public static void setDebitCardNumber(String number){
        LoggedInUser.DebitCardNumber = number;
    }
    public static void setBalance(double balance){
        LoggedInUser.balance = balance;
    }
    public static String getName(){
        return name;
    }
    public static  String getEmail(){
        return email;
    }
    public static long getMobileNumber(){
        return MobileNumber;
    }
    public static String getDebitCardNumber(){
        return DebitCardNumber;
    }
    public static int getAge(){
        return Age;
    }
    public static double getBalance(){
        return balance;
    }
}
