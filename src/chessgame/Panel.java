package chessgame;

import java.awt.Graphics;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */

/**
 *
 * @author arvid.renestam
 */
public final class Panel extends javax.swing.JPanel {
    
    public static final Board board = new Board();
    public static final Leaderboard leaderboard = new Leaderboard();
    
    public static final int PADDING = 30;
    
    static int width;
    static int height;
    
    public Panel() {
        initComponents();
        updateLeaderboard();
    }
    
    public static Board getBoard() {
        return board;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        width = getSize().width;
        height = getSize().height;
        
        board.draw(g, this);
    }
    
    private void updateLeaderboard() {
        txaLeaderboard.setText(leaderboard.getLeaderboardText());
        txaGames.setText(leaderboard.getMatchesText());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txfWhitePlayer = new javax.swing.JTextField();
        txfBlackPlayer = new javax.swing.JTextField();
        btnStartGame = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txaGames = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txaLeaderboard = new javax.swing.JTextArea();
        btnEndGame = new javax.swing.JButton();

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(189, 112, 64));
        jPanel1.setForeground(new java.awt.Color(0, 0, 0));
        jPanel1.setEnabled(false);
        jPanel1.setFocusable(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(300, 300));

        btnStartGame.setText("START GAME");
        btnStartGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartGameActionPerformed(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("WHITE PLAYER");

        jLabel2.setBackground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("BLACK PLAYER");

        jLabel3.setText("LEADERBOARD");

        txaGames.setEditable(false);
        txaGames.setBackground(new java.awt.Color(255, 255, 255));
        txaGames.setColumns(15);
        txaGames.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txaGames.setForeground(new java.awt.Color(0, 0, 0));
        txaGames.setRows(5);
        txaGames.setFocusable(false);
        jScrollPane1.setViewportView(txaGames);

        jLabel4.setText("GAMES");

        txaLeaderboard.setEditable(false);
        txaLeaderboard.setBackground(new java.awt.Color(255, 255, 255));
        txaLeaderboard.setColumns(20);
        txaLeaderboard.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        txaLeaderboard.setForeground(new java.awt.Color(0, 0, 0));
        txaLeaderboard.setRows(5);
        txaLeaderboard.setText("1. \n2. \n3.\n4.\n5.");
        txaLeaderboard.setFocusable(false);
        jScrollPane3.setViewportView(txaLeaderboard);

        btnEndGame.setText("END GAME");
        btnEndGame.setEnabled(false);
        btnEndGame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEndGameActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel4)
                        .addComponent(jLabel3)
                        .addComponent(btnStartGame, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txfWhitePlayer))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txfBlackPlayer, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addComponent(jScrollPane3)
                        .addComponent(btnEndGame, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txfBlackPlayer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txfWhitePlayer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnStartGame)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEndGame)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 427, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 539, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // select a square
    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        int posX = evt.getX();
        int posY = evt.getY();
        
        int squareSize = board.getSquareSize();
        int boardSize = board.getBoardSize();
        
        int boardX = PADDING;
        int boardY = (height - boardSize) / 2;
        
        // check if click was outside board
        if (posX < boardX || posY < boardY || posX > boardX + boardSize || posY > boardY + boardSize) {
            return;
        }

        // calculate which square was clicked
        int xIndex = (posX - boardX) / squareSize;
        int yIndex = (posY - boardY) / squareSize;
        int squareIndex = yIndex * 8 + xIndex;
        
        // update the clicked square
        board.selectSquare(squareIndex);
        repaint();
    }//GEN-LAST:event_formMousePressed
    
    // start new game
    private void btnStartGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartGameActionPerformed
        String whitePlayerName = txfWhitePlayer.getText().trim();
        String blackPlayerName = txfBlackPlayer.getText().trim();
        
        if (whitePlayerName.isBlank() || blackPlayerName.isBlank()) return;
        if (whitePlayerName.toLowerCase().equals(blackPlayerName.toLowerCase())) return;
        
        // make fields non-editable
        txfWhitePlayer.setEditable(false);
        txfWhitePlayer.setFocusable(false);
        txfBlackPlayer.setEditable(false);
        txfBlackPlayer.setFocusable(false);
        
        // change enabled button
        btnStartGame.setEnabled(false);
        btnEndGame.setEnabled(true);
        
        Player whitePlayer = leaderboard.getPlayer(whitePlayerName);
        Player blackPlayer = leaderboard.getPlayer(blackPlayerName);
        
        Match match = new Match(whitePlayer, blackPlayer);
        
        board.startNewMatch(match);
        repaint();
    }//GEN-LAST:event_btnStartGameActionPerformed

    private void btnEndGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEndGameActionPerformed
        // add the match to leaderboard
        Match currentMatch = board.getCurrentMatch();
        if (currentMatch != null) {
            boolean matchIsWon = board.checkMatchIsWon();
            if (!matchIsWon) return;
            leaderboard.addMatch(currentMatch, true);
        }
        
        // change enabled button
        btnStartGame.setEnabled(true);
        btnEndGame.setEnabled(false);
        
        // make fields editable
        txfWhitePlayer.setEditable(true);
        txfWhitePlayer.setFocusable(true);
        txfBlackPlayer.setEditable(true);
        txfBlackPlayer.setFocusable(true);
        
        // clear fields
        txfWhitePlayer.setText("");
        txfBlackPlayer.setText("");
        
        updateLeaderboard();
        repaint();
    }//GEN-LAST:event_btnEndGameActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEndGame;
    private javax.swing.JButton btnStartGame;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea txaGames;
    private javax.swing.JTextArea txaLeaderboard;
    private javax.swing.JTextField txfBlackPlayer;
    private javax.swing.JTextField txfWhitePlayer;
    // End of variables declaration//GEN-END:variables
}
