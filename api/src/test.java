import java.io.IOException;

public class test {

    public static void main(String[] args) throws IOException, InterruptedException {

        BaseApi baseApi = new BaseApi();

        //noinspection InfiniteLoopStatement
        while(true) {
            baseApi.fetch();
            System.out.println("\n Latitude: "+ baseApi.latitude);
            System.out.println("Longitude: "+ baseApi.longitude);
            System.out.println("Altitude: "+ baseApi.altitude);
            System.out.println("Velocity: "+ baseApi.velocity);
            System.out.println("Time: "+ baseApi.time);

            Thread.sleep(500);
        }
    }
}
