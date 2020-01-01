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

    public Api() {
        try {
            this.uri= new URI("http://api.open-notify.org/iss-now.json");
        } catch (URISyntaxException e) {
            System.out.println("Wrong URL");
        }
    }

    public void fetch() throws InterruptedException, IOException {
        sendNewRequest();
        updateValues();
    }

    private void sendNewRequest() throws InterruptedException, IOException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(this.uri)
                .GET()
                .build();
        this.response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    private void updateValues(){
        JSONObject json_response = new JSONObject(this.response);
        JSONObject position = (JSONObject) json_response.get("iss_position");
        this.latitude = position.getFloat("latitude");
        this.longitude = position.getFloat("longitude");
        this.time = json_response.getInt("timestamp");
    }
}