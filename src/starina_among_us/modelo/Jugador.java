package starina_among_us.modelo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class Jugador {
    
    // ATRIBUTOS
    private String nombre;
    private int x, y;
    private int velocidad;
    private boolean estaVivo;
    private boolean esImpostor;
    
    // ESTADOS
    private boolean moviendose = false; 
    private boolean mirandoDerecha = true; // NUEVO: Para saber a dónde mira
    
    // IMÁGENES
    private Image imgQuieto;
    private Image imgCaminando;
    private Image imgMuerto;
    
    private final int ANCHO = 50; 
    private final int ALTO = 60;

    public Jugador(String nombre, int x, int y, boolean esImpostor) {
        this.nombre = nombre;
        this.x = x;
        this.y = y;
        this.esImpostor = esImpostor;
        this.velocidad = 5; // Velocidad constante
        this.estaVivo = true;
        
        try {
            imgQuieto = new ImageIcon(getClass().getResource("/starina_among_us/recursos/personajes/rozul.png")).getImage();
            imgMuerto = new ImageIcon(getClass().getResource("/starina_among_us/recursos/personajes/rozul_dead.png")).getImage();
            imgCaminando = new ImageIcon(getClass().getResource("/starina_among_us/recursos/personajes/rozul_walk.gif")).getImage();
        } catch (Exception e) {
            System.out.println("Error imágenes: " + e.getMessage());
        }
    }

    public void mover(int dx, int dy) {
        if (estaVivo) {
            this.x += dx * velocidad;
            this.y += dy * velocidad;
            
            // LÓGICA DE VOLTEAR
            if (dx > 0) {
                mirandoDerecha = true;
                moviendose = true;
            } else if (dx < 0) {
                mirandoDerecha = false;
                moviendose = true;
            } else if (dy != 0) {
                // Si solo se mueve verticalmente, sigue moviéndose pero mantiene el lado
                moviendose = true;
            }
        }
    }
    
    public void detener() {
        this.moviendose = false;
    }
    
    public void dibujar(Graphics g, JPanel panelObservador) {
        Image imagenActual;
        
        if (!estaVivo) {
            imagenActual = imgMuerto;
        } else if (moviendose) {
            imagenActual = imgCaminando;
        } else {
            imagenActual = imgQuieto;
        }
        
        if (imagenActual != null) {
            // TRUCO DEL ESPEJO:
            if (mirandoDerecha) {
                // Dibujo normal: (x, y) con ancho positivo
                g.drawImage(imagenActual, x, y, ANCHO, ALTO, panelObservador);
            } else {
                // Dibujo invertido: Empezamos en (x + ANCHO) y dibujamos hacia atrás con ancho negativo
                g.drawImage(imagenActual, x + ANCHO, y, -ANCHO, ALTO, panelObservador);
            }
            
            g.setColor(Color.WHITE);
            g.drawString(nombre, x + 10, y - 5);
        } else {
            g.setColor(Color.MAGENTA);
            g.fillRect(x, y, ANCHO, ALTO);
        }
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isVivo() { return estaVivo; }
    public void setVivo(boolean vivo) { this.estaVivo = vivo; }
}