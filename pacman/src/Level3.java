package pacman.src;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.util.*;
import java.util.List;

class Node {
    int x, y; // Grid coordinates
    int g; // Cost from start to current node
    int h; // Heuristic (estimated cost to target)
    int f; // Total cost (f = g + h)
    Node parent; // Parent node for reconstructing path

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        this.g = 0;
        this.h = 0;
        this.f = 0;
        this.parent = null;
    }
}

public class Level3 extends JPanel implements ActionListener {

	private Dimension d;
    private final Font smallFont = new Font("Arial", Font.BOLD, 14);
    private boolean inGame = false;
    private boolean dying = false;

    private final int BLOCK_SIZE = 24;
    private final int N_BLOCKS = 15;
    private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE;
    private final int MAX_GHOSTS = 12;
    private final int PACMAN_SPEED = 4;

    private int N_GHOSTS = 6;
    private int lives, score;
    private int[] dx, dy;
    private int[] ghost_x, ghost_y, ghost_dx, ghost_dy, ghostSpeed;

    private Image heart, ghost;
    private Image up, down, left, right;

    private int pacman_x, pacman_y, pacmand_x, pacmand_y;
    private int req_dx, req_dy;

    private final short levelData[] = {
    	19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
        17, 16, 16, 16, 16, 24, 16, 16, 16, 16, 16, 16, 16, 16, 20,
        25, 24, 24, 24, 28, 0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
        0,  0,  0,  0,  0,  0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
        19, 18, 18, 18, 18, 18, 16, 16, 16, 16, 24, 24, 24, 24, 20,
        17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
        17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
        17, 16, 16, 16, 24, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
        17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 18, 18, 18, 18, 20,
        17, 24, 24, 28, 0, 25, 24, 24, 16, 16, 16, 16, 16, 16, 20,
        21, 0,  0,  0,  0,  0,  0,   0, 17, 16, 16, 16, 16, 16, 20,
        17, 18, 18, 22, 0, 19, 18, 18, 16, 16, 16, 16, 16, 16, 20,
        17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
        17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
        25, 24, 24, 24, 26, 24, 24, 24, 24, 24, 24, 24, 24, 24, 28
    };

    private final int validSpeeds[] = {1, 2, 3, 4, 6, 8};
    private final int maxSpeed = 6;

    private int currentSpeed = 3;
    private short[] screenData;
    private Timer timer;

    public Level3() {

        loadImages();
        initVariables();
        addKeyListener(new TAdapter());
        setFocusable(true);
        initGame();
    }
    
    
    private void loadImages() {
    	down = new ImageIcon("/Users/giakhanh/Desktop/DSA/Project/pacman/pacman/images/down.gif").getImage();
    	up = new ImageIcon("/Users/giakhanh/Desktop/DSA/Project/pacman/pacman/images/up.gif").getImage();
    	left = new ImageIcon("/Users/giakhanh/Desktop/DSA/Project/pacman/pacman/images/left.gif").getImage();
    	right = new ImageIcon("/Users/giakhanh/Desktop/DSA/Project/pacman/pacman/images/right.gif").getImage();
        ghost = new ImageIcon("/Users/giakhanh/Desktop/DSA/Project/pacman/pacman/images/ghost.gif").getImage();
        heart = new ImageIcon("/Users/giakhanh/Desktop/DSA/Project/pacman/pacman/images/heart.png").getImage();

    }
       private void initVariables() {

        screenData = new short[N_BLOCKS * N_BLOCKS];
        d = new Dimension(400, 400);
        ghost_x = new int[MAX_GHOSTS];
        ghost_dx = new int[MAX_GHOSTS];
        ghost_y = new int[MAX_GHOSTS];
        ghost_dy = new int[MAX_GHOSTS];
        ghostSpeed = new int[MAX_GHOSTS];
        dx = new int[4];
        dy = new int[4];
        
        timer = new Timer(100, this);
        timer.start();
    }

    private void playGame(Graphics2D g2d) {

        if (dying) {

            death();

        } else {

            movePacman();
            drawPacman(g2d);
            moveGhosts(g2d);
            checkMaze();
        }
    }

    private void showIntroScreen(Graphics2D g2d) {
 
    	String start = "Press SPACE to start";
        g2d.setColor(Color.yellow);
        g2d.drawString(start, (SCREEN_SIZE)/4, 150);
    }

