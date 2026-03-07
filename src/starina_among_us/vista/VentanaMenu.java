package starina_among_us.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import starina_among_us.modelo.GestorConfiguracion;
import starina_among_us.modelo.GestorSonido;
import starina_among_us.modelo.GestorLenguaje;

public class VentanaMenu extends JFrame {

    // --- COMPONENTES DE INTERFAZ ---
    private JTextField txtNombre, txtIp;
    private JComboBox<String> comboColor, comboMapa;
    private JButton btnOnline, btnHowToPlay, btnFreeplay, btnSettings;

    // --- PANELES (CAPAS) ---
    private PanelFondo panelPrincipal;    // El que dibuja el espacio y logos
    private JPanel contenedorMenuInicio;  // Capa con los 3 botones grandes
    private JPanel contenedorOnline;      // Capa con los campos de texto
    private JPanel panelAjustes;          // Capa de configuración (Pop-up)

    // --- RECURSOS GRÁFICOS ---
    private BufferedImage imgTituloStarina, imgTituloAmongUs;
    private BufferedImage[] fondosMenu = new BufferedImage[10];
    private ImageIcon imgOnlineEN, imgOnlineES, imgHowEN, imgHowES, imgFreeEN, imgFreeES, imgConfig;

    // --- ESTADO ---
    private final int CENTRO_X = 400;
    private int indiceFondoActual = 0;
    private String idiomaActual = "es";
    
    private JButton btnCambiarIdioma;

    public VentanaMenu() {
        
        GestorConfiguracion.cargar(); // 1. Cargamos el XML
        
        // 2. Aplicamos los valores cargados al GestorSonido
        GestorSonido.setVolumenMusica(GestorConfiguracion.volumenMusica);
        GestorSonido.setVolumenEfectos(GestorConfiguracion.volumenSFX);
        // 1. Configuración de la Ventana
        setTitle("Starina Among Us - Menú Principal");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        // 2. Cargar Imágenes (Logos y Fondos)
        cargarRecursosBasicos();

        // 3. Inicializar el Fondo Principal (Capa 0)
        panelPrincipal = new PanelFondo();
        panelPrincipal.setLayout(null);
        setContentPane(panelPrincipal);

        // 4. Inicializar Capas de Interfaz (Paneles Transparentes)
        inicializarCapas();

        // 5. Cargar y Recortar Botones del Sprite Sheet
        cargarAssetsMenu();

        // 6. Construir Menú de Inicio (Capa 1)
        construirMenuInicio();

        // 7. Construir Menú Online (Capa 2)
        construirMenuOnline();

        // 8. Construir Panel de Ajustes (Capa 3)
        crearPanelAjustes();

        // 9. Iniciar Sonido y Timers
        GestorSonido.musicaLoop("intro.wav");
        iniciarTimers();

        // Idioma inicial
        cambiarIdioma("es");
        
        
        panelPrincipal.add(panelAjustes);         // Capa superior
        panelPrincipal.add(contenedorOnline);     // Capa media
        panelPrincipal.add(contenedorMenuInicio); // Capa base
    }

    private void cargarRecursosBasicos() {
        try {
            imgTituloStarina = javax.imageio.ImageIO.read(getClass().getResource("/starina_among_us/recursos/menu/starina.png"));
            imgTituloAmongUs = javax.imageio.ImageIO.read(getClass().getResource("/starina_among_us/recursos/menu/amongus_title.png"));
            for (int i = 0; i < 10; i++) {
                String nombre = String.format("/starina_among_us/recursos/menu/background_menu%02d.jpg", i + 1);
                fondosMenu[i] = javax.imageio.ImageIO.read(getClass().getResource(nombre));
            }
        } catch (Exception e) {
            System.out.println("❌ Error recursos: " + e.getMessage());
        }
    }

    private void inicializarCapas() {
        // Capa de botones principales
        contenedorMenuInicio = new JPanel(null);
        contenedorMenuInicio.setOpaque(false);
        contenedorMenuInicio.setBounds(0, 0, 800, 600);
        

        // Capa de conexión online (oculta al inicio)
        contenedorOnline = new JPanel(null);
        contenedorOnline.setOpaque(false);
        contenedorOnline.setBounds(0, 0, 800, 600);
        contenedorOnline.setVisible(false);
        
    }

