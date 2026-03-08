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

/**
 * Representa una entidad de personaje en el juego, ya sea controlada localmente 
 * o replicada desde un cliente de red.
 * Maneja la logica de animacion, fisicas de estado (vivo/muerto, en ventilacion) 
 * y almacena el color y rol asignado.
 * * @author Wulliber Yepez, Carlos Ramirez, Jorg Sierra, Samuel Salazar
 * @version 1.0
 */
public class Jugador {
    
    private int id;
    private String nombre;
    private double x, y;
    private double velocidad;
    private boolean estaVivo;
    private boolean esImpostor;
    private Color colorPersonaje;
    
    private boolean moviendose = false; 
    private boolean mirandoDerecha = true; 
    private boolean cuerpoReportado = false;
    private boolean haVotado = false;
    
    private BufferedImage imgQuietoOriginal, imgMuertoOriginal;
    private BufferedImage imgQuietoPintada, imgMuertoPintada;
    private BufferedImage[] animacionWalkOriginal; 
    private BufferedImage[] animacionWalkPintada;  
    
    private int frameActual = 0;
    private long tiempoUltimoFrame = 0;
    private final int VELOCIDAD_ANIMACION = 60; 
    
    private final int ANCHO = 50; 
    private final int ALTO = 60;
    private int retardoAnimacion = 0;
    
    private boolean enVentilacion = false;
    private boolean animandoVent = false;
    
    private java.awt.Color colorOriginal;

    /**
     * Constructor del Jugador.
     * Crea la instancia, asigna las coordenadas iniciales y precarga todas las imagenes
     * necesarias para la animacion de caminata y estado de cadaver.
     * * @param id Identificador unico de red.
     * @param nombre Nombre a mostrar sobre la cabeza.
     * @param x Coordenada de aparicion en el eje X.
     * @param y Coordenada de aparicion en el eje Y.
     * @param esImpostor Define el rol del jugador en la partida.
     */
    public Jugador(int id, String nombre, double x, double y, boolean esImpostor) {
        this.id = id;
        this.nombre = nombre;
        this.x = x;
        this.y = y;
        this.esImpostor = esImpostor;
        this.velocidad = 5; 
        this.estaVivo = true;
        
        this.colorPersonaje = new Color(197, 17, 17); 
        this.colorOriginal = this.colorPersonaje;
        
        cargarImagenes();
        cambiarSkin(this.colorPersonaje);
    }
    
    private void cargarImagenes() {
        try {
            imgQuietoOriginal = ImageIO.read(getClass().getResource("/starina_among_us/recursos/personajes/rozul.png"));
            imgMuertoOriginal = ImageIO.read(getClass().getResource("/starina_among_us/recursos/personajes/rozul_dead.png"));
            
            animacionWalkOriginal = new BufferedImage[12];
            animacionWalkPintada = new BufferedImage[12];
            
            for (int i = 0; i < 12; i++) {
                String ruta = "/starina_among_us/recursos/personajes/walk_" + i + ".png";
                animacionWalkOriginal[i] = ImageIO.read(getClass().getResource(ruta));
                animacionWalkPintada[i] = animacionWalkOriginal[i];
            }
        } catch (Exception e) {
            System.out.println("Error cargando sprites: " + e.getMessage());
            e.printStackTrace(); 
        }
    }
    
    /**
     * Aplica un nuevo color a todas las imagenes cargadas del jugador utilizando
     * la herramienta de reemplazo de pixeles.
     * * @param nuevoColor El color RGB a aplicar.
     */
    public void cambiarSkin(Color nuevoColor) {
        this.colorPersonaje = nuevoColor;
        
        if (imgQuietoOriginal != null) {
            imgQuietoPintada = HerramientasColor.crearPersonaje(imgQuietoOriginal, nuevoColor);
        }
        if (imgMuertoOriginal != null) {
            imgMuertoPintada = HerramientasColor.crearPersonaje(imgMuertoOriginal, nuevoColor);
        }
        
        if (animacionWalkOriginal != null) {
            for (int i = 0; i < animacionWalkOriginal.length; i++) {
                if (animacionWalkOriginal[i] != null) {
                    animacionWalkPintada[i] = HerramientasColor.crearPersonaje(animacionWalkOriginal[i], nuevoColor);
                }
            }
        }
    }

