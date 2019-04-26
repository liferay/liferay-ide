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

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://alloy.liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/security" prefix="liferay-security" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@ page import="org.liferay.jukebox.search.AlbumDisplayTerms" %>
<%@ page import="org.liferay.jukebox.search.AlbumSearch" %>
<%@ page import="org.liferay.jukebox.search.ArtistDisplayTerms" %>
<%@ page import="org.liferay.jukebox.search.ArtistSearch" %>
<%@ page import="org.liferay.jukebox.search.SongDisplayTerms" %>
<%@ page import="org.liferay.jukebox.search.SongSearch" %>
<%@ page import="org.liferay.jukebox.service.ArtistServiceUtil" %>
<%@ page import="org.liferay.jukebox.service.AlbumServiceUtil" %>
<%@ page import="org.liferay.jukebox.service.SongServiceUtil" %>
<%@ page import="org.liferay.jukebox.service.ArtistLocalServiceUtil" %>
<%@ page import="org.liferay.jukebox.service.AlbumLocalServiceUtil" %>
<%@ page import="org.liferay.jukebox.service.SongLocalServiceUtil" %>
<%@ page import="org.liferay.jukebox.model.Artist" %>
<%@ page import="org.liferay.jukebox.model.Album" %>
<%@ page import="org.liferay.jukebox.model.Song" %>
<%@ page import="org.liferay.jukebox.service.permission.JukeBoxPermission" %>
<%@ page import="org.liferay.jukebox.service.permission.ArtistPermission" %>
<%@ page import="org.liferay.jukebox.service.permission.AlbumPermission" %>
<%@ page import="org.liferay.jukebox.service.permission.SongPermission" %>
<%@ page import="org.liferay.jukebox.AlbumNameException" %>
<%@ page import="org.liferay.jukebox.ArtistNameException" %>
<%@ page import="org.liferay.jukebox.DuplicatedSongException" %>
<%@ page import="org.liferay.jukebox.SongNameException" %>

<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>
<%@ page import="com.liferay.portal.kernel.template.TemplateHandler" %>
<%@ page import="com.liferay.portal.kernel.template.TemplateHandlerRegistryUtil" %>
<%@ page import="com.liferay.portal.kernel.search.Document" %>
<%@ page import="com.liferay.portal.kernel.search.Field" %>
<%@ page import="com.liferay.portal.kernel.search.Hits" %>
<%@ page import="com.liferay.portal.kernel.search.Indexer" %>
<%@ page import="com.liferay.portal.kernel.search.IndexerRegistryUtil" %>
<%@ page import="com.liferay.portal.kernel.search.QueryConfig" %>
<%@ page import="com.liferay.portal.kernel.search.SearchContext" %>
<%@ page import="com.liferay.portal.kernel.search.SearchContextFactory" %>
<%@ page import="com.liferay.portal.kernel.search.SearchResult" %>
<%@ page import="com.liferay.portal.kernel.search.SearchResultUtil" %>
<%@ page import="com.liferay.portal.kernel.search.Summary" %>
<%@ page import="com.liferay.portal.kernel.util.Constants" %>
<%@ page import="com.liferay.portal.kernel.util.GetterUtil" %>
<%@ page import="com.liferay.portal.kernel.util.HtmlUtil" %>
<%@ page import="com.liferay.portal.kernel.util.MathUtil" %>
<%@ page import="com.liferay.portal.kernel.util.ParamUtil" %>
<%@ page import="com.liferay.portal.kernel.util.StringPool" %>
<%@ page import="com.liferay.portal.kernel.util.Validator" %>
<%@ page import="com.liferay.portal.kernel.xuggler.XugglerUtil" %>
<%@ page import="com.liferay.portal.kernel.workflow.WorkflowConstants" %>
<%@ page import="com.liferay.portal.kernel.portlet.LiferayWindowState" %>
<%@ page import="com.liferay.portlet.trash.util.TrashUtil" %>

<%@ page import="com.liferay.portal.security.permission.ActionKeys" %>
<%@ page import="com.liferay.portal.util.PortalUtil" %>

<%@ page import="com.liferay.portlet.portletdisplaytemplate.util.PortletDisplayTemplateUtil" %>

<%@ page import="java.text.Format" %>
<%@ page import="java.text.DateFormat" %>

<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Comparator" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.GregorianCalendar" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.TimeZone" %>

<%@ page import="javax.portlet.PortletURL" %>

<%@ page import="javax.portlet.PortletMode" %>
<%@ page import="javax.portlet.PortletRequest" %>
<%@ page import="javax.portlet.PortletPreferences" %>
<%@ page import="javax.portlet.WindowState" %>

<portlet:defineObjects />

<liferay-theme:defineObjects />