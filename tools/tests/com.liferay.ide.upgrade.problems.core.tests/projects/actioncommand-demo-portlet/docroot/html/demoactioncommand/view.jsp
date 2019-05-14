<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://alloy.liferay.com/tld/aui" prefix="aui" %>

<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="javax.portlet.ActionRequest" %>

<portlet:defineObjects />

<%
String name = ParamUtil.getString(renderRequest, "name");
%>
<%
if(name != "") {
%>	
	<span>Hello <%=name %></span>
<%
}
else {
%>
	<portlet:actionURL var="setNameURL">
		<portlet:param name="<%= ActionRequest.ACTION_NAME %>" value="SayHello" />
	</portlet:actionURL>
	<aui:form action="<%=setNameURL %>" method="POST" name="fm">
		<aui:input name="name" label="Say Hello To:" />
		<aui:button type="submit" />
	</aui:form>
<%} %>
