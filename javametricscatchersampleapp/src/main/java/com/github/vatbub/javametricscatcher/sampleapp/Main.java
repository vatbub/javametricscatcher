package com.github.vatbub.javametricscatcher.sampleapp;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.github.vatbub.javametricscatcher.client.ServerReporter;
import com.github.vatbub.javametricscatcher.sampleapp.metricviews.MetricViewFactory;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main extends Application {
    private static final String METRIC_PACKAGE_NAME = "com.github.vatbub.javametricscatcher.common.custommetrics";

    private Stage stage;
    private MetricRegistry registry;

    @FXML
    private Button createButton;

    @FXML
    private ComboBox<MetricType> metricTypeSelector;

    @FXML
    private TextField metricName;

    private static Main controllerInstance;
    private ConsoleReporter consoleReporter;
    private ServerReporter serverReporter;

    public static Main getControllerInstance() {
        return controllerInstance;
    }

    @FXML
    void createButtonOnAction(ActionEvent event) throws ClassNotFoundException, IllegalAccessException, InstantiationException, IOException {
        MetricType metricType = metricTypeSelector.getSelectionModel().getSelectedItem();
        Metric metric = (Metric) Class.forName(METRIC_PACKAGE_NAME + "." + metricType).newInstance();
        registry.register(metricName.getText(), metric);
        metricName.setText("");
        MetricViewFactory.show(metricType, metric);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Main.fxml"));
        Parent root = fxmlLoader.load();
        controllerInstance = fxmlLoader.getController();

        Scene scene = new Scene(root);

        primaryStage.setTitle("JavaMetricsCatcher Sample App");

        primaryStage.setMinWidth(scene.getRoot().minWidth(0) + 70);
        primaryStage.setMinHeight(scene.getRoot().minHeight(0) + 70);

        primaryStage.setScene(scene);

        primaryStage.show();
    }

    @FXML
    void initialize() throws IOException {
        assert createButton != null : "fx:id=\"createButton\" was not injected: check your FXML file 'Main.fxml'.";
        assert metricTypeSelector != null : "fx:id=\"metricTypeSelector\" was not injected: check your FXML file 'Main.fxml'.";
        assert metricName != null : "fx:id=\"metricName\" was not injected: check your FXML file 'Main.fxml'.";

        registry = new MetricRegistry();
        metricTypeSelector.getItems().addAll(MetricType.values());
        consoleReporter = ConsoleReporter.forRegistry(registry).build();
        consoleReporter.start(1, TimeUnit.SECONDS);
        serverReporter = ServerReporter.forRegistry(registry, "localhost").withTcpPort(1020).withUdpPort(1021).build();
        serverReporter.start(1, TimeUnit.SECONDS);
    }

    public enum MetricType {
        CustomCounter, CustomHistogram, CustomTimer, IntegerGauge, LongGauge
    }
}
