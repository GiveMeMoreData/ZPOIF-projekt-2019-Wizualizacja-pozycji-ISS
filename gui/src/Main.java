import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import netscape.javascript.JSObject;
import java.io.IOException;
import java.net.URL;

public class Main extends Application {

    private JSObject javascriptConnector;

    public void start(Stage primaryStage) throws IOException {
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
        Button btnCos = new Button("coś");
        Button btnSettings = new Button("Ustawienia");
        Button btnExit = new Button("Zamknij");
        layoutMenu.getChildren().addAll(btnMap, btnCos, btnSettings, btnExit);

        // Settings
        //TODO dodanie zmiany wielkości okna i ewentualnie częstotliowści pobierania danych
        VBox layoutSettings = new VBox();
        Button btnBack = new Button("Wróć");
        layoutSettings.getChildren().addAll(btnBack);

        // Map
        VBox layoutMap = new VBox();
        WebView webView = new WebView();
        final WebEngine webEngine = webView.getEngine();

        Button btnBackMap = new Button("Wróć");
        layoutMap.getChildren().addAll(webView, btnBackMap);

        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (Worker.State.SUCCEEDED == newValue) {
                javascriptConnector = (JSObject) webEngine.executeScript("getJsConnector()");
                ScheduledService<Void> sv = new ScheduledService<Void>() {
                    public Task<Void> createTask() {
                        return new Task<Void>() {
                            Api api = new Api();

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

        // Wszystkie sceny aplikacji

        Scene menu = new Scene(layoutMenu, 1000, 800);
        Scene map = new Scene(layoutMap, 1000, 800);
        Scene settings = new Scene(layoutSettings, 1000, 800);

        // BUTTON HANDLING
        // Powrót do menu
        //TODO może da się to zrobić jednym przyciskiem?
        btnBackMap.setOnAction(actionEvent -> primaryStage.setScene(menu));
        btnBack.setOnAction(actionEvent -> primaryStage.setScene(menu));

        // Przejście do mapy (webview)
        btnMap.setOnAction(actionEvent -> primaryStage.setScene(map));

        // Przejście do ustawień
        btnSettings.setOnAction(actionEvent -> primaryStage.setScene(settings));

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
}
