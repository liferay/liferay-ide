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
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.ui.snippets;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.wst.common.snippets.core.ISnippetVariable;
import org.eclipse.wst.common.snippets.internal.VariableInsertionDialog;
import org.eclipse.wst.common.snippets.internal.palette.SnippetVariable;
import org.eclipse.wst.common.snippets.internal.util.StringUtils;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class TaglibVariableInsertionDialog extends VariableInsertionDialog
{
    protected IEditorPart editor;

    public TaglibVariableInsertionDialog( Shell parentShell, IEditorPart editor, boolean clearModality )
    {
        super( parentShell, clearModality );
        this.editor = editor;
    }

    @Override
    protected Control createDialogArea( Composite parent )
    {
        Control control = super.createDialogArea( parent );

        if( control instanceof Composite )
        {
            Composite composite = (Composite) control;
            replaceUIText( composite, Msgs.variableLowercase, Msgs.attributeLowercase );
            replaceUIText( composite, Msgs.variableUppercase, Msgs.attributeUppercase );
        }

        fTableViewer.getTable().getColumns()[0].setText( Msgs.attributeName );
        fTableViewer.getTable().redraw();

        return control;
    }

    @Override
    protected void prepareText()
    {
        // check the editor, if it is freemarker then prepare freemarker, else use JSP

        String text = prepareJSPText();

        if( isFreemarkerEditor( editor ) )
        {
            Pattern p1 = Pattern.compile( "(.*)-([a-z])(.*)" ); //$NON-NLS-1$
            Matcher m1 = p1.matcher( text );
            while( m1.matches() )
            {
                text = m1.replaceFirst( m1.group( 1 ) + m1.group( 2 ).toUpperCase() + m1.group( 3 ) );
                m1 = p1.matcher( text );
            }

            text = text.replaceAll( "<([a-zA-Z]+):", "<@$1\\." ); //$NON-NLS-1$ //$NON-NLS-2$
            text = text.replaceAll( "</([a-zA-Z]+):", "</@$1\\." ); //$NON-NLS-1$ //$NON-NLS-2$

            setPreparedText( text );
            return;
        }

        setPreparedText( text );
    }

    private String prepareJSPText()
    {
        // this could be horribly inefficient
        String text = fItem.getContentString();
        ISnippetVariable[] variables = fItem.getVariables();
        for( int i = 0; i < variables.length; i++ )
        {
            String value = (String) fTableViewer.getColumnData()[1].get( ( (SnippetVariable) variables[i] ).getId() );

            if( !CoreUtil.isNullOrEmpty( value ) )
            {
                value = StringPool.SPACE + variables[i].getName() + "=\"" + value + StringPool.DOUBLE_QUOTE; //$NON-NLS-1$
            }

            text = StringUtils.replace( text, "${" + variables[i].getName() + "}", value ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // remove all cursor markers
        text = StringUtils.replace( text, "${cursor}", "" ); //$NON-NLS-1$ //$NON-NLS-2$

        // Update EOLs (bug 80231)
        String systemEOL = System.getProperty( "line.separator" ); //$NON-NLS-1$
        text = StringUtils.replace( text, "\r\n", "\n" ); //$NON-NLS-1$ //$NON-NLS-2$
        text = StringUtils.replace( text, "\r", "\n" ); //$NON-NLS-1$ //$NON-NLS-2$
        if( !"\n".equals( systemEOL ) && systemEOL != null ) { //$NON-NLS-1$
            text = StringUtils.replace( text, "\n", systemEOL ); //$NON-NLS-1$
        }

        return text;
    }

    private static boolean isFreemarkerEditor( IEditorPart editorPart )
    {
        try
        {
            if( ( (IStorageEditorInput) editorPart.getEditorInput() ).getStorage().getName().endsWith( ".ftl" ) ) //$NON-NLS-1$
            {
                return true;
            }
        }
        catch( Exception e )
        {
            // ignore just return false
        }

        return false;
    }

    protected void replaceUIText( Composite parent, String search, String replace )
    {
        if( parent == null )
        {
            return;
        }

        if( parent.getChildren() == null || parent.getChildren().length == 0 )
        {
            return;
        }

        for( Control child : parent.getChildren() )
        {
            if( child instanceof Label )
            {
                Label label = (Label) child;
                if( label.getText() != null )
                {
                    label.setText( label.getText().replaceAll( search, replace ) );
                }
            }
            else if( child instanceof Text )
            {
                Text text = (Text) child;
                if( text.getText() != null )
                {
                    text.setText( text.getText().replaceAll( search, replace ) );
                }
            }
            else if( child instanceof Composite )
            {
                replaceUIText( (Composite) child, search, replace );
            }
        }
    }

    private static class Msgs extends NLS
    {
        public static String attributeLowercase;
        public static String attributeName;
        public static String attributeUppercase;
        public static String variableLowercase;
        public static String variableUppercase;

        static
        {
            initializeMessages( TaglibVariableInsertionDialog.class.getName(), Msgs.class );
        }
    }
}
