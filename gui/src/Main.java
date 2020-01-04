import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import netscape.javascript.JSObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main extends Application {

    private JSObject javascriptConnector;
    Stage primaryStage;
    private int windowHeight = 720;
    private int windowWidth = 1280;

    public void start(Stage unUsedStage) {

        primaryStage = new Stage();
        primaryStage.setWidth(windowWidth);
        primaryStage.setHeight(windowHeight);
        // 5 słów wyjaśnienia
        // Struktura aplikacji
        // Layout <- to co pokażemy użytkownikowi, na nim umieszczamy teskt, przyciski, WebView itd.
        //           layoutem może być wiele rzeczy np VBox, HBox i w zależności od tego co wybierzemy
        //           będa się zmieniały domyślne zachowania elementów na nim się znajdujących
        // Scena  <- Jeden widok, okienko aplikcji. np Menu, ustawienia, mapa
        //           Jej konstruktor wymaga layoutu i zdefinowanej wysokości i szerokości
        // Stage  <- fundament aplikacji, mu będziemy dawać różne sceny do wyświetlenia

        // LAYOUTY
        // Menu


        VBox layoutMenu = new VBox(80);
        layoutMenu.setAlignment(Pos.CENTER);


        Button btnMap = new Button("Mapa");
        Button btnIssLive= new Button("ISS livestream");
        Button btnSettings = new Button("Ustawienia");
        Button btnExit = new Button("Zamknij");
        layoutMenu.getChildren().addAll(btnMap, btnIssLive, btnSettings, btnExit);

        // Settings
        //TODO dodanie zmiany wielkości okna i ewentualnie częstotliowści pobierania danych



        // Map
        VBox layoutMap = new VBox();
        WebView webView = new WebView();
        final WebEngine webEngine = webView.getEngine();

        Button btnBackMap = new Button("Wróć");
        layoutMap.getChildren().addAll(webView, btnBackMap);

        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (Worker.State.SUCCEEDED == newValue) {
                javascriptConnector = (JSObject) webEngine.executeScript("getJsConnector()");
                Api api = new Api();
                ScheduledService<Void> sv = new ScheduledService<>() {
                    public Task<Void> createTask() {
                        return new Task<>() {

                            public Void call() {
                                try {
                                    api.fetch();
                                } catch (IOException | InterruptedException e) {
                                    System.out.println("IOException");
                                }
                                String longtitude = Float.toString(api.longitude);
                                String latitude = Float.toString(api.latitude);
                                Platform.runLater(() -> javascriptConnector.call("update", longtitude, latitude));
                                return null;
                            }
                        };
                    }
                };
                sv.setPeriod(Duration.seconds(5));
                sv.start();
            }
        });

        // LAYOUT ISS Livestream

        VBox layoutIssLive = new VBox();
        WebView issWebView = new WebView();
        final WebEngine issWebEngine = issWebView.getEngine();
        // Ten link powinien być ok ale film nie chce się ładować, może chodzić o silnik przeglądarki, którego używa javafx
        //https://www.youtube.com/embed/kL090M6qtXQ?controls=0&autoplay=1&fs=0&mute=1&iv_load_policy=3&rel=0&showinfo=0
        issWebEngine.load("https://www.youtube.com/embed/kL090M6qtXQ?controls=0&autoplay=1&fs=0&mute=1&iv_load_policy=3&rel=0&showinfo=0");
        issWebView.setPrefSize(windowWidth,windowHeight);
        layoutIssLive.getChildren().addAll(issWebView);


        // LAYOUT ustawien

        HBox layoutSettings = new HBox(100);

        VBox settingsViews = new VBox(40);
        settingsViews.setAlignment(Pos.CENTER);

        HBox settingsControls = new HBox(80);
        VBox settingsLabels = new VBox(40);
        settingsLabels.setAlignment(Pos.CENTER);
        VBox settingsValues = new VBox(40);
        settingsValues.setAlignment(Pos.CENTER);

        // Ustawienie Boxów
        layoutSettings.getChildren().addAll(settingsViews,settingsControls);
        settingsControls.getChildren().addAll(settingsLabels,settingsValues);



        Button btnSettingsGeneral = new Button("Ogólne");
        Button btnSettings2DMap = new Button("Mapa 2D");
        Button btnAcceptChanges = new Button("Akceptuj");
        Button btnBack = new Button("Wróć");


        ComboBox<String> resolutionComboBox = new ComboBox<>();
        resolutionComboBox.getItems().addAll(
                "1920x1080",
                "1280x720",
                "720x576",
                "720x480"
        );
        resolutionComboBox.getSelectionModel().selectFirst();

        ComboBox<String> themeComboBox = new ComboBox<>();
        themeComboBox.getItems().addAll(
                "Light",
                "Dark"
        );
        themeComboBox.getSelectionModel().selectFirst();
        Button btnViewSourceCode = new Button("Source Code");


        btnAcceptChanges.setOnAction(actionEvent -> {
            String[] values = resolutionComboBox.getValue().split("x");
            setStageSize(Integer.parseInt(values[0]),Integer.parseInt(values[1]));

        });

        settingsLabels.getChildren().addAll(new Label("Rozdzielczość"),new Label("Fullscreen"), new Label("Fullscreen\nborderless"),new Label("Theme"), new Label("View Source\nCode"));
        settingsValues.getChildren().addAll(resolutionComboBox,new CheckBox(),new CheckBox(),themeComboBox,btnViewSourceCode);

        settingsViews.getChildren().addAll(btnSettingsGeneral,btnSettings2DMap,btnBack,btnAcceptChanges);

        // Wszystkie sceny aplikacji


        Scene menu = new Scene(layoutMenu);
        Scene map = new Scene(layoutMap);
        Scene settings = new Scene(layoutSettings);
        Scene issLive = new Scene(layoutIssLive);

        // BUTTON HANDLING
        // Powrót do menu
        //TODO może da się to zrobić jednym przyciskiem?
        btnBackMap.setOnAction(actionEvent -> primaryStage.setScene(menu));
        btnBack.setOnAction(actionEvent -> primaryStage.setScene(menu));

        // Przejście do mapy (webview)
        btnMap.setOnAction(actionEvent -> setScene(map));

        btnIssLive.setOnAction(actionEvent -> setScene(issLive));

        // Przejście do ustawień
        btnSettings.setOnAction(actionEvent -> {
            primaryStage.setScene(settings);
            settings.getStylesheets().add("styleSettings.css");
        });

        // Zamknięcie aplikacji
        btnExit.setOnAction(actionEvent -> {
            Platform.exit();
            System.exit(0);
        });



        primaryStage.setScene(menu);
        menu.getStylesheets().add("style.css"); //TODO Dodany tylko do menu, mógłby być globalny
        primaryStage.show();

        URL url = this.getClass().getResource("webview.html");
        webEngine.load(url.toString());

    }

    private void setScene(Scene scene){
        primaryStage.setScene(scene);
    }

    private void setStageSize(int width, int height){
        windowHeight=height;
        windowWidth=width;
        Rectangle2D screen = Screen.getPrimary().getBounds();
        double screenWidth = screen.getWidth();
        double screenHeight = screen.getHeight();
        primaryStage.setHeight(windowHeight);
        primaryStage.setWidth(windowWidth);
        primaryStage.setY((screenHeight-windowHeight)/2);
        primaryStage.setX((screenWidth-windowWidth)/2);
    }



}
