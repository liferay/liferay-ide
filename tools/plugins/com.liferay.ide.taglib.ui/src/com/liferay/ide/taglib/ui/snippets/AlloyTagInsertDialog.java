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

package com.liferay.ide.taglib.ui.snippets;

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.taglib.ui.model.Tag;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ui.def.DefinitionLoader.Reference;
import org.eclipse.sapphire.ui.def.DialogDef;
import org.eclipse.sapphire.ui.swt.SapphireDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.common.snippets.core.ISnippetItem;
import org.eclipse.wst.common.snippets.internal.util.StringUtils;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class AlloyTagInsertDialog extends SapphireDialog
{

    protected List<DisposeListener> disposeListeners = new ArrayList<DisposeListener>();
    protected ISnippetItem fItem = null;
    protected String fPreparedText = null;

    public AlloyTagInsertDialog( Shell host, Element model, Reference<DialogDef> reference, boolean clearModality )
    {
        super( host, model, reference );
        /**
         * Required to fix defect 218700, since Dialogs default to APPLICATION_MODAL.
         */
        if( clearModality )
        {
            setShellStyle( SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MODELESS );
        }
        else
        {
            setShellStyle( SWT.RESIZE | getShellStyle() );
        }
    }

    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );

        shell.setSize( 600, 730 );
    }

    public void addDisposeListener( DisposeListener listener )
    {
        if( !disposeListeners.contains( listener ) )
        {
            disposeListeners.add( listener );
        }
    }

    /**
     * @see org.eclipse.jface.window.Window#create()
     */
    public void create()
    {
        super.create();

        for( int i = 0; i < disposeListeners.size(); i++ )
        {
            getShell().addDisposeListener( disposeListeners.get( i ) );
        }

        getShell().setActive();
    }

    protected void prepareText()
    {
        // this could be horribly inefficient
        String text = ( element().adapt( Tag.class ) ).getSource().content();

        // remove all cursor markers
        text = StringUtils.replace( text, "${cursor}", StringPool.EMPTY ); //$NON-NLS-1$

        // Update EOLs (bug 80231)
        String systemEOL = System.getProperty( "line.separator" ); //$NON-NLS-1$
        text = StringUtils.replace( text, "\r\n", "\n" ); //$NON-NLS-1$ //$NON-NLS-2$
        text = StringUtils.replace( text, "\r", "\n" ); //$NON-NLS-1$ //$NON-NLS-2$
        if( !"\n".equals( systemEOL ) && systemEOL != null ) { //$NON-NLS-1$
            text = StringUtils.replace( text, "\n", systemEOL ); //$NON-NLS-1$
        }

        setPreparedText( text );
    }

    /**
     * Gets the item.
     *
     * @return Returns a ISnippetItem
     */
    public ISnippetItem getItem()
    {
        return fItem;
    }

    /**
     * Gets the preparedText.
     *
     * @return Returns a String
     */
    public String getPreparedText()
    {
        if( fPreparedText == null )
            prepareText();
        return fPreparedText;
    }

    public void removeDisposeListener( DisposeListener listener )
    {
        disposeListeners.remove( listener );
    }

    /**
     * Sets the item.
     *
     * @param item
     *            The item to set
     */
    public void setItem( ISnippetItem item )
    {
        fItem = item;
    }

    /**
     * Sets the preparedText.
     *
     * @param preparedText
     *            The preparedText to set
     */
    protected void setPreparedText( String preparedText )
    {
        fPreparedText = preparedText;
    }

    protected void createButtonsForButtonBar( Composite parent )
    {
        super.createButtonsForButtonBar( parent );
        getButton( IDialogConstants.OK_ID ).setText( "Insert" ); //$NON-NLS-1$
    }
}
