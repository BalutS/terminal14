package org.unimag.vista.bus;

import java.util.List;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
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
import org.unimag.controlador.bus.BusControladorActualizar;
import org.unimag.controlador.empresa.EmpresaControladorListar;
import org.unimag.dto.BusDto;
import org.unimag.dto.EmpresaDto;
import org.unimag.recurso.constante.Configuracion;
import org.unimag.recurso.utilidad.Formulario;
import org.unimag.recurso.utilidad.GestorImagen;
import org.unimag.recurso.utilidad.Icono;
import org.unimag.recurso.utilidad.Marco;
import org.unimag.recurso.utilidad.Mensaje;

public class VistaBusEditar extends StackPane {

    private static final int H_GAP = 10;
    private static final int V_GAP = 20;
    private static final int ALTO_FILA = 40;
    private static final int ALTO_CAJA = 35;
    private static final int TAMANIO_FUENTE = 20;
    private static final double ANCHO = 0.8;

    private final GridPane miGrilla;
    private final Rectangle miMarco;
    private final BusDto busOriginal;
    private final int posicionBus;

    private TextField txtModeloBus;
    private ComboBox<EmpresaDto> cmbEmpresa;
    private TextField cajaImagen;
    private ImageView imgPorDefecto;
    private ImageView imgPrevisualizar;
    private String rutaImagenSeleccionada;

    public VistaBusEditar(Stage esce, double ancho, double alto, BusDto bus, int posicion) {
        this.busOriginal = bus;
        this.posicionBus = posicion;
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
        cargarDatosBus();
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

        for (int i = 0; i < 8; i++) {
            RowConstraints fila = new RowConstraints();
            fila.setMinHeight(ALTO_FILA);
            fila.setMaxHeight(ALTO_FILA);
            miGrilla.getRowConstraints().add(fila);
        }
    }

    private void crearTitulo() {
        Text miTitulo = new Text("FORMULARIO - EDITAR BUS");
        miTitulo.setFill(Color.web(Configuracion.MORADO_OSCURO));
        miTitulo.setFont(Font.font("Rockwell", FontWeight.BOLD, 28));
        GridPane.setHalignment(miTitulo, HPos.CENTER);
        GridPane.setMargin(miTitulo, new Insets(30, 0, 0, 0));
        miGrilla.add(miTitulo, 0, 0, 3, 1);
    }

    private void crearFormulario() {
        // Campo Modelo
        Label lblModelo = new Label("Modelo del Bus");
        lblModelo.setFont(Font.font("Times New Roman", FontPosture.ITALIC, TAMANIO_FUENTE));
        miGrilla.add(lblModelo, 0, 2);

        txtModeloBus = new TextField();
        txtModeloBus.setPromptText("Digita el modelo");
        GridPane.setHgrow(txtModeloBus, Priority.ALWAYS);
        txtModeloBus.setPrefHeight(ALTO_CAJA);
        miGrilla.add(txtModeloBus, 1, 2);

        // Campo Empresa
        Label lblEmpresa = new Label("Empresa");
        lblEmpresa.setFont(Font.font("Times New Roman", FontPosture.ITALIC, TAMANIO_FUENTE));
        miGrilla.add(lblEmpresa, 0, 3);

        cmbEmpresa = new ComboBox<>();
        List<EmpresaDto> empresas = EmpresaControladorListar.obtenerEmpresas();
        cmbEmpresa.getItems().addAll(empresas);
        cmbEmpresa.setPromptText("Selecciona una empresa");
        GridPane.setHgrow(cmbEmpresa, Priority.ALWAYS);
        cmbEmpresa.setPrefHeight(ALTO_CAJA);
        miGrilla.add(cmbEmpresa, 1, 3);

        // Campo Imagen
        Label lblImagen = new Label("Imagen del bus");
        lblImagen.setFont(Font.font("Times New Roman", FontPosture.ITALIC, TAMANIO_FUENTE));
        miGrilla.add(lblImagen, 0, 4);

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
        miGrilla.add(panelHorizontal, 1, 4);

        // Vista previa imagen
        imgPorDefecto = Icono.obtenerIcono("imgNoDisponible.png", 150);
        GridPane.setHalignment(imgPorDefecto, HPos.CENTER);
        GridPane.setValignment(imgPorDefecto, VPos.CENTER);
        miGrilla.add(imgPorDefecto, 2, 1, 1, 6);

        // Botón Actualizar
        Button btnActualizar = new Button("Actualizar Bus");
        btnActualizar.setTextFill(Color.web(Configuracion.MORADO_OSCURO));
        btnActualizar.setMaxWidth(Double.MAX_VALUE);
        btnActualizar.setFont(Font.font("Times New Roman", TAMANIO_FUENTE));
        btnActualizar.setOnAction((e) -> {
            actualizarBus();
        });
        miGrilla.add(btnActualizar, 1, 6);
    }

