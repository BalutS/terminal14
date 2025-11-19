package org.unimag.controlador.asiento;

import org.unimag.dto.AsientoDto;
import org.unimag.modelo.Asiento;
import org.unimag.servicio.AsientoServicio;

public class AsientoControladorActualizar {

   public static Boolean actualizarAsiento(int posicion, AsientoDto asiento, String rutaImagen) {
        AsientoServicio servicio = new AsientoServicio();
        AsientoDto resultado = servicio.updateSet(posicion, asiento, rutaImagen);
        return resultado != null;
    }
}
