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

import com.liferay.ide.kaleo.core.IKaleoConnection;
import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.ui.util.UploadWorkflowFileJob;
import com.liferay.ide.ui.util.UIUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.navigator.CommonDropAdapter;
import org.eclipse.ui.navigator.CommonDropAdapterAssistant;
import org.eclipse.ui.navigator.CommonViewer;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class WorkflowDropAdapterAssistant extends CommonDropAdapterAssistant {

	public WorkflowDropAdapterAssistant() {
	}

	@Override
	public IStatus handleDrop(CommonDropAdapter aDropAdapter, DropTargetEvent aDropTargetEvent, Object aTarget) {
		if (aTarget instanceof WorkflowDefinitionsFolder) {
			WorkflowDefinitionsFolder folder = (WorkflowDefinitionsFolder)aTarget;

			IKaleoConnection kaleoConnection = KaleoCore.getKaleoConnection(folder.getParent());

			TransferData transferData = aDropTargetEvent.currentDataType;

			if (LocalSelectionTransfer.getTransfer().isSupportedType(transferData)) {
				Object dropData = LocalSelectionTransfer.getTransfer().getSelection();

				if (dropData instanceof IStructuredSelection) {
					IStructuredSelection selection = (IStructuredSelection)dropData;

					Object element = selection.getFirstElement();

					if (element instanceof IFile) {
						IFile file = (IFile)element;

						Runnable runnable = new Runnable() {

							public void run() {
								folder.clearCache();

								Runnable runnable = new Runnable() {

									public void run() {
										IViewPart serversView = UIUtil.showView(
											"org.eclipse.wst.server.ui.ServersView");

										CommonViewer viewer = (CommonViewer)serversView.getAdapter(CommonViewer.class);

										viewer.refresh(true);
									}

								};

								Display.getDefault().asyncExec(runnable);
							}

						};

						new UploadWorkflowFileJob(kaleoConnection, file, runnable).schedule();
					}
				}
			}
		}

		return Status.OK_STATUS;
	}

	@Override
	public IStatus validateDrop(Object target, int operation, TransferData data) {
		try {
			if (target instanceof WorkflowDefinitionsFolder) {
				if (LocalSelectionTransfer.getTransfer().isSupportedType(data)) {
					Object dropData = LocalSelectionTransfer.getTransfer().getSelection();

					if (dropData instanceof IStructuredSelection) {
						IStructuredSelection selection = (IStructuredSelection)dropData;

						Object element = selection.getFirstElement();

						if (element instanceof IFile) {
							IFile file = (IFile)element;

							IContentType contentType = file.getContentDescription().getContentType();

							if ("com.liferay.ide.kaleo.core.workflowdefinitioncontent".equals(contentType.getId())) {
								return Status.OK_STATUS;
							}
						}
					}
				}
			}
		}
		catch (Exception e) {
		}

		return null;
	}

}