package org.palladiosimulator.analyzer.slingshot.e2e.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.palladiosimulator.analyzer.slingshot.e2e.helpers.EDP2AccessHelper;
import org.palladiosimulator.analyzer.slingshot.e2e.helpers.SlingshotTestRun;
import org.palladiosimulator.analyzer.slingshot.e2e.helpers.TestModelURIs;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;

public class MediaStoreTest {

	@Test
	void mediaStoreTest() {
		
		Map<String, Object> config = new HashMap<String, Object>(); 
		
	    config.put("simTime", -1);
	    
	    final SlingshotTestRun run = new SlingshotTestRun(new TestModelURIs("MediaStoreModel"), config);
	    run.initAndRun();
	    
	    EDP2AccessHelper edp2AccessHelper = new EDP2AccessHelper();
	    
	    
	    MeasurementSpecification spec = edp2AccessHelper.getSpec("__fZBELexEeqQOtSQ6pWWhw");
	    
	    List<Double> measurementValues = edp2AccessHelper.getAsRealNumber(spec);
	    
	    assertTrue(measurementValues.stream().allMatch(value -> value <= 32 && value >=14 ));
	    
	}
	
	
}

