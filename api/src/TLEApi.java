import org.json.JSONObject;
import java.net.*;

public class TLEApi extends Api{

    public String line1;
    public String line2;

    public TLEApi() {
        super();
        try {
            this.uri= new URI("https://api.wheretheiss.at/v1/satellites/25544/tles");
        } catch (URISyntaxException e) {
            System.out.println("Could not connect to API");
        }
    }

    protected void updateValues(){
        JSONObject json_response = new JSONObject(this.response);
        this.line1 = json_response.getString("line1");
        this.line2 = json_response.getString("line2");
    }
}