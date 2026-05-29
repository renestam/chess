/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package chessgame.client;

import java.awt.Dimension;
import javax.swing.JFrame;

/**
 *
 * @author arvid.renestam
 */
public class GameWindow extends JFrame {
    
    public static int MIN_WIDTH = 600;
    public static int MIN_HEIGHT = 350;
    
    public static int PREF_WIDTH = 800;
    public static int PREF_HEIGHT = 738;
    
    public GameWindow() {
        initComponents();
    } 
    
    private void initComponents() {
        GamePanel panel = new chessgame.client.GamePanel();
        
        setVisible(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(PREF_WIDTH, PREF_HEIGHT));
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        getContentPane().add(panel, java.awt.BorderLayout.CENTER);

        pack();
    }
    
}
