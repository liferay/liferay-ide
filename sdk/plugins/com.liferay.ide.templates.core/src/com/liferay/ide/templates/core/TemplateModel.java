/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.app.VelocityEngine;

/**
 * @author Gregory Amerson
 */
public class TemplateModel
{

    protected String bundleId;
    protected String id;
    protected String name;
    protected String resource;
    protected String templateFolder;
    protected VelocityEngine engine;
    private TemplateVariable[] vars;

    public TemplateModel(
        String bundleId, String id, String name, String resource, String templateFolder, VelocityEngine velocityEngine,
        TemplateVariable[] vars )
    {

        super();
        this.bundleId = bundleId;
        this.id = id;
        this.name = name;
        this.resource = resource;
        this.templateFolder = templateFolder;
        this.engine = velocityEngine;
        this.vars = vars;
    }

    public VelocityEngine getEngine()
    {
        return engine;
    }

    public void setEngine( VelocityEngine engine )
    {
        this.engine = engine;
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
