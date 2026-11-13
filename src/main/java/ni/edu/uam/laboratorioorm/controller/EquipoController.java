package ni.edu.uam.laboratorioorm.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import ni.edu.uam.laboratorioorm.dao.EquipoDAO;
import ni.edu.uam.laboratorioorm.dao.TipoEquipoDAO;
import ni.edu.uam.laboratorioorm.model.Equipo;
import ni.edu.uam.laboratorioorm.model.TipoEquipo;

import java.util.List;

public class EquipoController {

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtMarca;
    @FXML private TextField txtCantidad;
    @FXML private TextField txtBuscarNombre;

    @FXML private ComboBox<TipoEquipo> cmbTipoEquipo;
    @FXML private ComboBox<TipoEquipo> cmbFiltroTipo;
    @FXML private ComboBox<String> cmbEstado;

    @FXML private TableView<Equipo> tblEquipos;

    @FXML private TableColumn<Equipo, String> colCodigo;
    @FXML private TableColumn<Equipo, String> colNombre;
    @FXML private TableColumn<Equipo, String> colMarca;
    @FXML private TableColumn<Equipo, String> colEstado;
    @FXML private TableColumn<Equipo, Integer> colCantidad;
    @FXML private TableColumn<Equipo, TipoEquipo> colTipo;

    private final EquipoDAO equipoDAO = new EquipoDAO();
    private final TipoEquipoDAO tipoEquipoDAO = new TipoEquipoDAO();

    private Long idSeleccionado = null;

    @FXML
    public void initialize() {

        // Relaciona las columnas con los atributos de Equipo.
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoEquipo"));

        // Carga los estados permitidos.
        cmbEstado.setItems(FXCollections.observableArrayList(
                "Disponible",
                "En mantenimiento",
                "Fuera de servicio"
        ));

        // Detecta cuando se selecciona un equipo de la tabla.
        tblEquipos.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, actual) -> {
                    if (actual != null) {
                        seleccionarEquipo(actual);
                    }
                });

        try {
            cargarTipos();
            cargarEquipos();

        } catch (RuntimeException ex) {
            mostrarError("No se pudieron cargar los datos.", ex);
        }
    }

    // Consulta los tipos de equipo para los ComboBox.
    private void cargarTipos() {
        List<TipoEquipo> tipos = tipoEquipoDAO.listar();

        cmbTipoEquipo.setItems(FXCollections.observableArrayList(tipos));
        cmbFiltroTipo.setItems(FXCollections.observableArrayList(tipos));
    }

    // Consulta los equipos y actualiza la tabla.
    @FXML
    private void cargarEquipos() {
        try {
            List<Equipo> equipos = equipoDAO.listar();

            tblEquipos.setItems(FXCollections.observableArrayList(equipos));
            limpiarCampos();

        } catch (RuntimeException ex) {
            mostrarError("No se pudieron cargar los equipos.", ex);
        }
    }

    // Muestra en el formulario los datos del equipo seleccionado.
    private void seleccionarEquipo(Equipo equipo) {
        idSeleccionado = equipo.getId();

        txtCodigo.setText(equipo.getCodigo());
        txtNombre.setText(equipo.getNombre());
        txtMarca.setText(equipo.getMarca() == null ? "" : equipo.getMarca());
        txtCantidad.setText(String.valueOf(equipo.getCantidad()));

        cmbEstado.setValue(equipo.getEstado());
        cmbTipoEquipo.setValue(buscarTipo(equipo.getTipoEquipo()));
    }

    // Busca en el ComboBox el tipo correspondiente por su ID.
    private TipoEquipo buscarTipo(TipoEquipo tipo) {
        if (tipo == null) {
            return null;
        }

        for (TipoEquipo actual : cmbTipoEquipo.getItems()) {
            if (actual.getId().equals(tipo.getId())) {
                return actual;
            }
        }

        return null;
    }

    // Limpia el formulario y la selección de la tabla.
    @FXML
    private void limpiarCampos() {
        txtCodigo.clear();
        txtNombre.clear();
        txtMarca.clear();
        txtCantidad.clear();

        cmbTipoEquipo.setValue(null);
        cmbEstado.setValue(null);

        idSeleccionado = null;
        tblEquipos.getSelectionModel().clearSelection();
    }

    // Muestra los errores de la aplicación mediante Alert.
    private void mostrarError(String mensaje, RuntimeException ex) {
        ex.printStackTrace();

        Alert alerta = new Alert(
                Alert.AlertType.ERROR,
                mensaje + " " + ex.getMessage()
        );

        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