    private void construirMenuInicio() {
        // ONLINE
        btnOnline = new JButton();
        btnOnline.setBounds(CENTRO_X - 150, 240, 300, 100);
        estilizarBotonImagen(btnOnline);
        btnOnline.addActionListener(e -> {
            contenedorMenuInicio.setVisible(false); // Oculta inicio
            panelAjustes.setVisible(false);         // Oculta ajustes
            contenedorOnline.setVisible(true);      // Muestra online
        });
        contenedorMenuInicio.add(btnOnline);

        // HOW TO PLAY
        btnHowToPlay = new JButton();
        btnHowToPlay.setBounds(CENTRO_X - 110, 350, 220, 60);
        estilizarBotonImagen(btnHowToPlay);
        contenedorMenuInicio.add(btnHowToPlay);

        // FREEPLAY
        btnFreeplay = new JButton();
        btnFreeplay.setBounds(CENTRO_X - 110, 420, 220, 60);
        estilizarBotonImagen(btnFreeplay);
        contenedorMenuInicio.add(btnFreeplay);

        // CONFIG (Ajustes)
        btnSettings = new JButton();
        btnSettings.setBounds(30, 490, 66, 66);
        estilizarBotonImagen(btnSettings);
        btnSettings.addActionListener(e -> {
            panelAjustes.setVisible(true);         // Muestra ajustes
            contenedorMenuInicio.setVisible(false); // OCULTA menú principal
            contenedorOnline.setVisible(false);     // Por si acaso, oculta online
        });
        contenedorMenuInicio.add(btnSettings);
    }

    private void construirMenuOnline() {
        int xCampos = CENTRO_X - 150; // Esto centra los campos (400 - 150 = 250)
        int anchoCampo = 300;
        int altoCampo = 35; // Un poco más altos para que el texto respire

        // 1. NOMBRE (Lo bajamos a Y=150 para que no esté pegado arriba)
        crearEtiqueta(contenedorOnline, "TU NOMBRE / YOUR NAME:", xCampos, 150);
        txtNombre = new JTextField("Tripulante");
        txtNombre.setBounds(xCampos, 180, anchoCampo, altoCampo);
        txtNombre.setFont(new Font("Arial", Font.BOLD, 16));
        contenedorOnline.add(txtNombre);

        // 2. COLOR (Con una separación de 50px respecto al anterior)
        crearEtiqueta(contenedorOnline, "COLOR DEL TRAJE:", xCampos, 230);
        String[] colores = {"Rojo", "Azul", "Verde", "Rosa", "Naranja", "Amarillo", "Negro", "Blanco"};
        comboColor = new JComboBox<>(colores);
        comboColor.setBounds(xCampos, 260, anchoCampo, altoCampo);
        comboColor.setFont(new Font("Arial", Font.BOLD, 16));
        contenedorOnline.add(comboColor);

        // 3. MAPA (¡Aquí está de vuelta!)
        crearEtiqueta(contenedorOnline, "MAPA (Solo afecta si eres HOST):", xCampos, 310);
        String[] mapas = {"Uni", "Salones"};
        comboMapa = new JComboBox<>(mapas);
        comboMapa.setBounds(xCampos, 340, anchoCampo, altoCampo);
        comboMapa.setFont(new Font("Arial", Font.BOLD, 16));
        contenedorOnline.add(comboMapa);

        // 4. IP DEL SERVIDOR
        crearEtiqueta(contenedorOnline, "IP DEL SERVIDOR / SERVER IP:", xCampos, 390);
        txtIp = new JTextField("localhost");
        txtIp.setBounds(xCampos, 420, anchoCampo, altoCampo);
        txtIp.setFont(new Font("Arial", Font.BOLD, 16));
        contenedorOnline.add(txtIp);

        // --- BOTONES HOST / JOIN ---
        JButton btnHost = new JButton("HOST (CREAR)");
        btnHost.setBounds(xCampos, 490, 140, 45);
        btnHost.setBackground(new Color(50, 150, 50));
        btnHost.setForeground(Color.WHITE);
        btnHost.setFont(new Font("Arial", Font.BOLD, 14));
        btnHost.setFocusPainted(false);
        btnHost.addActionListener(e -> iniciarJuego(true));
        contenedorOnline.add(btnHost);

        JButton btnJoin = new JButton("JOIN (UNIRSE)");
        btnJoin.setBounds(xCampos + 160, 490, 140, 45);
        btnJoin.setBackground(new Color(50, 100, 200));
        btnJoin.setForeground(Color.WHITE);
        btnJoin.setFont(new Font("Arial", Font.BOLD, 14));
        btnJoin.setFocusPainted(false);
        btnJoin.addActionListener(e -> iniciarJuego(false));
        contenedorOnline.add(btnJoin);

        // --- BOTÓN ATRÁS ---
        JButton btnBack = new JButton("◄ ATRÁS");
        btnBack.setBounds(20, 20, 110, 40);
        btnBack.setBackground(new Color(200, 50, 50));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFont(new Font("Arial", Font.BOLD, 14));
        btnBack.setFocusPainted(false);
        btnBack.addActionListener(e -> {
            contenedorOnline.setVisible(false);
            contenedorMenuInicio.setVisible(true);
        });
        contenedorOnline.add(btnBack);
    }

