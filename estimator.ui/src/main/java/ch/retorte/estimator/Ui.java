package ch.retorte.estimator;

import ch.retorte.estimator.mainscreen.MainScreenController;
import ch.retorte.estimator.storage.ApplicationData;
import ch.retorte.estimator.storage.Storage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Starting point of the user interface.
 */
public class Ui extends Application {

  //---- Static

  private static final String MAIN_SCREEN_LAYOUT_FILE = "/layouts/MainScreen.fxml";

  private static ObservableList<Estimator> availableEstimators = FXCollections.observableArrayList();

  //---- Fields

  private Storage storage;

  private MainScreenController mainScreenController;

  private Timer timer;


  //---- Methods

  void addEstimator(Estimator estimator) {
    availableEstimators.add(estimator);
  }

  void launch() {
    Application.launch();
  }

  @Override
  public void start(Stage stage) throws IOException {
    stage.setTitle("Estimator");
    stage.setScene(new Scene(getRoot()));
    stage.show();

    initializeStorage();
    initializeMainScreenController();
    initializeUpdateTimer();
    loadData();
  }

  private void initializeStorage() {
    String homeDirectory = System.getProperty("user.home");
    storage = new Storage(homeDirectory + "/");
  }

  private void initializeMainScreenController() {
    mainScreenController.setAvailableEstimators(availableEstimators);
    mainScreenController.setUpdateListener(new InputChangeListener());
  }

  private void initializeUpdateTimer() {
    timer = new Timer();
    timer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        Platform.runLater(() -> mainScreenController.refresh());
      }
    }, 0, 1000);

  }

  private Parent getRoot() throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(MAIN_SCREEN_LAYOUT_FILE));
    Parent result = fxmlLoader.load();
    mainScreenController = fxmlLoader.getController();
    return result;
  }

  @Override
  public void stop() throws Exception {
    timer.cancel();
    super.stop();
  }

  private void loadData() {
    ApplicationData applicationData = storage.load();
    if (applicationData != null) {
      mainScreenController.setData(applicationData);
    }
  }

  private void saveData() {
    ApplicationData applicationData = mainScreenController.getData();
    storage.save(applicationData);
  }

  public class InputChangeListener implements ChangeListener<Object> {

    @Override
    public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
      mainScreenController.refresh();
      saveData();
    }
  }
}
