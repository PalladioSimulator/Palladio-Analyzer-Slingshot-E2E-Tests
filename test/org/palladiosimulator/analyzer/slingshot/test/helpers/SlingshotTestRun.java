package org.palladiosimulator.analyzer.slingshot.test.helpers;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.analyzer.slingshot.core.Slingshot;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.core.extension.PCMResourceSetPartitionProvider;
import org.palladiosimulator.analyzer.slingshot.workflow.WorkflowConfigurationModule;
import org.palladiosimulator.analyzer.workflow.ConstantsContainer;
import org.palladiosimulator.analyzer.workflow.blackboard.PCMResourceSetPartition;
import org.palladiosimulator.analyzer.workflow.jobs.LoadModelIntoBlackboardJob;
import org.palladiosimulator.analyzer.workflow.jobs.PreparePCMBlackboardPartitionJob;
import org.palladiosimulator.edp2.impl.RepositoryManager;
import org.palladiosimulator.edp2.models.Repository.LocalMemoryRepository;
import org.palladiosimulator.edp2.models.Repository.RepositoryFactory;
import org.palladiosimulator.monitorrepository.MeasurementSpecification;
import org.palladiosimulator.monitorrepository.Monitor;
import org.palladiosimulator.monitorrepository.MonitorRepository;

import de.uka.ipd.sdq.simucomframework.SimuComConfig;
import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

/**
 *
 *
 * @author Sarah Stiess
 *
 */
public class SlingshotTestRun {

	/** TODO : is it ever useful to change this from the default ?? */
	final String partitionId = ConstantsContainer.DEFAULT_PCM_INSTANCE_PARTITION_ID;
	final TestModelURIs modelURIs;

	final MDSDBlackboard blackboard;
	final SimuComConfig config;

	/** only one driver per test run!! */
	final SimulationDriver simulationDriver;

	public SlingshotTestRun(final TestModelURIs modelURIs,
			final Map<String, Object> launchConfigurationParams) {
		this.modelURIs = modelURIs;

		this.blackboard = this.initBlackboard();
		this.config = this.createConfig(launchConfigurationParams);

		this.simulationDriver = Slingshot.getInstance().getSimulationDriver();

		this.configureProviders();
	}

	/**
	 * Create SlingshotTestRun with default SimuComConfig.
	 *
	 * @param modelURIs
	 */
	public SlingshotTestRun(final TestModelURIs modelURIs) {
		this(modelURIs, new HashMap<>());
	}

	public SimulationDriver getDriver() {
		return this.simulationDriver;
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
	 * Initialise the Simulation Driver and start the Simulation run.
	 */
	public void initAndRun() {
		final SimuComConfig config = Slingshot.getInstance().getInstance(SimuComConfig.class);

		simulationDriver.init(config, new NullProgressMonitor());
		simulationDriver.start();
	}

	/**
	 * Takes care of configuring Providers, that are usually configured via the
	 * RunConfiguration UI.
	 *
	 */
	private void configureProviders() {
		// provide blackboard.
		WorkflowConfigurationModule.blackboardProvider.set(this.blackboard);

		// provide partition
		final PCMResourceSetPartitionProvider pcmResourceSetPartition = Slingshot.getInstance()
				.getInstance(PCMResourceSetPartitionProvider.class);
		final PCMResourceSetPartition partition = (PCMResourceSetPartition) blackboard
				.getPartition(this.partitionId);
		pcmResourceSetPartition.set(partition);

		// rovide simuComConfig
		WorkflowConfigurationModule.simuComConfigProvider.set(this.config);
	}

	/**
	 *
	 * Prepare blackboard.
	 *
	 * Initialise partitions and load models into partition.
	 *
	 * @return blackboard ready for running a simulation.
	 */
	private MDSDBlackboard initBlackboard() {
		final MDSDBlackboard blackboard = new MDSDBlackboard();
		initPartition(blackboard);

		for (final URI location : this.modelURIs.getModelLocations()) {
			this.readModelWithJob(location, blackboard);
		}
		return blackboard;
	}

	/**
	 *
	 * Use {@link PreparePCMBlackboardPartitionJob} to prepare default partition,
	 * i.e. with id {@code ConstantsContainer.DEFAULT_PCM_INSTANCE_PARTITION_ID} in
	 * given blackboard.
	 *
	 * @param blackboard blackboard to be prepared.
	 */
	private void initPartition(final MDSDBlackboard blackboard) {
		try {
			final PreparePCMBlackboardPartitionJob job = new PreparePCMBlackboardPartitionJob();
			job.setBlackboard(blackboard);
			job.execute(new NullProgressMonitor());
		} catch (JobFailedException | UserCanceledException e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * Create minimal SimuComConfig, i.e. setting all parameters that cause crashes
	 * if missing.
	 *
	 * TODO : only add missing configuration Parameters!!!
	 *
	 * @param launchConfigurationParams
	 * @return
	 */
	private SimuComConfig createConfig(final Map<String, Object> launchConfigurationParams) {
		/* Purpose unclear */
		launchConfigurationParams.putIfAbsent(SimuComConfig.VERBOSE_LOGGING, false);
		launchConfigurationParams.putIfAbsent(SimuComConfig.SIMULATOR_ID, SimuComConfig.DEFAULT_SIMULATOR_ID);

		/* Random Seeds */
		launchConfigurationParams.putIfAbsent(SimuComConfig.USE_FIXED_SEED, false);
		// TODO : fixed seeds?

		/* Simulation Stop Conditions */
		launchConfigurationParams.putIfAbsent(SimuComConfig.SIMULATION_TIME, "10");
		launchConfigurationParams.putIfAbsent(SimuComConfig.MAXIMUM_MEASUREMENT_COUNT,
				SimuComConfig.DEFAULT_MAXIMUM_MEASUREMENT_COUNT);

		/* EDD2 MetaData */
		launchConfigurationParams.putIfAbsent(SimuComConfig.VARIATION_ID, SimuComConfig.DEFAULT_VARIATION_NAME); // experimentSettings.description
		launchConfigurationParams.putIfAbsent(SimuComConfig.EXPERIMENT_RUN, SimuComConfig.DEFAULT_EXPERIMENT_RUN); // experimentGroup.purpose

		/* EDP2 Recorder ("Persistence Framework" in UI, must be exactly that string) */
		launchConfigurationParams.putIfAbsent(SimuComConfig.PERSISTENCE_RECORDER_NAME,
				"Experiment Data Persistency & Presentation (EDP2)");

		/* EDP2 Repository ("Data source" in UI) */
		final LocalMemoryRepository localRepo = RepositoryFactory.eINSTANCE.createLocalMemoryRepository();
		RepositoryManager.addRepository(RepositoryManager.getCentralRepository(), localRepo);
		launchConfigurationParams.putIfAbsent("EDP2RepositoryID", localRepo.getId());

		return new SimuComConfig(launchConfigurationParams, false);
	}

	/**
	 * Use {@link LoadModelIntoBlackboardJob} to load model into blackboard.
	 *
	 * @param absolutePath absolute path to resource without leading "/"
	 * @param blackboard   blackboard to load model into.
	 */
	private void readModelWithJob(final URI absolutePath, final MDSDBlackboard blackboard) {
		try {

			final LoadModelIntoBlackboardJob loadJob = new LoadModelIntoBlackboardJob(absolutePath, this.partitionId);
			loadJob.setBlackboard(blackboard);
			loadJob.execute(new NullProgressMonitor());

		} catch (JobFailedException | UserCanceledException e) {
			e.printStackTrace();
		}
	}

}
