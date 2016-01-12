/*******************************************************************************
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
 *
 *******************************************************************************/

package com.liferay.ide.ui;

import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * @author Greg Amerson
 * @author Lovett Li
 */
@SuppressWarnings( "deprecation" )
public class LiferayPerspectiveFactory extends AbstractPerspectiveFactory
{
    public static final String ID = "com.liferay.ide.eclipse.ui.perspective.liferay"; //$NON-NLS-1$

    public void createInitialLayout( IPageLayout layout )
    {
        createLayout( layout );
        setupActions( layout );
        addShortcuts( layout );
    }

    protected void createLayout( IPageLayout layout )
    {
        // Editors are placed for free.
        String editorArea = layout.getEditorArea();

        // Top left.
        IFolderLayout topLeft = layout.createFolder( "topLeft", IPageLayout.LEFT, 0.20f, editorArea );//$NON-NLS-1$
        topLeft.addView( ID_PACKAGE_EXPLORER_VIEW );
        // topLeft.addView(ID_J2EE_HIERARCHY_VIEW);
        topLeft.addPlaceholder( ID_J2EE_HIERARCHY_VIEW );
        topLeft.addPlaceholder( IPageLayout.ID_RES_NAV );
        topLeft.addPlaceholder( JavaUI.ID_TYPE_HIERARCHY );
        topLeft.addPlaceholder( JavaUI.ID_PACKAGES_VIEW );

        // Top right.
        IFolderLayout topRight = layout.createFolder( "topRight", IPageLayout.RIGHT, 0.68f, editorArea );//$NON-NLS-1$
        topRight.addView( IPageLayout.ID_OUTLINE );
        topRight.addView( ID_WST_SNIPPETS_VIEW );

        // IViewDescriptor tlView = PlatformUI.getWorkbench().getViewRegistry().find(ID_TASKLIST_VIEW);

        // if (tlView != null) {
        // topRight.addView(ID_TASKLIST_VIEW);
        // }

        topRight.addPlaceholder( IPageLayout.ID_BOOKMARKS );

        IFolderLayout topRightBottom = layout.createFolder( "topRightBottom", IPageLayout.BOTTOM, 0.7f, "topRight" ); //$NON-NLS-1$ //$NON-NLS-2$
        topRightBottom.addView( ANT_VIEW_ID );
        topRightBottom.addView( IPageLayout.ID_PROP_SHEET );

        IFolderLayout bottomTopLeft = layout.createFolder( "bottomTopLeft", IPageLayout.BOTTOM, 0.7f, "topLeft" ); //$NON-NLS-1$ //$NON-NLS-2$

        bottomTopLeft.addView( ID_SERVERS_VIEW );

        // Bottom
        IFolderLayout bottom = layout.createFolder( "bottom", IPageLayout.BOTTOM, 0.7f, editorArea );//$NON-NLS-1$
        bottom.addView( ID_MARKERS_VIEW );
        bottom.addView( ID_CONSOLE_VIEW );
        bottom.addView( ID_JAVADOC_VIEW );

        addViewIfExist(layout, bottom, ID_DATA_VIEW);

        bottom.addPlaceholder( IPageLayout.ID_PROBLEM_VIEW );
        bottom.addPlaceholder( IProgressConstants.PROGRESS_VIEW_ID );
        bottom.addPlaceholder( ID_SEARCH_VIEW );

    }
}
