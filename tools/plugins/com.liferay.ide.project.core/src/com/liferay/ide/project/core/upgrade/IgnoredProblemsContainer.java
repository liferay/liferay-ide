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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lovett Li
 */
public class IgnoredProblemsContainer
{

    private Map<String, Problem> problemMap;

    public IgnoredProblemsContainer()
    {
        problemMap = new HashMap<>();
    }

    public void add( Problem problem )
    {
        String ticketNum = problem.getTicket();
        problemMap.put( ticketNum, problem );
    }

    public void remove( Problem problem )
    {
        problemMap.remove( problem.getTicket() );
    }

    public Map<String, Problem> getProblemMap()
    {
        return problemMap;
    }

    public void setProblemMap( Map<String, Problem> problemMap )
    {
        this.problemMap = problemMap;
    }

}
