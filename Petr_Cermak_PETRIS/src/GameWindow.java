import javax.swing.JFrame;
public class GameWindow {
    public static final int width = 500, height = 650;
    private GameBoard board;
    private GameTitle title;
    private JFrame window;
    public GameWindow() {
        window = new JFrame("Petris");
        window.setSize(width, height);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setResizable(false);
        board = new GameBoard();
        title = new GameTitle(this);
        window.addKeyListener(board);
        window.addKeyListener(title);
        window.add(title);
        window.setVisible(true);
    }
    /** starts the game
     * removes the title screen when you press enter
     * loads the gameBoard
     * adds mouse listeners for buttons
     */
    public void start() {
        window.remove(title);
        window.addMouseMotionListener(board);
        window.addMouseListener(board);
        window.add(board);
        board.startGame();
        window.revalidate();
    }
}
