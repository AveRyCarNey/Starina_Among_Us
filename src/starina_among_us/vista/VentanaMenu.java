package starina_among_us.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import starina_among_us.modelo.GestorConfiguracion;
import starina_among_us.modelo.GestorSonido;
import starina_among_us.modelo.GestorLenguaje;

/**
 * Ventana grafica principal que actua como el menu de inicio del juego.
 * Implementa un diseno "Responsive" y maneja multiples capas de paneles (JPanels)
 * para simular la navegacion entre menus (Online, Local, Ajustes, Creditos).
 * * @author Wulliber Yepez, Carlos Ramirez, Jorg Sierra, Samuel Salazar
 * @version 1.0
 */
public class VentanaMenu extends JFrame {

    // --- COMPONENTES DE INTERFAZ ---
    private JTextField txtNombre, txtIp;
    private JComboBox<String> comboColor, comboMapa;
    private JButton btnOnline, btnHowToPlay, btnFreeplay, btnSettings;

    // --- PANELES (CAPAS) ---
    private PanelFondo panelPrincipal;    
    private JPanel contenedorMenuInicio;  
    private JPanel contenedorOnline;      
    private JPanel panelAjustes;          
    private JPanel contenedorFreeplay;
    
    // --- PANELES Y COMPONENTES ADICIONALES ---
    private JPanel panelHowToPlay;    
    private JPanel panelAbout;        
    private JButton btnAbout;         
    private JTextArea txtHowToBody, txtAboutBody; 
    private JLabel lblHowToTitle, lblAboutTitle;
    private JButton btnCerrarHowTo, btnCerrarAbout;

    // --- RECURSOS GRAFICOS ---
    private BufferedImage imgTituloStarina, imgTituloAmongUs;
    private BufferedImage[] fondosMenu = new BufferedImage[10];
    private ImageIcon imgOnlineEN, imgOnlineES, imgHowEN, imgHowES, imgFreeEN, imgFreeES, imgConfig;

    // --- ESTADO ---
    private int indiceFondoActual = 0;
    private String idiomaActual = "es";
    private boolean freeplayEsImpostor = false;
    private boolean mostrarLogosFlotantes = true; 
    
    private JLabel lblNombreT, lblColorT, lblMapaT, lblIpT, lblRolT, lblSeleccionaMapaT;
    private JLabel lblMusicaT, lblSfxT; 
    private JButton btnHost, btnJoin, btnBackOnline, btnBackFree, btnRol, btnCerrarAjustes;
    private JButton btnMapaUni, btnMapaSalones; 
    private JCheckBox chkAA; 
    
    private JButton btnCambiarIdioma;
    
    private JLabel lblImpostoresT;
    private JComboBox<String> comboImpostores;

    /**
     * Constructor principal del Menu.
     * Carga las configuraciones guardadas, reproduce la musica de introduccion,
     * construye las capas de la interfaz y activa el "Responsive Design".
     */
    public VentanaMenu() {
        
        GestorConfiguracion.cargar(); 
        GestorSonido.setVolumenMusica(GestorConfiguracion.volumenMusica);
        GestorSonido.setVolumenEfectos(GestorConfiguracion.volumenSFX);
        
        setTitle("Starina Among Us - Menu Principal");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true); 

        cargarRecursosBasicos();

        panelPrincipal = new PanelFondo();
        panelPrincipal.setLayout(null);
        setContentPane(panelPrincipal);

        inicializarCapas();
        cargarAssetsMenu();

        construirMenuInicio();
        construirMenuOnline();
        construirMenuFreeplay();
        crearPanelAjustes();
        crearPanelHowToPlay();
        crearPanelAbout();

        configurarResponsive();

        GestorSonido.musicaLoop("intro.wav");
        iniciarTimers();

        cambiarIdioma(starina_among_us.modelo.GestorConfiguracion.idioma);
        
