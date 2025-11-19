package org.unimag.controlador.bus;

import org.unimag.dto.BusDto;
import org.unimag.servicio.BusServicio;

public class BusControladorActualizar {
    
    public static Boolean actualizarBus(int posicion, BusDto bus, String rutaImagen) {
        BusServicio servicio = new BusServicio();
        BusDto resultado = servicio.updateSet(posicion, bus, rutaImagen);
        return resultado != null;
    }
}