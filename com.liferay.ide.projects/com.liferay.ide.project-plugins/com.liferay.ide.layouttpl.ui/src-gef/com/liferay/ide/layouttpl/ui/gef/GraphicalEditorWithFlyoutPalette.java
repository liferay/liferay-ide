/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.layouttpl.ui.gef;

import com.liferay.ide.layouttpl.ui.LayoutTplUI;

import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences;
import org.eclipse.gef.ui.views.palette.PalettePage;
import org.eclipse.gef.ui.views.palette.PaletteViewerPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public abstract class GraphicalEditorWithFlyoutPalette extends GraphicalEditor {

	private PaletteViewerProvider provider;
	private FlyoutPaletteComposite splitter;
	private CustomPalettePage page;

	/**
	 * @see GraphicalEditor#initializeGraphicalViewer()
	 */
	protected void initializeGraphicalViewer() {
		splitter.hookDropTargetListener(getGraphicalViewer());
	}

	/**
	 * Creates a PaletteViewerProvider that will be used to create palettes for
	 * the view and the flyout.
	 * 
	 * @return the palette provider
	 */
	protected PaletteViewerProvider createPaletteViewerProvider() {
		return new PaletteViewerProvider(getEditDomain());
	}

	/**
	 * @return a newly-created {@link CustomPalettePage}
	 */
	protected CustomPalettePage createPalettePage() {
		return new CustomPalettePage(getPaletteViewerProvider());
	}

	/**
	 * @see GraphicalEditor#createPartControl(Composite)
	 */
	public void createPartControl(Composite parent) {
		splitter =
			new FlyoutPaletteComposite(
				parent, SWT.NONE, getSite().getPage(), getPaletteViewerProvider(), getPalettePreferences());
		super.createPartControl(splitter);
		splitter.setGraphicalControl(getGraphicalControl());
		if (page != null) {
			splitter.setExternalViewer(page.getPaletteViewer());
			page = null;
		}
	}

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class type) {
		if (type == PalettePage.class) {
			if (splitter == null) {
				page = createPalettePage();
				return page;
			}
			return createPalettePage();
		}
		return super.getAdapter(type);
	}

	/**
	 * @return the graphical viewer's control
	 */
	protected Control getGraphicalControl() {
		return getGraphicalViewer().getControl();
	}

	/**
	 * By default, this method returns a FlyoutPreferences object that stores
	 * the flyout settings in the GEF plugin. Sub-classes may override.
	 * 
	 * @return the FlyoutPreferences object used to save the flyout palette's
	 *         preferences
	 */
	protected FlyoutPreferences getPalettePreferences() {
		return FlyoutPaletteComposite.createFlyoutPreferences(LayoutTplUI.getDefault().getPluginPreferences());
	}

	/**
	 * Returns the PaletteRoot for the palette viewer.
	 * 
	 * @return the palette root
	 */
	protected abstract PaletteRoot getPaletteRoot();

	/**
	 * Returns the palette viewer provider that is used to create palettes for
	 * the view and the flyout. Creates one if it doesn't already exist.
	 * 
	 * @return the PaletteViewerProvider that can be used to create
	 *         PaletteViewers for this editor
	 * @see #createPaletteViewerProvider()
	 */
	protected final PaletteViewerProvider getPaletteViewerProvider() {
		if (provider == null)
			provider = createPaletteViewerProvider();
		return provider;
	}

	/**
	 * Sets the edit domain for this editor.
	 * 
	 * @param ed
	 *            The new EditDomain
	 */
	protected void setEditDomain(DefaultEditDomain ed) {
		super.setEditDomain(ed);
		getEditDomain().setPaletteRoot(getPaletteRoot());
	}

	/**
	 * A custom PalettePage that helps GraphicalEditorWithFlyoutPalette keep the
	 * two PaletteViewers (one displayed in the editor and the other displayed
	 * in the PaletteView) in sync when switching from one to the other (i.e.,
	 * it helps maintain state across the two viewers).
	 * 
	 * @author Pratik Shah
	 * @since 3.0
	 */
	protected class CustomPalettePage extends PaletteViewerPage {

		/**
		 * Constructor
		 * 
		 * @param provider
		 *            the provider used to create a PaletteViewer
		 */
		public CustomPalettePage(PaletteViewerProvider provider) {
			super(provider);
		}

		/**
		 * @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		public void createControl(Composite parent) {
			super.createControl(parent);
			if (splitter != null)
				splitter.setExternalViewer(viewer);
		}

		/**
		 * @see org.eclipse.ui.part.IPage#dispose()
		 */
		public void dispose() {
			if (splitter != null)
				splitter.setExternalViewer(null);
			super.dispose();
		}

		/**
		 * @return the PaletteViewer created and displayed by this page
		 */
		public PaletteViewer getPaletteViewer() {
			return viewer;
		}
	}

}
