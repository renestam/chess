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
 * Manages an individual client's input and output object streams.
 * Forwards deserialized network objects to the active GameController.
 * @author arvid.renestam
 */
public class ClientManager {

    private final Socket socket;
    private ObjectOutputStream streamOut;
    private ObjectInputStream streamIn;
    private boolean running = true;
    
    // Reference to the controller directing the current match
    private GameController gameController;
    
    // Cache early setup packets (like Player profiles) received while in the lobby queue
    private Object cachedData;

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

    /**
     * Sets or changes the active game manager for this connection pipeline.
     * Instantly flushes cached queue-stage objects up to the controller loop.
     */
    public synchronized void setGameController(GameController gameController) {
        this.gameController = gameController;
        
        // If data arrived while waiting in the lobby, hand it over to the game controller now!
        if (this.gameController != null && this.cachedData != null) {
            System.out.println("Forwarding cached lobby data to new GameController...");
            this.gameController.handleIncomingData(this, this.cachedData);
            this.cachedData = null; // Purge cache to prevent processing duplication
        }
    }

    private final Thread listenerThread = new Thread(() -> {
        while (running && !Thread.interrupted()) {
            try {
                // 1. Read the incoming object (Move, Player, Match, etc.)
                Object incomingData = streamIn.readObject();
                
                // 2. Handle it safely
                dealWithIncomingData(incomingData);
                
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Client disconnected or error occurred: " + e.getMessage());
                closeConnection(); 
                break; 
            }
        }
    });
    
    /**
     * Sends objects (like Move or Match updates) back to this specific client.
     */
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

    /**
     * Routes incoming data structures up to the assigned GameController or caches it.
     */
    private synchronized void dealWithIncomingData(Object obj) {
        System.out.println("Received object from client: " + obj.getClass().getSimpleName());
        
        if (this.gameController != null) {
            this.gameController.handleIncomingData(this, obj);
        } else {
            // Save the player registration profile so it can be reclaimed when match begins
            System.out.println("No GameController assigned yet. Caching data for matchmaking...");
            this.cachedData = obj;
        }
    }

    /**
     * Safely tears down everything without throwing unhandled exceptions.
     */
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