/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package chessgame.server;

import java.io.IOException;

/**
 * 
 * @author arvid.renestam
 */
public class Main {

    public static void main(String[] args) {
        try {
            System.out.println("Initializing Chess Server on port 3000...");
            new Server().start();
        } catch (IOException ex) {
           System.err.println("Server failed to boot: " + ex.getMessage());
        }
    }
    
}
