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

package com.liferay.ide.kaleo.ui.navigator;

import com.liferay.ide.kaleo.core.KaleoAPIException;
import com.liferay.ide.kaleo.ui.KaleoImages;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * @author Gregory Amerson
 */
public class WorkflowDefinitionsDecorator
	extends LabelProvider
	implements ILightweightLabelDecorator /* * * implements IColorProvider, IFontProvider, * * IStyledLabelProvider */ {

	public static WorkflowDefinitionsDecorator getDefault() {
		return _instance;
	}

	public WorkflowDefinitionsDecorator() {
	}

	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof WorkflowDefinitionEntry) {
			WorkflowDefinitionEntry definition = (WorkflowDefinitionEntry)element;

			if (!definition.isLoadingNode()) {
				int version = definition.getVersion();
				int draftVersion = definition.getDraftVersion();

				decoration.addSuffix(combine(version, draftVersion));
			}
		}
		else if (element instanceof WorkflowDefinitionsFolder) {
			WorkflowDefinitionsFolder folder = (WorkflowDefinitionsFolder)element;

			IStatus status = folder.getStatus();

			if (status != null) {
				if (status.getException() instanceof KaleoAPIException) {
					decoration.addSuffix("  [Error API unavailable. Ensure kaleo-designer-portlet is deployed.]");

					IWorkbench workBench = PlatformUI.getWorkbench();

					ISharedImages shareImages = workBench.getSharedImages();

					decoration.addOverlay(shareImages.getImageDescriptor(ISharedImages.IMG_DEC_FIELD_ERROR));
				}
				else {
					decoration.addSuffix("  [" + status.getMessage() + "]");
				}
			}
			else {
				decoration.addSuffix("");
			}
		}
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof WorkflowDefinitionsFolder) {
			return KaleoImages.IMG_WORKFLOW_DEFINITIONS_FOLDER;
		}
		else if (element instanceof WorkflowDefinitionEntry) {
			WorkflowDefinitionEntry definition = (WorkflowDefinitionEntry)element;

			if (definition.isLoadingNode()) {
				return KaleoImages.IMG_LOADING;
			}
			else {
				return KaleoImages.IMG_WORKFLOW_DEFINITION;
			}
		}

		return null;
	}

	public StyledString getStyledText(Object element) {
		if (element instanceof WorkflowDefinitionsFolder) {
			return new StyledString(_WORKFLOW_DEFINITIONS_FOLDER_NAME);
		}
		else if (element instanceof WorkflowDefinitionEntry) {
			WorkflowDefinitionEntry definitionNode = (WorkflowDefinitionEntry)element;

			return new StyledString(definitionNode.getName());
		}
		else {
			return null;
		}
	}

	@Override
	public String getText(Object element) {
		if (element instanceof WorkflowDefinitionsFolder) {
			return _WORKFLOW_DEFINITIONS_FOLDER_NAME;
		}
		else if (element instanceof WorkflowDefinitionEntry) {
			WorkflowDefinitionEntry definitionNode = (WorkflowDefinitionEntry)element;

			return definitionNode.getName();
		}
		else {
			return null;
		}
	}

	protected String combine(int version, int draftVersion) {
		if (draftVersion == -1) {
			return "  [Version: " + version + "]";
		}

		return "  [Version: " + version + ", Draft Version: " + draftVersion + "]";
	}

	private static final String _WORKFLOW_DEFINITIONS_FOLDER_NAME = "Kaleo Workflows";

	private static WorkflowDefinitionsDecorator _instance;

}