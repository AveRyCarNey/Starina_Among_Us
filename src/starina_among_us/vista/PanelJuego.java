package starina_among_us.vista;

import starina_among_us.modelo.Jugador;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
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
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import starina_among_us.modelo.GestorConfiguracion;
import starina_among_us.modelo.GestorSonido;
import starina_among_us.modelo.excepciones.AmongUsException;
import starina_among_us.modelo.excepciones.RecursoNoEncontradoException;

/**
 * Panel principal del juego donde ocurre el renderizado grafico, el ciclo de actualizacion 
 * y la logica de interaccion de los jugadores en la partida.
 * * @author Wulliber Yepez, Carlos Ramirez, Jorg Sierra, Samuel Salazar
 * @version 1.0
 */
public class PanelJuego extends JPanel implements KeyListener, ActionListener, FocusListener {

    private Jugador miJugador;
    private Timer reloj;
    private ClienteRed clienteRed;
    
    private ConcurrentHashMap<Integer, Jugador> jugadoresConectados;
    
    private int miId; 
    
    private Image fondoMapa;
    
    private boolean arriba, abajo, izquierda, derecha;
    private boolean estabaMoviendose = false; 
    
    private JButton botonKill;
    private int idVictimaCercana = -1;
    
    private JButton botonVent;
    private int idVentCercana = -1;
    
    private JButton botonReport;
    private int idCuerpoCercano = -1;
    
    private BufferedImage imgRevealCrewmate;
    private BufferedImage imgRevealImpostor;
    private int faseReveal = 0; 
    private float opacidadFadeReveal = 0.0f;
    
    private double camaraX = 0;
    private double camaraY = 0;
    private boolean camaraInicializada = false;
    
    private boolean victoriaForzadaPorTareas = false;
    
    private BufferedImage mapaDatos;
    private int spawnX = 400; 
    private int spawnY = 300;
    private ArrayList<Point> listaVents = new ArrayList<>();
    
    private int idEspectando = -1; 
    private boolean modoEspectador = false;
    
    private java.awt.image.BufferedImage imgReporteFondo; 
    private java.awt.image.BufferedImage imgReporteTexto; 
    
    private boolean mostrandoAnimacionReporte = false;
    
    private String miNombreElegido;
    private Color miColorElegido;
    
    private boolean juegoIniciado = false;
    private boolean soyHost = false;
    private JButton botonIniciarPartida;
    private final int MIN_JUGADORES = 5; 
    private final int MAX_JUGADORES = 10;
    
    private JButton botonUse;
    
    private boolean enVentilacion = false;
    private boolean animandoVent = false;
    private int ventActualIndex = -1;
    
    private java.awt.image.BufferedImage imgVentHole;
    
    private boolean mostrandoTabletReunion = false;
    private int tiempoRestanteReunion = 120; 
    private Timer timerReunion; 
    private int idVotadoSeleccionado = -1; 
    private boolean yaVote = false; 
    
    private java.awt.image.BufferedImage imgTabletFondo;
    private java.awt.image.BufferedImage imgVoteConfirmar;
    private java.awt.image.BufferedImage imgVoteCancelar;
    private java.awt.image.BufferedImage imgVoteSkip;
    private java.awt.image.BufferedImage imgVoteMegafono;
    private java.awt.image.BufferedImage imgVoteIVoted;
    private java.awt.image.BufferedImage imgVoteTripulante;
    
    private javax.swing.JButton botonAbrirChat;
    private javax.swing.JScrollPane scrollChat;
    private javax.swing.JTextArea areaChat;
    private javax.swing.JTextField campoChat;
    private boolean chatAbierto = false;
    
    private HashMap<Integer, Integer> registroVotos = new HashMap<>();
    
    private boolean mostrandoResultadosVotacion = false;
    private boolean animandoFadeVotacion = false;
    private float opacidadFadeVotacion = 0.0f;
    private Timer timerFadeVotacion;
    
    private java.awt.image.BufferedImage imgFondoEjected;
    private java.awt.image.BufferedImage imgCharEjectedOriginal;
    private java.awt.image.BufferedImage imgCharEjectedPintado;
    
    private boolean mostrandoAnimacionExpulsion = false;
    private Timer timerExpulsion;
    private double expulsionX = -100; 
    private double expulsionAngulo = 0; 
    private String textoExpulsion = "";
    private int charsMostradosExpulsion = 0; 
    private int idExpulsadoActual = -1;
    
    private HashMap<Integer, BufferedImage> iconosPintadosTablet = new HashMap<>();
    
    private int idReportadorActual = -1;
    
    private boolean juegoTerminado = false;
    private boolean victoriaLocal = false; 
    private BufferedImage imgVictoriaFondo;
    private BufferedImage imgDerrotaFondo;
    
    private boolean animandoFinDeJuego = false;
    private float opacidadFinJuego = 0.0f;
    private Timer timerFinDeJuego;
    
    private float opacidadImagenFin = 0.0f;
    private String textoGanadores = "";
    
    private Timer timerMisionServidor;
    private int tiempoMisionServidor = 0;
    private boolean misionServidorActiva = false;
    private Rectangle zonaSeguraServidor = new Rectangle(560, 400, 130, 100);
    
    private Timer timerMisionPC;
    private int tiempoMisionPC = 0;
    private boolean misionPCActiva = false;
    private Rectangle zonaSeguraPC = new Rectangle(1395, 824, 69, 176);
    
    private java.awt.image.BufferedImage imgBaseGanador;
    private java.util.ArrayList<java.awt.image.BufferedImage> spritesGanadores = new java.util.ArrayList<>();
    private java.util.ArrayList<String> nombresGanadores = new java.util.ArrayList<>();
    
    private java.awt.image.BufferedImage imgBotonEmergencia;
    private java.awt.Rectangle zonaEmergencia;
    private int botonEmergenciaX;
    private int botonEmergenciaY;
    private boolean cercaBotonEmergencia = false;
    
    private boolean esEmergenciaActual = false; 
    private java.awt.image.BufferedImage imgEmergenciaFondo;
    private java.awt.image.BufferedImage imgEmergenciaTexto;
    private java.awt.image.BufferedImage imgEmergenciaCuerpo;
    private java.awt.image.BufferedImage imgEmergenciaMano;
    private java.awt.image.BufferedImage imgEmergenciaMesa;
    
    private java.awt.image.BufferedImage imgEmergenciaCuerpoPintado;
    private java.awt.image.BufferedImage imgEmergenciaManoPintada;
    
    private JButton botonQuit;
    
    private starina_among_us.modelo.GestorTareas gestorTareas = new starina_among_us.modelo.GestorTareas();
    private starina_among_us.modelo.Tarea tareaActualEnZona = null; 
    
    private java.util.ArrayList<java.awt.image.BufferedImage> spritesInicio = new java.util.ArrayList<>();
    private java.util.ArrayList<String> nombresInicio = new java.util.ArrayList<>();
    
    public starina_among_us.modelo.GestorTareas getGestorTareas() { return gestorTareas; }
    
    private int contadorPasos = 0;
    
    private boolean visionSaboteada = false;
    private int tiempoSaboteo = 0; 
    private Timer timerEfectoSaboteo;

    private int cooldownSabotage = 10; 
    private Timer timerCooldownSabotage;

    private JButton botonSabotage;
    
    public static int impostoresConfigurados = 1;
    
    private int cooldownKill = 10; 
    private Timer timerCooldownKill;
    
    private String nombreMapaCargado;
    
    private ArrayList<Point> zonasSpawn = new ArrayList<>();
    
    private BufferedImage imgFiltroSin, imgFiltroCon, imgBotellon;
    private boolean estado_cargandoBotellon = false; 
    private boolean estado_filtroCompletado = false; 
    private Rectangle area_filtro = new Rectangle(750, 750, 60, 70); 
    private Rectangle area_botellonActual; 

    private final Rectangle[] ubicacionesBotellon = {
        new Rectangle(390, 514, 55, 65),
        new Rectangle(1702, 964, 65, 70),
        new Rectangle(72, 850, 50, 65)
    };
    
    /**
     * Constructor principal de la interfaz de juego.
     * Inicializa el mapa, la conexion de red, los recursos graficos, la interfaz de botones y los eventos.
     *
     * @param mapaElegido Nombre del mapa a jugar ("Uni" o "Salones").
     * @param ipServidor Direccion IP para la conexion multijugador o palabra clave para modo offline.
     * @param nombre Nickname elegido por el jugador local.
     * @param color Color RGB base elegido para el avatar del jugador.
     * @param esHost Indica si este cliente actua tambien como el servidor anfitrion de la partida.
     * @throws AmongUsException Si ocurre un error al cargar recursos criticos del juego.
     */
    public PanelJuego(String mapaElegido, String ipServidor, String nombre, Color color, boolean esHost) throws AmongUsException {
        this.setLayout(null);
        this.setBackground(Color.DARK_GRAY);
        this.setFocusable(true);
        this.addKeyListener(this);
        this.addFocusListener(this);
        this.miNombreElegido = nombre;
        this.miColorElegido = color;
        this.soyHost = esHost;
        
        setLayout(null); 
        
        cargarMapa(mapaElegido);
        gestorTareas.cargarMisionesPorMapa(mapaElegido);
        
        jugadoresConectados = new ConcurrentHashMap<>();

        BufferedImage hojaBotones = null;
        try {
            hojaBotones = javax.imageio.ImageIO.read(getClass().getResource("/starina_among_us/recursos/botones/gui_botones.png"));
        } catch (Exception e) {
            System.out.println("Alerta: No se encontro gui_botones.png");
        }
        
        try {
            imgRevealCrewmate = javax.imageio.ImageIO.read(getClass().getResource("/starina_among_us/recursos/eventos/crewmate.jpg"));
            imgRevealImpostor = javax.imageio.ImageIO.read(getClass().getResource("/starina_among_us/recursos/eventos/impostor.jpg"));
        } catch (Exception e) {
            System.out.println("Error cargando pantallas de roles: " + e.getMessage());
        }
        
        try {
            imgFiltroSin = ImageIO.read(getClass().getResource("/starina_among_us/recursos/misiones/filtro_sin_botellon_fullsize.png"));
            imgFiltroCon = ImageIO.read(getClass().getResource("/starina_among_us/recursos/misiones/filtro_con_botellon_fullsize.png"));
            imgBotellon = ImageIO.read(getClass().getResource("/starina_among_us/recursos/misiones/botellon_fullsize.png"));
        } catch (Exception e) {
            System.out.println("Error cargando assets de mision de agua: " + e.getMessage());
        }

        int azar = (int)(Math.random() * ubicacionesBotellon.length);
        this.area_botellonActual = new Rectangle(ubicacionesBotellon[azar]); 

        botonIniciarPartida = new JButton("ESPERANDO JUGADORES...");
        botonIniciarPartida.setBounds(250, 450, 300, 50);
        botonIniciarPartida.setBackground(new Color(50, 150, 50));
        botonIniciarPartida.setForeground(Color.WHITE);
        botonIniciarPartida.setFont(new Font("Arial", Font.BOLD, 16));
        botonIniciarPartida.setVisible(soyHost); 
        botonIniciarPartida.addActionListener(e -> {
            if (jugadoresConectados.size() >= MIN_JUGADORES) {
                
                java.util.List<Integer> ids = new java.util.ArrayList<>(jugadoresConectados.keySet());
                java.util.Collections.shuffle(ids);
                
                int cantidadMala = PanelJuego.impostoresConfigurados;
                if (cantidadMala >= ids.size()) cantidadMala = ids.size() - 1; 
                if (cantidadMala < 1) cantidadMala = 1;

                java.util.List<Integer> idsMalos = new java.util.ArrayList<>();
                for(int i = 0; i < cantidadMala; i++) {
                    idsMalos.add(ids.get(i));
                }
                
                for (Integer id : ids) {
                    boolean leTocoImpostor = idsMalos.contains(id); 
                    
                    actualizarRolJugador(id, leTocoImpostor); 
                    
                    if (clienteRed != null) clienteRed.enviar("ROL," + id + "," + leTocoImpostor);
                }
                
                if (clienteRed != null) clienteRed.enviar("START_REVEAL");
                iniciarAnimacionReveal(); 
            }
        });
        add(botonIniciarPartida);
        
        botonUse = new JButton();
        botonUse.setBounds(50, 430, 113, 116);
        botonUse.setContentAreaFilled(false);
        botonUse.setBorderPainted(false);
        botonUse.setFocusPainted(false);
        botonUse.setEnabled(false); 
        
        botonUse.addActionListener(e -> {
            
            if (cercaBotonEmergencia) {
                System.out.println("Emergencia solicitada localmente.");
                
                if (clienteRed != null) clienteRed.enviar("EMERGENCIA_RED," + miId);
                
                try {
                    iniciarReunion(miId, true); 
                } catch (Exception ex) {
                    System.out.println("Error iniciando reunion local: " + ex.getMessage());
                }
                
                botonUse.setEnabled(false);
                PanelJuego.this.requestFocusInWindow(); 
                return; 
            }
            
            if (tareaActualEnZona == null) return; 
            
            else if (tareaActualEnZona.getId().equals("LAB_FRASCOS")) {
                botonUse.setEnabled(false); 
                
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
                        if (clienteRed != null) clienteRed.enviar("COLOR_RAVE," + miId + "," + r + "," + g + "," + b);
                    } else {
                        Color colorReal = miJugador.getColorOriginal();
                        aplicarColorRave(miId, colorReal);
                        if (clienteRed != null) clienteRed.enviar("COLOR_RAVE," + miId + "," + colorReal.getRed() + "," + colorReal.getGreen() + "," + colorReal.getBlue());
    
                        boolean esNueva = gestorTareas.registrarTareaCompletada("LAB_FRASCOS");
                        if (esNueva) {
                            if (clienteRed != null) clienteRed.enviar("TAREA_LISTA,LAB_FRASCOS");
                            starina_among_us.modelo.GestorSonido.jugar("general_sounds/task_Complete.wav");
                            verificarFinDeJuego();
                        }
    
                        ((javax.swing.Timer)ev.getSource()).stop();
                    }
                });
                timerRave.start();
            } 
            
