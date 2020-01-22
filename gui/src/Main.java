import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import netscape.javascript.JSObject;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;


public class Main extends Application {

    private JSObject javascriptConnector;

    //TODO Tu będą wszystkie wartości ustawień, domyślnie zczytywane z pliku przy uruchomieniu aplikacji
    private int windowHeight = 1080;
    private int windowWidth = 1920;

    private boolean fullscreen = true;

    Stage stage;
    Scene menu,loading,map,issLiveView,settings;

    public void start(Stage unusedStage) {
        // 5 słów wyjaśnienia
        // Struktura aplikacji
        // Layout <- to co pokażemy użytkownikowi, na nim umieszczamy teskt, przyciski, WebView itd.
        //           layoutem może być wiele rzeczy np VBox, HBox i w zależności od tego co wybierzemy
        //           będa się zmieniały domyślne zachowania elementów na nim się znajdujących
        // Scena  <- Jeden widok, okienko aplikcji. np Menu, ustawienia, mapa
        //           Jej konstruktor wymaga layoutu i zdefinowanej wysokości i szerokości
        // Stage  <- fundament aplikacji, mu będziemy dawać różne sceny do wyświetlenia

        stage = new Stage();
        stage.setWidth(windowWidth);
        stage.setHeight(windowHeight);
        stage.setFullScreenExitHint("");

        loading = new Scene(getLoadingLayout());
        menu = new Scene(getMenuLayout());
        menu.getStylesheets().add("style.css"); //TODO Dodany tylko do menu, dobrze by było mieć globalny
        map = new Scene(getMapLayout());
        issLiveView = new Scene(getIssLiveViewLayout());
        settings = new Scene(getSettingsLayout());


        PauseTransition timer = new PauseTransition(Duration.millis(1500));
        timer.setOnFinished(actionEvent -> setScene(menu));


        // Tu następuje pojawienie się aplikacji
        setScene(loading);
        stage.setMaximized(fullscreen);
        stage.setResizable(false);
        stage.show();
        timer.play();
    }


