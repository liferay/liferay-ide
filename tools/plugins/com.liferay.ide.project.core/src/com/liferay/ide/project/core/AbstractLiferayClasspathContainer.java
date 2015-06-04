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

package com.liferay.ide.project.core;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.core.LiferayServerCore;
import com.liferay.ide.server.util.ComponentUtil;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.common.jdt.internal.classpath.ClasspathDecorations;
import org.eclipse.jst.common.jdt.internal.classpath.ClasspathDecorationsManager;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public abstract class AbstractLiferayClasspathContainer implements IClasspathContainer
{
    protected static final Collection<String> portalSourceJars = Arrays.asList
    (
        "util-bridges.jar", //$NON-NLS-1$
        "util-java.jar", //$NON-NLS-1$
        "util-taglib.jar", //$NON-NLS-1$
        "portal-impl.jar" //$NON-NLS-1$
    );

    protected static ClasspathDecorationsManager cpDecorations;

    protected static final String SEPARATOR = "!"; //$NON-NLS-1$

    static
    {
        cpDecorations = new ClasspathDecorationsManager( LiferayServerCore.PLUGIN_ID );
    }

    public static String getDecorationManagerKey( IProject project, String container )
    {
        return project.getName() + SEPARATOR + container;
    }

    static ClasspathDecorationsManager getDecorationsManager()
    {
        return cpDecorations;
    }

    protected IClasspathEntry[] classpathEntries;

    protected IJavaProject javaProject;

    protected IPath path;

    protected IPath pluginPackageFilePath;

    protected IPath portalDir;

    protected String javadocURL;

    protected IPath sourceLocation;

    public AbstractLiferayClasspathContainer(
        IPath containerPath, IJavaProject project, IPath portalDir, String javadocURL, IPath sourceURL )
    {
        this.path = containerPath;
        this.javaProject = project;
        this.portalDir = portalDir;
        this.javadocURL = javadocURL;
        this.sourceLocation = sourceURL;
    }

 
    
    public IClasspathEntry[] getClasspathEntries()
    {
        if( this.classpathEntries == null )
        {
            List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();

            if( this.portalDir != null )
            {
                for( String pluginJar : getPortalJars() )
                {
                    entries.add( createPortalJarClasspathEntry( pluginJar ) );
                }

                for( String pluginPackageJar : getPortalDependencyJars() )
                {
                    entries.add( createPortalJarClasspathEntry( pluginPackageJar ) );
                }
            }

            this.classpathEntries = entries.toArray( new IClasspathEntry[entries.size()] );
        }

        return this.classpathEntries;
    }

    public abstract String getDescription();

    public int getKind()
    {
        return K_APPLICATION;
    }

    public IPath getPath()
    {
        return this.path;
    }

    public IPath getPortalDir()
    {
        return portalDir;
    }

    public String getJavadocURL()
    {
        return javadocURL;
    }

    public IPath getSourceLocation()
    {
        return sourceLocation;
    }

    protected IClasspathEntry createClasspathEntry( IPath entryPath, IPath sourcePath )
    {
        return createClasspathEntry( entryPath, sourcePath, null );
    }

    protected IClasspathEntry createClasspathEntry( IPath entryPath, IPath sourceLocation, String javadocURL )
    {
        IPath sourceRootPath = null;
        IPath sourcePath = null;
        IAccessRule[] rules = new IAccessRule[] {};
        IClasspathAttribute[] attrs = new IClasspathAttribute[] {};

        final ClasspathDecorations dec =
            cpDecorations.getDecorations(
                getDecorationManagerKey( javaProject.getProject(), getPath().toString() ), entryPath.toString() );

        if( dec != null )
        {
            sourcePath = dec.getSourceAttachmentPath();
            sourceRootPath = dec.getSourceAttachmentRootPath();
            attrs = dec.getExtraAttributes();
        }

        if( javadocURL != null )
        {
            if( CoreUtil.empty( attrs ) )
            {
                attrs = new IClasspathAttribute[] { newJavadocAttr( javadocURL ) };
            }
            else
            {
                List<IClasspathAttribute> newAttrs = new ArrayList<IClasspathAttribute>();

                for( IClasspathAttribute attr : attrs )
                {
                    if( IClasspathAttribute.JAVADOC_LOCATION_ATTRIBUTE_NAME.equals( attr.getName() ) )
                    {
                        newAttrs.add( newJavadocAttr( javadocURL ) );
                    }
                    else
                    {
                        newAttrs.add( attr );
                    }
                }

                attrs = newAttrs.toArray( new IClasspathAttribute[0] );
            }
        }

        if( sourcePath == null && sourceLocation != null )
        {
            sourcePath = sourceLocation;
        }

        return JavaCore.newLibraryEntry( entryPath, sourcePath, sourceRootPath, rules, attrs, false );
    }

    protected IClasspathAttribute newJavadocAttr( String javadocURL )
    {
        return JavaCore.newClasspathAttribute( IClasspathAttribute.JAVADOC_LOCATION_ATTRIBUTE_NAME, javadocURL );
    }

    protected IClasspathEntry createContextClasspathEntry( String context )
    {
        IClasspathEntry entry = null;

        IFile serviceJar = ComponentUtil.findServiceJarForContext( context );

        if( serviceJar.exists() )
        {
            final IWebProject webproject = LiferayCore.create( IWebProject.class, serviceJar.getProject() );

            // IDE-110 IDE-648
            if( webproject != null && webproject.getDefaultDocrootFolder() != null )
            {
                IFolder defaultDocroot = webproject.getDefaultDocrootFolder();
                IFolder serviceFolder = defaultDocroot.getFolder( new Path( "WEB-INF/service") ); //$NON-NLS-1$

                if( serviceFolder.exists() )
                {
                    entry = createClasspathEntry( serviceJar.getLocation(), serviceFolder.getLocation() );
                }
            }

            if( entry == null )
            {
                entry = createClasspathEntry( serviceJar.getLocation(), null );
            }
        }

        //TODO IDE-657 IDE-110
        if( entry == null )
        {
            IProject project = this.javaProject.getProject();

            SDK sdk = SDKUtil.getSDK( project );

            IPath sdkLocation = sdk.getLocation();

            String type =
                ProjectUtil.isPortletProject( project ) ? "portlets" : ProjectUtil.isHookProject( project ) //$NON-NLS-1$
                    ? "hooks" : ProjectUtil.isExtProject( project ) ? "ext" : StringPool.EMPTY; //$NON-NLS-1$ //$NON-NLS-2$

            IPath serviceJarPath =
                sdkLocation.append( type ).append( context ).append(
                    ISDKConstants.DEFAULT_DOCROOT_FOLDER + "/WEB-INF/lib" ).append( context + "-service.jar" ); //$NON-NLS-1$ //$NON-NLS-2$

            if( serviceJarPath.toFile().exists() )
            {
                IPath servicePath = serviceJarPath.removeLastSegments( 2 ).append( "service" ); //$NON-NLS-1$

                entry = createClasspathEntry( serviceJarPath, servicePath.toFile().exists() ? servicePath : null );
            }
        }

        return entry;
    }

    protected IClasspathEntry createPortalJarClasspathEntry( String portalJar )
    {
        IPath entryPath = this.portalDir.append( "/WEB-INF/lib/" + portalJar ); //$NON-NLS-1$

        IPath sourcePath = null;

        if( portalSourceJars.contains( portalJar ) )
        {
            sourcePath = getSourceLocation();
        }

        return createClasspathEntry( entryPath, sourcePath, this.javadocURL );
    }

    protected IClasspathEntry findSuggestedEntry( IPath jarPath, IClasspathEntry[] suggestedEntries )
    {
        // compare jarPath to an existing entry
        if( jarPath != null && ( !CoreUtil.isNullOrEmpty( jarPath.toString() ) ) &&
            ( !CoreUtil.isNullOrEmpty( suggestedEntries ) ) )
        {
            int matchLength = jarPath.segmentCount();

            for( IClasspathEntry suggestedEntry : suggestedEntries )
            {
                IPath suggestedPath = suggestedEntry.getPath();
                IPath pathToMatch =
                    suggestedPath.removeFirstSegments( suggestedPath.segmentCount() - matchLength ).setDevice( null ).makeAbsolute();
                if( jarPath.equals( pathToMatch ) )
                {
                    return suggestedEntry;
                }
            }
        }

        return null;
    }

    protected IFile getPluginPackageFile()
    {
        IFile retval = null;

        if( pluginPackageFilePath == null )
        {
            retval = lookupPluginPackageFile();

            if( retval != null && retval.exists() )
            {
                pluginPackageFilePath = retval.getFullPath();

                return retval;
            }
        }
        else
        {
            retval = ResourcesPlugin.getWorkspace().getRoot().getFile( pluginPackageFilePath );

            if( !retval.exists() )
            {
                pluginPackageFilePath = null;

                retval = lookupPluginPackageFile();
            }
        }

        return retval;
    }

    protected String[] getPortalDependencyJars()
    {
        String[] jars = new String[0];

        IFile pluginPackageFile = getPluginPackageFile();

        try
        {
            String deps = getPropertyValue( "portal-dependency-jars", pluginPackageFile ); //$NON-NLS-1$

            String[] split = deps.split( StringPool.COMMA );

            if( split.length > 0 && !( CoreUtil.isNullOrEmpty( split[0] ) ) )
            {
                for( int i = 0; i < split.length; i++ )
                {
                    split[i] = split[i].trim();
                }

                return split;
            }
        }
        catch( Exception e )
        {
        }

        return jars;
    }

    protected abstract String[] getPortalJars();

    protected String getPropertyValue( String key, IFile propertiesFile )
    {
        String retval = null;

        InputStream contents = null;

        try
        {
            Properties props = new Properties();
            contents = getPluginPackageFile().getContents();
            props.load( contents );

            retval = props.getProperty( key, StringPool.EMPTY );
        }
        catch( Exception e )
        {
        }
        finally
        {
            if( contents != null )
            {
                try
                {
                    contents.close();
                }
                catch( Exception e )
                {
                    // best effort
                }
            }
        }

        return retval;
    }

    protected String[] getRequiredDeploymentContexts()
    {
        String[] jars = new String[0];

        IFile pluginPackageFile = getPluginPackageFile();

        try
        {
            String context = getPropertyValue( "required-deployment-contexts", pluginPackageFile ); //$NON-NLS-1$

            String[] split = context.split( StringPool.COMMA );

            if( split.length > 0 && !( CoreUtil.isNullOrEmpty( split[0] ) ) )
            {
                return split;
            }
        }
        catch( Exception e )
        {
        }

        return jars;
    }

    protected IFile lookupPluginPackageFile()
    {
        IFile pluginPackageFile = null;

        IVirtualComponent comp = ComponentCore.createComponent( this.javaProject.getProject() );

        if( comp != null )
        {
            IContainer resource = comp.getRootFolder().getUnderlyingFolder();

            if( resource instanceof IFolder )
            {
                IFolder webroot = (IFolder) resource;

                pluginPackageFile =
                    webroot.getFile( "WEB-INF/" + ILiferayConstants.LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE ); //$NON-NLS-1$

                if( !pluginPackageFile.exists() )
                {
                    // IDE-226 the file may be missing because we are in an ext plugin which has a different layout
                    // check for ext-web in the path to the docroot

                    if( webroot.getFullPath().toPortableString().endsWith( "WEB-INF/ext-web/docroot" ) ) //$NON-NLS-1$
                    {
                        // look for packages file in first docroot
                        IPath parentDocroot = webroot.getFullPath().removeFirstSegments( 1 ).removeLastSegments( 3 );
                        IFolder parentWebroot = this.javaProject.getProject().getFolder( parentDocroot );

                        if( parentWebroot.exists() )
                        {
                            pluginPackageFile =
                                parentWebroot.getFile( "WEB-INF/" + //$NON-NLS-1$
                                    ILiferayConstants.LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE );
                        }
                    }
                }
            }
        }

        return pluginPackageFile;
    }

}
