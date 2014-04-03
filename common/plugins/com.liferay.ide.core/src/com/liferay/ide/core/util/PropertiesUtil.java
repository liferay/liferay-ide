/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Kuo Zhang
 * @author Gregory Amerson
 */
public class PropertiesUtil
{

    public final static String ELEMENT_LANGUAGE_PROPERTIES = "language-properties";

    public final static String ELEMENT_PORTLET = "portlet";

    public final static String ELEMENT_RESOURCE_BUNDLE = "resource-bundle";

    public final static String ELEMENT_SUPPORTED_LOCALE = "supported-locale";

    public final static String PROPERTIES_FILE_SUFFIX = ".properties";

    private final static SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

    private static LanguageFileInfo tmpLanguageFileInfo = null;

    private static ResourceNodeInfo tmpResourceNodeInfo = null;

    public static void encodeLanguagePropertiesFilesToDefault( IResource resource, final IProgressMonitor monitor )
    {
        if( resource.getType() == IResource.PROJECT )
        {
            final IFile[] languagePropertiesFiles = getAllLanguagePropertiesFiles( (IProject) resource );

            for( final IFile file : languagePropertiesFiles )
            {
                encodeLanguagePropertyFile( file, monitor );
            }
        }
        else if( resource.getType() == IResource.FILE )
        {
            final IFile file = (IFile) resource;

            encodeLanguagePropertyFile( file, monitor );
        }
    }

    private static void encodeLanguagePropertyFile( final IFile file, final IProgressMonitor monitor )
    {
        try
        {
            final String contents = CoreUtil.readStreamToString( file.getContents() );
            file.setCharset( null, monitor );
            file.setContents( new ByteArrayInputStream( contents.getBytes( "UTF-8" ) ), IResource.FORCE, monitor );
        }
        catch( Exception e )
        {
            LiferayCore.logError( e );
        }
    }

    // Get all the language properties files referenced from portlet.xml and liferay-hook.xml
    public static IFile[] getAllLanguagePropertiesFiles( IProject proj )
    {
        final List<IFile> retval = new ArrayList<IFile>();

        final IFile[] resourceFiles =
            getLanguagePropertiesFromPortletXml( CoreUtil.getDescriptorFile( proj, ILiferayConstants.PORTLET_XML_FILE ) );

        final IFile[] languageFiles =
            getLanguagePropertiesFromLiferayHookXml( CoreUtil.getDescriptorFile(
                proj, ILiferayConstants.LIFERAY_HOOK_XML_FILE ) );

        if( resourceFiles.length > 0 )
        {
            retval.addAll( Arrays.asList( resourceFiles ) );
        }

        if( languageFiles.length > 0 )
        {
            retval.addAll( Arrays.asList( languageFiles ) );
        }

        return retval.toArray( new IFile[0] );
    }

