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

import com.liferay.ide.ui.snippets.AbstractSnippetInsertion;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.common.snippets.internal.util.StringUtils;

@SuppressWarnings( "restriction" )
public class AlloyTagSnippetInsertion extends AbstractSnippetInsertion
{

    public AlloyTagSnippetInsertion()
    {
        super();
    }

    /**
     * Copied from DefaultSnippetInsertion.getInsertString() version 1.7 (WTP 3.2.1)
     */
    @Override
    protected String getResolvedString( Shell host )
    {
        String insertString = null;

        if( fItem.getVariables().length > 0 )
        {
            insertString = AlloyTagItemHelper.getInsertString( host, fItem, fEditorPart.getEditorInput() );
        }
        else
        {
            insertString = StringUtils.replace( fItem.getContentString(), "${cursor}", "" ); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return insertString;
    }

}
