/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.ide.core;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Andy Wu
 */
public class LiferayProjectImporterReader extends ExtensionReader<ILiferayProjectImporter> {

	public LiferayProjectImporterReader() {
		super(LiferayCore.PLUGIN_ID, _EXTENSION, _PROVIDER_ELEMENT);
	}

	public ILiferayProjectImporter getImporter(String buildType) {
		ILiferayProjectImporter retval = null;

		ILiferayProjectImporter[] importers = getImporters();

		for (ILiferayProjectImporter importer : importers) {
			String type = importer.getBuildType();

			if (type.equals(buildType)) {
				retval = importer;
			}
		}

		return retval;
	}

	public ILiferayProjectImporter[] getImporters() {
		ILiferayProjectImporter[] importers = getExtensions().toArray(new ILiferayProjectImporter[0]);

		Arrays.sort(
			importers,
			new Comparator<ILiferayProjectImporter>() {

				@Override
				public int compare(ILiferayProjectImporter importer1, ILiferayProjectImporter importer2) {
					return (importer1.getPriority() > importer2.getPriority()) ? 1 : -1;
				}

			});

		return importers;
	}

	@Override
	protected ILiferayProjectImporter initElement(
		IConfigurationElement configElement, ILiferayProjectImporter importer) {

		String buildType = configElement.getAttribute(_ATTRIBUTE_BUILDTYPE);
		String priority = configElement.getAttribute(_ATTRIBUTE_PRIORITY);

		AbstractLiferayProjectImporter projectImporter = (AbstractLiferayProjectImporter)importer;

		projectImporter.setBuildType(buildType);

		projectImporter.setPriority(Integer.valueOf(priority));

		return importer;
	}

	private static final String _ATTRIBUTE_BUILDTYPE = "buildType";

	private static final String _ATTRIBUTE_PRIORITY = "priority";

	private static final String _EXTENSION = "liferayProjectImporters";

	private static final String _PROVIDER_ELEMENT = "liferayProjectImporter";

}