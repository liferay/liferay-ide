/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
import org.eclipse.osgi.util.NLS;

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
        PaletteToolbar toolbar = new PaletteToolbar( Msgs.tools );

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

        PaletteDrawer group = new PaletteDrawer( Msgs.layout );

        ImageDescriptor desc =
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry(
                "/icons/palette/column_16x16.png" ) ); //$NON-NLS-1$
        ImageDescriptor large =
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry(
                "/icons/palette/column_32x32.png" ) ); //$NON-NLS-1$

        CombinedTemplateCreationEntry component =
            new CombinedTemplateCreationEntry(
                Msgs.column, Msgs.createSingleColumn, PortletColumn.class, new SimpleFactory( PortletColumn.class ),
                desc, large );

        group.add( component );

        desc =
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry(
                "/icons/palette/row_16x16.png" ) ); //$NON-NLS-1$
        large =
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry(
                "/icons/palette/row_32x32.png" ) ); //$NON-NLS-1$

        CombinedTemplateCreationEntry component2 =
            new CombinedTemplateCreationEntry( Msgs.row, Msgs.createSingleRow, PortletLayout.class, new SimpleFactory(
                PortletLayout.class ), desc, large );

        group.add( component2 );

        palette.add( group );

        PaletteDrawer group2 = new PaletteDrawer( Msgs.templates );

        desc =
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry(
                "/icons/palette/2column_50_50_16x16.png" ) ); //$NON-NLS-1$
        large =
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry(
                "/icons/palette/2column_50_50_32x32.png" ) ); //$NON-NLS-1$

        CombinedTemplateCreationEntry component3 =
            new CombinedTemplateCreationEntry(
                Msgs.Columns5050, Msgs.create2ColumnRow5050,
                new PortletLayoutTemplate( 2, 50, 50 ), new PortletLayoutFactory( 2, 50, 50 ), desc, large );

        group2.add( component3 );

        desc =
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry(
                "/icons/palette/2column_30_70_16x16.png" ) ); //$NON-NLS-1$
        large =
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry(
                "/icons/palette/2column_30_70_32x32.png" ) ); //$NON-NLS-1$

        CombinedTemplateCreationEntry component4 =
            new CombinedTemplateCreationEntry(
                Msgs.Columns3070, Msgs.create2ColumnRow3070,
                new PortletLayoutTemplate( 2, 30, 70 ), new PortletLayoutFactory( 2, 30, 70 ), desc, large );

        group2.add( component4 );

        desc =
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry(
                "/icons/palette/2column_70_30_16x16.png" ) ); //$NON-NLS-1$
        large =
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry(
                "/icons/palette/2column_70_30_32x32.png" ) ); //$NON-NLS-1$

        CombinedTemplateCreationEntry component5 =
            new CombinedTemplateCreationEntry(
                Msgs.Columns7030, Msgs.create2ColumnRow7030,
                new PortletLayoutTemplate( 2, 70, 30 ), new PortletLayoutFactory( 2, 70, 30 ), desc, large );

        group2.add( component5 );

        desc =
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry(
                "/icons/palette/3column_16x16.png" ) ); //$NON-NLS-1$
        large =
            ImageDescriptor.createFromURL( LayoutTplUI.getDefault().getBundle().getEntry(
                "/icons/palette/3column_32x32.png" ) ); //$NON-NLS-1$

        CombinedTemplateCreationEntry component6 =
            new CombinedTemplateCreationEntry(
                Msgs.threeColumns, Msgs.create3ColumnRow,
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

    private static class Msgs extends NLS
    {
        public static String column;
        public static String Columns3070;
        public static String Columns5050;
        public static String Columns7030;
        public static String create2ColumnRow3070;
        public static String create2ColumnRow5050;
        public static String create2ColumnRow7030;
        public static String create3ColumnRow;
        public static String createSingleColumn;
        public static String createSingleRow;
        public static String layout;
        public static String row;
        public static String templates;
        public static String threeColumns;
        public static String tools;

        static
        {
            initializeMessages( LayoutTplEditorPaletteFactory.class.getName(), Msgs.class );
        }
    }
}
