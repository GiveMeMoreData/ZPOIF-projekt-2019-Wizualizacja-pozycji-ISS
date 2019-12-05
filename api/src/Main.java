import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {



        Api api = new Api();

        while(true) {
            api.requestGET();
            System.out.println("Latitude: "+api.latitude);
            System.out.println("Longitude: "+api.longitude);
            System.out.println("Time: "+api.time);
        }



    }
}
