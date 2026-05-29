/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame.server;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 *
 * @author arvid.renestam
 */
public class Lobby {
    
    private Deque<ClientManager> waitingClients = new ArrayDeque<>();
    
    
    public void addClientManager(ClientManager cm) {
        waitingClients.add(cm);
    }
    
    public ClientManager getClientManager() {
        return waitingClients.pollFirst();
    }
    
}
