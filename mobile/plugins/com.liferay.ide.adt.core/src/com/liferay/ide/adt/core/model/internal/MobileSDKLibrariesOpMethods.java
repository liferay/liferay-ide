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
package com.liferay.ide.adt.core.model.internal;

import com.liferay.ide.adt.core.ADTCore;
import com.liferay.ide.adt.core.model.MobileSDKLibrariesOp;
import com.liferay.ide.adt.core.model.ServerInstance;
import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.sapphire.modeling.Status;


/**
 * @author Gregory Amerson
 */
public class MobileSDKLibrariesOpMethods
{

    public static final Status execute( final MobileSDKLibrariesOp op, final ProgressMonitor monitor )
    {
        saveWizardSettings( op );

        // TODO perform op

        return Status.createOkStatus();
    }

    private static void saveWizardSettings( final MobileSDKLibrariesOp op )
    {
        if( ! CoreUtil.isNullOrEmpty( op.getUrl().content() ) )
        {
            try
            {
                final ElementList<ServerInstance> previousServerInstances = op.getPreviousServerInstances();

                if( ! containsInstance( op, previousServerInstances ) )
                {
                    op.getPreviousServerInstances().insert().copy( op );
                }

                op.resource().save();
            }
            catch( ResourceStoreException e )
            {
                ADTCore.logError( "Unable to persist wizard settings", e );
            }
        }
    }

    private static boolean containsInstance( MobileSDKLibrariesOp op, ElementList<ServerInstance> instances )
    {
        for( ServerInstance instance : instances )
        {
            if( instance.getUrl().content().equals( op.getUrl().content() ) )
            {
                return true;
            }
        }

        return false;
    }
}
