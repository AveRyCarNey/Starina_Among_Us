package starina_among_us.vista;

import starina_among_us.modelo.Jugador;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import starina_among_us.red.ClienteRed;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JProgressBar;
import starina_among_us.modelo.GestorSonido;

public class PanelJuego extends JPanel implements KeyListener, ActionListener, FocusListener {

    private Jugador miJugador;
    // Motor del juego: Controla los FPS y el ciclo de actualización
    private Timer reloj;
    // Referencia a la conexión de red para enviar/recibir datos
    private ClienteRed clienteRed;
    
    // Almacén principal de datos: Diccionario que vincula un ID (Entero) con un Jugador (Objeto)
    // Nos permite buscar rápido a cualquier jugador por su número.
    private ConcurrentHashMap<Integer, Jugador> jugadoresConectados;
    
    // Identidad del usuario local: Define cuál de todos los muñecos soy "yo" para controlarlo.
    private int miId; 
    
    // Recursos gráficos del escenario
    private Image fondoMapa;
    
    // Banderas para saber qué teclas están presionadas
    private boolean arriba, abajo, izquierda, derecha;
    
    // Para detectar cuando frenamos
    private boolean estabaMoviendose = false; 
    
    private JButton botonKill;
    private int idVictimaCercana = -1; // Guardaremos aquí a quién vamos a matar
    
    private JButton botonVent;
    private int idVentCercana = -1; // Cuál alcantarilla tengo cerca (0, 1 o 2)
    
    private JButton botonReport;
    private int idCuerpoCercano = -1;
    
    
    private double camaraX = 0;
    private double camaraY = 0;
    private boolean camaraInicializada = false;
    
    
    private BufferedImage mapaDatos;
    private int spawnX = 400; // Por defecto
    private int spawnY = 300;
    private ArrayList<Point> listaVents = new ArrayList<>();
    
    // --- VARIABLES DE ESPECTADOR ---
    private int idEspectando = -1; // A quién está mirando la cámara actualmente
    private boolean modoEspectador = false; // ¿Estoy muerto y espectando?
    
    // IMÁGENES DE REPORTE (Separadas)
    private java.awt.image.BufferedImage imgReporteFondo; // El efecto
    private java.awt.image.BufferedImage imgReporteTexto; // Las letras
    
    private boolean mostrandoAnimacionReporte = false;
    
    private String miNombreElegido;
    private Color miColorElegido;
    
    // --- VARIABLES DEL LOBBY ---
    private boolean juegoIniciado = false;
    private boolean soyHost = false;
    private JButton botonIniciarPartida;
    private final int MIN_JUGADORES = 5; 
    
    
    private JButton botonUse;
    
    // --- VARIABLES DE VENTILACIÓN ---
    private boolean enVentilacion = false;
    private boolean animandoVent = false;
    private int ventActualIndex = -1;
    
    private java.awt.image.BufferedImage imgVentHole;
    
    // --- VARIABLES DE LA TABLET DE REUNIÓN ---
    private boolean mostrandoTabletReunion = false;
    private int tiempoRestanteReunion = 120; 
    private Timer timerReunion; 
    private int idVotadoSeleccionado = -1; 
    private boolean yaVote = false; 
    
    // NUEVAS VARIABLES DE IMÁGENES
    private java.awt.image.BufferedImage imgTabletFondo;
    private java.awt.image.BufferedImage imgVoteConfirmar;
    private java.awt.image.BufferedImage imgVoteCancelar;
    private java.awt.image.BufferedImage imgVoteSkip;
    private java.awt.image.BufferedImage imgVoteMegafono;
    private java.awt.image.BufferedImage imgVoteIVoted;
    private java.awt.image.BufferedImage imgVoteTripulante;
    
    // --- VARIABLES DEL CHAT DE REUNIÓN ---
    private javax.swing.JButton botonAbrirChat;
    private javax.swing.JScrollPane scrollChat;
    private javax.swing.JTextArea areaChat;
    private javax.swing.JTextField campoChat;
    private boolean chatAbierto = false;
    
    // --- VARIABLES DE LA EXPULSIÓN Y VOTOS ---
    private HashMap<Integer, Integer> registroVotos = new HashMap<>(); // Quién votó -> Por quién
    
    private java.awt.image.BufferedImage imgFondoEjected;
    private java.awt.image.BufferedImage imgCharEjectedOriginal;
    private java.awt.image.BufferedImage imgCharEjectedPintado;
    
    private boolean mostrandoAnimacionExpulsion = false;
    private Timer timerExpulsion;
    private double expulsionX = -100; // Posición horizontal del cuerpo flotando
    private double expulsionAngulo = 0; // Rotación
    private String textoExpulsion = "";
    private int charsMostradosExpulsion = 0; // Para el efecto de "máquina de escribir"
    private int idExpulsadoActual = -1;
    
    // Caché para no pintar los iconos 60 veces por segundo y evitar LAG
    private HashMap<Integer, BufferedImage> iconosPintadosTablet = new HashMap<>();
    
    // NUEVO: Para saber a quién ponerle el megáfono
    private int idReportadorActual = -1;
    
    // --- VARIABLES DE FIN DE JUEGO ---
    private boolean juegoTerminado = false;
    private boolean victoriaLocal = false; // ¿Ganó mi equipo?
    private BufferedImage imgVictoriaFondo;
    private BufferedImage imgDerrotaFondo;
    
    // NUEVAS: Para la transición de oscurecimiento
    private boolean animandoFinDeJuego = false;
    private float opacidadFinJuego = 0.0f;
    private Timer timerFinDeJuego;
    
    // NUEVAS: Para la Fase 2 de la animación
    private float opacidadImagenFin = 0.0f;
    private String textoGanadores = "";
    
    // --- VARIABLES DE LOS GANADORES EN PANTALLA ---
    private java.awt.image.BufferedImage imgBaseGanador;
    private java.util.ArrayList<java.awt.image.BufferedImage> spritesGanadores = new java.util.ArrayList<>();
    private java.util.ArrayList<String> nombresGanadores = new java.util.ArrayList<>();
    
    private JButton botonQuit;
    
    
    private starina_among_us.modelo.GestorTareas gestorTareas = new starina_among_us.modelo.GestorTareas();
    private starina_among_us.modelo.Tarea tareaActualEnZona = null; // Para saber qué tarea tenemos enfrente
    
    public starina_among_us.modelo.GestorTareas getGestorTareas() { return gestorTareas; }
    
    private int contadorPasos = 0;
    
    

