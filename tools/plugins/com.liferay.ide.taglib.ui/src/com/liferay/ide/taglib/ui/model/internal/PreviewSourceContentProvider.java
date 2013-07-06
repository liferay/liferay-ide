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

package com.liferay.ide.taglib.ui.model.internal;

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.taglib.ui.model.Attribute;
import com.liferay.ide.taglib.ui.model.Tag;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.services.DerivedValueService;
import org.eclipse.sapphire.services.DerivedValueServiceData;

/**
 * @author Gregory Amerson
 */
public class PreviewSourceContentProvider extends DerivedValueService
{

    @Override
    protected DerivedValueServiceData compute()
    {
        boolean preview = "Preview".equals( context( Property.class ).name() ); //$NON-NLS-1$

        Tag tag = (Tag) context( Element.class );;

        StringBuffer buffer = new StringBuffer();

        String tagName = tag.getName().content();

        String prefix = tag.getPrefix().content();

        if( preview )
        {
            buffer.append( "<span style='color:RGB(64,128,128)'>&lt;" ); //$NON-NLS-1$
        }
        else
        {
            buffer.append( "<" ); //$NON-NLS-1$
        }

        buffer.append( prefix + ":" + tagName ); //$NON-NLS-1$

        if( preview )
        {
            buffer.append( "</span>" ); //$NON-NLS-1$
        }

        for( Attribute attr : tag.getRequiredAttributes() )
        {
            appendAttr( attr, buffer, preview );
        }

        for( Attribute attr : tag.getOtherAttributes() )
        {
            appendAttr( attr, buffer, preview );
        }

        for( Attribute attr : tag.getEvents() )
        {
            appendAttr( attr, buffer, preview );
        }

        if( preview )
        {
            buffer.append( "<span style='color:RGB(64,128,128)'>&gt;&lt;" ); //$NON-NLS-1$
        }
        else
        {
            buffer.append( "><" ); //$NON-NLS-1$
        }

        buffer.append( "/" + prefix + ":" + tagName ); //$NON-NLS-1$ //$NON-NLS-2$

        if( preview )
        {
            buffer.append( "&gt;</span>" ); //$NON-NLS-1$
        }
        else
        {
            buffer.append( ">" ); //$NON-NLS-1$
        }

        return new DerivedValueServiceData( buffer.toString() );
    }

    protected void appendAttr( Attribute attr, StringBuffer buffer, boolean preview )
    {
        String content = attr.getValue().content();

        if( content != null )
        {
            buffer.append( StringPool.SPACE );

            if( preview )
            {
                buffer.append( "<span style='color:RGB(127,0,127)'>" ); //$NON-NLS-1$
            }

            buffer.append( attr.getName().content() );

            if( preview )
            {
                buffer.append( "</span>" ); //$NON-NLS-1$
            }

            buffer.append( StringPool.EQUALS );

            if( preview )
            {
                buffer.append( "<span style='color:RGB(42,0,255);font-style:italic'>&quot;" ); //$NON-NLS-1$
            }
            else
            {
                buffer.append( StringPool.DOUBLE_QUOTE );
            }

            buffer.append( content );

            if( preview )
            {
                buffer.append( "&quot;</span>" ); //$NON-NLS-1$
            }
            else
            {
                buffer.append( StringPool.DOUBLE_QUOTE );
            }
        }
    }

}
