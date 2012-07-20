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

package com.liferay.ide.ui.snippets;

import com.liferay.ide.ui.snippets.wizard.AbstractModelWizard;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.common.snippets.internal.util.StringUtils;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public abstract class ModelSnippetInsertion extends AbstractSnippetInsertion
{

    public ModelSnippetInsertion()
    {
        super();
    }

    @Override
    public void insert( IEditorPart editorPart )
    {
        if( this.fEditorPart == null )
        {
            this.fEditorPart = editorPart;
        }

        super.insert( editorPart );
    }

    protected abstract AbstractModelWizard createModelWizard( IEditorPart fEditorPart );

    protected String getPreparedText( AbstractModelWizard wizard )
    {
        String text = fItem.getContentString();

        text = StringUtils.replace( text, "${model}", wizard.getModel() );
        text = StringUtils.replace( text, "${varName}", wizard.getVarName() );

        // Update EOLs (bug 80231)
        String systemEOL = System.getProperty( "line.separator" ); //$NON-NLS-1$
        text = StringUtils.replace( text, "\r\n", "\n" ); //$NON-NLS-1$ //$NON-NLS-2$
        text = StringUtils.replace( text, "\r", "\n" ); //$NON-NLS-1$ //$NON-NLS-2$
        if( !"\n".equals( systemEOL ) && systemEOL != null ) { //$NON-NLS-1$
            text = StringUtils.replace( text, "\n", systemEOL ); //$NON-NLS-1$
        }

        return text;
    }

    @Override
    protected String getResolvedString( Shell host )
    {
        AbstractModelWizard wizard = createModelWizard( this.fEditorPart );
        WizardDialog dialog = new WizardDialog( host, wizard );
        dialog.setBlockOnOpen( true );
        int retval = dialog.open();

        if( retval == Window.OK )
        {
            return getPreparedText( wizard );
        }

        return "";
    }

}