    private void cargarDatosBus() {
        txtModeloBus.setText(busOriginal.getModeloBus());
        
        // Seleccionar la empresa en el ComboBox
        for (EmpresaDto empresa : cmbEmpresa.getItems()) {
            if (empresa.getIdEmpresa() == busOriginal.getEmpresaBus().getIdEmpresa()) {
                cmbEmpresa.setValue(empresa);
                break;
            }
        }
        
        cajaImagen.setText(busOriginal.getNombreImagenPublicoBus());
        
        String imagenPrivada = busOriginal.getNombreImagenPrivadoBus();
        if (imagenPrivada != null && !imagenPrivada.isEmpty()) {
            try {
                miGrilla.getChildren().remove(imgPorDefecto);
                imgPrevisualizar = Icono.previsualizar("lasFotos/" + imagenPrivada, 150);
                GridPane.setHalignment(imgPrevisualizar, HPos.CENTER);
                miGrilla.add(imgPrevisualizar, 2, 1, 1, 6);
            } catch (Exception e) {
                System.err.println("Error al cargar imagen: " + e.getMessage());
            }
        }
    }

    private void actualizarVistaPrevia() {
        if (rutaImagenSeleccionada.isEmpty()) {
            miGrilla.getChildren().remove(imgPorDefecto);
            miGrilla.getChildren().remove(imgPrevisualizar);
            miGrilla.add(imgPorDefecto, 2, 1, 1, 6);
        } else {
            miGrilla.getChildren().remove(imgPorDefecto);
            miGrilla.getChildren().remove(imgPrevisualizar);
            imgPrevisualizar = Icono.previsualizar(rutaImagenSeleccionada, 150);
            GridPane.setHalignment(imgPrevisualizar, HPos.CENTER);
            miGrilla.add(imgPrevisualizar, 2, 1, 1, 6);
        }
    }

    private Boolean formularioCompleto() {
        if (txtModeloBus.getText().isBlank()) {
            Mensaje.mostrar(Alert.AlertType.WARNING, this.getScene().getWindow(), 
                    "Alerta", "Agrega un modelo");
            txtModeloBus.requestFocus();
            return false;
        }
        
        if (cmbEmpresa.getValue() == null) {
            Mensaje.mostrar(Alert.AlertType.WARNING, this.getScene().getWindow(), 
                    "Alerta", "Selecciona una empresa");
            cmbEmpresa.requestFocus();
            return false;
        }
        
        return true;
    }

    private void actualizarBus() {
        if (formularioCompleto()) {
            BusDto dtoActualizado = new BusDto();
            dtoActualizado.setIdBus(busOriginal.getIdBus());
            dtoActualizado.setModeloBus(txtModeloBus.getText());
            dtoActualizado.setEmpresaBus(cmbEmpresa.getValue());
            
            if (rutaImagenSeleccionada.isEmpty()) {
                dtoActualizado.setNombreImagenPublicoBus(busOriginal.getNombreImagenPublicoBus());
            } else {
                dtoActualizado.setNombreImagenPublicoBus(cajaImagen.getText());
            }

            if (BusControladorActualizar.actualizarBus(posicionBus, dtoActualizado, rutaImagenSeleccionada)) {
                Mensaje.mostrar(Alert.AlertType.INFORMATION, null, 
                        "Éxito", "La información ha sido actualizada exitosamente");
            } else {
                Mensaje.mostrar(Alert.AlertType.ERROR, null, 
                        "Error", "La información no ha podido ser actualizada");
            }
        }
    }
}