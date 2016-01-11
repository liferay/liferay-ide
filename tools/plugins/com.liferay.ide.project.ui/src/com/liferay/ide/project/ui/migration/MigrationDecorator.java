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

import com.liferay.blade.api.Problem;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class MigrationDecorator extends BaseLabelProvider implements ILightweightLabelDecorator
{

    @Override
    public void decorate( Object element, IDecoration decoration )
    {

        final List<Problem> problems = new ArrayList<>();

        final List<Problem> resolvedProblems = new ArrayList<>();

        if( element instanceof IResource )
        {
            final IResource resource = (IResource) element;

            problems.addAll( MigrationUtil.getProblemsFromResource( resource ) );
            resolvedProblems.addAll( MigrationUtil.getResolvedProblemsFromResource( resource ) );
        }

        if( problems != null && problems.size() > 0 )
        {
            for( Problem problem : problems )
            {
                if( problem.getStatus() == Problem.STATUS_RESOLVED && !resolvedProblems.contains( problem ) )
                {
                    resolvedProblems.add( problem );
                }
            }

            final StringBuilder sb = new StringBuilder();

            if( resolvedProblems.size() > 0 )
            {
                sb.append( ", " + resolvedProblems.size() + " resolved" );
            }

            sb.append( "]" );

            decoration.addSuffix( sb.toString() );
        }
    }

}
