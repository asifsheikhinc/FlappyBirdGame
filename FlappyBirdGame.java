import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.prefs.Preferences; // হাই স্কোর সেভ করার জন্য

public class FlappyBirdGame extends JPanel implements ActionListener, KeyListener {
    
    private final int WIDTH = 500, HEIGHT = 500;
    
    // বার্ড ফিজিক্স
    private float birdY = 240, velocity = 0;
    private final float gravity = 0.52f, jumpPower = -9.2f;
    private final int BIRD_W = 40, BIRD_H = 30;
    
    private ArrayList<Rectangle> pipes = new ArrayList<>();
    private Timer timer = new Timer(16, this);
    private Random rand = new Random();
    
    private int score = 0, highSc = 0;
    private boolean isGameOver = false, isStarted = false;

    // Preferences অবজেক্ট তৈরি
    private Preferences prefs = Preferences.userNodeForPackage(FlappyBirdGame.class);

    public FlappyBirdGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);
        
        // গেম শুরুতেই আগের সেভ করা হাই স্কোর লোড করা
        highSc = prefs.getInt("highScore", 0); 
        
        resetGame();
        timer.start();
    }

    private void addPipe() {
        int gap = 155; 
        int width = 75;
        int h = 60 + rand.nextInt(180);
        int x = pipes.isEmpty() ? 600 : pipes.get(pipes.size() - 1).x + 280;
        
        pipes.add(new Rectangle(x, 0, width, h)); // উপরের পাইপ
        pipes.add(new Rectangle(x, h + gap, width, HEIGHT - h - gap)); // নিচের পাইপ
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted && !isGameOver) {
            velocity += gravity;
            birdY += velocity;

            for (Rectangle p : pipes) p.x -= 5;

            if (!pipes.isEmpty() && pipes.get(0).x < -80) {
                pipes.remove(0); pipes.remove(0);
                addPipe();
                score++;
                
                // স্কোর বাড়লে সাথে সাথে হাই স্কোর আপডেট এবং সেভ করা
                if (score > highSc) {
                    highSc = score;
                    prefs.putInt("highScore", highSc);
                }
            }

            Rectangle birdHitbox = new Rectangle(75, (int) birdY, BIRD_W, BIRD_H);
            for (Rectangle p : pipes) {
                if (p.intersects(birdHitbox)) isGameOver = true;
            }
            if (birdY > HEIGHT - 40 || birdY < 0) isGameOver = true;
        }
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // ১. ব্যাকগ্রাউন্ড
        GradientPaint sky = new GradientPaint(0, 0, new Color(135, 206, 235), 0, HEIGHT, new Color(224, 247, 250));
        g2.setPaint(sky);
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        // ২. পাইপ ডিজাইন
        for (Rectangle p : pipes) {
            g2.setColor(new Color(40, 180, 40));
            g2.fillRoundRect(p.x, p.y, p.width, p.height, 5, 5);
            
            g2.setColor(new Color(30, 150, 30));
            if (p.y == 0) { 
                g2.fillRect(p.x - 5, p.height - 20, p.width + 10, 20);
                g2.setColor(Color.BLACK);
                g2.drawRect(p.x - 5, p.height - 20, p.width + 10, 20);
            } else { 
                g2.fillRect(p.x - 5, p.y, p.width + 10, 20);
                g2.setColor(Color.BLACK);
                g2.drawRect(p.x - 5, p.y, p.width + 10, 20);
            }
            
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(p.x, p.y, p.width, p.height, 5, 5);
        }

        // ৩. মাটি
        g2.setColor(new Color(222, 216, 149));
        g2.fillRect(0, HEIGHT - 30, WIDTH, 30);
        g2.setColor(new Color(115, 191, 46));
        g2.fillRect(0, HEIGHT - 35, WIDTH, 5);

        // ৪. বার্ড
        drawBird(g2, 75, (int) birdY);

        // ৫. স্কোর কার্ড এবং হাই স্কোর প্রদর্শন
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial Black", Font.BOLD, 22));
        g2.drawString("Score: " + score, 20, 40);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("Best: " + highSc, 20, 65);

        if (isGameOver) {
            showMenu(g2, "GAME OVER", "Final Score: " + score + " | Best: " + highSc);
        } else if (!isStarted) {
            showMenu(g2, "FLAPPY BIRD", "SPACE TO START");
        }
    }

    private void drawBird(Graphics2D g2, int x, int y) {
        g2.setColor(new Color(255, 235, 59));
        g2.fillOval(x, y, BIRD_W, BIRD_H);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(x, y, BIRD_W, BIRD_H);

        g2.setColor(Color.WHITE);
        g2.fillOval(x + 22, y + 4, 14, 12);
        g2.setColor(Color.BLACK);
        g2.fillOval(x + 28, y + 7, 5, 5);

        g2.setColor(new Color(255, 255, 255, 180));
        g2.fillOval(x + 5, y + 12, 18, 12);
        g2.setColor(Color.BLACK);
        g2.drawOval(x + 5, y + 12, 18, 12);

        g2.setColor(new Color(255, 87, 34)); 
        int[] bx = {x + 32, x + 48, x + 32, x + 38};
        int[] by = {y + 14, y + 20, y + 28, y + 20};
        g2.fillPolygon(bx, by, 4);
        g2.setColor(Color.BLACK);
        g2.drawPolygon(bx, by, 4);
    }

    private void showMenu(Graphics2D g2, String t, String s) {
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(0, 0, WIDTH, HEIGHT);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial Black", Font.BOLD, 35));
        g2.drawString(t, 120, 220);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        g2.drawString(s, 110, 260);
    }

    private void resetGame() {
        birdY = 240; velocity = 0; score = 0;
        pipes.clear();
        addPipe(); addPipe();
        isGameOver = false; isStarted = false;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (isGameOver) resetGame();
            else {
                isStarted = true;
                velocity = jumpPower;
            }
        }
    }
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame f = new JFrame("Flappy Bird Ultimate");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new FlappyBirdGame());
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
