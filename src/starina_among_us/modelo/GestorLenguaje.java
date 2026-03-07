/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package starina_among_us.modelo;

import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.InputStream;
import java.util.HashMap;

public class GestorLenguaje {
    private static HashMap<String, String> textos = new HashMap<>();
    private static String idiomaActual = "es"; // Por defecto español

    public static void cargarIdioma(String iso) {
        idiomaActual = iso;
        try {
            String ruta = "/starina_among_us/recursos/idiomas/texts_" + iso + ".xml";
            InputStream in = GestorLenguaje.class.getResourceAsStream(ruta);
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
            
            // Limpiamos los textos anteriores
            textos.clear();
            
            // Recorremos todos los elementos del XML y los guardamos en el mapa
            org.w3c.dom.NodeList nodos = doc.getElementsByTagName("*");
            for (int i = 0; i < nodos.getLength(); i++) {
                Element elemento = (Element) nodos.item(i);
                if (elemento.getChildNodes().getLength() == 1) { // Solo si es un texto final
                    textos.put(elemento.getTagName(), elemento.getTextContent());
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error cargando idioma: " + e.getMessage());
        }
    }

    public static String get(String llave) {
        return textos.getOrDefault(llave, "!! " + llave + " !!");
    }
}
