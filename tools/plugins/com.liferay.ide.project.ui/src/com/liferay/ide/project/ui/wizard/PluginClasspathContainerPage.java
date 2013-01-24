/******************************************************************************
 * Copyright (c) 2006 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/

package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.project.core.PluginClasspathContainerInitializer;
import com.liferay.ide.project.core.util.ProjectUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPage;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPageExtension;
import org.eclipse.jdt.ui.wizards.NewElementWizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Gregory Amerson
 */
public class PluginClasspathContainerPage extends NewElementWizardPage
    implements IClasspathContainerPage, IClasspathContainerPageExtension
{

    private IProject ownerProject;
    private String type;

    private Combo typeCombo;

    public PluginClasspathContainerPage()
    {
        super( "PluginClasspathContainerPage" ); //$NON-NLS-1$
        setTitle( Msgs.liferayPluginAPILibrary );
        setDescription( Msgs.containerManagesClasspathEntries );
    }

    public void createControl( Composite parent )
    {
        final Composite composite = new Composite( parent, SWT.NONE );
        composite.setLayout( new GridLayout( 2, false ) );

        final Label label = new Label( composite, SWT.NONE );
        label.setText( Msgs.liferayPluginTypeLabel );

        final String[] types = new String[] { "portlet", "hook", "ext" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        this.typeCombo = new Combo( composite, SWT.READ_ONLY );
        this.typeCombo.setItems( types );

        final int index;

        if( this.type != null )
        {
            index = indexOf( types, this.type );
        }
        else
        {
            if( ProjectUtil.isPortletProject( this.ownerProject ) )
            {
                index = 0;
            }
            else if( ProjectUtil.isHookProject( this.ownerProject ) )
            {
                index = 1;
            }
            else if( ProjectUtil.isExtProject( this.ownerProject ) )
            {
                index = 2;
            }
            else
            {
                index = -1;
            }
        }

        if( index != -1 )
        {
            this.typeCombo.select( index );
        }

        final GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.minimumWidth = 100;

        this.typeCombo.setLayoutData( gd );

        setControl( composite );
    }

    public boolean finish()
    {
        if( this.ownerProject != null && ProjectUtil.isLiferayFacetedProject( this.ownerProject ) )
        {
            return true;
        }
        else
        {
            setErrorMessage( Msgs.selectedProjectNotLiferayProject );
            return false;
        }
    }

    public IClasspathEntry getSelection()
    {
        IPath path = new Path( PluginClasspathContainerInitializer.ID + "/" ); //$NON-NLS-1$

        final int index = this.typeCombo.getSelectionIndex();

        if( index != -1 )
        {
            final String type = this.typeCombo.getItem( index );
            path = path.append( type );
        }

        return JavaCore.newContainerEntry( path );
    }

    public void initialize( IJavaProject project, IClasspathEntry[] currentEntries )
    {
        this.ownerProject = ( project == null ? null : project.getProject() );
    }

    public void setSelection( IClasspathEntry entry )
    {
        final IPath path = entry == null ? null : entry.getPath();

        if( path != null && path.segmentCount() == 2 )
        {
            this.type = path.segment( 1 );
        }

    }

    private static int indexOf( final String[] array, final String str )
    {
        for( int i = 0; i < array.length; i++ )
        {
            if( array[i].equals( str ) )
            {
                return i;
            }
        }

        return -1;
    }

    private static class Msgs extends NLS
    {
        public static String containerManagesClasspathEntries;
        public static String liferayPluginAPILibrary;
        public static String liferayPluginTypeLabel;
        public static String selectedProjectNotLiferayProject;

        static
        {
            initializeMessages( PluginClasspathContainerPage.class.getName(), Msgs.class );
        }
    }
}
