package org.palladiosimulator.analyzer.slingshot.e2e.test;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.Math;

import org.junit.jupiter.api.Test;
import org.palladiosimulator.analyzer.slingshot.e2e.helpers.SlingshotTestRun;
import org.palladiosimulator.analyzer.slingshot.e2e.helpers.TestModelURIs;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;

import de.uka.ipd.sdq.simucomframework.SimuComConfig;

import org.palladiosimulator.analyzer.slingshot.e2e.helpers.EDP2AccessHelper;

public class SubSystemTest {

	/**
	*	Slingshot implementation of the PCMFork-Test https://palladio-simulator.atlassian.net/browse/SIMULIZAR-106
	*	Tests whether the functionality of nested Subsystems with components is working correctly by using an example model.
	*/
	@Test
	void testNestedSubSystemAllocation() {
		//Create Hashmap to set simulation parameters.
		Map<String, Object> config = new HashMap<String, Object>(); 

	    //Set simulation parameters needed for the nestedSubSystemModel.
	    config.put(SimuComConfig.MAXIMUM_MEASUREMENT_COUNT, "100");
	    
		//Set maximum simulation time
		config.put(SimuComConfig.SIMULATION_TIME, "150000");
	    
	    //Create and run Slingshotsimulation with the nestedSubSystemModel and created Hashmap as config
	    final SlingshotTestRun run = new SlingshotTestRun(new TestModelURIs("nestedSubSystemModel"), config);
	    run.initAndRun();
	    
	    //Create EDP2AccesHelper to access simulationresults
	    final EDP2AccessHelper edp2AccessHelper = new EDP2AccessHelper();
	    
		//Access the resultrepository with the given Specification and read the data as doubles.
	    MeasurementSpecification responseTimeSpec = edp2AccessHelper.getSpec("__fZBELexEeqQOtSQ6pWWhw");
	    List<Double> measurementValues = edp2AccessHelper.getAsRealNumber(responseTimeSpec);
	    
        //Check wether the simulation returned measurements
	    assertFalse(measurementValues.isEmpty(),"No Measurementvalues.");

	    //Create a boolean to keep track if any of the datapoints lie outside the expected range
	    boolean accept = true;

		//Loop over every datapoint
	    for (Double value: measurementValues) {

			/**
			* We have two ranges in which we expect the results to lie.
			* We expect the values to lie not further than 0.0001 away from either 0.1 or 0.2	(0.0999-0.1001 || 0.1999-0.2001)
			* 
			* We calculate the distance of each value towards 0.1 and 0.2
			* If the distance is greater than 0.0001 for both ranges the datapoint can't be in our expected range and we assume a failed test.
			*/
	    	if(Math.abs(value-0.1)>0.0001 && Math.abs(value-0.2)>0.0001) {
	    		accept = false;
	    	}
	    }
	    
		//If the value of accept remained true until here we can confirm that all datapoints are in the expected range
		//so we can validate the test.
	    assertTrue(accept,"Measurements not in expected range.");
	}
	
	
}
