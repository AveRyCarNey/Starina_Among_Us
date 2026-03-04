/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package starina_among_us.vista;

import javax.swing.*;
import java.awt.*;

public class VentanaMenu extends JFrame {

    private JTextField txtNombre;
    private JTextField txtIp;
    private JComboBox<String> comboColor;
    private JComboBox<String> comboMapa;

    public VentanaMenu() {
        setTitle("Starina Among Us - Menú Principal");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null); // Usamos null para posicionar todo libremente como en un juego

        // Fondo oscuro
        getContentPane().setBackground(new Color(30, 30, 40));

        // Título del juego
        JLabel lblTitulo = new JLabel("STARINA AMONG US", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 48));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBounds(0, 50, 800, 60);
        add(lblTitulo);

        // --- PANEL DE CONFIGURACIÓN ---
        int centroX = 400 - 150; // Centrar los elementos (ancho 300)

        // 1. Nombre
        crearEtiqueta("Tu Nombre:", centroX, 150);
        txtNombre = new JTextField("Tripulante");
        txtNombre.setBounds(centroX, 180, 300, 30);
        add(txtNombre);

        // 2. Color
        crearEtiqueta("Color del Traje:", centroX, 220);
        String[] colores = {"Rojo", "Azul", "Verde", "Rosa", "Naranja", "Amarillo", "Negro", "Blanco"};
        comboColor = new JComboBox<>(colores);
        comboColor.setBounds(centroX, 250, 300, 30);
        add(comboColor);

        // 3. Mapa
        crearEtiqueta("Seleccionar Mapa (Solo afecta si eres Host):", centroX, 290);
        String[] mapas = {"Uni", "Salones"};
        comboMapa = new JComboBox<>(mapas);
        comboMapa.setBounds(centroX, 320, 300, 30);
        add(comboMapa);

        // 4. IP del Servidor
        crearEtiqueta("IP del Servidor:", centroX, 360);
        txtIp = new JTextField("localhost");
        txtIp.setBounds(centroX, 390, 300, 30);
        add(txtIp);

        // --- BOTONES DE CONEXIÓN ---
        JButton btnHost = new JButton("CREAR SALA (Host)");
        btnHost.setBounds(centroX, 450, 140, 40);
        btnHost.setBackground(new Color(50, 150, 50));
        btnHost.setForeground(Color.WHITE);
        btnHost.setFocusPainted(false);
        btnHost.addActionListener(e -> iniciarJuego(true));
        add(btnHost);

        JButton btnJoin = new JButton("UNIRSE (Join)");
        btnJoin.setBounds(centroX + 160, 450, 140, 40);
        btnJoin.setBackground(new Color(50, 100, 200));
        btnJoin.setForeground(Color.WHITE);
        btnJoin.setFocusPainted(false);
        btnJoin.addActionListener(e -> iniciarJuego(false));
        add(btnJoin);
    }

    private void crearEtiqueta(String texto, int x, int y) {
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(Color.LIGHT_GRAY);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        lbl.setBounds(x, y, 300, 25);
        add(lbl);
    }

    private void iniciarJuego(boolean esHost) {
        String nombre = txtNombre.getText();
        String ip = txtIp.getText();
        String mapa = (String) comboMapa.getSelectedItem();
        String colorSeleccionado = (String) comboColor.getSelectedItem();

        // Convertir el nombre del color a RGB
        Color colorFondo = Color.RED; // Por defecto
        switch (colorSeleccionado) {
            case "Rojo": colorFondo = new Color(197, 17, 17); break;
            case "Azul": colorFondo = new Color(19, 46, 209); break;
            case "Verde": colorFondo = new Color(17, 127, 45); break;
            case "Rosa": colorFondo = new Color(237, 84, 186); break;
            case "Naranja": colorFondo = new Color(239, 125, 13); break;
            case "Amarillo": colorFondo = new Color(245, 245, 87); break;
            case "Negro": colorFondo = new Color(63, 71, 78); break;
            case "Blanco": colorFondo = new Color(214, 224, 240); break;
        }

        // ¡AQUÍ CONECTAREMOS CON TU JUEGO EN EL SIGUIENTE PASO!
        System.out.println("Iniciando... Host: " + esHost + " | IP: " + ip + " | Mapa: " + mapa + " | Color: " + colorSeleccionado);
        
        // Si el jugador le dio a CREAR SALA (Host), arrancamos el Servidor en secreto
        if (esHost) {
            new Thread(() -> {
                starina_among_us.red.Servidor.main(null); // Asumiendo que tu archivo se llama Servidor
            }).start();
            // Le damos medio segundo al servidor para que despierte antes de conectar el cliente
            try { Thread.sleep(500); } catch (Exception ex) {} 
        }

        // Abrimos la ventana del juego con todos los datos que elegimos
        VentanaJuego juego = new VentanaJuego(mapa, ip, nombre, colorFondo, esHost);
        juego.setVisible(true);
        
        this.dispose(); // Cerramos el menú
        
    }

    public static void main(String[] args) {
        // Esto hace que al darle "Run" a este archivo, se abra el menú
        SwingUtilities.invokeLater(() -> new VentanaMenu().setVisible(true));
    }
}