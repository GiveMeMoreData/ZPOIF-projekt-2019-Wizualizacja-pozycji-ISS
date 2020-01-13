import java.io.IOException;

public class test {

    public static void main(String[] args) throws IOException, InterruptedException {

        Api api = new Api();

        //noinspection InfiniteLoopStatement
        while(true) {
            api.fetch();
            System.out.println("\n Latitude: "+api.latitude);
            System.out.println("Longitude: "+api.longitude);
            System.out.println("Altitude: "+api.altitude);
            System.out.println("Velocity: "+api.velocity);
            System.out.println("Time: "+api.time);

            Thread.sleep(500);
        }
    }
}
