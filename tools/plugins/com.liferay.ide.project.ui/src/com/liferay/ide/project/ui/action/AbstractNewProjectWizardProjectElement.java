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

package com.liferay.ide.project.ui.action;

import com.liferay.ide.core.AbstractConfigurationElement;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.spi.RegistryContributor;
import org.eclipse.osgi.util.NLS;

/**
 * @author Gregory Amerson
 */
public abstract class AbstractNewProjectWizardProjectElement extends AbstractConfigurationElement {

	@Override
	public Object createExecutableExtension(String propertyName) throws CoreException {
		if (NewWizardAction.ATT_CLASS.equals(propertyName)) {
			return createNewWizard();
		}

		return null;
	}

	@Override
	public String getAttribute(String attr) throws InvalidRegistryObjectException {
		return getProjectElementAttribute(attr);
	}

	@Override
	public IConfigurationElement[] getChildren(String name) throws InvalidRegistryObjectException {
		if (NewWizardAction.TAG_DESCRIPTION.equals(name)) {
			return new IConfigurationElement[] {new ProjectDescriptionElement()};
		}
		else if (NewWizardAction.TAG_CLASS.equals(name)) {
			return new IConfigurationElement[] {new ProjectClassElement()};
		}

		return null;
	}

	@Override
	public IContributor getContributor() throws InvalidRegistryObjectException {
		return new RegistryContributor(null, getContributorID(), null, null);
	}

	protected abstract Object createNewWizard();

	protected abstract String getContributorID();

	protected abstract String getProjectElementAttribute(String attr);

	protected abstract String getProjectParameterElementAttribute(String name);

	protected class ProjectClassElement extends AbstractConfigurationElement {

		@Override
		public IConfigurationElement[] getChildren(String name) throws InvalidRegistryObjectException {
			if (NewWizardAction.TAG_PARAMETER.equals(name)) {
				return new IConfigurationElement[] {new ProjectParameterElement()};
			}

			return null;
		}

	}

	protected class ProjectDescriptionElement extends AbstractConfigurationElement {

		@Override
		public String getValue() throws InvalidRegistryObjectException {
			return Msgs.importDescription;
		}

	}

	protected class ProjectParameterElement extends AbstractConfigurationElement {

		@Override
		public String getAttribute(String name) throws InvalidRegistryObjectException {
			return getProjectParameterElementAttribute(name);
		}

	}

	private static class Msgs extends NLS {

		public static String importDescription;

		static {
			initializeMessages(AbstractNewProjectWizardProjectElement.class.getName(), Msgs.class);
		}

	}

}