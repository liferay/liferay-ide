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

package com.liferay.ide.project.core.upgrade;

import com.liferay.blade.api.Problem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Terry Jia
 */
public class FileProblems
{

    private File _file;
    private List<Problem> _problems = new ArrayList<Problem>();

    public void addProblem( Problem problem )
    {
        _problems.add( problem );
    }

    public File getFile()
    {
        return _file;
    }

    public List<Problem> getProblems()
    {
        return _problems;
    }

    public void setFile( File file )
    {
        _file = file;
    }

    public void setProblems( List<Problem> problems )
    {
        _problems = problems;
    }
}
