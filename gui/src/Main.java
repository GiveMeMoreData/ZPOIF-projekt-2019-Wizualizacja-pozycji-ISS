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
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import netscape.javascript.JSObject;


import java.awt.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;


public class Main extends Application {

    private JSObject javascriptConnector;

    // Parametry domyślne, normalnie ładowane z pliku config przez readProperties()
    private int windowHeight = 1080;
    private int windowWidth = 1920;
    private boolean fullscreen = true;
    private String theme = "Light";


    // Odczyt rozdzielczość ekranu użytkownika
    Rectangle2D screen = Screen.getPrimary().getBounds();
    int screenWidth = (int) screen.getWidth();
    int screenHeight = (int) screen.getHeight();

    //Wszystkie sceny aplikacji
    Stage stage;
    Scene menu,loading,map,settings;

    public void start(Stage unusedStage) {
        // 5 słów wyjaśnienia
        // Struktura aplikacji
        // Layout <- to co pokażemy użytkownikowi, na nim umieszczamy teskt, przyciski, WebView itd.
        //           layoutem może być wiele rzeczy np VBox, HBox i w zależności od tego co wybierzemy
        //           będa się zmieniały domyślne zachowania elementów na nim się znajdujących
        // Scena  <- Jeden widok, okienko aplikcji. np Menu, ustawienia, mapa
        //           Jej konstruktor wymaga layoutu i zdefinowanej wysokości i szerokości
        // Stage  <- fundament aplikacji, mu będziemy dawać różne sceny do wyświetlenia

        //Wczytaj domyśle ustawienia
        readProperties();


        // Główne okno aplikacji
        stage = new Stage();
        stage.setWidth(windowWidth);
        stage.setHeight(windowHeight);
        stage.setFullScreenExitHint("");
        stage.getIcons().add(new Image("icon.png"));

        // Tworzenie wszystkich scen aplikacji
        loading = new Scene(getLoadingLayout());
        menu = new Scene(getMenuLayout());
        map = new Scene(getMapLayout());
        settings = new Scene(getSettingsLayout());

        // Animacja ekranu ładowania
        PauseTransition timer = new PauseTransition(Duration.millis(1500));
        timer.setOnFinished(actionEvent -> setScene(menu));

        // Tu następuje pojawienie się aplikacji
        stage.setScene(loading);
        stage.setMaximized(fullscreen);
        stage.setResizable(false);
        stage.show();
        timer.play();
    }

