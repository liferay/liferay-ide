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

package com.liferay.ide.kaleo.ui.action;

import com.liferay.ide.kaleo.core.IKaleoConnection;
import com.liferay.ide.kaleo.core.KaleoAPIException;
import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.ui.navigator.WorkflowDefinitionEntry;
import com.liferay.ide.kaleo.ui.navigator.WorkflowDefinitionsFolder;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.navigator.CommonViewer;

/**
 * @author Gregory Amerson
 */
public class PublishWorkflowDefinitionAction extends AbstractWorkflowDefinitionAction {

	public PublishWorkflowDefinitionAction(ISelectionProvider sp) {
		super(sp, "Publish Kaleo workflow");
	}

	@Override
	public void perform(Object node) {
		if (getSelectionProvider() instanceof CommonViewer && node instanceof WorkflowDefinitionEntry) {
			WorkflowDefinitionEntry definitionNode = (WorkflowDefinitionEntry)node;

			Job publishJob = new Job("Publishing workflow draft definition") {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					IKaleoConnection kaleoConnection = KaleoCore.getKaleoConnection(
						definitionNode.getParent().getParent());

					try {
						kaleoConnection.publishKaleoDraftDefinition(
							definitionNode.getName(), definitionNode.getTitleMap(), definitionNode.getContent(),
							definitionNode.getCompanyId() + "", definitionNode.getUserId() + "",
							definitionNode.getGroupId() + "");
					}
					catch (KaleoAPIException kapie) {
						kapie.printStackTrace();
					}

					WorkflowDefinitionsFolder definitionsFolder = (WorkflowDefinitionsFolder)definitionNode.getParent();

					CommonViewer viewer = (CommonViewer)getSelectionProvider();

					Display.getDefault().asyncExec(
						new Runnable() {

							public void run() {
								definitionsFolder.clearCache();
								viewer.refresh(true);
							}

						});

					return Status.OK_STATUS;
				}

			};

			publishJob.schedule();
		}
	}

}