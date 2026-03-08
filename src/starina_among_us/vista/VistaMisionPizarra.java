package starina_among_us.vista;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

/**
 * Ventana emergente que presenta un Quiz de Matematicas escolar.
 * Lee y carga preguntas dinamicamente desde un documento XML de recursos locales.
 * El jugador debe responder preguntas correctamente de manera consecutiva.
 * * @author Wulliber Yepez, Carlos Ramirez, Jorg Sierra, Samuel Salazar
 * @version 1.0
 */
public class VistaMisionPizarra extends JDialog {

    private PanelJuego panelPadre;
    private ArrayList<Pregunta> bancoPreguntas = new ArrayList<>();
    private int rachaCorrectas = 0;
    private int indicePreguntaActual = 0; 
    private JLabel labelPregunta;
    private JButton btnOp1, btnOp2, btnOp3;
    private JProgressBar barraProgreso;
    private String respuestaCorrectaActual;

    /**
     * Constructor principal del minijuego de la Pizarra.
     * * @param padre Instancia del panel base del juego para validacion de red.
     */
    public VistaMisionPizarra(PanelJuego padre) {
        super(SwingUtilities.getWindowAncestor(padre), "Quiz de Matematicas", Dialog.ModalityType.APPLICATION_MODAL);
        this.panelPadre = padre;
        
        setSize(800, 500);
        setLocationRelativeTo(padre);
        setUndecorated(true); 
        
        JPanel panelFondo = new JPanel();
        panelFondo.setBackground(new Color(34, 76, 46));
        panelFondo.setLayout(null);
        panelFondo.setBorder(BorderFactory.createLineBorder(new Color(101, 67, 33), 15)); 
        setContentPane(panelFondo);

        cargarPreguntasXML();

        JButton btnCerrar = new JButton("X");
        btnCerrar.setBounds(730, 20, 50, 40);
        btnCerrar.setBackground(Color.RED);
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 20));
        btnCerrar.setFocusPainted(false);
        btnCerrar.addActionListener(e -> dispose());
        panelFondo.add(btnCerrar);

        JLabel lblTitulo = new JLabel(starina_among_us.modelo.GestorLenguaje.get("lbl_quiz_titulo"), SwingConstants.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Comic Sans MS", Font.BOLD, 24)); 
        lblTitulo.setBounds(50, 40, 700, 30);
        panelFondo.add(lblTitulo);

        barraProgreso = new JProgressBar(0, 3);
        barraProgreso.setBounds(50, 100, 700, 25);
        barraProgreso.setValue(0);
        barraProgreso.setForeground(new Color(50, 200, 50));
        barraProgreso.setBackground(Color.DARK_GRAY);
        panelFondo.add(barraProgreso);

        labelPregunta = new JLabel("¿Pregunta?", SwingConstants.CENTER);
        labelPregunta.setForeground(Color.WHITE);
        labelPregunta.setFont(new Font("Arial", Font.BOLD, 28));
        labelPregunta.setBounds(50, 150, 700, 50);
        panelFondo.add(labelPregunta);

        btnOp1 = crearBotonRespuesta(50, 260);
        btnOp2 = crearBotonRespuesta(300, 260);
        btnOp3 = crearBotonRespuesta(550, 260);
        
        panelFondo.add(btnOp1);
        panelFondo.add(btnOp2);
        panelFondo.add(btnOp3);

        reiniciarQuiz();
    }

    /**
     * Accede al sistema de archivos local y parsea las etiquetas del archivo XML de
     * preguntas para popular la lista de juego.
     */
    private void cargarPreguntasXML() {
        try {
            String idiomaActual = starina_among_us.modelo.GestorConfiguracion.idioma;
            String rutaArchivo = "/starina_among_us/recursos/misiones/quiz_math_" + idiomaActual + ".xml";
            InputStream is = getClass().getResourceAsStream(rutaArchivo);
            
            if (is == null) {
                System.out.println("No se encontro archivo de idioma. Cargando el espanol por defecto...");
                is = getClass().getResourceAsStream("/starina_among_us/recursos/misiones/quiz_math_es.xml");
            }
            
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("pregunta");
            
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element elemento = (Element) nNode;
                    String txt = elemento.getElementsByTagName("texto").item(0).getTextContent();
                    String correcta = elemento.getElementsByTagName("correcta").item(0).getTextContent();
                    String falsa1 = elemento.getElementsByTagName("falsa1").item(0).getTextContent();
                    String falsa2 = elemento.getElementsByTagName("falsa2").item(0).getTextContent();
                    bancoPreguntas.add(new Pregunta(txt, correcta, falsa1, falsa2));
                }
            }
        } catch (Exception e) {
            System.out.println("Excepcion leyendo XML: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void reiniciarQuiz() {
        rachaCorrectas = 0;
        indicePreguntaActual = 0;
        barraProgreso.setValue(0);
        Collections.shuffle(bancoPreguntas); 
        cargarSiguientePregunta();
    }

    private JButton crearBotonRespuesta(int x, int y) {
        JButton btn = new JButton();
        btn.setBounds(x, y, 200, 150);
        btn.setFont(new Font("Arial", Font.BOLD, 20));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setMargin(new Insets(10, 10, 10, 10)); 
        
        btn.addActionListener(e -> verificarRespuesta(btn.getText()));
        return btn;
    }

    private void cargarSiguientePregunta() {
        if (bancoPreguntas.isEmpty()) return;

        if (indicePreguntaActual >= bancoPreguntas.size()) {
            Collections.shuffle(bancoPreguntas);
            indicePreguntaActual = 0;
        }

        Pregunta p = bancoPreguntas.get(indicePreguntaActual);
        indicePreguntaActual++; 
        
        respuestaCorrectaActual = p.correcta;
        labelPregunta.setText(p.texto);

        ArrayList<String> opciones = new ArrayList<>();
        opciones.add(p.correcta);
        opciones.add(p.falsa1);
        opciones.add(p.falsa2);
        Collections.shuffle(opciones);

        btnOp1.setText("<html><center>" + opciones.get(0) + "</center></html>");
        btnOp2.setText("<html><center>" + opciones.get(1) + "</center></html>");
        btnOp3.setText("<html><center>" + opciones.get(2) + "</center></html>");
    }

    /**
     * Valida el texto del boton presionado contra la respuesta guardada
     * y controla el aumento de la racha de aciertos.
     * * @param respuestaHTML La opcion seleccionada.
     */
    private void verificarRespuesta(String respuestaHTML) {
        String respuestaElegida = respuestaHTML.replace("<html><center>", "").replace("</center></html>", "");
        
        if (respuestaElegida.equals(respuestaCorrectaActual)) {
            rachaCorrectas++;
            barraProgreso.setValue(rachaCorrectas);
            
            if (rachaCorrectas >= 3) {
                System.out.println("Examen aprobado.");
                panelPadre.completarMisionPizarra();
                dispose();
            } else {
                cargarSiguientePregunta(); 
            }
        } else {
            System.out.println("Incorrecto. Volviendo a empezar...");
            getContentPane().setBackground(new Color(150, 50, 50));
            Timer t = new Timer(200, e -> getContentPane().setBackground(new Color(34, 76, 46)));
            t.setRepeats(false);
            t.start();
            reiniciarQuiz(); 
        }
    }

    /**
     * Clase de datos interna para guardar el formato estandar de una pregunta del quiz.
     */
    class Pregunta {
        String texto, correcta, falsa1, falsa2;
        public Pregunta(String t, String c, String f1, String f2) {
            this.texto = t; this.correcta = c; this.falsa1 = f1; this.falsa2 = f2;
        }
    }
}