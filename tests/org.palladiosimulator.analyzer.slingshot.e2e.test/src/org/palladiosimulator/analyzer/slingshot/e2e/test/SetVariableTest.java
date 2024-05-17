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

public class SetVariableTest {
	
	@Test
	void testSetVariable() {
		
		Map<String, Object> config = new HashMap<String, Object>(); 
	    
	    config.put("maximumMeasurementCount", 100);
	    config.put("simTime", -1);
	    
	    
	    final SlingshotTestRun run = new SlingshotTestRun(new TestModelURIs("SetVariableModel"), config);
	    run.initAndRun();
	    
	    
	    final EDP2AccessHelper edp2AccessHelper = new EDP2AccessHelper();
	    
	    MeasurementSpecification responseTimeSpec = edp2AccessHelper.getSpec("_yFnS8P8qEemyptI4kDp6-Q");
	    
	    List<Double> measurementValues = edp2AccessHelper.getAsRealNumber(responseTimeSpec);
	    
	    
	    assertTrue(measurementValues.stream().allMatch(value -> value == 50));
	}
	
	
}
