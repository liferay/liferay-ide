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

package com.liferay.ide.project.ui.migration;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.navigator.CommonNavigator;

import com.liferay.ide.project.ui.ProjectUI;

/**
 * @author Terry Jia
 */
public class ShowAllAction extends Action
{

    private CommonNavigator cn;

    public ShowAllAction( String text, CommonNavigator cn )
    {
        super( text, IAction.AS_CHECK_BOX );
        this.cn = cn;
        setImageDescriptor( ProjectUI.getDefault().getImageRegistry().getDescriptor( ProjectUI.WAR_IMAGE_ID ) );
    }

    @Override
    public void run()
    {
        if (cn instanceof MigrationView) {
            MigrationView.showAll = !MigrationView.showAll;

            cn.getCommonViewer().collapseAll();
        }
    }

}