    public PanelJuego(String mapaElegido, String ipServidor, String nombre, Color color, boolean esHost) {
        this.setLayout(null);
        this.setBackground(Color.DARK_GRAY);
        this.setFocusable(true);
        this.addKeyListener(this);
        this.addFocusListener(this);
        this.miNombreElegido = nombre;
        this.miColorElegido = color;
        this.soyHost = esHost;

        
        setLayout(null); // Para poder poner botones libres
        
        // Cargar el mapa
        cargarMapa(mapaElegido);
        
        // Inicializar la base de datos de jugadores vacía
        jugadoresConectados = new ConcurrentHashMap<>();

        
        
        // --- CARGAR HOJA DE SPRITES ---
        BufferedImage hojaBotones = null;
        try {
            hojaBotones = javax.imageio.ImageIO.read(getClass().getResource("/starina_among_us/recursos/botones/gui_botones.png"));
        } catch (Exception e) {
            System.out.println("¡ALERTA! No se encontró gui_botones.png");
        }

        // --- INICIALIZAR BOTONES (Primero creamos, luego configuramos) ---
        
        // --- BOTÓN DEL LOBBY (Solo para el Host) ---
        botonIniciarPartida = new JButton("ESPERANDO JUGADORES...");
        botonIniciarPartida.setBounds(250, 450, 300, 50);
        botonIniciarPartida.setBackground(new Color(50, 150, 50));
        botonIniciarPartida.setForeground(Color.WHITE);
        botonIniciarPartida.setFont(new Font("Arial", Font.BOLD, 16));
        botonIniciarPartida.setVisible(soyHost); // Solo el creador lo ve
        botonIniciarPartida.addActionListener(e -> {
            if (jugadoresConectados.size() >= MIN_JUGADORES) {
                iniciarPartidaLobby(); // Lo inicio para mí
                clienteRed.enviar("START_GAME"); // Les aviso a los demás
            }
        });
        add(botonIniciarPartida);
        
        

        // --- BOTÓN USAR / MISIÓN ---
        botonUse = new JButton();
        botonUse.setBounds(50, 430, 113, 116);
        botonUse.setContentAreaFilled(false);
        botonUse.setBorderPainted(false);
        botonUse.setFocusPainted(false);
        botonUse.setEnabled(false); // Apagado por defecto
        
        botonUse.addActionListener(e -> {
            if (tareaActualEnZona == null) return; // Seguridad extra
            
            // --- TAREA 1: EL LABORATORIO ---
            if (tareaActualEnZona.getId().equals("LAB_FRASCOS")) {
                System.out.println("🧪 Bebiendo sustancia desconocida...");
                tareaActualEnZona.setCompletada(true); 
                botonUse.setEnabled(false); 
                
                // Animación de colores (5 segundos)
                javax.swing.Timer timerRave = new javax.swing.Timer(1000, null);
                int[] contador = {0};

                timerRave.addActionListener(ev -> {
                    contador[0]++;
                    if (contador[0] <= 5) {
                        int r = (int)(Math.random() * 255);
                        int g = (int)(Math.random() * 255);
                        int b = (int)(Math.random() * 255);
                        Color colorLoco = new Color(r, g, b);
                        
                        aplicarColorRave(miId, colorLoco);
                        clienteRed.enviar("COLOR_RAVE," + miId + "," + r + "," + g + "," + b);
                    } else {
                        Color colorReal = miJugador.getColorOriginal();
                        aplicarColorRave(miId, colorReal);
                        clienteRed.enviar("COLOR_RAVE," + miId + "," + colorReal.getRed() + "," + colorReal.getGreen() + "," + colorReal.getBlue());
    
                        // --- NUEVA LÓGICA DE REGISTRO ---
                        boolean esNueva = gestorTareas.registrarTareaCompletada("LAB_FRASCOS");
                        if (esNueva) {
                            clienteRed.enviar("TAREA_LISTA,LAB_FRASCOS");
                            GestorSonido.jugar("general_sounds/task_Complete.wav");
                            verificarFinDeJuego();
                        }
    
                        ((javax.swing.Timer)ev.getSource()).stop();
                    }
                });
                timerRave.start();
            } 
            
            // --- TAREA 2: LA OFICINA DEL PROFESOR ---
            else if (tareaActualEnZona.getId().equals("OFI_TRABAJO")) {
                System.out.println("📄 Abriendo minijuego de ordenar hojas...");
                
                // Creamos la ventana emergente y la hacemos visible
                starina_among_us.vista.VistaMisionOficina minijuego = new starina_among_us.vista.VistaMisionOficina(PanelJuego.this);
                minijuego.setVisible(true); 
            }
            
            // --- TAREA 3: LA PIZARRA DE MATEMÁTICAS ---
            else if (tareaActualEnZona.getId().equals("PIZARRA_MATH")) {
                System.out.println("🧮 Abriendo el examen sorpresa...");
                starina_among_us.vista.VistaMisionPizarra minijuego = new starina_among_us.vista.VistaMisionPizarra(PanelJuego.this);
                minijuego.setVisible(true); 
            }
            // --- TAREA 4: LA BIBLIOTECA ---
            else if (tareaActualEnZona.getId().equals("BIBLIO_LIBROS")) {
                System.out.println("📚 Abriendo estantería de libros...");
                starina_among_us.vista.VistaMisionBiblioteca minijuego = new starina_among_us.vista.VistaMisionBiblioteca(PanelJuego.this);
                minijuego.setVisible(true); 
            }
        });
        
        botonUse.setFocusable(false);
        
        // 1. BOTÓN KILL (Abajo Derecha)
        botonKill = new JButton();
        // Ajustamos el tamaño a 115x115 según tu recorte
        botonKill.setBounds(650, 430, 115, 115); 
        botonKill.setContentAreaFilled(false);
        botonKill.setBorderPainted(false);
        botonKill.setFocusPainted(false);
        botonKill.setVisible(false);
        botonKill.setEnabled(false);
        botonKill.setFocusable(false);
        
        // 2. BOTÓN VENT (Arriba del Kill)
        botonVent = new JButton();
        // Lo subimos un poco (Y=310) para que no tape al Kill
        botonVent.setBounds(650, 310, 120, 110); 
        botonVent.setContentAreaFilled(false);
        botonVent.setBorderPainted(false);
        botonVent.setFocusPainted(false);
        botonVent.setVisible(false);
        botonVent.setEnabled(false);
        botonVent.setFocusable(false);
        
        // 3. BOTÓN REPORT (A la izquierda del Kill)
        botonReport = new JButton();
        // Lo movemos a la izquierda (X=520) para que no choque con el Kill
        botonReport.setBounds(520, 433, 117, 112); 
        botonReport.setContentAreaFilled(false);
        botonReport.setBorderPainted(false);
        botonReport.setFocusPainted(false);
        botonReport.setVisible(false);
        botonReport.setEnabled(false);
        
        
        // --- BOTÓN QUIT (Para salir al final del juego) ---
        botonQuit = new JButton();
        botonQuit.setBounds(650, 400, 125, 142); // Ubicado abajo a la derecha
        botonQuit.setContentAreaFilled(false);
        botonQuit.setBorderPainted(false);
        botonQuit.setFocusPainted(false);
        botonQuit.setVisible(false); // Invisible hasta que termine la partida
        botonQuit.setFocusable(false);
        
        botonQuit.addActionListener(e -> {
            System.out.println("🚪 Limpiando la sala y saliendo al Menú Principal...");
            
            // 1. Si soy el HOST, le ordeno al servidor que borre la memoria de esta partida
            if (soyHost && clienteRed != null) {
                clienteRed.enviar("RESET_SERVER");
            }
            
            // 2. Apagamos nuestra propia conexión a internet
            if (clienteRed != null) {
                clienteRed.desconectar();
            }
            
            // 3. Limpiamos nuestras variables locales por si acaso
            jugadoresConectados.clear();
            registroVotos.clear();
            
            // 4. Cerramos la ventana actual
            javax.swing.SwingUtilities.getWindowAncestor(this).dispose();
            
            // 5. Volvemos al menú
            new VentanaMenu().setVisible(true);
        });
        
        this.add(botonQuit);
        
        
        // --- 1. ÁREA DE MENSAJES (JTextArea) ---
        areaChat = new javax.swing.JTextArea();
        areaChat.setEditable(false);
        areaChat.setLineWrap(true);
        areaChat.setWrapStyleWord(true);
        areaChat.setFont(new Font("Arial", Font.BOLD, 14));
        areaChat.setBackground(new Color(220, 230, 240)); // Un celeste muy clarito
        areaChat.setForeground(Color.BLACK);
        
        // Le ponemos una barra de desplazamiento
        scrollChat = new javax.swing.JScrollPane(areaChat);
        scrollChat.setBounds(480, 110, 250, 320); // Ubicado a la derecha de la tablet
        scrollChat.setVisible(false); // Invisible al inicio
        this.add(scrollChat);

        // --- 2. CAMPO PARA ESCRIBIR (JTextField) ---
        campoChat = new javax.swing.JTextField();
        campoChat.setBounds(480, 440, 250, 35);
        campoChat.setFont(new Font("Arial", Font.PLAIN, 16));
        campoChat.setVisible(false);
        
        // Al presionar ENTER en el campo de texto, se envía el mensaje
        campoChat.addActionListener(e -> {
            enviarMensajeChat();
        });
        this.add(campoChat);

        // --- 3. BOTÓN PARA ABRIR/CERRAR EL CHAT ---
        botonAbrirChat = new javax.swing.JButton(); // Sin texto
        botonAbrirChat.setBounds(650, 60, 55, 59); // Tamaño exacto de tu recorte
        botonAbrirChat.setContentAreaFilled(false);
        botonAbrirChat.setBorderPainted(false);
        botonAbrirChat.setFocusPainted(false);
        botonAbrirChat.setVisible(false);
        botonAbrirChat.setFocusable(false);
        
        botonAbrirChat.addActionListener(e -> {
            chatAbierto = !chatAbierto; // Alternar estado
            scrollChat.setVisible(chatAbierto);
            
            // --- NUEVO: Solo mostramos la caja de texto si ESTOY VIVO ---
            if (chatAbierto && miJugador != null && miJugador.isVivo()) {
                campoChat.setVisible(true);
                campoChat.requestFocus(); // Poner el cursor listo
            } else {
                campoChat.setVisible(false); // Los muertos no tienen teclado
            }
        });
        this.add(botonAbrirChat);
        
        

        // --- APLICAR TUS RECORTES EXACTOS ---
        if (hojaBotones != null) {
            try {
                // TUS COORDENADAS:
                
                // KILL: 921, 116 | 115x115
                BufferedImage imgKill = starina_among_us.modelo.HerramientasImagen.recortar(hojaBotones, 921, 116, 115, 115);
                
                // VENT: 1243, 2 | 120x110
                BufferedImage imgVent = starina_among_us.modelo.HerramientasImagen.recortar(hojaBotones, 1243, 2, 120, 110);
                
                // REPORT: 589, 3 | 117x112
                BufferedImage imgReport = starina_among_us.modelo.HerramientasImagen.recortar(hojaBotones, 589, 3, 117, 112);
                
                //USE: 1150, 112 | 113x116
                BufferedImage imgUse = starina_among_us.modelo.HerramientasImagen.recortar(hojaBotones, 1150, 112, 113, 116);
                
                // QUIT: 124, 5 | 125x142 
                BufferedImage imgQuit = starina_among_us.modelo.HerramientasImagen.recortar(hojaBotones, 124, 5, 125, 142);
                botonQuit.setIcon(new ImageIcon(imgQuit));
                
                // Transparencias (50%)
                BufferedImage killGris = starina_among_us.modelo.HerramientasImagen.hacerTransparente(imgKill, 0.5f);
                BufferedImage ventGris = starina_among_us.modelo.HerramientasImagen.hacerTransparente(imgVent, 0.5f);
                BufferedImage reportGris = starina_among_us.modelo.HerramientasImagen.hacerTransparente(imgReport, 0.5f);
                BufferedImage useGris = starina_among_us.modelo.HerramientasImagen.hacerTransparente(imgUse, 0.5f);
                
                // Asignar Iconos
                botonKill.setIcon(new ImageIcon(imgKill));
                botonKill.setDisabledIcon(new ImageIcon(killGris));
                
                botonVent.setIcon(new ImageIcon(imgVent));
                botonVent.setDisabledIcon(new ImageIcon(ventGris));
                
                botonReport.setIcon(new ImageIcon(imgReport));
                botonReport.setDisabledIcon(new ImageIcon(reportGris));
                
                botonUse.setIcon(new ImageIcon(imgUse));
                botonUse.setDisabledIcon(new ImageIcon(useGris));
                
            } catch (Exception e) {
                System.out.println("Error recortando botones: " + e.getMessage());
            }
        } else {
            // Respaldo de Texto
            botonKill.setText("MATAR"); botonKill.setContentAreaFilled(true); botonKill.setBackground(Color.RED);
            botonVent.setText("VENT"); botonVent.setContentAreaFilled(true); botonVent.setBackground(Color.GRAY);
            botonReport.setText("REPORT"); botonReport.setContentAreaFilled(true); botonReport.setBackground(Color.ORANGE);
        }

        // --- DEFINIR ACCIONES ---
        
        botonKill.addActionListener(e -> {
            if (idVictimaCercana != -1) {
                GestorSonido.jugar("impostor_kill.wav");
                clienteRed.enviar("MATAR," + idVictimaCercana);
                botonKill.setEnabled(false);
                idVictimaCercana = -1;
                this.requestFocusInWindow(); 
            }
        });

        botonVent.addActionListener(e -> {
            if (animandoVent) return; 

            if (!enVentilacion) {
                System.out.println("⬇️ BAJANDO A LA ALCANTARILLA...");
                animandoVent = true;
                enVentilacion = true; 
                
                // NUEVO: Me marco en animación y aviso a la red
                miJugador.setAnimandoVent(true);
                GestorSonido.jugar("Vent/ventGroundEnter.wav");
                clienteRed.enviar("VENT_ANIM," + miId + ",true");
                
                if (idVentCercana == -1 || idVentCercana >= listaVents.size()) idVentCercana = 0;
                ventActualIndex = idVentCercana;

                java.awt.Point v = listaVents.get(ventActualIndex);
                int miOffsetX = 18;
                
                miJugador.setX(v.x - 25 + miOffsetX);
                miJugador.setY(v.y - 55);
                clienteRed.enviar("MOV," + miId + "," + (int)miJugador.getX() + "," + (int)miJugador.getY() + "," + miJugador.isMirandoDerecha() + ",false");

                javax.swing.Timer t = new javax.swing.Timer(20, null);
                int[] frames = {0};
                t.addActionListener(ev -> {
                    miJugador.setY(miJugador.getY() + 2); 
                    frames[0]++;
                    clienteRed.enviar("MOV," + miId + "," + (int)miJugador.getX() + "," + (int)miJugador.getY() + "," + miJugador.isMirandoDerecha() + ",false");
                    repaint();
                    
                    if (frames[0] >= 35) { 
                        t.stop();
                        animandoVent = false;
                        
                        // NUEVO: Terminé de animar, apago la variable
                        miJugador.setAnimandoVent(false);
                        clienteRed.enviar("VENT_ANIM," + miId + ",false");
                        
                        clienteRed.enviar("VENT," + miId + ",true");
                        miJugador.setEnVentilacion(true);
                    }
                });
                t.start();
                
            } else {
                System.out.println("⬆️ SUBIENDO A LA SUPERFICIE...");
                animandoVent = true;
                
                // NUEVO: Me marco en animación y aviso a la red
                miJugador.setAnimandoVent(true);
                clienteRed.enviar("VENT_ANIM," + miId + ",true");
                
                clienteRed.enviar("VENT," + miId + ",false");
                miJugador.setEnVentilacion(false);
                
                int miOffsetX = 18; 
                javax.swing.Timer t = new javax.swing.Timer(20, null);
                int[] frames = {0};
                t.addActionListener(ev -> {
                    java.awt.Point v = listaVents.get(ventActualIndex);
                    miJugador.setX(v.x - 25 + miOffsetX); 
                    miJugador.setY(miJugador.getY() - 2); 
                    frames[0]++;
                    clienteRed.enviar("MOV," + miId + "," + (int)miJugador.getX() + "," + (int)miJugador.getY() + "," + miJugador.isMirandoDerecha() + ",false");
                    repaint();
                    
                    if (frames[0] >= 35) { 
                        t.stop();
                        animandoVent = false;
                        enVentilacion = false; 
                        
                        // NUEVO: Terminé de animar, apago la variable
                        miJugador.setAnimandoVent(false);
                        clienteRed.enviar("VENT_ANIM," + miId + ",false");
                    }
                });
                t.start();
            }
        });
        
        botonReport.addActionListener(e -> {
            if (idCuerpoCercano != -1) {
                clienteRed.enviar("REPORT," + idCuerpoCercano);
                this.requestFocusInWindow();
            }
        });
        
        

        // --- AGREGAR AL PANEL ---
        this.add(botonKill);
        this.add(botonVent);
        this.add(botonReport);
        this.add(botonUse);

        clienteRed = new ClienteRed(this, ipServidor);
        reloj = new Timer(15, this);
        reloj.start();
        
        // Cargar el hueco de la ventilación
        try {
            java.awt.image.BufferedImage ventsImg = javax.imageio.ImageIO.read(getClass().getResource("/starina_among_us/recursos/eventos/vents.png"));
            // Recorte exacto: X=434, Y=7, Ancho=82, Alto=40
            imgVentHole = starina_among_us.modelo.HerramientasImagen.recortar(ventsImg, 434, 7, 82, 40);
            System.out.println("✅ Hueco de vent cargado con éxito.");
        } catch (Exception e) {
            System.out.println("❌ Error cargando el hueco de vents.png: " + e.getMessage());
        }
        
        
        
        
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                
                // 1. MODO ESPECTADOR (Si estoy muerto)
                if (modoEspectador) {
                    // Click Izquierdo (Botón 1) -> Anterior
                    // Click Derecho (Botón 3) -> Siguiente
                    boolean avanzar = (e.getButton() != java.awt.event.MouseEvent.BUTTON1);
                    cambiarObjetivoEspectador(avanzar);
                }
                
                // 2. MODO VENTILACIÓN (Si estoy vivo y escondido en la alcantarilla)
                else if (enVentilacion && !animandoVent && listaVents.size() > 1) {
                    
                    // Click Izquierdo = Vent anterior / Click Derecho = Vent siguiente
                    if (javax.swing.SwingUtilities.isLeftMouseButton(e)) {
                        ventActualIndex--;
                        if (ventActualIndex < 0) ventActualIndex = listaVents.size() - 1;
                    } else if (javax.swing.SwingUtilities.isRightMouseButton(e)) {
                        ventActualIndex++;
                        if (ventActualIndex >= listaVents.size()) ventActualIndex = 0;
                    }
                    GestorSonido.jugar("Vent/ventMoveGround1.wav");
                    
                    // Teletransportar al jugador (mantenemos su Y sumado en 50 para que siga "hundido")
                    Point nuevaVent = listaVents.get(ventActualIndex);
                    int miOffsetX = 18; 
                    
                    miJugador.setX(nuevaVent.x - 25 + miOffsetX); 
                    miJugador.setY(nuevaVent.y - 55 + 50);
                    
                    clienteRed.enviar("MOV," + miId + "," + (int)miJugador.getX() + "," + (int)miJugador.getY() + "," + miJugador.isMirandoDerecha() + ",false");
                    
                    repaint(); // La cámara saltará a la nueva vent
                }
                // 3. MODO VOTACIÓN (Si la tablet está abierta y aún no he votado)
                else if (mostrandoTabletReunion && !mostrandoAnimacionReporte && !yaVote) {
                    
                    int mx = e.getX();
                    int my = e.getY();
                    
                    // Recreamos las matemáticas de la pantalla para saber dónde están los botones
                    int tabletW = 750, tabletH = 550, padding = 45;
                    int tabletX = (getWidth() - tabletW) / 2;
                    int tabletY = (getHeight() - tabletH) / 2;
                    int screenX = tabletX + padding;
                    int screenY = tabletY + padding;
                    int screenW = tabletW - (padding * 2);
                    int screenH = tabletH - (padding * 2);
                    
                    // --- A) ¿CLIC EN SKIP VOTE? ---
                    int skipX = screenX + 10;
                    int skipY = screenY + screenH - 35;
                    if (mx >= skipX && mx <= skipX + 110 && my >= skipY && my <= skipY + 25) {
                        System.out.println("🗳️ Has votado por SKIP VOTE");
                        idVotadoSeleccionado = -2; // -2 significará Skip
                        yaVote = true;
                        
                        jugadoresConectados.get(miId).setHaVotado(true);
                        clienteRed.enviar("VOTO," + miId + "," + idVotadoSeleccionado);
                        
                        repaint();
                        return;
                    }
                    
                    // --- B) ¿CLIC EN LOS JUGADORES? ---
                    int boxW = 280, boxH = 45, espaciadoColumnas = 20, espaciadoFilas = 55;
                    int startX = screenX + (screenW - (boxW * 2 + espaciadoColumnas)) / 2;
                    int startY = screenY + 70;
                    
                    int col = 0, row = 0;
                    for (Jugador j : jugadoresConectados.values()) {
                        int drawX = startX + (col * (boxW + espaciadoColumnas));
                        int drawY = startY + (row * espaciadoFilas);
                        
                        // ¿El clic chocó con esta caja?
                        if (mx >= drawX && mx <= drawX + boxW && my >= drawY && my <= drawY + boxH) {
                            
                            if (!j.isVivo()) return; // No se puede votar a los muertos
                            
                            // --- NUEVA REGLA: NO AUTO-VOTO ---
                            if (j.getId() == miId) {
                                System.out.println("🚫 No puedes votarte a ti mismo.");
                                return; // Ignoramos el clic por completo
                            }
                            
                            // Si ya lo tenía seleccionado, reviso si hizo clic en los botones
                            if (idVotadoSeleccionado == j.getId()) {
                                
                                // ¿Clic en Confirmar? (Check verde)
                                if (mx >= drawX + boxW - 100 && mx <= drawX + boxW - 60) {
                                    System.out.println("🗳️ Voto confirmado para: " + j.getNombre());
                                    yaVote = true;
                                    
                                    jugadoresConectados.get(miId).setHaVotado(true);
                                    clienteRed.enviar("VOTO," + miId + "," + idVotadoSeleccionado);
                                }
                                // ¿Clic en Cancelar? (X roja)
                                else if (mx >= drawX + boxW - 50 && mx <= drawX + boxW - 10) {
                                    idVotadoSeleccionado = -1; // Deseleccionar
                                }
                                
                            } else {
                                // Seleccionar a este nuevo jugador
                                idVotadoSeleccionado = j.getId();
                            }
                            repaint();
                            return;
                        }
                        
                        col++;
                        if (col > 1) { col = 0; row++; }
                    }
                }
            }
        });
        
        
        // --- CARGAR ASSETS DE EVENTOS (REPORTE) ---
        imgReporteFondo = null; // Aseguramos que empiecen vacías
        imgReporteTexto = null;

        try {
            System.out.println("Intentando cargar: /starina_among_us/recursos/eventos/assets_events.png");
            
            // 1. Cargar la hoja
            java.net.URL urlImagen = getClass().getResource("/starina_among_us/recursos/eventos/assets_events.png");
            
            if (urlImagen == null) {
                System.out.println("❌ ERROR FATAL: La URL de la imagen es NULL. Verifica la carpeta 'eventos' y el nombre 'asstes_events.png'");
            } else {
                java.awt.image.BufferedImage hojaEventos = javax.imageio.ImageIO.read(urlImagen);
                System.out.println("✅ Imagen cargada correctamente. Tamaño: " + hojaEventos.getWidth() + "x" + hojaEventos.getHeight());

                // 2. RECORTAR FONDO (Effect) -> 1, 1 | 950 x 435
                imgReporteFondo = starina_among_us.modelo.HerramientasImagen.recortar(hojaEventos, 1, 1, 940, 435);
                
                // 3. RECORTAR TEXTO (Dead Body) -> 1, 437 | 421 x 369
                imgReporteTexto = starina_among_us.modelo.HerramientasImagen.recortar(hojaEventos, 1, 437, 420, 205);
                
                System.out.println("✂️ Recortes realizados con éxito.");
            }
            
        } catch (Exception e) {
            System.out.println("❌ EXCEPCIÓN AL CARGAR IMAGEN: " + e.getMessage());
            e.printStackTrace();
        }
        
        // --- CARGAR ASSETS DE VOTACIÓN ---
        try {
            java.awt.image.BufferedImage hojaVotacion = javax.imageio.ImageIO.read(getClass().getResource("/starina_among_us/recursos/eventos/Voting2.png"));
            imgTabletFondo = starina_among_us.modelo.HerramientasImagen.recortar(hojaVotacion, 8, 7, 856, 581);
            imgVoteConfirmar = starina_among_us.modelo.HerramientasImagen.recortar(hojaVotacion, 570, 665, 48, 50);
            imgVoteCancelar = starina_among_us.modelo.HerramientasImagen.recortar(hojaVotacion, 511, 665, 50, 50);
            imgVoteSkip = starina_among_us.modelo.HerramientasImagen.recortar(hojaVotacion, 995, 665, 110, 25);
            imgVoteMegafono = starina_among_us.modelo.HerramientasImagen.recortar(hojaVotacion, 359, 665, 70, 62);
            imgVoteIVoted = starina_among_us.modelo.HerramientasImagen.recortar(hojaVotacion, 241, 665, 36, 35);
            imgVoteTripulante = starina_among_us.modelo.HerramientasImagen.recortar(hojaVotacion, 8, 665, 55, 45);
            
            java.awt.image.BufferedImage imgIconoChat = starina_among_us.modelo.HerramientasImagen.recortar(hojaVotacion, 628, 665, 55, 59);
            botonAbrirChat.setIcon(new javax.swing.ImageIcon(imgIconoChat));
            System.out.println("✅ Tablet de votación cargada con éxito.");
        } catch (Exception e) {
            System.out.println("❌ Error cargando Voting.png: " + e.getMessage());
        }
        
        // --- CARGAR ASSETS DE EXPULSIÓN ---
        try {
            imgFondoEjected = javax.imageio.ImageIO.read(getClass().getResource("/starina_among_us/recursos/eventos/ejected.png"));
            imgCharEjectedOriginal = javax.imageio.ImageIO.read(getClass().getResource("/starina_among_us/recursos/eventos/Ejected_character.png"));
        } catch (Exception e) {
            System.out.println("❌ Error cargando assets de expulsión: " + e.getMessage());
        }
        
        // --- CARGAR ASSETS DE FIN DE JUEGO ---
        try {
            imgVictoriaFondo = javax.imageio.ImageIO.read(getClass().getResource("/starina_among_us/recursos/eventos/win.jpg"));
            imgDerrotaFondo = javax.imageio.ImageIO.read(getClass().getResource("/starina_among_us/recursos/eventos/defeat.jpg"));
            imgBaseGanador = javax.imageio.ImageIO.read(getClass().getResource("/starina_among_us/recursos/personajes/rozul.png"));
        } catch (Exception e) {
            System.out.println("❌ Error cargando pantallas de victoria/derrota: " + e.getMessage());
        }
        
        
        
    }
                
    public void iniciarPartidaLobby() {
     this.juegoIniciado = true;
     if (botonIniciarPartida != null) {
         botonIniciarPartida.setVisible(false);
     }
     System.out.println("🚀 ¡LA PARTIDA HA COMENZADO!");
 }
    
    public void cargarMapa(String nombreMapa) {
    try {
        System.out.println("Cargando mapa: " + nombreMapa);
        // 1. Cargar las imágenes
        fondoMapa = new javax.swing.ImageIcon(getClass().getResource("/starina_among_us/recursos/mapas/" + nombreMapa + "_downscale.png")).getImage();
        mapaDatos = javax.imageio.ImageIO.read(getClass().getResource("/starina_among_us/recursos/mapas/" + nombreMapa + "_DataMap_downscale.png"));

        // 1. LIMPIEZA TOTAL (Asegúrate de que la lista NO sea static)
listaVents.clear(); 

// Lista temporal para guardar todos los píxeles rojos encontrados
java.util.List<Point> todosLosPixelesRojos = new java.util.ArrayList<>();

for (int i = 0; i < mapaDatos.getWidth(); i += 4) {
    for (int j = 0; j < mapaDatos.getHeight(); j += 4) {
        Color c = new Color(mapaDatos.getRGB(i, j));
        if (c.getRed() > 200 && c.getGreen() < 50 && c.getBlue() < 50) {
            todosLosPixelesRojos.add(new Point(i, j));
        }
    }
}

// 2. AGRUPAR PÍXELES (Algoritmo de Clúster)
while (!todosLosPixelesRojos.isEmpty()) {
    Point semilla = todosLosPixelesRojos.remove(0);
    java.util.List<Point> grupo = new java.util.ArrayList<>();
    grupo.add(semilla);

    // Buscamos todos los píxeles que pertenezcan a esta misma mancha roja
    for (int k = 0; k < todosLosPixelesRojos.size(); k++) {
        Point candidato = todosLosPixelesRojos.get(k);
        // Si está a menos de 100 píxeles de la semilla, es la misma alcantarilla
        if (semilla.distance(candidato) < 100) {
            grupo.add(todosLosPixelesRojos.remove(k));
            k--; // Ajustamos el índice al eliminar
        }
    }

    // 3. CALCULAR EL CENTRO REAL
    long sumX = 0, sumY = 0;
    for (Point p : grupo) {
        sumX += p.x;
        sumY += p.y;
    }
    Point centroReal = new Point((int)(sumX / grupo.size()), (int)(sumY / grupo.size()));
    
    listaVents.add(centroReal);
    System.out.println("✅ Vent #" + (listaVents.size()-1) + " consolidada en: " + centroReal.x + "," + centroReal.y);
}     
        // --- BLINDAJE ANTI-PÍXEL FANTASMA ---
        // Obtenemos el tamaño REAL de la imagen JPG
        int anchoReal = fondoMapa.getWidth(null);
        int altoReal = fondoMapa.getHeight(null);
        
        // El escáner solo llegará hasta donde termine la imagen JPG real
        int limiteX = Math.min(anchoReal, mapaDatos.getWidth());
        int limiteY = Math.min(altoReal, mapaDatos.getHeight());

        // 2. ¡EL ESCÁNER!
        for (int x = 0; x < limiteX; x += 5) {
            for (int y = 0; y < limiteY; y += 5) {
                int pixel = mapaDatos.getRGB(x, y);
                Color c = new Color(pixel, true);
                
                // A) ¿Es CELESTE? (Spawn Point)
                if (c.getRed() < 50 && c.getGreen() > 200 && c.getBlue() > 200) {
                    spawnX = x; spawnY = y;
                }
                // B) ¿Es ROJO? (Alcantarilla)
                else if (c.getRed() > 200 && c.getGreen() < 50 && c.getBlue() < 50) {
                    boolean esNueva = true;
                    for (java.awt.Point v : listaVents) {
                        if (v.distance(x, y) < 40) { esNueva = false; break; }
                    }
                    if (esNueva) listaVents.add(new java.awt.Point(x, y));
                }
            }
        }
        // Creamos una lista de "Falso/Verdadero" del mismo tamaño que las misiones encontradas
        
        System.out.println("✅ Mapa cargado. Tamaño Real: " + anchoReal + "x" + altoReal);
        System.out.println("✅ Spawn corregido en: " + spawnX + "," + spawnY + " | Vents: " + listaVents.size());
    } catch (Exception e) {
        System.out.println("❌ Error cargando el mapa: " + e.getMessage());
    }
}
    
    public void registrarVotoRed(int idVotante, int idVotado) {
     if (jugadoresConectados.containsKey(idVotante)) {
         jugadoresConectados.get(idVotante).setHaVotado(true);
         registroVotos.put(idVotante, idVotado); // Guardamos la decisión
         repaint(); 
     }
 }
    
    
    public boolean esPasoValido(double posX, double posY) {
        if (mapaDatos == null || fondoMapa == null) return true; 

        int pieX = (int) posX + 25; 
        int pieY = (int) posY + 55; 

        int limiteX = Math.min(fondoMapa.getWidth(null), mapaDatos.getWidth());
        int limiteY = Math.min(fondoMapa.getHeight(null), mapaDatos.getHeight());

        if (pieX < 0 || pieX >= limiteX || pieY < 0 || pieY >= limiteY) return false;

        // Leemos el color INCLUYENDO la transparencia (true)
        Color c = new Color(mapaDatos.getRGB(pieX, pieY), true);

        // --- LA MAGIA ESTÁ AQUÍ ---
        // 1. Si el píxel es transparente (Alpha bajo) o blanco, ES PISO. ¡Pasa libremente!
        if (c.getAlpha() < 50 || (c.getRed() > 200 && c.getGreen() > 200 && c.getBlue() > 200)) {
            return true;
        }

        // 2. Si es de color NEGRO OSCURO y además es SÓLIDO (opaco), entonces SÍ ES PARED.
        if (c.getRed() < 100 && c.getGreen() < 100 && c.getBlue() < 100) {
            return false; // CHOQUE
        }
        
        // Si es cualquier otro color sólido (Celeste, Rojo, Verde), déjalo caminar por encima
        return true; 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Forzar el estado del botón visualmente en cada frame
        if (botonVent != null && (enVentilacion || animandoVent)) {
            botonVent.setEnabled(true);
        }
        try {
            Graphics2D g2 = (Graphics2D) g;
            java.awt.geom.AffineTransform camaraOriginal = g2.getTransform();
            
            double zoom = 1.0; // Cambia esto si lo quieres más cerca (ej. 1.2)
            
            double objetivoX = getWidth() / 2.0; 
            double objetivoY = getHeight() / 2.0;
            
            int idASeguir = (modoEspectador && idEspectando != -1) ? idEspectando : miId;
            
            // --- CÁMARA SUAVE (LERP) ---
            if (jugadoresConectados.containsKey(idASeguir)) {
                Jugador centro = jugadoresConectados.get(idASeguir);
                objetivoX = centro.getX() + 25; 
                objetivoY = centro.getY() + 30;
                
                if (!camaraInicializada) {
                    camaraX = objetivoX;
                    camaraY = objetivoY;
                    camaraInicializada = true; 
                } else {
                    camaraX += (objetivoX - camaraX) * 0.10;
                    camaraY += (objetivoY - camaraY) * 0.10;
                }
            } else {
                camaraX = objetivoX;
                camaraY = objetivoY;
            }
            
            // Mover el "lienzo" a la posición de la cámara
            g2.translate(getWidth() / 2.0, getHeight() / 2.0);
            g2.scale(zoom, zoom);                              
            g2.translate(-camaraX, -camaraY);              
            
            // 1. DIBUJAR MAPA 
            if (fondoMapa != null && mapaDatos != null) {
                g2.drawImage(fondoMapa, 0, 0, fondoMapa.getWidth(null), fondoMapa.getHeight(null), this);
            }
            
            // --- 1. DIBUJAR TODAS LAS ALCANTARILLAS EN EL PISO SIEMPRE ---
            if (imgVentHole != null) {
                for (java.awt.Point v : listaVents) {
                    // El hueco mide 82x40. Para que el centro de la imagen coincida 
                    // exactamente con el centro de tu punto rojo, le restamos la mitad:
                    // X = -41 (mitad de 82) | Y = -20 (mitad de 40)
                    // Si notas que aún queda descuadrado, cambia estos números (ej: -41 + 5, -20 - 10)
                    int offsetX = -18;
                    int offsetY = -20; 
                    
                    g2.drawImage(imgVentHole, v.x + offsetX, v.y + offsetY, null);
                    
                }
            }
            

            // --- 2. DIBUJAR JUGADORES Y APLICAR LA MÁSCARA AL ENTRAR ---
            for (Jugador j : jugadoresConectados.values()) {
                
                // 1. CERO FANTASMAS
                if (!j.isVivo() && j.isCuerpoReportado()) continue; 
                
                // 2. OCULTAR SI ESTÁ TOTALMENTE EN VENTILACIÓN
                if (j.isEnVentilacion()) {
                    // Si soy yo mismo y NO me estoy animando (ya estoy al fondo), no me dibujo
                    if (j.getId() == miId && !animandoVent) continue;
                    // Si es OTRO jugador y está en ventilación, se vuelve invisible
                    if (j.getId() != miId) continue;
                }
                
                // 3. MÁSCARA AUTOMÁTICA (Solo si estoy animando la entrada/salida)
                java.awt.Shape clipOriginal = g2.getClip();
                boolean mascaraAplicada = false;

                // ¡AQUÍ ESTÁ LA MAGIA! Solo calculamos el hueco si la variable está encendida
                if (j.isAnimandoVent()) {
                    for (java.awt.Point v : listaVents) {
                        if (Math.abs(j.getX() + 25 - v.x) < 40 && Math.abs(j.getY() + 55 - v.y) < 80) {
                            if (j.getY() + 55 > v.y - 10) {
                                g2.clipRect((int)j.getX() - 50, 0, 150, v.y + 10);
                                mascaraAplicada = true;
                            }
                            break;
                        }
                    }
                }
                
                j.dibujar(g2, this); 
                
                if (mascaraAplicada) g2.setClip(clipOriginal);
            }
            
            // 3. CIRCULO DE MUERTE (Radar del impostor)
            if (botonKill != null && botonKill.isEnabled() && idVictimaCercana != -1) {
                if (jugadoresConectados.containsKey(idVictimaCercana)) {
                    Jugador victima = jugadoresConectados.get(idVictimaCercana);
                    g2.setColor(Color.RED);
                    g2.setStroke(new java.awt.BasicStroke(3));
                    g2.drawOval((int)victima.getX() - 5, (int)victima.getY() - 5, 60, 60);
                    
                    if (jugadoresConectados.containsKey(miId)) {
                        Jugador yo = jugadoresConectados.get(miId);
                        g2.drawLine((int)yo.getX()+25, (int)yo.getY()+25, (int)victima.getX()+25, (int)victima.getY()+25);
                    }
                    g2.drawString(" KILL ID " + idVictimaCercana, (int)victima.getX(), (int)victima.getY() - 10);
                }
            }

            // RESTAURAR CÁMARA ORIGINAL PARA LA INTERFAZ
            g2.setTransform(camaraOriginal);
            
            // 4. INTERFAZ DE ESPECTADOR
            if (modoEspectador && idEspectando != -1) {
                if (jugadoresConectados.containsKey(idEspectando)) {
                    Jugador objetivo = jugadoresConectados.get(idEspectando);
                    g2.setColor(Color.BLACK);
                    g2.fillRect(250, 10, 300, 40); 
                    g2.setColor(Color.WHITE);
                    g2.drawRect(250, 10, 300, 40); 
                    g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
                    g2.drawString("ESPECTANDO A: " + objetivo.getNombre(), 280, 35);
                }
            }
            
            // 5. PANTALLA REPORTE
            if (mostrandoAnimacionReporte) {
                g2.setColor(new Color(0, 0, 0, 150));
                g2.fillRect(0, 0, getWidth(), getHeight());
                int centroX = getWidth() / 2;
                int centroY = getHeight() / 2;

                if (imgReporteFondo != null && imgReporteTexto != null) {
                    g2.drawImage(imgReporteFondo, centroX - 400, centroY - 175, 800, 350, this);
                    g2.drawImage(imgReporteTexto, centroX - 150, centroY - 130, 300, 260, this);
                }
            }
            
            // 6. TABLET DE REUNIÓN (UI Superpuesta)
            if (mostrandoTabletReunion && !mostrandoAnimacionReporte) {
                g2.setColor(new Color(0, 0, 0, 180));
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                if (imgTabletFondo != null) {
                    // 1. DIBUJAR LA TABLET FÍSICA
                    int tabletW = 750; 
                    int tabletH = 550; 
                    int tabletX = (getWidth() - tabletW) / 2;
                    int tabletY = (getHeight() - tabletH) / 2;
                    
                    g2.drawImage(imgTabletFondo, tabletX, tabletY, tabletW, tabletH, this);
                    
                    // 2. DEFINIR EL "ÁREA DE PANTALLA CELESTE" 
                    int padding = 45;
                    int screenX = tabletX + padding;
                    int screenY = tabletY + padding;
                    int screenW = tabletW - (padding * 2);
                    int screenH = tabletH - (padding * 2);
                    
                    // 3. TEXTOS GLOBALES Y SKIP VOTE
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("Arial", Font.BOLD, 26));
                    g2.drawString("Who Is The Impostor?", screenX + (screenW/2) - 135, screenY + 35);
                    
                    // Botón Skip Vote (Abajo a la izquierda)
                    if (imgVoteSkip != null) {
                        g2.drawImage(imgVoteSkip, screenX + 10, screenY + screenH - 35, 110, 25, null);
                    }
                    
                    // Reloj (A la derecha del Skip Vote)
                    if (tiempoRestanteReunion <= 10) g2.setColor(Color.RED);
                    else g2.setColor(Color.DARK_GRAY);
                    g2.setFont(new Font("Arial", Font.BOLD, 16));
                    g2.drawString("Voting Ends In: " + tiempoRestanteReunion + "s", screenX + 130, screenY + screenH - 18);
                    
                    // 4. CUADRÍCULA DE JUGADORES
                    int boxW = 280;             
                    int boxH = 45;              
                    int espaciadoColumnas = 20; 
                    int espaciadoFilas = 55;    
                    
                    int startX = screenX + (screenW - (boxW * 2 + espaciadoColumnas)) / 2;  
                    int startY = screenY + 70;  
                    
                    int col = 0;
                    int row = 0;
                    
                    for (Jugador j : jugadoresConectados.values()) {
                        int drawX = startX + (col * (boxW + espaciadoColumnas));
                        int drawY = startY + (row * espaciadoFilas);
                        
                        // Color de fondo
                        if (!j.isVivo()) g2.setColor(new Color(150, 150, 150, 180)); 
                        else g2.setColor(Color.WHITE); 
                        
                        g2.fillRoundRect(drawX, drawY, boxW, boxH, 10, 10);
                        g2.setColor(Color.GRAY);
                        g2.drawRoundRect(drawX, drawY, boxW, boxH, 10, 10);
                        
                        // --- DIBUJAR AL TRIPULANTITO YA PINTADO ---
                        if (iconosPintadosTablet.containsKey(j.getId())) {
                            // Sacamos su foto personal pintada de la memoria
                            java.awt.image.BufferedImage suFoto = iconosPintadosTablet.get(j.getId());
                            g2.drawImage(suFoto, drawX + 8, drawY + 5, 45, 35, null);
                        }
                        
                        // --- DIBUJAR MEGÁFONO AL QUE REPORTÓ ---
                        if (j.getId() == idReportadorActual && imgVoteMegafono != null) {
                            // Lo centramos mejor respecto a la caja
                            g2.drawImage(imgVoteMegafono, drawX - 25, drawY + 10, 30, 28, null);
                        }
                        
                        // Nombre
                        g2.setColor(Color.BLACK);
                        g2.setFont(new Font("Arial", Font.BOLD, 18));
                        g2.drawString(j.getNombre(), drawX + 55, drawY + 28);
                        
                        // --- ESTADO SELECCIONADO: MOSTRAR BOTONES ---
                        // Si hice clic en este jugador y aún no he votado oficialmente...
                        if (idVotadoSeleccionado == j.getId() && !yaVote && j.isVivo()) {
                            
                            // Ponemos un recuadro verde alrededor de la caja para resaltar
                            g2.setColor(new Color(50, 200, 50));
                            g2.setStroke(new java.awt.BasicStroke(3));
                            g2.drawRoundRect(drawX, drawY, boxW, boxH, 10, 10);
                            
                            // Dibujamos el Confirmar y Cancelar a la derecha de la caja
                            if (imgVoteConfirmar != null) {
                                g2.drawImage(imgVoteConfirmar, drawX + boxW - 100, drawY + 3, 40, 40, null);
                            }
                            if (imgVoteCancelar != null) {
                                g2.drawImage(imgVoteCancelar, drawX + boxW - 50, drawY + 3, 40, 40, null);
                            }
                        }
                        
                        // Si ESTE jugador ya votó (sea yo o sea otro por red), dibujamos su estampilla
                        if (j.isHaVotado() && imgVoteIVoted != null) {
                            g2.drawImage(imgVoteIVoted, drawX - 15, drawY + 5, 35, 35, null);
                        }
                        
                        // Tachado rojo
                        if (!j.isVivo()) {
                            g2.setColor(new Color(255, 0, 0, 150)); 
                            g2.setStroke(new java.awt.BasicStroke(4));
                            g2.drawLine(drawX + 10, drawY + 10, drawX + boxW - 10, drawY + boxH - 10);
                            g2.drawLine(drawX + boxW - 10, drawY + 10, drawX + 10, drawY + boxH - 10);
                        }
                        
                        col++;
                        if (col > 1) { 
                            col = 0; row++;
                        }
                    }
                }
            }
            
            // 7. ANIMACIÓN DE EXPULSIÓN (Capa Superior Máxima)
            if (mostrandoAnimacionExpulsion) {
                // Fondo totalmente negro
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                // Las estrellas de fondo
                if (imgFondoEjected != null) {
                    g2.drawImage(imgFondoEjected, 0, 0, getWidth(), getHeight(), this);
                }
                
                // El personaje flotando y girando (Si alguien fue expulsado)
                if (imgCharEjectedPintado != null) {
                    java.awt.geom.AffineTransform old = g2.getTransform();
                    // Movemos el eje al centro del muñeco
                    g2.translate(expulsionX, getHeight() / 2.0);
                    // Lo giramos
                    g2.rotate(expulsionAngulo);
                    // Lo dibujamos
                    g2.drawImage(imgCharEjectedPintado, -45, -65, 90, 130, this);
                    // Restauramos la cámara
                    g2.setTransform(old);
                }
                
                // Texto de máquina de escribir centrado
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 24));
                String textoMostrar = textoExpulsion.substring(0, charsMostradosExpulsion);
                g2.drawString(textoMostrar, (getWidth() / 2) - 150, (getHeight() / 2) + 120);
            }
            
            // 8. ANIMACIÓN DE OSCURECIMIENTO (Fondo Negro)
            // Lo dibujamos si se está animando o si el juego ya terminó (como fondo)
            if (animandoFinDeJuego || juegoTerminado) {
                java.awt.Composite originalComposite = g2.getComposite();
                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, opacidadFinJuego));
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setComposite(originalComposite);
            }

            // 9. PANTALLA DE FIN DE JUEGO (Imagen, Personajes y Texto con Fade-In)
            if (juegoTerminado) {
                java.awt.Composite originalComposite = g2.getComposite();
                // Aplicamos la transparencia para que todo aparezca suavemente
                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, opacidadImagenFin));
                
                // --- CAPA 1: DIBUJAR EL FONDO (Win o Defeat) ---
                if (victoriaLocal && imgVictoriaFondo != null) {
                    g2.drawImage(imgVictoriaFondo, 0, 0, getWidth(), getHeight(), this);
                } else if (!victoriaLocal && imgDerrotaFondo != null) {
                    g2.drawImage(imgDerrotaFondo, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g2.setColor(Color.BLACK);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }

                // --- CAPA 2: DIBUJAR A LOS GANADORES EN EL CENTRO ---
                int spriteW = 100; // Ancho del personaje en pantalla
                int spriteH = 120; // Alto
                int separacion = 30; // Espacio entre cada jugador
                
                int totalW = (spritesGanadores.size() * spriteW) + ((spritesGanadores.size() - 1) * separacion);
                int startX = (getWidth() - totalW) / 2;
                int startY = (getHeight() - spriteH) / 2; // Centro vertical

                for (int i = 0; i < spritesGanadores.size(); i++) {
                    int drawX = startX + (i * (spriteW + separacion));
                    
                    // Dibujamos el personaje
                    g2.drawImage(spritesGanadores.get(i), drawX, startY, spriteW, spriteH, null);
                    
                    // Dibujamos su nombre arriba de la cabeza
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Arial", Font.BOLD, 18));
                    String nom = nombresGanadores.get(i);
                    int nomW = g2.getFontMetrics().stringWidth(nom);
                    g2.drawString(nom, drawX + (spriteW - nomW) / 2, startY - 15);
                }
                
                // --- CAPA 3: DIBUJAR EL TEXTO DE QUIÉN GANÓ ---
                if (textoGanadores.contains("IMPOSTORES")) g2.setColor(new Color(255, 50, 50)); // Rojo
                else g2.setColor(new Color(100, 200, 255)); // Celeste
                
                g2.setFont(new Font("Arial", Font.BOLD, 36));
                int anchoTexto = g2.getFontMetrics().stringWidth(textoGanadores);
                g2.drawString(textoGanadores, (getWidth() - anchoTexto) / 2, getHeight() - 80); 
                
                // --- RESTAURAR EL PINCEL ORIGINAL ---
                g2.setComposite(originalComposite);
            }
            
            // --- 10. DIBUJAR BARRA DE TAREAS ---
            int barraX = 20;
            int barraY = 20;
            int barraAnchoTotal = 300;
            int barraAlto = 25;
            
            // Fondo gris
            g2.setColor(new Color(50, 50, 50, 200));
            g2.fillRect(barraX, barraY, barraAnchoTotal, barraAlto);
            
            // Relleno verde usando el Gestor
            int rellenoVerde = (int)(barraAnchoTotal * gestorTareas.obtenerPorcentajeProgreso());
            g2.setColor(new Color(50, 255, 50)); 
            g2.fillRect(barraX, barraY, rellenoVerde, barraAlto);
            
            // Borde blanco
            g2.setColor(Color.WHITE);
            g2.setStroke(new java.awt.BasicStroke(3));
            g2.drawRect(barraX, barraY, barraAnchoTotal, barraAlto);
            
            // --- PANTALLA DE LOBBY ---
            if (!juegoIniciado) {
                // Fondo oscuro transparente
                g2.setColor(new Color(0, 0, 0, 180));
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                // Textos
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 36));
                g2.drawString("SALA DE ESPERA", 250, 100);
                
                g2.setFont(new Font("Arial", Font.PLAIN, 20));
                int totalActual = jugadoresConectados.size();
                g2.drawString("Jugadores conectados: " + totalActual + " / " + MIN_JUGADORES, 270, 150);
                
                // Lista de nombres
                int yNombre = 200;
                for (Jugador j : jugadoresConectados.values()) {
                    g2.drawString("- " + j.getNombre(), 300, yNombre);
                    yNombre += 30;
                }
                
                // Lógica del botón para el Host
                if (soyHost && botonIniciarPartida != null) {
                    if (totalActual >= MIN_JUGADORES) {
                        botonIniciarPartida.setText("¡INICIAR PARTIDA!");
                        botonIniciarPartida.setEnabled(true);
                        botonIniciarPartida.setBackground(new Color(50, 200, 50));
                    } else {
                        botonIniciarPartida.setText("Faltan jugadores...");
                        botonIniciarPartida.setEnabled(false);
                        botonIniciarPartida.setBackground(Color.GRAY);
                    }
                } else if (!soyHost) {
                    g2.setColor(Color.YELLOW);
                    g2.drawString("Esperando a que el Host inicie...", 250, 480);
                }
            }
            
            java.awt.Toolkit.getDefaultToolkit().sync();
            
        } catch (Exception ex) {
            // ¡PANTALLA ROJA DE DIAGNÓSTICO!
            g.setColor(Color.RED);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
            g.drawString("💥 CRASH GRÁFICO: " + ex.toString(), 20, 30);
            StackTraceElement[] trace = ex.getStackTrace();
            for (int i = 0; i < Math.min(20, trace.length); i++) {
                g.drawString(trace[i].toString(), 20, 50 + (i * 15));
            }
        }
    }

    // --- CICLO DEL JUEGO (Lo que hace el reloj cada 15ms) ---
   @Override
    public void actionPerformed(ActionEvent e) {
        // VALIDACIÓN DE SEGURIDAD
        // Si el juego no ha iniciado, ya terminó, o se está oscureciendo la pantalla... ¡NADIE SE MUEVE!
        if (!juegoIniciado || juegoTerminado || animandoFinDeJuego) {
            return; 
        }
        
        if (!jugadoresConectados.containsKey(miId)) return;
        
        Jugador miMuñeco = jugadoresConectados.get(miId);
        
        // --- 1. LÓGICA DE MOVIMIENTO (Calculada por el Panel) ---
        
        // Definimos la velocidad (Píxeles por frame)
        // IMPORTANTE: Si no ponemos velocidad, se moverá a 1 píxel por hora.
        double velocidad = 4.0; 
        
        // Si hay animación, ESTÁ LA TABLET ABIERTA, o estoy muerto, no me muevo
        if (mostrandoAnimacionReporte || mostrandoTabletReunion || (modoEspectador && !miMuñeco.isVivo())) {
            velocidad = 0;
        }

        double dx = 0;
        double dy = 0;
        
        

        if (izquierda) dx = -1;
        if (derecha)   dx = 1;
        if (arriba)    dy = -1;
        if (abajo)     dy = 1;

        // CORRECCIÓN DE DIAGONAL (Normalización)
        if (dx != 0 && dy != 0) {
            dx *= 0.7071; 
            dy *= 0.7071;
        }
        
        // APLICAR VELOCIDAD
        dx *= velocidad;
        dy *= velocidad;

        // ¿Me estoy moviendo ahora mismo?
        boolean meMuevoAhora = (dx != 0 || dy != 0);
        
        // Si estoy animándome o escondido, no me puedo mover con las teclas
        if (enVentilacion || animandoVent) {
            meMuevoAhora = false;
            dx = 0; 
            dy = 0;
            botonVent.setEnabled(true); 
            botonUse.setEnabled(false);
        }

        if (meMuevoAhora) {
            double nuevaX = miMuñeco.getX() + dx;
            double nuevaY = miMuñeco.getY() + dy;

            boolean meMoviRealmente = false;

            // Revisamos X e Y por separado (Esto permite "resbalar" en las paredes)
            if (esPasoValido(nuevaX, miMuñeco.getY())) {
                miMuñeco.setX(nuevaX);
                meMoviRealmente = true;
            }
            if (esPasoValido(miMuñeco.getX(), nuevaY)) {
                miMuñeco.setY(nuevaY);
                meMoviRealmente = true;
            }
            
            if (meMoviRealmente) {
                miMuñeco.setMoviendose(true);
                if (dx > 0) miMuñeco.setMirandoDerecha(true);
                if (dx < 0) miMuñeco.setMirandoDerecha(false);
                
                String mensaje = "MOV," + miId + "," + (int)miMuñeco.getX() + "," + (int)miMuñeco.getY() 
                                 + "," + miMuñeco.isMirandoDerecha() + ",true";
                clienteRed.enviar(mensaje);
                estabaMoviendose = true; 
            }
            
            contadorPasos++;
    if (contadorPasos >= 20) { // Suena un paso cada 300ms aprox
        // Aquí podrías detectar el color del suelo para elegir carpeta, 
        // por ahora usemos Tile como base:
        GestorSonido.jugar("Footsteps/Tile/FootstepTile.wav");
        contadorPasos = 0;
    }
        } else {
            // B) SI NO TOCO TECLAS:
            miMuñeco.setMoviendose(false);
            
            // C) ¡EL FRENO DE MANO!
            if (estabaMoviendose) {
                // Enviamos posición final y avisamos que paramos (false)
                String mensaje = "MOV," + miId + "," + (int)miMuñeco.getX() + "," + (int)miMuñeco.getY()
                                 + "," + miMuñeco.isMirandoDerecha() + ",false";
                clienteRed.enviar(mensaje);
                estabaMoviendose = false; 
            }
        }
        
        
        // ==========================================================
        //     DE AQUÍ PARA ABAJO ES TU CÓDIGO DE RADARES EXACTO
        //           (NO HE TOCADO NADA DE ESTA PARTE)
        // ==========================================================
        
        // --- NUEVO: RADAR DE ASESINO ---
        if (botonKill.isVisible()) { 
            Jugador yo = jugadoresConectados.get(miId);
            if (yo == null) return; 

            double miCentroX = yo.getX() + 25; 
            double miCentroY = yo.getY() + 25;

            double distanciaMinima = 10000;
            Jugador victimaPotencial = null;
            
            for (Jugador otro : jugadoresConectados.values()) {
                if (otro.getId() == miId) continue; 
                if (!otro.isVivo()) continue;    
                if (otro.esImpostor()) continue; 

                double otroCentroX = otro.getX() + 25;
                double otroCentroY = otro.getY() + 25;
                
                double distancia = Math.hypot(otroCentroX - miCentroX, otroCentroY - miCentroY);
                
                if (distancia < 10) continue; 

                if (distancia < 60) { 
                    if (distancia < distanciaMinima) {
                        distanciaMinima = distancia;
                        victimaPotencial = otro;
                    }
                }
            }
            
            if (victimaPotencial != null) {
                if (idVictimaCercana != victimaPotencial.getId()) {
                    idVictimaCercana = victimaPotencial.getId();
                    botonKill.setEnabled(true);
                    System.out.println("🎯 MIRA FIJADA EN: Jugador " + idVictimaCercana);
                }
            } else {
                if (botonKill.isEnabled()) {
                    botonKill.setEnabled(false);
                    idVictimaCercana = -1;
                    System.out.println("❌ Nadie cerca");
                }
            }
        }
        // --- RADAR ÚNICO DE INTERACCIONES (VENTS Y MISIONES) ---
        if (enVentilacion || animandoVent) {
            // 1. Si estamos bajo tierra, se enciende y el radar IGNORA el resto del código
            botonVent.setEnabled(true); 
            botonUse.setEnabled(false);
        } 
        else if (mapaDatos != null && this.miJugador != null) {
            // 1. PUNTO BASE (Centro de los pies)
            int pieX = (int) miJugador.getX() + 25;
            int pieY = (int) miJugador.getY() + 55;
            
            boolean tocandoRojo = false;
            boolean tocandoVerde = false;

            // 2. RADAR DE MALLA (Revisamos un área pequeña de 10x10 píxeles)
            // Esto hace que si CUALQUIER parte de tus pies toca el rojo, se active.
            for (int x = pieX - 5; x <= pieX + 5; x += 5) {
                for (int y = pieY - 5; y <= pieY + 5; y += 5) {
                    // Validar que no nos salgamos de los bordes de la imagen
                    if (x >= 0 && x < mapaDatos.getWidth() && y >= 0 && y < mapaDatos.getHeight()) {
                        Color c = new Color(mapaDatos.getRGB(x, y), true);
                        
                        // Detección con tolerancia
                        if (c.getRed() > 150 && c.getGreen() < 120 && c.getBlue() < 120) tocandoRojo = true;
                        if (c.getGreen() > 150 && c.getRed() < 120 && c.getBlue() < 120) tocandoVerde = true;
                    }
                }
            }

            // 3. LÓGICA DE BOTONES
            if (tocandoRojo) {
                if (this.miJugador.getEsImpostor()) {
                    botonVent.setEnabled(true);
                    
                    double distMin = Double.MAX_VALUE;
                    for (int i = 0; i < listaVents.size(); i++) {
                        double d = listaVents.get(i).distance(pieX, pieY);
                        if (d < distMin) {
                            distMin = d;
                            idVentCercana = i;
                        }
                    }
                }
                botonUse.setEnabled(false);
            } 
            else if (tocandoVerde) {
                if (!this.miJugador.getEsImpostor()) {
                    // Preguntamos al Gestor si hay una tarea AQUÍ que NO esté completada
                    tareaActualEnZona = gestorTareas.obtenerTareaEnZona(pieX, pieY);
                    
                    if (tareaActualEnZona != null) {
                        botonUse.setEnabled(true); // ¡Se ilumina!
                    } else {
                        botonUse.setEnabled(false); // Se pone transparente (ya la hicimos)
                    }
                }
                botonVent.setEnabled(false);
            } 
            else {
                botonVent.setEnabled(false);
                botonUse.setEnabled(false); // Lejos de cualquier zona, siempre transparente
                tareaActualEnZona = null; 
            }
        }
        
        
        // --- RADAR DE REPORT ---
        if (jugadoresConectados.containsKey(miId) && jugadoresConectados.get(miId).isVivo()) {
            
            Jugador yo = jugadoresConectados.get(miId);
            double distanciaMinima = 10000;
            Jugador cuerpoEncontrado = null;
            
            for (Jugador otro : jugadoresConectados.values()) {
                // CONDICIÓN: Muerto y NO reportado (Fantasma)
                if (otro.getId() != miId && !otro.isVivo() && !otro.isCuerpoReportado()) {

                double distancia = Math.hypot(otro.getX() - yo.getX(), otro.getY() - yo.getY());

                if (distancia < 60) { 
                    distanciaMinima = distancia;
                    cuerpoEncontrado = otro;
                }
            }
            }
            
            if (cuerpoEncontrado != null) {
                botonReport.setVisible(true);
                botonReport.setEnabled(true);
                idCuerpoCercano = cuerpoEncontrado.getId();
            } else {
                botonReport.setVisible(false);
                botonReport.setEnabled(false);
                idCuerpoCercano = -1;
            }
        }
        // --- ACTUALIZAR ANIMACIONES DE TODOS LOS JUGADORES ---
        // Esto es vital para ver mover los pies a los demas (y a ti mismo)
        for (Jugador j : jugadoresConectados.values()) {
            j.actualizarAnimacion();
        }
        repaint();
    }

    // --- TECLADO (Solo enciende/apaga interruptores) ---
    @Override
    public void keyPressed(KeyEvent e) {
        
        // SI ESTOY MUERTO, NO PUEDO MOVERME
        if (modoEspectador) return; 

        int tecla = e.getKeyCode();
        
        if (tecla == KeyEvent.VK_RIGHT) derecha = true;
        if (tecla == KeyEvent.VK_LEFT)  izquierda = true;
        if (tecla == KeyEvent.VK_UP)    arriba = true;
        if (tecla == KeyEvent.VK_DOWN)  abajo = true;
        
        if (tecla == KeyEvent.VK_SPACE) miJugador.setVivo(!miJugador.isVivo());
        
        
        
    }

    @Override 
    public void keyReleased(KeyEvent e) {
        int tecla = e.getKeyCode();
        
        if (tecla == KeyEvent.VK_RIGHT) derecha = false;
        if (tecla == KeyEvent.VK_LEFT)  izquierda = false;
        if (tecla == KeyEvent.VK_UP)    arriba = false;
        if (tecla == KeyEvent.VK_DOWN)  abajo = false;
    }
    
    @Override public void keyTyped(KeyEvent e) {}
    
    
    
    
   // Sincronizar estado de jugadores remotos basado en datos del servidor
    public void actualizarJugadorRemoto(int id, double x, double y) {
        // Ignorar actualizaciones de mi propio jugador
        if (id == miId) return;

        if (jugadoresConectados.containsKey(id)) {
            Jugador j = jugadoresConectados.get(id);
            double dx = x - j.getX(); 
            double dy = y - j.getY();
            
            // Calculamos la distancia real (Pitágoras)
            double distancia = Math.hypot(dx, dy);

            // --- CASO 1: TELETRANSPORTE (VENT) ---
            // Si la distancia es enorme (más de 100 px), es un salto de vent.
            // Lo movemos de golpe y FORZAMOS QUE ESTÉ QUIETO.
            if (distancia > 100) {
                j.setX(x);
                j.setY(y);
                j.detener(); // <--- Apaga la animación.
            }
            
            // --- CASO 2: ESTÁ PARADO (Corrección de lag) ---
            // Si la distancia es muy pequeña, lo pegamos a la coordenada y frenamos.
            else if (distancia < 5) {
                j.detener();
                j.setX(x); 
                j.setY(y);
            } 
            
            // --- CASO 3: CAMINANDO NORMAL ---
            // Si es una distancia media, usamos la interpolación suave.
            else {
                // Calculamos el pasito suave
                double pasoX = dx / 5.0;
                double pasoY = dy / 5.0;

                // Aplicamos la nueva posición directamente
                j.setX(j.getX() + pasoX);
                j.setY(j.getY() + pasoY);
            }
            
        } else {
            // Registrar nuevo jugador si no existía
            Jugador nuevo = new Jugador(id, "Jugador " + id, x, y, false);
            jugadoresConectados.put(id, nuevo);
        }
        repaint();
    }
       // Este se ejecuta cuando llega el mensaje "BIENVENIDO" del servidor
    public void inicializarJugadorLocal(int id, boolean esImpostor, double xDelServidor, double yDelServidor) {
        this.miId = id;
        
        // 1. Usar el nombre que escribiste en el menú (en vez del genérico "Jugador X")
        String nombreFinal = this.miNombreElegido;
        if (esImpostor) nombreFinal += " (IMPOSTOR)";
        
        double miX = spawnX - 25;
        double miY = spawnY - 55;
        
        this.miJugador = new Jugador(id, nombreFinal, miX, miY, esImpostor);
        
        // 2. ¡APLICAR EL COLOR AL MUÑECO!
        if (this.miColorElegido != null) {
            this.miJugador.setColorRGB(this.miColorElegido.getRed(), this.miColorElegido.getGreen(), this.miColorElegido.getBlue());
        }
        
        jugadoresConectados.put(this.miId, this.miJugador);
        
        System.out.println("✅ Jugador Local Creado -> ID: " + id + " | Nombre: " + nombreFinal);
        
        if (esImpostor) {
            botonKill.setVisible(true);
            botonVent.setVisible(true);
        }
    }
    public String getMiNombreElegido() {
        return miNombreElegido;
    }

    public Color getMiColorElegido() {
        return miColorElegido;
    }
    
    public void reportarMuerte(int idMuerto) {
        System.out.println("--- INTENTANDO MATAR A ID: " + idMuerto + " ---");
        boolean encontrado = false;

        // BÚSQUEDA MANUAL: Revisamos todos los muñecos uno por uno
        for (Jugador j : jugadoresConectados.values()) {
            if (j.getId() == idMuerto) {
                // ¡LO ENCONTRAMOS!
                j.setVivo(false); 
                j.detener(); // Detenerlo para que no sea un cadáver que camina
                
                System.out.println("¡MUERTE CONFIRMADA! Jugador " + idMuerto + " ahora es un cadáver.");
                encontrado = true;
                break; // Ya lo matamos, dejamos de buscar
            }
        }

        if (!encontrado) {
            System.out.println("ERROR: Se ordenó matar al ID " + idMuerto + " pero no lo encuentro en mi lista.");
            System.out.println("Jugadores visibles: " + jugadoresConectados.keySet());
        }
        
        if (idMuerto == miId) {
            System.out.println("💀 ME HAN MATADO. ACTIVANDO MODO ESPECTADOR.");
            modoEspectador = true;
            idEspectando = -1; // Reset para que busque uno nuevo al hacer click o forzarlo
            cambiarObjetivoEspectador(true); // Auto-asignar el primero que encuentre
            
            // Apagar botones de interacción física
            botonKill.setVisible(false);
            botonVent.setVisible(false);
            botonReport.setVisible(false);
        }
        repaint();
        verificarFinDeJuego();
    }
        
    
    public void eliminarJugador(int id) {
    if (jugadoresConectados.containsKey(id)) {
        jugadoresConectados.remove(id);
        repaint(); // Redibujar para que desaparezca
        
        verificarFinDeJuego();
    }
}
    
    
    @Override
    public void focusGained(FocusEvent e) {
        // Cuando vuelves a hacer clic en la ventana. 
        // No hace falta hacer nada especial aquí.
    }

    // CORRECCIÓN EN focusLost
