/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package starina_among_us.vista;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

// Librerías nativas de Java para leer XML
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class VistaMisionPizarra extends JDialog {

    private PanelJuego panelPadre;
    private ArrayList<Pregunta> bancoPreguntas = new ArrayList<>();
    private int rachaCorrectas = 0;
    private int indicePreguntaActual = 0; 
    private JLabel labelPregunta;
    private JButton btnOp1, btnOp2, btnOp3;
    private JProgressBar barraProgreso;
    private String respuestaCorrectaActual;

    public VistaMisionPizarra(PanelJuego padre) {
        super(SwingUtilities.getWindowAncestor(padre), "Quiz de Matemáticas", Dialog.ModalityType.APPLICATION_MODAL);
        this.panelPadre = padre;
        
        setSize(800, 500);
        setLocationRelativeTo(padre);
        setUndecorated(true); 
        
        JPanel panelFondo = new JPanel();
        panelFondo.setBackground(new Color(34, 76, 46)); // Verde pizarra
        panelFondo.setLayout(null);
        panelFondo.setBorder(BorderFactory.createLineBorder(new Color(101, 67, 33), 15)); // Marco de madera
        setContentPane(panelFondo);

        // --- 1. LEER EL ARCHIVO XML ---
        cargarPreguntasXML();

        // --- 2. CONFIGURAR INTERFAZ ---
        JButton btnCerrar = new JButton("X");
        btnCerrar.setBounds(730, 20, 50, 40);
        btnCerrar.setBackground(Color.RED);
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFont(new Font("Arial", Font.BOLD, 20));
        btnCerrar.setFocusPainted(false);
        btnCerrar.addActionListener(e -> dispose());
        panelFondo.add(btnCerrar);

        JLabel lblTitulo = new JLabel("EXAMEN DE PIZARRA (Responde 3 seguidas)", SwingConstants.CENTER);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Comic Sans MS", Font.BOLD, 24)); // Estilo tiza
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

        cargarSiguientePregunta();
    }

    private void cargarPreguntasXML() {
        try {
            InputStream is = getClass().getResourceAsStream("/starina_among_us/recursos/misiones/quiz_math.xml");
            if (is == null) {
                System.out.println("❌ Error: No se encontró quiz_math.xml");
                return;
            }
            
            // Motores nativos de Java para interpretar XML
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();

            // Buscar todas las etiquetas <pregunta>
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
            System.out.println("✅ Se cargaron " + bancoPreguntas.size() + " preguntas del XML.");
            
        } catch (Exception e) {
            System.out.println("❌ Excepción leyendo XML: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    private void reiniciarQuiz() {
        rachaCorrectas = 0;
        indicePreguntaActual = 0;
        barraProgreso.setValue(0);
        
        // REVOLVEMOS EL MAZO UNA SOLA VEZ AQUÍ
        Collections.shuffle(bancoPreguntas); 
        
        cargarSiguientePregunta();
    }

    private JButton crearBotonRespuesta(int x, int y) {
        JButton btn = new JButton();
        btn.setBounds(x, y, 200, 150);
        btn.setFont(new Font("Arial", Font.BOLD, 20));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        // Ajustamos texto largo para que baje de línea si es necesario
        btn.setMargin(new Insets(10, 10, 10, 10)); 
        
        btn.addActionListener(e -> verificarRespuesta(btn.getText()));
        return btn;
    }

    private void cargarSiguientePregunta() {
        if (bancoPreguntas.isEmpty()) return;

        // Por si acaso llegamos al final de la lista, volvemos a revolver
        if (indicePreguntaActual >= bancoPreguntas.size()) {
            Collections.shuffle(bancoPreguntas);
            indicePreguntaActual = 0;
        }

        // Sacamos la pregunta en orden y avanzamos el índice para la próxima
        Pregunta p = bancoPreguntas.get(indicePreguntaActual);
        indicePreguntaActual++; 
        
        respuestaCorrectaActual = p.correcta;
        labelPregunta.setText(p.texto);

        // Revolvemos solo los botones de esta pregunta
        ArrayList<String> opciones = new ArrayList<>();
        opciones.add(p.correcta);
        opciones.add(p.falsa1);
        opciones.add(p.falsa2);
        Collections.shuffle(opciones);

        btnOp1.setText("<html><center>" + opciones.get(0) + "</center></html>");
        btnOp2.setText("<html><center>" + opciones.get(1) + "</center></html>");
        btnOp3.setText("<html><center>" + opciones.get(2) + "</center></html>");
    }

    private void verificarRespuesta(String respuestaHTML) {
        String respuestaElegida = respuestaHTML.replace("<html><center>", "").replace("</center></html>", "");
        
        if (respuestaElegida.equals(respuestaCorrectaActual)) {
            rachaCorrectas++;
            barraProgreso.setValue(rachaCorrectas);
            
            if (rachaCorrectas >= 3) {
                System.out.println("✅ ¡Examen aprobado!");
                panelPadre.completarMisionPizarra();
                dispose();
            } else {
                cargarSiguientePregunta(); // Sacamos el siguiente papelito del mazo
            }
        } else {
            System.out.println("❌ ¡Incorrecto! Volviendo a empezar...");
            
            // Efecto visual rojo de error
            getContentPane().setBackground(new Color(150, 50, 50));
            Timer t = new Timer(200, e -> getContentPane().setBackground(new Color(34, 76, 46)));
            t.setRepeats(false);
            t.start();
            
            reiniciarQuiz(); // <-- REINICIAMOS EL MAZO AL EQUIVOCARNOS
        }
    }

    class Pregunta {
        String texto, correcta, falsa1, falsa2;
        public Pregunta(String t, String c, String f1, String f2) {
            this.texto = t; this.correcta = c; this.falsa1 = f1; this.falsa2 = f2;
        }
    }
}