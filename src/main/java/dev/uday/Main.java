package dev.uday;

import dev.uday.GUI.MainFrame;

public class Main {
    public static String OS_Name;
    public static String OS_Version;
    public static String OS_Architecture;
    public static String USER_NAME;
    public static String JAVA_VM_NAME;
    public static String JAVA_VERSION;
    public static String About;
    public static void main(String[] args) {
        MainFrame.setMainFrame();
        OS_Name = System.getProperty("os.name");
        OS_Version = System.getProperty("os.version");
        JAVA_VERSION = System.getProperty("java.version");
        JAVA_VM_NAME = System.getProperty("java.vm.name");
        USER_NAME = System.getProperty("user.name");
        OS_Architecture = System.getProperty("os.arch");
        About = "About this application\n\n" +
                "This is a simple chat application that allows users to communicate with each other.\n" +
                "It is built using Java and Swing for the GUI.\n" +
                "The server and client is built using Java's ServerSocket and Socket classes.\n" +
                "The application uses a simple server to client encrypted protocol to send and receive messages and data.\n" +
                "The application is built by Uday Khare.\n\n"+
                "Host OS: " + Main.OS_Name + "\n" +
                "User Name: " + Main.USER_NAME + "\n" +
                "Host OS Version: " + Main.OS_Version + "\n" +
                "Host OS Architecture: " + Main.OS_Architecture + "\n" +
                "Java VM: " + Main.JAVA_VM_NAME + "\n" +
                "Java Version: " + Main.JAVA_VERSION + "\n\n\n" +
                "Github: " + "UdayKhare09" + "\n" +
                "LinkedIn: " + "https://linkedin.com/in/uday-khare-a09208289" + "\n" +
                "Email: " + "udaykhare77@gmail.com" + "\n" +
                "Portfolio: " + "https://portfolio.udaykhare.social" + "\n" +
                "Please feel free to contact me for any queries, suggestions, collaborations or any bugs you find in the application.\n" +
                "And don't forget to star the repository if you like the project!";
    }
}