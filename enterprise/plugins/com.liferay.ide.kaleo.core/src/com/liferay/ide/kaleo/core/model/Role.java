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

import com.liferay.ide.kaleo.core.model.internal.RoleEditModeBinding;
import com.liferay.ide.kaleo.core.model.internal.RoleNamePossibleValuesMetaService;
import com.liferay.ide.kaleo.core.model.internal.RoleNamePossibleValuesService;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Service.Context;
import org.eclipse.sapphire.modeling.annotations.Services;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public interface Role extends Element
{
    ElementType TYPE = new ElementType( Role.class );

    // *** role-id ***

    @Type( base = Integer.class )
    @Label( standard = "&role-id" )
    @XmlBinding( path = "role-id" )
    ValueProperty PROP_ROLE_ID = new ValueProperty( TYPE, "RoleId" );

    Value<Integer> getRoleId();
    void setRoleId( String val );
    void setRoleId( Integer val );

    // *** role-type

    @Type( base = RoleType.class )
    @Label( standard = "role type" )
    @XmlBinding( path = "role-type" )
    @DefaultValue( text = "regular" )
    ValueProperty PROP_ROLE_TYPE = new ValueProperty( TYPE, "RoleType" );

    Value<RoleType> getRoleType();
    void setRoleType( String roleType );
    void setRoleType( RoleType roleType );

    // *** Name ***

    @XmlBinding( path = "name" )
    @Label( standard = "&name" )
    @Required
    @Services
    (
         value = 
         { 
             @Service( impl = RoleNamePossibleValuesMetaService.class, context = Context.METAMODEL ),
             @Service( impl = RoleNamePossibleValuesService.class )
         }
    )
    ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" );

    Value<String> getName();
    void setName( String value );

    // *** Auto create ***

    @Type( base = Boolean.class )
    @XmlBinding( path = "auto-create" )
    @Label( standard = "&auto-create" )
    ValueProperty PROP_AUTO_CREATE = new ValueProperty( TYPE, "AutoCreate" );

    Value<Boolean> getAutoCreate();
    void setAutoCreate( String value );
    void setAutoCreate( Boolean value );

    @Type( base = RoleEditMode.class )
    @Label( standard = "edit mode" )
    @CustomXmlValueBinding( impl = RoleEditModeBinding.class )
    ValueProperty PROP_EDIT_MODE = new ValueProperty( TYPE, "EditMode" );

    Value<RoleEditMode> getEditMode();
    void setEditMode( String editMode );
    void setEditMode( RoleEditMode editMode );

}
