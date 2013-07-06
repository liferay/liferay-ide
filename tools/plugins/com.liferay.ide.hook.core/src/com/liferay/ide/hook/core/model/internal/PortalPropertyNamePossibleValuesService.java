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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.modeling.Status.Severity;
import org.eclipse.sapphire.services.PossibleValuesService;

/**
 * @author Gregory Amerson
 * @author Tao Tao
 */
public class PortalPropertyNamePossibleValuesService extends PossibleValuesService
{

    private String[] hookProperties;
    private String[] wildCardHookProperties;

    @Override
    protected void fillPossibleValues( Set<String> values )
    {
        if( this.hookProperties != null )
        {
            values.addAll( Arrays.asList( this.hookProperties ) );
        }

        if( this.hookProperties == null )
        {
            final IProject project = context( Element.class ).root().adapt( IFile.class ).getProject();

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

    @Override
    public Severity getInvalidValueSeverity( String invalidValue )
    {
        if( wildCardHookProperties == null )
        {
            wildCardHookProperties = getWildCardHookProperties();
        }

        for( String wildCardProperty : wildCardHookProperties )
        {
            String propertyWithoutWildCard = wildCardProperty.substring( 0, ( wildCardProperty.indexOf( "*" ) - 1 ) ); //$NON-NLS-1$
            String pattern = "^" + propertyWithoutWildCard + "\\..+"; //$NON-NLS-1$ //$NON-NLS-2$

            if( invalidValue.matches( pattern ) )
            {
                return Severity.OK;
            }
        }

        return super.getInvalidValueSeverity( invalidValue );
    }

    public String[] getWildCardHookProperties()
    {
        List<String> properties = new ArrayList<String>();

        for( String property : hookProperties )
        {
            if( property.endsWith( ".*" ) ) //$NON-NLS-1$
            {
                properties.add( property );
            }
        }

       return properties.toArray( new String[0] );
    }
}
