/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame.client;

import chessgame.shared.Board;
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
    
    private final GamePanel gamePanel;

    public ServerConnection(String ipAddress, int port, GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        try {
            this.socket = new Socket(ipAddress, port);
            System.out.println("Connected to server!");

            this.streamOut = new ObjectOutputStream(socket.getOutputStream());
            this.streamOut.flush();
            this.streamIn = new ObjectInputStream(socket.getInputStream());


            startListening();

        } catch (IOException e) {
            System.err.println("Could not connect to server: " + e.getMessage());
        }
    }

    private void startListening() {
        this.running = true;
        
        this.listenerThread = new Thread(() -> {
            while (running && !Thread.interrupted()) {
                try {
                    Object incomingData = streamIn.readObject();

                    processServerMessage(incomingData);
                    
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Disconnected from server: " + e.getMessage());
                    stop();
                    break;
                }
            }
        });
        
        this.listenerThread.start();
    }

    private void processServerMessage(Object obj) {
        System.out.println("Received from server: " + obj.getClass().getSimpleName());
        
        // MOVE
        if (obj instanceof Move move) {
            Board localBoard = GamePanel.getClientBoard().getBoard();
            Move realLocalMove = move.bindToLocalBoard(localBoard);
            gamePanel.handleIncomingMove(realLocalMove);
        } 
        
        // MATCH
        else if (obj instanceof Match match) {
            // a match was found or completed
            if (match.getWhitePlayer() != null && match.getBlackPlayer() != null) {
                if (match.isActive()) {
                    gamePanel.startMatch(match);
                } else {
                    gamePanel.endMatch(match);
                }
            }
        }
    }

    public void sendObject(Object obj) {
        try {
            if (streamOut != null) {
                streamOut.reset();
                streamOut.writeObject(obj);
                streamOut.flush();
            }
            System.out.println("Sent to server: " + obj.getClass().getSimpleName());
        } catch (IOException e) {
            System.out.println("Error sending object to server: " + e.getMessage());
        }
    }

    public synchronized void stop() {
        if (!running) return;
        running = false;
        try {
            if (listenerThread != null) listenerThread.interrupt();
            if (streamIn != null) streamIn.close();
            if (streamOut != null) streamOut.close();
            if (socket != null && !socket.isClosed()) socket.close();
            System.out.println("Connection closed safely.");
        } catch (IOException e) {}
    }
    
}
