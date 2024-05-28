package org.palladiosimulator.analyzer.slingshot.e2e.helpers;

import java.util.List;
import java.util.stream.Collectors;

import javax.measure.quantity.Quantity;

import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.analyzer.slingshot.core.Slingshot;
import org.palladiosimulator.edp2.dao.MeasurementsDao;
import org.palladiosimulator.edp2.impl.RepositoryManager;
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
import org.palladiosimulator.monitorrepository.MeasurementSpecification;
import org.palladiosimulator.monitorrepository.Monitor;
import org.palladiosimulator.monitorrepository.MonitorRepository;

/**
 * Helper to access an EDP2 Repository and match the results.
 *
 * @author stiesssh
 *
 */
public class EDP2AccessHelper {

	/** Things with the measurement */
	final Repository repo;
	final ExperimentRun run;

	/**
	 *
	 * @param repo
	 */
//	@Deprecated
//	public EDP2AccessHelper(final Repository repo) {
//
//		// only ever 1 because i only run one simulation in tests.
//		assert (repo.getExperimentGroups().size() == 1);
//		assert (repo.getExperimentGroups().get(0).getExperimentSettings().size() == 1);
//		assert (repo.getExperimentGroups().get(0).getExperimentSettings().get(0).getExperimentRuns().size() == 1);
//
//		this.repo = repo;
//		this.run = repo.getExperimentGroups().get(0).getExperimentSettings().get(0).getExperimentRuns().get(0);
//
//	}


	public EDP2AccessHelper() {
		final List<Repository> repos = RepositoryManager.getCentralRepository().getAvailableRepositories();
		assert (repos.size() == 1);

		assert (repos.get(0).getExperimentGroups().size() == 1);
		assert (repos.get(0).getExperimentGroups().get(0).getExperimentSettings().size() == 1);
		assert (repos.get(0).getExperimentGroups().get(0).getExperimentSettings().get(0).getExperimentRuns()
				.size() == 1);

		this.repo = repos.get(0);
		this.run = repo.getExperimentGroups().get(0).getExperimentSettings().get(0).getExperimentRuns().get(0);

	}

	/**
	 *
	 * @param id
	 * @return
	 */
	public MeasurementSpecification getSpec(final String id) {
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

	/**
	 * Get RawMeasurements for the given MeasuringPoint and MetricDescription
	 *
	 * @param measuringPoint
	 * @param metricSpec
	 * @return
	 */
	public RawMeasurements getRawMeasurements(final MeasuringPoint measuringPoint, final MetricDescription metricSpec) {

		for (final Measurement measurement : run.getMeasurement()) {

			// URI does not match because platform vs. bundle resource. wtf.
			if (this.measuringPointsEquals(measurement.getMeasuringType().getMeasuringPoint(), measuringPoint)
					&& this.metricDescriptionEqualOrSubsumed(measurement.getMeasuringType().getMetric(), metricSpec)) {
				assert (measurement.getMeasurementRanges().size() == 1);
				return measurement.getMeasurementRanges().get(0).getRawMeasurements();
			}
		}

		throw new IllegalArgumentException(String.format("No matching measurements for MeasuringPoint %s.",
				measuringPoint.getStringRepresentation()));
	}

	/**
	 *
	 * @param spec
	 * @return
	 */
	public RawMeasurements getRawMeasurements(final MeasurementSpecification spec) {
		final MeasuringPoint mp = spec.getMonitor().getMeasuringPoint();
		final MetricDescription metricSpec = spec.getMetricDescription();

		return getRawMeasurements(mp, metricSpec);
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
	 * Beware of time/value mixup regarding ResourceDemand.
	 *
	 * @param raws
	 * @return
	 */
	public DataSeries getValues(final MeasurementSpecification spec) {
		return this.getValues(this.getRawMeasurements(spec));
	}

	/**
	 *
	 * TODO : not yet implemented!
	 *
	 * @param spec
	 * @return
	 */
	public List<?> getAsIdentifier(final MeasurementSpecification spec) {
		final DataSeries series = getValues(spec);
		assert (this.getMetric(series).getCaptureType() == CaptureType.IDENTIFIER);
		// TODO what do identifier measures even look like?!?!

		return null;
	}

	/**
	 *
	 * @param spec
	 * @return
	 */
	public List<Integer> getAsIntegerNumber(final MeasurementSpecification spec) {
		final DataSeries series = getValues(spec);
		assert (this.getMetric(series).getCaptureType() == CaptureType.INTEGER_NUMBER);


		final MeasurementsDao<Integer, ? extends Quantity> dao = (MeasurementsDao<Integer, ? extends Quantity>) MeasurementsUtility
				.getMeasurementsDao(series);
		final List<Integer> values = dao.getMeasurements().stream().map(measure -> measure.getValue())
				.collect(Collectors.toList());

		return values;
	}

	/**
	 *
	 * @param spec
	 * @return
	 */
	public List<Double> getAsRealNumber(final MeasurementSpecification spec) {
		final DataSeries series = getValues(spec);
		assert (this.getMetric(series).getCaptureType() == CaptureType.REAL_NUMBER);
		final MeasurementsDao<Double, ? extends Quantity> dao = (MeasurementsDao<Double, ? extends Quantity>) MeasurementsUtility
				.getMeasurementsDao(series);

		final List<Double> values = dao.getMeasurements().stream().map(measure -> measure.getValue())
				.collect(Collectors.toList());

		return values;
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

	/**
	 *
	 * I want you to know that i hate everyone and their mother for creating
	 * measuringpoint without a fucking id. I also want you to know that i hate
	 * everyone and their mother for saving URIs as String.
	 *
	 * @param mp1
	 * @param mp2
	 * @return true iff mp1 and mp2 reference the same PCM element.
	 */
	private boolean measuringPointsEquals(final MeasuringPoint mp1, final MeasuringPoint mp2) {
		final URI uri1 = URI.createURI(mp1.getResourceURIRepresentation());
		final URI uri2 = URI.createURI(mp2.getResourceURIRepresentation());

		if (uri1.segmentCount() != uri2.segmentCount()) {
			return false;
		}
		for (int i = 0; i < uri1.segmentCount(); i++) {
			if (!uri1.segment(i).equals(uri2.segment(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 *
	 * @param desc1
	 * @param desc2
	 * @return
	 */
	private boolean metricDescriptionEqualOrSubsumed(final MetricDescription desc1, final MetricDescription desc2) {
		if (desc1.getClass().equals(desc2.getClass())) {
			return desc1 == desc2;
			// does this even work?
		}
		if (desc2 instanceof BaseMetricDescription) {
			return MetricDescriptionUtility
					.isBaseMetricDescriptionSubsumedByMetricDescription((BaseMetricDescription) desc2, desc1);
		}
		if (desc1 instanceof BaseMetricDescription) {
			return MetricDescriptionUtility
					.isBaseMetricDescriptionSubsumedByMetricDescription((BaseMetricDescription) desc1, desc2);
		}
		return false;
	}
}
