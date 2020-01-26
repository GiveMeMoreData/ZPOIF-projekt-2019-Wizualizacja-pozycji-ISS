import org.json.JSONObject;
import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TLEApi {

    URI uri;
    private String response;
    public String line1;
    public String line2;
    HttpClient client;
    HttpRequest request;


    public TLEApi() {
        try {
            this.uri= new URI("https://api.wheretheiss.at/v1/satellites/25544/tles");
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
        this.line1 = json_response.getString("line1");
        this.line2 = json_response.getString("line2");
    }
}