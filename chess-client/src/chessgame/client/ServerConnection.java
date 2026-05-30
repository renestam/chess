/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame.client;

import chessgame.shared.Match;
import chessgame.shared.Move;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 
 * @author arvid.renestam
 */
public class ServerConnection {

    private Socket socket;
    private ObjectOutputStream streamOut;
    private ObjectInputStream streamIn;
    private Thread listenerThread;
    private boolean running = false;
    
    // Reference to the UI panel so we can update it when events happen
    private final GamePanel gamePanel;

    public ServerConnection(String ipAddress, int port, GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        try {
            // 1. Connect to the server
            this.socket = new Socket(ipAddress, port);
            System.out.println("Connected to server!");

            // 2. Set up Object streams. CRITICAL ORDER: Output must be first and flushed!
            this.streamOut = new ObjectOutputStream(socket.getOutputStream());
            this.streamOut.flush();
            this.streamIn = new ObjectInputStream(socket.getInputStream());

            // 3. Start listening for incoming objects from the server
            startListening();

        } catch (IOException e) {
            System.err.println("Could not connect to server: " + e.getMessage());
        }
    }

    private void startListening() {
        this.running = true;
        
        // This thread runs in the background looking for server data
        this.listenerThread = new Thread(() -> {
            while (running && !Thread.interrupted()) {
                try {
                    // This blocks until the server transmits an object via writeObject()
                    Object incomingData = streamIn.readObject();
                    
                    // Route the deserialized object safely
                    processServerMessage(incomingData);
                    
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Disconnected from server: " + e.getMessage());
                    stop(); // Clean up local resources if connection drops
                    break; // Escape loop to avoid CPU spike
                }
            }
        });
        
        this.listenerThread.start();
    }

    /**
     * Identifies what object type arrived from the server and routes it to the UI.
     */
    private void processServerMessage(Object obj) {
        System.out.println("Received from server: " + obj.getClass().getSimpleName());
        
        if (obj instanceof Move move) {
            // Update the board with the opponent's move
            gamePanel.handleIncomingMove(move);
            
        } else if (obj instanceof Match match) {
            // A match was found or completed
            if (match.getWhitePlayer() != null && match.getBlackPlayer() != null) {
                if (match.isActive()) {
                    gamePanel.startMatch(match);
                } else {
                    gamePanel.endMatch(match);
                }
            }
        }
    }

    /**
     * Sends any serializable object (Player profiles, Moves, etc.) to the server.
     * @param obj
     */
    public void sendObject(Object obj) {
        try {
            if (streamOut != null) {
                streamOut.writeObject(obj);
                streamOut.flush();
            }
        } catch (IOException e) {
            System.out.println("Error sending object to server: " + e.getMessage());
        }
    }

    // Call this when closing the application to safely tear down networking elements
    public synchronized void stop() {
        if (!running) return;
        running = false;
        try {
            if (listenerThread != null) listenerThread.interrupt();
            if (streamIn != null) streamIn.close();
            if (streamOut != null) streamOut.close();
            if (socket != null && !socket.isClosed()) socket.close();
            System.out.println("Connection closed safely.");
        } catch (IOException e) {
            // Soft catch during forced closure
        }
    }
    
}
