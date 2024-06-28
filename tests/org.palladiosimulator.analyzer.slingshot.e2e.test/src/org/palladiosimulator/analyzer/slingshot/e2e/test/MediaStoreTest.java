package org.palladiosimulator.analyzer.slingshot.e2e.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.palladiosimulator.analyzer.slingshot.e2e.helpers.EDP2AccessHelper;
import org.palladiosimulator.analyzer.slingshot.e2e.helpers.SlingshotTestRun;
import org.palladiosimulator.analyzer.slingshot.e2e.helpers.TestModelURIs;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;

import de.uka.ipd.sdq.simucomframework.SimuComConfig;

public class MediaStoreTest {

	/**
	*	Slingshot implementation of the MediaStore-Test https://palladio-simulator.atlassian.net/browse/SIMULIZAR-110
	*	Tests whether PCM simulation is working by using the example MediaStore Model.
	*/
	@Test
	void mediaStoreTest() {

		//Create Hashmap to set simulation parameters.
		Map<String, Object> config = new HashMap<String, Object>(); 
		
		//Set simulation parameters needed for the MediaStoreModel.
		//Set Simulationtime to a high value to ensure that the simulation finishes with results
	    config.put(SimuComConfig.SIMULATION_TIME, "20000");
	    
		//Create and run Slingshotsimulation with the MediaStoreModel and created Hashmap as config
	    final SlingshotTestRun run = new SlingshotTestRun(new TestModelURIs("MediaStoreModel"), config);
	    run.initAndRun();
	   
	    //Create EDP2AccesHelper to access simulationresults
	    EDP2AccessHelper edp2AccessHelper = new EDP2AccessHelper();
	    
	    //Access the resultrepository with the given Specification and read the data as doubles.
	    MeasurementSpecification spec = edp2AccessHelper.getSpec("__fZBELexEeqQOtSQ6pWWhw");
	    List<Double> measurementValues = edp2AccessHelper.getAsRealNumber(spec);
	    
	    //Check wether the simulation returned measurements
	    assertFalse(measurementValues.isEmpty(),"No Measurementvalues.");

		//The expected range of the values of the datapoints is between 14000 and 32000.
		//If at least 90% of all measurments are in the expected range validate the test.
	    int maxcount = measurementValues.size();
	    int count = 0;
	    
		//Checking for every measurment if in range
	    for(double measurment: measurementValues) {
	    	if(measurment > 14000 && measurment < 32000) {
	    		count++;
	    	}
	    }
	    
		//Accept if at least 90% are in range
	    assertTrue((count/maxcount)-0.9 <= 0.1, "Values not in range");
	    
	}
	
	
}

