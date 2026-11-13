package ni.edu.uam.laboratorioorm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import ni.edu.uam.laboratorioorm.database.JPAUtil;

import java.io.IOException;

public class LaboratorioApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                LaboratorioApplication.class.getResource("equipos-view.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Control de equipos de laboratorio");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        // Cierra la fábrica de persistencia al finalizar la aplicación.
        JPAUtil.cerrar();
    }
}
