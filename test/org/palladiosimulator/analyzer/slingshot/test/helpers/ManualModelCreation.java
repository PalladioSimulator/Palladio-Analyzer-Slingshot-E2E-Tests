package org.palladiosimulator.analyzer.slingshot.test.helpers;

import org.palladiosimulator.pcm.core.CoreFactory;
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.usagemodel.ClosedWorkload;
import org.palladiosimulator.pcm.usagemodel.Delay;
import org.palladiosimulator.pcm.usagemodel.ScenarioBehaviour;
import org.palladiosimulator.pcm.usagemodel.Start;
import org.palladiosimulator.pcm.usagemodel.Stop;
import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.pcm.usagemodel.UsagemodelFactory;

/**
 * Methods to manually create hardcoded models. I'm not a fan of this and won't
 * use it, probably. It is to error prone in my opinion.
 *
 * @author Saras Stiess
 *
 */
public class ManualModelCreation {

	public static UsageModel createUsageModel() {
		final UsageModel usageModel = UsagemodelFactory.eINSTANCE.createUsageModel();
		final UsageScenario usageScenario = UsagemodelFactory.eINSTANCE.createUsageScenario();

		// workload
		final ClosedWorkload closedWorkload = UsagemodelFactory.eINSTANCE.createClosedWorkload();
		// set bi-directional reference to usage scenario
		closedWorkload.setUsageScenario_Workload(usageScenario);
		closedWorkload.setPopulation(1);
		final PCMRandomVariable thinkTime = CoreFactory.eINSTANCE.createPCMRandomVariable();
		thinkTime.setSpecification("1.0");
		closedWorkload.setThinkTime_ClosedWorkload(thinkTime);

		usageScenario.setWorkload_UsageScenario(closedWorkload);

		// usage behavior
		// entities
		final ScenarioBehaviour behavior = UsagemodelFactory.eINSTANCE.createScenarioBehaviour();
		behavior.setEntityName("scenarioBehavior");
		final Start startEntity = UsagemodelFactory.eINSTANCE.createStart();
		startEntity.setEntityName("start");
		final Delay delayEntity = UsagemodelFactory.eINSTANCE.createDelay();
		final PCMRandomVariable delayTime = CoreFactory.eINSTANCE.createPCMRandomVariable();
		delayTime.setSpecification("1.0");
		delayEntity.setTimeSpecification_Delay(delayTime);
		delayEntity.setEntityName("delay");
		final Stop stopEntity = UsagemodelFactory.eINSTANCE.createStop();
		stopEntity.setEntityName("stop");

		// references
		startEntity.setScenarioBehaviour_AbstractUserAction(behavior);
		startEntity.setSuccessor(delayEntity);
		delayEntity.setSuccessor(stopEntity);
		stopEntity.setPredecessor(delayEntity);

		behavior.setUsageScenario_SenarioBehaviour(usageScenario);
		usageScenario.setScenarioBehaviour_UsageScenario(behavior);
		usageModel.getUsageScenario_UsageModel().add(usageScenario);

		return usageModel;
	}

}
