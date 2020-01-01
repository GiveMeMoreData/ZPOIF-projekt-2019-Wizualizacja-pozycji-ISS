import java.io.IOException;

public class test {

    public static void main(String[] args) throws IOException, InterruptedException {

        Api api = new Api();

        while(true) {
            api.fetch();
            System.out.println("\n Latitude: "+api.latitude);
            System.out.println("Longitude: "+api.longitude);
            System.out.println("Time: "+api.time);
            Thread.sleep(1000);
        }
    }
}
