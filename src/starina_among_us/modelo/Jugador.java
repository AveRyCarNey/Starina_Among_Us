package starina_among_us.modelo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Composite;
import java.awt.Image;
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
    private boolean cuerpoReportado = false;
    
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
    private int retardoAnimacion = 0;

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

    // Método exclusivo para manejar los frames de la animación
    public void actualizarAnimacion() {
        if (moviendose) {
            // SOLO LÓGICA DE FRAMES (Sin tocar X ni Y)
            retardoAnimacion++;
            
            if (retardoAnimacion > 3) { // Velocidad de la animación
                frameActual++; 
                
                if (frameActual >= animacionWalkPintada.length) {
                    frameActual = 0;
                }
                retardoAnimacion = 0; 
            }
        } else {
            // Si está quieto, reseteamos al frame 0
            frameActual = 0;
        }
    }
    
    public void detener() {
        this.moviendose = false;
    }
    
    public void dibujar(Graphics g, JPanel panelObservador) {

    Image imagenActual;
    boolean esFantasma = false; // Bandera para saber si aplicamos transparencia

    // --- 1. SELECCIÓN DE IMAGEN ---
    if (!estaVivo) {
        if (cuerpoReportado) {
            // FANTASMA: Muerto y reportado.
            // Usamos la imagen normal (de pie) pero será transparente
            imagenActual = imgMuertoPintada; 
            esFantasma = true; 
        } else {
            // CADÁVER RECIENTE: Huesito en el suelo (Opaco)
            imagenActual = imgMuertoPintada;
        }
    } 
    else if (moviendose) {
        if (frameActual < animacionWalkPintada.length) {
            imagenActual = animacionWalkPintada[frameActual];
        } else {
            imagenActual = imgQuietoPintada;
        }
    } 
    else {
        imagenActual = imgQuietoPintada;
    }

    // --- 2. DIBUJADO EN PANTALLA ---
    if (imagenActual != null) {

        Graphics2D g2 = (Graphics2D) g;
        Composite originalComposite = g2.getComposite(); // Guardar configuración original

        // SI ES FANTASMA -> 50% TRANSPARENCIA
        if (esFantasma) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        }

        // Dibujar (Normal o Espejo)
        if (mirandoDerecha) {
            g2.drawImage(imagenActual, (int)x, (int)y, ANCHO, ALTO, panelObservador);
        } else {
            g2.drawImage(imagenActual, (int)x + ANCHO, (int)y, -ANCHO, ALTO, panelObservador);
        }

        // RESTAURAR OPACIDAD (Para que el nombre se lea bien)
        if (esFantasma) {
            g2.setComposite(originalComposite);
        }

        // Dibujar Nombre
        g.setColor(Color.WHITE);
        g.drawString(nombre, (int)x + 10, (int)y - 5);
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
    public void setCuerpoReportado(boolean reportado) {
        this.cuerpoReportado = reportado;
    }
    public String getNombre(){return this.nombre;}
    public boolean isCuerpoReportado() {
    return cuerpoReportado;
}
    public boolean isMoviendose() {
        return moviendose;
    }

    public void setMoviendose(boolean moviendose) {
        this.moviendose = moviendose;
    }

    public boolean isMirandoDerecha() {
        return mirandoDerecha;
    }

    public void setMirandoDerecha(boolean mirandoDerecha) {
        this.mirandoDerecha = mirandoDerecha;
    }
    
    // Cambiamos el nombre para que no choque con setters automáticos y sea más claro
public void setColorManual(int colorID) {
    Color realColor;
    // Lista de colores según el ID
    switch(colorID) {
        case 1: realColor = Color.BLUE; break;
        case 2: realColor = Color.GREEN; break;
        case 3: realColor = Color.YELLOW; break;
        case 4: realColor = Color.PINK; break;
        case 5: realColor = Color.CYAN; break;
        default: realColor = new Color(197, 17, 17); break; // Rojo original
    }
    
    this.colorPersonaje = realColor;
    // Llamamos a cambiarSkin para regenerar las imágenes con el nuevo color
    this.cambiarSkin(realColor); 
}

public void setColorRGB(int r, int g, int b) {
    // Guardamos el objeto Color real
    this.colorPersonaje = new Color(r, g, b);
    
    // Ejecutamos la lógica de pintado de frames
    this.cambiarSkin(this.colorPersonaje);
}

    
}