/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.hook.ui.wizard;

import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.ui.viewsupport.FilteredElementTreeSelectionDialog;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class PropertiesFilteredDialog extends FilteredElementTreeSelectionDialog
{

    public static class PropertiesContentProvider implements ITreeContentProvider
    {
        protected String[] properties;

        public void dispose()
        {
        }

        public Object[] getChildren( Object parentElement )
        {
            return null;
        }

        public Object[] getElements( Object inputElement )
        {
            if( properties == null && inputElement instanceof String[] )
            {
                properties = (String[]) inputElement;
            }

            return properties;
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
            this.properties = null;
        }

    }

    public static class PropertiesLabelProvider extends LabelProvider
    {
    }

    public class PropertyFilter extends ViewerFilter
    {
        protected String fixedPattern;

        public PropertyFilter( String fixedPattern )
        {
            this.fixedPattern = fixedPattern;
        }

        @Override
        public boolean select( Viewer viewer, Object parentElement, Object element )
        {
            return fixedPattern == null ? true : element != null && element.toString().matches( fixedPattern );
        }

    }

    public class PropertySelectionValidator implements ISelectionStatusValidator
    {

        public PropertySelectionValidator( boolean multiSelect )
        {
        }

        public IStatus validate( Object[] selection )
        {
            return Status.OK_STATUS;
        }

    }

    public class PropertyViewerComparator extends ViewerComparator
    {
    }

    protected static final String DIALOG_SETTINGS = "com.liferay.ide.hook.ui.wizard.PropertiesFilteredDialog"; //$NON-NLS-1$

    protected File portalDir;

    public PropertiesFilteredDialog( Shell shell )
    {
        this( shell, null );
    }

    public PropertiesFilteredDialog( Shell shell, String fixedPattern )
    {
        super( shell, new PropertiesLabelProvider(), new PropertiesContentProvider() );

        setAllowMultiple( false );

        setComparator( new PropertyViewerComparator() );

        addFilter( new PropertyFilter( fixedPattern ) );

        setValidator( new PropertySelectionValidator( false ) );

        setHelpAvailable( false );
    }

}
