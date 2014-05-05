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

package com.liferay.ide.project.core.model;

import com.liferay.ide.project.core.UpgradeLiferayProjectAction;
import com.liferay.ide.project.core.UpgradeLiferayProjectActionReader;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;

/**
 * @author Simon Jiang
 */
public class UpgradeLiferayProjectsOpMethods
{

    public static final Status execute( final UpgradeLiferayProjectsOp op, final ProgressMonitor pm )
    {
        Status retval = Status.createOkStatus();

        final IProgressMonitor monitor = ProgressMonitorBridge.create( pm );

        monitor.beginTask( "Creating Liferay plugin project (this process may take several minutes)", 30 );

        final ElementList<NamedItem> projectItems = op.getSelectedProjects();
        final ElementList<NamedItem> upgradeActions = op.getSelectedActions();
        final String runtimeName = op.getRuntimeName().content();
        final List<String> projectItemNames = new ArrayList<String>();
        final List<String> projectActionItems = new ArrayList<String>();

        for( NamedItem projectItem : projectItems )
        {
            projectItemNames.add( projectItem.getName().content() );
        }

        for( NamedItem upgradeAction : upgradeActions )
        {
            projectActionItems.add( upgradeAction.getName().content() );
        }

        Status[] upgradeStatuses = performUpgrade( projectItemNames, projectActionItems, runtimeName, monitor );


        for( Status s : upgradeStatuses )
        {
            if( !s.ok() )
            {
                retval = Status.createErrorStatus( "Some upgrade actions failed, please see Eclipse error log for more details" );
            }
        }

        return retval;
    }


    private static HashMap<String, UpgradeLiferayProjectAction> getActionMap( List<UpgradeLiferayProjectAction> upgradeActions )
    {
        HashMap<String, UpgradeLiferayProjectAction> actionMaps = new HashMap<String,UpgradeLiferayProjectAction>();

        for( UpgradeLiferayProjectAction upgradeAction : upgradeActions)
        {
            actionMaps.put( upgradeAction.getUpgradeActionType(), upgradeAction );
        }

        return actionMaps;
    }

    public static final Status[] performUpgrade(
        final List<String> projectItems, final List<String> projectActions, final String runtimeName,
        final IProgressMonitor monitor )
    {
        final List<Status> retval = new ArrayList<Status>();

        int worked = 0;
        int workUnit = projectItems.size();
        int actionUnit = projectActions.size();
        int totalWork = 100;
        int perUnit = totalWork / ( workUnit * actionUnit );
        monitor.beginTask( "Upgrading Project ", totalWork );

        UpgradeLiferayProjectActionReader upgradeLiferayProjectActionReader = new UpgradeLiferayProjectActionReader();
        final HashMap<String, UpgradeLiferayProjectAction> actionMap =
            getActionMap( upgradeLiferayProjectActionReader.getUpgradeActions() );

        for( String projectItem : projectItems )
        {
            if( projectItem != null )
            {
                IProject project = ProjectUtil.getProject( projectItem );
                monitor.setTaskName( "Upgrading Project " + project.getName() );

                for( String action : projectActions )
                {
                    if( action.equals( "RuntimeUpgrade" ) )
                    {
                        UpgradeLiferayProjectAction upgradeLiferayProjectAction = actionMap.get( action );
                        final Status status = upgradeLiferayProjectAction.execute( project, runtimeName, monitor, perUnit );
                        retval.add( status );
                        worked = worked + totalWork / ( workUnit * actionUnit );
                        monitor.worked( worked );
                    }
                    else
                    {
                        UpgradeLiferayProjectAction upgradeLiferayProjectAction = actionMap.get( action );
                        final Status status = upgradeLiferayProjectAction.execute( project, monitor, perUnit );
                        retval.add( status );
                        worked = worked + totalWork / ( workUnit * actionUnit );
                        monitor.worked( worked );
                    }
                }
            }
        }

        return retval.toArray( new Status[0] );
    }
}
