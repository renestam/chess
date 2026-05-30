/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
//package chessgame.client;
//
//import chessgame.shared.Move;
//
//public class GameManager {
//    
//    private MainMenuUI mainMenu;
//    private ChessBoardUI chessBoard;
//    private ServerConnection connection;
//
//    public GameManager() {
//        // Start by showing the main menu
//        this.mainMenu = new MainMenuUI(this); 
//        this.mainMenu.setVisible(true);
//    }
//
//    // ─── THIS IS WHERE YOU START THE SERVER CONNECTION ───
//    public void connectToServer(String ip, int port) {
//        System.out.println("Connecting to server...");
//        
//        // Initialize the connection!
//        this.connection = new ServerConnection(ip, port, this);
//        
//        // You can update the UI here to show a "Waiting for opponent..." loading screen
//    }
//
//    /**
//     * This method is called by your ServerConnection's background thread
//     * whenever an object (like a Move) arrives from the server.
//     */
//    public void handleServerObject(Object obj) {
//        if (obj instanceof Move move) {
//            // Forward the opponent's move directly to your UI board to display it!
//            chessBoard.applyOpponentMove(move);
//        }
//        // Handle other objects like Match updates, Chat, etc.
//    }
//
//    /**
//     * Call this when your local player drags and drops a piece on their screen
//     */
//    public void sendMoveToServer(Move move) {
//        if (connection != null) {
//            connection.sendObject(move);
//        }
//    }
//}