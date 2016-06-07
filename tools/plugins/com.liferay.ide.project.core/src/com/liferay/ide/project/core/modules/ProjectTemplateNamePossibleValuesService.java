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

package com.liferay.ide.project.core.modules;

import com.liferay.ide.core.LiferayCore;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.Value;

/**
 * @author Simon Jiang
 * @author Gregory Amerson
 */
public class ProjectTemplateNamePossibleValuesService extends PossibleValuesService
{
    private List<String> possibleValues;

    @Override
    protected void initPossibleValuesService()
    {
        possibleValues = new ArrayList<String>();

        new Job("Getting project templates") {

            @Override
            protected IStatus run( IProgressMonitor monitor )
            {
                try
                {
                    for( String projectTemplate : BladeCLI.getProjectTemplates() )
                    {
                        if ( !projectTemplate.contains( "fragment" ))
                        {
                            possibleValues.add( projectTemplate );
                        }
                    }
                }
                catch( Exception e )
                {
                    return LiferayCore.createErrorStatus( e );
                }

                refresh();

                return Status.OK_STATUS;
            }
        }.schedule();
    }

    @Override
    protected void compute( Set<String> values )
    {
        values.addAll( possibleValues );
    }

    @Override
    public org.eclipse.sapphire.modeling.Status problem( Value<?> value )
    {
        return org.eclipse.sapphire.modeling.Status.createOkStatus();
    }

    @Override
    public boolean ordered()
    {
        return true;
    }
}