    private void crearPanelAjustes() {
        panelAjustes = new JPanel(null);
        panelAjustes.setBackground(new Color(0, 0, 0, 230)); // Un poco más oscuro
        panelAjustes.setBounds(200, 80, 400, 420);
        panelAjustes.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        panelAjustes.setVisible(false);
        panelPrincipal.add(panelAjustes);

        // --- SECCIÓN MÚSICA ---
        crearEtiqueta(panelAjustes, "MÚSICA / MUSIC", 50, 40);
        JSlider sliderMusica = new JSlider(0, 100, 50);
        sliderMusica.setBounds(50, 70, 300, 40);
        sliderMusica.setOpaque(false);
        sliderMusica.setValue((int)(GestorConfiguracion.volumenMusica * 100));
        sliderMusica.addChangeListener(e -> {
        float vol = sliderMusica.getValue() / 100f;
        GestorSonido.setVolumenMusica(vol);
        GestorConfiguracion.volumenMusica = vol; // Sincroniza con config
        GestorConfiguracion.guardar();           // Guarda en XML
    });
        panelAjustes.add(sliderMusica);

        // --- SECCIÓN EFECTOS ---
        crearEtiqueta(panelAjustes, "EFECTOS / SFX", 50, 130);
        JSlider sliderSFX = new JSlider(0, 100, 50);
        sliderSFX.setBounds(50, 160, 300, 40);
        sliderSFX.setOpaque(false);
        sliderSFX.setValue((int)(GestorConfiguracion.volumenSFX * 100));
        sliderSFX.addChangeListener(e -> {
        float vol = sliderSFX.getValue() / 100f;
        GestorSonido.setVolumenEfectos(vol);
        GestorConfiguracion.volumenSFX = vol;    // Sincroniza con config
        GestorConfiguracion.guardar();           // Guarda en XML
    });
        panelAjustes.add(sliderSFX);

        // --- BOTÓN IDIOMA DINÁMICO ---
        btnCambiarIdioma = new JButton();
        btnCambiarIdioma.setBounds(50, 250, 300, 45);
        btnCambiarIdioma.setFont(new Font("Arial", Font.BOLD, 16));
        btnCambiarIdioma.addActionListener(e -> {
        // Alternamos entre "es" y "en"
        String nuevo = idiomaActual.equals("es") ? "en" : "es";
        cambiarIdioma(nuevo);
        GestorConfiguracion.idioma = nuevo;      // Sincroniza con config
        GestorConfiguracion.guardar();           // Guarda en XML
    });
        panelAjustes.add(btnCambiarIdioma);
        
        JCheckBox chkAA = new JCheckBox("ANTIALIASING");
        chkAA.setBounds(50, 210, 200, 30);
        chkAA.setForeground(Color.WHITE);
        chkAA.setOpaque(false);
        chkAA.setSelected(GestorConfiguracion.antialiasing); // Carga del XML
    
        chkAA.addActionListener(e -> {
            GestorConfiguracion.antialiasing = chkAA.isSelected();
            GestorConfiguracion.guardar(); // Guarda la elección
            System.out.println("Antialiasing: " + GestorConfiguracion.antialiasing);
        });
        panelAjustes.add(chkAA);

        // BOTÓN CERRAR
        JButton btnCerrar = new JButton("CERRAR / CLOSE");
        btnCerrar.setBounds(125, 340, 150, 40);
        btnCerrar.setBackground(new Color(200, 50, 50));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.addActionListener(e -> {
            panelAjustes.setVisible(false);
            contenedorMenuInicio.setVisible(true);
        });
        panelAjustes.add(btnCerrar);
    }

