package org.palladiosimulator.analyzer.slingshot.e2e.test;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.palladiosimulator.analyzer.slingshot.e2e.helpers.SlingshotTestRun;
import org.palladiosimulator.analyzer.slingshot.e2e.helpers.TestModelURIs;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;

import de.uka.ipd.sdq.simucomframework.SimuComConfig;

import org.palladiosimulator.analyzer.slingshot.e2e.helpers.EDP2AccessHelper;

/**
 *
 */
public class LinkingResourceTest {


	    
	   /**
		*	Slingshot implementation of the LinkingResource-Test https://palladio-simulator.atlassian.net/browse/SIMULIZAR-104
		*	Tests whether the network simulation works without middleware marshalling / demarshalling.
		*/


		//Test with simulationparameter simulateLinkingResources enabled
	    @Test
	    void testLinkingResourceThroughputSimulation() {
	    	
			//Create Hashmap to set simulation parameters.
	    	Map<String, Object> config = new HashMap<String, Object>(); 

			//Set simulateLinkingResources as enabled
	    	config.put(SimuComConfig.SIMULATE_THROUGHPUT_OF_LINKING_RESOURCES, true);
			//Set maximum measurments the simulation will take
	    	config.put(SimuComConfig.MAXIMUM_MEASUREMENT_COUNT, "10000");
			//Set maximum time simulation will run
	    	config.put(SimuComConfig.SIMULATION_TIME, "20000");

			//Create and run Slingshotsimulation with an examplemodel and created Hashmap as config
	    	final SlingshotTestRun run = new SlingshotTestRun(new TestModelURIs("LinkingResourceModel"), config );
			run.initAndRun();
	    	
			//Create EDP2AccesHelper to access simulationresults
			final EDP2AccessHelper edp2AccessHelper = new EDP2AccessHelper();
			
	        // Get measurement specification for response time metric tuple
	    	MeasurementSpecification responseTimeSpec = edp2AccessHelper.getSpec("_kpMM87esEeqCx9D3JGHCDw");

	        // Get measurement values as real numbers
	        List<Double> measurementValues = edp2AccessHelper.getAsRealNumber(responseTimeSpec);

	        //Check wether the simulation returned measurements
		    assertFalse(measurementValues.isEmpty(),"No Measurementvalues.");

	        // Validate measurement values against expected range
	        assertTrue(measurementValues.stream().allMatch(value -> value > 184.0 && value < 185.0),"Measurements not in expected range.");
	    }
	    
		//Test with simulationparameter simulateLinkingResources disabled
	    @Test
	    void testNoLinkingResourceSimulation() {
	    	
			//Create Hashmap to set simulation parameters.
	    	Map<String, Object> config = new HashMap<String, Object>(); 

			//Set simulateLinkingResources as disabled
    	    config.put(SimuComConfig.SIMULATE_THROUGHPUT_OF_LINKING_RESOURCES, false);
    	    //Set maximum measurments the simulation will take
	    	config.put(SimuComConfig.MAXIMUM_MEASUREMENT_COUNT, "10000");
			//Set maximum time simulation will run
	    	config.put(SimuComConfig.SIMULATION_TIME, "20000");

			//Create and run Slingshotsimulation with an examplemodel and created Hashmap as config
	    	final SlingshotTestRun run = new SlingshotTestRun(new TestModelURIs("LinkingResourceModel"), config);
			run.initAndRun();
			
			//Create EDP2AccesHelper to access simulationresults
			final EDP2AccessHelper edp2AccessHelper = new EDP2AccessHelper();
			
	        // Get measurement specification for response time metric tuple
	        MeasurementSpecification responseTimeSpec = edp2AccessHelper.getSpec("_kpMM87esEeqCx9D3JGHCDw");

	        // Get measurement values as real numbers
	        List<Double> measurementValues = edp2AccessHelper.getAsRealNumber(responseTimeSpec);

	        //Check wether the simulation returned measurements
		    assertFalse(measurementValues.isEmpty(),"No Measurementvalues.");

	        // Validate measurement values against expected range
	        assertTrue(measurementValues.stream().allMatch(value -> value > 1.0 && value < 2.0),"Measurements not in expected range.");
	    }
	

}