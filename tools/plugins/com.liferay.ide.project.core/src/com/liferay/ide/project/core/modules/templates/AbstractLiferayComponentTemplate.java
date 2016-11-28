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

package com.liferay.ide.project.core.modules.templates;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.IProjectBuilder;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.modules.IComponentTemplate;
import com.liferay.ide.project.core.modules.NewLiferayComponentOp;
import com.liferay.ide.project.core.modules.PropertyKey;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.java.JavaPackageName;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
/**
 * @author Simon Jiang
 */

@SuppressWarnings( "rawtypes" )
public abstract class AbstractLiferayComponentTemplate implements IComponentTemplate<NewLiferayComponentOp>, Comparable<IComponentTemplate>
{
    protected String displayName;
    protected String shortName;

    protected final static String TEMPLATE_DIR = "com/liferay/ide/project/core/modules/templates";

    protected File[] bndTemplateFiles;

    protected Configuration cfg = new Configuration();

    protected String componentClassName;

    protected File[] dependenciesTemplateFiles;
    protected ILiferayProject liferayProject;
    protected JavaPackageName packageName;
    protected IProject project;
    protected String projectName;
    protected List<String> properties = new ArrayList<String>();

    protected String serviceName;
    protected File[] sourceTemplateFiles;
    protected String templateName;
    protected String componentNameWithoutTemplateName;
    protected String modelClass;
    protected String simpleModelClass;

    private final static String[][] DEPENDENCY=
    {
        { "com.liferay.portal", "com.liferay.portal.kernel", "2.0.0"},
        { "org.osgi", "org.osgi.service.component.annotations", "1.3.0"}
    };

    public AbstractLiferayComponentTemplate()
    {
        super();
    }

    public void setDisplayName( final String displayName )
    {
        this.displayName = displayName;
    }

    public void setShortName( final String shortName )
    {
        this.shortName = shortName;
    }

    @Override
    public String getDisplayName()
    {
        return this.displayName;
    }

    @Override
    public String getShortName()
    {
        return this.shortName;
    }

    public int compareTo( IComponentTemplate componentTemplate )
    {
        if( componentTemplate != null )
        {
            return this.displayName.compareTo( componentTemplate.getDisplayName() );
        }

        return 0;
    }

    protected void createFile( IFile newFile, final byte[] input ) throws CoreException
    {
        if( newFile.getParent() instanceof IFolder )
        {
            CoreUtil.prepareFolder( (IFolder) newFile.getParent() );
        }

        newFile.create( new ByteArrayInputStream( input ), true, null );
    }

    protected void createFileInResouceFolder( IFolder sourceFolder, String filePath, File resourceFile )
        throws CoreException
    {
        final IFile projectFile = getProjectFile( sourceFolder, filePath );

        if( !projectFile.exists() )
        {
            String readContents = FileUtil.readContents( resourceFile, true );
            createFile( projectFile, readContents.getBytes() );
        }
    }

    protected final IPackageFragment createJavaPackage( IJavaProject javaProject, final String packageName )
    {
        IPackageFragmentRoot packRoot = getSourceFolder( javaProject );

        if( packRoot == null )
        {
            //IFolder resourceFolder = liferayProject.getSourceFolder( "src/main/java" );
            //packRoot = javaProject.getPackageFragmentRoot(resourceFolder);
            //return packRoot.getPackageFragment( packageName );
            return null;
        }

        IPackageFragment pack = packRoot.getPackageFragment( packageName );

        if( pack == null )
        {
            pack = packRoot.getPackageFragment( "" );
        }

        if( !pack.exists() )
        {
            String packName = pack.getElementName();
            try
            {
                pack = packRoot.createPackageFragment( packName, true, null );
            }
            catch( CoreException e )
            {
                ProjectCore.logError( e );
            }

        }

        return pack;
    }

    protected void createResorcesFolder( IProject project ) throws CoreException
    {
        IFolder resourceFolder = liferayProject.getSourceFolder( "resources" );

        if( resourceFolder == null || !resourceFolder.exists() )
        {
            IJavaProject javaProject = JavaCore.create( project );

            List<IClasspathEntry> existingRawClasspath = Arrays.asList( javaProject.getRawClasspath() );
            List<IClasspathEntry> newRawClasspath = new ArrayList<IClasspathEntry>();

            IClasspathAttribute[] attributes =
                new IClasspathAttribute[] { JavaCore.newClasspathAttribute( "FROM_GRADLE_MODEL", "true" ) }; //$NON-NLS-1$ //$NON-NLS-2$

            IClasspathEntry resourcesEntry = JavaCore.newSourceEntry(
                project.getFullPath().append( "src/main/resources" ), new IPath[0], new IPath[0], null, attributes );

            newRawClasspath.add( resourcesEntry );

            for( IClasspathEntry entry : existingRawClasspath )
            {
                newRawClasspath.add( entry );
            }

            javaProject.setRawClasspath( newRawClasspath.toArray( new IClasspathEntry[0] ), new NullProgressMonitor() );

            project.refreshLocal( IResource.DEPTH_INFINITE, new NullProgressMonitor() );
        }
    }

