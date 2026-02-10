package starina_among_us.modelo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class Jugador {
    
    // ATRIBUTOS
    private int id;
    private String nombre;
    private double x, y;
    private double velocidad;
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

    public Jugador(int id, String nombre, double x, double y, boolean esImpostor) {
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

    public void mover(double dx, double dy) {
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
                
                g.drawImage(imagenActual, (int)x, (int)y, ANCHO, ALTO, panelObservador);
            } else {
               
                g.drawImage(imagenActual, (int)x + ANCHO, (int)y, -ANCHO, ALTO, panelObservador);
            }
            
            g.setColor(Color.WHITE);
            
            g.drawString(nombre, (int)x + 10, (int)y - 5);
        } else {
            g.setColor(Color.MAGENTA);
            
            g.fillRect((int)x, (int)y, ANCHO, ALTO);
        }
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public boolean isVivo() { return estaVivo; }
    public void setVivo(boolean vivo) { this.estaVivo = vivo; }
}