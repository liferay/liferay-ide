/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.layouttpl.ui.editor;

import com.liferay.ide.layouttpl.ui.LayoutTplUI;
import com.liferay.ide.layouttpl.ui.model.PortletColumn;
import com.liferay.ide.layouttpl.ui.model.PortletLayout;

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
 */
public class LayoutTplEditorPaletteFactory
{

    /** Preference ID used to persist the palette location. */
    // private static final String PALETTE_DOCK_LOCATION = "LayoutTplEditorPaletteFactory.Location";
    /** Preference ID used to persist the palette size. */
    // private static final String PALETTE_SIZE = "LayoutTplEditorPaletteFactory.Size";
    /** Preference ID used to persist the flyout palette's state. */
    // private static final String PALETTE_STATE = "LayoutTplEditorPaletteFactory.State";

    /** Create the "Tools" group. */
    private static PaletteContainer createToolsGroup( PaletteRoot palette )
    {
        PaletteToolbar toolbar = new PaletteToolbar( "Tools" );

        // Add a selection tool to the group
        ToolEntry tool = new PanningSelectionToolEntry();
        toolbar.add( tool );
        palette.setDefaultEntry( tool );

        // Add a marquee tool to the group
        toolbar.add( new MarqueeToolEntry() );

        return toolbar;
    }

    /**
     * Creates the PaletteRoot and adds all palette elements. Use this factory method to create a new palette for your
     * graphical editor.
     * 
     * @return a new PaletteRoot
     */
    static PaletteRoot createPalette()
    {
        PaletteRoot palette = new PaletteRoot();
        palette.add( createToolsGroup( palette ) );

        PaletteDrawer group = new PaletteDrawer( "Layout" );

        ImageDescriptor desc =
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry(
                "/icons/palette/column_16x16.png" ) );
        ImageDescriptor large =
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry(
                "/icons/palette/column_32x32.png" ) );

        CombinedTemplateCreationEntry component =
            new CombinedTemplateCreationEntry(
                "Column", "Create a single column", PortletColumn.class, new SimpleFactory( PortletColumn.class ),
                desc, large );

        group.add( component );

        desc =
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry(
                "/icons/palette/row_16x16.png" ) );
        large =
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry(
                "/icons/palette/row_32x32.png" ) );

        CombinedTemplateCreationEntry component2 =
            new CombinedTemplateCreationEntry( "Row", "Create a single row", PortletLayout.class, new SimpleFactory(
                PortletLayout.class ), desc, large );

        group.add( component2 );

        palette.add( group );

        PaletteDrawer group2 = new PaletteDrawer( "Templates" );

        desc =
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry(
                "/icons/palette/2column_50_50_16x16.png" ) );
        large =
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry(
                "/icons/palette/2column_50_50_32x32.png" ) );

        CombinedTemplateCreationEntry component3 =
            new CombinedTemplateCreationEntry(
                "2 Columns (50/50)", "Create a 2 column row both at 50% width.",
                new PortletLayoutTemplate( 2, 50, 50 ), new PortletLayoutFactory( 2, 50, 50 ), desc, large );

        group2.add( component3 );

        desc =
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry(
                "/icons/palette/2column_30_70_16x16.png" ) );
        large =
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry(
                "/icons/palette/2column_30_70_32x32.png" ) );

        CombinedTemplateCreationEntry component4 =
            new CombinedTemplateCreationEntry(
                "2 Columns (30/70)", "Create a 2 column row with 30% and 70% for column widths.",
                new PortletLayoutTemplate( 2, 30, 70 ), new PortletLayoutFactory( 2, 30, 70 ), desc, large );

        group2.add( component4 );

        desc =
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry(
                "/icons/palette/2column_70_30_16x16.png" ) );
        large =
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry(
                "/icons/palette/2column_70_30_32x32.png" ) );

        CombinedTemplateCreationEntry component5 =
            new CombinedTemplateCreationEntry(
                "2 Columns (70/30)", "Create a 2 column row with 70% and 30% for column widths.",
                new PortletLayoutTemplate( 2, 70, 30 ), new PortletLayoutFactory( 2, 70, 30 ), desc, large );

        group2.add( component5 );

        desc =
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry(
                "/icons/palette/3column_16x16.png" ) );
        large =
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry(
                "/icons/palette/3column_32x32.png" ) );

        CombinedTemplateCreationEntry component6 =
            new CombinedTemplateCreationEntry(
                "3 Columns", "Create a 3 column row with all widths at 33%.",
                new PortletLayoutTemplate( 3, 33, 33, 33 ), new PortletLayoutFactory( 3, 33, 33, 33 ), desc, large );

        group2.add( component6 );

        palette.add( group2 );

        return palette;
    }

    /** Utility class. */
    private LayoutTplEditorPaletteFactory()
    {
        // Utility class
    }

}