@Override
public void focusLost(FocusEvent e) {
    derecha = izquierda = arriba = abajo = false;
    if (jugadoresConectados.containsKey(miId)) {
        Jugador miMuñeco = jugadoresConectados.get(miId);
        miMuñeco.detener();
        // Agregamos las partes faltantes (mirandoDerecha y moviendose=false)
        String mensaje = "MOV," + miId + "," + (int)miMuñeco.getX() + "," + (int)miMuñeco.getY() 
                         + "," + miMuñeco.isMirandoDerecha() + ",false";
        clienteRed.enviar(mensaje);
    }
    repaint();
}
    // Método que llama ClienteRed cuando llega un aviso de color
    public void actualizarColorJugador(int id, int r, int g, int b) {
        if (jugadoresConectados.containsKey(id)) {
            Jugador j = jugadoresConectados.get(id);
            Color nuevoColor = new Color(r, g, b);
            
            j.cambiarSkin(nuevoColor); // ¡Esto pintará las 12 imágenes de la animación!
            repaint();
        }
    }
    
   
    
    public void actualizarRolJugador(int id, boolean esImpostor) {
        // Si el jugador ya existe, le actualizamos el rol
        if (jugadoresConectados.containsKey(id)) {
            Jugador j = jugadoresConectados.get(id);
            j.setImpostor(esImpostor);
            
            if (esImpostor && jugadoresConectados.get(miId).esImpostor()) {
                 System.out.println("¡Detectado aliado Impostor ID " + id + "!");
            }
        } else {
            // Si el mensaje ROL llega antes que el MOV (raro, pero posible),
            // creamos un muñeco temporal para guardar el dato.
            Jugador nuevo = new Jugador(id, "Jugador " + id, 0, 0, esImpostor);
            jugadoresConectados.put(id, nuevo);
        }
    }
    public void iniciarReunion(int idReportador) {
        System.out.println("🚨 ANIMACIÓN DE REPORTE INICIADA 🚨");
        GestorSonido.jugar("general_sounds/report_Bobbyfound.wav");
        
        // GUARDAMOS QUIÉN FUE EL HÉROE (O el impostor fingiendo)
        this.idReportadorActual = idReportador;
        
        // 1. ACTIVAR ANIMACIÓN VISUAL
        mostrandoAnimacionReporte = true;
        
        // 2. APAGAR CONTROLES (Para que nadie se mueva durante la alerta)
        botonKill.setEnabled(false);
        botonReport.setVisible(false);
        botonReport.setEnabled(false);
        
        // Forzamos que se dibuje la pantalla roja INMEDIATAMENTE
        repaint();
        
        // --- AQUÍ ESTÁ LA CLAVE: USAMOS UN TIMER, NO UN JOPTIONPANE ---
        // Esto espera 3000 milisegundos (3 segundos) sin congelar la ventana
        Timer timerAnimacion = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                // --- ESTO OCURRE DESPUÉS DE LOS 3 SEGUNDOS ---
                
                // 1. Quitar la pantalla roja
                mostrandoAnimacionReporte = false;
                
                // 2. Limpiar cadáveres (Convertirlos en fantasmas transparentes)
                for (Jugador j : jugadoresConectados.values()) {
                    if (!j.isVivo()) {
                        j.setCuerpoReportado(true);
                    }
                }
                
                // 3. Teletransportar A LA ZONA CELESTE (Mesa de Reunión)
                if (jugadoresConectados.containsKey(miId)) {
                    Jugador yo = jugadoresConectados.get(miId);
                    
                    if (yo.isVivo()) {
                        // Usamos las coordenadas del spawn (zona celeste) detectadas en cargarMapa
                        // Le sumamos un offset aleatorio (entre -40 y 40) para esparcirlos alrededor de la mesa
                        int offsetX = (int)(Math.random() * 80) - 40;
                        int offsetY = (int)(Math.random() * 80) - 40;
                        
                        int nuevaX = spawnX + offsetX; 
                        int nuevaY = spawnY + offsetY;
                        
                        yo.setX(nuevaX);
                        yo.setY(nuevaY);
                        yo.detener();
                        
                        // Avisar al servidor de nuestra nueva posición
                        clienteRed.enviar("MOV," + miId + "," + nuevaX + "," + nuevaY + "," + yo.isMirandoDerecha() + ",false");
                    }
                }
                
                // 4. Resetear variables de juego
                idCuerpoCercano = -1;
                
                // Recuperar el foco para poder escribir en el chat (cuando lo hagamos)
                PanelJuego.this.requestFocusInWindow();
                
                // DETENER EL TIMER (Para que no se repita infinitamente)
                ((Timer)e.getSource()).stop(); 
                
                botonAbrirChat.setVisible(true);
            }
        });
        
        timerAnimacion.setRepeats(false); // Aseguramos que solo suene una vez
        timerAnimacion.start(); // ¡CORRE TIEMPO!
        abrirTabletReunion();
        
    }
    
    public void abrirTabletReunion() {
        mostrandoTabletReunion = true;
        tiempoRestanteReunion = 60; // 120 segundos
        yaVote = false;
        idVotadoSeleccionado = -1;
        
        // Limpiamos el estado de votación de TODOS los jugadores
        for (Jugador j : jugadoresConectados.values()) {
            j.setHaVotado(false);
        }
        
        // Bloquear movimiento
        arriba = abajo = izquierda = derecha = false;
        
        // Apagar botones de acción
        botonKill.setVisible(false);
        botonVent.setVisible(false);
        botonReport.setVisible(false);
        botonUse.setVisible(false);
        
        // Iniciar el reloj
        if (timerReunion != null && timerReunion.isRunning()) timerReunion.stop();
        
        timerReunion = new Timer(1000, e -> { 
            tiempoRestanteReunion--;
            if (tiempoRestanteReunion <= 0) cerrarTabletReunion(); 
            repaint();
        });
        timerReunion.start();
        
        // --- NUEVO: PINTAR LOS ICONOS DE TODOS ---
        iconosPintadosTablet.clear(); // Limpiamos las fotos de la reunión anterior
        for (Jugador j : jugadoresConectados.values()) {
            if (imgVoteTripulante != null && j.getColor() != null) {
                // Usamos tu super herramienta de color para pintar la cabecita
                java.awt.image.BufferedImage iconoColor = starina_among_us.modelo.HerramientasColor.crearPersonaje(imgVoteTripulante, j.getColor());
                iconosPintadosTablet.put(j.getId(), iconoColor); // Lo guardamos en el caché
            }
        }
        
        
        areaChat.setText(""); // Limpiamos el chat de la reunión anterior
        
        repaint();
    }
    
    public void cerrarTabletReunion() {
        mostrandoTabletReunion = false;
        if (timerReunion != null) timerReunion.stop();
        
        // --- 1. CONTEO DE VOTOS ---
        java.util.HashMap<Integer, Integer> conteo = new java.util.HashMap<>();
        for (Integer voto : registroVotos.values()) {
            conteo.put(voto, conteo.getOrDefault(voto, 0) + 1);
        }
        
        int idMasVotado = -1;
        int maxVotos = 0;
        boolean empate = false;
        
        botonAbrirChat.setVisible(false);
        scrollChat.setVisible(false);
        campoChat.setVisible(false);
        chatAbierto = false;
        
        for (java.util.Map.Entry<Integer, Integer> entry : conteo.entrySet()) {
            if (entry.getValue() > maxVotos) {
                maxVotos = entry.getValue();
                idMasVotado = entry.getKey();
                empate = false;
            } else if (entry.getValue() == maxVotos) {
                empate = true;
            }
        }
        
        // --- 2. DECIDIR QUÉ PASA ---
        if (empate || idMasVotado == -2 || idMasVotado == -1) {
            iniciarAnimacionExpulsion(-1); // Empate o Skip
        } else {
            iniciarAnimacionExpulsion(idMasVotado); // Alguien fue expulsado
        }
    }

    public void iniciarAnimacionExpulsion(int idExpulsado) {
        this.idExpulsadoActual = idExpulsado;
        mostrandoAnimacionExpulsion = true;
        expulsionX = -100; // Empieza fuera de la pantalla por la izquierda
        expulsionAngulo = 0;
        charsMostradosExpulsion = 0;
        
        if (idExpulsado >= 0 && jugadoresConectados.containsKey(idExpulsado)) {
            Jugador j = jugadoresConectados.get(idExpulsado);
            textoExpulsion = j.getNombre() + " fue expulsado.";
            // Pintamos el muñequito de expulsión con su color
            // NUEVO: Escudo protector contra NullPointerException
            if (imgCharEjectedOriginal != null) {
                imgCharEjectedPintado = starina_among_us.modelo.HerramientasColor.crearPersonaje(imgCharEjectedOriginal, j.getColor());
            } else {
                System.out.println("⚠️ AVISO: imgCharEjectedOriginal es nulo. Se mostrará una expulsión invisible.");
                imgCharEjectedPintado = null;
            }
            
            
        } else {
            textoExpulsion = "Nadie fue expulsado. (Empate / Skipped)";
            imgCharEjectedPintado = null; // No hay muñequito flotando
        }

        // Timer de la animación (Se mueve a 30 FPS)
        timerExpulsion = new Timer(30, e -> {
            expulsionX += 4.5; // Velocidad de flotación hacia la derecha
            expulsionAngulo += 0.05; // Velocidad de giro
            
            // Efecto de máquina de escribir (1 letra nueva cada 3 frames)
            if (charsMostradosExpulsion < textoExpulsion.length() && (int)expulsionX % 3 == 0) {
                charsMostradosExpulsion++;
            }
            
            // Cuando ya cruzó toda la pantalla, terminamos
            if (expulsionX > getWidth() + 200) {
                ((Timer)e.getSource()).stop();
                finalizarExpulsion();
            }
            repaint();
        });
        timerExpulsion.start();
    }
    
    public void finalizarExpulsion() {
        mostrandoAnimacionExpulsion = false;
        registroVotos.clear(); 
        
        // --- EJECUTAR LA EXPULSIÓN OFICIALMENTE AQUÍ ---
        if (idExpulsadoActual != -1) {
            
            // Si soy el Host, ahora sí les digo a todos por red que lo maten
            if (soyHost) {
                clienteRed.enviar("MATAR," + idExpulsadoActual);
            }
            
            // Lo matamos localmente y forzamos a que desaparezca (sin dejar cadáver)
            if (jugadoresConectados.containsKey(idExpulsadoActual)) {
                Jugador j = jugadoresConectados.get(idExpulsadoActual);
                j.setVivo(false);
                j.setCuerpoReportado(true); // Se vuelve invisible en el mapa
                j.detener();
            }
            
            // Si YO fui el expulsado, me convierto en espectador inmediatamente
            if (idExpulsadoActual == miId) {
                System.out.println("🌌 FUI EXPULSADO AL ESPACIO. MODO ESPECTADOR ON.");
                modoEspectador = true;
                idEspectando = -1; // Reset para buscar a alguien a quien espectear
                cambiarObjetivoEspectador(true); // Me ancla a un jugador vivo
                
                // Apago todos mis controles
                botonKill.setVisible(false);
                botonVent.setVisible(false);
                botonReport.setVisible(false);
                botonUse.setVisible(false);
            }
        }
        
        // Devolvemos los botones al jugador si sigue vivo
        Jugador yo = jugadoresConectados.get(miId);
        if (yo != null && yo.isVivo()) {
            if (yo.esImpostor()) {
                botonKill.setVisible(true);
                botonVent.setVisible(true);
            }
        }
        repaint();
        
        verificarFinDeJuego();
    }
    
    private void cambiarObjetivoEspectador(boolean avanzar) {
        // 1. Obtener lista de jugadores VIVOS (excluyéndome a mí si estoy muerto)
        java.util.ArrayList<Integer> vivos = new java.util.ArrayList<>();
        
        for (Jugador j : jugadoresConectados.values()) {
            if (j.isVivo()) {
                vivos.add(j.getId());
            }
        }
        
        // Si no queda nadie vivo (o solo 1), no hacemos nada
        if (vivos.isEmpty()) return;
        
        // Ordenamos la lista para que el cambio sea predecible (1 -> 2 -> 5...)
        java.util.Collections.sort(vivos);
        
        // 2. Buscar índice actual
        int indiceActual = vivos.indexOf(idEspectando);
        
        // 3. Calcular nuevo índice
        if (avanzar) {
            indiceActual++;
            if (indiceActual >= vivos.size()) indiceActual = 0; // Vuelta al principio
        } else {
            indiceActual--;
            if (indiceActual < 0) indiceActual = vivos.size() - 1; // Vuelta al final
        }
        
        // 4. Asignar nuevo objetivo
        idEspectando = vivos.get(indiceActual);
        System.out.println("Espectando ahora a: Jugador " + idEspectando);
        repaint();
    }
    
    // Método para permitir que ClienteRed acceda a un jugador específico
    public Jugador getJugador(int id) {
        return jugadoresConectados.get(id);
    }


