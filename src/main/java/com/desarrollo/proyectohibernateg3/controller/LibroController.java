package com.desarrollo.proyectohibernateg3.controller;
import com.desarrollo.proyectohibernateg3.dao.LibroDAO;
import com.desarrollo.proyectohibernateg3.model.Libro;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class LibroController {

    @FXML
    private TextField txtTitulo;

    @FXML
    private TextField txtAutor;

    @FXML
    private TextField txtAnio;

    @FXML
    private TextField txtPrecio;

    @FXML
    private Button btnModificar;

    @FXML
    private Button btnEliminar;

    @FXML
    private TableView<Libro> tablaLibros;

    @FXML
    private TableColumn<Libro, Integer> colId;

    @FXML
    private TableColumn<Libro, String> colTitulo;

    @FXML
    private TableColumn<Libro, String> colAutor;

    @FXML
    private TableColumn<Libro, Integer> colAnio;

    @FXML
    private TableColumn<Libro, BigDecimal> colPrecio;

    private final LibroDAO libroDAO = new LibroDAO();

    // null indica que no hay un libro seleccionado.
    private Integer idSeleccionado;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        colTitulo.setCellValueFactory(
                new PropertyValueFactory<>("titulo")
        );

        colAutor.setCellValueFactory(
                new PropertyValueFactory<>("autor")
        );

        colAnio.setCellValueFactory(
                new PropertyValueFactory<>("anioPublicaciones")
        );

        colPrecio.setCellValueFactory(
                new PropertyValueFactory<>("precio")
        );

        tablaLibros.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        seleccionarLibro(newValue);
                    } else {
                        idSeleccionado = null;
                        actualizarBotones();
                    }
                });

        actualizarBotones();
        cargarLibros();
    }

    private void seleccionarLibro(Libro libro) {
        idSeleccionado = libro.getId();

        txtTitulo.setText(libro.getTitulo());
        txtAutor.setText(libro.getAutor());
        txtAnio.setText(String.valueOf(libro.getAnioPublicaciones()));
        txtPrecio.setText(String.valueOf(libro.getPrecio()));

        actualizarBotones();
    }

    private void actualizarBotones() {
        boolean sinSeleccion = idSeleccionado == null;

        btnModificar.setDisable(sinSeleccion);
        btnEliminar.setDisable(sinSeleccion);
    }

    private Libro obtenerLibroFormulario() {
        String titulo = txtTitulo.getText().trim();
        String autor = txtAutor.getText().trim();
        String textoAnio = txtAnio.getText().trim();
        String textoPrecio = txtPrecio.getText().trim();

        if (titulo.isEmpty() || autor.isEmpty()
                || textoAnio.isEmpty() || textoPrecio.isEmpty()) {
            throw new IllegalArgumentException(
                    "Debe completar todos los campos."
            );
        }

        int anio;
        BigDecimal precio;

        try {
            anio = Integer.parseInt(textoAnio);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    "El año debe ser un número entero."
            );
        }

        try {
            precio = new BigDecimal(textoPrecio);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    "El precio debe ser un número válido. "
                            + "Use punto para los decimales, por ejemplo: 150.50."
            );
        }

        if (anio <= 0) {
            throw new IllegalArgumentException(
                    "El año debe ser mayor que cero."
            );
        }

        if (precio.signum() < 0) {
            throw new IllegalArgumentException(
                    "El precio no puede ser negativo."
            );
        }

        return new Libro(titulo, autor, anio, precio);
    }

    @FXML
    private void guardarLibro() {
        try {
            Libro libro = obtenerLibroFormulario();

            LibroDAO.guardar(libro);

            limpiarFormulario();
            cargarLibros();

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Operación realizada",
                    "El libro se guardó correctamente."
            );

        } catch (IllegalArgumentException ex) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Revise los datos",
                    ex.getMessage()
            );

        } catch (Exception ex) {
            mostrarError("No se pudo guardar el libro.", ex);
        }
    }

    @FXML
    private void modificarLibro() {
        if (idSeleccionado == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Seleccione un libro",
                    "Debe seleccionar el libro que desea modificar."
            );
            return;
        }

        try {
            Libro libro = obtenerLibroFormulario();
            libro.setId(idSeleccionado);

            boolean modificado = libroDAO.modificar(libro);

            if (!modificado) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Modificación no realizada",
                        "No se pudo modificar el registro seleccionado."
                );
                return;
            }

            limpiarFormulario();
            cargarLibros();

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Operación realizada",
                    "El libro se modificó correctamente."
            );

        } catch (IllegalArgumentException ex) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Revise los datos",
                    ex.getMessage()
            );

        } catch (Exception ex) {
            mostrarError("No se pudo modificar el libro.", ex);
        }
    }

    @FXML
    private void eliminarLibro() {
        if (idSeleccionado == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Seleccione un libro",
                    "Debe seleccionar el libro que desea eliminar."
            );
            return;
        }

        try {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmación de eliminación");
            confirmacion.setHeaderText("Eliminar libro");
            confirmacion.setContentText(
                    "¿Está seguro que desea eliminar el registro seleccionado?"
            );

            Optional<ButtonType> resultado = confirmacion.showAndWait();

            if (resultado.isEmpty() || resultado.get() != ButtonType.OK) {
                return;
            }

            boolean eliminado = LibroDAO.eliminar(idSeleccionado.longValue());

            if (!eliminado) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Eliminación no realizada",
                        "El registro no existe o no fue eliminado."
                );
                return;
            }

            limpiarFormulario();
            cargarLibros();

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Operación realizada",
                    "El libro se eliminó correctamente."
            );

        } catch (Exception ex) {
            mostrarError("No se pudo eliminar el libro.", ex);
        }
    }

    @FXML
    private void cargarLibros() {
        try {
            List<Libro> libros = libroDAO.listar();

            tablaLibros.setItems(
                    FXCollections.observableArrayList(libros)
            );

            limpiarFormulario();

        } catch (Exception ex) {
            mostrarError("No se pudo cargar la lista de libros.", ex);
        }
    }

    @FXML
    private void limpiarFormulario() {
        tablaLibros.getSelectionModel().clearSelection();

        idSeleccionado = null;

        txtTitulo.clear();
        txtAutor.clear();
        txtAnio.clear();
        txtPrecio.clear();

        actualizarBotones();
    }

    private void mostrarAlerta(
            Alert.AlertType tipo,
            String titulo,
            String mensaje
    ) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarError(String mensaje, Exception ex) {
        ex.printStackTrace();

        String detalle = ex.getMessage();

        mostrarAlerta(
                Alert.AlertType.ERROR,
                "Error",
                mensaje + (
                        detalle == null ? "" : "\nDetalle: " + detalle
                )
        );
    }
}