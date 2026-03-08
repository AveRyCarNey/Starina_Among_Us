package starina_among_us.modelo;

import java.io.*;
import java.util.Properties;

/**
 * Gestor encargado de la persistencia de las opciones graficas y de sonido del usuario.
 * Lee y guarda los datos en un archivo 'config.xml' en el disco duro, permitiendo 
 * que el volumen y el idioma elegido no se pierdan al cerrar el juego.
 * * @author Wulliber Yepez, Carlos Ramirez, Jorg Sierra, Samuel Salazar
 * @version 1.0
 */
public class GestorConfiguracion {
    private static final String ARCHIVO_CONFIG = "config.xml";
    private static Properties props = new Properties();

    public static float volumenMusica = 0.8f;
    public static float volumenSFX = 0.8f;
    public static String idioma = "es";
    public static boolean antialiasing = true;

    /**
     * Intenta leer el archivo de configuracion del disco. Si lo encuentra,
     * sobreescribe las variables locales. Si no existe o esta corrupto,
     * crea uno nuevo con los valores predeterminados.
     */
    public static void cargar() {
        try (InputStream is = new FileInputStream(ARCHIVO_CONFIG)) {
            props.loadFromXML(is);
            volumenMusica = Float.parseFloat(props.getProperty("musica", "0.8"));
            volumenSFX = Float.parseFloat(props.getProperty("sfx", "0.8"));
            idioma = props.getProperty("idioma", "es");
            antialiasing = Boolean.parseBoolean(props.getProperty("antialiasing", "true"));
        } catch (IOException e) {
            System.out.println("Configuracion no encontrada, usando valores por defecto.");
            guardar(); 
        }
    }

    /**
     * Serializa las variables estaticas actuales a la clase Properties y
     * las guarda permanentemente en formato XML en el archivo de configuracion local.
     */
    public static void guardar() {
        try (OutputStream os = new FileOutputStream(ARCHIVO_CONFIG)) {
            props.setProperty("musica", String.valueOf(volumenMusica));
            props.setProperty("sfx", String.valueOf(volumenSFX));
            props.setProperty("idioma", idioma);
            props.setProperty("antialiasing", String.valueOf(antialiasing));
            props.storeToXML(os, "Configuracion de Starina Among Us");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}