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
package com.liferay.ide.sdk.ui;

import com.liferay.ide.sdk.core.ISDKConstants;
import com.liferay.ide.sdk.core.SDKUtil;

import java.util.ArrayList;
import java.util.List;

import org.apache.ivyde.eclipse.cpcontainer.IvyClasspathContainer;
import org.apache.ivyde.eclipse.cpcontainer.IvyClasspathContainerConfAdapter;
import org.apache.ivyde.eclipse.cpcontainer.IvyClasspathContainerConfiguration;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.corext.refactoring.rename.RenameJavaProjectProcessor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;



/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class SDKProjectRenameParticipant extends RenameParticipant
{

    private IJavaProject project;
    private String oldName;

    public SDKProjectRenameParticipant()
    {
        super();
    }

    @Override
    protected boolean initialize( Object element )
    {
        if( element instanceof IJavaProject )
        {
            this.project = (IJavaProject) element;

            this.oldName = this.project.getProject().getName();

            if( SDKUtil.isIvyProject( this.project.getProject() ) )
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getName()
    {
        return "SDK project rename"; //$NON-NLS-1$
    }

    @Override
    public RefactoringStatus checkConditions( IProgressMonitor pm, CheckConditionsContext context )
        throws OperationCanceledException
    {
        return new RefactoringStatus();
    }

    @Override
    public Change createChange( IProgressMonitor pm ) throws CoreException, OperationCanceledException
    {
        final String newName = this.getArguments().getNewName();

        return new Change()
        {
            @Override
            public String getName()
            {
                return "Update Ivy classpath entry"; //$NON-NLS-1$
            }

            @Override
            public void initializeValidationData( IProgressMonitor pm )
            {
                System.out.println();
            }

            @Override
            public RefactoringStatus isValid( IProgressMonitor pm ) throws CoreException,
                OperationCanceledException
            {
                return new RefactoringStatus();
            }

            @Override
            public Change perform( IProgressMonitor pm ) throws CoreException
            {
                final RenameJavaProjectProcessor rjpp = (RenameJavaProjectProcessor) getProcessor();
                final IJavaProject newJavaProject = (IJavaProject) rjpp.getNewElement();
                final IvyClasspathContainerConfiguration conf =
                    new IvyClasspathContainerConfiguration( newJavaProject, ISDKConstants.IVY_XML_FILE, true );

                IClasspathEntry oldEntry = null;

                for( IClasspathEntry cpEntry : newJavaProject.getRawClasspath() )
                {
                    if( cpEntry.getPath().segment( 0 ).equals( IvyClasspathContainer.CONTAINER_ID ) )
                    {
                        oldEntry = cpEntry;
                        break;
                    }
                }

                IvyClasspathContainerConfAdapter.load( conf, oldEntry.getPath(), oldEntry.getExtraAttributes() );


                final String oldIvySettingsPath = conf.getIvySettingsSetup().getRawIvySettingsPath();
                final String oldIvyUserDir = conf.getIvySettingsSetup().getRawIvyUserDir();

                conf.setProject( newJavaProject );
                conf.getIvySettingsSetup().setIvySettingsPath( oldIvySettingsPath.replaceAll( oldName, newName ) );
                conf.getIvySettingsSetup().setIvyUserDir( oldIvyUserDir.replaceAll( oldName, newName ) );

                List<IClasspathEntry> newEntries = new ArrayList<IClasspathEntry>();

                for( IClasspathEntry cpEntry : newJavaProject.getRawClasspath() )
                {
                    if( ! cpEntry.getPath().segment( 0 ).equals( IvyClasspathContainer.CONTAINER_ID ) )
                    {
                       newEntries.add( cpEntry );
                    }
                }

                IPath newIvyPath = IvyClasspathContainerConfAdapter.getPath( conf );

                newJavaProject.setRawClasspath( newEntries.toArray( new IClasspathEntry[0] ), pm );

                IClasspathEntry ivyEntry = JavaCore.newContainerEntry(newIvyPath, null, oldEntry.getExtraAttributes(), false);

                IvyClasspathContainer ivycp = new IvyClasspathContainer( newJavaProject, newIvyPath, new IClasspathEntry[0], new IClasspathAttribute[0] );
                JavaCore.setClasspathContainer( newIvyPath, new IJavaProject[] { newJavaProject }, new IClasspathContainer[] { ivycp }, pm );

                IClasspathEntry[] entries = newJavaProject.getRawClasspath();

                newEntries.add( ivyEntry );
                entries = (IClasspathEntry[]) newEntries.toArray( new IClasspathEntry[newEntries.size()] );
                newJavaProject.setRawClasspath( entries, newJavaProject.getOutputLocation(), pm );

                JavaCore.getClasspathContainerInitializer( IvyClasspathContainer.CONTAINER_ID ).requestClasspathContainerUpdate( newIvyPath, newJavaProject, ivycp );

                ivycp.launchResolve( false, pm );

                return null;
            }

            @Override
            public Object getModifiedElement()
            {
                return null;
            }
        };
    }

}
