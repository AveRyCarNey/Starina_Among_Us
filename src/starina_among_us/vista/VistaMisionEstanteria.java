package starina_among_us.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import starina_among_us.modelo.GestorSonido;
import starina_among_us.modelo.GestorLenguaje;

/**
 * Ventana emergente para la mision de ordenar la estanteria metalica.
 * Implementa una mecanica interactiva de Drag and Drop y un 
 * sistema progresivo de 3 fases (Orden ascendente, descendente y aleatorio).
 * * @author Wulliber Yepez, Carlos Ramirez, Jorg Sierra, Samuel Salazar
 * @version 1.0
 */
public class VistaMisionEstanteria extends JDialog {

    private PanelJuego panelPadre;
    
    private int faseActual = 1;
    private String textoInstruccion = "";

    /**
     * Clase interna que representa los espacios de destino en la estanteria.
     */
    private class Hueco {
        int numeroRequerido;
        Rectangle bounds;
        public Hueco(int num, int x, int y, int w, int h) {
            this.numeroRequerido = num;
            this.bounds = new Rectangle(x, y, w, h);
        }
    }
    
    /**
     * Clase interna que representa las cajas fisicas interactuables por el jugador.
     */
    private class Caja {
        int numero;
        Rectangle bounds;
        Point posicionOriginal;
        boolean colocadaCorrectamente = false;
        
        public Caja(int num) {
            this.numero = num;
            this.bounds = new Rectangle(0, 0, 60, 60); 
        }
    }

    private ArrayList<Hueco> listaHuecos = new ArrayList<>();
    private ArrayList<Caja> listaCajas = new ArrayList<>();
    
    private Caja cajaArrastrada = null;
    private int ratonOffsetX = 0;
    private int ratonOffsetY = 0;
    
    private PanelJuegoArrastrable panelFondo;

