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

public class ForkExampleModelTest {

	@Test
	void testForkExampleModel() {
		
		Map<String, Object> config = new HashMap<String, Object>(); 
	    
	    config.put("maximumMeasurementCount", 100);
	    config.put("simTime", -1);
	    	    
	    final SlingshotTestRun run = new SlingshotTestRun(new TestModelURIs("Fork_3.1Model"), config);
	    run.initAndRun();
	    
	    
	    final EDP2AccessHelper edp2AccessHelper = new EDP2AccessHelper();
	    
	    MeasurementSpecification responseTimeSpec = edp2AccessHelper.getSpec("_8rfecHtQEd6J8YwisObTKw");
	    
	    List<Double> measurementValues = edp2AccessHelper.getAsRealNumber(responseTimeSpec);
	    
	    
	    assertTrue(measurementValues.stream().allMatch(value -> value == 1.2 || value == 1.0));
	}
	
}
