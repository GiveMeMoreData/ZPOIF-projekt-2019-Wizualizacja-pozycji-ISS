import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public abstract class Api {
    URI uri;
    private HttpClient client;
    private HttpRequest request;
    String response;

    public Api(){
        this.client = HttpClient.newHttpClient();
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

    protected abstract void updateValues();
}
