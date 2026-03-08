package starina_among_us.modelo;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Clase utilitaria que provee metodos estaticos para la manipulacion
 * directa de imagenes y graficos 2D. Se utiliza principalmente para extraer 
 * sprites de hojas de texturas y aplicar filtros visuales.
 * * @author Wulliber Yepez, Carlos Ramirez, Jorg Sierra, Samuel Salazar
 * @version 1.0
 */
public class HerramientasImagen {

    /**
     * Extrae un sub-rectangulo de una imagen mayor.
     * Util para cargar botones o iconos que vienen agrupados en un solo archivo.
     * * @param hojaSprites La imagen original completa.
     * @param x Coordenada X donde empieza el recorte.
     * @param y Coordenada Y donde empieza el recorte.
     * @param ancho Ancho en pixeles del recorte.
     * @param alto Alto en pixeles del recorte.
     * @return Una nueva BufferedImage que contiene solo la porcion seleccionada.
     */
    public static BufferedImage recortar(BufferedImage hojaSprites, int x, int y, int ancho, int alto) {
        return hojaSprites.getSubimage(x, y, ancho, alto);
    }

    /**
     * Aplica un filtro de transparencia global a una imagen dada.
     * Se usa para crear efectos de fantasma o deshabilitar botones visualmente.
     * * @param original La imagen base que se desea modificar.
     * @param opacidad Nivel de transparencia (0.0f es totalmente invisible, 1.0f es totalmente opaco).
     * @return Una nueva BufferedImage con el canal Alpha modificado.
     */
    public static BufferedImage hacerTransparente(BufferedImage original, float opacidad) {
        BufferedImage translucida = new BufferedImage(
                original.getWidth(), 
                original.getHeight(), 
                BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2 = translucida.createGraphics();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacidad));
        g2.drawImage(original, 0, 0, null);
        g2.dispose();
        
        return translucida;
    }
}