package Snake;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
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

    private class SnakePanel extends JPanel implements ActionListener, KeyListener {


        private class Tile {
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
        final ArrayList<Tile> snakeBody;

        //  food
        Tile food;

        // game logic
        Timer gameLoop;
        int vx;
        int vy;

        public SnakePanel(int BOARD_WIDTH, int BOARD_HEIGHT) {
            this.BOARD_WIDTH = BOARD_WIDTH;
            this.BOARD_HEIGHT = BOARD_HEIGHT;

            snakeHead = new Tile(5, 5);
            snakeBody = new ArrayList<>();

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
            food.x = (int) (Math.random() * BOARD_WIDTH / TILESIZE);
            food.y = (int) (Math.random() * BOARD_HEIGHT / TILESIZE);
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
            for (int i = 0; i < BOARD_WIDTH/TILESIZE; i++) {
                g.drawLine(i*TILESIZE, 0, i*TILESIZE, BOARD_HEIGHT);
                g.drawLine(0, i*TILESIZE, BOARD_WIDTH, i*TILESIZE);
            }

            // food
            g.setColor(Color.red);
            g.fillRect(food.x * TILESIZE, food.y * TILESIZE, TILESIZE, TILESIZE);

            // snake head
            g.setColor(Color.green);
            drawSegment(g, snakeHead.x * TILESIZE, snakeHead.y * TILESIZE);

            for (Tile tile : snakeBody) {
                drawSegment(g, tile.x * TILESIZE, tile.y * TILESIZE);
            }
        }


        public void drawSegment(Graphics g, int x, int y) {
            g.drawRect(x + 1, y + 1,
                    TILESIZE - 1, TILESIZE - 1);
            g.fillPolygon(new int[]{x, x + TILESIZE, x + TILESIZE},
                    new int[]{y + TILESIZE + 1,  y + TILESIZE + 1, y + 1}, 3);
        }

        public void move() {
            if (collision(snakeHead, food)) {
                snakeBody.add(new Tile(food.x, food.y));
                placeFood();
            }

            for (int i = snakeBody.size() - 1; i >= 0; i--) {
                Tile snakePart = snakeBody.get(i);
                if (i == 0) {
                    snakePart.x = snakeHead.x;
                    snakePart.y = snakeHead.y;
                }
                else {
                    Tile prevSnakePart = snakeBody.get(i - 1);
                    snakePart.x = prevSnakePart.x;
                    snakePart.y = prevSnakePart.y;
                }
            }

            // Snake Head
            snakeHead.x += vx;
            snakeHead.y += vy;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            move();
            repaint();
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_UP && vy != 1) {
                vx = 0;
                vy = -1;
            }
            else if (e.getKeyCode() == KeyEvent.VK_DOWN && vy != -1) {
                vx = 0;
                vy = 1;
            }
            else if (e.getKeyCode() == KeyEvent.VK_LEFT && vx != 1) {
                vx = -1;
                vy = 0;
            }
            else if (e.getKeyCode() == KeyEvent.VK_RIGHT && vx != -1) {
                vx = 1;
                vy = 0;
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void keyReleased(KeyEvent e) {}
    }
}
