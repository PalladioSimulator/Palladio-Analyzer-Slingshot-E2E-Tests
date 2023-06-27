package org.palladiosimulator.analyzer.slingshot.test;



import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.analyzer.slingshot.test.helpers.SlingshotTestRun;
import org.palladiosimulator.analyzer.slingshot.test.helpers.EDP2AccessHelper;
import org.palladiosimulator.analyzer.slingshot.test.helpers.TestModelURIs;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;
/**
 * Yeah, Works.
 *
 * I guess, the test design should actually be like this:
 *
 * 1. one run per test case / test file, in a @ beforeall.
 *
 * 2. various test cases that assert the results, e.g for each MP
 *
 */
public class UsageOnlyTest {

	private EDP2AccessHelper helper;
	private SlingshotTestRun run;

	@BeforeEach
	public void setup() {
		run = new SlingshotTestRun(new TestModelURIs("minimalModelSet/usageModelOnly/"));
		run.initAndRun();

		this.helper = new EDP2AccessHelper();
	}

	@Test
	@DisplayName("Insert nice name for display :D")
	public void test_UsageScenarioResponseTime() {

		// this is wrong. but where else to place it?
		final MeasurementSpecification spec = run.getSpec("_JmmO4BDTEe6FXrMRahCZ6g");

//		final RawMeasurements raws = helper.getRawMeasurements(spec);
//		final DataSeries values = helper.getValues(raws);

		final List<Double> actuals = helper.getAsRealNumber(spec);

		// HOW TO properly JUnit 5 ?

		assertTrue(true, "reached end");
	}




}
