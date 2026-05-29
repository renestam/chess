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
        t.start();
    }

    @Override
    public void run() {
        while(true) {
            try {
                Socket incomingSocket = listeningSocket.accept();
                /*lobby.addCM(new ClientManager(incomingSocket));
                if(lobby.getNoOfWaitingClients() == 2) {
                ClientManager cm1 = lobby.getOneCM();
                ClientManager cm2 = lobby.getOneCM();
                new Game(cm1, cm2); //DETTA game har en egen gamecontroller och en egen modell (som innehåller all relevant information för just detta spel)
                }*/
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
