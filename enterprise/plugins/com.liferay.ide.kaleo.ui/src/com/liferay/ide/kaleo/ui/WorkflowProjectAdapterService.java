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

package com.liferay.ide.kaleo.ui;

import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.core.WorkflowSupportManager;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.ui.editor.WorkflowDefinitionEditorInput;
import com.liferay.ide.kaleo.ui.navigator.WorkflowDefinitionEntry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.sapphire.UniversalConversionService;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.ui.IEditorInput;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Gregory Amerson
 */
public class WorkflowProjectAdapterService extends UniversalConversionService {

	@Override
	public <A> A convert(Object object, Class<A> adapterType) {
		A retval = null;

		if (IProject.class.equals(adapterType)) {
			ISapphirePart sapphirePart = context().find(ISapphirePart.class);

			WorkflowDefinition workflowDefinition =
				sapphirePart.getLocalModelElement().nearest(WorkflowDefinition.class);

			IFile file = workflowDefinition.adapt(IFile.class);

			if (file != null) {
				retval = adapterType.cast(file.getProject());
			}
			else {

				// create support project

				WorkflowSupportManager workflowSupportManager = KaleoCore.getDefault().getWorkflowSupportManager();

				IEditorInput editorInput = workflowDefinition.adapt(IEditorInput.class);

				if (editorInput instanceof WorkflowDefinitionEditorInput) {
					WorkflowDefinitionEditorInput workflowInput = (WorkflowDefinitionEditorInput)editorInput;

					WorkflowDefinitionEntry workflowEntry = workflowInput.getWorkflowDefinitionEntry();

					IServer server = workflowEntry.getParent().getParent();

					workflowSupportManager.setCurrentServer(server);
				}

				IJavaProject supportProject = workflowSupportManager.getSupportProject();

				retval = adapterType.cast(supportProject.getProject());
			}
		}

		return retval;
	}

}