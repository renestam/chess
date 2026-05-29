/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author arvid.renestam
 */
public class ClientManager {

    private Socket s;
    private DataInputStream streamIn;
    private DataOutputStream streamOut;

    public ClientManager(Socket s) {
        this.s = s;
        try {
            streamIn = new DataInputStream(s.getInputStream());
            streamOut = new DataOutputStream(s.getOutputStream());
        } catch (IOException ex) {
            System.out.println("problem. fixa.");
        }

        t2.start();
    }

    Thread t2 = new Thread(() -> {
        while (!Thread.interrupted()) {
            try {
                String incomingMsg = streamIn.readUTF();
                dealWithIncomingMsg(incomingMsg);
            } catch (IOException e) {
                System.out.println("problem: " + e.getMessage());
            }
        }
    });
    
    private void sendMsgToClient(String msg) {
        try {
            streamOut.writeUTF(msg);
        } catch (IOException ex) {
            Logger.getLogger(ClientManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void dealWithIncomingMsg(String incomingMsg) {
        System.out.println(incomingMsg);
    }

}
