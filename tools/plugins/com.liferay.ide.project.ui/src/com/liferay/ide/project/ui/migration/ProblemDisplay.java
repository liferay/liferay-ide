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

import com.liferay.ide.project.core.upgrade.UpgradeProblems;

import java.util.List;

/**
 * @author Terry Jia
 */
public class ProblemDisplay
{

    private List<UpgradeProblems> listUpgradeProblems;
    private UpgradeProblems singleUpgradeProblems;
    private String type;

    public List<UpgradeProblems> getListUpgradeProblems()
    {
        return listUpgradeProblems;
    }

    public UpgradeProblems getSingleUpgradeProblems()
    {
        return singleUpgradeProblems;
    }

    public String getType()
    {
        return type;
    }

    public boolean isSingle()
    {
        return singleUpgradeProblems != null;
    }

    public void setListUpgradeProblems( List<UpgradeProblems> listUpgradeProblems )
    {
        this.listUpgradeProblems = listUpgradeProblems;
        type = listUpgradeProblems.get( 0 ).getType();
    }

    public void setSingleUpgradeProblems( UpgradeProblems singleUpgradeProblems )
    {
        this.singleUpgradeProblems = singleUpgradeProblems;
        type = singleUpgradeProblems.getType();
    }
}
