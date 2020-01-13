import org.json.JSONObject;
import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Api {

    URI uri;
    private String response;
    public float latitude;
    public float longitude;
    public int time;
    public float altitude;
    public float velocity;
    HttpClient client;
    HttpRequest request;


    public Api() {
        try {
            this.uri= new URI("https://api.wheretheiss.at/v1/satellites/25544");
        } catch (URISyntaxException e) {
            System.out.println("Could not connect to API");
        }
        client = HttpClient.newHttpClient();

    }

    public void fetch() throws InterruptedException, IOException {
        sendNewRequest();
        updateValues();
    }

    private void sendNewRequest() throws InterruptedException, IOException {
        request = HttpRequest.newBuilder().uri(this.uri)
                .GET().build();
        this.response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    private void updateValues(){
        JSONObject json_response = new JSONObject(this.response);
        this.latitude = json_response.getFloat("latitude");
        this.longitude = json_response.getFloat("longitude");
        this.altitude = json_response.getFloat("altitude");
        this.velocity = json_response.getFloat("velocity");
        this.time = json_response.getInt("timestamp");
    }
}