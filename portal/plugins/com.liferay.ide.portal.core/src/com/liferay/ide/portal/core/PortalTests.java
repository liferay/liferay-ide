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
            
            JSONObject user = connection.getUserByEmailAddress(companyId);
            long userId = user.getLong( "userId" );
            System.out.println("userId " + userId);
            
            JSONArray userSites = connection.getUserSites();
            
            for( int i = 0; i < userSites.length(); i++ )
            {
                JSONObject site = userSites.getJSONObject( i );
                long groupId = site.getLong( "groupId" );
                System.out.println("groupId " + groupId);
                
                JSONArray structures = connection.getStructures( groupId );
                
                for( int j = 0; j < structures.length(); j++ )
                {
                    JSONObject structure = structures.getJSONObject( j );
                    long structureId = structure.getLong( "structureId" );
                    System.out.println(structure);
                    
                    JSONArray structureTemplates = connection.getStructureTemplates( groupId, structureId );
                    
                    for( int k = 0; k < structureTemplates.length(); k++ )
                    {
                        JSONObject structureTemplate = structureTemplates.getJSONObject( k );
                        System.out.println(structureTemplate);
                    }
                }
                
                JSONArray articles = connection.getJournalArticles( groupId, userId );
                
                for( int j = 0; j < articles.length(); j++ )
                {
                    JSONObject article = articles.getJSONObject( j );
                    long articleId = article.getLong( "articleId" );
                    System.out.println("articleId " + articleId);
                    System.out.println(article);
                }
            }
            
            
            
            
        }
        catch( Exception e )
        {
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
