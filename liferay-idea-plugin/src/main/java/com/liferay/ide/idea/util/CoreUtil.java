package com.liferay.ide.idea.util;

import java.security.MessageDigest;
import java.util.List;
import org.osgi.framework.Version;

public class CoreUtil {

    public static boolean isNotNullOrEmpty( Object[] array )
    {
        return !isNullOrEmpty( array );
    }

    public static boolean isNullOrEmpty( List<?> list )
    {
        return list == null || list.size() == 0;
    }

    public static boolean isNullOrEmpty( Object[] array )
    {
        return array == null || array.length == 0;
    }

    public static boolean isNullOrEmpty( String val )
    {
        return val == null || val.equals( StringPool.EMPTY ) || val.trim().equals( StringPool.EMPTY );
    }

    public static final String createStringDigest( final String str )
    {
        try
        {
            final MessageDigest md = MessageDigest.getInstance( "SHA-256" ); //$NON-NLS-1$
            final byte[] input = str.getBytes( "UTF-8" ); //$NON-NLS-1$
            final byte[] digest = md.digest( input );

            final StringBuilder buf = new StringBuilder();

            for( int i = 0; i < digest.length; i++ )
            {
                String hex = Integer.toHexString( 0xFF & digest[ i ] );

                if( hex.length() == 1 )
                {
                    buf.append( '0' );
                }

                buf.append( hex );
            }

            return buf.toString();
        }
        catch( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    public static int compareVersions( Version v1, Version v2 )
    {
        if( v2 == v1 )
        { // quicktest
            return 0;
        }

        int result = v1.getMajor() - v2.getMajor();

        if( result != 0 )
        {
            return result;
        }

        result = v1.getMinor() - v2.getMinor();

        if( result != 0 )
        {
            return result;
        }

        result = v1.getMicro() - v2.getMicro();

        if( result != 0 )
        {
            return result;
        }

        return v1.getQualifier().compareTo( v2.getQualifier() );
    }
}
