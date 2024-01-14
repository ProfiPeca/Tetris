import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.Timer;
public class GameTitle extends JPanel implements KeyListener {
    private static final long serialVersionUID = 1L;
    private BufferedImage titleScreen;
    private GameWindow window;
    private BufferedImage[] playButton = new BufferedImage[2];
    private Timer timer;
    public GameTitle(GameWindow window) {
        titleScreen = ImageLoader.loadImage("/Petris.png");
        timer = new Timer(1000 / 60, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        });
        timer.start();
        this.window = window;
    }
    /**
     * renders main menu
     *
     * @return game main menu
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, GameWindow.width, GameWindow.height);
        g.drawImage(titleScreen, GameWindow.width / 2 - titleScreen.getWidth() / 2, 30 - titleScreen.getHeight() / 2 + 150, null);
        g.setColor(Color.WHITE);
        g.drawString("Petrův velmi kvalitní tetris", 150, GameWindow.height / 2 + 100);
    }

    /**
     * starts the game by pressing enter, restarts the game if its already running
     */
    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
            window.start();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
