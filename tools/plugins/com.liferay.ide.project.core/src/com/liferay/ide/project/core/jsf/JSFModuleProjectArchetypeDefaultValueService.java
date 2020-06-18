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

package com.liferay.ide.project.core.jsf;

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Simon Jiang
 * @author Seiphon Wang
 */
public class JSFModuleProjectArchetypeDefaultValueService
	extends DefaultValueService implements SapphireContentAccessor {

	@Override
	public void dispose() {
		NewLiferayJSFModuleProjectOp op = _op();

		SapphireUtil.detachListener(op.property(NewLiferayJSFModuleProjectOp.PROP_TEMPLATE_NAME), _listener);

		super.dispose();
	}

	@Override
	protected String compute() {
		return _getDefaultValue();
	}

	@Override
	protected void initDefaultValueService() {
		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(final PropertyContentEvent event) {
				refresh();
			}

		};

		NewLiferayJSFModuleProjectOp op = _op();

		SapphireUtil.attachListener(op.property(NewLiferayJSFModuleProjectOp.PROP_TEMPLATE_NAME), _listener);
	}

	private String _getArchetypeGAVFromUrl(String url) {
		try {
			Connection connection = Jsoup.connect(url);

			connection = connection.timeout(10000);

			connection = connection.validateTLSCertificates(false);

			Document document = connection.get();

			Element archetype = document.getElementById(_archetype_element_id);

			Elements spans = archetype.getElementsByTag("span");

			Element span = spans.get(1);

			String content = span.toString();

			String[] values = content.split("<br>");

			String groupId = "";
			String artifactId = "";
			String version = "";

			for (String value : values) {
				if (value.contains("-DarchetypeGroupId=")) {
					groupId = value.substring(value.indexOf("=") + 1, value.indexOf("\\"));

					groupId = groupId.trim();

					continue;
				}

				if (value.contains("-DarchetypeArtifactId=")) {
					artifactId = value.substring(value.indexOf("=") + 1, value.indexOf("\\"));

					artifactId = artifactId.trim();

					continue;
				}

				if (value.contains("-DarchetypeVersion=")) {
					version = value.substring(value.indexOf("=") + 1, value.indexOf("\\"));

					version = version.trim();

					continue;
				}
			}

			StringBuffer sb = new StringBuffer();

			sb.append(groupId);
			sb.append(":");
			sb.append(artifactId);
			sb.append(":");
			sb.append(version);

			return sb.toString();
		}
		catch (Exception e) {
			return null;
		}
	}

	private String _getDefaultValue() {
		NewLiferayJSFModuleProjectOp op = _op();

		StringBuffer sb = new StringBuffer(
			"https://faces.liferay.dev/home/-/archetype-portlet/liferay-portal-version/");

		sb.append(get(op.getLiferayVersion()));

		sb.append("/jsf-version/2.2/component-suite/");

		String templateName = get(op.getTemplateName());

		sb.append(templateName);

		sb.append("/build-tool/maven");

		return _getArchetypeGAVFromUrl(sb.toString());
	}

	private NewLiferayJSFModuleProjectOp _op() {
		return context(NewLiferayJSFModuleProjectOp.class);
	}

	private String _archetype_element_id = "_1_WAR_comliferayfacessitearchetypeportlet_:form:command";
	private FilteredListener<PropertyContentEvent> _listener;

}