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
 *      Gregory Amerson - initial implementation and ongoing maintenance
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

    private String bundleId;
    private Configuration config;
    private String id;
    private String name;
    private String resource;
    private String templateFolder;
    private TemplateVariable[] vars;

    public TemplateModel( String bundleId,
                          Configuration config,
                          String id,
                          String name,
                          String resource,
                          String templateFolder,
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

    public String getBundleId()
    {
        return bundleId;
    }

    public Configuration getConfig()
    {
        return config;
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
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

    public String getResource()
    {
        return resource;
    }

    public String getTemplateFolder()
    {
        return templateFolder;
    }

    public void setConfig( Configuration config )
    {
        this.config = config;
    }

}
