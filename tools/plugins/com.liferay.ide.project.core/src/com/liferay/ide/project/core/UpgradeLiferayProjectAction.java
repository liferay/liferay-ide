/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.project.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.modeling.Status;

/**
 * @author Simon Jiang
 */

public abstract class UpgradeLiferayProjectAction
{

    protected IProject project;

    protected String upgradeActionType;

    public UpgradeLiferayProjectAction()
    {
        super();
    }

    public UpgradeLiferayProjectAction( IProject project )
    {
        this.project = project;
    }


    public String getUpgradeActionType()
    {
        return upgradeActionType;
    }


    public void setUpgradeActionType( String upgradeActionType )
    {
        this.upgradeActionType = upgradeActionType;
    }

    public abstract Status execute( Object ...objects );

}