            else if (tareaActualEnZona.getId().equals("OFI_TRABAJO")) {
                starina_among_us.vista.VistaMisionOficina minijuego = new starina_among_us.vista.VistaMisionOficina(PanelJuego.this);
                minijuego.setVisible(true); 
            }
            
            else if (tareaActualEnZona.getId().equals("PIZARRA_MATH")) {
                starina_among_us.vista.VistaMisionPizarra minijuego = new starina_among_us.vista.VistaMisionPizarra(PanelJuego.this);
                minijuego.setVisible(true); 
            }
            
            else if (tareaActualEnZona.getId().equals("BIBLIO_LIBROS")) {
                starina_among_us.vista.VistaMisionBiblioteca minijuego = new starina_among_us.vista.VistaMisionBiblioteca(PanelJuego.this);
                minijuego.setVisible(true); 
            }
            
            else if (tareaActualEnZona.getId().equals("SALONES_SERVIDOR")) {
                if (!misionServidorActiva) {
                    misionServidorActiva = true;
                    tiempoMisionServidor = 15;
                    botonUse.setEnabled(false);
                    starina_among_us.modelo.GestorSonido.jugar("general_sounds/task_Inprogress.wav"); 
                    
                    timerMisionServidor = new Timer(1000, ev -> {
                        tiempoMisionServidor--;
                        
                        if (tiempoMisionServidor <= 0) {
                            ((Timer)ev.getSource()).stop();
                            misionServidorActiva = false;
                            
                            boolean esNueva = gestorTareas.registrarTareaCompletada("SALONES_SERVIDOR");
                            if (esNueva) {
                                if (clienteRed != null) clienteRed.enviar("TAREA_LISTA,SALONES_SERVIDOR");
                                starina_among_us.modelo.GestorSonido.jugar("general_sounds/task_Complete.wav");
                                verificarFinDeJuego();
                            }
                        }
                        repaint(); 
                    });
                    timerMisionServidor.start();
                }
            }
            
            else if (tareaActualEnZona.getId().equals("SALONES_PC")) {
                if (!misionPCActiva) {
                    starina_among_us.vista.VistaMisionPC minijuego = new starina_among_us.vista.VistaMisionPC(PanelJuego.this);
                    minijuego.setVisible(true); 
                    
                    if (minijuego.isCompletada()) {
                        misionPCActiva = true;
                        tiempoMisionPC = 5; 
                        botonUse.setEnabled(false);
                        starina_among_us.modelo.GestorSonido.jugar("general_sounds/task_Inprogress.wav");
                        
                        timerMisionPC = new Timer(1000, ev -> {
                            tiempoMisionPC--;
                            if (tiempoMisionPC <= 0) {
                                ((Timer)ev.getSource()).stop();
                                misionPCActiva = false;
                                
                                boolean esNueva = gestorTareas.registrarTareaCompletada("SALONES_PC");
                                if (esNueva) {
                                    if (clienteRed != null) clienteRed.enviar("TAREA_LISTA,SALONES_PC");
                                    starina_among_us.modelo.GestorSonido.jugar("general_sounds/task_Complete.wav");
                                    verificarFinDeJuego();
                                }
                            }
                            repaint();
                        });
                        timerMisionPC.start();
                    }
                }
            }
            
            else if (tareaActualEnZona.getId().equals("SALONES_AGUA")) {
                int px = (int) miJugador.getX() + 25;
                int py = (int) miJugador.getY() + 55;

                if (!estado_cargandoBotellon && area_botellonActual.contains(px, py)) {
                    estado_cargandoBotellon = true; 
                    area_botellonActual = new Rectangle(0,0,0,0); 
                    GestorSonido.jugar("general_sounds/task_Inprogress.wav");
                } 
                else if (estado_cargandoBotellon && area_filtro.contains(px, py)) {
                    estado_filtroCompletado = true;
                    estado_cargandoBotellon = false;
                    
                    boolean esNueva = gestorTareas.registrarTareaCompletada("SALONES_AGUA");
                    if (esNueva) {
                        if (clienteRed != null) clienteRed.enviar("TAREA_LISTA,SALONES_AGUA");
                        starina_among_us.modelo.GestorSonido.jugar("general_sounds/task_Complete.wav"); 
                        verificarFinDeJuego();
                    }
                }
                repaint();
            }
            
