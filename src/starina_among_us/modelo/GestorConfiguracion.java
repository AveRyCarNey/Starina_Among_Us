/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package starina_among_us.modelo;

import java.io.*;
import java.util.Properties;

public class GestorConfiguracion {
    private static final String ARCHIVO_CONFIG = "config.xml";
    private static Properties props = new Properties();

    // Valores por defecto
    public static float volumenMusica = 0.8f;
    public static float volumenSFX = 0.8f;
    public static String idioma = "es";
    public static boolean antialiasing = true;

    public static void cargar() {
        try (InputStream is = new FileInputStream(ARCHIVO_CONFIG)) {
            props.loadFromXML(is);
            volumenMusica = Float.parseFloat(props.getProperty("musica", "0.8"));
            volumenSFX = Float.parseFloat(props.getProperty("sfx", "0.8"));
            idioma = props.getProperty("idioma", "es");
            antialiasing = Boolean.parseBoolean(props.getProperty("antialiasing", "true"));
        } catch (IOException e) {
            System.out.println("Configuración no encontrada, usando valores por defecto.");
            guardar(); // Crea el archivo por primera vez
        }
    }

    public static void guardar() {
        try (OutputStream os = new FileOutputStream(ARCHIVO_CONFIG)) {
            props.setProperty("musica", String.valueOf(volumenMusica));
            props.setProperty("sfx", String.valueOf(volumenSFX));
            props.setProperty("idioma", idioma);
            props.setProperty("antialiasing", String.valueOf(antialiasing));
            props.storeToXML(os, "Configuración de Starina Among Us");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}