    /**
     * Constructor principal del minijuego de la estanteria.
     * * @param panelPadre Referencia al panel principal del juego para notificacion de exito.
     */
    public VistaMisionEstanteria(PanelJuego panelPadre) {
        super((JFrame) SwingUtilities.getWindowAncestor(panelPadre), "Estanteria", true);
        this.panelPadre = panelPadre;
        
        setSize(550, 550);
        setLocationRelativeTo(panelPadre);
        setUndecorated(true);
        
        panelFondo = new PanelJuegoArrastrable();
        panelFondo.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 4));
        setContentPane(panelFondo);
        
        cargarFase();
    }

    /**
     * Configura el nivel de dificultad y distribuye las cajas y huecos
     * dependiendo de la fase actual del minijuego.
     */
    private void cargarFase() {
        listaHuecos.clear();
        listaCajas.clear();
        
        int boxSize = 60;
        int gap = 20;
        int startX = (getWidth() - (5 * boxSize + 4 * gap)) / 2; 
        
        ArrayList<Integer> numerosParaHuecos = new ArrayList<>();
        for (int i = 1; i <= 10; i++) numerosParaHuecos.add(i);

        if (faseActual == 1) {
            textoInstruccion = GestorLenguaje.get("lbl_estanteria_fase1");
        } else if (faseActual == 2) {
            textoInstruccion = GestorLenguaje.get("lbl_estanteria_fase2");
            Collections.reverse(numerosParaHuecos); 
        } else if (faseActual == 3) {
            textoInstruccion = GestorLenguaje.get("lbl_estanteria_fase3");
            Collections.shuffle(numerosParaHuecos); 
        }

        int huecoStartY = 80;
        int indexHueco = 0;
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 5; col++) {
                int x = startX + col * (boxSize + gap);
                int y = huecoStartY + row * (boxSize + gap);
                int numeroExigido = numerosParaHuecos.get(indexHueco);
                listaHuecos.add(new Hueco(numeroExigido, x, y, boxSize, boxSize));
                indexHueco++;
            }
        }
        
        for (int i = 1; i <= 10; i++) {
            listaCajas.add(new Caja(i));
        }

        ArrayList<Point> posicionesBase = new ArrayList<>();
        int cajaStartY = 350;
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 5; col++) {
                int x = startX + col * (boxSize + gap);
                int y = cajaStartY + row * (boxSize + gap);
                posicionesBase.add(new Point(x, y));
            }
        }
        
        Collections.shuffle(posicionesBase);
        for (int i = 0; i < 10; i++) {
            Caja c = listaCajas.get(i);
            c.posicionOriginal = posicionesBase.get(i);
            c.bounds.setLocation(c.posicionOriginal);
        }
        
        if (panelFondo != null) panelFondo.repaint();
    }

    /**
     * Resetea el progreso de la fase actual en caso de error del jugador.
     */
    private void reiniciarPorError() {
        GestorSonido.jugar("general_sounds/Panel_GenericDisappear.wav");
        System.out.println("Error en posicionamiento. Reiniciando cajas de esta fase.");
        
        for (Caja c : listaCajas) {
            c.bounds.setLocation(c.posicionOriginal);
            c.colocadaCorrectamente = false;
        }
        panelFondo.repaint();
    }

    /**
     * Verifica la validez del estado de las cajas para avanzar o finalizar la mision.
     */
    private void verificarVictoria() {
        boolean faseCompletada = true;
        for (Caja c : listaCajas) {
            if (!c.colocadaCorrectamente) {
                faseCompletada = false;
                break;
            }
        }
        
        if (faseCompletada) {
            if (faseActual >= 3) {
                System.out.println("Estanteria organizada correctamente.");
                panelPadre.completarMisionEstanteria(); 
                dispose();
            } else {
                faseActual++;
                GestorSonido.jugar("general_sounds/task_Inprogress.wav"); 
                
                Timer t = new Timer(500, e -> {
                    cargarFase();
                });
                t.setRepeats(false);
                t.start();
            }
        }
    }

    /**
     * Panel personalizado que maneja el renderizado y los eventos del raton.
     */
    private class PanelJuegoArrastrable extends JPanel {
        
        public PanelJuegoArrastrable() {
            setBackground(new Color(40, 50, 60)); 
            setLayout(null);
            
            JButton btnCerrar = new JButton("X");
            btnCerrar.setBounds(490, 10, 45, 45);
            btnCerrar.setBackground(Color.RED);
            btnCerrar.setForeground(Color.WHITE);
            btnCerrar.setFont(new Font("Arial", Font.BOLD, 20));
            btnCerrar.setFocusPainted(false);
            btnCerrar.addActionListener(e -> dispose());
            add(btnCerrar);

            MouseAdapter mouseHandler = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    for (int i = listaCajas.size() - 1; i >= 0; i--) {
                        Caja c = listaCajas.get(i);
                        if (!c.colocadaCorrectamente && c.bounds.contains(e.getPoint())) {
                            cajaArrastrada = c;
                            ratonOffsetX = e.getX() - c.bounds.x;
                            ratonOffsetY = e.getY() - c.bounds.y;
                            break;
                        }
                    }
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    if (cajaArrastrada != null) {
                        cajaArrastrada.bounds.setLocation(e.getX() - ratonOffsetX, e.getY() - ratonOffsetY);
                        repaint();
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (cajaArrastrada != null) {
                        Point centroCaja = new Point(
                            cajaArrastrada.bounds.x + cajaArrastrada.bounds.width / 2,
                            cajaArrastrada.bounds.y + cajaArrastrada.bounds.height / 2
                        );
                        
                        boolean soltadoEnHueco = false;
                        
                        for (Hueco h : listaHuecos) {
                            if (h.bounds.contains(centroCaja)) {
                                soltadoEnHueco = true;
                                if (h.numeroRequerido == cajaArrastrada.numero) {
                                    cajaArrastrada.bounds.setLocation(h.bounds.getLocation());
                                    cajaArrastrada.colocadaCorrectamente = true;
                                    GestorSonido.jugar("general_sounds/Panel_Click.wav");
                                    verificarVictoria();
                                } else {
                                    reiniciarPorError();
                                }
                                break;
                            }
                        }
                        
                        if (!soltadoEnHueco) {
                            cajaArrastrada.bounds.setLocation(cajaArrastrada.posicionOriginal);
                        }
                        
                        cajaArrastrada = null;
                        repaint();
                    }
                }
            };
            
            addMouseListener(mouseHandler);
            addMouseMotionListener(mouseHandler);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 22));
            int tw = g2.getFontMetrics().stringWidth(textoInstruccion);
            g2.drawString(textoInstruccion, (getWidth() - tw) / 2, 40);

            Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
            Stroke solid = new BasicStroke(2);

            for (Hueco h : listaHuecos) {
                g2.setColor(new Color(80, 90, 100));
                g2.fillRect(h.bounds.x, h.bounds.y, h.bounds.width, h.bounds.height);
                
                g2.setColor(new Color(120, 130, 140));
                g2.setStroke(dashed);
                g2.drawRect(h.bounds.x, h.bounds.y, h.bounds.width, h.bounds.height);
                
                g2.setColor(new Color(150, 160, 170, 100));
                g2.setFont(new Font("Arial", Font.BOLD, 30));
                String numStr = String.valueOf(h.numeroRequerido);
                int nw = g2.getFontMetrics().stringWidth(numStr);
                g2.drawString(numStr, h.bounds.x + (h.bounds.width - nw)/2, h.bounds.y + 40);
            }

            g2.setStroke(solid);

            for (Caja c : listaCajas) {
                if (c != cajaArrastrada) {
                    dibujarCaja(g2, c);
                }
            }
            
            if (cajaArrastrada != null) {
                g2.setColor(new Color(0, 0, 0, 100));
                g2.fillRect(cajaArrastrada.bounds.x + 5, cajaArrastrada.bounds.y + 5, cajaArrastrada.bounds.width, cajaArrastrada.bounds.height);
                dibujarCaja(g2, cajaArrastrada);
            }
        }
        
        private void dibujarCaja(Graphics2D g2, Caja c) {
            if (c.colocadaCorrectamente) {
                g2.setColor(new Color(80, 200, 80)); 
            } else {
                g2.setColor(new Color(210, 150, 80));
            }
            g2.fillRect(c.bounds.x, c.bounds.y, c.bounds.width, c.bounds.height);
            
            g2.setColor(new Color(130, 80, 30));
            g2.drawRect(c.bounds.x, c.bounds.y, c.bounds.width, c.bounds.height);
            
            g2.setColor(new Color(230, 180, 100, 150));
            g2.fillRect(c.bounds.x, c.bounds.y + (c.bounds.height/2) - 5, c.bounds.width, 10);
            
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 28));
            String numStr = String.valueOf(c.numero);
            int nw = g2.getFontMetrics().stringWidth(numStr);
            g2.drawString(numStr, c.bounds.x + (c.bounds.width - nw)/2, c.bounds.y + 40);
        }
    }
}