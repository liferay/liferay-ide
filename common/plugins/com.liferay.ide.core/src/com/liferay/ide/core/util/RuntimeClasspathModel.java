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

import com.liferay.ide.core.LiferayCore;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.launching.VariableClasspathEntry;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry2;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class RuntimeClasspathModel
{
//    public static final int BOOTSTRAP= 0;
    public static final int USER= 1;

    private List<Object> userEntries = new ArrayList<Object>();
//    private List<Object> bootstrapEntries = new ArrayList<Object>();
//    private IJavaProject project;
    private ILaunchConfigurationWorkingCopy config;


    public RuntimeClasspathModel( ILaunchConfigurationWorkingCopy config )
    {
        this.config = config;
    }

    public void addEntry( int type, IRuntimeClasspathEntry entry )
    {
//        if( type == BOOTSTRAP )
//        {
//            bootstrapEntries.add( entry );
//        }

        if( type == USER )
        {
            userEntries.add( entry );
        }
    }
    public IClasspathEntry[] getEntries( int type, ILaunchConfiguration config )
    {
        List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();

//        if( type == BOOTSTRAP )
//        {
//            for( Object entry : bootstrapEntries )
//            {
//                System.out.println(entry);
//            }
//        }
        /*else*/ if ( type == USER )
        {

            for( Object entry : userEntries )
            {
                if( entry instanceof VariableClasspathEntry )
                {
                    VariableClasspathEntry runtimeClasspathEntry = (VariableClasspathEntry) entry;

                    IClasspathEntry newEntry = JavaCore.newLibraryEntry( new Path( runtimeClasspathEntry.getVariableString() ), null, null );

                    entries.add( newEntry );
                }
                else if ( entry instanceof IRuntimeClasspathEntry2 )
                {
                    IRuntimeClasspathEntry2 entry2 = (IRuntimeClasspathEntry2) entry;

                    try
                    {
                        IRuntimeClasspathEntry[] runtimeEntries = entry2.getRuntimeClasspathEntries( config );



                        for( IRuntimeClasspathEntry e : runtimeEntries )
                        {
                            IClasspathEntry newEntry = null;

                            if( e.getType() == IRuntimeClasspathEntry.VARIABLE )
                            {
                                newEntry = JavaCore.newLibraryEntry( e.getPath(), null, null );
                            }
                            else if ( e.getType() == IRuntimeClasspathEntry.ARCHIVE )
                            {
                                newEntry = JavaCore.newLibraryEntry( e.getPath(), null, null );
                            }
                            else if ( e instanceof VariableClasspathEntry )
                            {
                                VariableClasspathEntry vce = (VariableClasspathEntry) e;
                                newEntry = JavaCore.newLibraryEntry( new Path( vce.getVariableString() ), null, null);
                            }

                            if( newEntry != null )
                            {
                                entries.add( newEntry );
                            }
                        }
                    }
                    catch( CoreException e )
                    {
                        LiferayCore.logError( "error creating runtime classpath entry", e ); //$NON-NLS-1$
                    }
                }
                else if ( entry instanceof IRuntimeClasspathEntry )
                {
                    IRuntimeClasspathEntry rEntry = (IRuntimeClasspathEntry) entry;
                    IClasspathEntry newEntry = null;

                    if( rEntry.getType() == IRuntimeClasspathEntry.VARIABLE )
                    {
                        newEntry = JavaCore.newVariableEntry( rEntry.getPath(), null, null );
                    }
                    else if ( rEntry.getType() == IRuntimeClasspathEntry.ARCHIVE )
                    {
                        newEntry = JavaCore.newLibraryEntry( rEntry.getPath(), null, null );
                    }
                    else
                    {

                        System.out.println(rEntry.getType());
                    }

                    if( newEntry != null )
                    {
                        entries.add( newEntry );
                    }

                }
                else
                {
                    System.out.println(entry);
                }
            }
        }

        return entries.toArray( new IClasspathEntry[0] );
    }

    public ILaunchConfiguration getConfig()
    {
        return this.config;
    }
}
