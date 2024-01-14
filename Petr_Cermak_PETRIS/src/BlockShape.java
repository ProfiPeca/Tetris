import java.awt.Color;
import java.awt.Graphics;
public class BlockShape {
    private Color color;
    /**
     * used as coordinates of block
     */
    private int x, y;
    private long time, lastTime;
    /**
     * speed of falling block
     * normal-block falls rather slowly, used in keyReleased
     * fast-used in keyPressed
     */
    private int normal = 600, fast = 50;
    private int delay;
    private int[][] coords;
    private int[][] reference;
    private int movementX;
    private GameBoard board;
    private boolean collision = false, moveX = false;
    private int timePassedFromCollision = -1;
    private int[][] rotatedShape;

    public BlockShape(int[][] coords, GameBoard board, Color color) {
        this.coords = coords;
        this.board = board;
        this.color = color;
        movementX = 0;
        x = 4;
        y = 0;
        delay = normal;
        time = 0;
        lastTime = System.currentTimeMillis();
        reference = new int[coords.length][coords[0].length];
        System.arraycopy(coords, 0, reference, 0, coords.length);
    }
    long deltaTime;
    /**updates the board
     */
    public void update2() {
        moveX = true;
        deltaTime = System.currentTimeMillis() - lastTime;
        time += deltaTime;
        lastTime = System.currentTimeMillis();

        if (collision && timePassedFromCollision > 500) {
            for (int row = 0; row < coords.length; row++) {
                for (int col = 0; col < coords[0].length; col++) {
                    if (coords[row][col] != 0) {
                        board.getBoard()[y + row][x + col] = color;
                    }
                }
            }
            checkLine();
            board.addScore();
            board.setCurrentShape();
            timePassedFromCollision = -1;
        }
        /**
         * keeps track of movement in the row the block is at, to prevent block overlapping or the block escaping border
         */
        if (!(x + movementX + coords[0].length > 10) && !(x + movementX < 0)) {
            for (int row = 0; row < coords.length; row++) {
                for (int col = 0; col < coords[row].length; col++) {
                    if (coords[row][col] != 0) {
                        if (board.getBoard()[y + row][x + movementX + col] != null) {
                            moveX = false;
                        }
                    }
                }
            }
            if (moveX) {
                x += movementX;
            }
        }
        /**
         * checks the validity of
         */
        if (timePassedFromCollision == -1) {
            if (!(y + 1 + coords.length > 20)) {

                for (int row = 0; row < coords.length; row++) {
                    for (int col = 0; col < coords[row].length; col++) {
                        if (coords[row][col] != 0) {
                            if (board.getBoard()[y + 1 + row][x + col] != null) {
                                collision();
                            }
                        }
                    }
                }
                if (time > delay) {
                    y++;
                    time = 0;
                }
            } else {
                collision();
            }
        } else {
            timePassedFromCollision += deltaTime;
        }
        movementX = 0;
    }

    private void collision() {
        collision = true;
        timePassedFromCollision = 0;
    }

    public void render(Graphics g) {

        g.setColor(color);
        for (int row = 0; row < coords.length; row++) {
            for (int col = 0; col < coords[0].length; col++) {
                if (coords[row][col] != 0) {
                    g.fillRect(col * 30 + x * 30, row * 30 + y * 30, GameBoard.getBlockSize(), GameBoard.getBlockSize());
                }
            }
        }
    }

    /**
     * checks if line of blocks is full, then removes it and replaces it with the line of blocks that is above
     */
    private void checkLine() {
        int size = board.getBoard().length - 1;

        for (int i = board.getBoard().length - 1; i > 0; i--) {
            //count-counts the number of blocks in the row
            int count = 0;
            for (int j = 0; j < board.getBoard()[0].length; j++) {
                if (board.getBoard()[i][j] != null) {
                    count++;
                }
                board.getBoard()[size][j] = board.getBoard()[i][j];
            }
            if (count < board.getBoard()[0].length) {
                size--;
            }
        }
    }

    /**
     * rotates the block if possible
     * does so in anti-clockwise pattern
     */
    public void rotateShape() {
        rotatedShape = rotatedBlock(coords);
        rotatedShape = reverseRows(rotatedShape);
        if ((x + rotatedShape[0].length > 10) || (y + rotatedShape.length > 20)) {
            return;
        }
        for (int row = 0; row < rotatedShape.length; row++) {
            for (int col = 0; col < rotatedShape[row].length; col++) {
                if (rotatedShape[row][col] != 0) {
                    if (board.getBoard()[y + row][x + col] != null) {
                        return;
                    }
                }
            }
        }
        coords = rotatedShape;
    }

    private int[][] rotatedBlock(int[][] matrix) {
        int[][] matrixChange = new int[matrix[0].length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrixChange[j][i] = matrix[i][j];
            }
        }
        return matrixChange;
    }

    private int[][] reverseRows(int[][] matrix) {
        int middle = matrix.length / 2;
        for (int i = 0; i < middle; i++) {
            int[] temp = matrix[i];
            matrix[i] = matrix[matrix.length - i - 1];
            matrix[matrix.length - i - 1] = temp;
        }
        return matrix;
    }

    public Color getColor() {
        return color;
    }

    public void setMovementX(int movementX) {
        this.movementX = movementX;
    }

    public void speedUp() {
        delay = fast;
    }
    public void speedDown() {
        delay = normal;
    }

    public int[][] getCoords() {
        return coords;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
