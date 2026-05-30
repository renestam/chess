/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame.server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author arvid.renestam
 */
public class Lobby {
    
    private final BlockingQueue<ClientManager> waitingClients = new LinkedBlockingQueue<>();
    
    public void addCM(ClientManager cm) {
        waitingClients.add(cm);
    }
    
    public ClientManager getOneCM() {
        return waitingClients.poll();
    }
    
    public int getNoOfWaitingClients() {
        return waitingClients.size();
    }
}
