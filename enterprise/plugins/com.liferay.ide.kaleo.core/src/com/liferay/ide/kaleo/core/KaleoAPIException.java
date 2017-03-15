/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay IDE ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */

package com.liferay.ide.kaleo.core;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "serial" )
public class KaleoAPIException extends Exception
{
    private String msg;
    private String api;

    public KaleoAPIException( String api, Exception e )
    {
        super( e );

        this.api = api;
        this.msg = e.getMessage();
    }

    public KaleoAPIException( String api, String msg )
    {
        this.api = api;
        this.msg = msg;
    }

    @Override
    public String getMessage()
    {
        return msg + " API: " + api;
    }
}