    private static synchronized LanguageFileInfo getLanguageFileInfo( IFile liferayHookXml )
    {
        if( tmpLanguageFileInfo == null || ! tmpLanguageFileInfo.getLiferayHookXml().equals( liferayHookXml ) ||
            tmpLanguageFileInfo.getModificationStamp() != liferayHookXml.getModificationStamp() )
        {
            final LanguageFileInfo retval = new LanguageFileInfo( liferayHookXml );

            try
            {
                final DefaultHandler handler = new DefaultHandler()
                {
                    boolean isLangPropElem = false;

                    public void startElement( String uri, String localName, String qName, Attributes attributes )
                        throws SAXException
                    {
                        if( qName.equals( ELEMENT_LANGUAGE_PROPERTIES ) )
                        {
                            isLangPropElem = true;
                        }
                    }

                    public void characters( char ch[], int start, int length ) throws SAXException
                    {
                        if( isLangPropElem )
                        {
                            final String languagePropertiesValue = new String( ch, start, length );

                            if( languagePropertiesValue.endsWith( PROPERTIES_FILE_SUFFIX ) )
                            {
                                final String[] languagePropertiesPatterns =
                                    generateLanguagePropertiesPatterns(
                                        languagePropertiesValue, ELEMENT_LANGUAGE_PROPERTIES );

                                for( String pattern : languagePropertiesPatterns )
                                {
                                    if( pattern != null )
                                    {
                                        retval.addLanguagePropertiesPattern( pattern );
                                    }
                                }
                            }

                        }
                    }

                    public void endElement( String uri, String localName, String qName ) throws SAXException
                    {
                        if( qName.equals( ELEMENT_LANGUAGE_PROPERTIES ) )
                        {
                            isLangPropElem = false;
                        }
                    }
                };

                final InputStream contents = liferayHookXml.getContents();

                final SAXParser saxParser = saxParserFactory.newSAXParser();

                final XMLReader xmlReader = saxParser.getXMLReader();
                xmlReader.setFeature( "http://apache.org/xml/features/nonvalidating/load-external-dtd", false );
                xmlReader.setFeature( "http://xml.org/sax/features/validation", false );

                saxParser.parse( contents, handler );

                contents.close();
            }
            catch( Exception e )
            {
                LiferayCore.logError( "Error resolving" + ILiferayConstants.LIFERAY_HOOK_XML_FILE, e );
            }

            tmpLanguageFileInfo = retval;
        }

        return tmpLanguageFileInfo;
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

        if( liferayHookXml != null && liferayHookXml.exists() )
        {
            final LanguageFileInfo languageFileInfo = getLanguageFileInfo( liferayHookXml );

            for( String languagePropertiesVal : languageFileInfo.getLanguagePropertyPatterns() )
            {
                for( final IFolder srcFolder : srcFolders )
                {
                    final IFile[] languagePropertiesFiles = visitPropertiesFiles( srcFolder, languagePropertiesVal );

                    if( languagePropertiesFiles != null && languagePropertiesFiles.length > 0 )
                    {
                        retval.addAll( Arrays.asList( languagePropertiesFiles ) );
                    }
                }
            }
        }

        return retval.toArray( new IFile[0] );
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

        if( portletXml != null && portletXml.exists() )
        {
            for( IFolder srcFolder : srcFolders )
            {
                final ResourceNodeInfo resourceNodeInfo = getResourceNodeInfo( portletXml );

                for( String resourceBundleValue : resourceNodeInfo.getResourceBundlePatterns() )
                {
                    final IFile[] resourceBundleFiles = visitPropertiesFiles( srcFolder, resourceBundleValue );

                    if( resourceBundleFiles != null && resourceBundleFiles.length > 0 )
                    {
                        retval.addAll( Arrays.asList( resourceBundleFiles ) );
                    }
                }

                for( final String supportedLocaleValue : resourceNodeInfo.getSupportedLocalePatterns() )
                {
                    final IFile[] supportedLocaleFiles = visitPropertiesFiles( srcFolder, supportedLocaleValue );

                    if( supportedLocaleFiles != null && supportedLocaleFiles.length > 0 )
                    {
                        retval.addAll( Arrays.asList( supportedLocaleFiles ) );
                    }
                }
            }
        }

        return retval.toArray( new IFile[0] );
    }

    private static synchronized ResourceNodeInfo getResourceNodeInfo( IFile portletXml )
    {
        if( tmpResourceNodeInfo == null || ! tmpResourceNodeInfo.getPortletXml().equals( portletXml ) ||
            tmpResourceNodeInfo.getModificationStamp() != portletXml.getModificationStamp() )
        {
            final ResourceNodeInfo retval = new ResourceNodeInfo( portletXml );

            try
            {
                final DefaultHandler handler = new DefaultHandler()
                {
                    boolean isResourceBundleElem = false;
                    boolean isSupportedLocaleElem = false;

                    String resourceBundleValue = null;
                    final List<String> supportedLocaleValues = new ArrayList<String>();

                    public void startElement( String uri, String localName, String qName, Attributes attributes )
                        throws SAXException
                    {
                        if( qName.equals( ELEMENT_RESOURCE_BUNDLE ) )
                        {
                            isResourceBundleElem = true;
                        }

                        if( qName.equals( ELEMENT_SUPPORTED_LOCALE ) )
                        {
                            isSupportedLocaleElem = true;
                        }
                    }

                    public void characters( char ch[], int start, int length ) throws SAXException
                    {
                        if( isSupportedLocaleElem )
                        {
                            supportedLocaleValues.add( new String( ch, start, length ) );
                        }

                        if( isResourceBundleElem )
                        {
                            resourceBundleValue = new String( ch, start, length );
                        }
                    }

                    public void endElement( String uri, String localName, String qName ) throws SAXException
                    {
                        if( qName.equals( ELEMENT_RESOURCE_BUNDLE ) )
                        {
                            isResourceBundleElem = false;
                        }

                        if( qName.equals( ELEMENT_SUPPORTED_LOCALE ) )
                        {
                            isSupportedLocaleElem = false;
                        }

                        if( qName.equals( ELEMENT_PORTLET ) )
                        {
                            if( !CoreUtil.isNullOrEmpty( resourceBundleValue ) )
                            {
                                final String[] resourceBundlesPatterns =
                                    generateLanguagePropertiesPatterns( resourceBundleValue, ELEMENT_RESOURCE_BUNDLE );

                                for( String pattern : resourceBundlesPatterns )
                                {
                                    if( !CoreUtil.isNullOrEmpty( pattern ) )
                                    {
                                        retval.addResourceBundlePattern( pattern );
                                    }
                                }

                                if( supportedLocaleValues.size() > 0 )
                                {
                                    final String resourceBundleValueBase = resourceBundlesPatterns[0];

                                    for( String supportedLocaleValue : supportedLocaleValues )
                                    {
                                        retval.addSupportedLocalePattern( resourceBundleValueBase + "_" +
                                            supportedLocaleValue );
                                    }
                                }
                            }

                            resourceBundleValue = null;
                            supportedLocaleValues.clear();
                        }
                    }
                };

                final InputStream contents = portletXml.getContents();

                final SAXParser saxParser = saxParserFactory.newSAXParser();

                final XMLReader xmlReader = saxParser.getXMLReader();
                xmlReader.setFeature( "http://apache.org/xml/features/nonvalidating/load-external-dtd", false );
                xmlReader.setFeature( "http://xml.org/sax/features/validation", false );

                saxParser.parse( contents, handler );

                contents.close();
            }
            catch( Exception e )
            {
                LiferayCore.logError( "Error resolving" + ILiferayConstants.PORTLET_XML_FILE, e );
            }

            tmpResourceNodeInfo = retval;
        }

        return tmpResourceNodeInfo;
    }

