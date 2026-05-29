/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author arvid.renestam
 */
public class ClientManager {

    private final Socket socket;
    private ObjectOutputStream streamOut;
    private ObjectInputStream streamIn;
    private boolean running = true;

    public ClientManager(Socket socket) {
        this.socket = socket;
        try {
            // CRITICAL ORDER: Output stream must be initialized FIRST and flushed
            this.streamOut = new ObjectOutputStream(socket.getOutputStream());
            this.streamOut.flush(); 
            
            this.streamIn = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            System.out.println("Failed to establish object streams for a client.");
            closeConnection();
            return;
        }

        // Start the reading thread
        listenerThread.start();
    }

    private final Thread listenerThread = new Thread(() -> {
        while (running && !Thread.interrupted()) {
            try {
                // 1. Read the incoming object (Move, Match, Packet etc.)
                Object incomingData = streamIn.readObject();
                
                // 2. Handle it safely
                dealWithIncomingData(incomingData);
                
            } catch (IOException | ClassNotFoundException e) {
                // Catches user closing app, disconnecting, or stream errors
                System.out.println("Client disconnected or error occurred: " + e.getMessage());
                closeConnection(); 
                break; // CRITICAL: Breaks out of the while loop so your CPU doesn't melt!
            }
        }
    });
    
    // Send objects (like Move or Match updates) back to this specific client
    public void sendObjectToClient(Object obj) {
        try {
            if (streamOut != null) {
                streamOut.writeObject(obj);
                streamOut.flush();
            }
        } catch (IOException ex) {
            System.out.println("Error sending object to client: " + ex.getMessage());
            closeConnection();
        }
    }

    private void dealWithIncomingData(Object obj) {
        // This is where you pass objects off to your server game logic!
        System.out.println("Received object from client: " + obj.getClass().getSimpleName());
    }

    // Safely tears everything down without throwing unhandled exceptions
    public synchronized void closeConnection() {
        if (!running) return;
        running = false;
        try {
            listenerThread.interrupt();
            if (streamIn != null) streamIn.close();
            if (streamOut != null) streamOut.close();
            if (socket != null && !socket.isClosed()) socket.close();
            System.out.println("Client resources cleaned up cleanly.");
        } catch (IOException e) {
            // Soft catch during forced closure
        }
    }
}