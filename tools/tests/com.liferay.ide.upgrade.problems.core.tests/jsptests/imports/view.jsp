

<%@ page language="Java" import="java.net.*,java.io.*,javax.net.ssl.HttpsURLConnection,java.util.*, javax.xml.parsers.*, org.w3c.dom.Document, 
com.liferay.portal.model.UserGroup, com.liferay.portal.model.Role, com.liferay.portal.model.Portlet, com.liferay.portal.util.PortletKeys, javax.portlet.PortletPreferences, 
com.liferay.portal.service.PortletPreferencesLocalServiceUtil, com.liferay.portal.service.LayoutLocalServiceUtil"%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<portlet:defineObjects />

<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme"%>
<liferay-theme:defineObjects />



<%@include file="/jsp/init.jsp"%>




<%
	System.out.println("  --- Establishing Qlikview Connection ---");

	String internalServer = GetterUtil.getString(initInternalServer); //"https://srv-ess-qlik1";
	String externalServer = GetterUtil.getString(initExternalServer); //"https://esp.usdoj.gov";
	String targetURL = GetterUtil.getString(initTargetURL); 
	
	String qlikviewPath = GetterUtil.getString(initQlikviewPath);//"qlikview";
	String qvAJAXPath = GetterUtil.getString(initQVAJAXPath);//"QvAJAXZfc";
	
	boolean useTargetURL = Boolean.parseBoolean( GetterUtil.getString(initUseTargetURL) );
	boolean displayConnectionContent = Boolean.parseBoolean( GetterUtil.getString(initDisplayConnectionContent) );
	
	//String useTargetURL = GetterUtil.getString(initUseTargetURL);
	//String displayConnectionContent = GetterUtil.getString(initDisplayConnectionContent);
	
	System.out.println("useTargetURL: "+useTargetURL);
	System.out.println("displayConnectionContent: "+displayConnectionContent);
	
	try
	{
		if(user != null)
		{
			//User Info (client)
			String userId = user.getFullName();
			String loginID = user.getLogin(); //Currently being used with QV.
			String userName = user.getFullName();
			String userEmail = user.getEmailAddress();
			
			System.out.println("Web Ticket for: " + loginID);
			
			
			//Groups
			String latestUserGroup = GetterUtil.getString(origUserGroup);
			//out.println("Authorized UserGroup: "+ latestUserGroup);
			System.out.println("Authorized UserGroup: "+ latestUserGroup);
			
			List<UserGroup> groups = user.getUserGroups();
			boolean authorized = false;
			for (UserGroup group : groups)
			{
				//Check if authorized
				if( group.getGroup().getDescriptiveName().equalsIgnoreCase(latestUserGroup) )// || group.getGroup().getDescriptiveName().equalsIgnoreCase("Non LEA"))
					authorized = true;
			}
			
			
			//Roles
			List<Role> roles = user.getRoles();
			boolean isAdmin = false;
			for (Role role : roles)
			{
				//Check if Admin
				if( role.getDescriptiveName().equalsIgnoreCase("Administrator") )
					isAdmin = true;
			}
			
			
			//User Authorized for Portlet Content?
			if(authorized)
			{
				//if Admin, display content
				if(isAdmin && displayConnectionContent)
				{
					System.out.println("- Admin User - Displaying Connection Content");
					out.println("<div>");
				}
				else//otherwise use a non-display div
					out.println("<div style='display: none;'>");
				
				out.println("<br/>");
				out.println("Authorized!");
				out.println("<br/>");
				
				
				
				//WebTicket Generation
				String qvWebTicket = generateWebTicket(internalServer, qvAJAXPath, loginID);
				System.out.println("Web Ticket Received: " + qvWebTicket);
				
				//Qlikview Authentication
				String qvAuthenticateURL = generateAuthenticationURL(externalServer, qlikviewPath, qvAJAXPath, qvWebTicket);
				
				
				//Execute Authentication (from client)
				out.println("<iframe src='"+ qvAuthenticateURL +"' height='200px' width='100%'></iframe></div>");
				
				
				//Display Target Document
				if (useTargetURL)
				{
					System.out.println("Displaying: "+targetURL);
					out.println("<iframe src='"+ targetURL +"' height='850px' width='100%' min-width='1250px' frameborder='0'></iframe>");
				}
				
			}
			else
			{
				out.println("User Not Authorized");
				System.out.println("User NOT Authorized, must be part of UserGroup: "+ latestUserGroup);
			}
		}
		else
		{
			//out.println("You have not been Authenticated!");
			
			System.out.println("\n*** Someone has tried accessing QlikView Without Authenticating - Unknown Client Denied ***\n");
		}
		
	}
	catch ( Exception e )
	{
		e.printStackTrace();
	}
