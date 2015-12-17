package com.liferay.ide.project.core.modules;


/**
 * @author Gregory Amerson
 */
public class BladeCLIException extends Exception
{
    public BladeCLIException( String msg )
    {
        super( msg );
    }

    public BladeCLIException( String msg, Exception e )
    {
        super( msg, e );
    }

    private static final long serialVersionUID = 1081950550390345603L;
}
