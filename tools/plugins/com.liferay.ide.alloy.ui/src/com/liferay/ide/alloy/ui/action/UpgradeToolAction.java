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
package com.liferay.ide.alloy.ui.action;

import com.liferay.ide.alloy.core.AUIUpgradeToolJob;
import com.liferay.ide.ui.action.AbstractObjectAction;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;


/**
 * @author Gregory Amerson
 */
public class UpgradeToolAction extends AbstractObjectAction
{

    @Override
    public void run( IAction action )
    {
        if( fSelection instanceof IStructuredSelection )
        {
            Object[] elems = ( (IStructuredSelection) fSelection ).toArray();

            Object elem = elems[0];

            IProject project = null;

            if( elem instanceof IFile )
            {
                IFile projectFile = (IFile) elem;

                project = projectFile.getProject();
            }
            else if( elem instanceof IProject )
            {
                project = (IProject) elem;
            }

            if( project != null )
            {
                new AUIUpgradeToolJob( project ).schedule();
            }
        }
    }

}
