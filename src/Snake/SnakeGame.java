package Snake;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayDeque;
import java.util.Deque;
import javax.swing.*;

public class SnakeGame {
    private final JFrame frame;
    final int WIDTH;
    final int HEIGHT;
    final int TILESIZE;

    public SnakeGame(int WIDTH, int HEIGHT, int TILESIZE) {
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        this.TILESIZE = TILESIZE;

        frame = new JFrame("Snake");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SnakePanel snakePanel = new SnakePanel(this.WIDTH, this.HEIGHT);

        frame.add(snakePanel);
        frame.pack();

        snakePanel.requestFocus();

        frame.setLocationRelativeTo(null);

    }

    public void show() {
        frame.setVisible(true);
    }

    private static class SnakePanel extends JPanel implements ActionListener, KeyListener {


        private static class Tile {
            int x;
            int y;

            Tile(int x, int y) {
                this.x = x;
                this.y = y;
            }
        }

        final int BOARD_WIDTH;
        final int BOARD_HEIGHT;
        final int TILESIZE = 25;

        // snake
        Tile snakeHead;
        final Deque<Tile> snakeBody;

        final Deque<int[]> moves;
        int[] currentMove = new int[]{0, 0};

        //  food
        Tile food;

        // game logic
        Timer gameLoop;
        int vx;
        int vy;
        boolean gameOver = false;

        public SnakePanel(int BOARD_WIDTH, int BOARD_HEIGHT) {
            this.BOARD_WIDTH = BOARD_WIDTH;
            this.BOARD_HEIGHT = BOARD_HEIGHT;

            snakeHead = new Tile(5, 5);
            snakeBody = new ArrayDeque<>();

            moves = new ArrayDeque<>();

            food = new Tile(10, 10);
            placeFood();

            gameLoop = new Timer(100, this);
            gameLoop.start();

            vx = 0;
            vy = 0;

            setPreferredSize(new Dimension(this.BOARD_WIDTH, this.BOARD_HEIGHT));
            setBackground(Color.black);
            addKeyListener(this);
            setFocusable(true);
        }

        private void placeFood() {
            while (true) {
                boolean exitLoop = true;

                food.x = (int) (Math.random() * BOARD_WIDTH / TILESIZE);
                food.y = (int) (Math.random() * BOARD_HEIGHT / TILESIZE);
                if (collision(food, snakeHead)) {
                    exitLoop = false;
                }
                for (Tile t : snakeBody) {
                    if (collision(food, t)) {
                        exitLoop = false;
                        break;
                    }
                }
                if (exitLoop) {
                    break;
                }
            }
        }

        public boolean collision(Tile t1, Tile t2) {
            return t1.x == t2.x && t1.y == t2.y;
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            draw(g);
        }

        public void draw(Graphics g) {
            // grid
//            for (int i = 0; i < BOARD_WIDTH/TILESIZE; i++) {
//                g.drawLine(i*TILESIZE, 0, i*TILESIZE, BOARD_HEIGHT);
//                g.drawLine(0, i*TILESIZE, BOARD_WIDTH, i*TILESIZE);
//            }

            // food
            g.setColor(Color.red);
            g.fillRect(food.x * TILESIZE, food.y * TILESIZE, TILESIZE, TILESIZE);

            // snake head
            if (!gameOver) {
                g.setColor(Color.green);
            }
            drawSegment(g, snakeHead.x * TILESIZE, snakeHead.y * TILESIZE);

            for (Tile tile : snakeBody) {
                drawSegment(g, tile.x * TILESIZE, tile.y * TILESIZE);
            }

            g.setFont(new Font("Arial", Font.PLAIN, 16));
            if (gameOver) {
                g.drawString("Game Over: " + snakeBody.size(), TILESIZE - 16, TILESIZE);
            }
            else {
                g.setColor(Color.white);
                g.drawString("Score: " + snakeBody.size(), TILESIZE - 16, TILESIZE);
            }
        }


        public void drawSegment(Graphics g, int x, int y) {
            g.drawRect(x + 1, y + 1,
                    TILESIZE - 1, TILESIZE - 1);
            g.fillPolygon(new int[]{x, x + TILESIZE, x + TILESIZE},
                    new int[]{y + TILESIZE + 1,  y + TILESIZE + 1, y + 1}, 3);
        }


        public void move() {
            if (!moves.isEmpty()) {
                int[] nextMove = moves.getFirst();
                if ((nextMove[0] != -currentMove[0] || nextMove[1] != -currentMove[1]) &&
                        (nextMove[0] != 0 || nextMove[1] != 0)) {
                    currentMove = moves.removeFirst();
                }
            }

            int newX = snakeHead.x + currentMove[0];
            int newY = snakeHead.y + currentMove[1];

            // Check for collisions
            if (newX < 0 || newX >= (BOARD_WIDTH / TILESIZE) ||
                    newY < 0 || newY >= (BOARD_HEIGHT / TILESIZE) ||
                    snakeBody.stream().anyMatch(t -> t.x == newX && t.y == newY)) {
                gameOver = true;
                return;
            }

            // Move body
            if (!snakeBody.isEmpty()) {
                snakeBody.addFirst(new Tile(snakeHead.x, snakeHead.y));
                snakeBody.removeLast();
            }

            // Move head
            snakeHead.x = newX;
            snakeHead.y = newY;

            // Check for food
            if (snakeHead.x == food.x && snakeHead.y == food.y) {
                snakeBody.addFirst(new Tile(food.x, food.y));
                placeFood();
            }
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            move();

            if (gameOver) {
                gameLoop.stop();
            }
//            else {
                repaint();
//            }

        }

        @Override
        public void keyPressed(KeyEvent e) {
            int[] newMove = {0, 0};
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:    newMove[1] = -1; break;
                case KeyEvent.VK_DOWN:  newMove[1] = 1; break;
                case KeyEvent.VK_LEFT:  newMove[0] = -1; break;
                case KeyEvent.VK_RIGHT: newMove[0] = 1; break;
                default: return;
            }
            if (moves.size() < 3 && !moves.contains(newMove) &&
                    (newMove[0] != -currentMove[0] || newMove[1] != -currentMove[1])) {
                moves.addLast(newMove);
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void keyReleased(KeyEvent e) {}
    }
}
