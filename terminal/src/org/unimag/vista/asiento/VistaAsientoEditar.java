package org.unimag.vista.asiento;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
import org.unimag.controlador.asiento.AsientoControladorActualizar;
import org.unimag.controlador.bus.BusControladorListar;
import org.unimag.dto.AsientoDto;
import org.unimag.dto.BusDto;
import org.unimag.recurso.constante.Configuracion;
import org.unimag.recurso.utilidad.Formulario;
import org.unimag.recurso.utilidad.GestorImagen;
import org.unimag.recurso.utilidad.Icono;
import org.unimag.recurso.utilidad.Marco;
import org.unimag.recurso.utilidad.Mensaje;

public class VistaAsientoEditar extends StackPane {

    private static final int H_GAP = 10;
    private static final int V_GAP = 20;
    private static final int ALTO_FILA = 40;
    private static final int ALTO_CAJA = 35;
    private static final int TAMANIO_FUENTE = 20;

    private final GridPane miGrilla;
    private final Rectangle miMarco;
    private final AsientoDto asientoOriginal;
    private final int posicionAsiento;

    private ComboBox<BusDto> cbmBuses;
    private TextField cajaImagen;
    private ImageView imgPorDefecto;
    private ImageView imgPrevisualizar;
    private String rutaImagenSeleccionada;

    public VistaAsientoEditar(Stage esce, double ancho, double alto, AsientoDto asiento, int posicion) {
        this.asientoOriginal = asiento;
        this.posicionAsiento = posicion;
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
        cargarDatosAsiento();
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

        for (int i = 0; i < 7; i++) {
            RowConstraints fila = new RowConstraints();
            fila.setMinHeight(ALTO_FILA);
            fila.setMaxHeight(ALTO_FILA);
            miGrilla.getRowConstraints().add(fila);
        }
    }

    private void crearTitulo() {
        Text miTitulo = new Text("FORMULARIO - EDITAR ASIENTO");
        miTitulo.setFill(Color.web(Configuracion.MORADO_OSCURO));
        miTitulo.setFont(Font.font("Rockwell", FontWeight.BOLD, 28));
        GridPane.setHalignment(miTitulo, HPos.CENTER);
        GridPane.setMargin(miTitulo, new Insets(30, 0, 0, 0));
        miGrilla.add(miTitulo, 0, 0, 3, 1);
    }

    private void crearFormulario() {
        Label lblBus = new Label("Asiento del Bus:");
        lblBus.setFont(Font.font("Times New Roman", FontPosture.ITALIC, TAMANIO_FUENTE));
        miGrilla.add(lblBus, 0, 2);

        List<BusDto> arrBuses = BusControladorListar.obtenerBuses();
        BusDto opcionInicial = new BusDto(0, "", null, "", "", 0, 0);
        arrBuses.add(0, opcionInicial);

        cbmBuses = new ComboBox<>();
        cbmBuses.setMaxWidth(Double.MAX_VALUE);
        cbmBuses.setPrefHeight(ALTO_CAJA);

        ObservableList<BusDto> items = FXCollections.observableArrayList(arrBuses);
        cbmBuses.setItems(items);
        miGrilla.add(cbmBuses, 1, 2);

        Label lblImagen = new Label("Imagen del Asiento");
        lblImagen.setFont(Font.font("Times New Roman", FontPosture.ITALIC, TAMANIO_FUENTE));
        miGrilla.add(lblImagen, 0, 3);

        cajaImagen = new TextField();
        cajaImagen.setDisable(true);
        cajaImagen.setPrefHeight(ALTO_CAJA);

        String[] extensiones = {"*.png", "*.jpg", "*.jpeg"};
        FileChooser objSeleccionar = Formulario.selectorImagen("Selecciona la foto", "Imagenes", extensiones);

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
        miGrilla.add(panelHorizontal, 1, 3);

        imgPorDefecto = Icono.obtenerIcono("imgNoDisponible.png", 150);
        GridPane.setHalignment(imgPorDefecto, HPos.CENTER);
        miGrilla.add(imgPorDefecto, 2, 1, 1, 5);

        Button btnActualizar = new Button("Actualizar Asiento");
        btnActualizar.setTextFill(Color.web(Configuracion.MORADO_OSCURO));
        btnActualizar.setMaxWidth(Double.MAX_VALUE);
        btnActualizar.setFont(Font.font("Times New Roman", TAMANIO_FUENTE));
        btnActualizar.setOnAction((e) -> {
            actualizarAsiento();
        });
        miGrilla.add(btnActualizar, 1, 4);
    }

    private void cargarDatosAsiento() {
        for (BusDto bus : cbmBuses.getItems()) {
            if (bus.getIdBus() == asientoOriginal.getBusAsiento().getIdBus()) {
                cbmBuses.getSelectionModel().select(bus);
                break;
            }
        }

        cajaImagen.setText(asientoOriginal.getNombreImagenPublicoAsiento());
        String imagenPrivada = asientoOriginal.getNombreImagenPrivadoAsiento();
        if (imagenPrivada != null && !imagenPrivada.isEmpty()) {
            try {
                miGrilla.getChildren().remove(imgPorDefecto);
                imgPrevisualizar = Icono.previsualizar("lasFotos/" + imagenPrivada, 150);
                GridPane.setHalignment(imgPrevisualizar, HPos.CENTER);
                miGrilla.add(imgPrevisualizar, 2, 1, 1, 5);
            } catch (Exception e) {
                System.err.println("Error al cargar imagen: " + e.getMessage());
            }
        }
    }

    private void actualizarVistaPrevia() {
        if (rutaImagenSeleccionada.isEmpty()) {
            miGrilla.getChildren().remove(imgPorDefecto);
            if(imgPrevisualizar != null) miGrilla.getChildren().remove(imgPrevisualizar);
            miGrilla.add(imgPorDefecto, 2, 1, 1, 5);
        } else {
            miGrilla.getChildren().remove(imgPorDefecto);
            if(imgPrevisualizar != null) miGrilla.getChildren().remove(imgPrevisualizar);
            imgPrevisualizar = Icono.previsualizar(rutaImagenSeleccionada, 150);
            GridPane.setHalignment(imgPrevisualizar, HPos.CENTER);
            miGrilla.add(imgPrevisualizar, 2, 1, 1, 5);
        }
    }

    private Boolean formularioCompleto() {
        if (cbmBuses.getSelectionModel().getSelectedIndex() == 0) {
            Mensaje.mostrar(Alert.AlertType.WARNING, null, "Alerta", "Escoge un Bus");
            cbmBuses.requestFocus();
            return false;
        }
        return true;
    }

    private void actualizarAsiento() {
        if (formularioCompleto()) {
            AsientoDto dtoActualizado = new AsientoDto();
            dtoActualizado.setIdAsiento(asientoOriginal.getIdAsiento());
            dtoActualizado.setBusAsiento(cbmBuses.getSelectionModel().getSelectedItem());

            if (rutaImagenSeleccionada.isEmpty()) {
                dtoActualizado.setNombreImagenPublicoAsiento(asientoOriginal.getNombreImagenPublicoAsiento());
            } else {
                dtoActualizado.setNombreImagenPublicoAsiento(cajaImagen.getText());
            }

            if (AsientoControladorActualizar.actualizarAsiento(posicionAsiento, dtoActualizado, rutaImagenSeleccionada)) {
                Mensaje.mostrar(Alert.AlertType.INFORMATION, null, "Éxito", "La información ha sido actualizada exitosamente");
            } else {
                Mensaje.mostrar(Alert.AlertType.ERROR, null, "Error", "La información no ha podido ser actualizada");
            }
        }
    }
}
