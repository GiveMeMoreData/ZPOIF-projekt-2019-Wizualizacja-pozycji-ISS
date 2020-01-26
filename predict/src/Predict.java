
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.propagation.analytical.tle.TLEPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;
import java.io.File;
import java.io.IOException;

public class Predict {
    private TLEPropagator propagator;
    public double[] latitudes;
    public double[] longitudes;
    private OneAxisEllipsoid earth;
    private TLEApi api;
    private TimeScale utc;
    private double duration = 3600;
    private double stepT = 60;
    private int predictionsAmount = (int) (duration / stepT);
    private Frame frameOfReference;
    private int counter;
    private int maxCounter = 60;


    public Predict(){
        this.counter = maxCounter;
        File orekitData = new File("predict/orekit-data-master");
        DataProvidersManager manager = DataProvidersManager.getInstance();
        manager.addProvider(new DirectoryCrawler(orekitData));

        this.frameOfReference = FramesFactory.getITRF(IERSConventions.IERS_2010, true);
        this.utc = TimeScalesFactory.getUTC();

        this.api = new TLEApi();

        this.earth = new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
                Constants.WGS84_EARTH_FLATTENING,
                frameOfReference);


    }
    public void fetch(){
        updateValues();
    }

    private void updateValues(){
        if(counter < maxCounter){return;}
        this.counter = 0;
        AbsoluteDate initialDate = new AbsoluteDate(String.valueOf(java.time.Clock.systemUTC().instant()), utc);
        try {
            api.fetch();
        } catch (IOException | InterruptedException e) {
            System.out.println("IOException");
        }
        String line1 = api.line1;
        String line2 = api.line2;

        //System.out.println(line1);
        //System.out.println(line2);
        TLE tle = new TLE(line1, line2);
        this.propagator = TLEPropagator.selectExtrapolator(tle);
        propagator.setSlaveMode();
        AbsoluteDate finalDate = initialDate.shiftedBy(duration);
        this.latitudes = new double[predictionsAmount + 1];
        this.longitudes = new double[predictionsAmount + 1];
        int cpt = 0;
        for (AbsoluteDate extrapDate = initialDate;
             extrapDate.compareTo(finalDate) <= 0;
             extrapDate = extrapDate.shiftedBy(stepT))  {
            SpacecraftState currentState = propagator.propagate(extrapDate);
            Vector3D coordinates = currentState.getPVCoordinates(frameOfReference).getPosition();
            GeodeticPoint point = earth.transform(coordinates, frameOfReference, initialDate);
            this.latitudes[cpt] = point.getLatitude() / Math.PI * 180;
            this.longitudes[cpt] = point.getLongitude() / Math.PI * 180;
            cpt ++;
        }

    }

}
