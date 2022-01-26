//package test;

import javax.swing.*;

public class Game extends JFrame {
 
    public static final int WIDTH = 400, HEIGHT = 600;

    private Panel panel;
    private Title title;
    private JFrame window;
 
    public Game() {
      window = new JFrame("Tetris");
         window.setSize(WIDTH, HEIGHT);
         window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         window.setLocationRelativeTo(null);
         window.setResizable(false);

         panel = new Panel();
         title = new Title(this);

         window.addKeyListener(panel);
         window.addKeyListener(title);
         
         window.add(title);
         window.setVisible(true);
         
    }
   

    public void startTetris() {  
        window.remove(title);
        window.addMouseMotionListener(panel);
        window.addMouseListener(panel);
        window.add(panel);
        panel.startGame();
        window.revalidate();
    }

    public static void main(String[] args) {
        new Game();
    }
}


