package org.palladiosimulator.analyzer.slingshot.e2e.test;

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

public class SetVariableTest {
	
	/**
	*	Slingshot implementation of the PCMSetVariable-Test https://palladio-simulator.atlassian.net/browse/SIMULIZAR-103
	*	Tests the functionality of setting variables in the simulation.
	*/
	@Test
	void testSetVariable() {
		//Create Hashmap to set simulation parameters.
		Map<String, Object> config = new HashMap<String, Object>(); 
	   
	    //Set simulation parameters needed for the SetVariableModel.
	    config.put(SimuComConfig.MAXIMUM_MEASUREMENT_COUNT, "100");
	    
	    //This breaks the SlingshotTestRun. Needs further testing if even necessary
	    //config.put(SimuComConfig.SIMULATION_TIME, "-1");
	    
	    //Create and run Slingshotsimulation with the SetVariableModel and created Hashmap as config
	    final SlingshotTestRun run = new SlingshotTestRun(new TestModelURIs("SetVariableModel"), config);
	    run.initAndRun();
	    
	    //Create EDP2AccesHelper to access simulationresults
	    final EDP2AccessHelper edp2AccessHelper = new EDP2AccessHelper();
	    
		//Access the resultrepository with the given Specification and read the data as doubles.
	    MeasurementSpecification responseTimeSpec = edp2AccessHelper.getSpec("_yFnS8P8qEemyptI4kDp6-Q");
	    List<Double> measurementValues = edp2AccessHelper.getAsRealNumber(responseTimeSpec);
	    
	    //The expected value of every datapoint is 50.
		//If every datapoint equals the expected value we validate the test.
	    assertTrue(measurementValues.stream().allMatch(value -> value == 50));
	}
	
	
}