%>




<%!
	
	public String generateWebTicket(String internalServer, String qvAJAXPath, String userID)
	{
		String ticket = null;
		
		try
		{
			//URL for WebTicket
			URL webTicketURL = new URL( internalServer + "/" + qvAJAXPath + "/GetWebTicket.aspx" ); //new URL( internalServer + "/QvAJAXZfc/GetWebTicket.aspx" );
			//System.out.println("User Web Ticket URL: " + webTicketURL);
			
			HttpsURLConnection qlikViewUrlConn = (HttpsURLConnection)webTicketURL.openConnection();
			qlikViewUrlConn.setRequestMethod( "POST" );
			qlikViewUrlConn.setDoOutput( true );
			qlikViewUrlConn.setUseCaches( false );
			qlikViewUrlConn.setRequestProperty( "Content-Type", "text/xml;charset=utf-8" );
			
			String webTicketXML = "<Global method=\"GetWebTicket\"><UserId>" + userID + "</UserId> <GroupList><string>test</string></GroupList> <GroupsIsNames>true</GroupsIsNames></Global>" ;
			qlikViewUrlConn.setRequestProperty("Content-length", webTicketXML.length()+"");
			
			OutputStreamWriter wr = new OutputStreamWriter( qlikViewUrlConn.getOutputStream() );
			wr.write( webTicketXML );
			wr.flush();
			
			int responseCode = qlikViewUrlConn.getResponseCode();
			String responseMsg = qlikViewUrlConn.getResponseMessage();
			
			
			//System Output
			System.out.println("POST: "+ webTicketURL);
			//System.out.println("Post parameters : " + urlParameters);
			System.out.println("RESPONSE: "+ responseCode +", "+ responseMsg);
			//System.out.println("Response Message : " + responseMsg);
			
			
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = documentBuilder.parse( qlikViewUrlConn.getInputStream() );
			
			if ( document.getDocumentElement().getFirstChild() != null && document.getDocumentElement().getFirstChild().getFirstChild() != null )
				ticket = document.getDocumentElement().getFirstChild().getFirstChild().getNodeValue();
			
			//System.out.println("The Web Ticket received for the user - '" + userId + "' is : " + ticket);
		}
		catch(Exception e)
		{ e.printStackTrace(); }
		
		return ticket;
	}
	
	
	
	public String generateAuthenticationURL(String externalServer, String qlikviewPath, String qvAJAXPath, String ticket)
	{
		//URL for initial qlikview authentication (with default acces point)
		String accessPointURL = externalServer + "/" + qlikviewPath + "/"; //"https://srv-pps-qlik/qlikview";
		String qvAuthenticateURL = externalServer + "/" + qvAJAXPath + "/Authenticate.aspx?type=html&webticket=" + ticket + "&try=" + accessPointURL + "?webticket=" + ticket;
		
		//Logging
		//System.out.println("Ticket Assigend: " + ticket);
		System.out.println("Establishing Client Handshake: " + qvAuthenticateURL +"\n");
		
		
		return qvAuthenticateURL;
	}
	
%>
