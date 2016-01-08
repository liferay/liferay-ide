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

/**
 * @author Lovett Li
 * @author Terry Jia
 */
public class PortalSettings implements UpgradeProblems
{

    private String _previousLiferayPortalLocation;
    private String _newName;
    private String _newliferayPortalLocation;
    private String _type;
    private FileProblems[] _problems;

    public PortalSettings()
    {
        super();
    }

    public PortalSettings(
        String previousLiferayPortalLocation, String newName, String newliferayPortalLocation, String type )
    {
        super();
        _previousLiferayPortalLocation = previousLiferayPortalLocation;
        _newName = newName;
        _newliferayPortalLocation = newliferayPortalLocation;
        _type = type;
    }

    public String getPreviousLiferayPortalLocation()
    {
        return _previousLiferayPortalLocation;
    }

    public void setPreviousLiferayPortalLocation( String liferayPortalLocation )
    {
        _previousLiferayPortalLocation = liferayPortalLocation;
    }

    public String getNewName()
    {
        return _newName;
    }

    public void setNewName( String newName )
    {
        _newName = newName;
    }

    public String getNewLiferayPortalLocation()
    {
        return _newliferayPortalLocation;
    }

    public void setNewLiferayPortalLocation( String newliferayPortalLocation )
    {
        _newliferayPortalLocation = newliferayPortalLocation;
    }

    public FileProblems[] getProblems()
    {
        return _problems;
    }

    public void setProblems( FileProblems[] problems )
    {
        _problems = problems;
    }

    public String getType()
    {
        return _type;
    }

    public void setType( String type )
    {
        _type = type;
    }

}
