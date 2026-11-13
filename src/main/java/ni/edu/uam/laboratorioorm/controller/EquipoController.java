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
import java.util.Optional;

public class EquipoController {

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtMarca;
    @FXML private TextField txtCantidad;
    @FXML private TextField txtBuscarNombre;

    @FXML private ComboBox<TipoEquipo> cmbTipoEquipo;
    @FXML private ComboBox<TipoEquipo> cmbFiltroTipo;
    @FXML private ComboBox<String> cmbEstado;

    @FXML private Button btnGuardar;
    @FXML private Button btnActualizar;
    @FXML private Button btnEliminar;

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

        // Relaciona las columnas con las propiedades de Equipo.
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoEquipo"));

        // Estados permitidos para los equipos.
        cmbEstado.setItems(FXCollections.observableArrayList(
                "Disponible",
                "En mantenimiento",
                "Fuera de servicio"
        ));

        // Detecta la selección de un equipo en la tabla.
        tblEquipos.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, actual) -> {
                    if (actual != null) {
                        seleccionarEquipo(actual);
                    }
                });

        actualizarBotones();
        cargarEquipos();
    }

    // Consulta los tipos disponibles.
    private void cargarTipos() {
        List<TipoEquipo> tipos = tipoEquipoDAO.listar();

        cmbTipoEquipo.setItems(FXCollections.observableArrayList(tipos));
        cmbFiltroTipo.setItems(FXCollections.observableArrayList(tipos));
    }

    // Recupera todos los equipos y restablece los filtros.
    private void actualizarListado() {
        cargarTipos();

        txtBuscarNombre.clear();
        cmbFiltroTipo.setValue(null);

        List<Equipo> equipos = equipoDAO.listar();
        tblEquipos.setItems(FXCollections.observableArrayList(equipos));

        limpiarCampos();
    }

    @FXML
    private void cargarEquipos() {
        try {
            actualizarListado();
        } catch (RuntimeException ex) {
            mostrarError("No se pudieron cargar los equipos.", ex);
        }
    }

    // Busca por nombre, tipo de equipo o ambos.
    @FXML
    private void aplicarFiltros() {
        String nombre = txtBuscarNombre.getText().trim();
        TipoEquipo tipo = cmbFiltroTipo.getValue();

        try {
            List<Equipo> equipos = equipoDAO.buscar(nombre, tipo);
            tblEquipos.setItems(FXCollections.observableArrayList(equipos));
            limpiarCampos();

        } catch (RuntimeException ex) {
            mostrarError("No se pudieron aplicar los filtros.", ex);
        }
    }

    // Elimina los criterios de búsqueda y muestra todos los equipos.
    @FXML
    private void limpiarFiltros() {
        txtBuscarNombre.clear();
        cmbFiltroTipo.setValue(null);
        cargarEquipos();
    }

    // Registra un equipo nuevo.
    @FXML
    private void guardarEquipo() {
        Equipo equipo = obtenerFormulario();

        if (equipo == null) {
            return;
        }

        try {
            equipoDAO.guardar(equipo);
        } catch (RuntimeException ex) {
            mostrarError("No se pudo guardar el equipo.", ex);
            return;
        }

        finalizarOperacion("Equipo guardado correctamente.");
    }

    // Modifica el equipo seleccionado.
    @FXML
    private void actualizarEquipo() {
        if (idSeleccionado == null) {
            return;
        }

        Equipo equipo = obtenerFormulario();

        if (equipo == null) {
            return;
        }

        equipo.setId(idSeleccionado);

        try {
            equipoDAO.actualizar(equipo);
        } catch (RuntimeException ex) {
            mostrarError("No se pudo actualizar el equipo.", ex);
            return;
        }

        finalizarOperacion("Equipo actualizado correctamente.");
    }

    // Elimina un equipo después de solicitar confirmación.
    @FXML
    private void eliminarEquipo() {
        if (idSeleccionado == null) {
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("Eliminar equipo");
        confirmacion.setContentText(
                "¿Está seguro de eliminar el equipo seleccionado?"
        );

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isEmpty() || resultado.get() != ButtonType.OK) {
            return;
        }

        try {
            boolean eliminado = equipoDAO.eliminar(idSeleccionado);

            if (!eliminado) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "El equipo ya no existe en la base de datos."
                );
                return;
            }

        } catch (RuntimeException ex) {
            mostrarError("No se pudo eliminar el equipo.", ex);
            return;
        }

        finalizarOperacion("Equipo eliminado correctamente.");
    }

    // Valida los campos y construye la entidad Equipo.
    private Equipo obtenerFormulario() {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String marca = txtMarca.getText().trim();

        if (codigo.isEmpty() || nombre.isEmpty()) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "El código y el nombre son obligatorios."
            );
            return null;
        }

        if (codigo.length() > 30 || nombre.length() > 150
                || marca.length() > 100) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Revise la longitud del código, nombre o marca."
            );
            return null;
        }

        int cantidad;

        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
        } catch (NumberFormatException ex) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "La cantidad debe ser un número entero."
            );
            return null;
        }

        if (cantidad < 0) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "La cantidad no puede ser negativa."
            );
            return null;
        }

        TipoEquipo tipo = cmbTipoEquipo.getValue();

        if (tipo == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Debe seleccionar un tipo de equipo."
            );
            return null;
        }

        String estado = cmbEstado.getValue();

        if (estado == null || !cmbEstado.getItems().contains(estado)) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Debe seleccionar un estado válido."
            );
            return null;
        }

        Equipo equipo = new Equipo();
        equipo.setCodigo(codigo);
        equipo.setNombre(nombre);
        equipo.setMarca(marca.isEmpty() ? null : marca);
        equipo.setCantidad(cantidad);
        equipo.setEstado(estado);
        equipo.setTipoEquipo(tipo);

        return equipo;
    }

    // Coloca los datos seleccionados en el formulario.
    private void seleccionarEquipo(Equipo equipo) {
        idSeleccionado = equipo.getId();

        txtCodigo.setText(equipo.getCodigo());
        txtNombre.setText(equipo.getNombre());
        txtMarca.setText(equipo.getMarca() == null ? "" : equipo.getMarca());
        txtCantidad.setText(String.valueOf(equipo.getCantidad()));

        cmbEstado.setValue(equipo.getEstado());
        cmbTipoEquipo.setValue(buscarTipo(equipo.getTipoEquipo()));

        actualizarBotones();
    }

    // Encuentra el tipo equivalente dentro del ComboBox.
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

    // Actualizar y Eliminar requieren una selección; Guardar siempre está disponible.
    private void actualizarBotones() {
        boolean sinSeleccion = idSeleccionado == null;

        btnGuardar.setDisable(false);
        btnActualizar.setDisable(sinSeleccion);
        btnEliminar.setDisable(sinSeleccion);
    }

    // Limpia el formulario y elimina la selección actual.
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

        actualizarBotones();
    }

    // Recarga los datos después de una operación exitosa.
    private void finalizarOperacion(String mensaje) {
        try {
            actualizarListado();
            mostrarAlerta(Alert.AlertType.INFORMATION, mensaje);

        } catch (RuntimeException ex) {
            mostrarError(
                    "La operación se completó, pero no se pudo actualizar la tabla.",
                    ex
            );
        }
    }

    // Registra los errores en consola y los muestra al usuario.
    private void mostrarError(String mensaje, RuntimeException ex) {
        ex.printStackTrace();

        String detalle = ex.getMessage();
        mostrarAlerta(
                Alert.AlertType.ERROR,
                detalle == null ? mensaje : mensaje + "\n" + detalle
        );
    }

    // Muestra mensajes mediante Alert.
    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alerta = new Alert(tipo, mensaje);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
