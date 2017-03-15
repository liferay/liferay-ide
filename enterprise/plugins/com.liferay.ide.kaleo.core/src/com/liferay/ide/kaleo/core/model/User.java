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
@Image( path = "images/user_16x16.gif" )
public interface User extends Element
{
    ElementType TYPE = new ElementType( User.class );

    // *** user-id ***

    @Type( base = Integer.class )
    @Label( standard = "&user-id" )
    @XmlBinding( path = "user-id" )
    @Service( impl = UserValidationService.class )
    ValueProperty PROP_USER_ID = new ValueProperty( TYPE, "UserId" );

    Value<Integer> getUserId();
    void setUserId( String val );
    void setUserId( Integer val );

    // *** Screen Name ***

    @XmlBinding( path = "screen-name" )
    @Label( standard = "&screen name" )
    ValueProperty PROP_SCREEN_NAME = new ValueProperty( TYPE, "ScreenName" );

    Value<String> getScreenName();
    void setScreenName( String value );

    // *** Email Address ***

    @XmlBinding( path = "email-address" )
    @Label( standard = "&email address" )
    @Service( impl = EmailAddressValidationService.class )
    ValueProperty PROP_EMAIL_ADDRESS = new ValueProperty( TYPE, "EmailAddress" );

    Value<String> getEmailAddress();
    void setEmailAddress( String value );

}
