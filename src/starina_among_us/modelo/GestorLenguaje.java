package starina_among_us.modelo;

import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Gestor encargado de la internacionalizacion del juego (i18n).
 * Carga dinamicamente archivos XML de traducciones y provee los textos
 * correspondientes a la interfaz de usuario en el idioma seleccionado.
 * * @author Wulliber Yepez, Carlos Ramirez, Jorg Sierra, Samuel Salazar
 * @version 1.0
 */
public class GestorLenguaje {
    private static HashMap<String, String> textos = new HashMap<>();
    private static String idiomaActual = "es"; 

    /**
     * Lee un archivo XML de la carpeta de recursos correspondiente al idioma
     * especificado y almacena todas las traducciones en memoria.
     * * @param iso El codigo ISO de dos letras del idioma (ej. "es" para espanol, "en" para ingles).
     */
    public static void cargarIdioma(String iso) {
        idiomaActual = iso;
        try {
            String ruta = "/starina_among_us/recursos/idiomas/texts_" + iso + ".xml";
            InputStream in = GestorLenguaje.class.getResourceAsStream(ruta);
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
            
            textos.clear();
            
            org.w3c.dom.NodeList nodos = doc.getElementsByTagName("*");
            for (int i = 0; i < nodos.getLength(); i++) {
                Element elemento = (Element) nodos.item(i);
                if (elemento.getChildNodes().getLength() == 1) { 
                    textos.put(elemento.getTagName(), elemento.getTextContent());
                }
            }
        } catch (Exception e) {
            System.out.println("Error cargando idioma: " + e.getMessage());
        }
    }

    /**
     * Busca y devuelve el texto traducido asociado a una clave especifica.
     * Si la clave no existe en el archivo XML, devuelve un texto de advertencia
     * visible en pantalla para facilitar la depuracion.
     * * @param llave El identificador unico del texto (ej. "btn_iniciar").
     * @return El texto traducido correspondiente, o una cadena de error si no se encuentra.
     */
    public static String get(String llave) {
        return textos.getOrDefault(llave, "!! " + llave + " !!");
    }
}