    public void createSampleFile( IFile newFile, final String srcFileName ) throws CoreException
    {
        try
        {
            URL sampleFileURL = getClass().getClassLoader().getResource( TEMPLATE_DIR + "/" + srcFileName );
            final String sampleContent =
                FileUtil.readContents( new File( FileLocator.toFileURL( sampleFileURL ).getFile() ), true );
            if( newFile.getParent() instanceof IFolder )
            {
                CoreUtil.prepareFolder( (IFolder) newFile.getParent() );
            }
            newFile.create( new ByteArrayInputStream( sampleContent.getBytes() ), true, null );

        }
        catch( IOException e )
        {
            throw new CoreException( ProjectCore.createErrorStatus( e ) );
        }
    }

    public void createSampleFile( IFile newFile, final String srcFileName, final String oldReplaceConent, final String  newReplaceContent) throws CoreException
    {
        try
        {
            URL sampleFileURL = getClass().getClassLoader().getResource( TEMPLATE_DIR + "/" + srcFileName );
            final String sampleContent =
                FileUtil.readContents( new File( FileLocator.toFileURL( sampleFileURL ).getFile() ), true );
            final String newCoentent = sampleContent.replace( oldReplaceConent, newReplaceContent );
            if( newFile.getParent() instanceof IFolder )
            {
                CoreUtil.prepareFolder( (IFolder) newFile.getParent() );
            }
            newFile.create( new ByteArrayInputStream( newCoentent.getBytes() ), true, null );

        }
        catch( IOException e )
        {
            throw new CoreException( ProjectCore.createErrorStatus( e ) );
        }
    }

    protected void initializeOperation( final NewLiferayComponentOp op )
    {
        this.projectName = op.getProjectName().content( true );
        this.packageName = op.getPackageName().content( true );
        this.componentClassName = op.getComponentClassName().content( true );
        this.templateName = op.getComponentClassTemplateName().content( true ).getShortName();
        this.serviceName = op.getServiceName().content( true );
        this.modelClass = op.getModelClass().content( true );

        this.componentNameWithoutTemplateName = componentClassName.replace( templateName, "" );

        if( modelClass != null )
        {
            int modeClassPos = modelClass.lastIndexOf( "." );

            simpleModelClass = modelClass.substring( modeClassPos + 1 );
        }

        ElementList<PropertyKey> propertyKeys = op.getPropertyKeys();

        for( int i = 0; i < propertyKeys.size(); i++ )
        {
            PropertyKey propertyKey = propertyKeys.get( i );

            this.properties.add(
                propertyKey.getName().content( true ) + "=" + propertyKey.getValue().content( true ) +
                    ( i != ( propertyKeys.size() - 1 ) ? "," : "" ) );
        }

    }

    public void doExecute( NewLiferayComponentOp op, IProgressMonitor monitor ) throws CoreException
    {
        try
        {
            initializeOperation( op );
            this.project = CoreUtil.getProject( projectName );

            if( project != null )
            {
                liferayProject = LiferayCore.create( project );

                if( liferayProject != null )
                {
                    initFreeMarker();

                    IFile srcFile = prepareClassFile( this.componentClassName );
                    doSourceCodeOperation( srcFile );

                    doNewPropertiesOperation();

                    doMergeResourcesOperation();

                    doMergeBndOperation();

                    doMergeDependencyOperation();

                    project.refreshLocal( IResource.DEPTH_INFINITE, new NullProgressMonitor() );
                }
            }
        }
        catch( Exception e )
        {
            throw new CoreException( ProjectCore.createErrorStatus( e ) );
        }
    }

    protected void initBndProperties( File bndFile, BndProperties bndProperty)
    {
        try
        {
            bndProperty.load( bndFile );
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }
    }

    protected void setBndProperties( BndProperties bndProperty )
    {
    }

    protected void doMergeBndOperation() throws CoreException
    {
        BndProperties bndProperty = new BndProperties();
        IFile iBndFile = project.getFile( "bnd.bnd" );

        if ( iBndFile.exists() )
        {
            File bndFile = iBndFile.getLocation().toFile();

            initBndProperties( bndFile, bndProperty );

            try( OutputStream out = new FileOutputStream( bndFile ) )
            {
                setBndProperties( bndProperty );
                bndProperty.store( out, null );
            }
            catch( Exception e )
            {
                ProjectCore.logError( e );
            }
        }
    }

