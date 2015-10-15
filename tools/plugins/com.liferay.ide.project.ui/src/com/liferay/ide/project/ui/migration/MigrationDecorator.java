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
        if( element instanceof MPNode )
        {
            final MPNode node = (MPNode) element;

            final IResource member = CoreUtil.getWorkspaceRoot().findMember( node.incrementalPath );

            if( member != null && member.exists() )
            {
                element = member;
            }
        }

        List<TaskProblem> problems = null;

        if( element instanceof IResource )
        {
            final IResource resource = (IResource) element;

            problems = MigrationUtil.getTaskProblemsFromResource( resource );
        }
        else if( element instanceof MPTree )
        {
            problems = MigrationUtil.getAllTaskProblems();
        }

        if( problems != null && problems.size() > 0 )
        {
            decoration.addSuffix( " [" + problems.size() + " problems]" );
        }
    }

}
