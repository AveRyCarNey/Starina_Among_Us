package starina_among_us.modelo;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class HerramientasColor {

    public static BufferedImage crearPersonaje(BufferedImage mapaOriginal, Color colorJugador) {
        int ancho = mapaOriginal.getWidth();
        int alto = mapaOriginal.getHeight();
        
        BufferedImage nuevaSkin = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);

        // Color Sombra (30% más oscuro)
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

                // --- PASO 1: DETECTAR BORDES Y GRISES ---
                // Calculamos la diferencia entre el canal más fuerte y el más debil.
                // Si la diferencia es pequeña (ej. R=50, G=50, B=55), es un gris/negro.
                int max = Math.max(r, Math.max(g, b));
                int min = Math.min(r, Math.min(g, b));
                int saturacion = max - min;

                if (saturacion < 20) { 
                    // ES GRIS, NEGRO O BLANCO -> Lo dejamos original (Bordes y sombra suelo)
                    nuevaSkin.setRGB(x, y, pixel);
                    continue; 
                }

                // --- PASO 2: ASIGNAR COLOR (EL GANADOR SE LO LLEVA TODO) ---
                
                // Si el VERDE gana claramente -> VISOR
                if (g > r && g > b) {
                    Color visor = new Color(Math.min(255, g/2), g, Math.min(255, g + 50)); 
                    nuevaSkin.setRGB(x, y, visor.getRGB());
                }
                
                // Si el AZUL gana al rojo (aunque sea por poco) -> SOMBRA
                // Esto atrapa los pixeles morados (Rojo alto + Azul alto) donde el azul sea un pelín mayor.
                else if (b > r) {
                    nuevaSkin.setRGB(x, y, colorSombra.getRGB());
                }
                
                // CUALQUIER OTRA COSA que tenga color -> CUERPO
                // Esto atrapa el Rojo puro Y los morados donde el rojo gana.
                else {
                    nuevaSkin.setRGB(x, y, colorJugador.getRGB());
                }
            }
        }
        return nuevaSkin;
    }
}