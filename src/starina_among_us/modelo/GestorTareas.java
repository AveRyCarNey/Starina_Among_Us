package starina_among_us.modelo;

import java.awt.Rectangle;
import java.util.ArrayList;

public class GestorTareas {
    private ArrayList<Tarea> listaTareas;
    // Ya no necesitamos la variable tareasCompletadasGlobales aquí, 
    // la calcularemos en vivo para que sea exacta.

    public GestorTareas() {
        listaTareas = new ArrayList<>();
        inicializarTareasMapaUni();
    }

    private void inicializarTareasMapaUni() {
        // Misión 1: Laboratorio
        listaTareas.add(new Tarea("LAB_FRASCOS", "Beber Químicos", new Rectangle(570, 890, 95, 70)));
        // Misión 2: Oficina
        listaTareas.add(new Tarea("OFI_TRABAJO", "Organizar Trabajo", new Rectangle(515, 1090, 100, 160)));
        // Misión 3: Pizarra
        listaTareas.add(new Tarea("PIZARRA_MATH", "Quiz de Matemáticas", new Rectangle(1690, 845, 150, 40)));
        // Misión 4: Biblioteca
        listaTareas.add(new Tarea("BIBLIO_LIBROS", "Organizar Libros", new Rectangle(1000, 1000, 150, 100)));
    }

    // --- NUEVO MÉTODO DE SEGURIDAD ---
    // Solo marca la tarea si NO estaba completada y devuelve 'true' solo si es un cambio nuevo
    public boolean registrarTareaCompletada(String idTarea) {
        for (Tarea t : listaTareas) {
            if (t.getId().equals(idTarea)) {
                if (!t.isCompletada()) {
                    t.setCompletada(true);
                    System.out.println("⭐ Tarea " + idTarea + " sumada al progreso global.");
                    return true;
                }
                break;
            }
        }
        return false;
    }

    public float obtenerPorcentajeProgreso() {
        int completadas = 0;
        for (Tarea t : listaTareas) {
            if (t.isCompletada()) completadas++;
        }
        // El progreso es: (tareas hechas) / (total de tareas en la lista)
        return (float) completadas / listaTareas.size();
    }
    
    public ArrayList<Tarea> getListaTareas() { return listaTareas; }

    public Tarea obtenerTareaEnZona(int jugadorX, int jugadorY) {
        for (Tarea t : listaTareas) {
            if (!t.isCompletada() && t.getZonaActiva().contains(jugadorX, jugadorY)) {
                return t;
            }
        }
        return null;
    }
}