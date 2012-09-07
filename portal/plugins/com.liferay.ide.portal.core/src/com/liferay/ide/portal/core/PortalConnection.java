
package com.liferay.ide.portal.core;

import static com.liferay.ide.core.util.CoreUtil.empty;

import com.liferay.ide.core.APIException;

import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.JSONArray;
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

    private String getBaseAPIUrl()
    {
        return "http://" + getHost() + ":" + getHttpPort();
    }

    private String getCompanyByVirtualHostAPI()
    {
        return getBaseAPIUrl() + GET_COMPANY_BY_VIRTUAL_HOST_API;
    }
    
    private String getUserByEmailAddressAPI()
    {
        return getBaseAPIUrl() + GET_USER_BY_EMAIL_ADDRESS_API;
    }
    
    private String getUserSitesAPI()
    {
        return getBaseAPIUrl() + GET_USER_SITES_API;
    }
    
    public JSONObject getCompanyIdByVirtualHost() throws APIException
    {
        JSONObject company = null;
        APIException apiException = null;

        try
        {
            GetMethod companyIdByVirutalHostAPI = new GetMethod( getCompanyByVirtualHostAPI() );
            
            NameValuePair[] params = new NameValuePair[] 
            {
                new NameValuePair( "virtualHost", getHost() )
            };
            
            companyIdByVirutalHostAPI.setQueryString( params );

            String response = getHttpResponse( companyIdByVirutalHostAPI );

            if( !empty( response ) )
            {
                JSONObject responseObject = new JSONObject( response );

                if( responseObject != null && responseObject.has( "exception" ) )
                {
                    company = null;
                    apiException = new APIException( getCompanyByVirtualHostAPI(), responseObject.getString( "exception" ) );
                }
                else
                {
                    company = responseObject;
                }
            }
        }
        catch( Exception e )
        {
            apiException = new APIException( getCompanyByVirtualHostAPI(), e );
        }
        
        if (apiException != null)
        {
            throw apiException;
        }

        return company;
    }

    public JSONObject getUserByEmailAddress(long companyId) throws APIException
    {
        JSONObject user = null;
        APIException apiException = null;
        String api = getUserByEmailAddressAPI();
        
        try
        {
            GetMethod userByEmailAddressAPI = new GetMethod( api );
            
            NameValuePair[] params = new NameValuePair[] 
            {
                new NameValuePair( "companyId", Long.toString( companyId) ),
                new NameValuePair( "emailAddress", getUsername() ),
            };
            
            userByEmailAddressAPI.setQueryString( params );

            String response = getHttpResponse( userByEmailAddressAPI );

            if( !empty( response ) )
            {
                JSONObject responseObject = new JSONObject( response );

                if( responseObject != null && responseObject.has( "exception" ) )
                {
                    user = null;
                    apiException = new APIException( api, responseObject.getString( "exception" ) );
                }
                else
                {
                    user = responseObject;
                }
            }
        }
        catch( Exception e )
        {
            apiException = new APIException( api, e );
        }
        
        if (apiException != null)
        {
            throw apiException;
        }

        return user;
    }
    
    public JSONArray getUserSites() throws APIException
    {
        JSONArray sites = null;
        APIException apiException = null;
        String api = getUserSitesAPI();
        
        try
        {
            GetMethod getUserSitesAPI = new GetMethod( api );
           
            String response = getHttpResponse( getUserSitesAPI );

            if( !empty( response ) )
            {
                JSONArray responseArray = new JSONArray( response );

                if( responseArray != null /*&& responseArray.has( "exception" )*/ )
                {
                    sites = null;
                    apiException = new APIException( api, /*responseArray.getString(*/ "exception" /*)*/ );
                }
                else
                {
                    sites = responseArray;
                }
            }
        }
        catch( Exception e )
        {
            apiException = new APIException( api, e );
        }
        
        if (apiException != null)
        {
            throw apiException;
        }

        return sites;
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
            // Read the response body.
            byte[] body = method.getResponseBody();
            String response = new String( body );

            return response;
        }

        return null;
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

    public String getUsername()
    {
        return username;
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
