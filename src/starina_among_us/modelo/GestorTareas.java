package starina_among_us.modelo;

import java.awt.Rectangle;
import java.util.ArrayList;

/**
 * Gestor encargado de almacenar, proveer y validar las misiones (Tareas) del juego.
 * Determina que tareas estan disponibles dependiendo del mapa seleccionado y calcula
 * la barra de progreso global (victoria de los tripulantes).
 * * @author Wulliber Yepez, Carlos Ramirez, Jorg Sierra, Samuel Salazar
 * @version 1.0
 */
public class GestorTareas {
    private ArrayList<Tarea> listaTareas;

    /**
     * Constructor principal que inicializa la lista vacia de misiones.
     */
    public GestorTareas() {
        listaTareas = new ArrayList<>();
    }
    
    /**
     * Configura la lista de tareas disponibles en base al mapa en el que se
     * va a jugar la partida.
     * * @param nombreMapa El nombre del mapa cargado (ej. "Uni", "Salones").
     */
    public void cargarMisionesPorMapa(String nombreMapa) {
        listaTareas.clear(); 
        
        if (nombreMapa.equalsIgnoreCase("Uni")) {
            inicializarTareasMapaUni();
        } else if (nombreMapa.equalsIgnoreCase("Salones")) {
            inicializarTareasMapaSalones();
        }
    }

    private void inicializarTareasMapaUni() {
        listaTareas.add(new Tarea("LAB_FRASCOS", "Beber Quimicos", new Rectangle(570, 890, 95, 70)));
        listaTareas.add(new Tarea("OFI_TRABAJO", "Organizar Trabajo", new Rectangle(515, 1090, 100, 160)));
        listaTareas.add(new Tarea("PIZARRA_MATH", "Quiz de Matematicas", new Rectangle(1690, 845, 150, 40)));
        listaTareas.add(new Tarea("BIBLIO_LIBROS", "Organizar Libros", new Rectangle(1000, 1000, 150, 100)));
    }
    
    private void inicializarTareasMapaSalones() {
        listaTareas.add(new Tarea("SALONES_SERVIDOR", "Reiniciar Servidor", new Rectangle(550, 395, 70, 45)));
        listaTareas.add(new Tarea("SALONES_PC", "Apagar PC", new Rectangle(1300, 850, 145, 120)));
        listaTareas.add(new Tarea("SALONES_AGUA", "Llenar Filtro de Agua", new Rectangle(784, 778, 28, 31)));
        listaTareas.add(new Tarea("SALONES_ESTANTERIA", "Ordenar Estanteria", new Rectangle(1300, 410, 162, 103)));
    }

    /**
     * Cambia el estado de una tarea a 'Completada'. Incluye un seguro para
     * evitar que la misma tarea sume progreso multiples veces si ocurre lag en la red.
     * * @param idTarea Identificador unico de la tarea a registrar.
     * @return true si la tarea se registro con exito; false si ya estaba lista o no existe.
     */
    public boolean registrarTareaCompletada(String idTarea) {
        for (Tarea t : listaTareas) {
            if (t.getId().equals(idTarea)) {
                if (!t.isCompletada()) {
                    t.setCompletada(true);
                    System.out.println("Tarea " + idTarea + " sumada al progreso global.");
                    return true;
                }
                break;
            }
        }
        return false;
    }

    /**
     * Busca una tarea especifica por su ID manual, independientemente de su zona fisica.
     * * @param id El identificador unico a buscar.
     * @return El objeto Tarea correspondiente, o nulo si no existe.
     */
    public Tarea getTareaPorId(String id) {
        for (Tarea t : listaTareas) {
            if (t.getId().equals(id)) return t;
        }
        return null;
    }

    /**
     * Calcula el porcentaje actual de tareas terminadas por los tripulantes.
     * * @return Un valor flotante entre 0.0 y 1.0.
     */
    public float obtenerPorcentajeProgreso() {
        if (listaTareas.isEmpty()) return 0;
        int completadas = 0;
        for (Tarea t : listaTareas) {
            if (t.isCompletada()) completadas++;
        }
        return (float) completadas / listaTareas.size();
    }
    
    /**
     * @return La lista completa de tareas configuradas en la partida actual.
     */
    public ArrayList<Tarea> getListaTareas() { return listaTareas; }

    /**
     * Detecta si las coordenadas dadas (usualmente los pies del jugador local)
     * se encuentran dentro del rango de accion interactuable de alguna tarea no completada.
     * * @param jugadorX Coordenada X del jugador.
     * @param jugadorY Coordenada Y del jugador.
     * @return La Tarea con la que se hizo colision, o nulo si el jugador no esta sobre ninguna.
     */
    public Tarea obtenerTareaEnZona(int jugadorX, int jugadorY) {
        for (Tarea t : listaTareas) {
            if (!t.isCompletada() && t.getZonaActiva() != null && t.getZonaActiva().contains(jugadorX, jugadorY)) {
                return t;
            }
        }
        return null;
    }
}