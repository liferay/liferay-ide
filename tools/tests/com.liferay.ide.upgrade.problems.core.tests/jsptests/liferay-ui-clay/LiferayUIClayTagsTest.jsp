<%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

<%@ include file="/init.jsp" %>

<clay:alert
	closeable="<%= true %>"
   	style="warning"
    message="you-have-not-selected-any-vocabularies"
/>

<clay:badge
	label="87"
	style="secondary"
/>
<clay:button
	elementClasses="dropdown-item transition-link"
	id='<%= liferayPortletResponse.getNamespace() + "assign-to-me-modal-opener" %>'
	label='<%= LanguageUtil.get(request, "assign-to-me") %>'
	size="lg"
	style="secondary"
/>

<clay:dropdown-actions
	buttonLabel="More"
	buttonStyle="secondary"
	caption="Showing 4 of 32 Options"
	dropdownItems="<%= dropdownsDisplayContext.getDefaultDropdownItems() %>"
	helpText="You can customize this menu or see all you have by pressing \"more\"."
/>
<clay:dropdown-menu
	buttonLabel="Done"
	dropdownItems="<%= dropdownsDisplayContext.getInputDropdownItems() %>"
	label="Inputs"
	searchable="<%= true %>"
/>

<clay:icon
    elementClasses="ratings-stars-average-icon"
    symbol="star"
/>
<clay:label
	closeable="<%= true %>"
	label="<%= parentCategory.getTitle(locale) %>" />
		
<clay:link
	ariaLabel='<%= LanguageUtil.format(request, "in-reply-to-x", HtmlUtil.escape(parentDiscussionComment.getUserName()), false) %>'
	elementClasses="lfr-discussion-parent-link"
	data-inreply-content="<%= HtmlUtil.escapeAttribute(parentDiscussionComment.getBody()) %>"
	data-inreply-title="<%= HtmlUtil.escapeAttribute(parentCommentUserBuffer) %>"
	href='<%= "#" + randomNamespace + "message_" + parentDiscussionComment.getCommentId() %>'
	icon="redo"
	label="<%= HtmlUtil.escape(parentDiscussionComment.getUserName()) %>"
/>
<clay:sticker
	label="PDF"
	position="top-left"
	style="danger"
/>
<clay:stripe
	style="danger"
	message="This is an error message."
	title="Error"
/>




