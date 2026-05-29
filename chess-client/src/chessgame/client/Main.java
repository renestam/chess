/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame.client;

/**
 *
 * @author arvid.renestam
 */
public class Main {
    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new GameWindow().setVisible(true);
        });
    }
    
}
