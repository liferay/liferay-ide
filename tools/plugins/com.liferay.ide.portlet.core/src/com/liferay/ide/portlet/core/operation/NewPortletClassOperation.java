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

package com.liferay.ide.portlet.core.operation;

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.portlet.core.PortletCore;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jst.j2ee.internal.common.operations.INewJavaClassDataModelProperties;
import org.eclipse.jst.j2ee.internal.web.operations.CreateWebClassTemplateModel;
import org.eclipse.jst.j2ee.internal.web.operations.NewWebClassOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * @author Greg Amerson
 * @author Cindy Li
 */
@SuppressWarnings("restriction")
public class NewPortletClassOperation extends NewWebClassOperation {

	public NewPortletClassOperation(IDataModel dataModel) {
		super(dataModel);
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		String defaultSuperclasses =
			INewPortletClassDataModelProperties.QUALIFIED_MVC_PORTLET + StringPool.COMMA +
				INewPortletClassDataModelProperties.QUALIFIED_LIFERAY_PORTLET + StringPool.COMMA +
					INewPortletClassDataModelProperties.QUALIFIED_GENERIC_PORTLET;

		try {
			Preferences preferences = PortletCore.getPreferences();

			String superclasses = preferences.get(PortletCore.PREF_KEY_PORTLET_SUPERCLASSES_USED, null);

			String superclass = getDataModel().getStringProperty(INewJavaClassDataModelProperties.SUPERCLASS);

			if (!defaultSuperclasses.contains(superclass)) {
				String newSuperclasses = null;

				if (superclasses == null) {
					newSuperclasses = superclass;
				}
				else if (!superclasses.contains(superclass)) {
					newSuperclasses = superclasses + StringPool.COMMA + superclass;
				}

				if (newSuperclasses != null) {
					preferences.put(PortletCore.PREF_KEY_PORTLET_SUPERCLASSES_USED, newSuperclasses);
					preferences.flush();
				}
			}
		}
		catch (BackingStoreException bse) {
			PortletCore.logError(bse);
		}

		return super.execute(monitor, info);
	}

	@Override
	protected CreateWebClassTemplateModel createTemplateModel() {
		return new CreatePortletTemplateModel(getDataModel());
	}

	@Override
	protected String getTemplateFile() {
		return TEMPLATE_FILE;
	}

	@Override
	protected Object getTemplateImplementation() {
		return PortletTemplate.create(null);
	}

	protected static final String TEMPLATE_DIR = "/templates/";

	protected static final String TEMPLATE_FILE = TEMPLATE_DIR + "portlet.javajet";

	// protected String generateTemplateSource(WTPPlugin plugin,
	// CreateJavaEEArtifactTemplateModel templateModel,
	// String templateFile, Object templateImpl, IProgressMonitor monitor)
	// throws JETException {
	// always use the template file version
	// return generateTemplateSource(plugin, templateModel, templateFile,
	// monitor);
	// }

}