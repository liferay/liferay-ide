/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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
 *******************************************************************************/

package com.liferay.ide.core.util;

import static com.liferay.ide.core.util.StringPool.SINGLE_QUOTE_CHAR;
import static com.liferay.ide.core.util.StringPool.DOUBLE_QUOTE_CHAR;

/**
 * @author Kuo Zhang
 */
public class StringUtil
{

    public static boolean isQuoted( String string )
    {
        if( ( string == null ) || ( string.length() < 2 ) )
        {
            return false;
        }

        int lastIndex = string.length() - 1;
        char firstChar = string.charAt( 0 );
        char lastChar = string.charAt( lastIndex );

        return( ( firstChar == SINGLE_QUOTE_CHAR && lastChar == SINGLE_QUOTE_CHAR ) ||
                ( firstChar == DOUBLE_QUOTE_CHAR && lastChar == DOUBLE_QUOTE_CHAR ) );
    }

    public static String replace( String content, String source, String target )
    {
        if( content == null )
        {
            return null;
        }

        int length = content.length();
        int position = 0;
        int previous = 0;
        int spacer = source.length();

        StringBuffer sb = new StringBuffer();

        while( position + spacer - 1 < length && content.indexOf( source, position ) > -1 )
        {
            position = content.indexOf( source, previous );
            sb.append( content.substring( previous, position ) );
            sb.append( target );
            position += spacer;
            previous = position;
        }

        sb.append( content.substring( position, content.length() ) );

        return sb.toString();
    }
}
