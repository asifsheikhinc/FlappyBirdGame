import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBirdGame extends JPanel implements ActionListener, KeyListener {
    
    // variables for bird
    int birdY = 240;
    int velocity = 0;
    
    // variables for pipes   
    ArrayList<Rectangle> pipeList = new ArrayList<>();
    Timer gameTimer = new Timer(22, this); // speed adjusted slightly
    
    int score = 0;
    boolean isGameOver = false;

    public FlappyBirdGame() {
        // adding initial pipes
        createNewPipe();
        createNewPipe();
        
        gameTimer.start();
        setFocusable(true);
        addKeyListener(this);
    }

    // method to create pipes
    public void createNewPipe() {
        Random rand = new Random();
        int space = 160; // gap between pipes
        int width = 55;
        int h = 50 + rand.nextInt(200); // random height
        
        int xLocation = 500;
        if (!pipeList.isEmpty()) {
            xLocation = pipeList.get(pipeList.size() - 1).x + 300;
        }
        
        // bottom pipe
        pipeList.add(new Rectangle(xLocation, 500 - h, width, h));
        // top pipe
        pipeList.add(new Rectangle(xLocation, 0, width, 500 - h - space));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // background sky
        g.setColor(new Color(135, 206, 235)); // Sky Blue
        g.fillRect(0, 0, 500, 500);

        // draw pipes
        g.setColor(new Color(34, 139, 34)); // Dark Green for pipes
        for (Rectangle r : pipeList) {
            g.fillRect(r.x, r.y, r.width, r.height);
            g.setColor(Color.BLACK);
            g.drawRect(r.x, r.y, r.width, r.height); // border
            g.setColor(new Color(34, 139, 34));
        }

        // --- DRAWING THE BIRD SHAPE ---
        int bX = 50; // bird X position
        int bSize = 30; // bird Body size

        // 1. Bird Body (Oval)
        g.setColor(Color.YELLOW);
        g.fillOval(bX, birdY, bSize, bSize);

        // 2. Beak (Triangle using fillPolygon)
        g.setColor(Color.RED);
        int[] beakX = {bX + bSize, bX + bSize + 10, bX + bSize};
        int[] beakY = {birdY + 10, birdY + 15, birdY + 20};
        g.fillPolygon(beakX, beakY, 3);

        // 3. Eye (White and Black Circles)
        g.setColor(Color.WHITE);
        g.fillOval(bX + 18, birdY + 6, 10, 10);
        g.setColor(Color.BLACK);
        g.fillOval(bX + 22, birdY + 9, 4, 4);

        // 4. Wing (Small Oval)
        g.setColor(Color.ORANGE);
        g.fillOval(bX + 2, birdY + 12, 12, 8);
        // ------------------------------

        // score display
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.drawString("Score: " + score, 20, 40);

        if (isGameOver) {
            g.setColor(Color.RED);
            g.drawString("Game Over!", 180, 230);
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.drawString("Press Space to Reset", 150, 260);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isGameOver) {
            velocity = velocity + 1; // gravity effect
            birdY = birdY + velocity;

            // moving pipes
            for (int i = 0; i < pipeList.size(); i++) {
                Rectangle p = pipeList.get(i);
                p.x = p.x - 5; // pipe moving speed

                // checking collision
                // Using bSize+10 for X to include the beak in collision
                if (p.intersects(new Rectangle(50, birdY, 40, 30))) { 
                    isGameOver = true;
                }
            }

            // adding new pipes and removing old ones
            if (pipeList.get(0).x < -60) {
                pipeList.remove(0);
                pipeList.remove(0);
                createNewPipe();
                score++;
            }

            // check if bird hits boundary
            if (birdY > 470 || birdY < 0) {
                isGameOver = true;
            }
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (isGameOver) {
                // reset all values
                birdY = 240;
                velocity = 0;
                score = 0;
                pipeList.clear();
                createNewPipe();
                createNewPipe();
                isGameOver = false;
            } else {
                velocity = -12; // jump power
            }
        }
    }

    public void keyTyped(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Student Project: Flappy Bird");
        FlappyBirdGame game = new FlappyBirdGame();
        frame.add(game);
        frame.setSize(500, 500);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}