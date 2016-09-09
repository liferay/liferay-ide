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

package com.liferay.ide.project.ui.upgrade.animated;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.core.util.SearchFilesVisitor;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.dialog.JavaProjectSelectionDialog;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * @author Joye Luo
 * @author Simon Jiang
 */
public class LiferayLayouttplUpgradeTableViewCustomPart extends AbstractLiferayTableViewCustomPart
{

    private class LayoutProjectViewerFilter extends ViewerFilter
    {

        @Override
        public boolean select( Viewer viewer, Object parentElement, Object element )
        {
            if( element instanceof IJavaProject )
            {
                IProject project = ( (IJavaProject) element ).getProject();

                if( project.getName().equals( "External Plug-in Libraries" ) )
                {
                    return false;
                }

                if( ProjectUtil.isLayoutTplProject( project ) )
                {
                    return true;
                }

                return false;
            }

            return false;
        }

    }

    private class LayoutSearchFilesVistor extends SearchFilesVisitor
    {

        @Override
        public boolean visit( IResourceProxy resourceProxy )
        {
            if( resourceProxy.getType() == IResource.FILE && resourceProxy.getName().endsWith( searchFileName ) )
            {
                IResource resource = resourceProxy.requestResource();

                if( resource.exists() )
                {
                    resources.add( (IFile) resource );
                }
            }

            return true;
        }
    }

    public LiferayLayouttplUpgradeTableViewCustomPart( Composite parent, int style )
    {
        super( parent, style );

    }

    @Override
    protected void createTempFile( final File srcFile, final File templateFile, final String projectName )
    {
        try
        {
            String content = upgradeLayouttplContent( FileUtil.readContents( srcFile, true ) );

            if( templateFile.exists() )
            {
                templateFile.delete();
            }

            templateFile.createNewFile();
            FileUtil.writeFile( templateFile, content, projectName );
        }
        catch( Exception e )
        {
            ProjectUI.logError( e );
        }
    }

    @Override
    protected void doUpgrade( File srcFile, IProject project )
    {
        try
        {
            String content = upgradeLayouttplContent( FileUtil.readContents( srcFile, true ) );
            FileUtils.writeStringToFile( srcFile, content, "UTF-8" );
        }
        catch( Exception e )
        {
            ProjectUI.logError( e );
        }
    }

    @Override
    protected IFile[] getAvaiableUpgradeFiles( IProject project )
    {
        List<IFile> files = new ArrayList<IFile>();

        List<IFile> searchFiles = new LayoutSearchFilesVistor().searchFiles( project, ".tpl" );
        files.addAll( searchFiles );

        return files.toArray( new IFile[files.size()] );
    }

    @Override
    protected IStyledLabelProvider getLableProvider()
    {
        return new LiferayUpgradeTabeViewLabelProvider( "Upgrade Layouttpl")
        {

            @Override
            public Image getImage( Object element )
            {
                return this.getImageRegistry().get( "layout" );
            }

            @Override
            protected void initalizeImageRegistry( ImageRegistry imageRegistry )
            {
                imageRegistry.put(
                    "layout", ProjectUI.imageDescriptorFromPlugin( ProjectUI.PLUGIN_ID, "/icons/e16/layout.png" ) );
            }
        };
    }

    @Override
    protected List<IProject> getSelectedProjects()
    {
        List<IProject> projects = new ArrayList<>();

        final JavaProjectSelectionDialog dialog =
            new JavaProjectSelectionDialog( Display.getCurrent().getActiveShell(), new LayoutProjectViewerFilter() );

            URL imageUrl = ProjectUI.getDefault().getBundle().getEntry( "/icons/e16/layout.png");
            Image layouttplImage = ImageDescriptor.createFromURL( imageUrl ).createImage();
            
            dialog.setImage( layouttplImage );
            dialog.setTitle( "Layout Template Project" );
            dialog.setMessage( "Select Layout Template Project" );
      
        if( dialog.open() == Window.OK )
        {
            final Object[] selectedProjects = dialog.getResult();

            if( selectedProjects != null )
            {
                for( Object project : selectedProjects )
                {
                    if( project instanceof IJavaProject )
                    {
                        IJavaProject p = (IJavaProject) project;
                        projects.add( p.getProject() );
                    }
                }
            }
        }

        return projects;
    }

    @Override
    protected boolean isNeedUpgrade( File srcFile )
    {
        final String content = FileUtil.readContents( srcFile );

        if( content != null && !content.equals( "" ) )
        {
            if( content.contains( "row-fluid" ) || content.contains( "span" ) )
            {
                return true;
            }
        }

        return false;
    }

    private String upgradeLayouttplContent( String content )
    {
        if( content != null && !content.equals( "" ) )
        {
            if( content.contains( "row-fluid" ) )
            {
                content = content.replaceAll( "row-fluid", "row" );
            }

            if( content.contains( "span" ) )
            {
                content = content.replaceAll( "span", "col-md-" );
            }
        }

        return content;
    }
}
