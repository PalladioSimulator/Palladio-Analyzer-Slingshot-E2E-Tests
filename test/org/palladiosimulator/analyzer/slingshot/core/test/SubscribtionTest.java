package org.palladiosimulator.analyzer.slingshot.core.test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.Subscriber;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;
import org.palladiosimulator.analyzer.slingshot.test.helpers.SlingshotTestRun;
import org.palladiosimulator.analyzer.slingshot.test.helpers.TestModelURIs;

/**
 *
 * i can also subscribe as a Lambda Subscriber.
 *
 * However i can not use annotations, because the class must be a
 * SlingshotBehaviourExtension to go for annotations and that apparently clashes
 * with the JUnit framework.
 *
 * GOAL : assert sequences of events in an state machine like fashion (d.f. jan
 * Haas)
 *
 *
 */
public class SubscribtionTest {

	boolean result = false;

	@Test
	public void test_subscriptor() {
		final SlingshotTestRun run = new SlingshotTestRun(new TestModelURIs("minimalModelSet/usageModelOnly/"));

		// this works
		final Subscriber<SimulationStarted> handler = Subscriber.builder(SimulationStarted.class)
				.name("subscriberTestSubcriber").handler(e -> {
					System.out.println("WERE HERE " + e.getName());
					this.result = true;
					return Result.of();
				}).build();

		final SimulationDriver simulationDriver = run.getDriver();
		simulationDriver.registerEventHandler(handler);

		run.initAndRun();

		assertTrue(result, "handler success");
		assertTrue(true, "reached end");
	}
}
