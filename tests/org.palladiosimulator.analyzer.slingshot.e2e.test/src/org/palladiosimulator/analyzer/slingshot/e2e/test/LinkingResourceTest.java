package org.palladiosimulator.analyzer.slingshot.e2e.test;


import static org.junit.jupiter.api.Assertions.assertTrue;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.Subscriber;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;
import org.palladiosimulator.analyzer.slingshot.e2e.helpers.SlingshotTestRun;
import org.palladiosimulator.analyzer.slingshot.e2e.helpers.TestModelURIs;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;
import org.palladiosimulator.analyzer.slingshot.e2e.helpers.EDP2AccessHelper;
import org.palladiosimulator.analyzer.slingshot.e2e.helpers.ManualModelCreation;

/**
 *
 */
public class LinkingResourceTest {

final EDP2AccessHelper edp2AccessHelper = new EDP2AccessHelper();
	    
	   
	    @Test
	    void testLinkingResourceThroughputSimulation() {
	    	
	    	Map<String, Object> config = new HashMap<String, Object>(); 
	    	    config.put("SIMULATE_LINKING_RESOURCES", 1);
	    	
	    	
	    	final SlingshotTestRun run = new SlingshotTestRun(new TestModelURIs("usageModelOnly"), config );
			run.initAndRun();
	    	
	        // Get measurement specification for response time metric tuple
	    	MeasurementSpecification responseTimeSpec = edp2AccessHelper.getSpec("_JmmO4BDTEe6FXrMRahCZ6g");

	        // Get measurement values as real numbers
	        List<Double> measurementValues = edp2AccessHelper.getAsRealNumber(responseTimeSpec);

	        // Validate measurement values against expected range
	        assertTrue(measurementValues.stream().allMatch(value -> value > 184.0 && value < 185.0));
	    }
	    
	    @Test
	    void testNoLinkingResourceSimulation() {
	    	
	    	Map<String, Object> config = new HashMap<String, Object>(); 
    	    config.put("SIMULATE_LINKING_RESOURCES", 0);
    	    
	    	final SlingshotTestRun run = new SlingshotTestRun(new TestModelURIs("usageModelOnly"), config);
			run.initAndRun();
			
	        // Get measurement specification for response time metric tuple
	        var responseTimeSpec = edp2AccessHelper.getSpec("_JmmO4BDTEe6FXrMRahCZ6g");

	        // Get measurement values as real numbers
	        List<Double> measurementValues = edp2AccessHelper.getAsRealNumber(responseTimeSpec);

	        // Validate measurement values against expected range
	        assertTrue(measurementValues.stream().allMatch(value -> value > 1.0 && value < 2.0));
	    }
	

}
