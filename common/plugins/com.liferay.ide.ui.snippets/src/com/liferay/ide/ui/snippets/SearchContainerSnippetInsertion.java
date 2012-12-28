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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.ui.snippets.wizard.AbstractModelWizard;
import com.liferay.ide.ui.snippets.wizard.LiferayUISearchContainerWizard;

import org.eclipse.ui.IEditorPart;
import org.eclipse.wst.common.snippets.internal.util.StringUtils;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class SearchContainerSnippetInsertion extends ModelSnippetInsertion
{

    public SearchContainerSnippetInsertion()
    {
        super();
    }

    protected String getPreparedText( AbstractModelWizard wizard )
    {
        String text = super.getPreparedText( wizard );

        text = StringUtils.replace( text, "${modelClass}", ( (LiferayUISearchContainerWizard) wizard ).getModelClass() ); //$NON-NLS-1$

        StringBuffer columns = new StringBuffer();
        String[] propColumns = wizard.getPropertyColumns();

        if( !CoreUtil.isNullOrEmpty( propColumns ) )
        {
            for( String prop : propColumns )
            {
                columns.append( "<liferay-ui:search-container-column-text property=\"" ); //$NON-NLS-1$
                columns.append( prop );
                columns.append( "\" />\n\n\t\t" ); //$NON-NLS-1$
            }
        }

        String columnsVal = columns.toString();
        text = StringUtils.replace( text, "${columns}", CoreUtil.isNullOrEmpty( columnsVal ) ? StringUtil.EMPTY : columnsVal ); //$NON-NLS-1$

        return text;
    }

    @Override
    protected AbstractModelWizard createModelWizard( IEditorPart fEditorPart )
    {
        return new LiferayUISearchContainerWizard( this.fEditorPart );
    }

}
