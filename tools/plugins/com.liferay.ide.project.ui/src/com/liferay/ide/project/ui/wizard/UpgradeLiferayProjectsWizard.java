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
package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.model.NamedItem;
import com.liferay.ide.project.core.model.UpgradeLiferayProjectsOp;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizardPage;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

/**
 * @author Simon Jiang
 */

public class UpgradeLiferayProjectsWizard extends SapphireWizard<UpgradeLiferayProjectsOp>
    implements IWorkbenchWizard, INewWizard
{
    private boolean firstErrorMessageRemoved = false;
    public UpgradeLiferayProjectsWizard(IProject[] projects)
    {
        super( op(projects), DefinitionLoader.sdef( UpgradeLiferayProjectsWizard.class ).wizard() );
    }

    @Override
    public IWizardPage[] getPages()
    {
        final IWizardPage[] wizardPages = super.getPages();

        if( !firstErrorMessageRemoved && wizardPages != null )
        {
            final SapphireWizardPage wizardPage = (SapphireWizardPage) wizardPages[0];

            final String message = wizardPage.getMessage();
            final int messageType = wizardPage.getMessageType();

            if( messageType == IMessageProvider.ERROR && ! CoreUtil.isNullOrEmpty( message ) )
            {
                wizardPage.setMessage( message, SapphireWizardPage.ERROR ); //$NON-NLS-1$
                firstErrorMessageRemoved = true;
            }
        }

        return wizardPages;
    }

    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
    }

    private static UpgradeLiferayProjectsOp op(IProject[] projects)
    {
        UpgradeLiferayProjectsOp projectUpgradeOp = UpgradeLiferayProjectsOp.TYPE.instantiate();
        for(IProject project : projects)
        {
            NamedItem instance = (NamedItem) projectUpgradeOp.getSelectedProjects().insert();
            instance.setName( project.getName() );
        }

        return projectUpgradeOp;
    }
}