            else if (tareaActualEnZona.getId().equals("SALONES_ESTANTERIA")) { 
                starina_among_us.vista.VistaMisionEstanteria minijuego = new starina_among_us.vista.VistaMisionEstanteria(this);
                minijuego.setVisible(true);
            }
        });
        
        botonUse.setFocusable(false);
        
        botonKill = new JButton();
        botonKill.setBounds(650, 430, 115, 115); 
        botonKill.setContentAreaFilled(false);
        botonKill.setBorderPainted(false);
        botonKill.setFocusPainted(false);
        botonKill.setVisible(false);
        botonKill.setEnabled(false);
        botonKill.setFocusable(false);
        
        botonKill.setFont(new Font("Arial", Font.BOLD, 24));
        botonKill.setHorizontalTextPosition(SwingConstants.CENTER);
        botonKill.setVerticalTextPosition(SwingConstants.CENTER);

        timerCooldownKill = new Timer(1000, e -> {
            if (miJugador != null && miJugador.getEsImpostor()) {
                
                if (mostrandoTabletReunion || mostrandoAnimacionReporte || mostrandoAnimacionExpulsion || juegoTerminado || !miJugador.isVivo()) {
                    botonKill.setVisible(false);
                    return; 
                }
                botonKill.setVisible(true);
                
                if (!juegoIniciado) {
                    botonKill.setText("<html><font color='red'>10</font></html>");
                    botonKill.setEnabled(false);
                    cooldownKill = 10; 
                } else {
                    if (cooldownKill > 0) {
                        cooldownKill--;
                        botonKill.setText("<html><font color='red'>" + cooldownKill + "</font></html>");
                        botonKill.setEnabled(false); 
                    } else {
                        botonKill.setText(""); 
                    }
                }
            } else {
                botonKill.setVisible(false);
                botonKill.setText(""); 
            }
        });
        timerCooldownKill.start();
        
        botonVent = new JButton();
        botonVent.setBounds(650, 310, 120, 110); 
        botonVent.setContentAreaFilled(false);
        botonVent.setBorderPainted(false);
        botonVent.setFocusPainted(false);
        botonVent.setVisible(false);
        botonVent.setEnabled(false);
        botonVent.setFocusable(false);
        
        botonReport = new JButton();
        botonReport.setBounds(520, 433, 117, 112); 
        botonReport.setContentAreaFilled(false);
        botonReport.setBorderPainted(false);
        botonReport.setFocusPainted(false);
        botonReport.setVisible(false);
        botonReport.setEnabled(false);
        
        botonQuit = new JButton();
        botonQuit.setBounds(650, 400, 125, 142); 
        botonQuit.setContentAreaFilled(false);
        botonQuit.setBorderPainted(false);
        botonQuit.setFocusPainted(false);
        botonQuit.setVisible(false); 
        botonQuit.setFocusable(false);
        
        botonQuit.addActionListener(e -> {
            if (soyHost && clienteRed != null) {
                if (clienteRed != null) clienteRed.enviar("RESET_SERVER");
            }
            
            if (clienteRed != null) {
                clienteRed.desconectar();
            }
            
            jugadoresConectados.clear();
            registroVotos.clear();
            victoriaForzadaPorTareas = false;
            
            JFrame ventanaActual = (JFrame) javax.swing.SwingUtilities.getWindowAncestor(this);
            int estado = ventanaActual.getExtendedState();
            Rectangle dimensiones = ventanaActual.getBounds();
            
            ventanaActual.dispose();
            
            VentanaMenu menuNuevo = new VentanaMenu();
            menuNuevo.setExtendedState(estado);
            if (estado != JFrame.MAXIMIZED_BOTH) {
                menuNuevo.setBounds(dimensiones);
            }
            menuNuevo.setVisible(true);
        });
        
        this.add(botonQuit);
        
        botonSabotage = new JButton();
        botonSabotage.setBounds(650, 170, 110, 112); 
        botonSabotage.setOpaque(false);
        botonSabotage.setContentAreaFilled(false);
        botonSabotage.setBorderPainted(false);
        botonSabotage.setFocusPainted(false);
        botonSabotage.setVisible(false);
        
        botonSabotage.setFont(new Font("Arial", Font.BOLD, 24)); 
        botonSabotage.setHorizontalTextPosition(SwingConstants.CENTER);
        botonSabotage.setVerticalTextPosition(SwingConstants.CENTER);
        
        botonSabotage.addActionListener(e -> {
            if (cooldownSabotage <= 0 && miJugador != null && miJugador.getEsImpostor()) {
                if (clienteRed != null) clienteRed.enviar("SABOTAJE_VISION");
                activarSabotajeVision();
                botonSabotage.setEnabled(false);
                this.requestFocusInWindow();
            }
        });
        this.add(botonSabotage);

        timerCooldownSabotage = new Timer(1000, e -> {
            if (miJugador != null && miJugador.getEsImpostor()) {
                
                if (mostrandoTabletReunion || mostrandoAnimacionReporte || mostrandoAnimacionExpulsion || juegoTerminado || !miJugador.isVivo()) {
                    botonSabotage.setVisible(false);
                    return; 
                }
                botonSabotage.setVisible(true); 
                
                if (!juegoIniciado) {
                    botonSabotage.setText("<html><font color='red'>10</font></html>");
                    botonSabotage.setEnabled(false);
                    cooldownSabotage = 10; 
                } 
                else {
                    if (cooldownSabotage > 0) {
                        cooldownSabotage--;
                        botonSabotage.setText("<html><font color='red'>" + cooldownSabotage + "</font></html>");
                        botonSabotage.setEnabled(false); 
                    } else {
                        botonSabotage.setText(""); 
                        botonSabotage.setEnabled(true); 
                    }
                }
            } else {
                botonSabotage.setVisible(false); 
            }
        });
        timerCooldownSabotage.start();
        
        areaChat = new javax.swing.JTextArea();
        areaChat.setEditable(false);
        areaChat.setLineWrap(true);
        areaChat.setWrapStyleWord(true);
        areaChat.setFont(new Font("Arial", Font.BOLD, 14));
        areaChat.setBackground(new Color(220, 230, 240)); 
        areaChat.setForeground(Color.BLACK);
        
        scrollChat = new javax.swing.JScrollPane(areaChat);
        scrollChat.setBounds(480, 110, 250, 320); 
        scrollChat.setVisible(false); 
        this.add(scrollChat);

        campoChat = new javax.swing.JTextField();
        campoChat.setBounds(480, 440, 250, 35);
        campoChat.setFont(new Font("Arial", Font.PLAIN, 16));
        campoChat.setVisible(false);
        
        campoChat.addActionListener(e -> {
            enviarMensajeChat();
        });
        this.add(campoChat);

        botonAbrirChat = new javax.swing.JButton(); 
        botonAbrirChat.setBounds(650, 60, 55, 59); 
        botonAbrirChat.setContentAreaFilled(false);
        botonAbrirChat.setBorderPainted(false);
        botonAbrirChat.setFocusPainted(false);
        botonAbrirChat.setVisible(false);
        botonAbrirChat.setFocusable(false);
        
        botonAbrirChat.addActionListener(e -> {
            chatAbierto = !chatAbierto; 
            scrollChat.setVisible(chatAbierto);
            
            if (chatAbierto && miJugador != null && miJugador.isVivo()) {
                campoChat.setVisible(true);
                campoChat.requestFocus(); 
            } else {
                campoChat.setVisible(false); 
            }
        });
        this.add(botonAbrirChat);
        
        if (hojaBotones != null) {
            try {
                BufferedImage imgKill = starina_among_us.modelo.HerramientasImagen.recortar(hojaBotones, 921, 116, 115, 115);
                BufferedImage imgVent = starina_among_us.modelo.HerramientasImagen.recortar(hojaBotones, 1243, 2, 120, 110);
                BufferedImage imgBotonSabotage = starina_among_us.modelo.HerramientasImagen.recortar(hojaBotones, 475, 3, 110, 112);
                BufferedImage imgReport = starina_among_us.modelo.HerramientasImagen.recortar(hojaBotones, 589, 3, 117, 112);
                BufferedImage imgUse = starina_among_us.modelo.HerramientasImagen.recortar(hojaBotones, 1150, 112, 113, 116);
                BufferedImage imgQuit = starina_among_us.modelo.HerramientasImagen.recortar(hojaBotones, 124, 5, 125, 142);
                
                botonQuit.setIcon(new ImageIcon(imgQuit));
                
                BufferedImage killGris = starina_among_us.modelo.HerramientasImagen.hacerTransparente(imgKill, 0.5f);
                BufferedImage ventGris = starina_among_us.modelo.HerramientasImagen.hacerTransparente(imgVent, 0.5f);
                BufferedImage reportGris = starina_among_us.modelo.HerramientasImagen.hacerTransparente(imgReport, 0.5f);
                BufferedImage useGris = starina_among_us.modelo.HerramientasImagen.hacerTransparente(imgUse, 0.5f);
                BufferedImage sabotajeGris = starina_among_us.modelo.HerramientasImagen.hacerTransparente(imgBotonSabotage, 0.5f);
                
                botonKill.setIcon(new ImageIcon(imgKill));
                botonKill.setDisabledIcon(new ImageIcon(killGris));
                
                botonVent.setIcon(new ImageIcon(imgVent));
                botonVent.setDisabledIcon(new ImageIcon(ventGris));
                
                botonReport.setIcon(new ImageIcon(imgReport));
                botonReport.setDisabledIcon(new ImageIcon(reportGris));
                
                botonUse.setIcon(new ImageIcon(imgUse));
                botonUse.setDisabledIcon(new ImageIcon(useGris));
                
                botonSabotage.setIcon(new ImageIcon(imgBotonSabotage));          
                botonSabotage.setDisabledIcon(new ImageIcon(sabotajeGris));      
                
            } catch (Exception e) {
                System.out.println("Error recortando botones: " + e.getMessage());
            }
        }
         else {
            botonKill.setText("MATAR"); botonKill.setContentAreaFilled(true); botonKill.setBackground(Color.RED);
            botonVent.setText("VENT"); botonVent.setContentAreaFilled(true); botonVent.setBackground(Color.GRAY);
            botonReport.setText("REPORT"); botonReport.setContentAreaFilled(true); botonReport.setBackground(Color.ORANGE);
        }

        botonKill.addActionListener(e -> {
            if (idVictimaCercana != -1) {
                GestorSonido.jugar("impostor_kill.wav");
                if (clienteRed != null) clienteRed.enviar("MATAR," + idVictimaCercana);
                
                idVictimaCercana = -1;
                
                cooldownKill = 30;
                botonKill.setEnabled(false); 
                botonKill.setText("<html><font color='red'>30</font></html>");
                this.requestFocusInWindow(); 
            }
        });

        botonVent.addActionListener(e -> {
            if (animandoVent) return; 

            if (!enVentilacion) {
                animandoVent = true;
                enVentilacion = true; 
                
                miJugador.setAnimandoVent(true);
                GestorSonido.jugar("Vent/ventGroundEnter.wav");
                if (clienteRed != null) clienteRed.enviar("VENT_ANIM," + miId + ",true");
                
                if (idVentCercana == -1 || idVentCercana >= listaVents.size()) idVentCercana = 0;
                ventActualIndex = idVentCercana;

                java.awt.Point v = listaVents.get(ventActualIndex);
                int miOffsetX = 18;
                
                miJugador.setX(v.x - 25 + miOffsetX);
                miJugador.setY(v.y - 55);
                if (clienteRed != null) clienteRed.enviar("MOV," + miId + "," + (int)miJugador.getX() + "," + (int)miJugador.getY() + "," + miJugador.isMirandoDerecha() + ",false");

                javax.swing.Timer t = new javax.swing.Timer(20, null);
                int[] frames = {0};
                t.addActionListener(ev -> {
                    miJugador.setY(miJugador.getY() + 2); 
                    frames[0]++;
                    if (clienteRed != null) clienteRed.enviar("MOV," + miId + "," + (int)miJugador.getX() + "," + (int)miJugador.getY() + "," + miJugador.isMirandoDerecha() + ",false");
                    repaint();
                    
                    if (frames[0] >= 35) { 
                        t.stop();
                        animandoVent = false;
                        
                        miJugador.setAnimandoVent(false);
                        if (clienteRed != null) clienteRed.enviar("VENT_ANIM," + miId + ",false");
                        
                        if (clienteRed != null) clienteRed.enviar("VENT," + miId + ",true");
                        miJugador.setEnVentilacion(true);
                    }
                });
                t.start();
                
            } else {
                animandoVent = true;
                
                miJugador.setAnimandoVent(true);
                if (clienteRed != null) clienteRed.enviar("VENT_ANIM," + miId + ",true");
                
                if (clienteRed != null) clienteRed.enviar("VENT," + miId + ",false");
                miJugador.setEnVentilacion(false);
                
                int miOffsetX = 18; 
                javax.swing.Timer t = new javax.swing.Timer(20, null);
                int[] frames = {0};
                t.addActionListener(ev -> {
                    java.awt.Point v = listaVents.get(ventActualIndex);
                    miJugador.setX(v.x - 25 + miOffsetX); 
                    miJugador.setY(miJugador.getY() - 2); 
                    frames[0]++;
                    if (clienteRed != null) clienteRed.enviar("MOV," + miId + "," + (int)miJugador.getX() + "," + (int)miJugador.getY() + "," + miJugador.isMirandoDerecha() + ",false");
                    repaint();
                    
                    if (frames[0] >= 35) { 
                        t.stop();
                        animandoVent = false;
                        enVentilacion = false; 
                        
                        miJugador.setAnimandoVent(false);
                        if (clienteRed != null) clienteRed.enviar("VENT_ANIM," + miId + ",false");
                    }
                });
                t.start();
            }
        });
        
        botonReport.addActionListener(e -> {
            if (idCuerpoCercano != -1) {
                if (clienteRed != null) clienteRed.enviar("REPORT," + idCuerpoCercano);
                this.requestFocusInWindow();
            }
        });
        
        this.add(botonKill);
        this.add(botonVent);
        this.add(botonReport);
        this.add(botonUse);

        if (ipServidor.startsWith("OFFLINE")) {
            this.juegoIniciado = true; 
            this.faseReveal = 5;
            if (botonIniciarPartida != null) botonIniciarPartida.setVisible(false);
            
            boolean soyImpostorLibre = ipServidor.equals("OFFLINE_IMPOSTOR");
            
            botonKill.setVisible(false);
            botonReport.setVisible(false);
            botonSabotage.setVisible(false);
            
            inicializarJugadorLocal(1, soyImpostorLibre, spawnX, spawnY);
            
            if (soyImpostorLibre) {
                botonKill.setVisible(true);
                botonVent.setVisible(true);
                botonSabotage.setVisible(true); 
            }
            
        } else {
            
            if (soyHost) {
                new Thread(() -> {
                    try {
                        starina_among_us.red.Servidor.main(new String[0]); 
                        
                    } catch (Exception ex) {
                        System.out.println("Error arrancando servidor: " + ex.getMessage());
                    }
                }).start();

                try {
                    Thread.sleep(1000); 
                } catch (InterruptedException ex) {
                    System.out.println("Error en la inicializacion local del servidor: " + ex.getMessage());
                }
            }
            
            clienteRed = new ClienteRed(this, ipServidor);
        }
        reloj = new Timer(15, this);
        reloj.start();
        
        try {
            java.awt.image.BufferedImage ventsImg = javax.imageio.ImageIO.read(getClass().getResource("/starina_among_us/recursos/eventos/vents.png"));
            imgVentHole = starina_among_us.modelo.HerramientasImagen.recortar(ventsImg, 434, 7, 82, 40);
        } catch (Exception e) {
            System.out.println("Error cargando el hueco de vents.png: " + e.getMessage());
        }
        
        try {
            java.awt.image.BufferedImage imgIcons = javax.imageio.ImageIO.read(getClass().getResource("/starina_among_us/recursos/mapas/icons.png"));
            imgBotonEmergencia = starina_among_us.modelo.HerramientasImagen.recortar(imgIcons, 882, 763, 51, 46);
        } catch (Exception e) {
            System.out.println("Error cargando icons.png: " + e.getMessage());
        }
        
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                
                if (modoEspectador) {
                    boolean avanzar = (e.getButton() != java.awt.event.MouseEvent.BUTTON1);
                    cambiarObjetivoEspectador(avanzar);
                }
                
                else if (enVentilacion && !animandoVent && listaVents.size() > 1) {
                    
                    if (javax.swing.SwingUtilities.isLeftMouseButton(e)) {
                        ventActualIndex--;
                        if (ventActualIndex < 0) ventActualIndex = listaVents.size() - 1;
                    } else if (javax.swing.SwingUtilities.isRightMouseButton(e)) {
                        ventActualIndex++;
                        if (ventActualIndex >= listaVents.size()) ventActualIndex = 0;
                    }
                    GestorSonido.jugar("Vent/ventMoveGround1.wav");
                    
                    Point nuevaVent = listaVents.get(ventActualIndex);
                    int miOffsetX = 18; 
                    
                    miJugador.setX(nuevaVent.x - 25 + miOffsetX); 
                    miJugador.setY(nuevaVent.y - 55 + 50);
                    
                    if (clienteRed != null) clienteRed.enviar("MOV," + miId + "," + (int)miJugador.getX() + "," + (int)miJugador.getY() + "," + miJugador.isMirandoDerecha() + ",false");
                    
                    repaint(); 
                }
                else if (mostrandoTabletReunion && !mostrandoAnimacionReporte && !yaVote) {
                    
                    int mx = e.getX();
                    int my = e.getY();
                    
                    int tabletW = 750, tabletH = 550, padding = 45;
                    int tabletX = (getWidth() - tabletW) / 2;
                    int tabletY = (getHeight() - tabletH) / 2;
                    int screenX = tabletX + padding;
                    int screenY = tabletY + padding;
                    int screenW = tabletW - (padding * 2);
                    int screenH = tabletH - (padding * 2);
                    
                    int skipX = screenX + 10;
                    int skipY = screenY + screenH - 35;
                    if (mx >= skipX && mx <= skipX + 110 && my >= skipY && my <= skipY + 25) {
                        idVotadoSeleccionado = -2;
                        yaVote = true;
                        jugadoresConectados.get(miId).setHaVotado(true);
                        registroVotos.put(miId, idVotadoSeleccionado); 

                        if (clienteRed != null) clienteRed.enviar("VOTO," + miId + "," + idVotadoSeleccionado);
                        verificarTodosVotaron(); 
                        
                        repaint();
                        return;
                    }
                    
                    int boxW = 280, boxH = 45, espaciadoColumnas = 20, espaciadoFilas = 55;
                    int startX = screenX + (screenW - (boxW * 2 + espaciadoColumnas)) / 2;
                    int startY = screenY + 70;
                    
                    int col = 0, row = 0;
                    for (Jugador j : jugadoresConectados.values()) {
                        int drawX = startX + (col * (boxW + espaciadoColumnas));
                        int drawY = startY + (row * espaciadoFilas);
                        
                        if (mx >= drawX && mx <= drawX + boxW && my >= drawY && my <= drawY + boxH) {
                            
                            if (!j.isVivo()) return; 
                            
                            if (j.getId() == miId) {
                                return; 
                            }
                            
                            if (idVotadoSeleccionado == j.getId()) {
                                
                                if (mx >= drawX + boxW - 100 && mx <= drawX + boxW - 60) {
                                    yaVote = true;
                                    jugadoresConectados.get(miId).setHaVotado(true);
                                    registroVotos.put(miId, idVotadoSeleccionado); 
        
                                    if (clienteRed != null) clienteRed.enviar("VOTO," + miId + "," + idVotadoSeleccionado);
                                    verificarTodosVotaron(); 
                                }
                                else if (mx >= drawX + boxW - 50 && mx <= drawX + boxW - 10) {
                                    idVotadoSeleccionado = -1; 
                                }
                                
                            } else {
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
        
        imgReporteFondo = null; 
        imgReporteTexto = null;

        try {
            java.net.URL urlImagen = getClass().getResource("/starina_among_us/recursos/eventos/assets_events.png");
            
            if (urlImagen == null) {
                System.out.println("Error: La URL de la imagen de eventos es nula.");
            } else {
                java.awt.image.BufferedImage hojaEventos = javax.imageio.ImageIO.read(urlImagen);

                imgReporteFondo = starina_among_us.modelo.HerramientasImagen.recortar(hojaEventos, 1, 1, 940, 435);
                imgReporteTexto = starina_among_us.modelo.HerramientasImagen.recortar(hojaEventos, 1, 437, 420, 205);
                
                imgEmergenciaFondo = starina_among_us.modelo.HerramientasImagen.recortar(hojaEventos, 0, 0, 947, 434);
                imgEmergenciaTexto = starina_among_us.modelo.HerramientasImagen.recortar(hojaEventos, 418, 435, 373, 178);
                imgEmergenciaCuerpo = starina_among_us.modelo.HerramientasImagen.recortar(hojaEventos, 748, 614, 121, 98);
                imgEmergenciaMano = starina_among_us.modelo.HerramientasImagen.recortar(hojaEventos, 226, 642, 56, 30);
                imgEmergenciaMesa = starina_among_us.modelo.HerramientasImagen.recortar(hojaEventos, 0, 643, 225, 81);
            }
            
        } catch (Exception e) {
            System.out.println("Excepcion al cargar imagen de eventos: " + e.getMessage());
        }
        
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
        } catch (Exception e) {
            System.out.println("Error cargando componentes de votacion: " + e.getMessage());
        }
        
        try {
            imgFondoEjected = javax.imageio.ImageIO.read(getClass().getResource("/starina_among_us/recursos/eventos/ejected.png"));
            imgCharEjectedOriginal = javax.imageio.ImageIO.read(getClass().getResource("/starina_among_us/recursos/eventos/Ejected_character.png"));
        } catch (Exception e) {
            System.out.println("Error cargando assets de expulsion: " + e.getMessage());
        }
        
        try {
            imgVictoriaFondo = javax.imageio.ImageIO.read(getClass().getResource("/starina_among_us/recursos/eventos/win.jpg"));
            imgDerrotaFondo = javax.imageio.ImageIO.read(getClass().getResource("/starina_among_us/recursos/eventos/defeat.jpg"));
            imgBaseGanador = javax.imageio.ImageIO.read(getClass().getResource("/starina_among_us/recursos/personajes/rozul.png"));
        } catch (Exception e) {
            System.out.println("Error cargando pantallas de victoria o derrota: " + e.getMessage());
        }
        
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();

                if (botonKill != null) botonKill.setBounds(w - 150, h - 170, 115, 115);
                if (botonVent != null) botonVent.setBounds(w - 150, h - 290, 120, 110);
                if (botonReport != null) botonReport.setBounds(w - 280, h - 167, 117, 112);
                if (botonQuit != null) botonQuit.setBounds(w - 150, h - 200, 125, 142);
                
                if (botonUse != null) botonUse.setBounds(50, h - 170, 113, 116);
                
                if (botonAbrirChat != null) botonAbrirChat.setBounds(w - 150, 60, 55, 59);
                if (botonSabotage != null) botonSabotage.setBounds(w - 150, 170, 110, 112);
                if (scrollChat != null) scrollChat.setBounds(w - 320, 110, 250, Math.max(200, h - 280));
                if (campoChat != null) campoChat.setBounds(w - 320, Math.max(320, h - 160), 250, 35);

                if (botonIniciarPartida != null) botonIniciarPartida.setBounds((w - 300) / 2, h - 150, 300, 50);
            }
        });
        
    }
                
    /**
     * Inicia formalmente la partida, escondiendo elementos del lobby y cambiando el estado.
     */
    public void iniciarPartidaLobby() {
        this.juegoIniciado = true;
        if (botonIniciarPartida != null) {
            botonIniciarPartida.setVisible(false);
        }
    }
    
    /**
     * Carga el archivo de imagen del mapa y la matriz de datos de colision.
     *
     * @param nombreMapa Nombre del archivo del mapa (ej. "Uni").
     * @throws RecursoNoEncontradoException Si no se pueden encontrar las imagenes del mapa en los recursos.
     */
    public void cargarMapa(String nombreMapa) throws RecursoNoEncontradoException {
        this.nombreMapaCargado = nombreMapa;
        try {
            fondoMapa = new javax.swing.ImageIcon(getClass().getResource("/starina_among_us/recursos/mapas/" + nombreMapa + "_downscale.png")).getImage();
            mapaDatos = javax.imageio.ImageIO.read(getClass().getResource("/starina_among_us/recursos/mapas/" + nombreMapa + "_DataMap_downscale.png"));

            listaVents.clear(); 

            java.util.List<Point> todosLosPixelesRojos = new java.util.ArrayList<>();

            for (int i = 0; i < mapaDatos.getWidth(); i += 4) {
                for (int j = 0; j < mapaDatos.getHeight(); j += 4) {
                    Color c = new Color(mapaDatos.getRGB(i, j));
                    if (c.getRed() > 200 && c.getGreen() < 50 && c.getBlue() < 50) {
                        todosLosPixelesRojos.add(new Point(i, j));
                    }
                }
            }

            while (!todosLosPixelesRojos.isEmpty()) {
                Point semilla = todosLosPixelesRojos.remove(0);
                java.util.List<Point> grupo = new java.util.ArrayList<>();
                grupo.add(semilla);

                for (int k = 0; k < todosLosPixelesRojos.size(); k++) {
                    Point candidato = todosLosPixelesRojos.get(k);
                    if (semilla.distance(candidato) < 100) {
                        grupo.add(todosLosPixelesRojos.remove(k));
                        k--; 
                    }
                }

                long sumX = 0, sumY = 0;
                for (Point p : grupo) {
                    sumX += p.x;
                    sumY += p.y;
                }
                Point centroReal = new Point((int)(sumX / grupo.size()), (int)(sumY / grupo.size()));
                
                listaVents.add(centroReal);
            }     
            
            int anchoReal = fondoMapa.getWidth(null);
            int altoReal = fondoMapa.getHeight(null);
            
            int limiteX = Math.min(anchoReal, mapaDatos.getWidth());
            int limiteY = Math.min(altoReal, mapaDatos.getHeight());
            
            zonasSpawn.clear();

            for (int x = 0; x < limiteX; x += 5) {
                for (int y = 0; y < limiteY; y += 5) {
                    int pixel = mapaDatos.getRGB(x, y);
                    Color c = new Color(pixel, true);
                    
                    if (c.getRed() < 50 && c.getGreen() > 200 && c.getBlue() > 200) {
                        spawnX = x; spawnY = y; 
                        zonasSpawn.add(new Point(x, y)); 
                    }
                    else if (c.getRed() > 200 && c.getGreen() < 50 && c.getBlue() < 50) {
                        boolean esNueva = true;
                        for (java.awt.Point v : listaVents) {
                            if (v.distance(x, y) < 40) { esNueva = false; break; }
                        }
                        if (esNueva) listaVents.add(new java.awt.Point(x, y));
                    }
                }
            }
            
            if (nombreMapa.equals("Uni")) {
                zonaEmergencia = new java.awt.Rectangle(1200, 1215, 290, 185);
                botonEmergenciaX = 1332;
                botonEmergenciaY = 1296;
            } else if (nombreMapa.equals("Salones")) {
                zonaEmergencia = new java.awt.Rectangle(1610, 385, 240, 305);
                botonEmergenciaX = 1696;
                botonEmergenciaY = 516;
            }
        } catch (Exception e) {
            throw new RecursoNoEncontradoException(nombreMapa);
        }
    }
    
    /**
     * @return El nombre del mapa actualmente en uso.
     */
    public String getMapaElegido() {
        return nombreMapaCargado;
    }
    
    /**
     * Registra un voto recibido a traves de la red por parte de otro jugador.
     * @param idVotante Identificador del jugador que emitio el voto.
     * @param idVotado Identificador del jugador seleccionado (o -2 si fue Skip).
     */
    public void registrarVotoRed(int idVotante, int idVotado) {
        if (jugadoresConectados.containsKey(idVotante)) {
            jugadoresConectados.get(idVotante).setHaVotado(true);
            registroVotos.put(idVotante, idVotado); 
            repaint(); 
            verificarTodosVotaron(); 
        }
    }
    
    /**
     * Motor principal de fisicas y colisiones por mapa de bits.
     * Comprueba si la proxima posicion del jugador choca con una pared basada en el mapa de datos (DataMap).
     *
     * @param posX Nueva posicion X calculada.
     * @param posY Nueva posicion Y calculada.
     * @return true si es una zona transitable, false si es una pared u obstaculo opaco.
     */
    public boolean esPasoValido(double posX, double posY) {
        if (mapaDatos == null || fondoMapa == null) return true; 

        int pieX = (int) posX + 25; 
        int pieY = (int) posY + 55; 

        int limiteX = Math.min(fondoMapa.getWidth(null), mapaDatos.getWidth());
        int limiteY = Math.min(fondoMapa.getHeight(null), mapaDatos.getHeight());

        if (pieX < 0 || pieX >= limiteX || pieY < 0 || pieY >= limiteY) return false;

        Color c = new Color(mapaDatos.getRGB(pieX, pieY), true);

        if (c.getAlpha() < 50 || (c.getRed() > 200 && c.getGreen() > 200 && c.getBlue() > 200)) {
            return true;
        }

        if (c.getRed() < 100 && c.getGreen() < 100 && c.getBlue() < 100) {
            return false; 
        }
        
        return true; 
    }

    /**
     * Renderizador grafico principal.
     * Dibuja el mapa base, las tareas, las interfaces superpuestas, 
     * el sistema de iluminacion, la camara y gestiona las fases visuales de inicio y fin.
     *
     * @param g Objeto Graphics que proporciona el lienzo de dibujo de Java Swing.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (faseReveal == 2 || faseReveal == 3) {
            boolean soyImpostor = miJugador != null && miJugador.getEsImpostor();
            BufferedImage bgReveal = soyImpostor ? imgRevealImpostor : imgRevealCrewmate;
            
            if (bgReveal != null) {
                g.drawImage(bgReveal, 0, 0, getWidth(), getHeight(), null);
            }
            
            int spriteW = 120; 
            int spriteH = 150;
            int separacion = 40;
            
            int totalW = (spritesInicio.size() * spriteW) + ((spritesInicio.size() - 1) * separacion);
            int startX = (getWidth() - totalW) / 2;
            int startY = (getHeight() / 2) - 20; 

            for (int i = 0; i < spritesInicio.size(); i++) {
                int drawX = startX + (i * (spriteW + separacion));
                
                g.drawImage(spritesInicio.get(i), drawX, startY, spriteW, spriteH, null);
                
                g.setFont(new Font("Arial", Font.BOLD, 22));
                g.setColor(soyImpostor ? new Color(255, 50, 50) : Color.WHITE);
                String nom = nombresInicio.get(i);
                int nomW = g.getFontMetrics().stringWidth(nom);
                g.drawString(nom, drawX + (spriteW - nomW) / 2, startY - 20);
            }
            
            g.setColor(new Color(0, 0, 0, Math.min(255, Math.max(0, (int)(opacidadFadeReveal * 255)))));
            g.fillRect(0, 0, getWidth(), getHeight());
            return; 
        }
        
        if (botonVent != null && (enVentilacion || animandoVent)) {
            botonVent.setEnabled(true);
        }
        try {
            Graphics2D g2 = (Graphics2D) g;
            if (GestorConfiguracion.antialiasing) {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            } else {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            }
            java.awt.geom.AffineTransform camaraOriginal = g2.getTransform();
            
            double zoom = 1.0; 
            
            double objetivoX = getWidth() / 2.0; 
            double objetivoY = getHeight() / 2.0;
            
            int idASeguir = (modoEspectador && idEspectando != -1) ? idEspectando : miId;
            
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
            
            g2.translate(getWidth() / 2.0, getHeight() / 2.0);
            g2.scale(zoom, zoom);                              
            g2.translate(-camaraX, -camaraY);              
            
            if (fondoMapa != null && mapaDatos != null) {
                g2.drawImage(fondoMapa, 0, 0, fondoMapa.getWidth(null), fondoMapa.getHeight(null), this);
            }
            
            if (imgVentHole != null) {
                for (java.awt.Point v : listaVents) {
                    int offsetX = -18;
                    int offsetY = -20; 
                    
                    g2.drawImage(imgVentHole, v.x + offsetX, v.y + offsetY, null);
                }
            }
            
            if (imgBotonEmergencia != null) {
                g2.drawImage(imgBotonEmergencia, botonEmergenciaX, botonEmergenciaY, null);
            }
            
            if (nombreMapaCargado.equalsIgnoreCase("Salones")) {
                if (!estado_cargandoBotellon && !estado_filtroCompletado && area_botellonActual != null) {
                    g2.drawImage(imgBotellon, area_botellonActual.x, area_botellonActual.y, 
                                 area_botellonActual.width, area_botellonActual.height, null);
                }

                if (estado_filtroCompletado) {
                    g2.drawImage(imgFiltroCon, area_filtro.x, area_filtro.y, 
                                 area_filtro.width, area_filtro.height, null);
                } else {
                    g2.drawImage(imgFiltroSin, area_filtro.x, area_filtro.y, 
                                 area_filtro.width, area_filtro.height, null);
                }
            }
            
            if (misionServidorActiva) {
                g2.setColor(new Color(255, 255, 0, 50)); 
                g2.fillRect(zonaSeguraServidor.x, zonaSeguraServidor.y, zonaSeguraServidor.width, zonaSeguraServidor.height);
                
                g2.setColor(Color.YELLOW);
                Stroke oldStroke = g2.getStroke();
                g2.setStroke(new java.awt.BasicStroke(3, java.awt.BasicStroke.CAP_BUTT, java.awt.BasicStroke.JOIN_BEVEL, 0, new float[]{10}, 0));
                g2.drawRect(zonaSeguraServidor.x, zonaSeguraServidor.y, zonaSeguraServidor.width, zonaSeguraServidor.height);
                g2.setStroke(oldStroke);
                
                Font fuenteOriginal = g2.getFont();
                
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 18));
                g2.drawString(starina_among_us.modelo.GestorLenguaje.get("lbl_reiniciando"), zonaSeguraServidor.x - 10, zonaSeguraServidor.y - 30);
                
                g2.setColor(new Color(255, 50, 50));
                g2.setFont(new Font("Arial", Font.BOLD, 28));
                g2.drawString(tiempoMisionServidor + "s", zonaSeguraServidor.x + (zonaSeguraServidor.width/2) - 15, zonaSeguraServidor.y - 5);
                
                g2.setFont(fuenteOriginal);
            }
            
            if (misionPCActiva) {
                g2.setColor(new Color(0, 255, 255, 50)); 
                g2.fillRect(zonaSeguraPC.x, zonaSeguraPC.y, zonaSeguraPC.width, zonaSeguraPC.height);
                
                g2.setColor(Color.CYAN);
                Stroke oldStroke = g2.getStroke();
                g2.setStroke(new java.awt.BasicStroke(3, java.awt.BasicStroke.CAP_BUTT, java.awt.BasicStroke.JOIN_BEVEL, 0, new float[]{10}, 0));
                g2.drawRect(zonaSeguraPC.x, zonaSeguraPC.y, zonaSeguraPC.width, zonaSeguraPC.height);
                g2.setStroke(oldStroke);
                
                Font fuenteOriginal = g2.getFont();
                
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 18));
                g2.drawString(starina_among_us.modelo.GestorLenguaje.get("lbl_apagando"), zonaSeguraPC.x + 25, zonaSeguraPC.y - 10);
                
                g2.setColor(Color.CYAN);
                g2.setFont(new Font("Arial", Font.BOLD, 28));
                g2.drawString(tiempoMisionPC + "s", zonaSeguraPC.x + (zonaSeguraPC.width/2) - 15, zonaSeguraPC.y + 35);
                
                g2.setFont(fuenteOriginal);
            }

            for (Jugador j : jugadoresConectados.values()) {
                
                if (!j.isVivo() && j.isCuerpoReportado()) continue; 
                
                if (j.isEnVentilacion()) {
                    if (j.getId() == miId && !animandoVent) continue;
                    if (j.getId() != miId) continue;
                }
                
                java.awt.Shape clipOriginal = g2.getClip();
                boolean mascaraAplicada = false;

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

            g2.setTransform(camaraOriginal);
            
            if (modoEspectador && idEspectando != -1) {
                if (jugadoresConectados.containsKey(idEspectando)) {
                    Jugador objetivo = jugadoresConectados.get(idEspectando);
                    g2.setColor(Color.BLACK);
                    g2.fillRect(250, 10, 300, 40); 
                    g2.setColor(Color.WHITE);
                    g2.drawRect(250, 10, 300, 40); 
                    g2.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
                    g2.drawString(starina_among_us.modelo.GestorLenguaje.get("lbl_espectando") + " " + objetivo.getNombre(), 280, 35);
                }
            }
            
            int centroPantallaX = getWidth() / 2; 
            int centroPantallaY = getHeight() / 2;
            
            float radioVision;
            if (miJugador != null && miJugador.getEsImpostor()) {
                radioVision = 450f; 
            } else {
                radioVision = visionSaboteada ? 120f : 300f;
            }

            float[] distancias = {0.0f, 0.6f, 1.0f};
            Color[] colores = {
                new Color(0, 0, 0, 0),     
                new Color(0, 0, 0, 180),   
                new Color(0, 0, 0, 255)    
            };
            
            java.awt.RadialGradientPaint niebla = new java.awt.RadialGradientPaint(
                new java.awt.geom.Point2D.Float(centroPantallaX, centroPantallaY), 
                radioVision, distancias, colores);
            
            Paint pinturaOriginal = g2.getPaint();
            g2.setPaint(niebla);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setPaint(pinturaOriginal);
            
            if (mostrandoAnimacionReporte) {
                g2.setColor(new Color(0, 0, 0, 150));
                g2.fillRect(0, 0, getWidth(), getHeight());
                int centroX = getWidth() / 2;
                int centroY = getHeight() / 2;

                if (esEmergenciaActual) {
                    if (imgEmergenciaFondo != null) {
                        g2.drawImage(imgEmergenciaFondo, centroX - 400, centroY - 175, 800, 350, this);
                        g2.drawImage(imgEmergenciaTexto, centroX - 186, centroY - 140, 373, 178, this);
                        
                        int mesaX = centroX - 112; 
                        int mesaY = centroY + 100; 
                        
                        int cuerpoX = mesaX + 50;  
                        int cuerpoY = mesaY - 60;  
                        
                        int manoX = mesaX + 90;    
                        int manoY = mesaY + 6;     
                        
                        if (imgEmergenciaCuerpoPintado != null) {
                            g2.drawImage(imgEmergenciaCuerpoPintado, cuerpoX, cuerpoY, 121, 98, this);
                        }
                        
                        if (imgEmergenciaMesa != null) {
                            g2.drawImage(imgEmergenciaMesa, mesaX, mesaY, 225, 81, this);
                        }
                        
                        if (imgEmergenciaManoPintada != null) {
                            g2.drawImage(imgEmergenciaManoPintada, manoX, manoY, 56, 30, this); 
                        }
                    } else {
                        g2.setColor(Color.RED);
                        g2.setFont(new Font("Arial", Font.BOLD, 40));
                        g2.drawString("EMERGENCIA! (Faltan Imagenes)", centroX - 250, centroY);
                    }
                } else {
                    if (imgReporteFondo != null && imgReporteTexto != null) {
                        g2.drawImage(imgReporteFondo, centroX - 400, centroY - 175, 800, 350, this);
                        g2.drawImage(imgReporteTexto, centroX - 150, centroY - 130, 300, 205, this);
                    }
                }
            }
            
            if (mostrandoTabletReunion && !mostrandoAnimacionReporte) {
                g2.setColor(new Color(0, 0, 0, 180));
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                if (imgTabletFondo != null) {
                    int tabletW = 750; 
                    int tabletH = 550; 
                    int tabletX = (getWidth() - tabletW) / 2;
                    int tabletY = (getHeight() - tabletH) / 2;
                    
                    g2.drawImage(imgTabletFondo, tabletX, tabletY, tabletW, tabletH, this);
                    
                    int padding = 45;
                    int screenX = tabletX + padding;
                    int screenY = tabletY + padding;
                    int screenW = tabletW - (padding * 2);
                    int screenH = tabletH - (padding * 2);
                    
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("Arial", Font.BOLD, 26));
                    g2.drawString(starina_among_us.modelo.GestorLenguaje.get("lbl_quien_es"), screenX + (screenW/2) - 135, screenY + 35);
                    
                    if (imgVoteSkip != null) {
                        g2.drawImage(imgVoteSkip, screenX + 10, screenY + screenH - 35, 110, 25, null);
                    }

                    if (mostrandoResultadosVotacion) {
                        int votosS = 0;
                        for (Integer v : registroVotos.values()) {
                            if (v != null && v == -2) votosS++;
                        }
                        if (votosS > 0) {
                            g2.setColor(new Color(220, 50, 50));
                            g2.fillOval(screenX + 125, screenY + screenH - 40, 30, 30);
                            g2.setColor(Color.WHITE);
                            g2.setFont(new Font("Arial", Font.BOLD, 18));
                            int numW = g2.getFontMetrics().stringWidth(String.valueOf(votosS));
                            g2.drawString(String.valueOf(votosS), screenX + 125 + (15 - numW/2), screenY + screenH - 18);
                        }
                    }
                    
                    if (!mostrandoResultadosVotacion) {
                        if (tiempoRestanteReunion <= 10) g2.setColor(Color.RED);
                        else g2.setColor(Color.DARK_GRAY);
                        g2.setFont(new Font("Arial", Font.BOLD, 16));
                        g2.drawString(starina_among_us.modelo.GestorLenguaje.get("lbl_voto_termina") + " " + tiempoRestanteReunion + "s", screenX + 130, screenY + screenH - 18);
                    } else {
                        g2.setColor(Color.DARK_GRAY);
                        g2.setFont(new Font("Arial", Font.BOLD, 16));
                        g2.drawString(starina_among_us.modelo.GestorLenguaje.get("lbl_voto_fin"), screenX + 180, screenY + screenH - 18);
                    }
                    
                    int boxW = 280;              
                    int boxH = 45;              
                    int espaciadoColumnas = 20; 
                    int espaciadoFilas = 55;    
                    
                    int startX = screenX + (screenW - (boxW * 2 + espaciadoColumnas)) / 2;  
                    int startY = screenY + 70;  
                    
                    int col = 0, row = 0;
                    
                    for (Jugador j : jugadoresConectados.values()) {
                        int drawX = startX + (col * (boxW + espaciadoColumnas));
                        int drawY = startY + (row * espaciadoFilas);
                        
                        if (!j.isVivo()) g2.setColor(new Color(150, 150, 150, 180)); 
                        else g2.setColor(Color.WHITE); 
                        
                        g2.fillRoundRect(drawX, drawY, boxW, boxH, 10, 10);
                        g2.setColor(Color.GRAY);
                        g2.drawRoundRect(drawX, drawY, boxW, boxH, 10, 10);
                        
                        if (iconosPintadosTablet.containsKey(j.getId())) {
                            java.awt.image.BufferedImage suFoto = iconosPintadosTablet.get(j.getId());
                            g2.drawImage(suFoto, drawX + 8, drawY + 5, 45, 35, null);
                        }
                        
                        if (j.getId() == idReportadorActual && imgVoteMegafono != null) {
                            g2.drawImage(imgVoteMegafono, drawX - 25, drawY + 10, 30, 28, null);
                        }
                        
                        g2.setColor(Color.BLACK);
                        g2.setFont(new Font("Arial", Font.BOLD, 18));
                        g2.drawString(j.getNombre(), drawX + 55, drawY + 28);
                        
                        if (!mostrandoResultadosVotacion) {
                            if (idVotadoSeleccionado == j.getId() && !yaVote && j.isVivo()) {
                                g2.setColor(new Color(50, 200, 50));
                                g2.setStroke(new java.awt.BasicStroke(3));
                                g2.drawRoundRect(drawX, drawY, boxW, boxH, 10, 10);
                                
                                if (imgVoteConfirmar != null) g2.drawImage(imgVoteConfirmar, drawX + boxW - 100, drawY + 3, 40, 40, null);
                                if (imgVoteCancelar != null) g2.drawImage(imgVoteCancelar, drawX + boxW - 50, drawY + 3, 40, 40, null);
                            }
                            
                            if (j.isHaVotado() && imgVoteIVoted != null) {
                                g2.drawImage(imgVoteIVoted, drawX - 15, drawY + 5, 35, 35, null);
                            }
                        } 
                        else {
                            int votosRecibidos = 0;
                            for (Integer v : registroVotos.values()) {
                                if (v != null && v == j.getId()) votosRecibidos++;
                            }
                            
                            if (votosRecibidos > 0) {
                                g2.setColor(new Color(220, 50, 50));
                                g2.fillOval(drawX + boxW - 40, drawY + 7, 30, 30); 
                                g2.setColor(Color.WHITE);
                                g2.setFont(new Font("Arial", Font.BOLD, 18));
                                int numW = g2.getFontMetrics().stringWidth(String.valueOf(votosRecibidos));
                                g2.drawString(String.valueOf(votosRecibidos), drawX + boxW - 40 + (15 - numW/2), drawY + 28);
                            }
                        }
                        
                        if (!j.isVivo()) {
                            g2.setColor(new Color(255, 0, 0, 150)); 
                            g2.setStroke(new java.awt.BasicStroke(4));
                            g2.drawLine(drawX + 10, drawY + 10, drawX + boxW - 10, drawY + boxH - 10);
                            g2.drawLine(drawX + boxW - 10, drawY + 10, drawX + 10, drawY + boxH - 10);
                        }
                        
                        col++;
                        if (col > 1) { col = 0; row++; }
                    }
                }
                
                if (animandoFadeVotacion) {
                    java.awt.Composite og = g2.getComposite();
                    g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, opacidadFadeVotacion));
                    g2.setColor(Color.BLACK);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setComposite(og);
                }
            }
            
            if (mostrandoAnimacionExpulsion) {
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                if (imgFondoEjected != null) {
                    g2.drawImage(imgFondoEjected, 0, 0, getWidth(), getHeight(), this);
                }
                
                if (imgCharEjectedPintado != null) {
                    java.awt.geom.AffineTransform old = g2.getTransform();
                    g2.translate(expulsionX, getHeight() / 2.0);
                    g2.rotate(expulsionAngulo);
                    g2.drawImage(imgCharEjectedPintado, -45, -65, 90, 130, this);
                    g2.setTransform(old);
                }
                
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 24));
                String textoMostrar = textoExpulsion.substring(0, charsMostradosExpulsion);
                g2.drawString(textoMostrar, (getWidth() / 2) - 150, (getHeight() / 2) + 120);
            }
            
            if (animandoFinDeJuego || juegoTerminado) {
                java.awt.Composite originalComposite = g2.getComposite();
                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, opacidadFinJuego));
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setComposite(originalComposite);
            }

            if (juegoTerminado) {
                java.awt.Composite originalComposite = g2.getComposite();
                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, opacidadImagenFin));
                
                if (victoriaLocal && imgVictoriaFondo != null) {
                    g2.drawImage(imgVictoriaFondo, 0, 0, getWidth(), getHeight(), this);
                } else if (!victoriaLocal && imgDerrotaFondo != null) {
                    g2.drawImage(imgDerrotaFondo, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g2.setColor(Color.BLACK);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }

                int spriteW = 100; 
                int spriteH = 120; 
                int separacion = 30; 
                
                int totalW = (spritesGanadores.size() * spriteW) + ((spritesGanadores.size() - 1) * separacion);
                int startX = (getWidth() - totalW) / 2;
                int startY = (getHeight() - spriteH) / 2; 

                for (int i = 0; i < spritesGanadores.size(); i++) {
                    int drawX = startX + (i * (spriteW + separacion));
                    
                    g2.drawImage(spritesGanadores.get(i), drawX, startY, spriteW, spriteH, null);
                    
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Arial", Font.BOLD, 18));
                    String nom = nombresGanadores.get(i);
                    int nomW = g2.getFontMetrics().stringWidth(nom);
                    g2.drawString(nom, drawX + (spriteW - nomW) / 2, startY - 15);
                }
                
                if (textoGanadores.contains("IMPOSTORES")) g2.setColor(new Color(255, 50, 50)); 
                else g2.setColor(new Color(100, 200, 255)); 
                
                g2.setFont(new Font("Arial", Font.BOLD, 36));
                int anchoTexto = g2.getFontMetrics().stringWidth(textoGanadores);
                g2.drawString(textoGanadores, (getWidth() - anchoTexto) / 2, getHeight() - 80); 
                
                g2.setComposite(originalComposite);
            }
            
            int barraX = 20;
            int barraY = 20;
            int barraAnchoTotal = 300;
            int barraAlto = 25;
            
            g2.setColor(new Color(50, 50, 50, 200));
            g2.fillRect(barraX, barraY, barraAnchoTotal, barraAlto);
            
            int rellenoVerde = (int)(barraAnchoTotal * gestorTareas.obtenerPorcentajeProgreso());
            g2.setColor(new Color(50, 255, 50)); 
            g2.fillRect(barraX, barraY, rellenoVerde, barraAlto);
            
            g2.setColor(Color.WHITE);
            g2.setStroke(new java.awt.BasicStroke(3));
            g2.drawRect(barraX, barraY, barraAnchoTotal, barraAlto);
            
            if (!juegoIniciado && faseReveal == 0) {
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                int centroX = getWidth() / 2; 
                
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 36));
                String textoSala = starina_among_us.modelo.GestorLenguaje.get("lbl_sala");
                g2.drawString(textoSala, centroX - (g2.getFontMetrics().stringWidth(textoSala) / 2), 100);
                
                g2.setFont(new Font("Arial", Font.PLAIN, 20));
                int totalActual = jugadoresConectados.size();
                String textoConectados = starina_among_us.modelo.GestorLenguaje.get("lbl_conectados") + " " + totalActual + " / " + MAX_JUGADORES;
                g2.drawString(textoConectados, centroX - (g2.getFontMetrics().stringWidth(textoConectados) / 2), 150);
                
                int yNombre = 220;
                for (Jugador j : jugadoresConectados.values()) {
                    String nom = "- " + j.getNombre();
                    g2.drawString(nom, centroX - (g2.getFontMetrics().stringWidth(nom) / 2), yNombre);
                    yNombre += 30;
                }
                
                if (soyHost && botonIniciarPartida != null) {
                    if (totalActual >= MIN_JUGADORES) {
                        botonIniciarPartida.setText(starina_among_us.modelo.GestorLenguaje.get("btn_iniciar"));
                        botonIniciarPartida.setEnabled(true);
                        botonIniciarPartida.setBackground(new Color(50, 200, 50));
                    } else {
                        botonIniciarPartida.setText(starina_among_us.modelo.GestorLenguaje.get("btn_faltan"));
                        botonIniciarPartida.setEnabled(false);
                        botonIniciarPartida.setBackground(Color.GRAY);
                    }
                } else if (!soyHost) {
                    g2.setColor(Color.YELLOW);
                    String txtEspera = starina_among_us.modelo.GestorLenguaje.get("lbl_esperando_host");
                    g2.drawString(txtEspera, centroX - (g2.getFontMetrics().stringWidth(txtEspera) / 2), getHeight() - 80);
                }
            }
            
            if (faseReveal == 1 || faseReveal == 4) {
                g2.setColor(new Color(0, 0, 0, Math.min(255, Math.max(0, (int)(opacidadFadeReveal * 255)))));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
            
            java.awt.Toolkit.getDefaultToolkit().sync();
            
        } catch (Exception ex) {
            g.setColor(Color.RED);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
            g.drawString("ERROR GRAFICO: " + ex.toString(), 20, 30);
            StackTraceElement[] trace = ex.getStackTrace();
            for (int i = 0; i < Math.min(20, trace.length); i++) {
                g.drawString(trace[i].toString(), 20, 50 + (i * 15));
            }
        }
    }

    /**
     * Ejecutado por el temporizador principal del juego cada 15ms.
     * Es el corazon del Game Loop: calcula la velocidad de movimiento, comprueba
     * colisiones contra las paredes, ejecuta los radares de proximidad para misiones 
     * o interacciones (kill/report) y avisa al servidor de los cambios.
     *
     * @param e Evento temporal provisto por el Timer de Swing.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!juegoIniciado || juegoTerminado || animandoFinDeJuego) {
            repaint();
            return; 
        }
        
        if (!jugadoresConectados.containsKey(miId)) return;
        Jugador miMuñeco = jugadoresConectados.get(miId);
        
        double velocidad = 4.0; 
        if (mostrandoAnimacionReporte || mostrandoTabletReunion || (modoEspectador && !miMuñeco.isVivo())) {
            velocidad = 0;
        }

        double dx = 0;
        double dy = 0;
        
        if (izquierda) dx = -1;
        if (derecha)   dx = 1;
        if (arriba)    dy = -1;
        if (abajo)     dy = 1;

        if (dx != 0 && dy != 0) {
            dx *= 0.7071; 
            dy *= 0.7071;
        }
        dx *= velocidad;
        dy *= velocidad;

        boolean meMuevoAhora = (dx != 0 || dy != 0);
        
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
                if (clienteRed != null) clienteRed.enviar(mensaje);
                estabaMoviendose = true; 
            }
            
            contadorPasos++;
            if (contadorPasos >= 20) { 
                starina_among_us.modelo.GestorSonido.jugar("Footsteps/Tile/FootstepTile01.wav");
                contadorPasos = 0;
            }
        } else {
            miMuñeco.setMoviendose(false);
            if (estabaMoviendose) {
                String mensaje = "MOV," + miId + "," + (int)miMuñeco.getX() + "," + (int)miMuñeco.getY()
                                 + "," + miMuñeco.isMirandoDerecha() + ",false";
                if (clienteRed != null) clienteRed.enviar(mensaje);
                estabaMoviendose = false; 
            }
        }
        
        int pieX = (int) miMuñeco.getX() + 25;
        int pieY = (int) miMuñeco.getY() + 55;

        if (misionServidorActiva && miMuñeco != null) {
            if (!zonaSeguraServidor.contains(pieX, pieY)) {
                if (timerMisionServidor != null) timerMisionServidor.stop();
                misionServidorActiva = false;
                starina_among_us.modelo.GestorSonido.jugar("general_sounds/Panel_GenericDisappear.wav");
                
                if (tareaActualEnZona != null && tareaActualEnZona.getId().equals("SALONES_SERVIDOR")) {
                    botonUse.setEnabled(true);
                }
            }
        }
        
        if (misionPCActiva && miMuñeco != null) {
            if (!zonaSeguraPC.contains(pieX, pieY)) {
                if (timerMisionPC != null) timerMisionPC.stop();
                misionPCActiva = false;
                starina_among_us.modelo.GestorSonido.jugar("general_sounds/Panel_GenericDisappear.wav");
                
                if (tareaActualEnZona != null && tareaActualEnZona.getId().equals("SALONES_PC")) {
                    botonUse.setEnabled(true);
                }
            }
        }
        
        if (botonKill.isVisible()) { 
            double miCentroX = miMuñeco.getX() + 25; 
            double miCentroY = miMuñeco.getY() + 25;
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
            
            if (cooldownKill > 0) {
                if (botonKill.isEnabled()) botonKill.setEnabled(false);
                idVictimaCercana = -1; 
            } else {
                if (victimaPotencial != null) {
                    if (idVictimaCercana != victimaPotencial.getId()) {
                        idVictimaCercana = victimaPotencial.getId();
                        botonKill.setEnabled(true);
                    }
                } else {
                    if (botonKill.isEnabled()) {
                        botonKill.setEnabled(false);
                        idVictimaCercana = -1;
                    }
                }
            }
        }

        if (enVentilacion || animandoVent) {
            botonVent.setEnabled(true); 
            botonUse.setEnabled(false);
        } 
        else if (mapaDatos != null && this.miJugador != null) {
            boolean tocandoRojo = false;
            boolean tocandoVerde = false;

            for (int x = pieX - 5; x <= pieX + 5; x += 5) {
                for (int y = pieY - 5; y <= pieY + 5; y += 5) {
                    if (x >= 0 && x < mapaDatos.getWidth() && y >= 0 && y < mapaDatos.getHeight()) {
                        Color c = new Color(mapaDatos.getRGB(x, y), true);
                        if (c.getRed() > 150 && c.getGreen() < 120 && c.getBlue() < 120) tocandoRojo = true;
                        if (c.getGreen() > 150 && c.getRed() < 120 && c.getBlue() < 120) tocandoVerde = true;
                    }
                }
            }

            cercaBotonEmergencia = false;
            boolean encenderBotonUse = false; 
            tareaActualEnZona = null;
            
            if (!enVentilacion && !animandoVent && miMuñeco.isVivo()) {
                tareaActualEnZona = gestorTareas.obtenerTareaEnZona(pieX, pieY);
                if (tareaActualEnZona != null) {
                    if (tareaActualEnZona.getId().equals("SALONES_AGUA")) {
                        tareaActualEnZona = null; 
                    } else {
                        encenderBotonUse = true;
                    }
                }

                if (nombreMapaCargado.equalsIgnoreCase("Salones") && !estado_filtroCompletado) {
                    
                    if (!estado_cargandoBotellon && area_botellonActual != null && area_botellonActual.contains(pieX, pieY)) {
                        tareaActualEnZona = gestorTareas.getTareaPorId("SALONES_AGUA");
                        encenderBotonUse = true;
                    }
                    else if (estado_cargandoBotellon && area_filtro.contains(pieX, pieY)) {
                        tareaActualEnZona = gestorTareas.getTareaPorId("SALONES_AGUA");
                        encenderBotonUse = true;
                    }
                }
                
                if (tocandoRojo && this.miJugador.getEsImpostor()) {
                    botonVent.setEnabled(true);
                    double distMin = Double.MAX_VALUE;
                    for (int i = 0; i < listaVents.size(); i++) {
                        double d = listaVents.get(i).distance(pieX, pieY);
                        if (d < distMin) {
                            distMin = d;
                            idVentCercana = i;
                        }
                    }
                } else {
                    botonVent.setEnabled(false);
                }

                if (zonaEmergencia != null && zonaEmergencia.contains(pieX, pieY)) {
                    if (miJugador != null && miJugador.isVivo()) {
                        cercaBotonEmergencia = true;
                        encenderBotonUse = true; 
                    }
                }

                botonUse.setEnabled(encenderBotonUse);
            }
        }
        
        if (jugadoresConectados.containsKey(miId) && jugadoresConectados.get(miId).isVivo()) {
            double distanciaMinima = 10000;
            Jugador cuerpoEncontrado = null;
            
            for (Jugador otro : jugadoresConectados.values()) {
                if (otro.getId() != miId && !otro.isVivo() && !otro.isCuerpoReportado()) {
                    double distancia = Math.hypot(otro.getX() - miMuñeco.getX(), otro.getY() - miMuñeco.getY());
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
        
        for (Jugador j : jugadoresConectados.values()) {
            j.actualizarAnimacion();
        }
        repaint();
    }

    /**
     * Responde a las pulsaciones del teclado fisico del usuario marcando
     * las banderas de direccion como activas para que el GameLoop las procese.
     * @param e Evento del teclado provisto por AWT.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        
        if (modoEspectador) return; 

        int tecla = e.getKeyCode();
        
        if (tecla == KeyEvent.VK_RIGHT || tecla == KeyEvent.VK_D) derecha = true;
        if (tecla == KeyEvent.VK_LEFT || tecla == KeyEvent.VK_A)  izquierda = true;
        if (tecla == KeyEvent.VK_UP || tecla == KeyEvent.VK_W)    arriba = true;
        if (tecla == KeyEvent.VK_DOWN || tecla == KeyEvent.VK_S)  abajo = true;
    }

    /**
     * Registra cuando el jugador deja de pulsar una tecla para detener el
     * movimiento en esa direccion.
     * @param e Evento del teclado provisto por AWT.
     */
    @Override 
    public void keyReleased(KeyEvent e) {
        int tecla = e.getKeyCode();
        
        if (tecla == KeyEvent.VK_RIGHT || tecla == KeyEvent.VK_D) derecha = false;
        if (tecla == KeyEvent.VK_LEFT || tecla == KeyEvent.VK_A)  izquierda = false;
        if (tecla == KeyEvent.VK_UP || tecla == KeyEvent.VK_W)    arriba = false;
        if (tecla == KeyEvent.VK_DOWN || tecla == KeyEvent.VK_S)  abajo = false;
    }
    
    @Override public void keyTyped(KeyEvent e) {}
    
    /**
     * Sincroniza la posicion de un jugador remoto aplicando algoritmos de interpolacion
     * para suavizar su caminata en la pantalla local o lo teletransporta si proviene de un vent.
     *
     * @param id El identificador unico del jugador remoto a actualizar.
     * @param x Nueva coordenada X recibida de la red.
     * @param y Nueva coordenada Y recibida de la red.
     */
    public void actualizarJugadorRemoto(int id, double x, double y) {
        if (id == miId) return;

        if (jugadoresConectados.containsKey(id)) {
            Jugador j = jugadoresConectados.get(id);
            double dx = x - j.getX(); 
            double dy = y - j.getY();
            
            double distancia = Math.hypot(dx, dy);

            if (distancia > 100) {
                j.setX(x);
                j.setY(y);
                j.detener(); 
            }
            else if (distancia < 5) {
                j.detener();
                j.setX(x); 
                j.setY(y);
            } 
            else {
                double pasoX = dx / 5.0;
                double pasoY = dy / 5.0;

                j.setX(j.getX() + pasoX);
                j.setY(j.getY() + pasoY);
            }
            
        } else {
            Jugador nuevo = new Jugador(id, "Jugador " + id, x, y, false);
            jugadoresConectados.put(id, nuevo);
        }
        repaint();
    }
    
    /**
     * Construye la representacion local de tu propio jugador luego de que 
     * el servidor haya autorizado la conexion y asignado un ID valido.
     *
     * @param id Identificador numerico otorgado por el servidor.
     * @param esImpostor Booleano que define si el jugador empieza como impostor.
     * @param xDelServidor Posicion en X pre-aprobada por el servidor.
     * @param yDelServidor Posicion en Y pre-aprobada por el servidor.
     */
    public void inicializarJugadorLocal(int id, boolean esImpostor, double xDelServidor, double yDelServidor) {
        this.miId = id;
        
        String nombreFinal = this.miNombreElegido;
        if (esImpostor) nombreFinal += " (IMPOSTOR)";
        
        double miX = spawnX - 25;
        double miY = spawnY - 55;
        
        if (!zonasSpawn.isEmpty()) {
            int indiceRandom = (int)(Math.random() * zonasSpawn.size());
            Point puntoElegido = zonasSpawn.get(indiceRandom);
            miX = puntoElegido.x - 25;
            miY = puntoElegido.y - 55;
        }
        
        this.miJugador = new Jugador(id, nombreFinal, miX, miY, esImpostor);
        
        if (this.miColorElegido != null) {
            this.miJugador.setColorRGB(this.miColorElegido.getRed(), this.miColorElegido.getGreen(), this.miColorElegido.getBlue());
        }
        
        jugadoresConectados.put(this.miId, this.miJugador);
        
        if (esImpostor) {
            botonKill.setVisible(true);
            botonVent.setVisible(true);
        }
        
        repaint();
    }
    
    public String getMiNombreElegido() {
        return miNombreElegido;
    }

    public Color getMiColorElegido() {
        return miColorElegido;
    }
    
    /**
     * Convierte a un jugador en fantasma e inicia su animacion de cadaver
     * luego de confirmarse un evento de Kill mediante la red.
     * @param idMuerto ID numerico del jugador asesinado.
     */
    public void reportarMuerte(int idMuerto) {
        boolean encontrado = false;

        for (Jugador j : jugadoresConectados.values()) {
            if (j.getId() == idMuerto) {
                j.setVivo(false); 
                j.detener(); 
                encontrado = true;
                break; 
            }
        }

        if (!encontrado) {
            System.out.println("Error: Se ordeno matar al ID " + idMuerto + " pero no se encuentra en la lista.");
        }
        
        if (idMuerto == miId) {
            modoEspectador = true;
            idEspectando = -1; 
            cambiarObjetivoEspectador(true); 
            
            botonKill.setVisible(false);
            botonVent.setVisible(false);
            botonReport.setVisible(false);
        }
        repaint();
        verificarFinDeJuego();
    }
        
    /**
     * Elimina el rastro de un jugador en el mapa tras haberse desconectado
     * intencionalmente o perdido la conexion por red.
     * @param id ID numerico del jugador saliente.
     */
    public void eliminarJugador(int id) {
        if (jugadoresConectados.containsKey(id)) {
            jugadoresConectados.remove(id);
            repaint(); 
            
            verificarFinDeJuego();
        }
    }
    
    @Override
    public void focusGained(FocusEvent e) {
    }

    /**
     * Pausa el movimiento del jugador de forma segura en caso de que 
     * el usuario minimice la ventana del juego.
     */
    @Override
    public void focusLost(FocusEvent e) {
        derecha = izquierda = arriba = abajo = false;
        if (jugadoresConectados.containsKey(miId)) {
            Jugador miMuñeco = jugadoresConectados.get(miId);
            miMuñeco.detener();
            String mensaje = "MOV," + miId + "," + (int)miMuñeco.getX() + "," + (int)miMuñeco.getY() 
                             + "," + miMuñeco.isMirandoDerecha() + ",false";
            if (clienteRed != null) clienteRed.enviar(mensaje);
        }
        repaint();
    }
    
    public void actualizarColorJugador(int id, int r, int g, int b) {
        if (jugadoresConectados.containsKey(id)) {
            Jugador j = jugadoresConectados.get(id);
            Color nuevoColor = new Color(r, g, b);
            
            j.cambiarSkin(nuevoColor); 
            repaint();
        }
    }
    
    public void actualizarRolJugador(int id, boolean esImpostor) {
        if (jugadoresConectados.containsKey(id)) {
            Jugador j = jugadoresConectados.get(id);
            j.setImpostor(esImpostor);
        } else {
            Jugador nuevo = new Jugador(id, "Jugador " + id, 0, 0, esImpostor);
            jugadoresConectados.put(id, nuevo);
        }
    }
    
    /**
     * Desata la secuencia visual y auditiva que indica el comienzo de una reunion de votacion.
     * Transporta a todos los jugadores vivos a su punto de aparicion en la mesa central.
     *
     * @param idReportador Identidad de la persona que convoco la reunion.
     * @param esEmergencia Indica si fue un boton de emergencia (true) o cuerpo reportado (false).
     */
    public void iniciarReunion(int idReportador, boolean esEmergencia) {
        cooldownSabotage = 10;
        cooldownKill = 10;
        
        this.idReportadorActual = idReportador;
        this.esEmergenciaActual = esEmergencia; 
        
        if (esEmergencia) {
            new Thread(() -> {
                starina_among_us.modelo.GestorSonido.jugar("general_sounds/alarm_emergencymeeting.wav"); 
            }).start();
            
            Jugador j = jugadoresConectados.get(idReportador);
            if (j != null && j.getColor() != null) {
                try {
                    if (imgEmergenciaCuerpo != null) {
                        imgEmergenciaCuerpoPintado = starina_among_us.modelo.HerramientasColor.crearPersonaje(imgEmergenciaCuerpo, j.getColor());
                    }
                    if (imgEmergenciaMano != null) {
                        imgEmergenciaManoPintada = starina_among_us.modelo.HerramientasColor.crearPersonaje(imgEmergenciaMano, j.getColor());
                    }
                } catch (Exception ex) {
                    System.out.println("Error al pintar personaje de emergencia: " + ex.getMessage());
                }
            }
        } else {
            starina_among_us.modelo.GestorSonido.jugar("general_sounds/report_Bobdyfound.wav");
        }
        
        mostrandoAnimacionReporte = true; 
        
        if (botonKill != null) botonKill.setEnabled(false);
        if (botonReport != null) {
            botonReport.setVisible(false);
            botonReport.setEnabled(false);
        }
        if (botonUse != null) botonUse.setEnabled(false);
        
        repaint();
        
        Timer timerAnimacion = new Timer(3000, new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                mostrandoAnimacionReporte = false;
                
                for (Jugador j : jugadoresConectados.values()) {
                    if (!j.isVivo()) j.setCuerpoReportado(true);
                }
                
                if (jugadoresConectados.containsKey(miId)) {
                    Jugador yo = jugadoresConectados.get(miId);
                    if (yo.isVivo()) {
                        
                        int nuevaX = spawnX; 
                        int nuevaY = spawnY;
                        
                        if (!zonasSpawn.isEmpty()) {
                            int indiceRandom = (int)(Math.random() * zonasSpawn.size());
                            Point puntoElegido = zonasSpawn.get(indiceRandom);
                            nuevaX = puntoElegido.x - 25;
                            nuevaY = puntoElegido.y - 55;
                        }
                        
                        yo.setX(nuevaX);
                        yo.setY(nuevaY);
                        yo.detener();
                        
                        if (clienteRed != null) clienteRed.enviar("MOV," + miId + "," + nuevaX + "," + nuevaY + "," + yo.isMirandoDerecha() + ",false");
                    }
                }
                
                idCuerpoCercano = -1;
                PanelJuego.this.requestFocusInWindow();
                ((Timer)e.getSource()).stop(); 
                if(botonAbrirChat != null) botonAbrirChat.setVisible(true);
                
                abrirTabletReunion();
            }
        });
        timerAnimacion.setRepeats(false);
        timerAnimacion.start(); 
    }
    
    /**
     * Dibuja y activa en pantalla la interfaz de la Tablet para votar
     * e inicia la cuenta regresiva temporal.
     */
    public void abrirTabletReunion() {
        mostrandoTabletReunion = true;
        tiempoRestanteReunion = 60;
        yaVote = false;
        idVotadoSeleccionado = -1;
        
        mostrandoResultadosVotacion = false;
        animandoFadeVotacion = false;
        opacidadFadeVotacion = 0.0f;
        
        for (Jugador j : jugadoresConectados.values()) j.setHaVotado(false);
        
        arriba = abajo = izquierda = derecha = false;
        botonKill.setVisible(false); botonVent.setVisible(false);
        botonReport.setVisible(false); botonUse.setVisible(false);
        
        if (timerReunion != null && timerReunion.isRunning()) timerReunion.stop();
        timerReunion = new Timer(1000, e -> { 
            tiempoRestanteReunion--;
            if (tiempoRestanteReunion <= 0) {
                mostrarResultadosYFade(); 
            }
            repaint();
        });
        timerReunion.start();
        
        iconosPintadosTablet.clear();
        for (Jugador j : jugadoresConectados.values()) {
            if (imgVoteTripulante != null && j.getColor() != null) {
                java.awt.image.BufferedImage iconoColor = starina_among_us.modelo.HerramientasColor.crearPersonaje(imgVoteTripulante, j.getColor());
                iconosPintadosTablet.put(j.getId(), iconoColor);
            }
        }
        areaChat.setText("");
        repaint();
    }
    
    /**
     * Logica final post-votacion de la tablet.
     * Analiza el recuento de los registros de votos y 
     * determina matematicamente si alguien es expulsado o hay empate.
     */
    public void cerrarTabletReunion() {
        mostrandoTabletReunion = false;
        if (timerReunion != null) timerReunion.stop();
        
        java.util.HashMap<Integer, Integer> conteo = new java.util.HashMap<>();
        for (Integer voto : registroVotos.values()) {
            conteo.put(voto, conteo.getOrDefault(voto, 0) + 1);
        }
        
        int idMasVotado = -1;
        int maxVotos = 0;
        boolean empate = false;
        
        for (java.util.Map.Entry<Integer, Integer> entry : conteo.entrySet()) {
            if (entry.getValue() > maxVotos) {
                maxVotos = entry.getValue();
                idMasVotado = entry.getKey();
                empate = false;
            } else if (entry.getValue() == maxVotos) {
                empate = true;
            }
        }
        
        if (empate || idMasVotado == -2 || idMasVotado == -1) {
            iniciarAnimacionExpulsion(-1); 
        } else {
            iniciarAnimacionExpulsion(idMasVotado); 
        }
    }
    
    private void verificarTodosVotaron() {
        if (!mostrandoTabletReunion || mostrandoResultadosVotacion) return;

        int vivosTotales = 0;
        int votosEmitidos = 0;

        for (Jugador j : jugadoresConectados.values()) {
            if (j.isVivo()) {
                vivosTotales++;
                if (j.isHaVotado()) votosEmitidos++;
            }
        }

        if (votosEmitidos >= vivosTotales) {
            mostrarResultadosYFade();
        }
    }

    private void mostrarResultadosYFade() {
        if (mostrandoResultadosVotacion) return; 
        mostrandoResultadosVotacion = true;
        
        if (timerReunion != null) timerReunion.stop();

        chatAbierto = false;
        botonAbrirChat.setVisible(false);
        scrollChat.setVisible(false);
        campoChat.setVisible(false);
        repaint(); 

        Timer t = new Timer(4000, ev -> {
            ((Timer)ev.getSource()).stop();
            iniciarFadeHaciaExpulsion();
        });
        t.setRepeats(false);
        t.start();
    }

    private void iniciarFadeHaciaExpulsion() {
        animandoFadeVotacion = true;
        opacidadFadeVotacion = 0.0f;

        timerFadeVotacion = new Timer(30, e -> {
            opacidadFadeVotacion += 0.05f; 
            if (opacidadFadeVotacion >= 1.0f) {
                opacidadFadeVotacion = 1.0f;
                ((Timer)e.getSource()).stop();

                animandoFadeVotacion = false;
                mostrandoResultadosVotacion = false;
                mostrandoTabletReunion = false;

                cerrarTabletReunion(); 
            }
            repaint();
        });
        timerFadeVotacion.start();
    }
    
    public void iniciarAnimacionExpulsion(int idExpulsado) {
        this.idExpulsadoActual = idExpulsado;
        mostrandoAnimacionExpulsion = true;
        expulsionX = -100; 
        expulsionAngulo = 0;
        charsMostradosExpulsion = 0;
        
        if (idExpulsado >= 0 && jugadoresConectados.containsKey(idExpulsado)) {
            Jugador j = jugadoresConectados.get(idExpulsado);
            textoExpulsion = j.getNombre() + " " + starina_among_us.modelo.GestorLenguaje.get("lbl_fue_expulsado");
            if (imgCharEjectedOriginal != null) {
                imgCharEjectedPintado = starina_among_us.modelo.HerramientasColor.crearPersonaje(imgCharEjectedOriginal, j.getColor());
            } else {
                imgCharEjectedPintado = null;
            }
            
        } else {
            textoExpulsion = starina_among_us.modelo.GestorLenguaje.get("lbl_nadie_expulsado");
            imgCharEjectedPintado = null; 
        }

        timerExpulsion = new Timer(30, e -> {
            expulsionX += 4.5; 
            expulsionAngulo += 0.05; 
            
            if (charsMostradosExpulsion < textoExpulsion.length() && (int)expulsionX % 3 == 0) {
                charsMostradosExpulsion++;
            }
            
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
        
        if (idExpulsadoActual != -1) {
            
            if (soyHost) {
                if (clienteRed != null) clienteRed.enviar("MATAR," + idExpulsadoActual);
            }
            
            if (jugadoresConectados.containsKey(idExpulsadoActual)) {
                Jugador j = jugadoresConectados.get(idExpulsadoActual);
                j.setVivo(false);
                j.setCuerpoReportado(true); 
                j.detener();
            }
            
            if (idExpulsadoActual == miId) {
                modoEspectador = true;
                idEspectando = -1; 
                cambiarObjetivoEspectador(true); 
                
                botonKill.setVisible(false);
                botonVent.setVisible(false);
                botonReport.setVisible(false);
                botonUse.setVisible(false);
            }
        }
        
        Jugador yo = jugadoresConectados.get(miId);
        if (yo != null && yo.isVivo()) {
            botonUse.setVisible(true); 
            
            if (yo.esImpostor()) {
                botonKill.setVisible(true);
                botonVent.setVisible(true);
            }
        }
        repaint();
        
        verificarFinDeJuego();
    }
    
    private void cambiarObjetivoEspectador(boolean avanzar) {
        java.util.ArrayList<Integer> vivos = new java.util.ArrayList<>();
        
        for (Jugador j : jugadoresConectados.values()) {
            if (j.isVivo()) {
                vivos.add(j.getId());
            }
        }
        
        if (vivos.isEmpty()) return;
        
        java.util.Collections.sort(vivos);
        
        int indiceActual = vivos.indexOf(idEspectando);
        
        if (avanzar) {
            indiceActual++;
            if (indiceActual >= vivos.size()) indiceActual = 0; 
        } else {
            indiceActual--;
            if (indiceActual < 0) indiceActual = vivos.size() - 1; 
        }
        
        idEspectando = vivos.get(indiceActual);
        repaint();
    }
    
    public Jugador getJugador(int id) {
        return jugadoresConectados.get(id);
    }


    public void agregarJugador(int id, String nombre, int x, int y, int r, int g, int b) {
        Jugador nuevo = new Jugador(id, nombre, x, y, false);
        
        nuevo.setColorRGB(r, g, b); 
        
        jugadoresConectados.put(id, nuevo);
        repaint();
    }

    /**
     * Auditor de victoria principal del juego.
     * Evalua las condiciones globales como "Todos los impostores muertos" o
     * "Mas impostores que tripulantes" y desata la secuencia final.
     */
    public synchronized void verificarFinDeJuego() {
        if(clienteRed == null) return;
        if (juegoTerminado || !juegoIniciado || animandoFinDeJuego) return;

        int vivosTripulantes = 0;
        int vivosImpostores = 0;

        for (Jugador j : jugadoresConectados.values()) {
            if (j.isVivo()) {
                if (j.getEsImpostor()) vivosImpostores++;
                else vivosTripulantes++;
            }
        }

        if (vivosTripulantes + vivosImpostores == 0) return;

        boolean gananImpostores = (vivosImpostores >= vivosTripulantes);
        boolean gananTripulantesPorTareas = (gestorTareas.obtenerPorcentajeProgreso() >= 1.0f) || victoriaForzadaPorTareas;
        boolean gananTripulantesPorExpulsion = (vivosImpostores == 0);
        
        boolean gananTripulantes = gananTripulantesPorExpulsion || gananTripulantesPorTareas;

        if (gananImpostores || gananTripulantes) {
            
            animandoFinDeJuego = true;
            juegoTerminado = false; 
            
            if (gananTripulantesPorTareas && clienteRed != null && !victoriaForzadaPorTareas) {
                clienteRed.enviar("VICTORIA_TAREAS");
            }
            Jugador yo = jugadoresConectados.get(miId);
            
            if (yo != null) {
                if (yo.getEsImpostor() && gananImpostores) victoriaLocal = true;
                else if (!yo.getEsImpostor() && gananTripulantes) victoriaLocal = true;
                else victoriaLocal = false;
            }
            
            if (gananImpostores) textoGanadores = starina_among_us.modelo.GestorLenguaje.get("lbl_ganan_imp");
            else textoGanadores = gananTripulantesPorTareas ? starina_among_us.modelo.GestorLenguaje.get("lbl_ganan_tareas") : starina_among_us.modelo.GestorLenguaje.get("lbl_ganan_trip");
            
            arriba = abajo = izquierda = derecha = false;
            botonKill.setVisible(false);
            botonVent.setVisible(false);
            botonReport.setVisible(false);
            botonUse.setVisible(false);
            
            spritesGanadores.clear();
            nombresGanadores.clear();
            java.util.ArrayList<Integer> idsAgregados = new java.util.ArrayList<>();
            
            for (Jugador j : jugadoresConectados.values()) {
                if ((gananImpostores && j.getEsImpostor()) || (gananTripulantes && !j.getEsImpostor())) {
                    if (!idsAgregados.contains(j.getId())) {
                        idsAgregados.add(j.getId());
                        if (imgBaseGanador != null && j.getColor() != null) {
                            java.awt.image.BufferedImage spritePintado = starina_among_us.modelo.HerramientasColor.crearPersonaje(imgBaseGanador, j.getColor());
                            spritesGanadores.add(spritePintado);
                            nombresGanadores.add(j.getNombre());
                        }
                    }
                }
            }
            
            opacidadFinJuego = 0.0f;
            opacidadImagenFin = 0.0f;
            
            timerFinDeJuego = new Timer(30, e -> {
                
                if (!juegoTerminado) {
                    opacidadFinJuego += 0.015f; 
                    if (opacidadFinJuego >= 1.0f) {
                        opacidadFinJuego = 1.0f;
                        juegoTerminado = true; 
                    }
                } else {
                    opacidadImagenFin += 0.02f;
                    if (opacidadImagenFin >= 1.0f) {
                        opacidadImagenFin = 1.0f;
                        animandoFinDeJuego = false; 
                        
                        botonQuit.setVisible(true); 
                        ((Timer)e.getSource()).stop();
                    }
                }
                repaint();
            });
            timerFinDeJuego.start();
        }
    }
    
    public void forzarVictoriaTareas() {
        this.victoriaForzadaPorTareas = true;
        verificarFinDeJuego();
        repaint();
    }
    
    public void enviarMensajeChat() {
        String texto = campoChat.getText().trim();
        
        if (!texto.isEmpty() && miJugador != null && miJugador.isVivo()) {
            
            String nombreLimpio = miJugador.getNombre().replace(" (IMPOSTOR)", "").replace("(IMPOSTOR)", "").trim();
            
            recibirMensajeChat(nombreLimpio, texto);
            
            if (clienteRed != null) clienteRed.enviar("CHAT," + nombreLimpio + "," + texto);
            campoChat.setText(""); 
        }
    }

    public void recibirMensajeChat(String emisor, String mensaje) {
        areaChat.append(emisor + ": " + mensaje + "\n");
        areaChat.setCaretPosition(areaChat.getDocument().getLength());
    }
    
    public void aplicarColorRave(int idJugador, Color colorNuevo) {
        Jugador j = jugadoresConectados.get(idJugador);
        if (j != null) {
            j.setColorTemporal(colorNuevo);
            repaint();
        }
    }
    
    /**
     * Finaliza la mision de la oficina y notifica a la red el incremento
     * en la barra de progreso general.
     */
    public void completarMisionOficina() {
        if (tareaActualEnZona != null && tareaActualEnZona.getId().equals("OFI_TRABAJO")) {
            boolean esNueva = gestorTareas.registrarTareaCompletada("OFI_TRABAJO");
            
            if (esNueva) {
                if (clienteRed != null) clienteRed.enviar("TAREA_LISTA,OFI_TRABAJO");
                GestorSonido.jugar("general_sounds/task_Complete.wav");
                verificarFinDeJuego();
            }
            
            botonUse.setEnabled(false); 
            repaint(); 
        }
    }
    
    public void completarMisionPizarra() {
        if (tareaActualEnZona != null && tareaActualEnZona.getId().equals("PIZARRA_MATH")) {
            boolean esNueva = gestorTareas.registrarTareaCompletada("PIZARRA_MATH");
            
            if (esNueva) {
                if (clienteRed != null) clienteRed.enviar("TAREA_LISTA,PIZARRA_MATH");
                GestorSonido.jugar("general_sounds/task_Complete.wav");
                verificarFinDeJuego();
            }
            
            botonUse.setEnabled(false); 
            repaint(); 
        }
    }
    
    public void completarMisionBiblioteca() {
        if (tareaActualEnZona != null && tareaActualEnZona.getId().equals("BIBLIO_LIBROS")) {
            boolean esNueva = gestorTareas.registrarTareaCompletada("BIBLIO_LIBROS");
            
            if (esNueva) {
                if (clienteRed != null) clienteRed.enviar("TAREA_LISTA,BIBLIO_LIBROS");
                GestorSonido.jugar("general_sounds/task_Complete.wav");
                verificarFinDeJuego();
            }
            
            botonUse.setEnabled(false); 
            repaint(); 
        }
    }
    
    public void activarSabotajeVision() {
        visionSaboteada = true;
        tiempoSaboteo = 15; 
        cooldownSabotage = 50; 
        
        starina_among_us.modelo.GestorSonido.jugar("general_sounds/Alarm_sabotage.wav"); 
        
        if (timerEfectoSaboteo != null) timerEfectoSaboteo.stop();
        
        timerEfectoSaboteo = new Timer(1000, e -> {
            tiempoSaboteo--;
            if (tiempoSaboteo <= 0) {
                visionSaboteada = false;
                ((Timer)e.getSource()).stop();
            }
            repaint();
        });
        timerEfectoSaboteo.start();
    }
    
    public void iniciarAnimacionReveal() {
        if (botonIniciarPartida != null) botonIniciarPartida.setVisible(false);
        
        spritesInicio.clear();
        nombresInicio.clear();
        
        Jugador yo = jugadoresConectados.get(miId);
        if (yo != null && imgBaseGanador != null) {
            boolean soyImpostor = yo.getEsImpostor();
            
            for (Jugador j : jugadoresConectados.values()) {
                if (soyImpostor) {
                    if (j.getEsImpostor()) {
                        BufferedImage spritePintado = starina_among_us.modelo.HerramientasColor.crearPersonaje(imgBaseGanador, j.getColor());
                        spritesInicio.add(spritePintado);
                        nombresInicio.add(j.getNombre());
                    }
                } else {
                    if (j.getId() == miId) {
                        BufferedImage spritePintado = starina_among_us.modelo.HerramientasColor.crearPersonaje(imgBaseGanador, j.getColor());
                        spritesInicio.add(spritePintado);
                        nombresInicio.add(j.getNombre());
                    }
                }
            }
        }
        
        faseReveal = 1; 
        opacidadFadeReveal = 0.0f;
        
        Timer timerAnimacion = new Timer(30, null);
        timerAnimacion.addActionListener(e -> {
            
            if (faseReveal == 1) { 
                opacidadFadeReveal += 0.05f;
                if (opacidadFadeReveal >= 1.0f) {
                    opacidadFadeReveal = 1.0f;
                    faseReveal = 2; 
                    starina_among_us.modelo.GestorSonido.jugar("general_sounds/Roundstart_MAIN.wav");
                }
            } 
            else if (faseReveal == 2) { 
                opacidadFadeReveal -= 0.05f;
                if (opacidadFadeReveal <= 0.0f) {
                    opacidadFadeReveal = 0.0f;
                    ((Timer)e.getSource()).stop(); 
                    
                    Timer tWait = new Timer(3000, ev -> {
                        faseReveal = 3;
                        timerAnimacion.start(); 
                    });
                    tWait.setRepeats(false);
                    tWait.start();
                }
            } 
            else if (faseReveal == 3) { 
                opacidadFadeReveal += 0.05f;
                if (opacidadFadeReveal >= 1.0f) {
                    opacidadFadeReveal = 1.0f;
                    faseReveal = 4; 
                    juegoIniciado = true; 
                }
            } 
            else if (faseReveal == 4) { 
                opacidadFadeReveal -= 0.05f;
                if (opacidadFadeReveal <= 0.0f) {
                    opacidadFadeReveal = 0.0f;
                    faseReveal = 5; 
                    
                    if (yo != null && yo.esImpostor()) {
                        botonKill.setVisible(true);
                        botonVent.setVisible(true);
                        botonSabotage.setVisible(true);
                    }
                    ((Timer)e.getSource()).stop();
                }
            }
            repaint();
        });
        timerAnimacion.start();
    }
    
    public void completarMisionEstanteria() {
        if (tareaActualEnZona != null && tareaActualEnZona.getId().equals("SALONES_ESTANTERIA")) {
            boolean esNueva = gestorTareas.registrarTareaCompletada("SALONES_ESTANTERIA");
            
            if (esNueva) {
                if (clienteRed != null) clienteRed.enviar("TAREA_LISTA,SALONES_ESTANTERIA");
                GestorSonido.jugar("general_sounds/task_Complete.wav");
                verificarFinDeJuego();
            }
            
            botonUse.setEnabled(false); 
            repaint(); 
        }
    }
}