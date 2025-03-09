package dev.uday.NET;

import dev.uday.GUI.MainFrame;
import dev.uday.GUI.MainPanel;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;

public class SocketClient {
    private static final String HOST = "localhost";
    private static final int PORT = 2005;
    private static KeyPair keyPair;
    private static PublicKey serverPublicKey;
    public static DataInputStream inputStream;
    public static DataOutputStream outputStream;
    private static Cipher cipher;
    public static String username;
    public static String password;
    private static Socket socket;
    public static boolean loggedIn = false;
    public static ArrayList<String> onlineUsers = new ArrayList<>();
    public static int onlineUsersCount;

    public static void init(String username, String password) {
        try {
            socket = new Socket(HOST, PORT);
            SocketClient.username = username;
            SocketClient.password = password;
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());

            System.out.println("Connected to server");

            // Generate RSA key pair
            keyExchange();
            System.out.println("Key exchange complete");

            // Send LoginInfo to server and handle response
            handleResponse(sendLoginInfo());
            System.out.println("Login response received");

            // Change to menu panel
            MainPanel.setMainPanel();

            // Start listening for packets
            System.out.println("Starting to receive packets");
            startReceivingPacket();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | InvalidKeyException |
                 BadPaddingException | IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        }
    }

    private static void handleResponse(int response) {
        if (response == 1) {
            JOptionPane.showMessageDialog(MainFrame.mainFrame, "Login Successful", "Success", JOptionPane.INFORMATION_MESSAGE);
            loggedIn = true;
        } else if (response == 0) {
            JOptionPane.showMessageDialog(MainFrame.mainFrame, "User not found", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        } else if (response == 2) {
            JOptionPane.showMessageDialog(MainFrame.mainFrame, "Wrong Password", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        } else if (response == 3) {
            JOptionPane.showMessageDialog(MainFrame.mainFrame, "User already logged in", "Error", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }
    }

    public static void sendPacket(byte[] bytes) {
        try {
            if (socket.isClosed() || !socket.isConnected()) {
                throw new IOException("Socket is closed or not connected");
            }
            cipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
            byte[] encryptedBytes = cipher.doFinal(bytes);
            // Send encrypted bytes to server
            outputStream.writeInt(encryptedBytes.length);
            outputStream.write(encryptedBytes);
            outputStream.flush();
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.err.println("Failed to send packet: " + e.getMessage());
        }
    }

    private static void startReceivingPacket() {
        try {
            System.out.println("Listening for packets");
            while (true) {
                int length = inputStream.readInt();
                byte[] bytes = new byte[length];
                inputStream.readFully(bytes);
                cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
                byte[] decryptedBytes = cipher.doFinal(bytes);
                System.out.println("Received packet");
                PacketHandler.handlePacket(decryptedBytes);
            }
        } catch (IOException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    private static void keyExchange() throws NoSuchAlgorithmException, NoSuchPaddingException, IOException, InvalidKeySpecException {
        keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        cipher = Cipher.getInstance("RSA");

        // Send public key to server
        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
        outputStream.writeInt(publicKeyBytes.length);
        outputStream.write(publicKeyBytes);
        outputStream.flush();

        // Receive server's public key
        int length = inputStream.readInt();
        byte[] serverPublicKeyBytes = new byte[length];
        inputStream.readFully(serverPublicKeyBytes);
        serverPublicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(serverPublicKeyBytes));

    }

    private static int sendLoginInfo() throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
        byte[] encryptedUsername = cipher.doFinal(SocketClient.username.getBytes());
        outputStream.writeInt(encryptedUsername.length);
        outputStream.write(encryptedUsername);
        outputStream.flush();

        // Send Pass to server
        byte[] encryptedPass = cipher.doFinal(SocketClient.password.getBytes());
        outputStream.writeInt(encryptedPass.length);
        outputStream.write(encryptedPass);
        outputStream.flush();

        // Receive response from server
        return inputStream.readInt();
    }

    public static void stop() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}