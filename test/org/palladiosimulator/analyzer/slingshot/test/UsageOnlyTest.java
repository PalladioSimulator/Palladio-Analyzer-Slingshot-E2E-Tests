package org.palladiosimulator.analyzer.slingshot.test;



import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.analyzer.slingshot.test.helpers.EDP2AccessHelper;
import org.palladiosimulator.analyzer.slingshot.test.helpers.SlingshotTestRun;
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

	@BeforeAll
	static void setup() {
		final SlingshotTestRun run = new SlingshotTestRun(new TestModelURIs("minimalModelSet/usageModelOnly/"));
		run.initAndRun();
	}

	@Test
	@DisplayName("Insert nice name for display :D")
	void test_UsageScenarioResponseTime() {

		final EDP2AccessHelper helper = new EDP2AccessHelper();
		final MeasurementSpecification spec = helper.getSpec("_JmmO4BDTEe6FXrMRahCZ6g");


		final List<Double> actuals = helper.getAsRealNumber(spec);

		// assertThat(actuals).isNotEmpty().isEqualTo(List.of(1.0, 1.0, 1.0, 1.0, 1.0));

		assertThat(actuals).isNotEmpty().allMatch(v -> Math.abs(v - 1.0) < 0.00001);
	}
}
