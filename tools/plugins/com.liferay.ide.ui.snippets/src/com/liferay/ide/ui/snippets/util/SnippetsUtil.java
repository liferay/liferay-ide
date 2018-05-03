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

package com.liferay.ide.ui.snippets.util;

import com.liferay.ide.ui.LiferayPerspectiveFactory;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.List;

import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.snippets.internal.SnippetDefinitions;
import org.eclipse.wst.common.snippets.internal.model.SnippetManager;
import org.eclipse.wst.common.snippets.internal.palette.ModelFactoryForUser;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class SnippetsUtil {

	public static IViewPart findSnippetsView() {
		for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
			for (IWorkbenchPage page : window.getPages()) {
				IViewPart snippetsView = page.findView(LiferayPerspectiveFactory.ID_WST_SNIPPETS_VIEW);

				if (snippetsView != null) {
					return snippetsView;
				}
			}
		}

		return null;
	}

	@SuppressWarnings("rawtypes")
	public static void importSnippetsFromFile(File snippetFile) throws FileNotFoundException {
		if ((snippetFile == null) || !snippetFile.exists() || !snippetFile.isFile()) {
			return;
		}

		// InputStream stream = Files.newInputStream(snippetFile.toPath());

		SnippetDefinitions definitions = ModelFactoryForUser.getInstance().load(snippetFile.getAbsolutePath());

		List importCategories = definitions.getCategories();

		SnippetManager manager = SnippetManager.getInstance();

		List currentCategories = manager.getDefinitions().getCategories();

		Display.getDefault().asyncExec(
			new Runnable() {

				public void run() {
					for (int i = 0; i < importCategories.size(); i++) {
						boolean found = false;

						for (int j = 0; j < currentCategories.size(); j++) {
							if (((PaletteEntry)currentCategories.get(j)).getId().compareToIgnoreCase(
									((PaletteEntry)importCategories.get(i)).getId()) == 0) {

								found = true;

								break;
							}
						}

						if (!found) {
							SnippetManager manager = SnippetManager.getInstance();

							manager.getPaletteRoot().add((PaletteEntry)importCategories.get(i));
						}
					}
				}

			});

		// IViewPart view = findSnippetsView();

		// if (view != null) {
		// SnippetsView snippetsView = (SnippetsView) view;
		// snippetsView.getViewer().getContents().refresh();
		// }

	}

}