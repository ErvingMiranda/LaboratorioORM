module ni.edu.uam.laboratorioorm {
    requires javafx.controls;
    requires javafx.fxml;


    opens ni.edu.uam.laboratorioorm to javafx.fxml;
    exports ni.edu.uam.laboratorioorm;
}