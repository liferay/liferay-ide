package com.liferay.ide.portal.core;

import org.json.JSONArray;
import org.json.JSONObject;



public class PortalTests
{

    /**
     * @param args
     */
    public static void main( String[] args )
    {
        testGetStructures();
    }

    private static void testGetStructures()
    {
        IPortalConnection connection = getConnection();
        
        try
        {
            JSONObject company = connection.getCompanyIdByVirtualHost();
            long companyId = company.getLong( "companyId" );
            System.out.println("companyId " + companyId);
            
            JSONObject user = connection.getUserByEmailAddress(companyId);
            long userId = user.getLong( "userId" );
            System.out.println("userId " + userId);
            
            connection.setPassword( "xxx" );
            JSONArray userSites = connection.getUserSites();
            System.out.println(userSites);
        }
        catch( Exception e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    private static IPortalConnection getConnection()
    {
        IPortalConnection connection = new PortalConnection();

        connection.setHost( "localhost" );
        connection.setHttpPort( "8080" );
        connection.setUsername( "test@liferay.com" );
        connection.setPassword( "test1" );
        
        return connection;
    }

    

    

}