    /*
     *  Convert the element values of <resource-bundle> in portlet.xml and <language-properties> in liferay-hook.xml
     *  to the corresponding regular expression to match the local files.
     *  The return values is: String[0] is base value of normal format without suffix, String[1] is a regex.
     *  Both may be null, check them before using them.
     */
    public static String[] generateLanguagePropertiesPatterns( String baseValue, String elementName )
    {
        String regex = null;

        if( elementName.equals( ELEMENT_RESOURCE_BUNDLE ) )
        {
            // Usually the content of <resource-bundle> doesn't contain ".properties", if it does, replace that with empty string.
            if( baseValue.endsWith( PROPERTIES_FILE_SUFFIX ) )
            {
                baseValue.replace( PROPERTIES_FILE_SUFFIX, "" );
            }

            baseValue = baseValue.replace(".", "/");

            if( ! baseValue.contains( "_" ) )
            {
                regex = baseValue + "_.*";
            }
        }
        else if( elementName.equals( ELEMENT_LANGUAGE_PROPERTIES ) )
        {
            baseValue = baseValue.replace( PROPERTIES_FILE_SUFFIX, "" );

            if( baseValue.contains( "*" ) )
            {
                regex = baseValue.replace( "*", ".*" );

                baseValue = null;
            }
            else
            {
                if( ! baseValue.contains( "_" ) )
                {
                    regex = baseValue + "_.*";
                }
            }
        }

        String[] retval = new String[]{ baseValue, regex };

        return retval;
    }

