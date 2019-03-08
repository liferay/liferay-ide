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

package com.liferay.blade.upgrade.liferay71.apichanges;

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.upgrade.PropertiesFileMigrator;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Charles Wu
 */
@Component(property = {
	"file.extensions=properties", "problem.title=Moved CAPTCHA Portal Properties to OSGi Configuration",
	"problem.summary=The CAPTCHA properties have been moved from portal.properties", "problem.tickets=LPS-67830",
	"problem.section=#moved-captcha-portal-properties", "version=7.1"
},
	service = FileMigrator.class)
public class MovedCaptchaPortalProperties extends PropertiesFileMigrator {

	@Override
	protected void addPropertiesToSearch(List<String> properties) {
		properties.add("captcha.max.challenges");
		properties.add("captcha.check.portal.create_account");
		properties.add("captcha.check.portal.send_password");
		properties.add("captcha.check.portlet.message_boards.edit_category");
		properties.add("captcha.check.portlet.message_boards.edit_message");
		properties.add("captcha.engine.impl");
		properties.add("captcha.engine.recaptcha.key.private");
		properties.add("captcha.engine.recaptcha.key.public");
		properties.add("captcha.engine.recaptcha.url.script");
		properties.add("captcha.engine.recaptcha.url.noscript");
		properties.add("captcha.engine.recaptcha.url.verify");
		properties.add("captcha.engine.simplecaptcha.height");
		properties.add("captcha.engine.simplecaptcha.width");
		properties.add("captcha.engine.simplecaptcha.background.producers");
		properties.add("captcha.engine.simplecaptcha.gimpy.renderers");
		properties.add("captcha.engine.simplecaptcha.noise.producers");
		properties.add("captcha.engine.simplecaptcha.text.producers");
		properties.add("captcha.engine.simplecaptcha.word.renderers");
	}

}