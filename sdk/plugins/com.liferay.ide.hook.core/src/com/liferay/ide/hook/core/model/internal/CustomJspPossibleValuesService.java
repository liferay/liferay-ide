/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.hook.core.model.internal;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.hook.core.model.CustomJspDir;
import com.liferay.ide.hook.core.model.Hook;

import java.io.File;
import java.io.FileFilter;
import java.util.SortedSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status.Severity;
import org.eclipse.sapphire.services.PossibleValuesService;

/**
 * @author Gregory Amerson
 */
public class CustomJspPossibleValuesService extends PossibleValuesService
{

    final FileFilter jspfilter = new FileFilter()
    {

        public boolean accept( File pathname )
        {
            return pathname.isDirectory() || pathname.getName().endsWith( ".jsp" );
        }
    };

    private IPath portalDir;

    @Override
    protected void fillPossibleValues( SortedSet<String> values )
    {
        final File portalDirFile = portalDir.toFile();
        final File htmlDirFile = new File( portalDirFile, "html" );

        if( htmlDirFile.exists() )
        {
            findJSPFiles( new Path( portalDirFile.getAbsolutePath() ), new File[] { htmlDirFile }, values );
        }
        else
        {
            File[] files = portalDirFile.listFiles( jspfilter );
            findJSPFiles( new Path( portalDirFile.getAbsolutePath() ), files, values );
        }
    }

    private void findJSPFiles( Path portalDir, final File[] files, final SortedSet<String> values )
    {
        for( File file : files )
        {
            if( file.isDirectory() )
            {
                findJSPFiles( portalDir, file.listFiles( jspfilter ), values );
            }
            else
            {
                values.add( new Path( file.getAbsolutePath() ).makeRelativeTo( portalDir ).toPortableString() );
            }
        }
    }

    private IFolder getCustomJspFolder()
    {
        CustomJspDir element = this.hook().getCustomJspDir().element();
        IFolder docroot = CoreUtil.getDocroot( project() );

        if( element != null && docroot != null )
        {
            Path customJspDir = element.getValue().getContent();
            IFolder customJspFolder = docroot.getFolder( customJspDir.toPortableString() );
            return customJspFolder;
        }
        else
        {
            return null;
        }
    }

    @Override
    public Severity getInvalidValueSeverity( String invalidValue )
    {
        IFolder customJspFolder = getCustomJspFolder();

        if( customJspFolder != null )
        {
            IFile invalidFile = customJspFolder.getFile( invalidValue );

            if( invalidFile.exists() )
            {
                return Severity.OK;
            }
        }

        return Severity.ERROR;
    }

    private Hook hook()
    {
        return this.context().find( Hook.class );
    }

    @Override
    protected void init()
    {
        super.init();
        IProject project = project();

        try
        {
            ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime( project );
            this.portalDir = liferayRuntime.getPortalDir();
        }
        catch( CoreException e )
        {
        }
    }

    protected IProject project()
    {
        return context( IModelElement.class ).root().adapt( IFile.class ).getProject();
    }

}
