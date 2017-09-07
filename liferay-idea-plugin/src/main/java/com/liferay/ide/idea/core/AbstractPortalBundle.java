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

package com.liferay.ide.idea.core;

import com.liferay.ide.idea.ui.util.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.osgi.framework.Version;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Simon Jiang
 * @author Terry Jia
 */
public abstract class AbstractPortalBundle implements PortalBundle
{
    protected Path autoDeployPath;
    protected Path liferayHome;
    protected Path modulesPath;
    protected Path bundlePath;

    public AbstractPortalBundle( Path path )
    {
        if( path == null )
        {
            throw new IllegalArgumentException( "path cannot be null" );
        }

        bundlePath = path;

        liferayHome = Paths.get(bundlePath.toString(), "..");

        autoDeployPath = Paths.get(liferayHome.toString(), "deploy");

        modulesPath = Paths.get(liferayHome.toString(), "osgi");
    }

    @Override
    public Path getAppServerDir()
    {
        return bundlePath;
    }

    @Override
    public Path[] getBundleDependencyJars()
    {
        final List<Path> libs = new ArrayList<Path>();
        Path bundleLibPath =  getAppServerLibDir();
        List<File> libFiles;

        try
        {
            libFiles = FileListing.getFileListing( new File( bundleLibPath.toString() ) );

            for( File lib : libFiles )
            {
                if( lib.exists() && lib.getName().endsWith( ".jar" ) )
                {
                    libs.add( lib.toPath() );
                }
            }
        }
        catch( FileNotFoundException e )
        {
        }

        return libs.toArray( new Path[libs.size()] );
    }

    protected abstract Path getAppServerLibDir();

    protected abstract int getDefaultJMXRemotePort();

    @Override
    public String[] getHookSupportedProperties()
    {
        Path portalDir = getAppServerPortalDir();
        Path[] extraLibs = getBundleDependencyJars();

        return new LiferayPortalValueLoader( portalDir, extraLibs ).loadHookPropertiesFromClass();
    }

    protected String getHttpPortValue(
        File xmlFile, String tagName, String attriName, String attriValue, String targetName )
    {
        DocumentBuilder db = null;

        DocumentBuilderFactory dbf = null;

        try
        {
            dbf = DocumentBuilderFactory.newInstance();

            db = dbf.newDocumentBuilder();

            Document document = db.parse( xmlFile );

            NodeList connectorNodes = document.getElementsByTagName( tagName );

            for( int i = 0; i < connectorNodes.getLength(); i++ )
            {
                Node node = connectorNodes.item( i );

                NamedNodeMap attributes = node.getAttributes();

                Node protocolNode = attributes.getNamedItem( attriName );

                if( protocolNode != null )
                {
                    if( protocolNode.getNodeValue().equals( attriValue ) )
                    {
                        Node portNode = attributes.getNamedItem( targetName );

                        return portNode.getNodeValue();
                    }
                }
            }
        }
        catch( Exception e )
        {
        }

        return null;
    }

    @Override
    public int getJmxRemotePort()
    {
        return getDefaultJMXRemotePort();
    }

    @Override
    public Path getAutoDeployPath()
    {
        return autoDeployPath;
    }

    @Override
    public Path getModulesPath()
    {
        return modulesPath;
    }

    @Override
    public Path getLiferayHome()
    {
        return liferayHome;
    }

    @Override
    public String getVersion()
    {
        return "";
    }

    @Override
    public Path getOSGiBundlesDir()
    {
        return liferayHome == null ? null : Paths.get(liferayHome.toString(),"osgi");
    }

    @Override
    public Properties getPortletCategories()
    {
        return null;
    }

    @Override
    public Properties getPortletEntryCategories()
    {
        return null;
    }

}