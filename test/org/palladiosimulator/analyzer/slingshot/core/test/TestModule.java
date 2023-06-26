package org.palladiosimulator.analyzer.slingshot.core.test;

import org.palladiosimulator.analyzer.slingshot.core.extension.AbstractSlingshotExtension;
import org.palladiosimulator.analyzer.slingshot.core.test.SubscribtionTest.InnerSubscriber;

public class TestModule extends AbstractSlingshotExtension {
	@Override
	protected void configure() {
		install(InnerSubscriber.class);
	}
}
