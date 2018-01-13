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

package com.liferay.ide.kaleo.ui.editor;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author Gregory Amerson
 */
public class KaleoEditorPaletteFactory {

	public static PaletteRoot createPalette(AbstractUIPlugin bundle, String folderName, ImageDescriptor entryImage) {
		PaletteRoot root = new PaletteRoot();

		try {
			File paletteFolder = _getPaletteFolder(bundle, folderName);

			if (paletteFolder != null) {
				for (File file : paletteFolder.listFiles()) {
					_createPaletteEntries(root, file, entryImage);
				}
			}
		}
		catch (Exception e) {
		}

		return root;
	}

	private static void _createPaletteEntries(PaletteContainer container, File paletteFile, ImageDescriptor image) {
		if (paletteFile.isDirectory()) {
			PaletteContainer newDrawer = new PaletteDrawer(paletteFile.getName());

			for (File file : paletteFile.listFiles()) {
				_createPaletteEntries(newDrawer, file, image);
			}

			container.add(newDrawer);
		}
		else {
			CreationFactory factory = new ScriptCreationFactory(paletteFile);

			String label = new Path(paletteFile.getName()).removeFileExtension().toPortableString();

			CombinedTemplateCreationEntry entry = new CombinedTemplateCreationEntry(
				label, label, factory, image, image);

			// entry.setToolClass( CreationTool.class );

			container.add(entry);
		}
	}

	private static File _getPaletteFolder(AbstractUIPlugin bundle, String folderName) {
		try {
			return new File(FileLocator.toFileURL(bundle.getBundle().getEntry(folderName)).getFile());
		}
		catch (IOException ioe) {
		}

		return null;
	}

}