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
 *******************************************************************************/

package com.liferay.ide.core.util;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.LiferayCore;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Kuo Zhang
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class PropertiesUtil
{

    public static void encodeLanguagePropertiesFilesToDefault( IResource resource, final IProgressMonitor monitor )
    {
        if( resource.getType() == IResource.PROJECT )
        {
            final IFile[] languagePropertiesFiles = getAllLanguagePropertiesFiles( (IProject) resource );

            for( IFile file : languagePropertiesFiles )
            {
                try
                {
                    String contents = CoreUtil.readStreamToString( file.getContents() );
                    file.setCharset( null, monitor );
                    file.setContents( new ByteArrayInputStream( contents.getBytes( "UTF-8" ) ), IResource.FORCE, monitor );
                }
                catch( Exception e )
                {
                    LiferayCore.logError( e );
                }
            }

        }
        else if( resource.getType() == IResource.FILE )
        {
            try
            {
                final IFile file = (IFile) resource;

                String contents = CoreUtil.readStreamToString( file.getContents() );
                file.setCharset( null, monitor );
                file.setContents( new ByteArrayInputStream( contents.getBytes( "UTF-8" ) ), IResource.FORCE, monitor );
            }
            catch( Exception e )
            {
                LiferayCore.logError( e );
            }
        }
    }

    // Get all the language properties files referenced from portlet.xml and liferay-hook.xml
    public static IFile[] getAllLanguagePropertiesFiles( IProject proj )
    {
        final List<IFile> retval = new ArrayList<IFile>();

        IFile[] resourceFiles = getLanguagePropertiesFromPortletXml( getPortletXml( proj ) );

        IFile[] languagenFiles = getLanguagePropertiesFromLiferayHookXml( getLiferayHookXml( proj ) );

        if( resourceFiles.length > 0 )
        {
            retval.addAll( Arrays.asList( resourceFiles ) );
        }

        if( languagenFiles.length > 0 )
        {
            retval.addAll( Arrays.asList( languagenFiles ) );
        }

        return retval.toArray( new IFile[0] );
    }

    private static LanguageFileInfo getLanguageFileInfo( IFile liferayHookXml )
    {
        final LanguageFileInfo retval = new LanguageFileInfo();

        try
        {
            final IStructuredModel model = StructuredModelManager.getModelManager().getModelForRead( liferayHookXml );

            if( model instanceof IDOMModel )
            {
                final IDOMDocument document = ( (IDOMModel) model ).getDocument();

                final NodeList languagePropertiesList = document.getElementsByTagName( "language-properties" );

                if( languagePropertiesList.getLength() > 0 )
                {
                    for( int i = 0; i < languagePropertiesList.getLength(); i++ )
                    {
                        final Node languageProperties = languagePropertiesList.item( i );

                        String languagePropertiesVal = NodeUtil.getTextContent( languageProperties );

                        retval.addLanguagePropertiesValue( languagePropertiesVal );
                    }
                }
            }

            model.releaseFromRead();
        }
        catch( Exception e )
        {
            // ignore errors this is best effort
        }

        return retval;
    }

    // Search all language properties files referenced by liferay-hook.xml
    public static IFile[] getLanguagePropertiesFromLiferayHookXml( IFile liferayHookXml )
    {
        final List<IFile> retval = new ArrayList<IFile>();

        final IProject proj = CoreUtil.getLiferayProject( liferayHookXml );

        if( proj == null )
        {
            return new IFile[0];
        }

        final IFolder[] srcFolders = CoreUtil.getSrcFolders( proj );

        if( srcFolders.length < 1 )
        {
            return new IFile[0];
        }

        final IWorkspaceRoot root = CoreUtil.getWorkspaceRoot();

        if( liferayHookXml != null && liferayHookXml.exists() )
        {
            final LanguageFileInfo languageFileInfo = getLanguageFileInfo( liferayHookXml );

            for( final String languagePropertyVal : languageFileInfo.getLanguagePropertyValues() )
            {
                // In liferay-hook.xml, the content of language-properties can use a wildcard
                // "*", need to search all the files whose names match the content.
                if( languagePropertyVal.contains( StringPool.ASTERISK ) )
                {
                    final String languagePropertiesValRegex = languagePropertyVal.replaceAll( "\\*", ".*" );

                    for( final IFolder srcFolder : srcFolders )
                    {
                        IFile[] languagePropertiesFiles = visitPropertiesFiles( srcFolder, languagePropertiesValRegex );

                        if( languagePropertiesFiles != null && languagePropertiesFiles.length > 0 )
                        {
                            for( IFile file : languagePropertiesFiles )
                            {
                                if( file.exists() )
                                {
                                    retval.add( file );
                                }
                            }
                        }
                    }
                }
                else
                {
                    for( final IFolder srcFolder : srcFolders )
                    {
                        final IFile languagePropertiesFile = root.getFile( srcFolder.getFullPath().append( languagePropertyVal ) );

                        if( languagePropertiesFile.exists() )
                        {
                            retval.add( languagePropertiesFile );
                        }
                    }
                }
            }
        }

        return retval.toArray( new IFile[0] );
    }

    public static IFile getLiferayHookXml( IProject proj )
    {
        final IFolder defaultDocrootFolder = CoreUtil.getDefaultDocrootFolder( proj );

        if( defaultDocrootFolder != null )
        {
            final IFile liferayHookXml = defaultDocrootFolder.getFile( new Path( "WEB-INF/liferay-hook.xml" ) );

            if( liferayHookXml != null && liferayHookXml.exists() )
            {
                return liferayHookXml;
            }
        }

        return null;
    }

    public static IFile getPortletXml( IProject proj )
    {
        final IFolder defaultDocrootFolder = CoreUtil.getDefaultDocrootFolder( proj );

        if( defaultDocrootFolder != null )
        {
            final IFile  portletXml = defaultDocrootFolder.getFile( new Path( "WEB-INF/portlet.xml" ) );

            if( portletXml!=null && portletXml.exists() )
            {
                return portletXml;
            }
        }

        return null;
    }

    // Search all resource bundle and supported locale files referenced by portlet.xml.
    public static IFile[] getLanguagePropertiesFromPortletXml( IFile portletXml )
    {
        final List<IFile> retval = new ArrayList<IFile>();

        final IProject proj = CoreUtil.getLiferayProject( portletXml );

        if( proj == null )
        {
            return new IFile[0];
        }

        final IFolder[] srcFolders = CoreUtil.getSrcFolders( proj );

        if( srcFolders.length < 1 )
        {
            return new IFile[0];
        }

        final IWorkspaceRoot root = CoreUtil.getWorkspaceRoot();

        if( portletXml != null && portletXml.exists() )
        {
            final ResourceNodeInfo resourceNodeInfo = getResourceNodeInfo( portletXml );

            for( final String resourceBundleValue : resourceNodeInfo.getResourceBundleValues() )
            {
                for( IFolder srcFolder : srcFolders )
                {
                    final IFile resourceBundleFile = root.getFile( srcFolder.getFullPath().append( resourceBundleValue ) );

                    if( resourceBundleFile.exists() )
                    {
                        retval.add( resourceBundleFile );
                    }
                }
            }

            for( final String supportedLocaleValue : resourceNodeInfo.getSupportedLocaleValues() )
            {
                for( IFolder srcFolder : srcFolders )
                {
                    final IFile supportedLocaleFile = root.getFile( srcFolder.getFullPath().append( supportedLocaleValue ) );

                    if( supportedLocaleFile.exists() )
                    {
                        retval.add( supportedLocaleFile );
                    }
                }
            }
        }

        return retval.toArray( new IFile[0] );
    }

    private static ResourceNodeInfo getResourceNodeInfo( IFile portletXml )
    {
        final ResourceNodeInfo retval = new ResourceNodeInfo();

        try
        {
            final IStructuredModel model = StructuredModelManager.getModelManager().getModelForRead( portletXml );

            if( model instanceof IDOMModel )
            {
                final IDOMDocument document = ( (IDOMModel) model ).getDocument();

                final NodeList portlets = document.getElementsByTagName( "portlet" );
                final NodeList allResourceBundles = document.getElementsByTagName( "resource-bundle" );
                final NodeList allSupportedLocales = document.getElementsByTagName( "supported-locale" );

                if( portlets != null && portlets.getLength() > 0 )
                {
                    for( int i = 0; i < portlets.getLength(); i++ )
                    {
                        Node portlet = portlets.item( i );
                        Node resourceBundle = null;
                        List<Node> supportedLocales = new ArrayList<Node>();

                        if( allResourceBundles.getLength() > 0 )
                        {
                            for( int j = 0; j < allResourceBundles.getLength(); j++ )
                            {
                                if( allResourceBundles.item( j ).getParentNode().equals( portlet ) )
                                {
                                    resourceBundle = allResourceBundles.item( j );
                                }
                            }

                            // Supported locale depends on resource bundle, if a portlet.xml does't have a resource-bundle element
                            // then supported locale will be ignored in the encoding validation.
                            if( resourceBundle != null )
                            {
                                final String resourceBundleVal = NodeUtil.getTextContent( resourceBundle );

                                retval.addResourceBundle( resourceBundleVal.replaceAll( "\\.", "/" ) + ".properties" );

                                if( allSupportedLocales.getLength() > 0 )
                                {
                                    for( int k = 0; k < allSupportedLocales.getLength(); k++ )
                                    {
                                        if( allSupportedLocales.item( k ).getParentNode().equals( portlet ) )
                                        {
                                            supportedLocales.add( allSupportedLocales.item( k ) );
                                        }
                                    }
                                }

                                if( supportedLocales.size() > 0 )
                                {
                                    for( Node supportedLocale : supportedLocales )
                                    {
                                        String supportedLocaleVal = NodeUtil.getTextContent( supportedLocale );

                                        String supportedLocaleResourceVal =
                                            resourceBundleVal.replaceAll( "\\.", "/" ) + "_" + supportedLocaleVal +
                                                ".properties";

                                        retval.addSupportedLocaleVal( supportedLocaleResourceVal );
                                    }
                                }
                            }
                        }
                    }
                }

                model.releaseFromRead();
            }
        }
        catch( Exception e )
        {
            // no error this is best effort
        }

        return retval;
    }

    public static boolean hasNonDefaultEncodingLanguagePropertiesFile( IProject proj )
    {
        try
        {
            final IFile[] resourceFiles = getLanguagePropertiesFromPortletXml( getPortletXml( proj ) );

            for( IFile file : resourceFiles )
            {
                if( ! ILiferayConstants.LIFERAY_LANGUAGE_PROPERTIES_FILE_ENCODING_CHARSET.equals( file.getCharset() ) )
                {
                    return true;
                }
            }

            final IFile[] languageFiles = getLanguagePropertiesFromLiferayHookXml( getLiferayHookXml( proj ) );

            for( IFile file : languageFiles )
            {
                if( ! ILiferayConstants.LIFERAY_LANGUAGE_PROPERTIES_FILE_ENCODING_CHARSET.equals( file.getCharset() ) )
                {
                    return true;
                }
            }
        }
        catch( CoreException e )
        {
            LiferayCore.logError( e );
        }

        return false;
    }

    // Check if the file is the language properties files referenced from portlet.xml or liferay-hook.xml
    public static boolean isLanguagePropertiesFile( IFile targetFile )
    {
        if( ! targetFile.getName().endsWith( ".properties" ) )
        {
            return false;
        }

        final IProject project = CoreUtil.getLiferayProject( targetFile );

        if( project == null )
        {
            return false;
        }

        final IFile portletXml = getPortletXml( project );
        final IFile liferayHookXml = getLiferayHookXml( project );

        final IFolder[] srcFolders = CoreUtil.getSrcFolders( project );
        final IPath targetFileLocation = targetFile.getLocation();

        if( portletXml != null && portletXml.exists() )
        {
            final String[] resourceBundleValues = getResourceNodeInfo( portletXml ).getResourceBundleValues();

            for( String resourceBundleValue : resourceBundleValues )
            {
                for( IFolder srcFolder : srcFolders )
                {
                    if( targetFileLocation.makeRelativeTo( srcFolder.getLocation() ).toString().equals( resourceBundleValue ) )
                    {
                        return true;
                    }
                }
            }

            final String[] supportedLocaleValues = getResourceNodeInfo( portletXml ).getSupportedLocaleValues();

            for( String suportedLocaleValue : supportedLocaleValues )
            {
                for( IFolder srcFolder : srcFolders )
                {
                    if( targetFileLocation.makeRelativeTo( srcFolder.getLocation() ).toString().equals( suportedLocaleValue ) )
                    {
                        return true;
                    }
                }
            }
        }

        if( liferayHookXml != null && liferayHookXml.exists() )
        {
            final String[] languagePropertyValues = getLanguageFileInfo( liferayHookXml ).getLanguagePropertyValues();

            for( String languagePropertyValue : languagePropertyValues )
            {
                for( IFolder srcFolder : srcFolders )
                {
                    if( targetFileLocation.makeRelativeTo( srcFolder.getLocation() ).toString().matches(
                        languagePropertyValue.replaceAll( "\\*", ".*" ) ) )
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static IFile[] visitPropertiesFiles( IResource container, String relativePath )
    {
        return new PropertiesVisitor().visitPropertiesFiles( container, relativePath );
    }

    private static class LanguageFileInfo
    {
        private final List<String> vals = new ArrayList<String>();

        public void addLanguagePropertiesValue( String languagePropertiesVal )
        {
            vals.add(languagePropertiesVal);
        }

        public String[] getLanguagePropertyValues()
        {
            return vals.toArray( new String[0] );
        }
    }

    private static class PropertiesVisitor implements IResourceProxyVisitor
    {
        IResource entryResource = null;
        String relativePathToEntry = null;
        List<IFile> resources = new ArrayList<IFile>();

        public boolean visit( IResourceProxy resourceProxy )
        {
            if( resourceProxy.getType() == IResource.FILE && resourceProxy.getName().endsWith( ".properties" ) )
            {
                IResource resource = resourceProxy.requestResource();

                String relativePath =
                    resource.getLocation().makeRelativeTo( entryResource.getLocation() ).toString();

                if( relativePath.matches( relativePathToEntry ) )
                {
                    resources.add( (IFile) resource );
                }
            }

            return true;
        }

        public IFile[] visitPropertiesFiles( IResource container, String relativePath )
        {
            this.entryResource = container;
            this.relativePathToEntry = relativePath;

            try
            {
                container.accept( this, IContainer.EXCLUDE_DERIVED );
            }
            catch( CoreException e )
            {
                LiferayCore.logError( e );
            }

            return resources.toArray( new IFile[resources.size()] );
        }
    }

    private static class ResourceNodeInfo
    {
        private final List<String> resourceBundlesValues = new ArrayList<String>();
        private final List<String> supportedLocaleValues = new ArrayList<String>();

        public void addResourceBundle( String resourceBundleVal )
        {
            this.resourceBundlesValues.add( resourceBundleVal );
        }

        public void addSupportedLocaleVal( String supportedLocaleVal )
        {
            this.supportedLocaleValues.add( supportedLocaleVal );
        }

        public String[] getResourceBundleValues()
        {
            return this.resourceBundlesValues.toArray( new String[0] );
        }

        public String[] getSupportedLocaleValues()
        {
            return this.supportedLocaleValues.toArray( new String[0] );
        }
    }
}
