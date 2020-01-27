public class PredictTest {
    public static void main(String[] args){
        Predict predict = new Predict();
        predict.fetch();
        for(int i=0; i<10; i++){
            System.out.println("Step: " + i);
            System.out.println("Latitude: " + predict.latitudes[i]);
            System.out.println("Longiude:" + predict.longitudes[i]);
        }
    }
}
