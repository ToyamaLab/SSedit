package ssedit.GUI;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import ssedit.Common.GlobalEnv;


public class Browser extends Region {
	final WebView browser = new WebView();
    final WebEngine webEngine = browser.getEngine();

    public Browser() {
        //apply the styles
        getStyleClass().add("browser");
        // load the web page
        webEngine.load("file://localhost" + GlobalEnv.outdirPath + GlobalEnv.OS_FS +  ".htmlViewer.html");
        //add the web view to the scene
        getChildren().add(browser);

    }

    public void reload(){
    	webEngine.load("file://localhost" + GlobalEnv.outdirPath + GlobalEnv.OS_FS +  ".htmlViewer.html");
//    	webEngine.reload();
    }
    private Node createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    @Override protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        layoutInArea(browser,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
    }

    @Override protected double computePrefWidth(double height) {
        return 750;
    }

    @Override protected double computePrefHeight(double width) {
        return 500;
    }

}
