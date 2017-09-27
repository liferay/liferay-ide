<%--
/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ include file="/html/portlet/message_boards/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");
MBMessage message = (MBMessage)request.getAttribute(WebKeys.MESSAGE_BOARDS_MESSAGE);
long messageId = BeanParamUtil.getLong(message, request, "messageId");
long categoryId = MBUtil.getCategoryId(request, message);
MBUtil.addPortletBreadcrumbEntries(message, request, renderResponse);
PortletURL portletURL = renderResponse.createRenderURL();
portletURL.setParameter("struts_action", "/message_boards/edit_message");
portletURL.setParameter("messageId", String.valueOf(message.getMessageId()));
PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(pageContext, "edit"), portletURL.toString());
PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(pageContext, "removed-attachments"), currentURL);
PortletURL iteratorURL = renderResponse.createRenderURL();
iteratorURL.setParameter("struts_action", "/message_boards/view_deleted_message_attachments");
iteratorURL.setParameter("redirect", currentURL);
iteratorURL.setParameter("messageId", String.valueOf(messageId));
%>

<liferay-ui:trash-empty
    confirmMessage="are-you-sure-you-want-to-remove-the-attachments-for-this-message"
    emptyMessage="remove-the-attachments-for-this-message"
    infoMessage="attachments-that-have-been-removed-for-more-than-x-will-be-automatically-deleted"
    portletURL="<%= emptyTrashURL.toString() %>"
    totalEntries="<%= message.getDeletedAttachmentsFileEntriesCount() %>"
/>