    // --- MÉTODOS DE APOYO ---
    private void cargarAssetsMenu() {
        try {
            BufferedImage atlas = javax.imageio.ImageIO.read(getClass().getResource("/starina_among_us/recursos/menu/mainmenu.png"));
            imgOnlineEN = new ImageIcon(starina_among_us.modelo.HerramientasImagen.recortar(atlas, 843, 344, 191, 77));
            imgOnlineES = new ImageIcon(starina_among_us.modelo.HerramientasImagen.recortar(atlas, 1035, 226, 191, 81));
            imgHowEN = new ImageIcon(starina_among_us.modelo.HerramientasImagen.recortar(atlas, 1421, 247, 189, 49));
            imgHowES = new ImageIcon(starina_among_us.modelo.HerramientasImagen.recortar(atlas, 1227, 425, 191, 50));
            imgFreeEN = new ImageIcon(starina_among_us.modelo.HerramientasImagen.recortar(atlas, 1227, 476, 191, 48));
            imgFreeES = new ImageIcon(starina_among_us.modelo.HerramientasImagen.recortar(atlas, 1439, 297, 191, 51));
            imgConfig = new ImageIcon(starina_among_us.modelo.HerramientasImagen.recortar(atlas, 1569, 57, 66, 66));
        } catch (Exception e) { System.out.println("❌ Assets: " + e.getMessage()); }
    }

    public void cambiarIdioma(String nuevoIdioma) {
        this.idiomaActual = nuevoIdioma;
        GestorLenguaje.cargarIdioma(nuevoIdioma); // Cargar el XML correspondiente
        
        // Actualizar Iconos de los botones de imagen
        if (idiomaActual.equals("es")) {
            btnOnline.setIcon(imgOnlineES);
            btnHowToPlay.setIcon(imgHowES);
            btnFreeplay.setIcon(imgFreeES);
            btnCambiarIdioma.setText("IDIOMA: ESPAÑOL");
        } else {
            btnOnline.setIcon(imgOnlineEN);
            btnHowToPlay.setIcon(imgHowEN);
            btnFreeplay.setIcon(imgFreeEN);
            btnCambiarIdioma.setText("LANGUAGE: ENGLISH");
        }
        
        btnSettings.setIcon(imgConfig);
        
        // Re-dibujar para que los cambios se noten
        repaint();
    }

    private void estilizarBotonImagen(JButton btn) {
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void crearEtiqueta(JPanel p, String t, int x, int y) {
        JLabel l = new JLabel(t);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Arial", Font.BOLD, 14));
        l.setBounds(x, y, 300, 25);
        p.add(l);
    }

    private void iniciarTimers() {
        new Timer(333, e -> indiceFondoActual = (int) (Math.random() * 10)).start();
        new Timer(32, e -> panelPrincipal.repaint()).start();
    }

    private void iniciarJuego(boolean esHost) {
        Color colorFondo = Color.RED;
        switch ((String)comboColor.getSelectedItem()) {
            case "Azul": colorFondo = new Color(19, 46, 209); break;
            case "Verde": colorFondo = new Color(17, 127, 45); break;
            // ... resto de colores ...
        }
        if (esHost) new Thread(() -> starina_among_us.red.Servidor.main(null)).start();
        new VentanaJuego("Uni", txtIp.getText(), txtNombre.getText(), colorFondo, esHost).setVisible(true);
        GestorSonido.detenerMusica();
        this.dispose();
    }

    // --- CLASE DEL DIBUJO ---
    private class PanelFondo extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            
            if (fondosMenu[indiceFondoActual] != null) 
                g2.drawImage(fondosMenu[indiceFondoActual], 0, 0, 800, 600, null);

            double balanceo = Math.sin(System.currentTimeMillis() * 0.002) * 10;
            if (imgTituloStarina != null)
                g2.drawImage(imgTituloStarina, CENTRO_X - 150, 40 + (int)balanceo, 300, 90, null);
            if (imgTituloAmongUs != null)
                g2.drawImage(imgTituloAmongUs, CENTRO_X - 190, 125 + (int)balanceo, 380, 100, null);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaMenu().setVisible(true));
    }
}