    private void drawScore(Graphics2D g) {
        g.setFont(smallFont);
        g.setColor(new Color(5, 181, 79));
        String s = "Score: " + score;
        g.drawString(s, SCREEN_SIZE / 2 + 96, SCREEN_SIZE + 16);

        for (int i = 0; i < lives; i++) {
            g.drawImage(heart, i * 28 + 8, SCREEN_SIZE + 1, this);
        }
    }

    private void checkMaze() {

        int i = 0;
        boolean finished = true;

        while (i < N_BLOCKS * N_BLOCKS && finished) {

            if ((screenData[i]) != 0) {
                finished = false;
            }

            i++;
        }

        if (finished) {

            score += 50;

            if (N_GHOSTS < MAX_GHOSTS) {
                N_GHOSTS++;
            }

            if (currentSpeed < maxSpeed) {
                currentSpeed++;
            }

            initLevel();
        }
    }

    private void death() {

    	lives--;

        if (lives == 0) {
            inGame = false;
        }

        continueLevel();
    }

    private void moveGhosts(Graphics2D g2d) {
        for (int i = 0; i < N_GHOSTS; i++) {
            int ghostGridX = ghost_x[i] / BLOCK_SIZE;
            int ghostGridY = ghost_y[i] / BLOCK_SIZE;
    
            // Check if the ghost is aligned with the grid
            if (ghost_x[i] % BLOCK_SIZE == 0 && ghost_y[i] % BLOCK_SIZE == 0) {
                int pacmanGridX = pacman_x / BLOCK_SIZE;
                int pacmanGridY = pacman_y / BLOCK_SIZE;
    
                List<int[]> path = aStarPath(ghostGridX, ghostGridY, pacmanGridX, pacmanGridY);
    
                if (!path.isEmpty() && path.size() > 1) {
                    // Follow the A* path
                    int[] nextMove = path.get(1);
                    int nextX = nextMove[0];
                    int nextY = nextMove[1];
                    ghost_dx[i] = nextX - ghostGridX;
                    ghost_dy[i] = nextY - ghostGridY;
                } else {
                    // Fallback to random movement
                    System.out.println("No path found for ghost " + i + " at (" + ghostGridX + "," + ghostGridY + ")");
                    boolean moved = false;
                    for (int retries = 0; retries < 4; retries++) {
                        int direction = (int) (Math.random() * 4);
                        int potentialX = ghostGridX + dx[direction];
                        int potentialY = ghostGridY + dy[direction];
                        if (isMovePossible(potentialX, potentialY)) {
                            ghost_dx[i] = dx[direction];
                            ghost_dy[i] = dy[direction];
                            moved = true;
                            break;
                        }
                    }
                    if (!moved) {
                        // If no valid random move, reverse direction
                        ghost_dx[i] = -ghost_dx[i];
                        ghost_dy[i] = -ghost_dy[i];
                    }
                }
            }
    
            // Move the ghost
            ghost_x[i] += ghost_dx[i] * ghostSpeed[i];
            ghost_y[i] += ghost_dy[i] * ghostSpeed[i];
    
            // Constrain ghost within the game frame
            if (ghost_x[i] < 0) ghost_x[i] = 0;
            if (ghost_y[i] < 0) ghost_y[i] = 0;
            if (ghost_x[i] > SCREEN_SIZE - BLOCK_SIZE) ghost_x[i] = SCREEN_SIZE - BLOCK_SIZE;
            if (ghost_y[i] > SCREEN_SIZE - BLOCK_SIZE) ghost_y[i] = SCREEN_SIZE - BLOCK_SIZE;
    
            // Draw the ghost
            drawGhost(g2d, ghost_x[i] + 1, ghost_y[i] + 1);
    
            // Check collision with Pac-Man
            if (pacman_x > (ghost_x[i] - 12) && pacman_x < (ghost_x[i] + 12)
                    && pacman_y > (ghost_y[i] - 12) && pacman_y < (ghost_y[i] + 12)
                    && inGame) {

                dying = true;
            }
        }
    }
    
    
    private List<int[]> aStarPath(int ghostX, int ghostY, int targetX, int targetY) {
        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));
        HashSet<String> closedList = new HashSet<>();
        Map<String, Node> allNodes = new HashMap<>();
    
        if (!isMovePossible(ghostX, ghostY) || !isMovePossible(targetX, targetY)) {
            return new ArrayList<>(); // Return empty path if start or target is invalid
        }
    
        Node startNode = new Node(ghostX, ghostY);
        Node targetNode = new Node(targetX, targetY);
    
        openList.add(startNode);
        allNodes.put(ghostX + "," + ghostY, startNode);
    
        int[] dx = {-1, 1, 0, 0}; // Directions: Left, Right, Up, Down
        int[] dy = {0, 0, -1, 1};
    
        while (!openList.isEmpty()) {
            Node current = openList.poll();
            closedList.add(current.x + "," + current.y);
    
            // Check if target is reached
            if (current.x == targetNode.x && current.y == targetNode.y) {
                return reconstructPath(current);
            }
    
            // Explore neighbors
            for (int i = 0; i < 4; i++) {
                int newX = current.x + dx[i];
                int newY = current.y + dy[i];
                String neighborKey = newX + "," + newY;
    
                if (!isMovePossible(newX, newY) || closedList.contains(neighborKey)) {
                    continue;
                }
    
                int tentativeG = current.g + 1;
    
                Node neighbor = allNodes.getOrDefault(neighborKey, new Node(newX, newY));
                allNodes.put(neighborKey, neighbor);
    
                if (tentativeG < neighbor.g || !openList.contains(neighbor)) {
                    neighbor.g = tentativeG;
                    neighbor.h = Math.abs(targetX - newX) + Math.abs(targetY - newY); // Manhattan distance
                    neighbor.f = neighbor.g + neighbor.h;
                    neighbor.parent = current;
    
                    if (!openList.contains(neighbor)) {
                        openList.add(neighbor);
                    }
                }
            }
        }
    
        return new ArrayList<>(); // Return an empty path if no solution
    }
    
    private List<int[]> reconstructPath(Node current) {
        List<int[]> path = new ArrayList<>();
        while (current != null) {
            path.add(new int[]{current.x, current.y});
            current = current.parent;
        }
        Collections.reverse(path);
        return path;
    }
    
    private boolean isMovePossible(int x, int y) {
        if (x < 0 || x >= N_BLOCKS || y < 0 || y >= N_BLOCKS) {
            System.out.println("Out of bounds: (" + x + "," + y + ")");
            return false;
        }
        int pos = x + y * N_BLOCKS;
        boolean movePossible = (screenData[pos] & 15) == 0; // No walls
        System.out.println("Checking move for (" + x + "," + y + "): screenData=" + screenData[pos] + ", movePossible=" + movePossible);
        return movePossible;
    }
    
    private void drawGhost(Graphics2D g2d, int x, int y) {
    	g2d.drawImage(ghost, x, y, this);
        }

    private void movePacman() {

        int pos;
        short ch;

        if (pacman_x % BLOCK_SIZE == 0 && pacman_y % BLOCK_SIZE == 0) {
            pos = pacman_x / BLOCK_SIZE + N_BLOCKS * (int) (pacman_y / BLOCK_SIZE);
            ch = screenData[pos];

            if ((ch & 16) != 0) {
                screenData[pos] = (short) (ch & 15);
                score++;
            }

            if (req_dx != 0 || req_dy != 0) {
                if (!((req_dx == -1 && req_dy == 0 && (ch & 1) != 0)
                        || (req_dx == 1 && req_dy == 0 && (ch & 4) != 0)
                        || (req_dx == 0 && req_dy == -1 && (ch & 2) != 0)
                        || (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) {
                    pacmand_x = req_dx;
                    pacmand_y = req_dy;
                }
            }

            // Check for standstill
            if ((pacmand_x == -1 && pacmand_y == 0 && (ch & 1) != 0)
                    || (pacmand_x == 1 && pacmand_y == 0 && (ch & 4) != 0)
                    || (pacmand_x == 0 && pacmand_y == -1 && (ch & 2) != 0)
                    || (pacmand_x == 0 && pacmand_y == 1 && (ch & 8) != 0)) {
                pacmand_x = 0;
                pacmand_y = 0;
            }
        } 
        pacman_x = pacman_x + PACMAN_SPEED * pacmand_x;
        pacman_y = pacman_y + PACMAN_SPEED * pacmand_y;
    }

    private void drawPacman(Graphics2D g2d) {

        if (req_dx == -1) {
        	g2d.drawImage(left, pacman_x + 1, pacman_y + 1, this);
        } else if (req_dx == 1) {
        	g2d.drawImage(right, pacman_x + 1, pacman_y + 1, this);
        } else if (req_dy == -1) {
        	g2d.drawImage(up, pacman_x + 1, pacman_y + 1, this);
        } else {
        	g2d.drawImage(down, pacman_x + 1, pacman_y + 1, this);
        }
    }

    private void drawMaze(Graphics2D g2d) {

        short i = 0;
        int x, y;

        for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) {
            for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {

                g2d.setColor(new Color(0,72,251));
                g2d.setStroke(new BasicStroke(5));
                
                if ((levelData[i] == 0)) { 
                	g2d.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
                 }

                if ((screenData[i] & 1) != 0) { 
                    g2d.drawLine(x, y, x, y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 2) != 0) { 
                    g2d.drawLine(x, y, x + BLOCK_SIZE - 1, y);
                }

                if ((screenData[i] & 4) != 0) { 
                    g2d.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 8) != 0) { 
                    g2d.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 16) != 0) { 
                    g2d.setColor(new Color(255,255,255));
                    g2d.fillOval(x + 10, y + 10, 6, 6);
               }

                i++;
            }
        }
    }

    private void initGame() {

    	lives = 3;
        score = 0;
        initLevel();
        N_GHOSTS = 6;
        currentSpeed = 3;
    }

    private void initLevel() {
        for (int i = 0; i < N_BLOCKS * N_BLOCKS; i++) {
            screenData[i] = levelData[i];
        }
        levelData[4 + 4 * N_BLOCKS] = 0; // Ensure ghosts' starting position is traversable
        System.out.println("screenData at (4,4): " + screenData[4 + 4 * N_BLOCKS]);
    
        // Initialize ghosts
        int dx = 1;
        for (int i = 0; i < N_GHOSTS; i++) {
            ghost_x[i] = 4 * BLOCK_SIZE; // Start position
            ghost_y[i] = 4 * BLOCK_SIZE;
            ghost_dx[i] = dx;
            ghost_dy[i] = 0;
            dx = -dx; // Alternate initial directions
            ghostSpeed[i] = validSpeeds[(int) (Math.random() * currentSpeed)];
        }
    
        pacman_x = 7 * BLOCK_SIZE; // Start position
        pacman_y = 11 * BLOCK_SIZE;
        pacmand_x = 0; // Reset direction move
        pacmand_y = 0;
        req_dx = 0; // Reset direction controls
        req_dy = 0;
        dying = false;
    }
    
    


    private void continueLevel() {

    	int dx = 1;
        int random;

        for (int i = 0; i < N_GHOSTS; i++) {

            ghost_y[i] = 4 * BLOCK_SIZE; //start position
            ghost_x[i] = 4 * BLOCK_SIZE;
            ghost_dy[i] = 0;
            ghost_dx[i] = dx;
            dx = -dx;
            random = (int) (Math.random() * (currentSpeed + 1));

            if (random > currentSpeed) {
                random = currentSpeed;
            }

            ghostSpeed[i] = validSpeeds[random];
        }

        pacman_x = 7 * BLOCK_SIZE;  //start position
        pacman_y = 11 * BLOCK_SIZE;
        pacmand_x = 0;	//reset direction move
        pacmand_y = 0;
        req_dx = 0;		// reset direction controls
        req_dy = 0;
        dying = false;
    }

 
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, d.width, d.height);

        drawMaze(g2d);
        drawScore(g2d);

        if (inGame) {
            playGame(g2d);
        } else {
            showIntroScreen(g2d);
        }

        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }


    //controls
    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if (inGame) {
                if (key == KeyEvent.VK_LEFT) {
                    req_dx = -1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_RIGHT) {
                    req_dx = 1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_UP) {
                    req_dx = 0;
                    req_dy = -1;
                } else if (key == KeyEvent.VK_DOWN) {
                    req_dx = 0;
                    req_dy = 1;
                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                    inGame = false;
                } 
            } else {
                if (key == KeyEvent.VK_SPACE) {
                    inGame = true;
                    initGame();
                }
            }
        }
}

	
    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
		
	}

