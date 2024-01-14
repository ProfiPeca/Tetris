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
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;
public class GameBoard  extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
    private BufferedImage pause, restart;

    /**
     * units that are used to properly draw the board with paintComponent
     */
    private static int boardHeight = 20, boardWidth = 10, blockSize = 30;

    // field
    private Color[][] board = new Color[boardHeight][boardWidth];

    // array with all the possible shapes
    private BlockShape[] shapes = new BlockShape[7];

    /**
     * current shape that is chosen, and the one that is coming next
     */
    private static BlockShape currentShape, nextShape;
    /**
     * game loop
     */
    private Timer looper;
    private int FPS = 60;
    private int delay = (1000 / FPS);
    /**
     * detects mouse location and checks if the left button is pressed in order to reset/pause the game
     */
    private int mouseX, mouseY;
    private boolean leftClick = false;
    private Rectangle stopBounds, restartButtonPosition;
    /**
     * gamePaused-checks if the game is paused, when its paused, the currentShape wont move
     * gameOver-checks if there is a block on the top of the board, if true, the game ends and you need to reset it
     */
    private boolean gamePaused = false;
    private boolean gameOver = false;
    /**
     * list of colors used on the blocks
     */
    private Color[] colors = {Color.CYAN, Color.MAGENTA, Color.ORANGE, Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW};
    private Random random = new Random();
    // buttons press lapse
    private Timer buttonLapse = new Timer(300, new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            buttonLapse.stop();
        }
    });

    /**
     * each block placed awards one point(displays*100)
     */
    private int gameScore = 0;

    public GameBoard() {

        pause = ImageLoader.loadImage("/pause.png");
        restart = ImageLoader.loadImage("/restart.png");

        mouseX = 0;
        mouseY = 0;

        stopBounds = new Rectangle(350, 500, pause.getWidth(), pause.getHeight() + pause.getHeight() / 2);
        restartButtonPosition = new Rectangle(350, 500 - restart.getHeight() - 20, restart.getWidth(), restart.getHeight() + restart.getHeight() / 2);

        /** creates the gameLoop
         */
        looper = new Timer(delay, new GameLoop());
        /** generates the shapes that are used in the board
         */
        shapes[0] = new BlockShape(new int[][]{{1, 1, 1, 1}}, this, colors[0]);
        shapes[1] = new BlockShape(new int[][]{{1, 1, 1}, {0, 1, 0}}, this, colors[1]);
        shapes[2] = new BlockShape(new int[][]{{1, 1, 1}, {1, 0, 0}}, this, colors[2]);
        shapes[3] = new BlockShape(new int[][]{{1, 1, 1}, {0, 0, 1}}, this, colors[3]);
        shapes[4] = new BlockShape(new int[][]{{0, 1, 1}, {1, 1, 0}}, this, colors[4]);
        shapes[5] = new BlockShape(new int[][]{{1, 1, 0}, {0, 1, 1}}, this, colors[5]);
        shapes[6] = new BlockShape(new int[][]{{1, 1}, {1, 1}}, this, colors[6]);
    }

    /**
     * toggles running of the game
     */
    private void update() {
        if (stopBounds.contains(mouseX, mouseY) && leftClick && !buttonLapse.isRunning() && !gameOver) {
            buttonLapse.start();
            gamePaused = !gamePaused;
        }
        //resets the game if you click the "reset" button
        if (restartButtonPosition.contains(mouseX, mouseY) && leftClick) {
            startGame();
        }
        //prevents the game from moving while the game is paused, or if the player has lost
        if (gamePaused || gameOver) {
            return;
        }
        //updates only the moving shape
        currentShape.update2();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                if (board[row][col] != null) {
                    g.setColor(board[row][col]);
                    g.fillRect(col * blockSize, row * blockSize, blockSize, blockSize);
                }
            }
        }
        g.setColor(nextShape.getColor());
        for (int row = 0; row < nextShape.getCoords().length; row++) {
            for (int col = 0; col < nextShape.getCoords()[0].length; col++) {
                if (nextShape.getCoords()[row][col] != 0) {
                    g.fillRect(col * blockSize + 320, row * blockSize + 50, blockSize, blockSize);
                }
            }
        }
        currentShape.render(g);

        if (stopBounds.contains(mouseX, mouseY)) {
            g.drawImage(pause.getScaledInstance(pause.getWidth() + 3, pause.getHeight() + 3, BufferedImage.SCALE_DEFAULT), stopBounds.x + 3, stopBounds.y + 3, null);
        } else {
            g.drawImage(pause, stopBounds.x, stopBounds.y, null);
        }

        if (restartButtonPosition.contains(mouseX, mouseY)) {
            g.drawImage(restart.getScaledInstance(restart.getWidth() + 3, restart.getHeight() + 3,
                    BufferedImage.SCALE_DEFAULT), restartButtonPosition.x + 3, restartButtonPosition.y + 3, null);
        } else {
            g.drawImage(restart, restartButtonPosition.x, restartButtonPosition.y, null);
        }
        /**
         * shows messages that are displayed ingame if certain conditions are met
         */
        if (gamePaused) {
            String gamePausedString = "GAME PAUSED";
            g.setColor(Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString(gamePausedString, 35, GameWindow.height / 2);
        }
        if (gameOver) {
            String gameOverString = "GAME OVER";
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString(gameOverString, 50, GameWindow.height / 2);
        }
        g.setColor(Color.WHITE);

        g.setFont(new Font("Arial", Font.BOLD, 20));

        g.drawString("SCORE", GameWindow.width - 125, GameWindow.height / 2);
        g.drawString(gameScore * 100 + "", GameWindow.width - 125, GameWindow.height / 2 + 30);

        /**
         * draws the lines of rows and columns
         */
        g.setColor(Color.GRAY);
        for (int i = 0; i <= boardHeight; i++) {
            g.drawLine(0, i * blockSize, boardWidth * blockSize, i * blockSize);
        }
        for (int j = 0; j <= boardWidth; j++) {
            g.drawLine(j * blockSize, 0, j * blockSize, boardHeight * blockSize);
        }
    }

    public void setNextShape() {
        int blockShapeNumber = random.nextInt(shapes.length);
        int colorIndex = blockShapeNumber;
        nextShape = new BlockShape(shapes[blockShapeNumber].getCoords(), this, colors[colorIndex]);
    }

    /**
     * spawns the current shape on top of the board
     * if unable to do so, ends the game
     */
    public void setCurrentShape() {
        currentShape = nextShape;
        setNextShape();
        for (int row = 0; row < currentShape.getCoords().length; row++) {
            for (int col = 0; col < currentShape.getCoords()[0].length; col++) {
                if (currentShape.getCoords()[row][col] != 0) {
                    if (board[currentShape.getY() + row][currentShape.getX() + col] != null) {
                        gameOver = true;
                    }
                }
            }
        }
    }

    public Color[][] getBoard() {
        return board;
    }

    //region PlayerInputs
    @Override
    public void keyPressed(KeyEvent e) {
        //rotates the block
        if (e.getKeyCode() == KeyEvent.VK_W) {
            currentShape.rotateShape();
        }
        //makes the block move right
        if (e.getKeyCode() == KeyEvent.VK_D) {
            currentShape.setMovementX(1);
        }
        //makes the block move left
        if (e.getKeyCode() == KeyEvent.VK_A) {
            currentShape.setMovementX(-1);
        }
        //makes the block move downwards in increased speed
        if (e.getKeyCode() == KeyEvent.VK_S) {
            currentShape.speedUp();
        }
    }

    @Override
    //makes it so the block wont keep falling fast
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_S) {
            currentShape.speedDown();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    //endregion

    /**
     * starts the game after you press enter
     * stopGame();-sets the score to 0
     * setNextShape();-sets the shape which will be displayed as the next block that will be used
     * setCurrentShape();-sets the current shape that is affected by your controls
     */
    public void startGame() {
        stopGame();
        setNextShape();
        setCurrentShape();
        gameOver = false;
        looper.start();
    }

    public void stopGame() {
        gameScore = 0;
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                board[row][col] = null;
            }
        }
        looper.stop();
    }

    /**
     * loops the update() and
     */
    class GameLoop implements ActionListener {

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

    public void addScore() {
        gameScore++;
    }

    public static int getBlockSize() {
        return blockSize;
    }
}
