package org.palladiosimulator.analyzer.slingshot.e2e.test;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.Math;

import org.junit.jupiter.api.Test;
import org.palladiosimulator.analyzer.slingshot.e2e.helpers.SlingshotTestRun;
import org.palladiosimulator.analyzer.slingshot.e2e.helpers.TestModelURIs;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;
import org.palladiosimulator.analyzer.slingshot.e2e.helpers.EDP2AccessHelper;

public class SubSystemTest {

	@Test
	void testNestedSubSystemAllocation() {
		
		Map<String, Object> config = new HashMap<String, Object>(); 
	    
	    config.put("maximumMeasurementCount", 10);
	    
	    
	    
	    final SlingshotTestRun run = new SlingshotTestRun(new TestModelURIs("nestedSubSystemModel"), config);
	    run.initAndRun();
	    
	    
	    final EDP2AccessHelper edp2AccessHelper = new EDP2AccessHelper();
	    
	    MeasurementSpecification responseTimeSpec = edp2AccessHelper.getSpec("__fZBELexEeqQOtSQ6pWWhw");
	    
	    List<Double> measurementValues = edp2AccessHelper.getAsRealNumber(responseTimeSpec);
	    
	    
	    boolean accept = true;
	    for (Double value: measurementValues) {
	    	if(Math.abs(value-0.1)>0.0001 && Math.abs(value-0.2)>0.0001) {
	    		accept = false;
	    	}
	    }
	    
	    assertTrue(accept);
	}
	
	
}
