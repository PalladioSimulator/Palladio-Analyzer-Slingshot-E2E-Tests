package org.palladiosimulator.analyzer.slingshot.test;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.palladiosimulator.analyzer.slingshot.core.Slingshot;
import org.palladiosimulator.analyzer.slingshot.test.helpers.SlingshotTestRun;
import org.palladiosimulator.analyzer.slingshot.test.helpers.EDP2AccessHelper;
import org.palladiosimulator.analyzer.slingshot.test.helpers.TestModelURIs;
import org.palladiosimulator.edp2.impl.RepositoryManager;
import org.palladiosimulator.edp2.models.ExperimentData.DataSeries;
import org.palladiosimulator.edp2.models.ExperimentData.RawMeasurements;
import org.palladiosimulator.edp2.models.Repository.Repository;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;
import org.palladiosimulator.monitorrepository.Monitor;
import org.palladiosimulator.monitorrepository.MonitorRepository;

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
public class MinimalTest {

	@Test
	public void test_delay() {
		final SlingshotTestRun run = new SlingshotTestRun(new TestModelURIs("minimalModelSet/usageModelOnly/"));
		run.initAndRun();

		final EDP2AccessHelper edp2 = new EDP2AccessHelper(this.getRepository());


		final MeasurementSpecification spec = this.getSpec("_JmmO4BDTEe6FXrMRahCZ6g");

		final RawMeasurements raws = edp2.getRawMeasurements(spec);

		final DataSeries values = raws.getDataSeries().get(1);

		// edp2.matchConstantValue(values, 1.0);

		assertTrue(true, "reached end");
	}

	@Test
	public void test_minimal() {
		final SlingshotTestRun run = new SlingshotTestRun(new TestModelURIs("minimalModelSet/MinimalModel/"));
		run.initAndRun();

		final EDP2AccessHelper edp2 = new EDP2AccessHelper(this.getRepository());

		final MeasuringPoint mp = getMP();

		final RawMeasurements raws = edp2.getRawMeasurements(mp, MetricDescriptionConstants.RESPONSE_TIME_METRIC_TUPLE);

		final DataSeries values = raws.getDataSeries().get(1);

		// edp2.matchConstantValue(values, 1.0);

		assertTrue(true, "reached end");
	}

	/**
	 * Maybe i should create Monitors and MPs in code, such that i *know* about
	 * them?
	 *
	 * @return
	 */
	private MeasuringPoint getMP() {
		final MonitorRepository repo = Slingshot.getInstance().getInstance(MonitorRepository.class);

		return repo.getMonitors().get(0).getMeasuringPoint();
	}

	private MeasurementSpecification getSpec(final String id) {
		final MonitorRepository repo = Slingshot.getInstance().getInstance(MonitorRepository.class);

		for (final Monitor monitor : repo.getMonitors()) {
			for (final MeasurementSpecification spec : monitor.getMeasurementSpecifications()) {
				if (spec.getId().equals(id)) {
					return spec;
				}
			}
		}
		throw new IllegalArgumentException(String.format("No MeasurementSpecification for id %s found.", id));
	}

	private Repository getRepository() {
		final List<Repository> repos = RepositoryManager.getCentralRepository().getAvailableRepositories();
		assertEquals(1, repos.size(), "repository is missing.");
		return repos.get(0);
	}
}
