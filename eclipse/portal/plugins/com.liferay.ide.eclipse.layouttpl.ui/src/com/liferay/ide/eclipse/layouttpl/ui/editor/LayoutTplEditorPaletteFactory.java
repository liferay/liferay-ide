/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.layouttpl.ui.editor;

import com.liferay.ide.eclipse.layouttpl.ui.LayoutTplUI;
import com.liferay.ide.eclipse.layouttpl.ui.model.PortletColumn;
import com.liferay.ide.eclipse.layouttpl.ui.model.PortletLayout;

import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteToolbar;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Utility class that can create a GEF Palette.
 * 
 */
public class LayoutTplEditorPaletteFactory {

	/** Preference ID used to persist the palette location. */
	private static final String PALETTE_DOCK_LOCATION = "LayoutTplEditorPaletteFactory.Location";
	/** Preference ID used to persist the palette size. */
	private static final String PALETTE_SIZE = "LayoutTplEditorPaletteFactory.Size";
	/** Preference ID used to persist the flyout palette's state. */
	private static final String PALETTE_STATE = "LayoutTplEditorPaletteFactory.State";

	/**
	 * Creates the PaletteRoot and adds all palette elements. Use this factory
	 * method to create a new palette for your graphical editor.
	 * 
	 * @return a new PaletteRoot
	 */
	static PaletteRoot createPalette() {
		PaletteRoot palette = new PaletteRoot();
		palette.add(createToolsGroup(palette));

		PaletteDrawer group = new PaletteDrawer("Layout");

		ImageDescriptor desc =
			ImageDescriptor.createFromURL(LayoutTplUI.getDefault().getBundle().getEntry("/icons/e16/layout.png"));

		CombinedTemplateCreationEntry component =
			new CombinedTemplateCreationEntry(
				"Portlet Column", "Create a portlet column", PortletColumn.class,
				new SimpleFactory(PortletColumn.class), desc, desc);

		group.add(component);

		CombinedTemplateCreationEntry component2 =
			new CombinedTemplateCreationEntry("Portlet Row", "Create a row", PortletLayout.class, new SimpleFactory(
				PortletLayout.class), desc, desc);

		group.add(component2);

		palette.add(group);

		PaletteDrawer group2 = new PaletteDrawer("Templates");

		CombinedTemplateCreationEntry component3 =
			new CombinedTemplateCreationEntry(
				"2 Column Row", "Create a 2 column row", PortletLayout.class, new PortletLayoutFactory(2),
				desc, desc);

		group2.add(component3);

		CombinedTemplateCreationEntry component4 =
			new CombinedTemplateCreationEntry(
				"3 Column Row", "Create a 3 column row", PortletLayout.class, new PortletLayoutFactory(3),
				desc, desc);

		group2.add(component4);

		palette.add(group2);

		return palette;
	}

	/** Create the "Tools" group. */
	private static PaletteContainer createToolsGroup(PaletteRoot palette) {
		PaletteToolbar toolbar = new PaletteToolbar("Tools");

		// Add a selection tool to the group
		ToolEntry tool = new PanningSelectionToolEntry();
		toolbar.add(tool);
		palette.setDefaultEntry(tool);

		// Add a marquee tool to the group
		toolbar.add(new MarqueeToolEntry());

		return toolbar;
	}

	/** Utility class. */
	private LayoutTplEditorPaletteFactory() {
		// Utility class
	}

}
