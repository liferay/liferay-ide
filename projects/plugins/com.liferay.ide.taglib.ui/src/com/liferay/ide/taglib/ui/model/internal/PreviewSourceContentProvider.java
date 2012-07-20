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

package com.liferay.ide.taglib.ui.model.internal;

import com.liferay.ide.taglib.ui.model.Attribute;
import com.liferay.ide.taglib.ui.model.Tag;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
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
        boolean preview = "Preview".equals( context( ModelProperty.class ).getName() );

        Tag tag = (Tag) context( IModelElement.class );;

        StringBuffer buffer = new StringBuffer();

        String tagName = tag.getName().getContent();

        String prefix = tag.getPrefix().getContent();

        if( preview )
        {
            buffer.append( "<span style='color:RGB(64,128,128)'>&lt;" );
        }
        else
        {
            buffer.append( "<" );
        }

        buffer.append( prefix + ":" + tagName );

        if( preview )
        {
            buffer.append( "</span>" );
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
            buffer.append( "<span style='color:RGB(64,128,128)'>&gt;&lt;" );
        }
        else
        {
            buffer.append( "><" );
        }

        buffer.append( "/" + prefix + ":" + tagName );

        if( preview )
        {
            buffer.append( "&gt;</span>" );
        }
        else
        {
            buffer.append( ">" );
        }

        return new DerivedValueServiceData( buffer.toString() );
    }

    protected void appendAttr( Attribute attr, StringBuffer buffer, boolean preview )
    {
        String content = attr.getValue().getContent();

        if( content != null )
        {
            buffer.append( " " );

            if( preview )
            {
                buffer.append( "<span style='color:RGB(127,0,127)'>" );
            }

            buffer.append( attr.getName().getContent() );

            if( preview )
            {
                buffer.append( "</span>" );
            }

            buffer.append( "=" );

            if( preview )
            {
                buffer.append( "<span style='color:RGB(42,0,255);font-style:italic'>&quot;" );
            }
            else
            {
                buffer.append( "\"" );
            }

            buffer.append( content );

            if( preview )
            {
                buffer.append( "&quot;</span>" );
            }
            else
            {
                buffer.append( "\"" );
            }
        }
    }

}