    public static boolean hasNonDefaultEncodingLanguagePropertiesFile( IProject proj )
    {
        try
        {
            final IFile[] resourceFiles =
                getLanguagePropertiesFromPortletXml( CoreUtil.getDescriptorFile(
                    proj, ILiferayConstants.PORTLET_XML_FILE ) );

            for( IFile file : resourceFiles )
            {
                if( ! ILiferayConstants.LANGUAGE_PROPERTIES_FILE_ENCODING_CHARSET.equals( file.getCharset() ) )
                {
                    return true;
                }
            }

            final IFile[] languageFiles =
                getLanguagePropertiesFromLiferayHookXml( CoreUtil.getDescriptorFile(
                    proj, ILiferayConstants.LIFERAY_HOOK_XML_FILE ) );

            for( IFile file : languageFiles )
            {
                if( ! ILiferayConstants.LANGUAGE_PROPERTIES_FILE_ENCODING_CHARSET.equals( file.getCharset() ) )
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

    // Check if the file is a language properties file referenced from portlet.xml or liferay-hook.xml
    public static boolean isLanguagePropertiesFile( IFile targetFile )
    {
        if( ! targetFile.getName().endsWith( PROPERTIES_FILE_SUFFIX ) )
        {
            return false;
        }

        final IProject project = CoreUtil.getLiferayProject( targetFile );

        if( project == null )
        {
            return false;
        }

        final IFile portletXml = CoreUtil.getDescriptorFile( project, ILiferayConstants.PORTLET_XML_FILE );
        final IFile liferayHookXml = CoreUtil.getDescriptorFile( project, ILiferayConstants.LIFERAY_HOOK_XML_FILE );

        final IFolder[] srcFolders = CoreUtil.getSrcFolders( project );
        final IPath targetFileLocation = targetFile.getLocation();

        if( portletXml != null && portletXml.exists() )
        {
            final String[] resourceBundleValues = getResourceNodeInfo( portletXml ).getResourceBundlePatterns();

            for( String resourceBundleValue : resourceBundleValues )
            {
                for( IFolder srcFolder : srcFolders )
                {
                    if( targetFileLocation.makeRelativeTo( srcFolder.getLocation() ).toString().
                        replace( PROPERTIES_FILE_SUFFIX, "" ).matches( resourceBundleValue ) )
                    {
                        return true;
                    }
                }
            }

            final String[] supportedLocaleValues = getResourceNodeInfo( portletXml ).getSupportedLocalePatterns();

            for( String suportedLocaleValue : supportedLocaleValues )
            {
                for( IFolder srcFolder : srcFolders )
                {
                    if( targetFileLocation.makeRelativeTo( srcFolder.getLocation() ).toString().
                        replace( PROPERTIES_FILE_SUFFIX, "" ).matches( suportedLocaleValue ) )
                    {
                        return true;
                    }
                }
            }
        }

        if( liferayHookXml != null && liferayHookXml.exists() )
        {
            final String[] languagePropertyValues = getLanguageFileInfo( liferayHookXml ).getLanguagePropertyPatterns();

            for( String languagePropertyValue : languagePropertyValues )
            {
                for( IFolder srcFolder : srcFolders )
                {
                    if( targetFileLocation.makeRelativeTo( srcFolder.getLocation() ).toString().
                        replace( PROPERTIES_FILE_SUFFIX, "" ).matches( languagePropertyValue ) )
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
        if( relativePath.contains( "*" ) )
        {
            return new PropertiesVisitor().visitPropertiesFiles( container, relativePath );
        }
        else
        {
            final IFile file = CoreUtil.getWorkspaceRoot().
                getFile( container.getFullPath().append( relativePath + PROPERTIES_FILE_SUFFIX ) );

            if( file != null && file.exists() )
            {
                return new IFile[] { file };
            }
        }

        return null;
    }

    private static class LanguageFileInfo
    {
        private IFile liferayHookXml;
        private long modificationStamp;

        public LanguageFileInfo( IFile file )
        {
            liferayHookXml = file;
            modificationStamp = liferayHookXml.getModificationStamp();
        }

        public IFile getLiferayHookXml()
        {
            return liferayHookXml;
        }

        public long getModificationStamp()
        {
            return modificationStamp;
        }

        private final List<String> vals = new ArrayList<String>();

        public void addLanguagePropertiesPattern( String languagePropertiesVal )
        {
            vals.add(languagePropertiesVal);
        }

        public String[] getLanguagePropertyPatterns()
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
            if( resourceProxy.getType() == IResource.FILE && resourceProxy.getName().endsWith( PROPERTIES_FILE_SUFFIX ) )
            {
                IResource resource = resourceProxy.requestResource();

                if( resource.exists() )
                {
                    String relativePath = resource.getLocation().
                        makeRelativeTo( entryResource.getLocation() ).toString().replace( PROPERTIES_FILE_SUFFIX, "" );

                    if( relativePath.matches( relativePathToEntry ) )
                    {
                        resources.add( (IFile) resource );
                    }
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
        private IFile portletXml;
        private long modificationStamp;

        public ResourceNodeInfo( IFile file )
        {
            portletXml = file;
            modificationStamp = portletXml.getModificationStamp();
        }

        public IFile getPortletXml()
        {
            return portletXml;
        }

        public long getModificationStamp()
        {
            return modificationStamp;
        }

        private final List<String> resourceBundlesPatterns = new ArrayList<String>();
        private final List<String> supportedLocalePatterns = new ArrayList<String>();

        public void addResourceBundlePattern( String resourceBundlePattern )
        {
            this.resourceBundlesPatterns.add( resourceBundlePattern );
        }

        public void addSupportedLocalePattern( String supportedLocalePattern )
        {
            this.supportedLocalePatterns.add( supportedLocalePattern );
        }

        public String[] getResourceBundlePatterns()
        {
            return this.resourceBundlesPatterns.toArray( new String[0] );
        }

        public String[] getSupportedLocalePatterns()
        {
            return this.supportedLocalePatterns.toArray( new String[0] );
        }
    }
}
