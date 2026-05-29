/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author arvid.renestam
 */
public class Server implements Runnable {
    
    private final ServerSocket listeningSocket;
    private Thread t = new Thread(this);
    private Lobby lobby = new Lobby();
    
    
    public Server() throws IOException {
        listeningSocket = new ServerSocket(3000);
    }
    
    public Server start() {
        t.start();
        return this;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Socket incomingSocket = listeningSocket.accept();
                System.out.println("New client connected!");
                
                // 1. Wrap the connection and add them to the matchmaking lobby
                ClientManager newClient = new ClientManager(incomingSocket);
                lobby.addCM(newClient);
                
                // 2. If we have 2 or more players, pull them out and start a game
                if (lobby.getNoOfWaitingClients() >= 2) {
                    ClientManager cm1 = lobby.getOneCM();
                    ClientManager cm2 = lobby.getOneCM();
                    
                    // Ensure we actually got two valid clients before starting
                    if (cm1 != null && cm2 != null) {
                        System.out.println("Match found! Starting a new game...");
                        // new Game(cm1, cm2); // This will spin up your game controller
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
