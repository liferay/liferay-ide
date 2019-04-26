<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:defineObjects />

<%



	RepositoryLocalServiceUtil.getRepositoryImpl(
			0, fileEntryId, 0);

%>
This is the <b>Test</b> portlet.
