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
public final class ClientManager {

    private final Socket socket;
    private ObjectOutputStream streamOut;
    private ObjectInputStream streamIn;
    private boolean running = true;

    private GameController gameController;
    
    private Object cachedData;

    public ClientManager(Socket socket) {
        this.socket = socket;
        try {
            this.streamOut = new ObjectOutputStream(socket.getOutputStream());
            this.streamOut.flush(); 
            
            this.streamIn = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            System.out.println("Object streams for a client failed.");
            closeConnection();
            return;
        }

        listenerThread.start();
    }

    public synchronized void setGameController(GameController gameController) {
        this.gameController = gameController;
        
        if (this.gameController != null && this.cachedData != null) {
            System.out.println("Forwarding cached lobby data to new GameController...");
            this.gameController.handleIncomingData(this, this.cachedData);
            this.cachedData = null; // Purge cache to prevent processing duplication
        }
    }

    private final Thread listenerThread = new Thread(() -> {
        while (running && !Thread.interrupted()) {
            try {
                Object incomingData = streamIn.readObject();
                
                dealWithIncomingData(incomingData);
                
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Client disconnected or error occurred: " + e.getMessage());
                closeConnection(); 
                break; 
            }
        }
    });
    
    // sends objects like Move or Match to the client
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

    private synchronized void dealWithIncomingData(Object obj) {
        System.out.println("Received object from client: " + obj.getClass().getSimpleName());
        
        if (this.gameController != null) {
            this.gameController.handleIncomingData(this, obj);
        } else {
            // save the player profile so it can be reclaimed when match begins
            System.out.println("No GameController assigned yet. Caching data for matchmaking...");
            this.cachedData = obj;
        }
    }

    public synchronized void closeConnection() {
        if (!running) return;
        running = false;
        try {
            listenerThread.interrupt();
            if (streamIn != null) streamIn.close();
            if (streamOut != null) streamOut.close();
            if (socket != null && !socket.isClosed()) socket.close();
            System.out.println("Client resources cleaned up cleanly.");
        } catch (IOException e) {}
    }
}