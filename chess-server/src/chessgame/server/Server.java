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
        // bind the program to listen on port 3000
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
                Socket incomingSocket = listeningSocket.accept();
                System.out.println("New client socket connected from: " + incomingSocket.getRemoteSocketAddress());
                
                ClientManager newClient = new ClientManager(incomingSocket);
                
                synchronized (lobby) {
                    lobby.addCM(newClient);
                    System.out.println("Client placed in queue.");
                    
                    if (lobby.getNoOfWaitingClients() >= 2) {
                        ClientManager cm1 = lobby.getOneCM();
                        ClientManager cm2 = lobby.getOneCM();
                        
                        if (cm1 != null && cm2 != null) {
                            System.out.println("Match found! Creating GameController...");
                            
                            // Instantiate your game controller to link these two communication pipelines together
                            new GameController(cm1, cm2); 
                        }
                    }
                }
                
            } catch (IOException ex) {
                if (!listeningSocket.isClosed()) {
                    System.err.println("Network crash: " + ex.getMessage());
                }
            }
        }
    }
    
    // safely close down the hosting process
    public synchronized void stop() {
        this.running = false;
        try {
            serverThread.interrupt();
            if (listeningSocket != null && !listeningSocket.isClosed()) {
                listeningSocket.close();
            }
            System.out.println("Server listening terminated successfully.");
        } catch (IOException e) {
            // Soft catch during close routine
        }
    }
}
