package org.palladiosimulator.analyzer.slingshot.test.helpers;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.monitorrepository.MonitorRepositoryPackage;
import org.palladiosimulator.pcm.allocation.AllocationPackage;
import org.palladiosimulator.pcm.repository.RepositoryPackage;
import org.palladiosimulator.pcm.resourceenvironment.ResourceenvironmentPackage;
import org.palladiosimulator.pcm.system.SystemPackage;
import org.palladiosimulator.pcm.usagemodel.UsagemodelPackage;
import org.palladiosimulator.spd.SpdPackage;

/**
 *
 * Holds a set of URIs. They all are absolute paths without leading "/" and
 * point to resources that at least look like pcm models (i.e. correct file
 * extension. )
 *
 *
 * @author Sarah Stiess
 *
 */
public class TestModelURIs {

	private static final Logger LOGGER = Logger.getLogger(TestModelURIs.class);

	final static Set<String> validFilesFromPackages = Set.of(RepositoryPackage.eNAME, SystemPackage.eNAME,
			ResourceenvironmentPackage.eNAME, AllocationPackage.eNAME, UsagemodelPackage.eNAME,
			MonitorRepositoryPackage.eNAME, SpdPackage.eNAME);

	final Set<URI> modelLocations;

	/**
	 *
	 */
	public TestModelURIs() {
		modelLocations = new HashSet<>();
	}

	/**
	 * set all (valid) model resource from directory - location.
	 *
	 * @param directory
	 */
	public TestModelURIs(final String directory) {
		this();
		final URL url = this.getClass().getClassLoader().getResource(directory);
		if (url == null) {
			throw new IllegalArgumentException(String.format("No resource at %s.", directory));
		}

		try {
			final File file = new File(FileLocator.resolve(url).toURI());
			if (file.isDirectory()) {
				final String[] files = file.list();

				LOGGER.info(String.format("Found %d files in %s", files.length, directory));

				Arrays.stream(files).filter(this::hasValidExtension)
						.map(f -> directory + f)
						.forEach(this::addModelLocation);
			} else {
				throw new IllegalArgumentException(String.format("Resource at %s is not a directory.", directory));
			}

		} catch (final URISyntaxException | IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * add single location
	 *
	 * @param location
	 */
	public void addModelLocation(final String location) {
		if (!hasValidExtension(location)) {
			LOGGER.info(String.format("Resource %s has invalid fileextension and will be skipped.", location));
			return;
		}

		final URL url = this.getClass().getClassLoader().getResource(location);
		if (url == null) {
			throw new IllegalArgumentException(String.format("No resource at %s", location));
		}

		final URI uri = URI.createURI(url.toString());
		modelLocations.add(uri);
		LOGGER.info(String.format("URI %s to Resource added.", uri.toString()));

	}

	/**
	 *
	 * @return URIs to PCM models.
	 */
	public Set<URI> getModelLocations() {
		return modelLocations;
	}

	/**
	 * TODO : does Palladio maybe already provide a util for this and i just dont
	 * know?
	 *
	 * @param file
	 * @return
	 */
	private boolean hasValidExtension(final String file) {
		final String fileextension = file.split("\\.")[file.split("\\.").length - 1];
		return validFilesFromPackages.contains(fileextension);
	}

}
