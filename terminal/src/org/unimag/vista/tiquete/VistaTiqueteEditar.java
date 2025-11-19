package org.unimag.vista.tiquete;

import java.util.List;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.unimag.controlador.asiento.AsientoControladorListar;
import org.unimag.controlador.pasajero.PasajeroControladorListar;
import org.unimag.controlador.tiquete.TiqueteControladorActualizar;
import org.unimag.controlador.viaje.ViajeControladorListar;
import org.unimag.dto.AsientoDto;
import org.unimag.dto.PasajeroDto;
import org.unimag.dto.TiqueteDto;
import org.unimag.dto.ViajeDto;
import org.unimag.recurso.constante.Configuracion;
import org.unimag.recurso.utilidad.Formulario;
import org.unimag.recurso.utilidad.GestorImagen;
import org.unimag.recurso.utilidad.Icono;
import org.unimag.recurso.utilidad.Marco;
import org.unimag.recurso.utilidad.Mensaje;

public class VistaTiqueteEditar extends StackPane {

    private static final int H_GAP = 10;
    private static final int V_GAP = 20;
    private static final int ALTO_FILA = 40;
    private static final int ALTO_CAJA = 35;
    private static final int TAMANIO_FUENTE = 20;

    private final GridPane miGrilla;
    private final Rectangle miMarco;
    private final TiqueteDto tiqueteOriginal;
    private final int posicionTiquete;

    private ComboBox<PasajeroDto> cmbPasajero;
    private ComboBox<ViajeDto> cmbViaje;
    private ComboBox<AsientoDto> cmbAsiento;
    private RadioButton rbActivo;
    private RadioButton rbInactivo;
    private ToggleGroup grupoEstado;
    private TextField cajaImagen;
    private ImageView imgPorDefecto;
    private ImageView imgPrevisualizar;
    private String rutaImagenSeleccionada;
    
    private List<AsientoDto> todosLosAsientos; // Lista completa de asientos
    private int asientoOriginalId; // ID del asiento original del tiquete

    public VistaTiqueteEditar(Stage esce, double ancho, double alto, TiqueteDto tiquete, int posicion) {
        this.tiqueteOriginal = tiquete;
        this.posicionTiquete = posicion;
        rutaImagenSeleccionada = "";
        
        setAlignment(Pos.CENTER);

        miGrilla = new GridPane();
        miGrilla.setAlignment(Pos.CENTER);

        miMarco = Marco.crear(esce, 
                Configuracion.MARCO_ALTO_PORCENTAJE,
                Configuracion.MARCO_ANCHO_PORCENTAJE,
                Configuracion.DEGRADE_ARREGLO_GENERO,
                Configuracion.DEGRADE_BORDE);

        getChildren().add(miMarco);

        configurarMiGrilla(ancho, alto);
        crearTitulo();
        crearFormulario();
        cargarDatosTiquete();
        getChildren().add(miGrilla);
    }

    private void configurarMiGrilla(double ancho, double alto) {
        double miAnchoGrilla = ancho * Configuracion.GRILLA_ANCHO_PORCENTAJE;
        miGrilla.setHgap(H_GAP);
        miGrilla.setVgap(V_GAP);
        miGrilla.setPrefSize(miAnchoGrilla, alto);
        miGrilla.setMinSize(miAnchoGrilla, alto);
        miGrilla.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        ColumnConstraints col0 = new ColumnConstraints();
        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints col2 = new ColumnConstraints();
        col0.setPrefWidth(200);
        col1.setPrefWidth(200);
        col2.setPrefWidth(200);
        col1.setHgrow(Priority.ALWAYS);
        
        miGrilla.getColumnConstraints().addAll(col0, col1, col2);

        for (int i = 0; i < 10; i++) {
            RowConstraints fila = new RowConstraints();
            fila.setMinHeight(ALTO_FILA);
            fila.setMaxHeight(ALTO_FILA);
            miGrilla.getRowConstraints().add(fila);
        }
    }

    private void crearTitulo() {
        Text miTitulo = new Text("FORMULARIO - EDITAR TIQUETE");
        miTitulo.setFill(Color.web(Configuracion.MORADO_OSCURO));
        miTitulo.setFont(Font.font("Rockwell", FontWeight.BOLD, 28));
        GridPane.setHalignment(miTitulo, HPos.CENTER);
        GridPane.setMargin(miTitulo, new Insets(30, 0, 0, 0));
        miGrilla.add(miTitulo, 0, 0, 3, 1);
    }