    protected void doMergeResourcesOperation() throws CoreException
    {
    }

    protected void doNewPropertiesOperation() throws CoreException
    {
    }

    protected List<String[]> getComponentDependency() throws CoreException
    {
        List<String[]> dependencyList = new ArrayList<String[]>();

        for( String[] dependency : DEPENDENCY)
        {
            dependencyList.add( dependency );
        }

        return dependencyList;
    }

    protected void doMergeDependencyOperation() throws CoreException
    {
        final IProjectBuilder builder = liferayProject.adapt( IProjectBuilder.class );

        builder.updateProjectDependency( project, getComponentDependency() );
    }

    protected void doSourceCodeOperation( IFile srcFile ) throws CoreException
    {
        try(OutputStream fos = new FileOutputStream( srcFile.getLocation().toFile() ))
        {
            Template temp = cfg.getTemplate( getTemplateFile() );

            Map<String, Object> root = getTemplateMap();

            Writer out = new OutputStreamWriter( fos );
            temp.process( root, out );
            fos.flush();
        }
        catch( IOException | TemplateException e )
        {
            throw new CoreException( ProjectCore.createErrorStatus( e ) );
        }
    }

    protected String getExtensionClass()
    {
        return null;
    }

    protected List<String> getImports()
    {
        List<String> imports = new ArrayList<String>();

        imports.add( "org.osgi.service.component.annotations.Component" );

        return imports;
    }

    protected IProject getProject()
    {
        return CoreUtil.getProject( projectName );
    }

    protected IFile getProjectFile( IFolder sourceFolder, String filePath )
    {
        IFile retval = null;

        if( sourceFolder != null )
        {
            retval = sourceFolder.getFile( new Path( filePath ) );
        }

        return retval;
    }

    protected List<String> getProperties()
    {
        return properties;
    }

    protected IPackageFragmentRoot getSourceFolder( IJavaProject javaProject )
    {
        try
        {
            for( IPackageFragmentRoot root : javaProject.getPackageFragmentRoots() )
            {
                if( root.getKind() == IPackageFragmentRoot.K_SOURCE )
                {
                    return root;
                }
            }
        }
        catch( Exception e )
        {
            ProjectCore.logError( e );
        }
        return null;
    }

    protected String getSuperClass()
    {
        return null;
    }

    protected abstract String getTemplateFile();

    protected Map<String, Object> getTemplateMap()
    {
        Map<String, Object> root = new HashMap<String, Object>();

        root.put( "importlibs", getImports() );
        root.put( "properties", getProperties() );
        root.put( "packagename", packageName );
        root.put( "classname", componentClassName );
        root.put( "projectname", projectName );
        root.put( "supperclass", getSuperClass() );
        root.put( "extensionclass", getExtensionClass() );
        root.put( "simplemodelclass", simpleModelClass );
        root.put( "componentNameWithoutTemplateName", componentNameWithoutTemplateName );
        root.put( "componentfolder", componentClassName.toLowerCase() );

        return root;
    }

    protected void initFreeMarker() throws CoreException
    {
        try
        {
            URL templateURL = getClass().getClassLoader().getResource( TEMPLATE_DIR );
            cfg.setDirectoryForTemplateLoading( new File( FileLocator.toFileURL( templateURL ).getFile() ) );

            cfg.setDefaultEncoding( "UTF-8" );
            cfg.setTemplateExceptionHandler( TemplateExceptionHandler.RETHROW_HANDLER );

        }
        catch( IOException e )
        {
            throw new CoreException( ProjectCore.createErrorStatus( e ) );
        }
    }

    protected IFile prepareClassFile( final String className ) throws CoreException
    {
        IFile file = null;
        try
        {
            IFolder sourceFolder = liferayProject.getSourceFolder( "java" );
            IJavaProject javaProject = JavaCore.create( project );

            if( packageName != null )
            {
                IPackageFragment pack = createJavaPackage( javaProject, packageName.toString() );

                if( pack == null )
                {
                    throw new CoreException( ProjectCore.createErrorStatus( "Can't create package folder" ) );
                }

                String fileName = className + ".java";
                IPath packageFullPath = new Path( packageName.toString().replace( '.', IPath.SEPARATOR ) );

                if( !packageFullPath.toFile().exists() )
                {
                    CoreUtil.prepareFolder( sourceFolder.getFolder( packageFullPath ) );
                }
                IPath javaFileFullPath = packageFullPath.append( fileName );
                file = sourceFolder.getFile( javaFileFullPath );
            }
        }
        catch( Exception e )
        {
            throw new CoreException( ProjectCore.createErrorStatus( e ) );
        }

        return file;
    }
}
