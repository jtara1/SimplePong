/*
 * This 6 year old tutorial was the basis of this project:
 * http://compsci.ca/v3/viewtopic.php?t=25991
 * @author: jtara1
 */
package gltest;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.*;
import java.util.ArrayList;
import javax.swing.*;


public class GLTest {
    
    private final JFrame frame = new JFrame();
    private InputHandler input;
    private Insets insets;
    
    private final Integer[] winSize = {1280, 720}; // window size
    private final String winTitle = "Pong"; // window title
    private Dimension frame_dim;
    private BufferedImage backBuffer;
    private final Boolean debug;
    
    private final long fps = 60; // frames per second
    private boolean isRunning = true;
    private ArrayList<GameObject> gameObjects = new ArrayList();
    private Paddle paddle;
    private Ball ball;


    
    /**
     * GLTest constructor
     * @param dbg: set debug mode
     */
    public GLTest(Boolean dbg) {        
        if (dbg) {
            debug = dbg;
        }
        else { 
            debug = false;
        }
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Boolean dbg = true;
        
        GLTest glTest = new GLTest(dbg = dbg);
        glTest.run();
        System.exit(0);
    }
    
    
    void initialize() {
                 
        frame.setTitle(winTitle); 
        frame.setSize(winSize[0], winSize[1]); 
        frame.setLocationRelativeTo(null); // set location to center of main monitor
        frame.setResizable(false); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
        // setVisible must be true before we can get insets
        insets = frame.getInsets();
        frame.setSize(winSize[0] + insets.right + insets.left, 
                winSize[1] + insets.top + insets.bottom);
        frame_dim = frame.getSize();
        if (debug) {
            System.out.println(insets.toString());
        }
        
        backBuffer = new BufferedImage(winSize[0], winSize[1], BufferedImage.TYPE_INT_RGB);
        input = new InputHandler(frame);
        
        // gameObjects
        paddle = new Paddle(frame_dim, insets);
        ball = new Ball(frame_dim);
        gameObjects.add(paddle);
        gameObjects.add(ball);
    }
    
    
    
    /** 
     * This method starts the game and runs it in a loop 
     */ 
    public void run() 
    { 
        initialize(); 
        while(isRunning) {
            long time = System.currentTimeMillis(); 

            update(); 
            draw(); 

            //  delay for each frame  -   time it took for one frame 
            time = (1000 / fps) - (System.currentTimeMillis() - time); 

            if (time > 0) {
                    try {
                        if (debug) {
                            System.out.printf("Waited %d milliseconds before next frame.\n", time);
                        }
                        Thread.sleep(time); 
                    }
                    catch(Exception e){} 
            } 
            else {
                if (debug) {
                    System.out.printf("time = %d; We lagging?\n", time);
                }
            }
        } 
        frame.setVisible(false); 
    }
    
    void update() {
        // new coordinates for paddle and ball
        int paddleX = paddle.getX() + paddle.getVelocityX();
        int paddleY = paddle.getY();
        int paddleWidth = paddle.getWidth();
        int ballX = ball.getX() + ball.getVelocityX();
        int ballY = ball.getY() + ball.getVelocityY();
        int ballDiameter = ball.getDiameter();
        int ballRadius = ball.getRadius();
        
        if (input.isKeyDown(KeyEvent.VK_RIGHT)) {
            if ((paddleX + paddle.getWidth()) <= winSize[0]) {
                paddle.setX(paddleX);
            }
        }
        if (input.isKeyDown(KeyEvent.VK_LEFT)) {
            if (paddleX > 0) {
                paddle.setX(paddleX - paddle.getVelocityX() * 2);
            }
        }
        
        // ball & paddle collided
        if ((ballY + ballDiameter) == paddleY && (paddleX <= (ballX + ballRadius) && 
                (paddleX + paddleWidth) >= (ballX + ballRadius))) {
            ball.reflect("bottom");
        }
        
        
        // ball movement & wall collision
        // check for out of bounds
        if (ballX <= 0 || (ballX + ballDiameter) >= winSize[0]) {
            ball.reflect("left");
        }
        else if (ballY <= 0) {
            ball.reflect("top");
        }
        else if ((ballY + ballDiameter) >= winSize[1]) {
            System.exit(0);
        }
    
        ball.setX(ballX);
        ball.setY(ballY);
    }
    
    void draw() {
        Graphics g = frame.getGraphics(); 
        Graphics bbg = backBuffer.getGraphics(); 

        bbg.setColor(Color.WHITE); 
        bbg.fillRect(0, 0, winSize[0], winSize[1]); 

        bbg.setColor(Color.BLACK); 
        
        // draw paddle
        if (debug) {
            System.out.printf("x = %d, y = %d, w = %d, h = % d\n",
                paddle.getX(), paddle.getY(), paddle.getWidth(), paddle.getHeight());
        }
        paddle.draw(bbg);
        
        // draw ball
        ball.draw(bbg);
            
        
        g.drawImage(backBuffer, insets.left, insets.top, frame);
//        g.drawImage(backBuffer, 0, 0, frame);
    }
}