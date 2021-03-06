package ch.retorte.estimator.estimations;

import ch.retorte.estimator.Estimator;
import ch.retorte.estimator.Ui;
import ch.retorte.estimator.converter.EstimatorLabelProvider;
import ch.retorte.estimator.converter.ForgivingNumberStringConverter;
import ch.retorte.estimator.mainscreen.EntryController;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.converter.NumberStringConverter;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Depicts a single estimation line entry.
 */
public class EstimationEntryView extends GridPane {

  //---- Static

  private static final String LAYOUT_FILE = "/layouts/EstimationEntry.fxml";
  private static final NumberStringConverter NUMBER_STRING_CONVERTER = new ForgivingNumberStringConverter();

  private static final String DECIMAL_FORMAT = "%.1f";

  private static final Map<String, String> ESTIMATOR_LABELS = newHashMap();

  static {
    ESTIMATOR_LABELS.put("uniform", "Uniform");
    ESTIMATOR_LABELS.put("normal_sd2", "Normal (SD:2)");
    ESTIMATOR_LABELS.put("normal_sd3", "Normal (SD:3)");
  }

  //---- FX Fields

  @FXML
  private Label nameLabel;

  @FXML
  private Label distributionLabel;

  @FXML
  private Label currentValueLabel;

  @FXML
  private Label estimateLabel;

  @FXML
  private Label availableResourcesLabel;

  @FXML
  private Label availableResourcesDeltaLabel;

  @FXML
  private Label actionsLabel;

  @FXML
  private TextField estimationName;

  @FXML
  private ComboBox<Estimator> estimator;

  @FXML
  private TextField currentValue;

  @FXML
  private TextField correctionValue;

  @FXML
  private Label estimatedValue;

  @FXML
  private TextField availableResources;

  @FXML
  private Label availableResourcesDelta;

  @FXML
  private Button deleteButton;


  //---- Fields

  private EntryController entryControl;
  private final ObservableList<Estimator> availableEstimators;
  private final EstimationEntry estimationEntry;
  private Ui.InputChangeListener inputChangeListener;


  //---- Constructor

  public EstimationEntryView(EntryController entryControl, ObservableList<Estimator> availableEstimators, EstimationEntry estimationEntry, Ui.InputChangeListener inputChangeListener, boolean first) {
    setupLayout();

    this.entryControl = entryControl;
    this.availableEstimators = availableEstimators;
    this.estimationEntry = estimationEntry;
    this.inputChangeListener = inputChangeListener;

    initializeLabels(first);
    initializeEditableFields();
    initializeReadOnlyFields();
    initializeDeleteButton();
  }

  private void initializeLabels(boolean first) {
    nameLabel.setManaged(first);
    distributionLabel.setManaged(first);
    currentValueLabel.setManaged(first);
    estimateLabel.setManaged(first);
    availableResourcesLabel.setManaged(first);
    availableResourcesDeltaLabel.setManaged(first);
    actionsLabel.setManaged(first);
  }

  private void setupLayout() {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(LAYOUT_FILE));
    fxmlLoader.setRoot(this);
    fxmlLoader.setController(this);

    try {
      fxmlLoader.load();
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    }
  }

  private void initializeEditableFields() {
    // Name
    estimationName.textProperty().bindBidirectional(estimationEntry.nameProperty());
    estimationEntry.nameProperty().addListener(inputChangeListener);

    // Estimator combo
    estimator.setItems(availableEstimators);
    estimator.valueProperty().bindBidirectional(estimationEntry.estimatorProperty());
    estimator.setConverter(new EstimatorLabelProvider(ESTIMATOR_LABELS));
    estimationEntry.estimatorProperty().addListener(inputChangeListener);
    // Initialize with first distribution
    estimationEntry.estimatorProperty().setValue(availableEstimators.get(0));

    // Current value
    currentValue.textProperty().bindBidirectional(estimationEntry.currentValueProperty(), NUMBER_STRING_CONVERTER);
    estimationEntry.currentValueProperty().addListener(inputChangeListener);

    correctionValue.textProperty().bindBidirectional(estimationEntry.correctionValueProperty(), NUMBER_STRING_CONVERTER);
    estimationEntry.correctionValueProperty().addListener(inputChangeListener);

    availableResources.textProperty().bindBidirectional(estimationEntry.availableResourcesProperty(), NUMBER_STRING_CONVERTER);
    estimationEntry.availableResourcesProperty().addListener(inputChangeListener);
  }

  private void initializeReadOnlyFields() {
    estimatedValue.textProperty().bind(estimationEntry.estimatedValueProperty().asString(DECIMAL_FORMAT));
    availableResourcesDelta.textProperty().bind(estimationEntry.availableResourcesDeltaProperty().asString(DECIMAL_FORMAT));
    availableResourcesDelta.styleProperty().bind(estimationEntry.getAvailableResourcesDeltaStyleProperty());
  }

  private void initializeDeleteButton() {
    deleteButton.setOnAction(event -> entryControl.delete(this));
  }

  public EstimationEntry getEntry() {
    return estimationEntry;
  }
}
