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
package com.liferay.ide.project.core.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.sapphire.PossibleValuesService;


/**
 * @author Simon Jiang
 */
public class NewLiferayComponentModelClassPossibleValuesService extends PossibleValuesService
{
	private final static String[] MODEL_CLASS_VALUES = 
	{
			"com.liferay.portal.kernel.model.Account",
			"com.liferay.portal.kernel.model.Address",
			"com.liferay.announcements.kernel.model.AnnouncementsDelivery",
			"com.liferay.announcements.kernel.model.AnnouncementsEntry",
			"com.liferay.AnnouncementsFlag.kernel.model.AnnouncementsFlag",
			"com.liferay.asset.kernel.model.AssetCategory",
			"com.liferay.asset.kernel.model.AssetCategoryProperty",
			"com.liferay.asset.kernel.model.AssetEntry",
			"com.liferay.asset.kernel.model.AssetLink",
			"com.liferay.asset.kernel.model.AssetTag",
			"com.liferay.asset.kernel.model.AssetTagStats",
			"com.liferay.asset.kernel.model.AssetVocabulary",
			"com.liferay.blogs.kernel.model.BlogsEntry",
			"com.liferay.blogs.kernel.model.BlogsStatsUser",
			"com.liferay.portal.kernel.model.BrowserTracker",
			"com.liferay.portal.kernel.model.ClassName",
			"com.liferay.portal.kernel.model.ClusterGroup",
			"com.liferay.portal.kernel.model.Company",
			"com.liferay.portal.kernel.model.Contact",
			"com.liferay.counter.kernel.model.Counter",
			"com.liferay.portal.kernel.model.Country",
			"com.liferay.document.kernel.model.DLContent",
			"com.liferay.document.kernel.model.DLFileEntryMetadata",
			"com.liferay.document.kernel.model.DLFileEntry",
			"com.liferay.document.kernel.model.DLFileEntryType",
			"com.liferay.document.kernel.model.DLFileRank",
			"com.liferay.document.kernel.model.DLFileShortcut",
			"com.liferay.document.kernel.model.DLFileVersion",
			"com.liferay.document.kernel.model.DLFolder",
			"com.liferay.document.kernel.model.DLSyncEvent",
			"com.liferay.portal.kernel.model.Dummy",
			"com.liferay.portal.kernel.model.EmailAddress",
			"com.liferay.expando.kernel.model.ExpandoColumn",
			"com.liferay.expando.kernel.model.ExpandoRow",
			"com.liferay.expando.kernel.model.ExpandoTable",
			"com.liferay.expando.kernel.model.ExpandoValue",
			"com.liferay.exportimport.kernel.model.ExportImportConfiguration",
			"com.liferay.portal.kernel.model.Group",
			"com.liferay.portal.kernel.model.StagedGroup",
			"com.liferay.portal.kernel.model.Image",
			"com.liferay.portal.kernel.model.LayoutBranch",
			"com.liferay.portal.kernel.model.LayoutFriendlyURL",
			"com.liferay.portal.kernel.model.Layout",
			"com.liferay.portal.kernel.model.LayoutPrototype",
			"com.liferay.portal.kernel.model.LayoutRevision",
			"com.liferay.portal.kernel.model.LayoutSetBranch",
			"com.liferay.portal.kernel.model.LayoutSet",
			"com.liferay.portal.kernel.model.LayoutSetPrototype",
			"com.liferay.portal.kernel.model.ListType",
			"com.liferay.messages.boards.kernel.model.MBBan",
			"com.liferay.messages.boards.kernel.model.MBCategory",
			"com.liferay.messages.boards.kernel.model.MBDiscussion",
			"com.liferay.messages.boards.kernel.model.MBMailingList",
			"com.liferay.messages.boards.kernel.model.MBMessage",
			"com.liferay.messages.boards.kernel.model.MBStatsUser",
			"com.liferay.messages.boards.kernel.model.MBThreadFlag",
			"com.liferay.messages.boards.kernel.model.MBThread",
			"com.liferay.portal.kernel.model.MembershipRequest",
			"com.liferay.portal.kernel.model.Organization",
			"com.liferay.portal.kernel.model.OrgGroupRole",
			"com.liferay.portal.kernel.model.OrgLabor",
			"com.liferay.portal.kernel.model.PasswordPolicy",
			"com.liferay.portal.kernel.model.PasswordPolicyRel",
			"com.liferay.portal.kernel.model.PasswordTracker",
			"com.liferay.portal.kernel.model.Phone",
			"com.liferay.portal.kernel.model.PluginSetting",
			"com.liferay.portal.kernel.model.PortalPreferences",
			"com.liferay.portal.kernel.model.PortletItem",
			"com.liferay.portal.kernel.model.Portlet",
			"com.liferay.portal.kernel.model.PortletPreferences",
			"com.liferay.ratings.kernel.model.RatingsEntry",
			"com.liferay.ratings.kernel.model.RatingsStats",
			"com.liferay.portal.kernel.model.RecentLayoutBranch",
			"com.liferay.portal.kernel.model.RecentLayoutRevision",
			"com.liferay.portal.kernel.model.RecentLayoutSetBranch",
			"com.liferay.portal.kernel.model.Region",
			"com.liferay.portal.kernel.model.Release",
			"com.liferay.portal.kernel.model.RepositoryEntry",
			"com.liferay.portal.kernel.model.Repository",
			"com.liferay.portal.kernel.model.ResourceAction",
			"com.liferay.portal.kernel.model.ResourceBlock",
			"com.liferay.portal.kernel.model.ResourceBlockPermission",
			"com.liferay.portal.kernel.model.ResourcePermission",
			"com.liferay.portal.kernel.model.ResourceTypePermission",
			"com.liferay.portal.kernel.model.Role",
			"com.liferay.portal.kernel.model.ServiceComponent",
			"com.liferay.social.kernel.model.SocialActivityAchievement",
			"com.liferay.social.kernel.model.SocialActivityCounter",
			"com.liferay.social.kernel.model.SocialActivityLimit",
			"com.liferay.social.kernel.model.SocialActivity",
			"com.liferay.social.kernel.model.SocialActivitySet",
			"com.liferay.social.kernel.model.SocialActivitySetting",
			"com.liferay.social.kernel.model.SocialRelation",
			"com.liferay.social.kernel.model.SocialRequest",
			"com.liferay.portal.kernel.model.Subscription",
			"com.liferay.portal.kernel.model.SystemEvent",
			"com.liferay.portal.kernel.model.Team",
			"com.liferay.portal.kernel.model.Ticket",
			"com.liferay.trash.kernel.model.TrashEntry",
			"com.liferay.trash.kernel.model.TrashVersion",
			"com.liferay.portal.kernel.model.UserGroupGroupRole",
			"com.liferay.portal.kernel.model.UserGroup",
			"com.liferay.portal.kernel.model.UserGroupRole",
			"com.liferay.portal.kernel.model.UserIdMappe",
			"com.liferay.portal.kernel.model.User",
			"com.liferay.portal.kernel.model.UserNotificationDelivery",
			"com.liferay.portal.kernel.model.UserNotificationEvent",
			"com.liferay.portal.kernel.model.UserTracker",
			"com.liferay.portal.kernel.model.UserTrackerPath",
			"com.liferay.portal.kernel.model.VirtualHost",
			"com.liferay.portal.kernel.model.WebDAVProps",
			"com.liferay.portal.kernel.model.Website",
			"com.liferay.portal.kernel.model.WorkflowDefinitionLink",
			"com.liferay.portal.kernel.model.WorkflowInstanceLink"
	};

    private List<String> possibleValues;

    @Override
    protected void initPossibleValuesService()
    {
        super.initPossibleValuesService();

        possibleValues = new ArrayList<String>();

        for( final String modelClass : Arrays.asList( MODEL_CLASS_VALUES ) )
        {
            possibleValues.add( modelClass );
        }

        Collections.sort( possibleValues );
    }

    @Override
    protected void compute( Set<String> values )
    {
        values.addAll( this.possibleValues );
    }
}
