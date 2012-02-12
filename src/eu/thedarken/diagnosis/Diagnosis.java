package eu.thedarken.diagnosis;
import android.app.Application;
import org.acra.*;
import org.acra.annotation.*;


@ReportsCrashes(formKey = "dExNZ2w0N1pWLW54aTg4RnJBQjV3YVE6MQ")
public class Diagnosis  extends Application {
    @Override
    public void onCreate() {
        // The following line triggers the initialization of ACRA
    	ACRA.init(this);
    	//TODO turn on again
        super.onCreate();
    }
}
