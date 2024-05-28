package org.palladiosimulator.analyzer.slingshot.e2e.helpers;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
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
		validFilesFromPackages.forEach(extension -> addModelsToLocation(directory, "*." + extension));
	}
	
	/**
	 * add single location
	 *
	 * @param dirName
	 * @param extension
	 */
	private void addModelsToLocation(final String dirName, final String extension) {

        final Enumeration<URL> files = Platform.getBundle("org.palladiosimulator.analyzer.slingshot.e2e.helpers").findEntries(dirName, extension, true);
        if (files == null) {
        	return;
        }

        URL fileURL = files.nextElement();

        if (fileURL != null) {
            try {
                fileURL = FileLocator.resolve(fileURL);
                final URI uri = URI.createURI(fileURL.toString());
                modelLocations.add(uri);
                LOGGER.info(String.format("URI %s to Resource added.", uri.toString()));
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

	/**
	 *
	 * @return URIs to PCM models.
	 */
	public Set<URI> getModelLocations() {
		return modelLocations;
	}

}