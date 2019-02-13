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

package com.liferay.ide.project.ui.modules.ext;

import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.modules.ext.NewModuleExtOp;
import com.liferay.ide.project.core.modules.ext.OverrideSourceEntry;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.ui.dialog.JarEntrySelectionDialog;
import com.liferay.ide.ui.util.UIUtil;

import java.io.File;
import java.io.IOException;

import java.net.URI;

import java.util.zip.ZipFile;

import org.eclipse.jface.window.Window;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphirePart;

/**
 * @author Charles Wu
 */
public class AddFilesFromSourceJarAction extends SapphireActionHandler {

	@Override
	protected Object run(Presentation context) {
		try {
			SapphirePart sapphirePart = context.part();

			if (sapphirePart.validation() != Status.createOkStatus()) {
				return null;
			}

			Element modelElement = sapphirePart.getModelElement();

			NewModuleExtOp moduleExtOp = modelElement.nearest(NewModuleExtOp.class);

			URI sourceFileUri = SapphireUtil.getContent(moduleExtOp.getSourceFileUri());

			JarEntrySelectionDialog dialog = new JarEntrySelectionDialog(UIUtil.getActiveShell());

			if (sourceFileUri != null) {
				dialog.setInput(new ZipFile(new File(sourceFileUri)));
			}
			else {
				dialog.setMessage("Unable to get source files in current context.");
			}

			dialog.setTitle("Select Override Files");

			if (dialog.open() == Window.OK) {
				for (Object result : dialog.getResult()) {
					ElementList<OverrideSourceEntry> overrideFiles = moduleExtOp.getOverrideFiles();

					OverrideSourceEntry fileEntry = overrideFiles.insert();

					fileEntry.setValue(result.toString());
				}
			}
		}
		catch (IOException ioe) {
			ProjectUI.logError(ioe);
		}

		return null;
	}

}