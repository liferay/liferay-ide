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

package com.liferay.ide.upgrade.plan.ui.internal;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Christopher Bryan Boyd
 */
public class ExpandHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart part = HandlerUtil.getActivePart(event);

		if (part instanceof UpgradePlanView) {
			UpgradePlanView upgradePlanView = (UpgradePlanView)part;

			UpgradePlanViewer upradPlanViewer = upgradePlanView.getUpgradePlanViewer();

			TreeViewer treeViewer = upradPlanViewer.getTreeViewer();

			Tree tree = treeViewer.getTree();

			try {
				tree.setRedraw(false);
				treeViewer.expandAll();
			}
			finally {
				tree.setRedraw(true);
			}
		}

		return null;
	}

}