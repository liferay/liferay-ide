package com.liferay.ide.core.jna;

import com.liferay.ide.core.LiferayCore;
import com.sun.jna.Native;


/**
 * @author Gregory Amerson
 */
public class LibraryUtil
{
    private static CLibrary libc = null;

    public static CLibrary getLibC()
    {
        if( libc == null && ! com.sun.jna.Platform.isWindows() )
        {
            try
            {
                libc = (CLibrary) Native.loadLibrary( "c", CLibrary.class );
            }
            catch( Exception e )
            {
                LiferayCore.logError( e );
            }
        }

        return libc;
    }

}
