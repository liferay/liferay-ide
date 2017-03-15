/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay IDE ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
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
public interface Notification extends Node
{
    ElementType TYPE = new ElementType( Notification.class );

    // *** Template ***

    @XmlBinding( path = "template" )
    @Label( standard = "&template" )
    @Required
    @LongString
    ValueProperty PROP_TEMPLATE = new ValueProperty( TYPE, "Template" );

    Value<String> getTemplate();

    void setTemplate( String value );

    @Type( base = TemplateLanguageType.class )
    @Label( standard = "template language" )
    @XmlBinding( path = "template-language" )
    @Required
    // @Service( impl = DefaultTemplateLanguageService.class )
    ValueProperty PROP_TEMPLATE_LANGUAGE = new ValueProperty( TYPE, "TemplateLanguage" );

    Value<TemplateLanguageType> getTemplateLanguage();

    void setTemplateLanguage( String templateLanguage );

    void setTemplateLanguage( TemplateLanguageType templateLanguage );

    @Label( standard = "notification types" )
    @ReadOnly
    @Derived
    @Service( impl = NotificationTypesDerivedValueService.class )
    ValueProperty PROP_NOTIFICATION_TYPES = new ValueProperty( TYPE, "NotificationTypes" );

    Value<String> getNotificationTypes();

    // *** NotificationTypes ***

    @Type( base = NotificationTransport.class )
    @Label( standard = "notification transports" )
    @Required
    @Length( min = 1 )
    @XmlListBinding
    (
        mappings = @XmlListBinding.Mapping
        (
            element = "notification-type",
            type = NotificationTransport.class
        )
    )
    ListProperty PROP_NOTIFICATION_TRANSPORTS = new ListProperty( TYPE, "NotificationTransports" );

    ElementList<NotificationTransport> getNotificationTransports();

    @Type( base = Address.class )
    @Label( standard = "addresses" )
    @XmlListBinding
    (
        path = "recipients",
        mappings = @XmlListBinding.Mapping
        (
            element = "address",
            type = Address.class
        )
    )
    ListProperty PROP_ADDRESSES = new ListProperty( TYPE, "Addresses" );

    ElementList<Address> getAddresses();

    // *** Roles ***

    @Type( base = Role.class )
    @Label( standard = "roles" )
    @XmlListBinding( path = "recipients/roles", mappings = @XmlListBinding.Mapping( element = "role", type = Role.class ) )
    ListProperty PROP_ROLES = new ListProperty( TYPE, "Roles" );

    ElementList<Role> getRoles();

    // *** Users ***

    @Type( base = User.class )
    @Label( standard = "users" )
    @XmlListBinding( path = "recipients", mappings = @XmlListBinding.Mapping( element = "user", type = User.class ) )
    ListProperty PROP_USERS = new ListProperty( TYPE, "Users" );

    ElementList<User> getUsers();

}
