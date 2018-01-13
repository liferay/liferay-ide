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

import com.liferay.ide.kaleo.core.model.internal.NotificationTypesDerivedValueService;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Length;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Derived;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.LongString;
import org.eclipse.sapphire.modeling.annotations.ReadOnly;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Gregory Amerson
 */
@Image(path = "images/notification_16x16.png")
public interface Notification extends Node {

	public ElementType TYPE = new ElementType(Notification.class);

	public ElementList<Address> getAddresses();

	public ElementList<NotificationTransport> getNotificationTransports();

	public Value<String> getNotificationTypes();

	public ElementList<Role> getRoles();

	public Value<String> getTemplate();

	public Value<TemplateLanguageType> getTemplateLanguage();

	public ElementList<User> getUsers();

	public void setTemplate(String value);

	public void setTemplateLanguage(String templateLanguage);

	public void setTemplateLanguage(TemplateLanguageType templateLanguage);

	@Label(standard = "addresses")
	@Type(base = Address.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "address", type = Address.class), path = "recipients")
	public ListProperty PROP_ADDRESSES = new ListProperty(TYPE, "Addresses");

	@Label(standard = "notification transports")
	@Length(min = 1)
	@Required
	@Type(base = NotificationTransport.class)
	@XmlListBinding(
		mappings = @XmlListBinding.Mapping(element = "notification-type", type = NotificationTransport.class)
	)
	public ListProperty PROP_NOTIFICATION_TRANSPORTS = new ListProperty(TYPE, "NotificationTransports");

	@Derived
	@Label(standard = "notification types")
	@ReadOnly
	@Service(impl = NotificationTypesDerivedValueService.class)
	public ValueProperty PROP_NOTIFICATION_TYPES = new ValueProperty(TYPE, "NotificationTypes");

	@Label(standard = "roles")
	@Type(base = Role.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "role", type = Role.class), path = "recipients/roles")
	public ListProperty PROP_ROLES = new ListProperty(TYPE, "Roles");

	@Label(standard = "&template")
	@LongString
	@Required
	@XmlBinding(path = "template")
	public ValueProperty PROP_TEMPLATE = new ValueProperty(TYPE, "Template");

	@Label(standard = "template language")
	@Required
	@Type(base = TemplateLanguageType.class)
	@XmlBinding(path = "template-language")
	public ValueProperty PROP_TEMPLATE_LANGUAGE = new ValueProperty(TYPE, "TemplateLanguage");

	@Label(standard = "users")
	@Type(base = User.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "user", type = User.class), path = "recipients")
	public ListProperty PROP_USERS = new ListProperty(TYPE, "Users");

}