package com.github.vatbub.javametricscatcher.sampleapp.metricviews;

        /*-
         * #%L
 * javametricscatcher.common
 * %%
 * Copyright (C) 2017 Frederik Kammel
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
         */


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class CustomCounter {
    private static Stage stage;
    private static CustomCounter controllerInstance;
    private com.github.vatbub.javametricscatcher.common.custommetrics.CustomCounter counter;

    public static void show(com.github.vatbub.javametricscatcher.common.custommetrics.CustomCounter counter) throws IOException {
        stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(CustomCounter.class.getResource("CustomCounter.fxml"));
        Parent root = fxmlLoader.load();
        controllerInstance = fxmlLoader.getController();
        controllerInstance.setCounter(counter);

        Scene scene = new Scene(root);

        stage.setTitle("JavaMetricsCatcher Sample App");

        stage.setMinWidth(scene.getRoot().minWidth(0) + 70);
        stage.setMinHeight(scene.getRoot().minHeight(0) + 70);

        stage.setScene(scene);

        stage.show();
    }

    @FXML
    private TextField incByTextField;

    @FXML
    private Button decByOneButton;

    @FXML
    private Label valueLabel;

    @FXML
    private Button decByButton;

    @FXML
    private Button incByOneButton;

    @FXML
    private TextField decByTextField;

    @FXML
    private Button incByButton;

    @FXML
    void incByOneButtonOnAction(ActionEvent event) {
        getCounter().inc();
        updateValue();
    }

    @FXML
    void incByButtonOnAction(ActionEvent event) {
        getCounter().inc(Long.parseLong(incByTextField.getText()));
        updateValue();
    }

    @FXML
    void decByOneButtonOnAction(ActionEvent event) {
        getCounter().dec();
        updateValue();
    }

    @FXML
    void decByButton(ActionEvent event) {
        getCounter().dec(Long.parseLong(decByTextField.getText()));
        updateValue();
    }

    @FXML
    void initialize() {
        assert incByTextField != null : "fx:id=\"incByTextField\" was not injected: check your FXML file 'CustomCounter.fxml'.";
        assert decByOneButton != null : "fx:id=\"decByOneButton\" was not injected: check your FXML file 'CustomCounter.fxml'.";
        assert valueLabel != null : "fx:id=\"valueLabel\" was not injected: check your FXML file 'CustomCounter.fxml'.";
        assert decByButton != null : "fx:id=\"decByButton\" was not injected: check your FXML file 'CustomCounter.fxml'.";
        assert incByOneButton != null : "fx:id=\"incByOneButton\" was not injected: check your FXML file 'CustomCounter.fxml'.";
        assert decByTextField != null : "fx:id=\"decByTextField\" was not injected: check your FXML file 'CustomCounter.fxml'.";
        assert incByButton != null : "fx:id=\"incByButton\" was not injected: check your FXML file 'CustomCounter.fxml'.";
    }

    private void updateValue(){
        valueLabel.setText(Long.toString(getCounter().getCount()));
    }

    public com.github.vatbub.javametricscatcher.common.custommetrics.CustomCounter getCounter() {
        return counter;
    }

    public void setCounter(com.github.vatbub.javametricscatcher.common.custommetrics.CustomCounter counter) {
        this.counter = counter;
        updateValue();
    }
}
