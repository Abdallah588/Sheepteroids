package main;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 * Notre classe Vaisseau, permettant de gérer tout le vaisseau
 *
 * @author kai
 */
public class Vaisseau {

    // La vitesse du vaisseau :
    // inférieur à 1 va accélérer
    // supérieur à 1 va ralentir
    private static final double VITESSEMAX = 8;

    // La vitesse d'accélération
    // Plus la vitesse est haute, plus on pourra aller vite
    private static final double ACCELERATION =0.05;

    // L'angle qui fera tourner le vaisseau.
    // Correspond à la vitesse de rotation
    private static final int ANGLE = 6;

    // Le temps entre deux tirs.
    private static final long VITESSETIR = 400;

    private Board board;

    private long start;
    private long dernierTir;
    // L'image
    BufferedImage image;

    // Les variables de déplacement
    private double dx, dy;

    // Les positions
    private double x, y;

    // L'angle de rotation
    private int angle;

    // Notre liste de missiles
    private ArrayList<Missile> missiles;
    private boolean visible;

    // Pour appuyer sur plusieurs touches en même temps
    private boolean space, up, down, left, right;

    /**
     * Constructeur de notre vaisseau
     *
     * @throws IOException
     */
    public Vaisseau(Board board) throws IOException {

        // On initialise les missiles
        missiles = new ArrayList<Missile>();

        // On définit ce qu'est l'image
        this.image = ImageIO.read(getClass().getResource("vaisseau.png"));

        // Et les variables de départ
        angle = 0;
        x = 300;
        y = 220;
        dx = 0;
        dy = 0;

        //
        dernierTir = System.currentTimeMillis();

        // Puis on le rends visible
        visible = true;

        board = board;

    }

    /**
     * Fonction permettant de dessiner un vaisseau
     *
     * @param g2d : le contexte graphique de l'application
     */
    public void draw(Graphics2D g2d) {
        AffineTransform at = new AffineTransform(); // Pour faire des transformations sur notre vaisseau
        at.rotate(Math.toRadians(angle), x + image.getWidth(null) / 2, y
                + image.getHeight(null) / 2);
        at.translate(x, y);
        g2d.drawImage(image, at, null);


    }

    /**
     * Fonction permettant au vaisseau de créer un nouveau missile
     */
    public void fire() {
        start = System.currentTimeMillis();
        //System.out.println(start - dernierTir);
        if (start - dernierTir > VITESSETIR) {
            dernierTir = System.currentTimeMillis();
            try {
                Missile miss = new Missile(board, x + image.getWidth(null) / 2, y
                        + image.getHeight(null) / 2, angle);
                missiles.add(miss);
                miss.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Fonction permettant de changer l'angle du vaisseau
     *
     * @param ang : l'angle qui va être changé
     */
    public void tournerVaisseau(int ang) {
        this.angle += ang;
    }

    /**
     * Fonction permettant de déplacer notre vaisseau
     */
    public void deplacerVaisseau() {
        dx += Math.cos(2 * Math.PI * (angle - 90) / 360) * ACCELERATION;
        dy += Math.sin(2 * Math.PI * (angle - 90) / 360) * ACCELERATION;

        // Pour l'accéleration
        if(Math.sqrt(dx*dx + dy*dy) > VITESSEMAX){		// On n'applique une limitation de vitesse qui sera la meme quel que soit l'angle de deplacement
        	if(dy != 0){								// Pour éviter une division par 0
        		double c = dx/dy;						// Sauvegarde le rapport pour garder un angle constant
        		dy = (dy>0?1:-1)* VITESSEMAX/(1+c*c);	// Calcul dy en fonction de la vitesse max et du rapport
        		dx = c * dy * (c*dy*dx>0?1:-1);			// Calcul dx en fonction de dx et de la vitesse max


			}else{
        		dx = VITESSEMAX;
			}
		}
    }

    /**
     * Parce qu'il faut bien s'arrêter à un moment donné
     */
    public void freiner() {
    	dx *= 0.98;
		dy *= 0.98;
    }

    /**
     * Permet de bouger le vaisseau
     */
    public void move() {

        // En X
        x += dx;
        if (x > 550) {
            x -= 550;
        }
        if (x < 0) {
            x += 550;
        }

        // En Y
        y += dy;
        if (y > 400) {
            y -= 400;
        }
        if (y < 0) {
            y += 400;
        }
    }

    /**
     * Qu'est-ce qu'il se passe si j'appuie sur une touche ?
     *
     * @param e : la touche sur laquelle on appuie
     * @throws IOException
     */
    public void keyPressed(KeyEvent e) throws IOException {

        // On récupère la touche en question
        int key = e.getKeyCode();

        space = key == KeyEvent.VK_SPACE || space;
        left = key == KeyEvent.VK_LEFT || left;
        down = key == KeyEvent.VK_DOWN || down;
        right = key == KeyEvent.VK_RIGHT || right;


        if (key == KeyEvent.VK_UP) {
            up = true;
            this.image = ImageIO.read(getClass().getResource("VaisseauMove.png"));
        }

        // nouvelle position

    }

    /**
     * Qu'est ce qu'il se passe si je relache une touche ?
     *
     * @param e : la touche sur laquelle j'appuie
     * @throws IOException
     */
    public void keyReleased(KeyEvent e) throws IOException {
        // On récupère la touche en question
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_SPACE) {
            space = false;
        }

        if (key == KeyEvent.VK_LEFT) {
            left = false;
        }

        if (key == KeyEvent.VK_RIGHT) {
            right = false;

        }

        if (key == KeyEvent.VK_UP) {
            this.image = ImageIO.read(getClass().getResource("vaisseau.png"));
            up = false;
        }

        if (key == KeyEvent.VK_DOWN) {
            down = false;
        }

    }

    /**
     * Pour gérer les différents appuies de touches en même temps
     */
    public void doAction() {

        tournerVaisseau((left ? -6 : 0) + (right ? 6 : 0));

        if (space)
            fire();

        if (up)
            deplacerVaisseau();


        if (down)
            freiner();

    }

    /**********************************************************************************************/
    /* LES GETTEURS / SETTEURS */
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public ArrayList<Missile> getMissiles() {
        return missiles;
    }

    public int getAngle() {
        if (angle > 359 || angle < -359)
            angle = 0;
        if (angle < 0)
            angle += 360;
        return angle;
    }

    /*
     * Méthode permettant de retourner un rectangle, correspondant à notre
     * vaisseau
     */
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, image.getWidth(),
                image.getHeight());
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /*****************************************************************************************************/

}