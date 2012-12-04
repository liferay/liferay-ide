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
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.core.remote;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "serial" )
public class APIException extends Exception
{

    private String msg;
    private String api;

    public APIException( String api, Exception e )
    {
        super( e );

        this.api = api;
        this.msg = e.getMessage();
    }

    public APIException( String api, String msg )
    {
        this.api = api;
        this.msg = msg;
    }

    @Override
    public String getMessage()
    {
        return msg + " API: " + api; //$NON-NLS-1$
    }
}