    private Parent getSettingsLayout() {
        // LAYOUT ustawień

        HBox layoutSettings = new HBox(100);
        layoutSettings.setAlignment(Pos.CENTER);

        VBox settingsViews = new VBox(40);
        settingsViews.setAlignment(Pos.CENTER);

        HBox settingsGeneralControls = new HBox(80);
        VBox settingsGeneralLabels = new VBox(56);
        settingsGeneralLabels.setAlignment(Pos.CENTER_LEFT);
        VBox settingsGeneralValues = new VBox(40);
        settingsGeneralValues.setAlignment(Pos.CENTER);


        // Ustawienie kontenerów
        layoutSettings.getChildren().addAll(settingsViews,settingsGeneralControls);

        //Dodanie pola na opisy ustawień i kontrolki
        settingsGeneralControls.getChildren().addAll(settingsGeneralLabels,settingsGeneralValues);

        //Przyciski powrotu i zmiany ustawień
        Button btnAcceptChanges = new Button("Akceptuj");
        Button btnBack = new Button("Wróć");

        settingsViews.getChildren().addAll(btnBack,btnAcceptChanges);
        btnBack.setOnAction(actionEvent -> setScene(menu));

        /*
            USTAWIENIA
        */

        // Zmiana rozdzielczości
        ComboBox<String> resolutionComboBox = new ComboBox<>();
        resolutionComboBox.getItems().addAll(
                "1920x1080",
                    "1776x1000",
                    "1760x990",
                    "1600x1024",
                    "1600x900",
                    "1440x960",
                    "1400x1050"
        );
        resolutionComboBox.setValue(windowWidth +"x"+ windowHeight);

        // Wybór motywu aplikacji
        ComboBox<String> themeComboBox = new ComboBox<>();
        themeComboBox.getItems().addAll("Light","Dark");
        themeComboBox.setValue(theme);

        //Otworzenie okna w przeglądarce z kodem źródłowym
        Button btnViewSourceCode = new Button("Github");
        btnViewSourceCode.setOnAction(actionEvent -> {
            try {
                Desktop.getDesktop().browse(new URL("https://github.com/GiveMeMoreData/ZPOIF-projekt-2019-Wizualizacja-pozycji-ISS").toURI());
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });

        //Checkbox trybu pełnoekranowego
        CheckBox fullscreenCheckBox = new CheckBox();
        fullscreenCheckBox.setSelected(fullscreen);

        /*
            POPUP zmianny ustawień
        */

        Popup popup = new Popup();

        // Elementy na popup'ie
        Label text = new Label("Wprowadzenie zmian wymaga\nponownego uruchomienia aplikacji");
        text.setAlignment(Pos.CENTER);

        Button cancelBtn = new Button("Anuluj");
        cancelBtn.setOnAction(actionEvent -> popup.hide());

        Button restartBtn = new Button("Restartuj");
        restartBtn.setOnAction(actionEvent -> {
            if( fullscreenCheckBox.isSelected()!=fullscreen){
                fullscreen=!fullscreen;
                windowWidth=screenWidth;
                windowHeight=screenHeight;
            }
            if (!fullscreen){
                String[] values = resolutionComboBox.getValue().split("x");
                setStageSize(Integer.parseInt(values[0]),Integer.parseInt(values[1]));
            }
            saveProperties();
            Platform.exit();
            System.exit(0);
        });

        // Layout popup'a
        HBox h = new HBox(40,cancelBtn,restartBtn);
        VBox v =  new VBox(40,text,h);
        v.setAlignment(Pos.CENTER);
        h.setAlignment(Pos.CENTER);

        StackPane st = new StackPane(v);
        st.getStylesheets().setAll("popup.css");
        st.setStyle("-fx-background-color: lightgrey; -fx-padding: 20;");

        // Zatwierdzenie kontenerów i elementów w popup'ie
        popup.getContent().add(st);

        //Zmiana ustawień
        btnAcceptChanges.setOnAction(actionEvent -> {
            //Zmiany wymagające restartu
            if (fullscreenCheckBox.isSelected()!=fullscreen || !resolutionComboBox.getValue().equals(windowWidth +"x"+ windowHeight)){
                popup.show(stage);
            }

            //Zmiany nie wymagające restaru
            theme= themeComboBox.getValue();
            setScene(settings);
        });

        // Dodanie wszystkich napisów i kontrolek ustawień do odpowiednich kontenerów
        settingsGeneralLabels.getChildren().addAll(new Label("Rozdzielczość"),new Label("Fullscreen"),new Label("Theme"), new Label("Zobacz projekt\nna Github"));
        settingsGeneralValues.getChildren().addAll(resolutionComboBox,fullscreenCheckBox,themeComboBox,btnViewSourceCode);


        return layoutSettings;

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

        btnIssLive.setOnAction(actionEvent -> {
            try {
                Desktop.getDesktop().browse(new URL("https://www.youtube.com/embed/wmHF9aLVOeM?controls=0&autoplay=1&fs=0&mute=1&iv_load_policy=3&rel=0&showinfo=0").toURI());
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        });


        // Przejście do mapy (webview)
        btnMap.setOnAction(actionEvent -> setScene(map));

        // Przejście do ustawień
        btnSettings.setOnAction(actionEvent -> setScene(settings));

        // Zamknięcie aplikacji
        btnExit.setOnAction(actionEvent -> {
            saveProperties();
            Platform.exit();
            System.exit(0);
        });

        return layoutMenu;
    }

    private Parent getMapLayout() {
        // Map
        VBox layoutMap = new VBox();
        WebView webView = new WebView();
        webView.setContextMenuEnabled(false);
        WebEngine webEngine = webView.getEngine();
        webView.setPrefSize(5000,5000);


        Button btnBackMap = new Button("Wróć");
        layoutMap.getChildren().addAll(webView, btnBackMap);


        layoutMap.setStyle("-fx-background-color: #1A233B;");

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

        webEngine.executeScript("window.width="+(windowWidth*0.85));
        URL url = this.getClass().getResource("webview.html");
        webEngine.load(url.toString());


        btnBackMap.setOnAction(actionEvent -> setScene(menu));

        return layoutMap;
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
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

        if (theme.equals("Light")){
            scene.getStylesheets().setAll("lightTheme.css");
        }
        else if (theme.equals("Dark")){
            scene.getStylesheets().setAll("darkTheme.css");
        }
        stage.setScene(scene);
    }

    private void setStageSize(int width, int height){
        windowHeight=height;
        windowWidth=width;
    }

    private void readProperties(){
        // Odczytuje zapisane ustawienia z pliku config.properties
        Properties prop = new Properties();

        try {
            prop.load(new FileInputStream("config.properties"));
        } catch (IOException e) {
            System.out.println("File seems to not exist");
            e.printStackTrace();
        }

        // Przypisanie wszystkich zapisanych ustawień
        // Do zmiennych używanych przez aplikacje
        fullscreen =  Boolean.parseBoolean(prop.getProperty("fullscreen"));
        theme =prop.getProperty("theme");
        windowWidth =Integer.parseInt(prop.getProperty("windowWidth"));
        windowHeight = Integer.parseInt(prop.getProperty("windowHeight"));

    }

    private void saveProperties() {
        Properties prop = new Properties();

        try {
            //set the properties value
            prop.setProperty("fullscreen", String.valueOf(fullscreen));
            prop.setProperty("theme", theme);
            prop.setProperty("windowWidth", String.valueOf(windowWidth));
            prop.setProperty("windowHeight", String.valueOf(windowHeight));

            //save properties to project root folder
            prop.store(new FileOutputStream("config.properties"), null);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
