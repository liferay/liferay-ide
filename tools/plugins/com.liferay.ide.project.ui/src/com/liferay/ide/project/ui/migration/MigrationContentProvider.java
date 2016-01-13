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

import com.liferay.ide.project.core.upgrade.MigrationProblems;
import com.liferay.ide.project.core.upgrade.Liferay7UpgradeAssistantSettings;
import com.liferay.ide.project.core.upgrade.UpgradeAssistantSettingsUtil;
import com.liferay.ide.project.core.upgrade.UpgradeProblems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class MigrationContentProvider implements ITreeContentProvider
{

    List<ProblemsContainer> _problems;

    @Override
    public void dispose()
    {
    }

    @Override
    public Object[] getChildren( Object parentElement )
    {
        if( parentElement instanceof ProblemsContainer )
        {
            ProblemsContainer problemsContainer = (ProblemsContainer) parentElement;

            List<UpgradeProblems> upgradeProblems = problemsContainer.getUpgradeProblemsList();

            if (upgradeProblems.size() == 1)
            {
                return problemsContainer.getUpgradeProblemsList().get( 0 ).getProblems();
            }
            else
            {
                return problemsContainer.getUpgradeProblemsList().toArray();
            }
        }
        else if( parentElement instanceof UpgradeProblems )
        {
            return ( (UpgradeProblems) parentElement ).getProblems();
        }

        return null;
    }

    @Override
    public Object[] getElements( Object inputElement )
    {
        return _problems.toArray();
    }

    @Override
    public Object getParent( Object element )
    {
        return null;
    }

    @Override
    public boolean hasChildren( Object element )
    {
        if( element instanceof ProblemsContainer )
        {
            ProblemsContainer problemsContainer = (ProblemsContainer) element;
            List<UpgradeProblems> upgradeProblems = problemsContainer.getUpgradeProblemsList();

            if (upgradeProblems.size() == 1)
            {
                return problemsContainer.getUpgradeProblemsList().get( 0 ).getProblems().length > 0;
            }
            else
            {
                return true;
            }
        }
        else if( element instanceof UpgradeProblems )
        {
            return ( (UpgradeProblems) element ).getProblems().length > 0;
        }

        return false;
    }

    @Override
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
    {
        if( newInput instanceof IWorkspaceRoot )
        {
            _problems = new ArrayList<ProblemsContainer>();

            try
            {
                Liferay7UpgradeAssistantSettings setting =
                    UpgradeAssistantSettingsUtil.getObjectFromStore( Liferay7UpgradeAssistantSettings.class );

                if( setting != null )
                {
                    ProblemsContainer problemsContainer = new ProblemsContainer();
                    problemsContainer.setUpgradeProblemsList( Collections.singletonList( (UpgradeProblems) setting.getPortalSettings() ) );

                    _problems.add( problemsContainer );
                }

                Object[] o = UpgradeAssistantSettingsUtil.getAllObjectFromStore( MigrationProblems.class );

                List<UpgradeProblems> codeProblems = new ArrayList<UpgradeProblems>();

                if( o != null && o.length > 0 )
                {
                    for( Object object : o )
                    {
                        codeProblems.add( (MigrationProblems) object );
                    }

                    ProblemsContainer problemDsiplay = new ProblemsContainer();
                    problemDsiplay.setUpgradeProblemsList( codeProblems );

                    _problems.add( problemDsiplay );
                }
            }
            catch( Exception e )
            {
            }
        }
    }

}
