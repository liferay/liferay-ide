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

import java.io.File;


/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class TaskProblem extends Problem
{

    private boolean _resolved = false;
    private final long _markerId;


    public TaskProblem()
    {
        _markerId = -1;
    }

    public TaskProblem( Problem problem, boolean resolved, long markerId )
    {
        super( problem.title, problem.summary, problem.type, problem.ticket, problem.file,
               problem.lineNumber, problem.startOffset, problem.endOffset, problem.html, problem.autoCorrectContext,
               Problem.STATUS_NOT_RESOLVED, Problem.DEFAULT_MARKER_ID );

        _resolved = resolved;
        _markerId = markerId;
    }

    public TaskProblem(
        String title, String summary, String type, String ticket, File file, int lineNumber,
        int startOffset, int endOffset, String html, String autoCorrectContext, boolean resolved, long markerId)
    {
        super( title, summary, type, ticket, file, lineNumber, startOffset, endOffset, html, autoCorrectContext,
            Problem.STATUS_NOT_RESOLVED, Problem.DEFAULT_MARKER_ID );

        _resolved = resolved;
        _markerId = markerId;
    }

    public boolean isResolved()
    {
        return _resolved;
    }

    public long getMarkerId()
    {
        return _markerId;
    }

    public void setResolved( boolean b )
    {
        _resolved = b;
    }

}