    private void crearFormulario() {
        Label lblPasajero = new Label("Pasajero");
        lblPasajero.setFont(Font.font("Times New Roman", FontPosture.ITALIC, TAMANIO_FUENTE));
        miGrilla.add(lblPasajero, 0, 2);

        cmbPasajero = new ComboBox<>();
        List<PasajeroDto> pasajeros = PasajeroControladorListar.obtenerPasajeros();
        cmbPasajero.getItems().addAll(pasajeros);
        cmbPasajero.setPromptText("Selecciona un pasajero");
        GridPane.setHgrow(cmbPasajero, Priority.ALWAYS);
        cmbPasajero.setPrefHeight(ALTO_CAJA);
        miGrilla.add(cmbPasajero, 1, 2);

        Label lblViaje = new Label("Viaje");
        lblViaje.setFont(Font.font("Times New Roman", FontPosture.ITALIC, TAMANIO_FUENTE));
        miGrilla.add(lblViaje, 0, 3);

        cmbViaje = new ComboBox<>();
        List<ViajeDto> viajes = ViajeControladorListar.obtenerViajes();
        cmbViaje.getItems().addAll(viajes);
        cmbViaje.setPromptText("Selecciona un viaje");
        GridPane.setHgrow(cmbViaje, Priority.ALWAYS);
        cmbViaje.setPrefHeight(ALTO_CAJA);
        miGrilla.add(cmbViaje, 1, 3);

        Label lblAsiento = new Label("Asiento");
        lblAsiento.setFont(Font.font("Times New Roman", FontPosture.ITALIC, TAMANIO_FUENTE));
        miGrilla.add(lblAsiento, 0, 4);

        cmbAsiento = new ComboBox<>();
        List<AsientoDto> asientos = AsientoControladorListar.obtenerAsientos();
        cmbAsiento.getItems().addAll(asientos);
        cmbAsiento.setPromptText("Selecciona un asiento");
        GridPane.setHgrow(cmbAsiento, Priority.ALWAYS);
        cmbAsiento.setPrefHeight(ALTO_CAJA);
        miGrilla.add(cmbAsiento, 1, 4);

        Label lblEstado = new Label("Estado del Tiquete");
        lblEstado.setFont(Font.font("Times New Roman", FontPosture.ITALIC, TAMANIO_FUENTE));
        miGrilla.add(lblEstado, 0, 5);

        grupoEstado = new ToggleGroup();
        
        rbActivo = new RadioButton("Activo");
        rbActivo.setToggleGroup(grupoEstado);
        rbActivo.setFont(Font.font("Times New Roman", TAMANIO_FUENTE - 2));
        rbActivo.setTextFill(Color.GREEN);
        
        rbInactivo = new RadioButton("Inactivo");
        rbInactivo.setToggleGroup(grupoEstado);
        rbInactivo.setFont(Font.font("Times New Roman", TAMANIO_FUENTE - 2));
        rbInactivo.setTextFill(Color.RED);

        HBox cajaEstado = new HBox(20);
        cajaEstado.setAlignment(Pos.CENTER_LEFT);
        cajaEstado.getChildren().addAll(rbActivo, rbInactivo);
        miGrilla.add(cajaEstado, 1, 5);

        Label lblImagen = new Label("Imagen del tiquete");
        lblImagen.setFont(Font.font("Times New Roman", FontPosture.ITALIC, TAMANIO_FUENTE));
        miGrilla.add(lblImagen, 0, 6);

        cajaImagen = new TextField();
        cajaImagen.setDisable(true);
        cajaImagen.setPrefHeight(ALTO_CAJA);
        
        String[] extensiones = {"*.png", "*.jpg", "*.jpeg"};
        FileChooser objSeleccionar = Formulario.selectorImagen(
                "Selecciona la foto", "Imagenes", extensiones);
        
        Button btnSeleccionarImagen = new Button("+");
        btnSeleccionarImagen.setPrefHeight(ALTO_CAJA);
        btnSeleccionarImagen.setOnAction((e) -> {
            rutaImagenSeleccionada = GestorImagen.obtenerRutaImagen(cajaImagen, objSeleccionar);
            actualizarVistaPrevia();
        });

        HBox.setHgrow(cajaImagen, Priority.ALWAYS);
        HBox panelHorizontal = new HBox(2);
        panelHorizontal.setAlignment(Pos.BOTTOM_RIGHT);
        panelHorizontal.getChildren().addAll(cajaImagen, btnSeleccionarImagen);
        miGrilla.add(panelHorizontal, 1, 6);

        // Vista previa imagen
        imgPorDefecto = Icono.obtenerIcono("imgNoDisponible.png", 150);
        GridPane.setHalignment(imgPorDefecto, HPos.CENTER);
        GridPane.setValignment(imgPorDefecto, VPos.CENTER);
        miGrilla.add(imgPorDefecto, 2, 1, 1, 8);

        // Botón Actualizar
        Button btnActualizar = new Button("Actualizar Tiquete");
        btnActualizar.setTextFill(Color.web(Configuracion.MORADO_OSCURO));
        btnActualizar.setMaxWidth(Double.MAX_VALUE);
        btnActualizar.setFont(Font.font("Times New Roman", TAMANIO_FUENTE));
        btnActualizar.setOnAction((e) -> {
            actualizarTiquete();
        });
        miGrilla.add(btnActualizar, 1, 8);
    }

