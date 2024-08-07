package org.palladiosimulator.analyzer.slingshot.e2e.test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.palladiosimulator.analyzer.slingshot.e2e.helpers.EDP2AccessHelper;
import org.palladiosimulator.analyzer.slingshot.e2e.helpers.SlingshotTestRun;
import org.palladiosimulator.analyzer.slingshot.e2e.helpers.TestModelURIs;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;

import de.uka.ipd.sdq.simucomframework.SimuComConfig;

public class ForkExampleModelTest {

	/**
	*	Slingshot implementation of the PCMFork-Test https://palladio-simulator.atlassian.net/browse/SIMULIZAR-107
	*	Tests whether ForActions are simulated correctly.
	*/
	void testForkExampleModel() {
		
		//Create Hashmap to set simulation parameters.
		Map<String, Object> config = new HashMap<String, Object>(); 
	    
		//Set simulation parameters needed for the Fork_3.1Model.
	    config.put(SimuComConfig.MAXIMUM_MEASUREMENT_COUNT, "100");
	    
	    //Set Simulationtime to a high value to ensure that the simulation finishes with results
	    config.put(SimuComConfig.SIMULATION_TIME, "20000");

		//Create and run Slingshotsimulation with the Fork_3.1Model and created Hashmap as config	.    
	    final SlingshotTestRun run = new SlingshotTestRun(new TestModelURIs("Fork_3.1Model"), config);
	    run.initAndRun();
	    
	    //Create EDP2AccesHelper to access simulationresults
	    final EDP2AccessHelper edp2AccessHelper = new EDP2AccessHelper();
	    
		//Access the resultrepository with the given Specification and read the data as doubles.
	    MeasurementSpecification responseTimeSpec = edp2AccessHelper.getSpec("_8rfecHtQEd6J8YwisObTKw");
	    List<Double> measurementValues = edp2AccessHelper.getAsRealNumber(responseTimeSpec);
	    
        //Check wether the simulation returned measurements
	    assertFalse(measurementValues.isEmpty(),"No Measurementvalues.");

		//The expected result is that every datapoint is either 1.2 or 1.0.
	    //Check if every datapoint is one of the expected values and if so validate the test.
	    assertTrue(measurementValues.stream().allMatch(value -> value == 1.2 || value == 1.0),"Measurements not in expected range.");
	}
	
}
