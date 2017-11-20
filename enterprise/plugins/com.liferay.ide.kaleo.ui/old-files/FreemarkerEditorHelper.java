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

package com.liferay.ide.kaleo.ui.helpers;

import com.liferay.ide.kaleo.ui.AbstractKaleoEditorHelper;
import com.liferay.ide.kaleo.ui.KaleoUI;
import com.liferay.ide.kaleo.ui.WorkflowContextConstants;
import com.liferay.ide.kaleo.ui.editor.KaleoFreemarkerEditor;
import com.liferay.ide.kaleo.ui.editor.ScriptPropertyEditorInput;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;

import org.jboss.ide.eclipse.freemarker.configuration.ConfigurationManager;
import org.jboss.ide.eclipse.freemarker.configuration.ContextValue;

/**
 * @author Gregory Amerson
 */
public class FreemarkerEditorHelper extends AbstractKaleoEditorHelper {

	public IEditorPart createEditorPart(ScriptPropertyEditorInput editorInput, IEditorSite editorSite) {
		IEditorPart editorPart = null;

		try {
			editorPart = new KaleoFreemarkerEditor();

			editorPart.init(editorSite, editorInput);
		}
		catch (Exception e) {
			KaleoUI.logError("Could not create freemarker editor.", e);

			editorPart = super.createEditorPart(editorInput, editorSite);
		}

		return editorPart;
	}

	@Override
	public void openEditor(ISapphirePart sapphirePart, Element modelElement, ValueProperty valueProperty) {
		IProject project = sapphirePart.adapt(IProject.class);

		ConfigurationManager configManager = ConfigurationManager.getInstance(project);

		ContextValue[] contextValues = {
			new ContextValue(WorkflowContextConstants.SERVICE_CONTEXT, Map.class, Map.class),
			new ContextValue(WorkflowContextConstants.WORKFLOW_CONTEXT, Map.class, Map.class),
			new ContextValue(WorkflowContextConstants.ENTRY_CLASS_NAME, String.class, String.class),
			new ContextValue(WorkflowContextConstants.GROUP_ID, Long.class, Long.class),
			new ContextValue(WorkflowContextConstants.ENTRY_TYPE, String.class, String.class),
			new ContextValue(WorkflowContextConstants.USER_ID, Long.class, Long.class),
			new ContextValue(WorkflowContextConstants.TASK_COMMENTS, String.class, String.class),
			new ContextValue(WorkflowContextConstants.COMPANY_ID, Long.class, Long.class),
			new ContextValue(WorkflowContextConstants.ENTRY_CLASS_PK, Long.class, Long.class),
			new ContextValue(WorkflowContextConstants.TRANSITION_NAME, String.class, String.class),
			new ContextValue(WorkflowContextConstants.WORKFLOW_TASK_ASSIGNEES, List.class, List.class)
		};

		for (ContextValue cv : contextValues) {
			configManager.addContextValue(cv, project);
		}

		super.openEditor(sapphirePart, modelElement, valueProperty);
	}

}