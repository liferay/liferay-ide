
package com.liferay.ide.portal.core;

import static com.liferay.ide.core.util.CoreUtil.empty;

import com.liferay.ide.core.APIException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PortalConnection implements IPortalConnection
{

    private String host;
    private HttpClient httpClient;
    private String httpPort;
    private String password;
    private URL portalHomeUrl;
    private String serverName;
    private String username;

    protected Object getAPI(Object ... args) throws APIException
    {
        if( !( args[0] instanceof String ) )
        {
            throw new IllegalArgumentException("First argument must be a string.");
        }
        
        Object retval = null;
        String api = (String) args[0];
        
        try
        {
            GetMethod getAPIMethod = new GetMethod( api );
            
            if( args.length >= 3 )
            {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                
                for( int i = 1; i < args.length; i += 2 )
                {
                    final NameValuePair param = new NameValuePair();
                    
                    if( args[i] != null )
                    {
                        param.setName( args[i].toString() );
                    }
                    
                    if( args[i + 1] != null )
                    {
                        param.setValue( args[i + 1].toString() );
                    }
                    
                    params.add( param );
                }
                
                getAPIMethod.setQueryString( params.toArray( new NameValuePair[0] ) );
            }
                            
            String response = getHttpResponse( getAPIMethod );
            
            if( !empty( response ) )
            {
                Object jsonResponse = getJSONResponse( response );
                
                if( jsonResponse == null )
                {
                    throw new APIException( api, "Unable to get API." );
                }
                else
                {
                    retval = jsonResponse;
                }
            }
        }
        catch( Exception e )
        {
            throw new APIException( api, e );
        }
        
        return retval;
    }

    private String getBaseAPIUrl()
    {
        return "http://" + getHost() + ":" + getHttpPort();
    }
    
    private String getCompanyByVirtualHostAPI()
    {
        return getBaseAPIUrl() + GET_COMPANY_BY_VIRTUAL_HOST_API;
    }
    
    public JSONObject getCompanyIdByVirtualHost() throws APIException
    {
        JSONObject company = null;
        
        String api = getCompanyByVirtualHostAPI();
        
        Object jsonResponse = getAPI( api, "virtualHost", getHost() );
        
        if( jsonResponse instanceof JSONObject )
        {
            company = (JSONObject) jsonResponse;
        }
        else
        {
            throw new APIException( api, "Unable to get JSONObject" );
        }

        return company;
    }
    
    public String getHost()
    {
        return host;
    }
    
    private HttpClient getHttpClient()
    {
        if( httpClient == null )
        {
            httpClient = new HttpClient();

            if( getUsername() != null || getPassword() != null )
            {
                httpClient.getParams().setAuthenticationPreemptive( true );
                UsernamePasswordCredentials creds = new UsernamePasswordCredentials( getUsername(), getPassword() );

                int httpPort = -1;

                try
                {
                    httpPort = Integer.parseInt( getHttpPort() );
                }
                catch( Exception e )
                {
                }

                httpClient.getState().setCredentials( new AuthScope( getHost(), httpPort ), creds );
            }
        }

        return httpClient;
    }
    
    public String getHttpPort()
    {
        return httpPort;
    }
    
    private String getHttpResponse( HttpMethod method ) throws Exception
    {
        int statusCode = getHttpClient().executeMethod( method );

        if( statusCode == HttpStatus.SC_OK )
        {
            byte[] body = method.getResponseBody();
            String response = new String( body );

            return response;
        }

        return null;
    }
    
    public JSONArray getJournalArticles( long groupId, long userId ) throws APIException
    {
        JSONArray journalArticles = null;
        String api = getJournalArticlesAPI();
        
        Object jsonResponse = getAPI( api, "groupId", groupId, "userId", userId, "start", -1, "end", -1, "-obc", null );
        
        if( jsonResponse instanceof JSONArray )
        {
            journalArticles = (JSONArray) jsonResponse;
        }
        else
        {
            throw new APIException( api, "Unable to get JSONArray" );
        }

        return journalArticles;
    }

    private Object getJSONResponse( String response )
    {
        Object retval = null;

        try
        {
            retval = new JSONObject( response );
        }
        catch( JSONException e )
        {
            try
            {
                retval = new JSONArray( response );
            }
            catch( JSONException e1 )
            {
            }
        }
        
        return retval;
    }
    
    public String getPassword()
    {
        return password;
    }

    public URL getPortalHtmlUrl()
    {
        return this.portalHomeUrl;
    }

    public String getServerName()
    {
        return serverName;
    }

    public JSONArray getStructures( long groupId ) throws APIException
    {
        JSONArray structures = null;
        String api = getStructuresAPI();
        
        Object jsonResponse = getAPI( api, "groupId", groupId );
        
        if( jsonResponse instanceof JSONArray )
        {
            structures = (JSONArray) jsonResponse;
        }
        else
        {
            throw new APIException( api, "Unable to get JSONArray" );
        }

        return structures;
    }
    
    public JSONArray getStructureTemplates( long groupId, long structureId ) throws APIException
    {
        JSONArray structureTemplates = null;
        String api = getStructureTemplatesAPI();
        
        Object jsonResponse = getAPI( api, "groupId", groupId, "structureId", structureId );
        
        if( jsonResponse instanceof JSONArray )
        {
            structureTemplates = (JSONArray) jsonResponse;
        }
        else
        {
            throw new APIException( api, "Unable to get JSONArray" );
        }

        return structureTemplates;
    }

    private String getStructuresAPI()
    {
        return getBaseAPIUrl() + GET_STRUCTURES_API;
    }
    
    private String getJournalArticlesAPI()
    {
        return getBaseAPIUrl() + GET_JOURNAL_ARTICLES_API;
    }
    
    private String getStructureTemplatesAPI()
    {
        return getBaseAPIUrl() + GET_STRUCTURE_TEMPLATES_API;
    }

    public JSONObject getUserByEmailAddress(long companyId) throws APIException
    {
        JSONObject user = null;
        String api = getUserByEmailAddressAPI();
        
        Object jsonResponse = getAPI( api, "companyId", Long.toString( companyId), "emailAddress", getUsername() );
        
        if( jsonResponse instanceof JSONObject )
        {
            user = (JSONObject) jsonResponse;
        }
        else
        {
            throw new APIException( api, "Unable to get JSONObject" );
        }

        return user;
    }

    private String getUserByEmailAddressAPI()
    {
        return getBaseAPIUrl() + GET_USER_BY_EMAIL_ADDRESS_API;
    }

    public String getUsername()
    {
        return username;
    }

    public JSONArray getUserSites() throws APIException
    {
        JSONArray sites = null;
        String api = getUserSitesAPI();
        
        Object jsonResponse = getAPI( api );
        
        if( jsonResponse instanceof JSONArray )
        {
            sites = (JSONArray) jsonResponse;
        }
        else
        {
            throw new APIException( api, "Unable to get JSONArray" );
        }
        
        return sites;
    }

    private String getUserSitesAPI()
    {
        return getBaseAPIUrl() + GET_USER_SITES_API;
    }

    public void setHost( String host )
    {
        this.host = host;
        this.httpClient = null;
    }

    public void setHttpPort( String httpPort )
    {
        this.httpPort = httpPort;
        this.httpClient = null;
    }

    public void setPassword( String password )
    {
        this.password = password;
        this.httpClient = null;
    }

    public void setPortalHtmlUrl( URL portalHomeUrl )
    {
        this.portalHomeUrl = portalHomeUrl;
        this.httpClient = null;
    }

    public void setServerName( String serverName )
    {
        this.serverName = serverName;
        this.httpClient = null;
    }

    public void setUsername( String username )
    {
        this.username = username;
        this.httpClient = null;
    }

}
