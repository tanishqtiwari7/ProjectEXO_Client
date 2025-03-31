package dev.uday.NET;

import dev.uday.GUI.MainFrame;
import dev.uday.GUI.MainPanel;
import dev.uday.NET.Packets.PacketHandler;

import javax.crypto.*;
import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;

public class SocketClient {
    private static final int CHUNK_SIZE = 240;
    private static String HOST;
    private static int PORT;
    public static String clientIP;
    private static KeyPair keyPair;
    private static PublicKey serverPublicKey;
    public static DataInputStream inputStream;
    public static DataOutputStream outputStream;
    private static Cipher cipher;
    public static String username;
    public static String password;
    private static Socket socket;
    public static String serverIP;
    public static boolean loggedIn = false;
    public static ArrayList<String> onlineUsers = new ArrayList<>();
    public static int onlineUsersCount;

    public static void init(String serverIP, String port, String username, String password) {
        try {
            HOST = serverIP;
            PORT = Integer.parseInt(port);
            socket = new Socket(HOST, PORT);
            SocketClient.serverIP = socket.getInetAddress().toString();
            clientIP = socket.getLocalAddress().toString();
            SocketClient.username = username;
            SocketClient.password = password;
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());

            System.out.println("Connected to server");

            keyExchange();
            System.out.println("Key exchange complete");

            handleResponse(sendLoginInfo());
            System.out.println("Login response received");

            MainPanel.setMainPanel();
            System.out.println("Starting to receive packets");
            startReceivingPacket();

        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    private static void handleResponse(int response) {
        switch (response) {
            case 1:
                showMessage("Login Successful", "Success", JOptionPane.INFORMATION_MESSAGE);
                loggedIn = true;
                break;
            case 0:
                showMessage("User not found", "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
                break;
            case 2:
                showMessage("Wrong Password", "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
                break;
            case 3:
                showMessage("User already logged in", "Error", JOptionPane.WARNING_MESSAGE);
                System.exit(0);
                break;
        }
    }

    private static void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(MainFrame.mainFrame, message, title, messageType);
    }

    public static void sendPacket(byte[] bytes) {
        try {
            if (socket.isClosed() || !socket.isConnected()) {
                throw new IOException("Socket is closed or not connected");
            }

            int totalChunks = (int) Math.ceil((double) bytes.length / CHUNK_SIZE);
            sendHeader(bytes.length, totalChunks);

            for (int i = 0; i < totalChunks; i++) {
                int start = i * CHUNK_SIZE;
                int end = Math.min(bytes.length, start + CHUNK_SIZE);
                byte[] chunk = new byte[end - start];
                System.arraycopy(bytes, start, chunk, 0, end - start);
                sendChunk(chunk);
            }
        } catch (IOException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
    }

    private static void sendHeader(int totalSize, int totalChunks) throws IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
        byte[] header = ("SIZE:" + totalSize + ";CHUNKS:" + totalChunks).getBytes();
        byte[] encryptedHeader = cipher.doFinal(header);
        outputStream.writeInt(encryptedHeader.length);
        outputStream.write(encryptedHeader);
        outputStream.flush();
    }

    private static void sendChunk(byte[] chunk) throws IOException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
        byte[] encryptedChunk = cipher.doFinal(chunk);
        outputStream.writeInt(encryptedChunk.length);
        outputStream.write(encryptedChunk);
        outputStream.flush();
    }

    private static void startReceivingPacket() {
        try {
            System.out.println("Listening for packets");
            while (true) {
                int headerLength = inputStream.readInt();
                byte[] headerBytes = new byte[headerLength];
                inputStream.readFully(headerBytes);
                cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
                byte[] decryptedHeader = cipher.doFinal(headerBytes);

                String header = new String(decryptedHeader);
                int totalSize = Integer.parseInt(header.split(";")[0].split(":")[1]);
                int totalChunks = Integer.parseInt(header.split(";")[1].split(":")[1]);

                ByteArrayOutputStream completePacket = new ByteArrayOutputStream();
                for (int i = 0; i < totalChunks; i++) {
                    int chunkLength = inputStream.readInt();
                    byte[] chunk = new byte[chunkLength];
                    inputStream.readFully(chunk);
                    cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
                    byte[] decryptedChunk = cipher.doFinal(chunk);
                    completePacket.write(decryptedChunk);
                }

                byte[] decryptedBytes = completePacket.toByteArray();
                System.out.println("Received packet (size: " + decryptedBytes.length + " bytes)");
                PacketHandler.handlePacket(decryptedBytes);
            }
        } catch (IOException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
    }

    private static void keyExchange() throws NoSuchAlgorithmException, NoSuchPaddingException, IOException, InvalidKeySpecException {
        keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        cipher = Cipher.getInstance("RSA");

        sendPublicKey();
        receiveServerPublicKey();
    }

    private static void sendPublicKey() throws IOException {
        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
        outputStream.writeInt(publicKeyBytes.length);
        outputStream.write(publicKeyBytes);
        outputStream.flush();
    }

    private static void receiveServerPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        int length = inputStream.readInt();
        byte[] serverPublicKeyBytes = new byte[length];
        inputStream.readFully(serverPublicKeyBytes);
        serverPublicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(serverPublicKeyBytes));
    }

    private static int sendLoginInfo() throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        cipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
        sendEncryptedData(username.getBytes());
        sendEncryptedData(password.getBytes());
        return inputStream.readInt();
    }

    private static void sendEncryptedData(byte[] data) throws IOException, IllegalBlockSizeException, BadPaddingException {
        byte[] encryptedData = cipher.doFinal(data);
        outputStream.writeInt(encryptedData.length);
        outputStream.write(encryptedData);
        outputStream.flush();
    }

    public static void stop() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}