    private void cargarDatosTiquete() {
        for (PasajeroDto pasajero : cmbPasajero.getItems()) {
            if (pasajero.getIdPasajero() == tiqueteOriginal.getPasajeroTiquete().getIdPasajero()) {
                cmbPasajero.setValue(pasajero);
                break;
            }
        }
        
        for (ViajeDto viaje : cmbViaje.getItems()) {
            if (viaje.getIdViaje() == tiqueteOriginal.getViajeTiquete().getIdViaje()) {
                cmbViaje.setValue(viaje);
                break;
            }
        }
        
        for (AsientoDto asiento : cmbAsiento.getItems()) {
            if (asiento.getIdAsiento() == tiqueteOriginal.getAsientoTiquete().getIdAsiento()) {
                cmbAsiento.setValue(asiento);
                break;
            }
        }
        
        if (tiqueteOriginal.getEstadoTiquete()) {
            rbActivo.setSelected(true);
        } else {
            rbInactivo.setSelected(true);
        }
        
        cajaImagen.setText(tiqueteOriginal.getNombreImagenPublicoTiquete());
        
        String imagenPrivada = tiqueteOriginal.getNombreImagenPrivadoTiquete();
        if (imagenPrivada != null && !imagenPrivada.isEmpty()) {
            try {
                miGrilla.getChildren().remove(imgPorDefecto);
                imgPrevisualizar = Icono.previsualizar("lasFotos/" + imagenPrivada, 150);
                GridPane.setHalignment(imgPrevisualizar, HPos.CENTER);
                miGrilla.add(imgPrevisualizar, 2, 1, 1, 8);
            } catch (Exception e) {
                System.err.println("Error al cargar imagen: " + e.getMessage());
            }
        }
    }

    private void actualizarVistaPrevia() {
        if (rutaImagenSeleccionada.isEmpty()) {
            miGrilla.getChildren().remove(imgPorDefecto);
            miGrilla.getChildren().remove(imgPrevisualizar);
            miGrilla.add(imgPorDefecto, 2, 1, 1, 8);
        } else {
            miGrilla.getChildren().remove(imgPorDefecto);
            miGrilla.getChildren().remove(imgPrevisualizar);
            imgPrevisualizar = Icono.previsualizar(rutaImagenSeleccionada, 150);
            GridPane.setHalignment(imgPrevisualizar, HPos.CENTER);
            miGrilla.add(imgPrevisualizar, 2, 1, 1, 8);
        }
    }

    private Boolean formularioCompleto() {
        if (cmbPasajero.getValue() == null) {
            Mensaje.mostrar(Alert.AlertType.WARNING, this.getScene().getWindow(), 
                    "Alerta", "Selecciona un pasajero");
            cmbPasajero.requestFocus();
            return false;
        }
        
        if (cmbViaje.getValue() == null) {
            Mensaje.mostrar(Alert.AlertType.WARNING, this.getScene().getWindow(), 
                    "Alerta", "Selecciona un viaje");
            cmbViaje.requestFocus();
            return false;
        }
        
        if (cmbAsiento.getValue() == null) {
            Mensaje.mostrar(Alert.AlertType.WARNING, this.getScene().getWindow(), 
                    "Alerta", "Selecciona un asiento");
            cmbAsiento.requestFocus();
            return false;
        }
        
        if (grupoEstado.getSelectedToggle() == null) {
            Mensaje.mostrar(Alert.AlertType.WARNING, this.getScene().getWindow(), 
                    "Alerta", "Selecciona el estado del tiquete");
            return false;
        }
        
        return true;
    }

    private void actualizarTiquete() {
        if (formularioCompleto()) {
            TiqueteDto dtoActualizado = new TiqueteDto();
            dtoActualizado.setIdTiquete(tiqueteOriginal.getIdTiquete());
            dtoActualizado.setPasajeroTiquete(cmbPasajero.getValue());
            dtoActualizado.setViajeTiquete(cmbViaje.getValue());
            dtoActualizado.setAsientoTiquete(cmbAsiento.getValue());
            dtoActualizado.setEstadoTiquete(rbActivo.isSelected());
            
            if (rutaImagenSeleccionada.isEmpty()) {
                dtoActualizado.setNombreImagenPublicoTiquete(tiqueteOriginal.getNombreImagenPublicoTiquete());
            } else {
                dtoActualizado.setNombreImagenPublicoTiquete(cajaImagen.getText());
            }

            if (TiqueteControladorActualizar.actualizarTiquete(posicionTiquete, dtoActualizado, rutaImagenSeleccionada)) {
                Mensaje.mostrar(Alert.AlertType.INFORMATION, null, 
                        "Éxito", "La información ha sido actualizada exitosamente");
            } else {
                Mensaje.mostrar(Alert.AlertType.ERROR, null, 
                        "Error", "La información no ha podido ser actualizada");
            }
        }
    }
}