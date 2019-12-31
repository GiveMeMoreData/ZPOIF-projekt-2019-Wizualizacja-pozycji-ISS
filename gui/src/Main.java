import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {



    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage window) throws IOException {

        // 5 słów wyjaśnienia
        // Struktura aplikacji
        // Layout <- to co pokażemy użytkownikowi, na nim umieszczamy teskt, przyciski, WebView itd.
        //           layoutem może być wiele rzeczy np VBox, HBox i w zależności od tego co wybierzemy
        //           będa się zmieniały domyślne zachowania elementów na nim się znajdujących
        // Scena  <- Jeden widok, okienko aplikcji. np Menu, ustawienia, mapa
        //           Jej konstruktor wymaga layoutu i zdefinowanej wysokości i szerokości
        // Stage  <- fundament aplikacji, mu będziemy dawać różne sceny do wyświetlenia


        // Połączenie z API ISS
        Api api = new Api();
        api.requestGET();

        // LAYOUTY
        // Menu
        VBox layoutMenu = new VBox(80);
        layoutMenu.setAlignment(Pos.CENTER);

        Button btnMap = new Button("Mapa");
        Button btnCos = new Button("coś");
        Button btnSettings = new Button("Ustawienia");
        Button btnExit = new Button("Zamknij");
        layoutMenu.getChildren().addAll(btnMap,btnCos,btnSettings,btnExit);

        // Settings
        //TODO dodanie zmiany wielości okna i ewentualnie częstotliowści pobierania danych
        VBox layoutSettings = new VBox();
        Button btnBack = new Button("Wróć");
        layoutSettings.getChildren().addAll(btnBack);

        // Map
        VBox layoutMap = new VBox();
        WebView webView = new WebView();
        webView.setPrefHeight(5000); // Ma być większa niż wysokość dowolnego monitora wyrażona w pikselach
        WebEngine engine = webView.getEngine();
        engine.load("https://www.gagolewski.com/"); // ( ͡° ͜ʖ ͡°)

        Button btnBackMap = new Button("Wróć");
        layoutMap.getChildren().addAll(webView,btnBackMap);


        // Wszystkie sceny aplikacji

        Scene menu = new Scene(layoutMenu,1000,800);
        Scene map = new Scene(layoutMap,1000,800);
        Scene settings = new Scene(layoutSettings,1000,800);


        //    BUTTON HANDLING

        // Powrót do menu
        //TODO może da się to zrobić jednym przyciskiem?
        btnBackMap.setOnAction(actionEvent -> window.setScene(menu));
        btnBack.setOnAction(actionEvent -> window.setScene(menu));

        // Przejście do mapy (webview)
        btnMap.setOnAction(actionEvent -> window.setScene(map));

        // Przejście do ustawień
        btnSettings.setOnAction(actionEvent -> window.setScene(settings));

        //Zamknięcie aplikacji
        btnExit.setOnAction(actionEvent -> {
            Platform.exit();
            System.exit(0);
        });


        window.setScene(menu);
        menu.getStylesheets().add("style.css"); //TODO Dodany tylko do menu, mógłby być globalny
        window.show();

    }
}
