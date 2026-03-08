package starina_among_us.modelo;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Clase utilitaria encargada del procesamiento y sustitucion de pixeles a nivel de mapa de bits.
 * Implementa el algoritmo que permite que los sprites base cambien de color dinamicamente
 * segun la eleccion del jugador en el lobby, respetando sombras y contornos.
 * * @author Wulliber Yepez, Carlos Ramirez, Jorg Sierra, Samuel Salazar
 * @version 1.0
 */
public class HerramientasColor {

    /**
     * Lee una imagen de molde pixel por pixel y reemplaza los canales RGB para aplicar
     * una nueva paleta de color al personaje, manteniendo el visor y el contorno intactos.
     * * @param mapaOriginal La imagen molde.
     * @param colorJugador El nuevo color que se desea aplicar al personaje.
     * @return Una nueva BufferedImage con la skin recoloreada.
     */
    public static BufferedImage crearPersonaje(BufferedImage mapaOriginal, Color colorJugador) {
        int ancho = mapaOriginal.getWidth();
        int alto = mapaOriginal.getHeight();
        
        BufferedImage nuevaSkin = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);

        Color colorSombra = new Color(
            Math.max(0, colorJugador.getRed() - 60),
            Math.max(0, colorJugador.getGreen() - 60),
            Math.max(0, colorJugador.getBlue() - 60)
        );

        for (int x = 0; x < ancho; x++) {
            for (int y = 0; y < alto; y++) {
                
                int pixel = mapaOriginal.getRGB(x, y);
                Color c = new Color(pixel, true);

                if (c.getAlpha() == 0) {
                    nuevaSkin.setRGB(x, y, pixel);
                    continue;
                }

                int r = c.getRed();
                int g = c.getGreen();
                int b = c.getBlue();

                int max = Math.max(r, Math.max(g, b));
                int min = Math.min(r, Math.min(g, b));
                int saturacion = max - min;

                if (saturacion < 20) { 
                    nuevaSkin.setRGB(x, y, pixel);
                    continue; 
                }

                if (g > r && g > b) {
                    Color visor = new Color(Math.min(255, g/2), g, Math.min(255, g + 50)); 
                    nuevaSkin.setRGB(x, y, visor.getRGB());
                } else if (b > r) {
                    nuevaSkin.setRGB(x, y, colorSombra.getRGB());
                } else {
                    nuevaSkin.setRGB(x, y, colorJugador.getRGB());
                }
            }
        }
        return nuevaSkin;
    }
}