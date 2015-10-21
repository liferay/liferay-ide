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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.ui.IViewPart;


/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class MigrationDecorator extends BaseLabelProvider implements ILightweightLabelDecorator
{

    private final String VIEW_ID = "com.liferay.ide.project.ui.migrationView";

    @Override
    public void decorate( Object element, IDecoration decoration )
    {
        if( element instanceof MPNode )
        {
            final MPNode node = (MPNode) element;

            final IResource member = CoreUtil.getWorkspaceRoot().findMember( node.incrementalPath );

            if( member != null && member.exists() )
            {
                element = member;
            }
        }

        final List<TaskProblem> problems = new ArrayList<>();

        final List<TaskProblem> resolvedProblems = new ArrayList<>();

        if( element instanceof IResource )
        {
            final IResource resource = (IResource) element;

            problems.addAll( MigrationUtil.getTaskProblemsFromResource( resource ) );
            resolvedProblems.addAll( MigrationUtil.getResolvedTaskProblemsFromResource( resource ) );
        }
        else if( element instanceof MPTree )
        {
            final IViewPart view = UIUtil.findView( VIEW_ID );

            if( view instanceof MigrationView )
            {
                problems.addAll( MigrationUtil.getAllTaskProblems( ( (MigrationView) view ).getCommonViewer() ) );
            }
        }

        if( problems != null && problems.size() > 0 )
        {
            for( TaskProblem problem : problems )
            {
                if( problem.isResolved() && !resolvedProblems.contains( problem ))
                {
                    resolvedProblems.add( problem );
                }
            }

            final StringBuilder sb = new StringBuilder();

            sb.append( String.format(
                " [%d%s problem%s",
                problems.size(),
                ( element instanceof MPTree ? " total" : ""),
                ( problems.size() > 1 ? "s" : "") ) );

            if( resolvedProblems.size() > 0 )
            {
                sb.append( ", " + resolvedProblems.size() + " resolved" );
            }

            sb.append( "]" );

            decoration.addSuffix( sb.toString() );
        }
    }

}
