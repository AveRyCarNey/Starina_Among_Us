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
import javax.swing.JProgressBar;

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
    private final int MIN_JUGADORES = 2; 
    
    // --- VARIABLES DE MISIONES ---
    private JButton botonUsar;
    private JProgressBar barraMisiones;
    private boolean[] misMisionesCompletadas; 
    private int totalMisionesGlobales = 1; // Para evitar división por cero
    private int misionesCompletadasGlobales = 0;
    private ArrayList<Point> listaMisiones = new ArrayList<>();
    private int idMisionCercana = -1;
    private JButton botonUse;
    
    // --- VARIABLES DE VENTILACIÓN ---
    private boolean enVentilacion = false;
    private boolean animandoVent = false;
    private int ventActualIndex = -1;
    
    private java.awt.image.BufferedImage imgVentHole;
    
    
    

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
        
        // --- BARRA DE MISIONES ---
        barraMisiones = new JProgressBar(0, 100);
        barraMisiones.setBounds(10, 10, 300, 25);
        barraMisiones.setValue(0);
        barraMisiones.setStringPainted(true);
        barraMisiones.setForeground(new Color(50, 200, 50));
        barraMisiones.setBackground(Color.DARK_GRAY);
        add(barraMisiones);

        // --- BOTÓN USAR / MISIÓN ---
        botonUse = new JButton();
        botonUse.setBounds(50, 430, 113, 116);
        botonUse.setContentAreaFilled(false);
        botonUse.setBorderPainted(false);
        botonUse.setFocusPainted(false);
        botonUse.setEnabled(false); // Apagado por defecto
        botonUse.addActionListener(e -> realizarMisionAutomatica());
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
        
        
        
        // Importante: No lo añadas al panel si el jugador es Fantasma (eso lo validaremos después)
        
        

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
                clienteRed.enviar("MATAR," + idVictimaCercana);
                botonKill.setEnabled(false);
                idVictimaCercana = -1;
                this.requestFocusInWindow(); 
            }
        });

        botonVent.addActionListener(e -> {
            System.out.println("🔘 CLIC EN VENT! enVentilacion: " + enVentilacion + " | animando: " + animandoVent);
            if (animandoVent) return; // Si se está moviendo, ignorar clics

            if (!enVentilacion) {
                System.out.println("⬇️ BAJANDO A LA ALCANTARILLA...");
                animandoVent = true;
                enVentilacion = true;
                if (idVentCercana == -1 || idVentCercana >= listaVents.size()) idVentCercana = 0;
                ventActualIndex = idVentCercana;

                java.awt.Point v = listaVents.get(ventActualIndex);
                
                int miOffsetX = 18;
                // Centramos al jugador (El centro del hueco menos la mitad del jugador)
                miJugador.setX(v.x - 25 + miOffsetX);
                // Ponemos los pies exactamente en la parte superior del hueco
                miJugador.setY(v.y - 55);

                javax.swing.Timer t = new javax.swing.Timer(20, null);
                int[] frames = {0};
                t.addActionListener(ev -> {
                    miJugador.setY(miJugador.getY() + 2); // Bajando
                    frames[0]++;
                    repaint();
                    // Subimos los frames a 35 para que baje 70 píxeles y se hunda completo
                    if (frames[0] >= 35) { 
                        t.stop();
                        animandoVent = false;
                    }
                });
                t.start();
                
            } else {
                System.out.println("⬆️ SUBIENDO A LA SUPERFICIE...");
                animandoVent = true;
                
                // --- TU OFFSET MÁGICO ---
                int miOffsetX = 18; 
                
                javax.swing.Timer t = new javax.swing.Timer(20, null);
                int[] frames = {0};
                t.addActionListener(ev -> {
                    // Mantenemos la X alineada con el hueco mientras sube
                    java.awt.Point v = listaVents.get(ventActualIndex);
                    miJugador.setX(v.x - 25 + miOffsetX); 
                    
                    miJugador.setY(miJugador.getY() - 2); // Subiendo...
                    frames[0]++;
                    repaint();
                    
                    if (frames[0] >= 35) { // Debe coincidir con los frames de bajada
                        t.stop();
                        animandoVent = false;
                        enVentilacion = false; 
                        System.out.println("✅ Terminó de SUBIR.");
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
        
        // Lo que pasará cuando le des clic
        botonUse.addActionListener(e -> {
            System.out.println("¡Realizando tarea!");
            // Aquí luego pondremos la lógica de abrir el minijuego
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
                    
                    // Teletransportar al jugador (mantenemos su Y sumado en 50 para que siga "hundido")
                    java.awt.Point nuevaVent = listaVents.get(ventActualIndex);
                    int miOffsetX = 18; 
                    
                    miJugador.setX(nuevaVent.x - 25 + miOffsetX); 
                    miJugador.setY(nuevaVent.y - 55 + 50);
                    
                    repaint(); // La cámara saltará a la nueva vent
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
    }
    
    public void iniciarPartidaLobby() {
        this.juegoIniciado = true;
        // Contamos cuántos tripulantes vivos hay y lo multiplicamos por los paneles verdes del mapa
        int tripulantes = 0;
        for (Jugador j : jugadoresConectados.values()) {
            if (!j.getEsImpostor()) tripulantes++;
        }
        totalMisionesGlobales = tripulantes * listaMisiones.size();
        if (botonIniciarPartida != null) {
            botonIniciarPartida.setVisible(false); // Escondemos el botón
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
        misMisionesCompletadas = new boolean[listaMisiones.size()];
        System.out.println("✅ Mapa cargado. Tamaño Real: " + anchoReal + "x" + altoReal);
        System.out.println("✅ Spawn corregido en: " + spawnX + "," + spawnY + " | Vents: " + listaVents.size());
    } catch (Exception e) {
        System.out.println("❌ Error cargando el mapa: " + e.getMessage());
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
                
                if (j.getId() == miId && enVentilacion) {
                    if (!animandoVent) continue; // Si ya estoy al fondo, no me dibujo
                    
                    java.awt.Shape clipOriginal = g2.getClip();
                    java.awt.Point v = listaVents.get(ventActualIndex);
                    
                    // Ajustamos el borde del hueco para la tijera de Java
                    // v.y es el centro matemático del punto rojo. Le sumamos 10 para que 
                    // te corte de la cintura para abajo cuando entres.
                    int bordeHuecoY = v.y + 10; 
                    
                    // El primer parámetro es la X. Usamos la X del jugador - 50 para darle margen
                    g2.clipRect((int)j.getX() - 50, 0, 150, bordeHuecoY);
                    
                    j.dibujar(g2, this); 
                    
                    g2.setClip(clipOriginal); 
                } else {
                    // Los demás jugadores se dibujan de forma normal
                    j.dibujar(g2, this);
                }
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
        // Si el juego no ha iniciado, abortamos el movimiento y salimos del método
        if (!juegoIniciado) {
            return; 
        }
        
        if (!jugadoresConectados.containsKey(miId)) return;
        
        Jugador miMuñeco = jugadoresConectados.get(miId);
        
        // --- 1. LÓGICA DE MOVIMIENTO (Calculada por el Panel) ---
        
        // Definimos la velocidad (Píxeles por frame)
        // IMPORTANTE: Si no ponemos velocidad, se moverá a 1 píxel por hora.
        double velocidad = 4.0; 
        
        // Si hay animación de reporte o estoy muerto, no me muevo
        if (mostrandoAnimacionReporte || (modoEspectador && !miMuñeco.isVivo())) {
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
                    
                    // IMPORTANTE: Encontrar la vent consolidada más cercana a pieX, pieY
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
                if (!this.miJugador.getEsImpostor()) botonUse.setEnabled(true);
                botonVent.setEnabled(false);
            } 
            else {
                botonVent.setEnabled(false);
                botonUse.setEnabled(false);
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
        
        if (tecla == KeyEvent.VK_C) {
            // Generamos color random
            int r = (int)(Math.random()*255);
            int g = (int)(Math.random()*255);
            int b = (int)(Math.random()*255);
            
            // 1. Enviamos al servidor: "COLOR,MiID,R,G,B"
            clienteRed.enviar("COLOR," + miId + "," + r + "," + g + "," + b);
            
            // 2. (Opcional) Lo cambiamos localmente de una vez para que sea instantáneo
            actualizarColorJugador(miId, r, g, b);
                repaint();
        
        }
        
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
    
    
    public void realizarMisionAutomatica() {
        if (idMisionCercana != -1 && !misMisionesCompletadas[idMisionCercana]) {
            // 1. La marcamos como hecha en nuestra lista personal
            misMisionesCompletadas[idMisionCercana] = true;
            
            // 2. Apagamos el botón para no repetirla
            botonUsar.setEnabled(false);
            botonUsar.setBackground(Color.LIGHT_GRAY);
            
            System.out.println("✅ ¡Misión " + idMisionCercana + " completada!");
            
            // 3. ¡Le avisamos a todo el mundo por internet que suba la barra!
            clienteRed.enviar("TASK_DONE"); 
        }
    }
    
    public void registrarMisionGlobal() {
        misionesCompletadasGlobales++;
        
        // Calculamos el porcentaje
        int porcentaje = (int) (((double) misionesCompletadasGlobales / totalMisionesGlobales) * 100);
        barraMisiones.setValue(porcentaje);
        
        if (porcentaje >= 100) {
            System.out.println("🎉 ¡VICTORIA DE LOS TRIPULANTES! (Todas las misiones hechas)");
            // Aquí luego pondremos la pantalla de victoria
        }
    }
    
    
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
    }
        
    
    public void eliminarJugador(int id) {
    if (jugadoresConectados.containsKey(id)) {
        jugadoresConectados.remove(id);
        repaint(); // Redibujar para que desaparezca
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

// CORRECCIÓN EN teletransportar
private void teletransportar() {
    if (!jugadoresConectados.containsKey(miId)) return;
    int siguienteVent = (idVentCercana + 1) % listaVents.size();
    int nuevaX = listaVents.get(siguienteVent).x - 25; // Centrar muñeco
    int nuevaY = listaVents.get(siguienteVent).y - 55;
    
    Jugador yo = jugadoresConectados.get(miId);
    yo.setX(nuevaX);
    yo.setY(nuevaY);
    
    // Agregamos las partes faltantes para evitar que el servidor crashée
    String mensaje = "MOV," + miId + "," + nuevaX + "," + nuevaY + "," + yo.isMirandoDerecha() + ",false";
    clienteRed.enviar(mensaje);
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
                
                // 3. Teletransportar A LA MESA (Solo si estoy vivo)
                if (jugadoresConectados.containsKey(miId)) {
                    Jugador yo = jugadoresConectados.get(miId);
                    
                    if (yo.isVivo()) {
                        // Coordenadas mesa
                        int randomX = 350 + (int)(Math.random() * 60); 
                        int randomY = 250 + (int)(Math.random() * 40);
                        
                        yo.setX(randomX);
                        yo.setY(randomY);
                        yo.detener();
                        
                        // Avisar al servidor
                        clienteRed.enviar("MOV," + miId + "," + randomX + "," + randomY);
                    }
                }
                
                // 4. Resetear variables de juego
                idCuerpoCercano = -1;
                
                // Recuperar el foco para poder escribir en el chat (cuando lo hagamos)
                PanelJuego.this.requestFocusInWindow();
                
                // DETENER EL TIMER (Para que no se repita infinitamente)
                ((Timer)e.getSource()).stop(); 
            }
        });
        
        timerAnimacion.setRepeats(false); // Aseguramos que solo suene una vez
        timerAnimacion.start(); // ¡CORRE TIEMPO!
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
    
}