public void agregarJugador(int id, String nombre, int x, int y, int r, int g, int b) {
    // CORRECCIÓN: El constructor de Jugador pide (id, nombre, x, y, esImpostor)
    // Pasamos 'false' inicialmente; el comando ROL lo actualizará después si es necesario.
    Jugador nuevo = new Jugador(id, nombre, x, y, false);
    
    // Aplicamos el color RGB
    nuevo.setColorRGB(r, g, b); 
    
    jugadoresConectados.put(id, nuevo);
    repaint();
}

    public void verificarFinDeJuego() {
        // Si ya terminó, no ha iniciado o ya estamos en la animación de fin, ignorar
        if (juegoTerminado || !juegoIniciado || animandoFinDeJuego) return;

        int vivosTripulantes = 0;
        int vivosImpostores = 0;

        // Contamos quién queda en pie
        for (Jugador j : jugadoresConectados.values()) {
            if (j.isVivo()) {
                if (j.getEsImpostor()) vivosImpostores++;
                else vivosTripulantes++;
            }
        }

        // SEGURO ANTI-BUGS: Si no queda absolutamente nadie vivo, ignoramos
        if (vivosTripulantes + vivosImpostores == 0) return;

        // --- NUEVAS REGLAS DE VICTORIA ---
        boolean gananImpostores = (vivosImpostores >= vivosTripulantes);
        // Victoria si no quedan impostores O si la barra de tareas está llena (100%)
        boolean gananTripulantesPorTareas = (gestorTareas.obtenerPorcentajeProgreso() >= 1.0f);
        boolean gananTripulantesPorExpulsion = (vivosImpostores == 0);
        
        boolean gananTripulantes = gananTripulantesPorExpulsion || gananTripulantesPorTareas;

        if (gananImpostores || gananTripulantes) {
            
            // 1. Decidir victoria local EXACTA y preparar el texto
            Jugador yo = jugadoresConectados.get(miId);
            if (yo != null) {
                if (yo.getEsImpostor() && gananImpostores) victoriaLocal = true;
                else if (!yo.getEsImpostor() && gananTripulantes) victoriaLocal = true;
                else victoriaLocal = false;
            }
            
            if (gananImpostores) textoGanadores = "Ganan los: IMPOSTORES";
            else textoGanadores = gananTripulantesPorTareas ? "¡TAREAS COMPLETADAS! Ganan Tripulantes" : "Ganan los: TRIPULANTES";
            
            // 2. Apagamos TODOS los controles para que ya nadie juegue
            arriba = abajo = izquierda = derecha = false;
            botonKill.setVisible(false);
            botonVent.setVisible(false);
            botonReport.setVisible(false);
            botonUse.setVisible(false);
            
            // --- NUEVO: PINTAR A LOS GANADORES ---
            spritesGanadores.clear();
            nombresGanadores.clear();
            
            for (Jugador j : jugadoresConectados.values()) {
                // Si ganaron los impostores y él es impostor, O si ganaron tripulantes y NO es impostor
                if ((gananImpostores && j.getEsImpostor()) || (gananTripulantes && !j.getEsImpostor())) {
                    if (imgBaseGanador != null && j.getColor() != null) {
                        java.awt.image.BufferedImage spritePintado = starina_among_us.modelo.HerramientasColor.crearPersonaje(imgBaseGanador, j.getColor());
                        spritesGanadores.add(spritePintado);
                        nombresGanadores.add(j.getNombre());
                    }
                }
            }
            
            // 3. ¡INICIAR LA TRANSICIÓN DE 2 FASES!
            animandoFinDeJuego = true;
            juegoTerminado = false; // Falso hasta que la pantalla esté negra
            opacidadFinJuego = 0.0f;
            opacidadImagenFin = 0.0f;
            
            timerFinDeJuego = new Timer(30, e -> {
                
                if (!juegoTerminado) {
                    // FASE 1: Oscurecer el mapa
                    opacidadFinJuego += 0.015f; 
                    if (opacidadFinJuego >= 1.0f) {
                        opacidadFinJuego = 1.0f;
                        juegoTerminado = true; // Activa la Fase 2
                    }
                } else {
                    // FASE 2: Aparecer la imagen y el texto (Fade In)
                    opacidadImagenFin += 0.02f;
                    if (opacidadImagenFin >= 1.0f) {
                        opacidadImagenFin = 1.0f;
                        animandoFinDeJuego = false; // Terminó toda la animación
                        
                        // --- NUEVO: MOSTRAR EL BOTÓN QUIT ---
                        botonQuit.setVisible(true); 
                        
                        ((Timer)e.getSource()).stop();
                    }
                }
                repaint();
            });
            timerFinDeJuego.start();
        }
    }
    
    
    // Método para enviar MI mensaje
    public void enviarMensajeChat() {
        String texto = campoChat.getText().trim();
        
        if (!texto.isEmpty() && miJugador != null && miJugador.isVivo()) {
            
            // --- NUEVO: Limpiamos el nombre para quitar cualquier etiqueta delatora ---
            String nombreLimpio = miJugador.getNombre().replace(" (IMPOSTOR)", "").replace("(IMPOSTOR)", "").trim();
            
            // Me muestro mi propio mensaje usando el nombre limpio
            recibirMensajeChat(nombreLimpio, texto);
            
            // Lo enviamos por la red a los demás con el nombre limpio
            clienteRed.enviar("CHAT," + nombreLimpio + "," + texto);
            campoChat.setText(""); // Limpiamos la cajita
        }
    }

    // Método que llama la red cuando alguien más (o yo) manda un mensaje
    public void recibirMensajeChat(String emisor, String mensaje) {
        areaChat.append(emisor + ": " + mensaje + "\n");
        // Mover el scroll automáticamente hacia abajo
        areaChat.setCaretPosition(areaChat.getDocument().getLength());
    }
    
    
    public void aplicarColorRave(int idJugador, Color colorNuevo) {
        Jugador j = jugadoresConectados.get(idJugador);
        if (j != null) {
            j.setColorTemporal(colorNuevo);
            // Si el jugador no se repinta solo, fuerza la creación de las imágenes aquí.
            // (Si usas HerramientasColor, puedes volver a pasar los sprites originales por esa clase aquí)
            repaint();
        }
    }
    
    // --- TAREA 2: OFICINA DEL PROFESOR ---