        panelPrincipal.add(panelAjustes);         
        panelPrincipal.add(contenedorOnline);     
        panelPrincipal.add(contenedorMenuInicio); 
        panelPrincipal.add(panelHowToPlay);
        panelPrincipal.add(panelAbout);
    }

    /**
     * Anade un oyente de eventos (ComponentListener) para reubicar dinamicamente 
     * todos los botones y contenedores cuando el jugador cambia el tamano de la ventana.
     */
    private void configurarResponsive() {
        panelPrincipal.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int w = panelPrincipal.getWidth();
                int h = panelPrincipal.getHeight();
                
                int cx = w / 2; 
                int cy = h / 2; 

                if (contenedorMenuInicio != null) contenedorMenuInicio.setBounds(0, 0, w, h);
                if (contenedorOnline != null) contenedorOnline.setBounds(0, 0, w, h);
                if (contenedorFreeplay != null) contenedorFreeplay.setBounds(0, 0, w, h);

                if (panelAjustes != null) panelAjustes.setBounds(cx - 200, cy - 210, 400, 420);

                if (btnOnline != null) btnOnline.setBounds(cx - 150, cy - 60, 300, 100);
                if (btnHowToPlay != null) btnHowToPlay.setBounds(cx - 110, cy + 60, 220, 60);
                if (btnFreeplay != null) btnFreeplay.setBounds(cx - 110, cy + 130, 220, 60);
                if (btnSettings != null) btnSettings.setBounds(30, h - 90, 66, 66); 
                
                if (panelHowToPlay != null) panelHowToPlay.setBounds(cx - 250, cy - 200, 500, 350);
                if (panelAbout != null) panelAbout.setBounds(cx - 200, cy - 150, 400, 300);

                if (lblHowToTitle != null) lblHowToTitle.setBounds(30, 20, 400, 40);
                if (txtHowToBody != null) txtHowToBody.setBounds(30, 70, 440, 200);
                if (btnCerrarHowTo != null) btnCerrarHowTo.setBounds(175, 280, 150, 40);

                if (lblAboutTitle != null) lblAboutTitle.setBounds(30, 20, 340, 40);
                if (txtAboutBody != null) txtAboutBody.setBounds(30, 70, 340, 150);
                if (btnCerrarAbout != null) btnCerrarAbout.setBounds(125, 230, 150, 40);

                if (btnAbout != null) btnAbout.setBounds(w - 150, h - 60, 120, 30);

                int startY = cy - 180;
                int gap = 65;
                int xCampos = cx - 150;
                int ancho = 300;
                int alto = 35;

                if (lblNombreT != null) lblNombreT.setBounds(xCampos, startY, 300, 25);
                if (txtNombre != null) txtNombre.setBounds(xCampos, startY + 25, ancho, alto);

                if (lblColorT != null) lblColorT.setBounds(xCampos, startY + gap, 300, 25);
                if (comboColor != null) comboColor.setBounds(xCampos, startY + gap + 25, ancho, alto);

                if (lblMapaT != null) lblMapaT.setBounds(xCampos, startY + (gap * 2), 300, 25);
                if (comboMapa != null) comboMapa.setBounds(xCampos, startY + (gap * 2) + 25, ancho, alto);

                if (lblImpostoresT != null) lblImpostoresT.setBounds(xCampos, startY + (gap * 3), 300, 25);
                if (comboImpostores != null) comboImpostores.setBounds(xCampos, startY + (gap * 3) + 25, ancho, alto);

                if (lblIpT != null) lblIpT.setBounds(xCampos, startY + (gap * 4), 300, 25);
                if (txtIp != null) txtIp.setBounds(xCampos, startY + (gap * 4) + 25, ancho, alto);

                if (btnHost != null) btnHost.setBounds(xCampos, startY + (gap * 5) + 10, 140, 45);
                if (btnJoin != null) btnJoin.setBounds(xCampos + 160, startY + (gap * 5) + 10, 140, 45);

                if (btnBackOnline != null) btnBackOnline.setBounds(20, 20, 110, 40);
                if (btnBackFree != null) btnBackFree.setBounds(20, 20, 110, 40);

                int freeY = cy - 120;
                if (lblRolT != null) lblRolT.setBounds(xCampos, freeY, 300, 25);
                if (btnRol != null) btnRol.setBounds(xCampos, freeY + 30, 300, 45);

                if (lblSeleccionaMapaT != null) lblSeleccionaMapaT.setBounds(xCampos, freeY + 100, 300, 25);
                if (btnMapaUni != null) btnMapaUni.setBounds(xCampos, freeY + 130, 300, 60);
                if (btnMapaSalones != null) btnMapaSalones.setBounds(xCampos, freeY + 210, 300, 60);
            }
        });
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
            System.out.println("Error en recursos base: " + e.getMessage());
        }
    }

    private void inicializarCapas() {
        contenedorMenuInicio = new JPanel(null);
        contenedorMenuInicio.setOpaque(false);

        contenedorOnline = new JPanel(null);
        contenedorOnline.setOpaque(false);
        contenedorOnline.setVisible(false);
        
        contenedorFreeplay = new JPanel(null);
        contenedorFreeplay.setOpaque(false);
        contenedorFreeplay.setVisible(false);
        panelPrincipal.add(contenedorFreeplay); 
    }

    private void construirMenuInicio() {
        btnOnline = new JButton();
        estilizarBotonImagen(btnOnline);
        btnOnline.addActionListener(e -> {
            contenedorMenuInicio.setVisible(false); 
            panelAjustes.setVisible(false);         
            contenedorOnline.setVisible(true);      
            mostrarLogosFlotantes = false;
        });
        contenedorMenuInicio.add(btnOnline);

        btnHowToPlay = new JButton();
        estilizarBotonImagen(btnHowToPlay);
        btnHowToPlay.addActionListener(e -> {
            panelHowToPlay.setVisible(true);
            contenedorMenuInicio.setVisible(false);
            mostrarLogosFlotantes = false;
        });
        contenedorMenuInicio.add(btnHowToPlay);

        btnFreeplay = new JButton();
        estilizarBotonImagen(btnFreeplay);
        btnFreeplay.addActionListener(e -> {
            contenedorMenuInicio.setVisible(false);
            panelAjustes.setVisible(false);
            contenedorFreeplay.setVisible(true); 
            mostrarLogosFlotantes = false;
        });
        contenedorMenuInicio.add(btnFreeplay);

        btnSettings = new JButton();
        estilizarBotonImagen(btnSettings);
        btnSettings.addActionListener(e -> {
            panelAjustes.setVisible(true);         
            contenedorMenuInicio.setVisible(false); 
            contenedorOnline.setVisible(false);     
            mostrarLogosFlotantes = false;
        });
        contenedorMenuInicio.add(btnSettings);

        btnAbout = new JButton(GestorLenguaje.get("btn_acerca"));
        btnAbout.setFont(new Font("Arial", Font.BOLD, 12));
        btnAbout.setBackground(new Color(0,0,0,150));
        btnAbout.setForeground(Color.WHITE);
        btnAbout.setFocusPainted(false);
        btnAbout.addActionListener(e -> {
            panelAbout.setVisible(true);
            contenedorMenuInicio.setVisible(false);
            mostrarLogosFlotantes = false;
        });
        contenedorMenuInicio.add(btnAbout);
    }

    private void construirMenuOnline() {
        lblNombreT = crearEtiqueta(contenedorOnline, GestorLenguaje.get("lbl_nombre"));
        txtNombre = new JTextField("Tripulante");
        txtNombre.setFont(new Font("Arial", Font.BOLD, 16));
        contenedorOnline.add(txtNombre);

        lblColorT = crearEtiqueta(contenedorOnline, GestorLenguaje.get("lbl_color"));
        comboColor = new JComboBox<>();
        comboColor.setFont(new Font("Arial", Font.BOLD, 16));
        contenedorOnline.add(comboColor);

        lblMapaT = crearEtiqueta(contenedorOnline, GestorLenguaje.get("lbl_mapa"));
        String[] mapas = {"Uni", "Salones"};
        comboMapa = new JComboBox<>(mapas);
        comboMapa.setFont(new Font("Arial", Font.BOLD, 16));
        contenedorOnline.add(comboMapa);

        lblImpostoresT = crearEtiqueta(contenedorOnline, GestorLenguaje.get("lbl_impostores"));
        String[] numImpostores = {"1", "2"};
        comboImpostores = new JComboBox<>(numImpostores);
        comboImpostores.setFont(new Font("Arial", Font.BOLD, 16));
        contenedorOnline.add(comboImpostores);

        lblIpT = crearEtiqueta(contenedorOnline, GestorLenguaje.get("lbl_ip"));
        txtIp = new JTextField("localhost");
        txtIp.setFont(new Font("Arial", Font.BOLD, 16));
        contenedorOnline.add(txtIp);

        btnHost = new JButton(GestorLenguaje.get("btn_host"));
        btnHost.setBackground(Color.BLACK); 
        btnHost.setForeground(Color.WHITE);
        btnHost.setFont(new Font("Arial", Font.BOLD, 14));
        btnHost.setFocusPainted(false);
        btnHost.addActionListener(e -> iniciarJuego(true));
        contenedorOnline.add(btnHost);

        btnJoin = new JButton(GestorLenguaje.get("btn_join"));
        btnJoin.setBackground(Color.BLACK); 
        btnJoin.setForeground(Color.WHITE);
        btnJoin.setFont(new Font("Arial", Font.BOLD, 14));
        btnJoin.setFocusPainted(false);
        btnJoin.addActionListener(e -> iniciarJuego(false));
        contenedorOnline.add(btnJoin);

        btnBackOnline = new JButton(GestorLenguaje.get("btn_atras"));
        btnBackOnline.setBackground(Color.BLACK); 
        btnBackOnline.setForeground(Color.WHITE);
        btnBackOnline.setFont(new Font("Arial", Font.BOLD, 14));
        btnBackOnline.setFocusPainted(false);
        btnBackOnline.addActionListener(e -> {
            contenedorOnline.setVisible(false);
            contenedorMenuInicio.setVisible(true);
            mostrarLogosFlotantes = true;
        });
        contenedorOnline.add(btnBackOnline);
    }

    private void construirMenuFreeplay() {
        lblRolT = crearEtiqueta(contenedorFreeplay, GestorLenguaje.get("lbl_rol"));
        
        btnRol = new JButton(GestorLenguaje.get("btn_rol_tripulante"));
        btnRol.setBackground(Color.BLACK); 
        btnRol.setForeground(Color.WHITE);
        btnRol.setFont(new Font("Arial", Font.BOLD, 18));
        btnRol.setFocusPainted(false);
        btnRol.addActionListener(e -> {
            freeplayEsImpostor = !freeplayEsImpostor; 
            if (freeplayEsImpostor) {
                btnRol.setText(GestorLenguaje.get("btn_rol_impostor"));
            } else {
                btnRol.setText(GestorLenguaje.get("btn_rol_tripulante"));
            }
        });
        contenedorFreeplay.add(btnRol);

        lblSeleccionaMapaT = crearEtiqueta(contenedorFreeplay, GestorLenguaje.get("lbl_sel_mapa"));

        btnMapaUni = new JButton(GestorLenguaje.get("btn_mapa_uni"));
        btnMapaUni.setBackground(Color.BLACK);
        btnMapaUni.setForeground(Color.WHITE);
        btnMapaUni.setFont(new Font("Arial", Font.BOLD, 18));
        btnMapaUni.setFocusPainted(false);
        btnMapaUni.addActionListener(e -> iniciarModoLibre("Uni"));
        contenedorFreeplay.add(btnMapaUni);

        btnMapaSalones = new JButton(GestorLenguaje.get("btn_mapa_salones"));
        btnMapaSalones.setBackground(Color.BLACK);
        btnMapaSalones.setForeground(Color.WHITE);
        btnMapaSalones.setFont(new Font("Arial", Font.BOLD, 18));
        btnMapaSalones.setFocusPainted(false);
        btnMapaSalones.addActionListener(e -> iniciarModoLibre("Salones"));
        contenedorFreeplay.add(btnMapaSalones);

        btnBackFree = new JButton(GestorLenguaje.get("btn_atras"));
        btnBackFree.setBackground(Color.BLACK);
        btnBackFree.setForeground(Color.WHITE);
        btnBackFree.setFont(new Font("Arial", Font.BOLD, 14));
        btnBackFree.setFocusPainted(false);
        btnBackFree.addActionListener(e -> {
            contenedorFreeplay.setVisible(false);
            contenedorMenuInicio.setVisible(true);
            mostrarLogosFlotantes = true;
        });
        contenedorFreeplay.add(btnBackFree);
    }
    
    private void crearPanelAjustes() {
        panelAjustes = new JPanel(null);
        panelAjustes.setBackground(new Color(0, 0, 0, 230)); 
        panelAjustes.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        panelAjustes.setVisible(false);
        panelPrincipal.add(panelAjustes);

        lblMusicaT = crearEtiqueta(panelAjustes, GestorLenguaje.get("lbl_musica"));
        lblMusicaT.setBounds(50, 40, 300, 25);
        JSlider sliderMusica = new JSlider(0, 100, (int)(GestorConfiguracion.volumenMusica * 100));
        sliderMusica.setBounds(50, 70, 300, 40);
        sliderMusica.setOpaque(false);
        sliderMusica.addChangeListener(e -> {
            float vol = sliderMusica.getValue() / 100f;
            starina_among_us.modelo.GestorSonido.setVolumenMusica(vol);
            GestorConfiguracion.volumenMusica = vol; 
            GestorConfiguracion.guardar();            
        });
        panelAjustes.add(sliderMusica);

        lblSfxT = crearEtiqueta(panelAjustes, GestorLenguaje.get("lbl_efectos"));
        lblSfxT.setBounds(50, 130, 300, 25);
        JSlider sliderSFX = new JSlider(0, 100, (int)(GestorConfiguracion.volumenSFX * 100));
        sliderSFX.setBounds(50, 160, 300, 40);
        sliderSFX.setOpaque(false);
        sliderSFX.addChangeListener(e -> {
            float vol = sliderSFX.getValue() / 100f;
            starina_among_us.modelo.GestorSonido.setVolumenEfectos(vol);
            GestorConfiguracion.volumenSFX = vol;    
            GestorConfiguracion.guardar();            
        });
        panelAjustes.add(sliderSFX);

        chkAA = new JCheckBox(GestorLenguaje.get("chk_aa"));
        chkAA.setBounds(50, 210, 200, 30);
        chkAA.setForeground(Color.WHITE);
        chkAA.setOpaque(false);
        chkAA.setSelected(GestorConfiguracion.antialiasing);
        chkAA.addActionListener(e -> {
            GestorConfiguracion.antialiasing = chkAA.isSelected();
            GestorConfiguracion.guardar(); 
        });
        panelAjustes.add(chkAA);

        btnCambiarIdioma = new JButton();
        btnCambiarIdioma.setBounds(50, 260, 300, 45);
        btnCambiarIdioma.setFont(new Font("Arial", Font.BOLD, 16));
        btnCambiarIdioma.addActionListener(e -> {
            String nuevo = idiomaActual.equals("es") ? "en" : "es";
            cambiarIdioma(nuevo);
            GestorConfiguracion.idioma = nuevo;      
            GestorConfiguracion.guardar();            
        });
        panelAjustes.add(btnCambiarIdioma);

        btnCerrarAjustes = new JButton(GestorLenguaje.get("btn_cerrar"));
        btnCerrarAjustes.setBounds(125, 340, 150, 40);
        btnCerrarAjustes.setBackground(Color.BLACK);
        btnCerrarAjustes.setForeground(Color.WHITE);
        btnCerrarAjustes.addActionListener(e -> {
            panelAjustes.setVisible(false);
            contenedorMenuInicio.setVisible(true);
            mostrarLogosFlotantes = true;
        });
        panelAjustes.add(btnCerrarAjustes);
    }

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
        } catch (Exception e) { System.out.println("Error en assets de menu: " + e.getMessage()); }
    }

    /**
     * Refresca todos los textos e imagenes de la interfaz utilizando el GestorLenguaje,
     * aplicando la traduccion en tiempo real sin necesidad de reiniciar el juego.
     * * @param nuevoIdioma Codigo ISO del idioma (ej. "es" o "en").
     */
    public void cambiarIdioma(String nuevoIdioma) {
        this.idiomaActual = nuevoIdioma;
        starina_among_us.modelo.GestorLenguaje.cargarIdioma(nuevoIdioma);
        
        if (idiomaActual.equals("es")) {
            btnOnline.setIcon(imgOnlineES); btnHowToPlay.setIcon(imgHowES); btnFreeplay.setIcon(imgFreeES);
            if(btnCambiarIdioma != null) btnCambiarIdioma.setText("IDIOMA: ESPAÑOL");
        } else {
            btnOnline.setIcon(imgOnlineEN); btnHowToPlay.setIcon(imgHowEN); btnFreeplay.setIcon(imgFreeEN);
            if(btnCambiarIdioma != null) btnCambiarIdioma.setText("LANGUAGE: ENGLISH");
        }
        btnSettings.setIcon(imgConfig);

        if(lblNombreT != null) lblNombreT.setText(GestorLenguaje.get("lbl_nombre"));
        if(lblColorT != null) lblColorT.setText(GestorLenguaje.get("lbl_color"));
        if(lblMapaT != null) lblMapaT.setText(GestorLenguaje.get("lbl_mapa"));
        if(lblIpT != null) lblIpT.setText(GestorLenguaje.get("lbl_ip"));
        if(lblRolT != null) lblRolT.setText(GestorLenguaje.get("lbl_rol"));
        if(lblSeleccionaMapaT != null) lblSeleccionaMapaT.setText(GestorLenguaje.get("lbl_sel_mapa"));
        
        if(lblMusicaT != null) lblMusicaT.setText(GestorLenguaje.get("lbl_musica"));
        if(lblSfxT != null) lblSfxT.setText(GestorLenguaje.get("lbl_efectos"));
        if(chkAA != null) chkAA.setText(GestorLenguaje.get("chk_aa"));

        if(btnHost != null) btnHost.setText(GestorLenguaje.get("btn_host"));
        if(btnJoin != null) btnJoin.setText(GestorLenguaje.get("btn_join"));
        if(btnBackOnline != null) btnBackOnline.setText(GestorLenguaje.get("btn_atras"));
        if(btnBackFree != null) btnBackFree.setText(GestorLenguaje.get("btn_atras"));
        if(btnCerrarAjustes != null) btnCerrarAjustes.setText(GestorLenguaje.get("btn_cerrar"));
        if(btnMapaUni != null) btnMapaUni.setText(GestorLenguaje.get("btn_mapa_uni"));
        if(btnMapaSalones != null) btnMapaSalones.setText(GestorLenguaje.get("btn_mapa_salones"));
        if(lblImpostoresT != null) lblImpostoresT.setText(GestorLenguaje.get("lbl_impostores"));
        
        if(lblHowToTitle != null) lblHowToTitle.setText(GestorLenguaje.get("lbl_how_to_title"));
        if(txtHowToBody != null) txtHowToBody.setText(GestorLenguaje.get("msg_how_to_body"));
        if(lblAboutTitle != null) lblAboutTitle.setText(GestorLenguaje.get("lbl_about_title"));
        if(txtAboutBody != null) txtAboutBody.setText(GestorLenguaje.get("msg_about_body"));
        if(btnAbout != null) btnAbout.setText(GestorLenguaje.get("lbl_about_title"));
        if(btnCerrarHowTo != null) btnCerrarHowTo.setText(GestorLenguaje.get("btn_cerrar"));
        if(btnCerrarAbout != null) btnCerrarAbout.setText(GestorLenguaje.get("btn_cerrar"));

        if(btnRol != null) {
            btnRol.setText(freeplayEsImpostor ? GestorLenguaje.get("btn_rol_impostor") : GestorLenguaje.get("btn_rol_tripulante"));
        }

        if (comboColor != null) {
            int indexGuardado = comboColor.getSelectedIndex();
            comboColor.removeAllItems();
            if (nuevoIdioma.equals("en")) {
                String[] colorsEN = {"Red", "Blue", "Green", "Pink", "Orange", "Yellow", "Black", "White"};
                for(String c : colorsEN) comboColor.addItem(c);
            } else {
                String[] colorsES = {"Rojo", "Azul", "Verde", "Rosa", "Naranja", "Amarillo", "Negro", "Blanco"};
                for(String c : colorsES) comboColor.addItem(c);
            }
            comboColor.setSelectedIndex(Math.max(0, indexGuardado));
        }
        
        repaint();
    }

    private void estilizarBotonImagen(JButton btn) {
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JLabel crearEtiqueta(JPanel p, String t) {
        JLabel l = new JLabel(t);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Arial", Font.BOLD, 14));
        p.add(l);
        return l; 
    }

    private void iniciarTimers() {
        new Timer(333, e -> indiceFondoActual = (int) (Math.random() * 10)).start();
        new Timer(32, e -> panelPrincipal.repaint()).start();
    }

    /**
     * Recolecta los parametros configurados por el usuario y arranca la VentanaJuego en red.
     * Atrapa las excepciones personalizadas y muestra alertas visuales en caso de error.
     * * @param esHost Define si el jugador sera el servidor central.
     */
    private void iniciarJuego(boolean esHost) {
        try { 
            String nombre = txtNombre.getText();
            String ip = txtIp.getText();
            String mapa = (String) comboMapa.getSelectedItem();
            String colorSeleccionado = (String) comboColor.getSelectedItem(); 
            
            if (colorSeleccionado == null) colorSeleccionado = "Rojo";

            Color colorFondo = Color.RED; 
            switch (colorSeleccionado) {
                case "Rojo": case "Red": colorFondo = new Color(197, 17, 17); break;
                case "Azul": case "Blue": colorFondo = new Color(19, 46, 209); break;
                case "Verde": case "Green": colorFondo = new Color(17, 127, 45); break;
                case "Rosa": case "Pink": colorFondo = new Color(237, 84, 186); break;
                case "Naranja": case "Orange": colorFondo = new Color(239, 125, 13); break;
                case "Amarillo": case "Yellow": colorFondo = new Color(245, 245, 87); break;
                case "Negro": case "Black": colorFondo = new Color(63, 71, 78); break;
                case "Blanco": case "White": colorFondo = new Color(214, 224, 240); break;
            }
            
            starina_among_us.modelo.GestorSonido.detenerMusica();
            
            String impSeleccionados = (String) comboImpostores.getSelectedItem();
            if (impSeleccionados != null) {
                starina_among_us.vista.PanelJuego.impostoresConfigurados = Integer.parseInt(impSeleccionados);
            }
            
            VentanaJuego juego = new VentanaJuego(mapa, ip, nombre, colorFondo, esHost);
            
            juego.setExtendedState(this.getExtendedState()); 
            if (this.getExtendedState() != JFrame.MAXIMIZED_BOTH) {
                juego.setBounds(this.getBounds());
            }
            
            juego.setVisible(true);
            this.dispose();

        } catch (starina_among_us.modelo.excepciones.AmongUsException ex) { 
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Inicializacion", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error critico inesperado: " + ex.getMessage(), "Error Fatal", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    /**
     * Inicia una partida local (Sin red) inyectando palabras clave en el campo de IP
     * para enganar al sistema y simular una conexion de un solo jugador.
     * * @param mapa El nombre del mapa a cargar.
     */
    private void iniciarModoLibre(String mapa) {
        try {
            System.out.println("Iniciando MODO LIBRE en: " + mapa);
            starina_among_us.modelo.GestorSonido.detenerMusica();
            
            String ipFalsa = freeplayEsImpostor ? "OFFLINE_IMPOSTOR" : "OFFLINE_TRIPULANTE";
            
            VentanaJuego juego = new VentanaJuego(mapa, ipFalsa, "Jugador Libre", Color.WHITE, false);
            
            juego.setExtendedState(this.getExtendedState()); 
            if (this.getExtendedState() != JFrame.MAXIMIZED_BOTH) {
                juego.setBounds(this.getBounds());
            }
            
            juego.setVisible(true);
            this.dispose(); 

        } catch (starina_among_us.modelo.excepciones.AmongUsException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error en Modo Libre", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Panel de fondo personalizado que dibuja las estrellas parpadeantes
     * y genera la oscilacion suave (Efecto Hover) de los logos del juego.
     */
    private class PanelFondo extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            
            if (fondosMenu[indiceFondoActual] != null) 
                g2.drawImage(fondosMenu[indiceFondoActual], 0, 0, getWidth(), getHeight(), null);

            if (mostrarLogosFlotantes) {
                int centroActualX = getWidth() / 2; 
                double balanceo = Math.sin(System.currentTimeMillis() * 0.002) * 10;
                
                if (imgTituloStarina != null)
                    g2.drawImage(imgTituloStarina, centroActualX - 150, 40 + (int)balanceo, 300, 90, null);
                if (imgTituloAmongUs != null)
                    g2.drawImage(imgTituloAmongUs, centroActualX - 190, 125 + (int)balanceo, 380, 100, null);
            }
        }
    }
    
    private void crearPanelHowToPlay() {
        panelHowToPlay = new JPanel(null);
        panelHowToPlay.setBackground(new Color(0, 0, 0, 240));
        panelHowToPlay.setBorder(BorderFactory.createLineBorder(Color.CYAN, 2));
        panelHowToPlay.setVisible(false);

        lblHowToTitle = crearEtiqueta(panelHowToPlay, GestorLenguaje.get("lbl_how_to_title"));
        lblHowToTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblHowToTitle.setForeground(Color.CYAN);

        txtHowToBody = new JTextArea(GestorLenguaje.get("msg_how_to_body"));
        txtHowToBody.setFont(new Font("Arial", Font.PLAIN, 16));
        txtHowToBody.setForeground(Color.WHITE);
        txtHowToBody.setOpaque(false);
        txtHowToBody.setEditable(false);
        txtHowToBody.setLineWrap(true);
        txtHowToBody.setWrapStyleWord(true);
        panelHowToPlay.add(txtHowToBody);

        btnCerrarHowTo = new JButton(GestorLenguaje.get("btn_cerrar"));
        btnCerrarHowTo.setBackground(Color.BLACK);
        btnCerrarHowTo.setForeground(Color.WHITE);
        btnCerrarHowTo.addActionListener(e -> {
            panelHowToPlay.setVisible(false);
            contenedorMenuInicio.setVisible(true);
            mostrarLogosFlotantes = true;
        });
        panelHowToPlay.add(btnCerrarHowTo);
    }

    private void crearPanelAbout() {
        panelAbout = new JPanel(null);
        panelAbout.setBackground(new Color(0, 0, 0, 240));
        panelAbout.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        panelAbout.setVisible(false);

        lblAboutTitle = crearEtiqueta(panelAbout, GestorLenguaje.get("lbl_about_title"));
        lblAboutTitle.setFont(new Font("Arial", Font.BOLD, 24));

        txtAboutBody = new JTextArea(GestorLenguaje.get("msg_about_body"));
        txtAboutBody.setFont(new Font("Arial", Font.PLAIN, 16));
        txtAboutBody.setForeground(Color.LIGHT_GRAY);
        txtAboutBody.setOpaque(false);
        txtAboutBody.setEditable(false);
        panelAbout.add(txtAboutBody);

        btnCerrarAbout = new JButton(GestorLenguaje.get("btn_cerrar"));
        btnCerrarAbout.setBackground(Color.BLACK);
        btnCerrarAbout.setForeground(Color.WHITE);
        btnCerrarAbout.addActionListener(e -> {
            panelAbout.setVisible(false);
            contenedorMenuInicio.setVisible(true);
            mostrarLogosFlotantes = true;
        });
        panelAbout.add(btnCerrarAbout);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaMenu().setVisible(true));
    }
}