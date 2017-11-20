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

import com.liferay.ide.kaleo.ui.navigator.WorkflowDefinitionsFolder;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.navigator.CommonViewer;

/**
 * @author Gregory Amerson
 */
public class RefreshWorkflowDefinitionsAction extends AbstractWorkflowDefinitionAction {

	public RefreshWorkflowDefinitionsAction(ISelectionProvider sp) {
		super(sp, "Refresh");

		// setActionDefinitionId( org.eclipse.ui.actions.RefreshAction.ID );

	}

	@Override
	public void perform(Object node) {
		if (getSelectionProvider() instanceof CommonViewer) {
			if (node instanceof WorkflowDefinitionsFolder) {
				((WorkflowDefinitionsFolder)node).clearCache();
			}

			CommonViewer viewer = (CommonViewer)getSelectionProvider();

			Display.getDefault().asyncExec(
				new Runnable() {

					public void run() {
						viewer.refresh(true);
					}

				});
		}
	}

}