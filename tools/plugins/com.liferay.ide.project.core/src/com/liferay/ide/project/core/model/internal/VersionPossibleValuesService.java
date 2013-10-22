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
package com.liferay.ide.project.core.model.internal;

import java.util.Set;

import org.eclipse.sapphire.modeling.Status.Severity;
import org.eclipse.sapphire.services.PossibleValuesService;


/**
 * @author Gregory Amerson
 */
public class VersionPossibleValuesService extends PossibleValuesService
{

    @Override
    protected void fillPossibleValues( Set<String> values )
    {
        values.add( "6.0.5" ); //$NON-NLS-1$
        values.add( "6.0.6" ); //$NON-NLS-1$
        values.add( "6.1.0" ); //$NON-NLS-1$
        values.add( "6.1.1" ); //$NON-NLS-1$
        values.add( "6.1.2" ); //$NON-NLS-1$
        values.add( "6.2.0-RC1" ); //$NON-NLS-1$
        values.add( "6.2.0-RC2" ); //$NON-NLS-1$
        values.add( "6.2.0-RC3" ); //$NON-NLS-1$
        values.add( "6.2.0-RC4" ); //$NON-NLS-1$
        values.add( "6.2.0-RC5" ); //$NON-NLS-1$
        values.add( "6.2.0-SNAPSHOT" ); //$NON-NLS-1$
    }

    @Override
    public boolean ordered()
    {
        return true;
    }

    @Override
    public Severity getInvalidValueSeverity( String invalidValue )
    {
        return Severity.OK;
    }

}
