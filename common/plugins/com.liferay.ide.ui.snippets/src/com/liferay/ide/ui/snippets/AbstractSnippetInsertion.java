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

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.wst.common.snippets.core.ISnippetItem;
import org.eclipse.wst.common.snippets.internal.ui.EntrySerializer;
import org.eclipse.wst.common.snippets.internal.util.StringUtils;
import org.eclipse.wst.common.snippets.ui.DefaultSnippetInsertion;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public abstract class AbstractSnippetInsertion extends DefaultSnippetInsertion
{

    protected IEditorPart fEditorPart;

    protected ISnippetItem fItem;

    private static Object lastEventContent;

    private static int lastEventTime;

    /**
     * Copied from DefaultSnippetInsertion.dragSetData() version 1.7 (WTP 3.2.1)
     */
    @Override
    public void dragSetData( DragSourceEvent event, ISnippetItem item )
    {
        // IDE-334 watch for double/triple drops
        if( Platform.OS_LINUX.equals( Platform.getOS() ) && lastEventTime == event.time )
        {
            event.data = lastEventContent;
            // avoid double drop
            return;
        }

        boolean isSimpleText = TextTransfer.getInstance().isSupportedType( event.dataType );
        if( isSimpleText )
        {
            // set variable values to ""
            IWorkbenchWindow window = SnippetsUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
            Shell shell = null;
            if( window != null )
            {
                shell = window.getShell();
            }

            if( fItem == null )
            {
                fItem = item;
            }

            String content = getResolvedString( shell );

            if( CoreUtil.isNullOrEmpty( content ) )
            {
                event.dataType = null;
            }

            // Update EOLs (bug 80231)
            String systemEOL = System.getProperty( "line.separator" ); //$NON-NLS-1$
            content = StringUtils.replace( content, "\r\n", "\n" ); //$NON-NLS-1$ //$NON-NLS-2$
            content = StringUtils.replace( content, "\r", "\n" ); //$NON-NLS-1$ //$NON-NLS-2$
            if( !"\n".equals( systemEOL ) && systemEOL != null ) { //$NON-NLS-1$
                content = StringUtils.replace( content, "\n", systemEOL ); //$NON-NLS-1$
            }
            event.data = content;
        }
        else
        {
            /*
             * All complex insertions send an XML encoded version of the item itself as the data. The drop action must
             * use this to prompt the user for the correct insertion data
             */
            event.data = EntrySerializer.getInstance().toXML( item );
        }

        if( Platform.OS_LINUX.equals( Platform.getOS() ) )
        {
            lastEventTime = event.time;
            lastEventContent = event.data;
        }
    }

    @Override
    public void setEditorPart( IEditorPart editorPart )
    {
        super.setEditorPart( editorPart );
        this.fEditorPart = editorPart;
    }

    @Override
    public void setItem( ISnippetItem item )
    {
        super.setItem( item );
        this.fItem = item;
    }

    /**
     * Copied from DefaultSnippetInsertion.getInsertString() version 1.7 (WTP 3.2.1)
     */
    @Override
    protected String getInsertString( Shell host )
    {
        if( fItem == null )
            return ""; //$NON-NLS-1$

        String insertString = getResolvedString( host );

        return insertString;
    }

    protected abstract String getResolvedString( Shell host );

}
