package starina_among_us.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Collections;

public class VistaMisionBiblioteca extends JDialog {

    private PanelJuego panelPadre;
    private ArrayList<Libro> libros = new ArrayList<>();
    private Rectangle[] slots = new Rectangle[5];
    private Libro libroArrastrado = null;
    private int offsetX, offsetY;
    
    // 1 = Alfabético, 2 = Puntuación
    private int faseActual = 1; 

    public VistaMisionBiblioteca(PanelJuego padre) {
        super(SwingUtilities.getWindowAncestor(padre), "Organizar Biblioteca", Dialog.ModalityType.APPLICATION_MODAL);
        this.panelPadre = padre;
        
        setSize(900, 480);
        setLocationRelativeTo(padre);
        setUndecorated(true);

        PanelMiniJuego panelMinijuego = new PanelMiniJuego();
        setContentPane(panelMinijuego);
        panelMinijuego.setLayout(null);

        // --- 1. CARGAR LAS IMÁGENES DE LOS LOMOS ---
        BufferedImage imgAfter = null, imgNube = null, imgPost = null, imgStarina = null, imgSpirit = null;
        try {
            imgAfter = ImageIO.read(getClass().getResource("/starina_among_us/recursos/misiones/libreria/after_hours_lomo.png"));
            // OJO: Si Java te da error al cargar esta imagen por la "ñ", 
            // renombra el archivo a "los_suenos_de_nube_lomo.jpg" y cámbialo aquí también.
            imgNube = ImageIO.read(getClass().getResource("/starina_among_us/recursos/misiones/libreria/los_suenos_de_nube_lomo.png"));
            imgPost = ImageIO.read(getClass().getResource("/starina_among_us/recursos/misiones/libreria/post_human_lomo.png"));
            imgStarina = ImageIO.read(getClass().getResource("/starina_among_us/recursos/misiones/libreria/starina_lomo.png"));
            imgSpirit = ImageIO.read(getClass().getResource("/starina_among_us/recursos/misiones/libreria/thats_the_spirit_lomo.png"));
        } catch (Exception e) {
            System.out.println("❌ Error cargando las imágenes de la librería: " + e.getMessage());
        }

        // --- 2. CREAR LOS LIBROS CON SUS IMÁGENES ---
        // 1. After hours (A) | Puntos: 4.5 (4to)
        libros.add(new Libro(1, 4, 4.5, imgAfter));
        // 2. Los sueños de nube (L) | Puntos: 4.2 (3ro)
        libros.add(new Libro(2, 3, 4.2, imgNube)); 
        // 3. Post human (P) | Puntos: 2.7 (1ro)
        libros.add(new Libro(3, 1, 2.7, imgPost));
        // 4. Starina (S) | Puntos: 3.5 (2do)
        libros.add(new Libro(4, 2, 3.5, imgStarina));
        // 5. That's the spirit (T) | Puntos: 4.7 (5to)
        libros.add(new Libro(5, 5, 4.7, imgSpirit));

        // --- 3. CONFIGURAR LOS ESPACIOS (Más delgados y altos para que luzcan reales) ---
        int anchoLibro = 75;
        int altoLibro = 320;
        int espacio = 25;
        int startX = 210; // Centrados
        int startY = 80;

        for (int i = 0; i < 5; i++) {
            slots[i] = new Rectangle(startX + (i * (anchoLibro + espacio)), startY, anchoLibro, altoLibro);
        }

        // --- 4. DESORDENAR AL INICIO ---
        do {
            Collections.shuffle(libros);
            for (int i = 0; i < libros.size(); i++) {
                libros.get(i).slotActual = i;
                libros.get(i).bounds = new Rectangle(slots[i]);
            }
        } while (verificarOrden()); 

        // --- 5. LÓGICA DE ARRASTRAR (DRAG & DROP) ---
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

                    // --- VERIFICAR FASES ---
                    if (verificarOrden()) {
                        if (faseActual == 1) {
                            System.out.println("🌟 ¡Fase 1 superada! Ahora por puntuación...");
                            faseActual = 2; // Pasamos a la fase 2
                            panelMinijuego.repaint();
                        } else if (faseActual == 2) {
                            System.out.println("✅ ¡Biblioteca organizada perfectamente!");
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

        // Botón Salir
        JButton btnCerrar = new JButton("X");
        btnCerrar.setBounds(830, 10, 50, 40);
        btnCerrar.setBackground(Color.RED);
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 20));
        btnCerrar.setFocusPainted(false);
        btnCerrar.addActionListener(e -> dispose());
        panelMinijuego.add(btnCerrar);
    }

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

    // --- PANEL DE DIBUJO ---
    class PanelMiniJuego extends JPanel {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            // Fondo oscuro (Dentro del estante)
            g2.setColor(new Color(30, 15, 5));
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Títulos según la fase
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 26));
            if (faseActual == 1) {
                g2.drawString("FASE 1: Ordena los libros Alfabéticamente (A - Z)", 120, 40);
            } else {
                g2.setColor(new Color(255, 215, 0)); // Dorado
                g2.drawString("FASE 2: Ahora ordénalos por Puntuación (Menor a Mayor)", 90, 40);
            }

            // Dibujar la base de madera del estante
            g2.setColor(new Color(101, 67, 33));
            g2.fillRect(50, 400, 800, 35); // La repisa está en Y=400

            // Solo dibujamos el borde de las marcas
            g2.setColor(new Color(20, 10, 5)); // Color oscuro para el borde
            for (Rectangle r : slots) {
                g2.drawRect(r.x, r.y, r.width, r.height); // Usamos drawRect en vez de fillRect
            }

            // Dibujar los Libros (TUS IMÁGENES)
            for (Libro lib : libros) {
                Rectangle r = lib.bounds;
                
                if (lib.img != null) {
                    // Si la imagen existe, la dibuja
                    g2.drawImage(lib.img, r.x, r.y, r.width, r.height, null);
                } else {
                    // Respaldo de seguridad por si una imagen falla
                    g2.setColor(Color.GRAY);
                    g2.fillRect(r.x, r.y, r.width, r.height);
                }
                
                // Dibujar el borde sutil
                g2.setColor(Color.BLACK);
                g2.drawRect(r.x, r.y, r.width, r.height);

                // Escribir la puntuación en la repisa de madera (justo debajo del libro)
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 18));
                g2.drawString(lib.puntos + "", r.x + 15, r.y + r.height + 25);
            }
        }
    }

    // --- ESTRUCTURA DEL LIBRO ---
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