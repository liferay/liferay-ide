/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.templates.core;

import com.liferay.ide.core.util.CoreUtil;

import freemarker.template.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 */
public class TemplateModel
{

    protected String bundleId;
    protected Configuration config;
    protected String id;
    protected String name;
    protected String resource;
    protected String templateFolder;
    private TemplateVariable[] vars;

    public TemplateModel(
        String bundleId, Configuration config, String id, String name, String resource, String templateFolder,
        TemplateVariable[] vars )
    {

        super();
        this.bundleId = bundleId;
        this.config = config;
        this.id = id;
        this.name = name;
        this.resource = resource;
        this.templateFolder = templateFolder;
        this.vars = vars;
    }

    public Configuration getConfig()
    {
        return config;
    }

    public void setConfig( Configuration config )
    {
        this.config = config;
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getResource()
    {
        return resource;
    }

    public String getBundleId()
    {
        return bundleId;
    }

    public String[] getRequiredVarNames()
    {
        List<String> reqVarNames = new ArrayList<String>();

        if( !CoreUtil.isNullOrEmpty( vars ) )
        {
            for( TemplateVariable var : vars )
            {
                if( var.isRequired() )
                {
                    reqVarNames.add( var.getName() );
                }
            }
        }

        return reqVarNames.toArray( new String[0] );
    }

}
