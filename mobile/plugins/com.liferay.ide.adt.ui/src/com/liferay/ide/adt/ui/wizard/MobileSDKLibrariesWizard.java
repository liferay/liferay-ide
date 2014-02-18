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
package com.liferay.ide.adt.ui.wizard;

import com.liferay.ide.adt.core.model.MobileSDKLibrariesOp;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;


/**
 * @author Gregory Amerson
 */
public class MobileSDKLibrariesWizard extends SapphireWizard<MobileSDKLibrariesOp>
{

    public MobileSDKLibrariesWizard( final IJavaProject project )
    {
        super( MobileSDKLibrariesOp.TYPE, DefinitionLoader.sdef( MobileSDKLibrariesWizard.class ).wizard( "wizard" ) );

        this.element().setProjectName( project.getProject().getName() );
    }

}
