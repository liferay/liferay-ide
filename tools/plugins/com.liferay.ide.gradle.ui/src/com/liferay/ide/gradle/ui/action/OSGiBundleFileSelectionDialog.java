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

package com.liferay.ide.gradle.ui.action;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.gradle.core.modules.OverrideFilePath;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.sapphire.ElementList;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

/**
 * @author Terry Jia
 */
public class OSGiBundleFileSelectionDialog extends ElementTreeSelectionDialog
{

    private static ElementList<OverrideFilePath> files;

    protected static String projectName = "";

    protected static class FileContentProvider implements ITreeContentProvider
    {

        private final Object[] EMPTY = new Object[0];

        public void dispose()
        {
        }

        public Object[] getChildren( Object parentElement )
        {
            return EMPTY;
        }

        public Object[] getElements( Object element )
        {
            Set<String> possibleValues = new HashSet<String>();

            if( element instanceof File )
            {
                File file = (File) element;

                if( file.exists() )
                {
                    try( JarFile jar = new JarFile( file ) )
                    {
                        Enumeration<JarEntry> enu = jar.entries();

                        while( enu.hasMoreElements() )
                        {
                            JarEntry entry = enu.nextElement();
                            String name = entry.getName();

                            if( ( name.startsWith( "META-INF/resources/" ) &&
                                ( name.endsWith( ".jsp" ) || name.endsWith( ".jspf" ) ) ) ||
                                name.equals( "portlet.properties" ) )
                            {
                                possibleValues.add( name );
                            }
                        }
                    }
                    catch( IOException e )
                    {
                    }
                }
            }

            for( OverrideFilePath file : files )
            {
                String currentFile = file.getValue().content();

                possibleValues.remove( currentFile );
            }

            if( projectName != null )
            {
                IProject project = CoreUtil.getProject( projectName );
                IFolder javaFolder = project.getFolder( "src/main/java" );
                IFolder resourceFolder = project.getFolder( "src/main/resources" );
                Iterator<String> it = possibleValues.iterator();

                while( it.hasNext() )
                {
                    String v = it.next();

                    if( resourceFolder.getFile( v ).exists() )
                    {
                        it.remove();
                    }

                    if( javaFolder.getFile( "portlet-ext.properties" ).exists() && v.equals( "portlet.properties" ) )
                    {
                        it.remove();
                    }

                }
            }

            return possibleValues.toArray();
        }

        public Object getParent( Object element )
        {
            return null;
        }

        public boolean hasChildren( Object element )
        {
            return false;
        }

        public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
        {
        }
    }

    protected static class FileLabelProvider extends LabelProvider
    {
        private final Image IMG_FILE =
            PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJ_FILE );

        public Image getImage( Object element )
        {
            return IMG_FILE;
        }

        public String getText( Object element )
        {
            return element.toString();
        }
    }

    public OSGiBundleFileSelectionDialog( Shell parent, ElementList<OverrideFilePath> currentFiles, String projectName )
    {
        super( parent, new FileLabelProvider(), new FileContentProvider() );

        files = currentFiles;
        this.projectName = projectName;

        setComparator( new ViewerComparator() );
    }

}
