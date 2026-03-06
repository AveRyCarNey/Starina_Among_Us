package starina_among_us.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Collections;

public class VistaMisionOficina extends JDialog {
    
    private PanelJuego panelPadre;
    private ArrayList<Hoja> hojas = new ArrayList<>();
    // --- CAMBIO 1: Ahora el molde tiene 6 espacios ---
    private Rectangle[] slots = new Rectangle[6]; 
    private Hoja hojaArrastrada = null;
    private int offsetX, offsetY;

    public VistaMisionOficina(PanelJuego padre) {
        super(SwingUtilities.getWindowAncestor(padre), "Organizar Trabajo", Dialog.ModalityType.APPLICATION_MODAL);
        this.panelPadre = padre;
        
        // --- CAMBIO 2: Ventana más ancha para que quepan las 6 hojas ---
        setSize(1150, 550); 
        setLocationRelativeTo(padre); 
        setUndecorated(true); 

        PanelMiniJuego panelMinijuego = new PanelMiniJuego();
        setContentPane(panelMinijuego);
        panelMinijuego.setLayout(null);

        // --- CAMBIO 3: Cargar del 1 al 6 ---
        try {
            for (int i = 1; i <= 6; i++) { 
                String ruta = "/starina_among_us/recursos/misiones/escritorio_uni/escritorio_uni_" + i + ".jpg";
                BufferedImage img = ImageIO.read(getClass().getResource(ruta));
                hojas.add(new Hoja(i, img));
            }
        } catch (Exception e) {
            System.out.println("❌ Error cargando las hojas del escritorio: " + e.getMessage());
        }

        // --- CAMBIO 4: Ajustar matemáticas para 6 espacios ---
        int anchoHoja = 160;
        int altoHoja = 225;
        int espacio = 20;
        int startX = 45; // Margen izquierdo
        int startY = 150;

        for (int i = 0; i < 6; i++) {
            slots[i] = new Rectangle(startX + (i * (anchoHoja + espacio)), startY, anchoHoja, altoHoja);
        }

        do {
            Collections.shuffle(hojas);
            for (int i = 0; i < hojas.size(); i++) {
                hojas.get(i).slotActual = i;
                hojas.get(i).bounds = new Rectangle(slots[i]);
            }
        } while (verificarVictoriaSilenciosa());

        panelMinijuego.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                for (int i = hojas.size() - 1; i >= 0; i--) { 
                    Hoja h = hojas.get(i);
                    if (h.bounds.contains(e.getPoint())) {
                        hojaArrastrada = h;
                        offsetX = e.getX() - h.bounds.x;
                        offsetY = e.getY() - h.bounds.y;
                        
                        hojas.remove(h);
                        hojas.add(h);
                        break;
                    }
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (hojaArrastrada != null) {
                    int slotDestino = hojaArrastrada.slotActual; 
                    
                    // --- CAMBIO 5: Revisar colisión con los 6 slots ---
                    for (int i = 0; i < 6; i++) {
                        if (slots[i].intersects(hojaArrastrada.bounds)) {
                            slotDestino = i;
                            break;
                        }
                    }

                    Hoja hojaEnDestino = null;
                    for (Hoja h : hojas) {
                        if (h != hojaArrastrada && h.slotActual == slotDestino) {
                            hojaEnDestino = h;
                            break;
                        }
                    }

                    if (hojaEnDestino != null) {
                        hojaEnDestino.slotActual = hojaArrastrada.slotActual;
                        hojaEnDestino.bounds.setLocation(slots[hojaEnDestino.slotActual].getLocation());
                    }

                    hojaArrastrada.slotActual = slotDestino;
                    hojaArrastrada.bounds.setLocation(slots[slotDestino].getLocation());

                    hojaArrastrada = null;
                    panelMinijuego.repaint();

                    if (verificarVictoriaSilenciosa()) {
                        System.out.println("✅ ¡Trabajo organizado correctamente!");
                        panelPadre.completarMisionOficina(); 
                        dispose(); 
                    }
                }
            }
        });

        panelMinijuego.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (hojaArrastrada != null) {
                    hojaArrastrada.bounds.x = e.getX() - offsetX;
                    hojaArrastrada.bounds.y = e.getY() - offsetY;
                    panelMinijuego.repaint(); 
                }
            }
        });

        // --- CAMBIO 6: Mover la "X" al nuevo borde derecho ---
        JButton btnCerrar = new JButton("X");
        btnCerrar.setBounds(1090, 10, 50, 40); 
        btnCerrar.setBackground(Color.RED);
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 20));
        btnCerrar.setFocusPainted(false);
        btnCerrar.addActionListener(e -> dispose());
        panelMinijuego.add(btnCerrar);
    }

    private boolean verificarVictoriaSilenciosa() {
        for (Hoja h : hojas) {
            if (h.slotActual + 1 != h.idReal) {
                return false;
            }
        }
        return true;
    }

    class PanelMiniJuego extends JPanel {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            g2.setColor(new Color(101, 67, 33));
            g2.fillRect(0, 0, getWidth(), getHeight());

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 32));
            // --- CAMBIO 7: Centrar el título en la nueva ventana ---
            g2.drawString("Ordena el Trabajo Final (Arrastra y Suelta)", 250, 70); 

            g2.setColor(new Color(70, 45, 20));
            for (Rectangle r : slots) {
                g2.fillRect(r.x, r.y, r.width, r.height);
            }

            for (Hoja h : hojas) {
                if (h.img != null) {
                    g2.drawImage(h.img, h.bounds.x, h.bounds.y, h.bounds.width, h.bounds.height, null);
                } else {
                    g2.setColor(Color.WHITE);
                    g2.fillRect(h.bounds.x, h.bounds.y, h.bounds.width, h.bounds.height);
                    g2.setColor(Color.BLACK);
                    g2.drawRect(h.bounds.x, h.bounds.y, h.bounds.width, h.bounds.height);
                    g2.setFont(new Font("Arial", Font.BOLD, 20));
                    g2.drawString("Pág " + h.idReal, h.bounds.x + 35, h.bounds.y + 90);
                }
            }
        }
    }

    class Hoja {
        int idReal; 
        BufferedImage img; 
        Rectangle bounds; 
        int slotActual; 

        public Hoja(int idReal, BufferedImage img) {
            this.idReal = idReal;
            this.img = img;
        }
    }
}