    /**
     * Avanza el contador de frames de la animacion de caminado.
     * Se llama en cada iteracion del reloj principal si el jugador se esta moviendo.
     */
    public void actualizarAnimacion() {
        if (moviendose) {
            retardoAnimacion++;
            if (retardoAnimacion > 3) { 
                frameActual++; 
                if (frameActual >= animacionWalkPintada.length) {
                    frameActual = 0;
                }
                retardoAnimacion = 0; 
            }
        } else {
            frameActual = 0;
        }
    }
    
    public void detener() {
        this.moviendose = false;
    }
    
    /**
     * Dibuja al jugador en el lienzo de la ventana principal.
     * Aplica transformaciones de espejo dependiendo de la direccion a la que mira,
     * y aplica opacidad si el jugador es un fantasma.
     * * @param g El contexto grafico proporcionado por Swing.
     * @param panelObservador El JPanel donde se dibuja.
     */
    public void dibujar(Graphics g, JPanel panelObservador) {
        Image imagenActual;
        boolean esFantasma = false; 

        if (!estaVivo) {
            if (cuerpoReportado) {
                imagenActual = imgMuertoPintada; 
                esFantasma = true; 
            } else {
                imagenActual = imgMuertoPintada;
            }
        } else if (moviendose) {
            if (frameActual < animacionWalkPintada.length) {
                imagenActual = animacionWalkPintada[frameActual];
            } else {
                imagenActual = imgQuietoPintada;
            }
        } else {
            imagenActual = imgQuietoPintada;
        }

        if (imagenActual != null) {
            Graphics2D g2 = (Graphics2D) g;
            Composite originalComposite = g2.getComposite(); 

            if (esFantasma) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            }

            if (mirandoDerecha) {
                g2.drawImage(imagenActual, (int)x, (int)y, ANCHO, ALTO, panelObservador);
            } else {
                g2.drawImage(imagenActual, (int)x + ANCHO, (int)y, -ANCHO, ALTO, panelObservador);
            }

            if (esFantasma) {
                g2.setComposite(originalComposite);
            }

            g.setColor(Color.WHITE);
            g.drawString(nombre, (int)x + 10, (int)y - 5);
        }
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getId() { return this.id; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public boolean isVivo() { return estaVivo; }
    public boolean esImpostor() { return esImpostor; }
    public void setVivo(boolean vivo) { this.estaVivo = vivo; }
    public void setImpostor(boolean esImpostor) { this.esImpostor = esImpostor; }
    public void setCuerpoReportado(boolean reportado) { this.cuerpoReportado = reportado; }
    public String getNombre(){ return this.nombre; }
    public boolean getEsImpostor(){ return this.esImpostor; }
    public boolean isCuerpoReportado() { return cuerpoReportado; }
    public boolean isMoviendose() { return moviendose; }
    public void setMoviendose(boolean moviendose) { this.moviendose = moviendose; }
    public boolean isMirandoDerecha() { return mirandoDerecha; }
    public void setMirandoDerecha(boolean mirandoDerecha) { this.mirandoDerecha = mirandoDerecha; }
    public boolean isHaVotado() { return haVotado; }
    public void setHaVotado(boolean haVotado) { this.haVotado = haVotado; }
    public boolean isEnVentilacion() { return enVentilacion; }
    public void setEnVentilacion(boolean enVentilacion) { this.enVentilacion = enVentilacion; }
    
    /**
     * Aplica un color prestablecido segun un ID numerico.
     * * @param colorID Numero del 1 al 5 que representa un color.
     */
    public void setColorManual(int colorID) {
        Color realColor;
        switch(colorID) {
            case 1: realColor = Color.BLUE; break;
            case 2: realColor = Color.GREEN; break;
            case 3: realColor = Color.YELLOW; break;
            case 4: realColor = Color.PINK; break;
            case 5: realColor = Color.CYAN; break;
            default: realColor = new Color(197, 17, 17); break; 
        }
        this.colorPersonaje = realColor;
        this.colorOriginal = realColor; 
        this.cambiarSkin(realColor); 
    }

    public void setColorRGB(int r, int g, int b) {
        this.colorPersonaje = new Color(r, g, b);
        this.colorOriginal = this.colorPersonaje; 
        this.cambiarSkin(this.colorPersonaje);
    }

    public Color getColor() { return this.colorPersonaje; }
    public java.awt.Color getColorOriginal() { return this.colorOriginal; }

    public void setColorTemporal(java.awt.Color nuevoColor) {
        this.colorPersonaje = nuevoColor; 
        this.cambiarSkin(nuevoColor);
    }
    
    public boolean isAnimandoVent() { return animandoVent; }
    public void setAnimandoVent(boolean animandoVent) { this.animandoVent = animandoVent; }
}