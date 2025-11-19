package org.unimag.controlador.tiquete;

import org.unimag.dto.TiqueteDto;
import org.unimag.servicio.TiqueteServicio;

public class TiqueteControladorActualizar {
    
    public static Boolean actualizarTiquete(int posicion, TiqueteDto tiquete, String rutaImagen) {
        TiqueteServicio servicio = new TiqueteServicio();
        TiqueteDto resultado = servicio.updateSet(posicion, tiquete, rutaImagen);
        return resultado != null;
    }
}