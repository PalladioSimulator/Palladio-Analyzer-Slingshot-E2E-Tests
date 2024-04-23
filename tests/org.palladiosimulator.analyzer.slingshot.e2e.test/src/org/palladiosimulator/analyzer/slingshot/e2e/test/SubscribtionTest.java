package org.palladiosimulator.analyzer.slingshot.e2e.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.palladiosimulator.analyzer.slingshot.core.api.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.Subscriber;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;
import org.palladiosimulator.analyzer.slingshot.e2e.helpers.SlingshotTestRun;
import org.palladiosimulator.analyzer.slingshot.e2e.helpers.TestModelURIs;

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

	List<AssertionError> errors = new ArrayList<>();

	@Test
	public void test_subscriptor() {
		final SlingshotTestRun run = new SlingshotTestRun(new TestModelURIs("usageModelOnly"));

		// this works
		final Subscriber<SimulationStarted> handler = Subscriber.builder(SimulationStarted.class)
				.name("subscriberTestSubcriber").handler(e -> {
					System.out.println("WERE HERE " + e.getName());
					try {
						assertTrue(false, "fail inside subscriptor");
						// like this we'll get the assertion error in the wrong thread though.
					} catch (final AssertionError err) {
						errors.add(err);
						// like this i can propagate the assertion error to the test case.
					}
					this.result = true;
					return Result.of();
				}).build();

		final SimulationDriver simulationDriver = run.getDriver();
		simulationDriver.registerEventHandler(handler);

		run.initAndRun();

		if (!errors.isEmpty()) {
			throw errors.get(0);
			// but to be honest, i think it's a bit odd. there's probably something better?
		}

		assertTrue(result, "handler success");
	}
}
