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

package com.liferay.ide.upgrade.problems.core.internal.liferay70;

import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.internal.PropertiesFileMigrator;

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=properties", "problem.title=Moved Journal Portlet Properties to OSGi Configuration",
	"problem.summary=Moved Journal Portlet Properties to OSGi Configuration", "problem.tickets=LPS-58672",
	"problem.section=#moved-journal-portlet-properties-to-osgi-configuration", "implName=JournalPortletProperties",
	"version=7.0"
},
	service = FileMigrator.class)
public class JournalPortletProperties extends PropertiesFileMigrator {

	@Override
	protected void addPropertiesToSearch(List<String> properties) {
		properties.add("journal.display.views");
		properties.add("journal.default.display.view");
		properties.add("journal.article.expire.all.versions");
		properties.add("journal.article.form.add");
		properties.add("journal.article.form.default.values");
		properties.add("journal.article.form.translate");
		properties.add("journal.article.form.update");
		properties.add("journal.article.force.autogenerate.id");
		properties.add("journal.article.types");
		properties.add("journal.article.token.page.break");
		properties.add("journal.article.check.interval");
		properties.add("journal.article.storage.type");
		properties.add("journal.article.view.permission.check.enabled");
		properties.add("journal.article.comments.enabled");
		properties.add("journal.article.database.keyword.search.content");
		properties.add("journal.feed.force.autogenerate.id");
		properties.add("journal.transformer.listener");
		properties.add("journal.publish.to.live.by.default");
		properties.add("journal.publish.version.history.by.default");
		properties.add("journal.sync.content.search.on.startup");
		properties.add("journal.email.from.name");
		properties.add("journal.email.from.address");
		properties.add("journal.email.article.added.enabled");
		properties.add("journal.email.article.added.subject");
		properties.add("journal.email.article.added.body");
		properties.add("journal.email.article.approval.denied.enabled");
		properties.add("journal.email.article.approval.denied.subject");
		properties.add("journal.email.article.approval.denied.body");
		properties.add("journal.email.article.approval.granted.enabled");
		properties.add("journal.email.article.approval.granted.subject");
		properties.add("journal.email.article.approval.granted.body");
		properties.add("journal.email.article.approval.requested.enabled");
		properties.add("journal.email.article.approval.requested.subject");
		properties.add("journal.email.article.approval.requested.body");
		properties.add("journal.email.article.review.enabled");
		properties.add("journal.email.article.review.subject");
		properties.add("journal.email.article.review.body");
		properties.add("journal.email.article.updated.enabled");
		properties.add("journal.email.article.updated.subject");
		properties.add("journal.email.article.updated.body");
		properties.add("journal.lar.creation.strategy");
		properties.add("journal.error.template[ftl]");
		properties.add("journal.error.template[vm]");
		properties.add("journal.error.template[xsl]");
		properties.add("journal.articles.page.delta.values");
		properties.add("journal.articles.search.with.index");
		properties.add("journal.articles.index.all.versions");
		properties.add("journal.content.publish.to.live.by.default");
		properties.add("journal.content.search.show.listed");
	}

}