public void completarMisionOficina() {
    if (tareaActualEnZona != null && tareaActualEnZona.getId().equals("OFI_TRABAJO")) {
        boolean esNueva = gestorTareas.registrarTareaCompletada("OFI_TRABAJO");
        
        if (esNueva) {
            clienteRed.enviar("TAREA_LISTA,OFI_TRABAJO");
            GestorSonido.jugar("general_sounds/task_Complete.wav");
            verificarFinDeJuego();
        }
        
        botonUse.setEnabled(false); 
        repaint(); 
    }
}
    
    // --- TAREA 3: PIZARRA DE MATEMÁTICAS ---
public void completarMisionPizarra() {
    if (tareaActualEnZona != null && tareaActualEnZona.getId().equals("PIZARRA_MATH")) {
        boolean esNueva = gestorTareas.registrarTareaCompletada("PIZARRA_MATH");
        
        if (esNueva) {
            clienteRed.enviar("TAREA_LISTA,PIZARRA_MATH");
            GestorSonido.jugar("general_sounds/task_Complete.wav");
            verificarFinDeJuego();
        }
        
        botonUse.setEnabled(false); 
        repaint(); 
    }
}
    
    public void completarMisionBiblioteca() {
        if (tareaActualEnZona != null && tareaActualEnZona.getId().equals("BIBLIO_LIBROS")) {
            // 1. Intentamos registrarla en el Gestor
            boolean esNueva = gestorTareas.registrarTareaCompletada("BIBLIO_LIBROS");
            
            if (esNueva) {
                // Solo si es la primera vez que se hace, avisamos a la red y verificamos victoria
                clienteRed.enviar("TAREA_LISTA,BIBLIO_LIBROS");
                GestorSonido.jugar("general_sounds/task_Complete.wav");
                verificarFinDeJuego();
            }
            
            botonUse.setEnabled(false); 
            repaint(); 
        }
    }
    
}