/*******************************************************************************
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
 *
 *******************************************************************************/
package com.liferay;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.journal.service.JournalArticleLocalService;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

import java.io.IOException;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;


public class LegacyAPIsAntPortlet extends MVCPortlet
{

    @Override
    public void doView( RenderRequest renderRequest, RenderResponse renderResponse ) throws IOException,
        PortletException
    {
        try
        {
            long groupId = PortalUtil.getScopeGroupId( renderRequest );
            int count = JournalArticleLocalServiceUtil.getArticlesCount( groupId );

            renderRequest.setAttribute( "groupJournalArticlesCount", count );

            JournalArticleLocalService service = JournalArticleLocalServiceUtil.getService();

            int journalArticlesCount = service.getJournalArticlesCount();
            renderRequest.setAttribute( "journalArticlesCount", journalArticlesCount );
        }
        catch( PortalException | SystemException e )
        {
            e.printStackTrace();
        }

        super.doView( renderRequest, renderResponse );
    }
}
