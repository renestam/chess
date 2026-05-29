/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author arvid.renestam
 */
public class ServerConnection {

    private Socket socket;
    private DataInputStream streamIn;
    private DataOutputStream streamOut;
    private Thread listenerThread;
    private boolean running = false;

    public ServerConnection(String ipAddress, int port) {
        try {
            // 1. Connect to the server
            this.socket = new Socket(ipAddress, port);
            System.out.println("Connected to server!");

            // 2. Set up the input and output streams
            this.streamIn = new DataInputStream(socket.getInputStream());
            this.streamOut = new DataOutputStream(socket.getOutputStream());

            // 3. Start listening for incoming messages from the server
            startListening();

        } catch (IOException e) {
            System.out.println("Could not connect to server: " + e.getMessage());
        }
    }

    private void startListening() {
        this.running = true;
        
        // This thread runs in the background looking for server messages
        this.listenerThread = new Thread(() -> {
            while (running && !Thread.interrupted()) {
                try {
                    // This blocks until the server calls writeUTF()
                    String incomingMsg = streamIn.readUTF();
                    
                    // Handle the message
                    processServerMessage(incomingMsg);
                    
                } catch (IOException e) {
                    System.out.println("Disconnected from server: " + e.getMessage());
                    stop(); // Clean up if the connection drops
                    break;
                }
            }
        });
        
        this.listenerThread.start();
    }

    // This is where you route messages to your UI or Match logic!
    private void processServerMessage(String msg) {
        System.out.println("Received from server: " + msg);
        
        // Example parsing:
        if (msg.startsWith("MATCH_FOUND")) {
            // Update UI to switch to the chess board screen
        } else if (msg.startsWith("MOVE")) {
            // Update the board with the opponent's move
        }
    }

    // Method to send your own moves to the server
    public void sendMsgToServer(String msg) {
        try {
            streamOut.writeUTF(msg);
        } catch (IOException e) {
            System.out.println("Error sending message: " + e.getMessage());
        }
    }

    // Call this when closing the game to safely shut down streams
    public synchronized void stop() {
        if (!running) return;
        running = false;
        try {
            if (listenerThread != null) listenerThread.interrupt();
            if (streamIn != null) streamIn.close();
            if (streamOut != null) streamOut.close();
            if (socket != null) socket.close();
            System.out.println("Connection closed safely.");
        } catch (IOException e) {
            // Soft catch during cleanup
        }
    }
}
