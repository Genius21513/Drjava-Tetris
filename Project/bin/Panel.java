//package test;

import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


import javax.swing.JPanel;
import javax.swing.Timer;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

class Panel extends JPanel implements KeyListener, ActionListener, MouseListener, MouseMotionListener {
    Timer timer;
    int frameCounter = 0;
    int speed = 180; // how much faster blocks drop depending on level (will add levels in future)
    private boolean [] keys;
    final int BoardWidth = 400;
    final int BoardHeight = 600;
    boolean start = true;
    boolean falling = false;
    boolean collides = false;
    boolean over = false;
    boolean paused = false;
    int stage = 1;
    int level = 1;
    int rows = 10; // rows in grid of the game
    int columns = 15; // columns in grid
    int cellSize = 20; // how big the squares are in the grid
    int x = 130;
    int y = 120;
    int curBlock;
    int nextBlock;
    TetrisBlock block;
    TetrisBlock nextPreviewBlock;
    int score = 0;
    Color [][] board = new Color[10][15];

    private Rectangle stopBounds, refreshBounds;
    private int mouseX, mouseY;
    private boolean leftClick = false;
    
    //Audio
    AudioInputStream audioInputStream;
    Clip clip;
    
    
 // buttons press lapse
    private Timer buttonLapse = new Timer(300, new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            buttonLapse.stop();
        }
    });

    
 public Panel(){
                
  keys = new boolean[KeyEvent.KEY_LAST+1];
      
  setFocusable(true);
  requestFocus();
  addKeyListener(this);
  setFocusTraversalKeysEnabled(false);
    
  timer = new Timer(10, new GameTimer());
//  timer = new Timer(10, this);
  timer.setRepeats(true);
 }
    
    public void startGame() {
     if (clip != null) clip.stop();
     playSound(0);
     stopGame();
  spawnNextBlock();
  spawnCurBlock();
  over = false;
   timer.start();
    }
    
    public void playSound(int s) {
        try {
         String url;
         if (s == 0) {
          url = "data/music/running.wav";
         }else {
          url = "data/music/over.wav";
         }
         
         audioInputStream = AudioSystem.getAudioInputStream(new File(url).getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY); 
            clip.start();
        } catch(Exception ex) {
            System.out.println("Error with playing sound.");
        }
    }
    
    public void stopGame() {
        score = 0;
        
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                board[row][col] = null;
            }
        }
        timer.stop();
    }
    
    private void update() {
     
        if (paused || over) {
            return;
        }
        
        fall();
        move();
    }

    
 public int rand(int min, int max) {
  int num = (int)Math.floor(Math.random()*(max-min+1) + min);
  return num;
 }

 public void spawnNextBlock() { // randomly chooses the next block
   nextBlock = rand(1, 7);
 }
 
 public void spawnCurBlock() { // creates the new current block
   curBlock = nextBlock;
   if (falling == false) { // only create a new block if the previous one is not falling or there is no previous block
    if( curBlock == 1) {
     block = new TetrisBlock( new int[][] { {0, 0, 1}, //                       L tile
                                           {1, 1, 1} }, Color.ORANGE); //      1 represents a cell that the block will fill
                                                                       //      0 represents a cell that will not be filled
    }
    
    if( curBlock == 2) {
     block = new TetrisBlock( new int[][] { {1, 1}, //                          O tile
                                           {1, 1} }, Color.YELLOW);   
    }
    
    if (curBlock == 3) {
     block = new TetrisBlock( new int[][] { {1},
                                           {1},
                                           {1},
                                           {1} }, Color.CYAN); //                    I tile  
    }
    
    if( curBlock == 4) {
     block = new TetrisBlock( new int[][] { {1, 0, 0 }, //                      J tile
                                           {1, 1, 1 } }, Color.BLUE);
    }
    
    if (curBlock == 5) {   
     block = new TetrisBlock( new int[][] { {0, 1, 0}, //                       T tile
                                           {1, 1, 1} }, Color.MAGENTA);
    }
    
    if (curBlock == 6) {
     block = new TetrisBlock( new int [][] { {0, 1, 1}, //                      S tile
                                            {1, 1, 0} }, Color.GREEN);
    }
    
    if (curBlock == 7) {
     block = new TetrisBlock( new int[][] { { 1, 1, 0}, //                      Z tile
                                           { 0, 1, 1} }, Color.RED);
    }
   }
   
   spawnNextBlock();
   spawnNextPreview();
   falling = true; // current block is falling
   if (block == null) return;
   block.isPlaced = false; // current block has not yet been placed
   }

 public void spawnNextPreview() { // create the preview of the next block that will be shown
   
   if (nextBlock == 1) {
    nextPreviewBlock = new TetrisBlock( new int[][] { {0, 0, 0},
                                                     {0, 0, 1},
                                                     {1, 1, 1},
                                                     {0, 0, 0} }, Color.ORANGE);
   }
   
   if( nextBlock == 2) {
    nextPreviewBlock = new TetrisBlock( new int[][] { {0, 0}, 
                                                     {1, 1},
                                                     {1, 1},
                                                     {0, 0} }, Color.YELLOW);   
   }
   
   if (nextBlock == 3) {
    nextPreviewBlock = new TetrisBlock( new int[][] { {0, 1}, 
                                                       {0, 1},
                                                       {0, 1},
                                                       {0, 1} }, Color.CYAN);
  
   }
   
   if( nextBlock == 4) {
    nextPreviewBlock = new TetrisBlock( new int[][] { {0, 0, 0 },
                                                     {1, 0, 0 },
                                                     {1, 1, 1 }, 
                                                     {0, 0, 0 } }, Color.BLUE);
   }
   
   if (nextBlock == 5) {   
    nextPreviewBlock = new TetrisBlock( new int[][] { {0, 0, 0},
                                                     {0, 1, 0},
                                                     {1, 1, 1},
                                                     {0, 0, 0} }, Color.MAGENTA);
   } 
   
   if (nextBlock == 6) {
    nextPreviewBlock = new TetrisBlock( new int [][] { {0, 0, 0},
                                                      {0, 1, 1},
                                                      {1, 1, 0}, 
                                                      {0, 0, 0} }, Color.GREEN);
   }
   
   if (nextBlock == 7) {
    nextPreviewBlock = new TetrisBlock( new int[][] { { 0, 0, 0},
                                                     { 1, 1, 0},
                                                     { 0, 1, 1},
                                                     { 0, 0, 0} }, Color.RED);
   }
 }

 public void drawPreviewBlock(Graphics g) { // draw the preview block
   g.setColor(nextPreviewBlock.getColor());
   
   for (int row = 0; row < nextPreviewBlock.getHeight(); row++) {
     for (int col = 0; col < nextPreviewBlock.getWidth(); col++) {
       if (nextPreviewBlock.getShape()[row][col] == 1) {
         
         int x = col * 10;
         int y = row * 10;
         
         g.fillRect(300 + x, 130 + y, 10, 10);
       }
     }
   }
 }

 public void drawBlock(Graphics g) { // draw the current block
   g.setColor(block.getColor());  
   for (int row = 0; row < block.getHeight(); row++) {
     for (int col = 0; col < block.getWidth(); col++) {
       if (block.getShape()[row][col] == 1) {
         
       int gridX = col * cellSize;
        
       int gridY = row * cellSize;
          
        g.fill3DRect(gridX + block.getX(), gridY + block.getY() - (block.getHeight()*cellSize), cellSize, cellSize, true);
       }
      }
   }
 }

 public void drawPlacedBlocks(Graphics g) { // draw the placed blocks
     for (int r = 0; r < 15; r++) {
       for (int c = 0; c < 10; c++) {
       Color color = board[c][r];
       
       if (color != null) {
         int x = c*cellSize;
         int y = r*cellSize;
         
         g.setColor(color);
         g.fill3DRect(x + 50, y + 120, cellSize, cellSize, true);
       }
     }
   }
 }

 public void newPlacedBlock() { // creates new placed blocks that can no longer move
   for(int r = 0; r < block.getHeight(); r++) {
     for (int c = 0; c < block.getWidth(); c++) {
       
       if(block.getShape()[r][c] == 1) {
         board[c + block.getGridX()][r + block.getGridY() - block.getHeight()] = block.getColor();
       }
     }
   }
 }
 
 public void nextBlock() { // sets up so that new block can spawn and then spawns it
   falling = false;
   if (block == null)  return;
   block.isPlaced = true;
   spawnCurBlock();
 }
 
 public void fall() { // block moves down
   if (falling == true) {
      frameCounter += 1;
      if (frameCounter % speed == 0) {
       block.drop(); 
      }
   }
 }
 
 public boolean hitBottom() {
   if  (block.getGridY() == 15) {
     return true;
   }
   else {
    return false;
   }
 }
   
 
 public boolean hitBlock() {
   for (int col = 0; col < block.getWidth(); col++) {
     for (int row = block.getHeight() - 1; row >= 0;  row--) {
       
       if (block.getShape()[row][col] != 0) {
         int x = col + block.getGridX(); 
         int y = row + block.getGridY() - block.getHeight() + 1;
       if (y <= 14 && x < 9 && x > 0) {
        if (board[x][y] != null) {
           return true;
         }
         }
       }
     } 
   }
   return false;
 }
 
 public boolean hitSide(int c) {
  for (int col = 0; col < block.getWidth(); col++) {
      for (int row = block.getHeight() - 1; row >= 0;  row--) {
        
        if (block.getShape()[row][col] != 0) {
         int x = col + block.getGridX();
         int y = row + block.getGridY() - block.getHeight();
         
         if (block.getGridX() + block.getWidth() - 1 <= 8 && block.getGridX() >= 1) {
                                                       
           if (board[x + c][y] != null) {
             return true;
           }
         }
        }
      }
     }
     return false;
 }
 
 public void clearLines() { // finds the complete line
    boolean completeLine;
    int scoreMultiplier = 0;
    int score2 = 0; // score received from lines that will be multipled by num of lines completed at once
    
    for (int rows = 14; rows >= 0; rows--) {
       
       completeLine = true; 
       scoreMultiplier++;
       score2 += 100*scoreMultiplier;
       
       for(int col = 0; col < 10; col++) {
         if(board[col][rows] == null) {
           completeLine = false;
           break;
         }
       }
       if (completeLine) {
         score += score2;
         clearLine(rows);
         moveDown(rows);
         clearLine(0);
       }
    }
 }
 
 public void clearLine(int r) { // clears the complete line(s)
        
       for (int i = 0; i < 10; i++) {
         board[i][rows] = null;
       }
     }
 
 public void moveDown(int r) { // moves block down when a line is cleared
    for(int row = r; row > 0; row--) {
      for (int col = 0; col < 10; col++) {
        
        board[col][row] = board[col][row - 1];
        board[col][row-1] = null;
      }
    }
 }
 
 public void nextLevel() {
    if (score - (score - 200) % 200 == 0 && score > 0) {
      level++;
      speed += 50;
    }
 }
 
 public void gameOver(){
   for (int i = 0; i < 10; i++) {
     if (board[i][0] != null) {
       over = true;
     }
   }
 }
 
 public void pause() {
    if (paused) {      
     timer.start();
    } else {      
     timer.stop();
    }
    paused = !paused;
  }
       
   
 /*public void nextStage() {
    if (stage == 3) {
    timer.stop();
  }
      if (stage == 2) {
    timer.start();
      }
  if(keys[KeyEvent.VK_ENTER]) {
  if (stage < 3) {
    stage++;
  }
  if (stage == 3) {
    stage = 1;
  }
  }
 }*/
      
   public void move() {
   
      gameOver();
      nextLevel();
      clearLines();
      if (hitBottom() || hitBlock()) {
        newPlacedBlock(); // the block is now placed, create a new block, and add to the score
        nextBlock();
        score += 2;
      }
      
      else { // if previous are all false, move the block down
       fall();
      }
      
//      if (keys[KeyEvent.VK_LEFT]) {
//        if (block.getGridX() != 0) {
//          if (!hitSide(-1)) { // if left edge of block as not hit something, move left
//           block.left();
//          }
//        }
//      }
//      
//      if (keys[KeyEvent.VK_RIGHT]) {
//        if(block.getGridX() + block.getWidth() - 1 != 9) {
//          if (!hitSide(1)) { // if right edge of block has not hit something, move right
//          block.right();
//          }
//        }
//      }
//      
//      if(keys[KeyEvent.VK_DOWN]) {
//        block.drop();
//      }
//      
//      if(keys[KeyEvent.VK_UP]) { // rotate
//        block.rotate();
//      }
//      
//      if(keys[KeyEvent.VK_R]) {
//       pause();
//      }
//            
//      if(keys[KeyEvent.VK_SPACE]) {
//       nextBlock();
//       startGame();
//      }
   }
   
 public void actionPerformed(ActionEvent e){ 
  // nextStage(); 
//   fall();
//   move();
   repaint();
 }
 
 @Override
 public void keyReleased(KeyEvent ke){
//   int key = ke.getKeyCode();
//   keys[key] = false;
  
  if (ke.getKeyCode() == KeyEvent.VK_DOWN) {
   fall();
        }
 } 
 
 @Override
 public void keyPressed(KeyEvent ke) {
//   int key = ke.getKeyCode();
//   keys[key] = true;
       
       if (ke.getKeyCode() == KeyEvent.VK_LEFT) {
         if (block.getGridX() != 0) {
           if (!hitSide(-1)) { // if left edge of block as not hit something, move left
            block.left();
           }
         }
       }
       
       if (ke.getKeyCode() == KeyEvent.VK_RIGHT) {
         if(block.getGridX() + block.getWidth() <= 9) {
           if (!hitSide(1)) { // if right edge of block has not hit something, move right
           block.right();
           }
         }
       }
       
       if (ke.getKeyCode() == KeyEvent.VK_DOWN) {
         block.drop();
       }
       
       if (ke.getKeyCode() == KeyEvent.VK_UP) {
        if(block.getGridX() + block.getHeight() > 10) {
         return;
        }
        if (!hitSide(-1) && !hitSide(1)) {
         block.rotate();   
          }         
       }
       
       if (ke.getKeyCode() == KeyEvent.VK_R) {
        pause();
       }
             
       if (ke.getKeyCode() == KeyEvent.VK_SPACE) {
        nextBlock();
        startGame();
       }
 }
 
 @Override
 public void keyTyped(KeyEvent ke){
 }
 
 @Override
 public void paint(Graphics g) {  
    Font title = new Font("title", Font.BOLD, 70);
    Font font1 = new Font("font1", Font.BOLD, 12);
    Font font2 = new Font("font2", Font.PLAIN, 10);
    
  /*  if (stage == 1 ) {
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, BoardWidth, BoardHeight);
    
    g.setColor(Color.DARK_GRAY);
    g.setFont(title);
    g.drawString("TETRIS", 65, 200);
    
    g.setColor(Color.WHITE);
    g.drawString("TETRIS", 60, 200);
    
    g.setColor(Color.MAGENTA);
    for(int x = 0; x < 4; x++) {
      g.fillRect(100 + 30*x, 500, 30, 30);
      
      g.setColor(Color.DARK_GRAY);
      g.drawRect(100 + 30*x, 500, 30, 30);
    }
    }*/
    
    //if (stage == 2) {
  
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, BoardWidth, BoardHeight);
    
    drawPlacedBlocks(g);
    drawBlock(g);
    drawPreviewBlock(g);
     
    g.setColor(Color.DARK_GRAY);
    for(int y = 0; y < columns; y++) {
     for(int x = 0; x < rows; x++) {
      g.drawRect(x*cellSize + 50, y*cellSize + 120, cellSize, cellSize); // grid squares
     }
    }
    g.setColor(Color.WHITE);
    g.drawRect(50, 120, 200, 300); // grid border
    
    g.setColor(Color.BLACK);
    g.fillRect(0, 20, 400, 100); // cover for top of screen
    
    g.setFont(title);
       
    g.setColor(Color.DARK_GRAY);
    g.drawString("TETRIS", 65, 80);
    //                                  title
    g.setColor(Color.WHITE);
    g.drawString("TETRIS", 60, 80);
    
    g.setFont(font1);
    g.drawString("Score: " + score, 52, 440); // score
    
    g.drawString("Level: " + level, 207, 440);
    
    g.drawRect(285, 120, 60, 60); // next block area
    
    g.setFont(font2);
        
    g.drawString ("left arrow  -  move left", 265, 250); // controls
    g.drawString("right arrow - move right", 265, 270);
    g.drawString("up arrow - rotate", 265, 290);
    g.drawString("down arrow - drop", 265, 310);
    g.drawString("restart - press SPACE", 265, 330);
    g.drawString("stop/resume - press R", 265, 350);
  
    if (over) {
   clip.stop();
   playSound(1);
      timer.stop();
      g.setColor(Color.BLACK);
      g.fillRect(0, 0, 400, 600);
      g.setColor(Color.WHITE);
      g.setFont(title);
      g.drawString("GAME", 80, 200);
      g.drawString("OVER", 85, 300);
      g.setFont(font1);
      g.drawString("Score: " + score, 150, 350);
      g.drawString("Press SPACE to restart.", 120, 380);
    }
 }
 
 class GameTimer implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            update();
            repaint();
        }

 }   
 

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            leftClick = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            leftClick = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}