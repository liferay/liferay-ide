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

/**
 * @author Lovett Li
 */
public class LiferayUpgradePerspectiveFactory extends AbstractPerspectiveFactory
{
    public static final String ID = "com.liferay.ide.eclipse.ui.perspective.liferaycodeupgrade";

    @Override
    public void createInitialLayout( IPageLayout layout )
    {
        createLayout(layout);
        addShortcuts(layout);
    }

    private void createLayout( IPageLayout layout )
    {
        String editorArea = layout.getEditorArea();

        IFolderLayout topLeft = layout.createFolder( "topLeft", IPageLayout.LEFT, 0.20f, editorArea );//$NON-NLS-1$
        topLeft.addView( ID_PROJECT_EXPLORER_VIEW );
        topLeft.addPlaceholder( ID_PACKAGE_EXPLORER_VIEW );
        topLeft.addPlaceholder( ID_J2EE_HIERARCHY_VIEW );
        topLeft.addPlaceholder( JavaUI.ID_TYPE_HIERARCHY );
        topLeft.addPlaceholder( JavaUI.ID_PACKAGES_VIEW );

        layout.addStandaloneView( ID_LIFERAY_UPGRADE_VIEW, false, IPageLayout.TOP, 0.60f, editorArea );
    }

}