    private Parent getSettingsLayout() {
        // LAYOUT ustawien

        HBox layoutSettings = new HBox(100);

        VBox settingsViews = new VBox(40);
        settingsViews.setAlignment(Pos.CENTER);

        HBox settingsGeneralControls = new HBox(80);
        VBox settingsGeneralLabels = new VBox(40);
        settingsGeneralLabels.setAlignment(Pos.CENTER);
        VBox settingsGeneralValues = new VBox(40);
        settingsGeneralValues.setAlignment(Pos.CENTER);


        // Ustawienie Boxów
        layoutSettings.getChildren().addAll(settingsViews,settingsGeneralControls);
        settingsGeneralControls.getChildren().addAll(settingsGeneralLabels,settingsGeneralValues);



        Button btnSettingsGeneral = new Button("Ogólne");
        Button btnSettings2DMap = new Button("Mapa 2D");
        Button btnAcceptChanges = new Button("Akceptuj");
        Button btnBack = new Button("Wróć");


        ComboBox<String> resolutionComboBox = new ComboBox<>();
        resolutionComboBox.getItems().addAll(
                "1920x1080",
                "1680x1050",
                "1600x1200",
                "1440x960",
                "1400x1050",
                "1280x720"
        );
        resolutionComboBox.getSelectionModel().selectFirst();

        ComboBox<String> themeComboBox = new ComboBox<>();
        themeComboBox.getItems().addAll(
                "Light",
                "Dark"
        );
        themeComboBox.getSelectionModel().selectFirst();
        Button btnViewSourceCode = new Button("Source Code");
        btnViewSourceCode.setOnAction(actionEvent -> {
            try {
                Desktop.getDesktop().browse(new URL("https://github.com/GiveMeMoreData/ZPOIF-projekt-2019-Wizualizacja-pozycji-ISS").toURI());
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });

        CheckBox fullscreenCheckBox = new CheckBox();


        //Zmiana ustawień
        btnAcceptChanges.setOnAction(actionEvent -> {

            if( fullscreenCheckBox.isSelected()!=fullscreen){
                fullscreen=fullscreenCheckBox.isSelected();
                stage.setMaximized(fullscreen);

            }
            if (!fullscreen){
                String[] values = resolutionComboBox.getValue().split("x");
                setStageSize(Integer.parseInt(values[0]),Integer.parseInt(values[1]));

            }

        });



        settingsGeneralLabels.getChildren().addAll(new Label("Rozdzielczość"),new Label("Fullscreen"),new Label("Theme"), new Label("View Source\nCode"));
        settingsGeneralValues.getChildren().addAll(resolutionComboBox,fullscreenCheckBox,themeComboBox,btnViewSourceCode);

        settingsViews.getChildren().addAll(btnSettingsGeneral,btnSettings2DMap,btnBack,btnAcceptChanges);


        btnBack.setOnAction(actionEvent -> setScene(menu));

        return layoutSettings;

    }

    private Parent getIssLiveViewLayout() {
        // LAYOUT ISS Livestream



        VBox layoutIssLive = new VBox();
        WebView issWebView = new WebView();
        // Ten link powinien być ok ale film nie chce się ładować, może chodzić o silnik przeglądarki, którego używa javafx
        //https://www.youtube.com/embed/kL090M6qtXQ?controls=0&autoplay=1&fs=0&mute=1&iv_load_policy=3&rel=0&showinfo=0
        issWebView.getEngine().load("https://www.youtube.com/embed/kL090M6qtXQ?controls=0&autoplay=1&fs=0&mute=1&iv_load_policy=3&rel=0&showinfo=0");
        issWebView.setPrefSize(windowWidth,windowHeight);

        Button btnBack = new Button("Wróć");
        btnBack.setOnAction(actionEvent -> setScene(menu));

        layoutIssLive.getChildren().addAll(issWebView,btnBack);
        return layoutIssLive;
    }

    private Parent getMenuLayout(){
        // Menu
        VBox layoutMenu = new VBox(80);
        layoutMenu.setAlignment(Pos.CENTER);


        Button btnMap = new Button("Mapa");
        Button btnIssLive= new Button("ISS livestream");
        Button btnSettings = new Button("Ustawienia");
        Button btnExit = new Button("Zamknij");
        layoutMenu.getChildren().addAll(btnMap, btnIssLive, btnSettings, btnExit);

        // Przejście do mapy (webview)
        btnMap.setOnAction(actionEvent -> setScene(map));

        btnIssLive.setOnAction(actionEvent -> setScene(issLiveView));

        // Przejście do ustawień
        btnSettings.setOnAction(actionEvent -> {
            setScene(settings);
            settings.getStylesheets().add("styleSettings.css");
        });

        // Zamknięcie aplikacji
        btnExit.setOnAction(actionEvent -> {
            Platform.exit();
            System.exit(0);
        });

        return layoutMenu;
    }

    private Parent getMapLayout() {
        // Map
        VBox layoutMap = new VBox();
        WebView webView = new WebView();
        final WebEngine webEngine = webView.getEngine();
        webView.setPrefSize(5000,5000);
        webEngine.executeScript("var width="+(windowWidth-20));

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
                                String altitude = Float.toString(api.altitude);
                                String velocity = Float.toString(api.velocity);
                                Platform.runLater(() -> javascriptConnector.call("update", longtitude, latitude, altitude, velocity));
                                return null;
                            }
                        };
                    }
                };
                sv.setPeriod(Duration.seconds(1));
                sv.start();
            }
        });
        URL url = this.getClass().getResource("webview.html");
        webEngine.load(url.toString());

        btnBackMap.setOnAction(actionEvent -> setScene(menu));

        return layoutMap;
    }

    private Parent getLoadingLayout() {
        //Loading screen layout
        Image loadingImage = new Image("face.png",windowWidth/2,windowHeight/2,true,true);
        ImageView imageView = new ImageView(loadingImage);
        StackPane layoutLoadingPage = new StackPane();
        layoutLoadingPage.setAlignment(Pos.CENTER);
        layoutLoadingPage.getChildren().add(imageView);

        layoutLoadingPage.setOnMouseClicked(mouseEvent -> setScene(menu));
        return layoutLoadingPage;
    }

    private void setScene(Scene scene){
        //TODO Może się przydać przy bardziej rozbudowanych przejściach pomiędzy scenami
        stage.setScene(scene);
    }

    private void setStageSize(int width, int height){
        windowHeight=height;
        windowWidth=width;
        Rectangle2D screen = Screen.getPrimary().getBounds();
        double screenWidth = screen.getWidth();
        double screenHeight = screen.getHeight();
        stage.setHeight(windowHeight);
        stage.setWidth(windowWidth);
        stage.setY((screenHeight-windowHeight)/2);
        stage.setX((screenWidth-windowWidth)/2);
    }

}
