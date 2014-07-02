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
package com.liferay.ide.adt.ui.wizard;

import com.liferay.ide.adt.core.model.NewLiferayAndroidProjectOp;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizardPage;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;


/**
 * @author Gregory Amerson
 */
public class NewLiferayAndroidProjectWizard extends SapphireWizard<NewLiferayAndroidProjectOp>
    implements IWorkbenchWizard, INewWizard
{

    private boolean firstErrorMessageRemoved = false;

    private static NewLiferayAndroidProjectOp createDefaultOp()
    {
        return NewLiferayAndroidProjectOp.TYPE.instantiate();
    }

    public NewLiferayAndroidProjectWizard()
    {
        super( createDefaultOp(), DefinitionLoader.sdef( NewLiferayAndroidProjectWizard.class ).wizard() );
    }

    @Override
    public IWizardPage[] getPages()
    {
        final IWizardPage[] wizardPages = super.getPages();

        if( !firstErrorMessageRemoved && wizardPages != null )
        {
            final SapphireWizardPage wizardPage = (SapphireWizardPage) wizardPages[0];

            if( "Project name must be specified".equals( wizardPage.getMessage() ) )
            {
                wizardPage.setMessage( "Please enter a project name.", SapphireWizardPage.NONE );
                firstErrorMessageRemoved = true;
            }
        }

        return wizardPages;
    }

    @Override
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
    }

}
