package starina_among_us.modelo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Jugador {
    
    // ATRIBUTOS
    private int id;
    private String nombre;
    private double x, y;
    private double velocidad;
    private boolean estaVivo;
    private boolean esImpostor;
    private Color colorPersonaje;
    
    // ESTADOS
    private boolean moviendose = false; 
    private boolean mirandoDerecha = true; // NUEVO: Para saber a dónde mira
    
    // IMÁGENES
    private BufferedImage imgQuietoOriginal, imgMuertoOriginal;
    private BufferedImage imgQuietoPintada, imgMuertoPintada;
    // Arrays para guardar los pasos (Frames)
    private BufferedImage[] animacionWalkOriginal; // Los originales
    private BufferedImage[] animacionWalkPintada;  // Los pintados (Skin actual)
    
    // Control de velocidad
    private int frameActual = 0;
    private long tiempoUltimoFrame = 0;
    private final int VELOCIDAD_ANIMACION = 60; // Milisegundos entre cada paso
    
    private final int ANCHO = 50; 
    private final int ALTO = 60;

    public Jugador(int id, String nombre, double x, double y, boolean esImpostor) {
        this.id = id;
        this.nombre = nombre;
        this.x = x;
        this.y = y;
        this.esImpostor = esImpostor;
        this.velocidad = 5; // Velocidad constante
        this.estaVivo = true;
        
        
        // 1. Definimos el color por defecto (Rojo Among Us)
        this.colorPersonaje = new Color(197, 17, 17); 
        
        // 2. Cargamos los moldes (Originales)
        cargarImagenes();
        
        // 3. --- ¡LA CORRECCIÓN MÁGICA! ---
        // Forzamos el pintado inmediato para que no se vea el molde morado al inicio.
        cambiarSkin(this.colorPersonaje);
        
        
    }
    
    private void cargarImagenes() {
        try {
            // Cargar estáticos
            imgQuietoOriginal = ImageIO.read(getClass().getResource("/starina_among_us/recursos/personajes/rozul.png"));
            imgMuertoOriginal = ImageIO.read(getClass().getResource("/starina_among_us/recursos/personajes/rozul_dead.png"));
            
            // --- CARGAR ANIMACIÓN (12 FRAMES) ---
            animacionWalkOriginal = new BufferedImage[12];
            animacionWalkPintada = new BufferedImage[12];
            
            for (int i = 0; i < 12; i++) {
                // Asumiendo que tus archivos se llaman "walk_0.png", "walk_1.png", etc.
                String ruta = "/starina_among_us/recursos/personajes/walk_" + i + ".png";
                animacionWalkOriginal[i] = ImageIO.read(getClass().getResource(ruta));
                
                // Al inicio, la "pintada" es igual a la original (roja)
                animacionWalkPintada[i] = animacionWalkOriginal[i];
            }
            
        } catch (Exception e) {
            System.out.println("Error cargando sprites: " + e.getMessage());
            e.printStackTrace(); // Útil para ver si falló el nombre del archivo
        }
    }
    
    public void cambiarSkin(Color nuevoColor) {
        this.colorPersonaje = nuevoColor;
        
        // Pintar estáticos
        if (imgQuietoOriginal != null) {
            imgQuietoPintada = HerramientasColor.crearPersonaje(imgQuietoOriginal, nuevoColor);
        }
        if (imgMuertoOriginal != null) {
            imgMuertoPintada = HerramientasColor.crearPersonaje(imgMuertoOriginal, nuevoColor);
        }
        
        // --- NUEVO: PINTAR ANIMACIÓN ---
        if (animacionWalkOriginal != null) {
            for (int i = 0; i < animacionWalkOriginal.length; i++) {
                if (animacionWalkOriginal[i] != null) {
                    animacionWalkPintada[i] = HerramientasColor.crearPersonaje(animacionWalkOriginal[i], nuevoColor);
                }
            }
        }
    }

    public void mover(double dx, double dy) {
        if (estaVivo) {
            double nuevaX = this.x + (dx * velocidad);
            double nuevaY = this.y + (dy * velocidad);
            
            // LÍMITES (Suponiendo ventana de 800x600 aprox)
            // Math.max(0, ...) impide que baje de 0
            // Math.min(740, ...) impide que pase de 740
            this.x = Math.max(0, Math.min(740, nuevaX));
            this.y = Math.max(0, Math.min(500, nuevaY));
            
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
            imagenActual = imgMuertoPintada;
        } else if (moviendose) {
            // --- LÓGICA DE ANIMACIÓN ---
            long tiempoActual = System.currentTimeMillis();
            
            // Si pasaron 100ms, avanzamos al siguiente frame
            if (tiempoActual - tiempoUltimoFrame > VELOCIDAD_ANIMACION) {
                frameActual++;
                if (frameActual >= animacionWalkPintada.length) {
                    frameActual = 0; // Volver al principio (Loop)
                }
                tiempoUltimoFrame = tiempoActual;
            }
            
            // Elegimos la foto que toca ahora
            imagenActual = animacionWalkPintada[frameActual];
        } else {
            frameActual = 0;
            imagenActual = imgQuietoPintada;
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
    public int getId() {
        return this.id;
    }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public boolean isVivo() { return estaVivo; }
    public boolean esImpostor() { return esImpostor; }
    
    public void setVivo(boolean vivo) { this.estaVivo = vivo; }
    public void setImpostor(boolean esImpostor) {
        this.esImpostor = esImpostor;
    }

    
}