package starina_among_us.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Ventana emergente para la mision de la biblioteca.
 * Incluye un sistema interactivo de Drag and Drop para ordenar libros
 * en sus estantes. Se divide en dos fases: orden alfabetico y numerico.
 * * @author Wulliber Yepez, Carlos Ramirez, Jorg Sierra, Samuel Salazar
 * @version 1.0
 */
public class VistaMisionBiblioteca extends JDialog {

    private PanelJuego panelPadre;
    private ArrayList<Libro> libros = new ArrayList<>();
    private Rectangle[] slots = new Rectangle[5];
    private Libro libroArrastrado = null;
    private int offsetX, offsetY;
    
    private int faseActual = 1; 

    /**
     * Constructor principal que inicializa el minijuego, cargando las imagenes
     * de los libros y desordenandolas en pantalla.
     * * @param padre Contexto del juego base para comunicar estados.
     */
    public VistaMisionBiblioteca(PanelJuego padre) {
        super(SwingUtilities.getWindowAncestor(padre), "Organizar Biblioteca", Dialog.ModalityType.APPLICATION_MODAL);
        this.panelPadre = padre;
        
        setSize(900, 480);
        setLocationRelativeTo(padre);
        setUndecorated(true);

        PanelMiniJuego panelMinijuego = new PanelMiniJuego();
        setContentPane(panelMinijuego);
        panelMinijuego.setLayout(null);

        BufferedImage imgAfter = null, imgNube = null, imgPost = null, imgStarina = null, imgSpirit = null;
        try {
            imgAfter = ImageIO.read(getClass().getResource("/starina_among_us/recursos/misiones/libreria/after_hours_lomo.png"));
            imgNube = ImageIO.read(getClass().getResource("/starina_among_us/recursos/misiones/libreria/los_suenos_de_nube_lomo.png"));
            imgPost = ImageIO.read(getClass().getResource("/starina_among_us/recursos/misiones/libreria/post_human_lomo.png"));
            imgStarina = ImageIO.read(getClass().getResource("/starina_among_us/recursos/misiones/libreria/starina_lomo.png"));
            imgSpirit = ImageIO.read(getClass().getResource("/starina_among_us/recursos/misiones/libreria/thats_the_spirit_lomo.png"));
        } catch (Exception e) {
            System.out.println("Error cargando las imagenes de la libreria: " + e.getMessage());
        }

        libros.add(new Libro(1, 4, 4.5, imgAfter));
        libros.add(new Libro(2, 3, 4.2, imgNube)); 
        libros.add(new Libro(3, 1, 2.7, imgPost));
        libros.add(new Libro(4, 2, 3.5, imgStarina));
        libros.add(new Libro(5, 5, 4.7, imgSpirit));

        int anchoLibro = 75;
        int altoLibro = 320;
        int espacio = 25;
        int startX = 210; 
        int startY = 80;

        for (int i = 0; i < 5; i++) {
            slots[i] = new Rectangle(startX + (i * (anchoLibro + espacio)), startY, anchoLibro, altoLibro);
        }

        do {
            Collections.shuffle(libros);
            for (int i = 0; i < libros.size(); i++) {
                libros.get(i).slotActual = i;
                libros.get(i).bounds = new Rectangle(slots[i]);
            }
        } while (verificarOrden()); 

        panelMinijuego.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                for (int i = libros.size() - 1; i >= 0; i--) {
                    Libro lib = libros.get(i);
                    if (lib.bounds.contains(e.getPoint())) {
                        libroArrastrado = lib;
                        offsetX = e.getX() - lib.bounds.x;
                        offsetY = e.getY() - lib.bounds.y;
                        
                        libros.remove(lib);
                        libros.add(lib);
                        break;
                    }
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (libroArrastrado != null) {
                    int slotDestino = libroArrastrado.slotActual; 
                    
                    for (int i = 0; i < 5; i++) {
                        if (slots[i].intersects(libroArrastrado.bounds)) {
                            slotDestino = i;
                            break;
                        }
                    }

                    Libro libroEnDestino = null;
                    for (Libro lib : libros) {
                        if (lib != libroArrastrado && lib.slotActual == slotDestino) {
                            libroEnDestino = lib;
                            break;
                        }
                    }

                    if (libroEnDestino != null) {
                        libroEnDestino.slotActual = libroArrastrado.slotActual;
                        libroEnDestino.bounds.setLocation(slots[libroEnDestino.slotActual].getLocation());
                    }

                    libroArrastrado.slotActual = slotDestino;
                    libroArrastrado.bounds.setLocation(slots[slotDestino].getLocation());

                    libroArrastrado = null;
                    panelMinijuego.repaint();

                    if (verificarOrden()) {
                        if (faseActual == 1) {
                            faseActual = 2; 
                            panelMinijuego.repaint();
                        } else if (faseActual == 2) {
                            panelPadre.completarMisionBiblioteca();
                            dispose();
                        }
                    }
                }
            }
        });

        panelMinijuego.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (libroArrastrado != null) {
                    libroArrastrado.bounds.x = e.getX() - offsetX;
                    libroArrastrado.bounds.y = e.getY() - offsetY;
                    panelMinijuego.repaint();
                }
            }
        });

        JButton btnCerrar = new JButton("X");
        btnCerrar.setBounds(830, 10, 50, 40);
        btnCerrar.setBackground(Color.RED);
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 20));
        btnCerrar.setFocusPainted(false);
        btnCerrar.addActionListener(e -> dispose());
        panelMinijuego.add(btnCerrar);
    }

    /**
     * Valida el orden actual de las cajas de colision para confirmar si el
     * jugador ha resuelto la fase.
     * * @return true si la distribucion coincide con los ID de victoria de la fase actual.
     */
    private boolean verificarOrden() {
        for (Libro lib : libros) {
            if (faseActual == 1) {
                if (lib.slotActual + 1 != lib.idAlfa) return false;
            } else {
                if (lib.slotActual + 1 != lib.idPuntos) return false;
            }
        }
        return true;
    }

    /**
     * Subpanel personalizado que maneja el renderizado de la biblioteca visualmente.
     */
    class PanelMiniJuego extends JPanel {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            g2.setColor(new Color(30, 15, 5));
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setFont(new Font("Arial", Font.BOLD, 26));
            if (faseActual == 1) {
                g2.setColor(Color.WHITE);
                String txtF1 = starina_among_us.modelo.GestorLenguaje.get("lbl_biblio_fase1");
                int w = g2.getFontMetrics().stringWidth(txtF1);
                g2.drawString(txtF1, (getWidth() - w) / 2, 40);
            } else {
                g2.setColor(new Color(255, 215, 0)); 
                String txtF2 = starina_among_us.modelo.GestorLenguaje.get("lbl_biblio_fase2");
                int w = g2.getFontMetrics().stringWidth(txtF2);
                g2.drawString(txtF2, (getWidth() - w) / 2, 40);
            }

            g2.setColor(new Color(101, 67, 33));
            g2.fillRect(50, 400, 800, 35); 

            g2.setColor(new Color(20, 10, 5)); 
            for (Rectangle r : slots) {
                g2.drawRect(r.x, r.y, r.width, r.height); 
            }

            for (Libro lib : libros) {
                Rectangle r = lib.bounds;
                
                if (lib.img != null) {
                    g2.drawImage(lib.img, r.x, r.y, r.width, r.height, null);
                } else {
                    g2.setColor(Color.GRAY);
                    g2.fillRect(r.x, r.y, r.width, r.height);
                }
                
                g2.setColor(Color.BLACK);
                g2.drawRect(r.x, r.y, r.width, r.height);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 18));
                g2.drawString(lib.puntos + "", r.x + 15, r.y + r.height + 25);
            }
        }
    }

    /**
     * Representacion de los datos internos de cada libro disponible para mover.
     */
    class Libro {
        int idAlfa, idPuntos;
        double puntos;
        BufferedImage img;
        Rectangle bounds;
        int slotActual;

        public Libro(int idA, int idP, double p, BufferedImage imagen) {
            this.idAlfa = idA; 
            this.idPuntos = idP;
            this.puntos = p;
            this.img = imagen;
        }
    }
}