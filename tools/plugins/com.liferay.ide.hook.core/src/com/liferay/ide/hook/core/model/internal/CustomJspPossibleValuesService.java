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

package com.liferay.ide.hook.core.model.internal;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.hook.core.model.Hook;
import com.liferay.ide.hook.core.util.HookUtil;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.Element;
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
            return pathname.isDirectory() ||
                   pathname.getName().endsWith( ".jsp" ) || //$NON-NLS-1$
                   pathname.getName().endsWith( ".jspf" ); //$NON-NLS-1$
        }
    };

    private Path portalDir;
    private File[] possibleValues;


    @Override
    protected void fillPossibleValues( final Set<String> values )
    {
        if( possibleValues == null )
        {
            final IProject project = project();

            final ILiferayProject liferayProject = LiferayCore.create( project );

            if( liferayProject != null )
            {
                this.portalDir = new Path( liferayProject.getAppServerPortalDir().toPortableString() );
            }

            final File portalDirFile = portalDir.toFile();
            final File htmlDirFile = new File( portalDirFile, "html" ); //$NON-NLS-1$

            final List<File> fileValues = new LinkedList<File>();

            if( htmlDirFile.exists() )
            {
                findJSPFiles( new File[] { htmlDirFile }, fileValues );
            }
            else
            {
                final File[] files = portalDirFile.listFiles( jspfilter );
                findJSPFiles( files, fileValues );
            }

            this.possibleValues = fileValues.toArray( new File[0] );
        }

        if( possibleValues != null )
        {
            for( final File file : possibleValues )
            {
                values.add( new Path( file.getAbsolutePath() ).makeRelativeTo( portalDir ).toPortableString() );
            }
        }
    }

    private void findJSPFiles( final File[] files, final List<File> fileValues )
    {
        for( File file : files )
        {
            if( file.isDirectory() )
            {
                findJSPFiles( file.listFiles( jspfilter ), fileValues );
            }
            else
            {
                fileValues.add( file );
            }
        }
    }

    @Override
    public Severity getInvalidValueSeverity( String invalidValue )
    {
        IFolder customJspFolder = HookUtil.getCustomJspFolder( hook(), project() );

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

    protected IProject project()
    {
        return context( Element.class ).root().adapt( IFile.class ).getProject();
    }

}
