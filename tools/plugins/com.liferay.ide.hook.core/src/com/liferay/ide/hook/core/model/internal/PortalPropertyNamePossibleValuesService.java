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
 * Contributors:
 *      Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.hook.core.model.internal;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;

import java.util.Arrays;
import java.util.SortedSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.services.PossibleValuesService;

/**
 * @author Gregory Amerson
 */
public class PortalPropertyNamePossibleValuesService extends PossibleValuesService
{

    private String[] hookProperties;

    @Override
    protected void fillPossibleValues( SortedSet<String> values )
    {
        if( this.hookProperties != null )
        {
            values.addAll( Arrays.asList( this.hookProperties ) );
        }

        if( this.hookProperties == null )
        {
            final IProject project = context( IModelElement.class ).root().adapt( IFile.class ).getProject();

            final ILiferayProject liferayProject = LiferayCore.create( project );

            if( liferayProject != null )
            {
                this.hookProperties = liferayProject.getHookSupportedProperties();
            }
        }

        if( this.hookProperties != null )
        {
            values.addAll( Arrays.asList( this.hookProperties ) );
        }
    }

}
