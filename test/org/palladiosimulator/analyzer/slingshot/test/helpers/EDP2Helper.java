package org.palladiosimulator.analyzer.slingshot.test.helpers;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.measure.quantity.Quantity;

import org.palladiosimulator.edp2.dao.MeasurementsDao;
import org.palladiosimulator.edp2.dao.exception.DataNotAccessibleException;
import org.palladiosimulator.edp2.models.ExperimentData.DataSeries;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentRun;
import org.palladiosimulator.edp2.models.ExperimentData.Measurement;
import org.palladiosimulator.edp2.models.ExperimentData.RawMeasurements;
import org.palladiosimulator.edp2.models.Repository.Repository;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;
import org.palladiosimulator.edp2.util.MeasurementsUtility;
import org.palladiosimulator.edp2.util.MetricDescriptionUtility;
import org.palladiosimulator.metricspec.BaseMetricDescription;
import org.palladiosimulator.metricspec.CaptureType;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;

/**
 * Helper to access an EDP2 Repository and match the results.
 *
 * @author stiesssh
 *
 */
public class EDP2Helper {

	/** Things with the measurement */
	final Repository repo;
	final ExperimentRun run;

	/**
	 *
	 * @param repo
	 */
	public EDP2Helper(final Repository repo) {

		// only ever 1 because i only run one simulation in tests.
		assert (repo.getExperimentGroups().size() == 1);
		assert (repo.getExperimentGroups().get(0).getExperimentSettings().size() == 1);
		assert (repo.getExperimentGroups().get(0).getExperimentSettings().get(0).getExperimentRuns().size() == 1);

		this.repo = repo;
		this.run = repo.getExperimentGroups().get(0).getExperimentSettings().get(0).getExperimentRuns().get(0);

	}

	/**
	 * Get RawMeasurements for the given MeasuringPoint and MetricDescription
	 *
	 * @param measuringPoint
	 * @param metricSpec
	 * @return
	 */
	public RawMeasurements getRawMeasurements(final MeasuringPoint measuringPoint, final MetricDescription metricSpec) {

		for (final Measurement measurement : run.getMeasurement()) {

			if (measurement.getMeasuringType().getMeasuringPoint().getResourceURIRepresentation() == measuringPoint
					.getResourceURIRepresentation()
					&& measurement.getMeasuringType().getMetric() == metricSpec) {
				assert (measurement.getMeasurementRanges().size() == 1);
				return measurement.getMeasurementRanges().get(0).getRawMeasurements();
			}
		}

		throw new IllegalArgumentException(String.format("No matching measurements for MeasuringPoint %s.",
				measuringPoint.getStringRepresentation()));
	}

	/**
	 * Beware of time/value mixup regarding ResourceDemand.
	 *
	 * @param raws
	 * @return
	 */
	public DataSeries getValues(final RawMeasurements raws) {
		assert (raws.getDataSeries().size() == 2);
		if (raws.getMeasurementRange().getMeasurement().getMeasuringType()
				.getMetric() == MetricDescriptionConstants.RESOURCE_DEMAND_METRIC_TUPLE) {
			return raws.getDataSeries().get(0);
		} else {
			return raws.getDataSeries().get(1);
		}
	}


	/**
	 *
	 * @param actual
	 * @param expected
	 */
	public void matchConstantValue(final DataSeries actual, final Double expected) {

		final BaseMetricDescription metricSpec = this.getMetric(actual);
		assert (metricSpec.getCaptureType() == CaptureType.REAL_NUMBER);

		final MeasurementsDao<Double, ? extends Quantity> dao = (MeasurementsDao<Double, ? extends Quantity>) MeasurementsUtility
				.getMeasurementsDao(actual);

		final List<Double> values = dao.getMeasurements().stream().map(measure -> measure.getValue())
				.collect(Collectors.toList());

		final Double[] foo = new Double[values.size()];
		Arrays.fill(foo, expected);
		final List<Double> expecteds = Arrays.asList(foo);

		matchValues(expecteds, values);

        try {
            dao.close();
        } catch (final DataNotAccessibleException e) {
            e.printStackTrace();
        }


	}

	/**
	 *
	 * @param expected
	 * @param actual
	 */
	private void matchValues(final List<Double> expected, final List<Double> actual) {
		assertEquals("Actual and Expected hold different number of values, cannot compare", expected.size(),
				actual.size());

		for (int i = 0; i < expected.size(); i++) {
			assertEquals(String.format("value differ at [%d], expected <%f> but was <%f>", i, expected.get(i),
					actual.get(i)), expected.get(i), actual.get(i), 0.00000001);
		}
	}

	/**
	 *
	 * @param series
	 * @return
	 */
	private BaseMetricDescription getMetric(final DataSeries series) {
		final RawMeasurements raw = series.getRawMeasurements();
		final int index = raw.getDataSeries().indexOf(series);

		final MetricDescription description = series.getRawMeasurements().getMeasurementRange().getMeasurement()
				.getMeasuringType().getMetric();
		if (description.eContainer() == null) {
			throw new IllegalStateException("MetricDescription not the one from common metrics.");
		}

		return MetricDescriptionUtility.toBaseMetricDescriptions(description)[index];
	}

}
