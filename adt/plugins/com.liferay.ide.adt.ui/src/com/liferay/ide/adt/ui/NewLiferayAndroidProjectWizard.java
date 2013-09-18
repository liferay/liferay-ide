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
package com.liferay.ide.adt.ui;

import com.liferay.ide.adt.core.model.NewLiferayAndroidProjectOp;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.swt.SapphireWizard;
import org.eclipse.sapphire.ui.swt.SapphireWizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;


/**
 * @author Gregory Amerson
 */
public class NewLiferayAndroidProjectWizard extends SapphireWizard<NewLiferayAndroidProjectOp>
    implements IWorkbenchWizard, INewWizard
{

    private static NewLiferayAndroidProjectOp createDefaultOp()
    {
        return NewLiferayAndroidProjectOp.TYPE.instantiate();
    }

    public NewLiferayAndroidProjectWizard()
    {
        super( createDefaultOp(), DefinitionLoader.sdef( NewLiferayAndroidProjectWizard.class ).wizard() );
    }

    @Override
    public void createPageControls( Composite pageContainer )
    {
        super.createPageControls( pageContainer );

        SapphireWizardPage wizardPage = (SapphireWizardPage) getPages()[0];

        wizardPage.setMessage( "Please enter a project name", SapphireWizardPage.NONE );
    }

    @Override
    public void init( IWorkbench workbench, IStructuredSelection selection )
    {
    }

}
