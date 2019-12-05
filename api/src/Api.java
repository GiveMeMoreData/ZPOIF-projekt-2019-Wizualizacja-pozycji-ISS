import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Api {

    private BufferedReader rawResponse;
    private StringBuilder request;
    private URL url;
    public float latitude;
    public float longitude;
    public  int time;

    public Api() throws MalformedURLException {
        this.url= new URL("http://api.open-notify.org/iss-now.json");
    }


    public void requestGET() throws IOException {

        // Sends new GET request to url
        sendNewRequest();

        // Reading data from request
        readRequest();

        // Updates 3 values this api class returns
        updateValues();
    }

    private void sendNewRequest() throws IOException {
        this.url.getContent(); // sends new GET request
        HttpURLConnection con = (HttpURLConnection) this.url.openConnection();
        rawResponse = new BufferedReader(new InputStreamReader(con.getInputStream()));
    }

    private void readRequest() throws IOException {
        String inputLine;
        request = new StringBuilder();
        while ((inputLine = rawResponse.readLine()) != null) {request.append(inputLine);}
        rawResponse.close();
    }

    private void updateValues(){
        JSONObject json_response = new JSONObject(request.toString());
        JSONObject position = (JSONObject) json_response.get("iss_position");

        this.latitude = position.getFloat("latitude");
        this.longitude = position.getFloat("longitude");
        this.time = json_response.getInt("timestamp");
    }
}