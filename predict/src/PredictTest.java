public class PredictTest {
    public static void main(String[] args){
        Predict predict = new Predict();
        for(int i=0; i<10; i++){
            System.out.println(predict.latitudes[i]);
            System.out.println(predict.longitudes[i]);
        }
    }
}
