package starina_among_us.modelo;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class HerramientasImagen {

    // Método para recortar un pedazo de la imagen grande
    public static BufferedImage recortar(BufferedImage hojaSprites, int x, int y, int ancho, int alto) {
        return hojaSprites.getSubimage(x, y, ancho, alto);
    }

    // Método para crear una versión "fantasma" (semitransparente)
    public static BufferedImage hacerTransparente(BufferedImage original, float opacidad) {
        BufferedImage translucida = new BufferedImage(
                original.getWidth(), 
                original.getHeight(), 
                BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2 = translucida.createGraphics();
        // Configuramos la transparencia (0.0 es invisible, 1.0 es opaco, 0.5 es mitad)
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacidad));
        g2.drawImage(original, 0, 0, null);
        g2.dispose();
        
        return translucida;
    }
}