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

package com.liferay.ide.kaleo.core.model;

import com.liferay.ide.kaleo.core.model.internal.EmailAddressValidationService;
import com.liferay.ide.kaleo.core.op.internal.UserValidationService;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Gregory Amerson
 */
@Image(path = "images/user_16x16.gif")
public interface User extends Element {

	public ElementType TYPE = new ElementType(User.class);

	public Value<String> getEmailAddress();

	public Value<String> getScreenName();

	public Value<Integer> getUserId();

	public void setEmailAddress(String value);

	public void setScreenName(String value);

	public void setUserId(Integer val);

	public void setUserId(String val);

	@Label(standard = "&email address")
	@Service(impl = EmailAddressValidationService.class)
	@XmlBinding(path = "email-address")
	public ValueProperty PROP_EMAIL_ADDRESS = new ValueProperty(TYPE, "EmailAddress");

	@Label(standard = "&screen name")
	@XmlBinding(path = "screen-name")
	public ValueProperty PROP_SCREEN_NAME = new ValueProperty(TYPE, "ScreenName");

	@Label(standard = "&user-id")
	@Service(impl = UserValidationService.class)
	@Type(base = Integer.class)
	@XmlBinding(path = "user-id")
	public ValueProperty PROP_USER_ID = new ValueProperty(TYPE, "UserId");

}