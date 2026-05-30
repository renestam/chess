/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 
 * @author arvid.renestam
 */
public class Server implements Runnable {
    
    private final ServerSocket listeningSocket;
    private final Thread serverThread = new Thread(this);
    private final Lobby lobby = new Lobby();
    private boolean running = false;
    
    public Server() throws IOException {
        // Explicitly binds the program to listen on port 3000
        listeningSocket = new ServerSocket(3000);
    }
    
    public Server start() {
        if (!running) {
            this.running = true;
            serverThread.start();
            System.out.println("Server successfully listening for connections on port 3000!");
        }
        return this;
    }

    @Override
    public void run() {
        while (running && !Thread.interrupted()) {
            try {
                // Blocks until a client inputs their IP address and hits Connect
                Socket incomingSocket = listeningSocket.accept();
                System.out.println("New raw client socket connected from: " + incomingSocket.getRemoteSocketAddress());
                
                // 1. Wrap connection inside Object-Stream manager
                ClientManager newClient = new ClientManager(incomingSocket);
                
                // 2. Thread-Safe Matchmaking Check
                // We synchronize on the lobby object so that multiple client threads 
                // cannot corrupt the queue state during checkout.
                synchronized (lobby) {
                    lobby.addCM(newClient);
                    System.out.println("Client placed in queue. Total waiting: " + lobby.getNoOfWaitingClients());
                    
                    if (lobby.getNoOfWaitingClients() >= 2) {
                        ClientManager cm1 = lobby.getOneCM();
                        ClientManager cm2 = lobby.getOneCM();
                        
                        if (cm1 != null && cm2 != null) {
                            System.out.println("Match found! Spinning up GameController...");
                            
                            // Instantiate your game controller to link these two communication pipelines together
                            new GameController(cm1, cm2); 
                        }
                    }
                }
                
            } catch (IOException ex) {
                if (!listeningSocket.isClosed()) {
                    System.err.println("Network acceptance crash: " + ex.getMessage());
                }
            }
        }
    }
    
    // Call this if you need to safely close down the hosting process
    public synchronized void stop() {
        this.running = false;
        try {
            serverThread.interrupt();
            if (listeningSocket != null && !listeningSocket.isClosed()) {
                listeningSocket.close();
            }
            System.out.println("Server listening hub terminated successfully.");
        } catch (IOException e) {
            // Soft catch during close routine
        }
    }
}
