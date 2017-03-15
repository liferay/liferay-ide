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

package com.liferay.ide.kaleo.core.op;

import com.liferay.ide.kaleo.core.model.Assignable;
import com.liferay.ide.kaleo.core.model.AssignmentType;
import com.liferay.ide.kaleo.core.model.Role;
import com.liferay.ide.kaleo.core.model.RoleName;
import com.liferay.ide.kaleo.core.model.ScriptLanguageType;
import com.liferay.ide.kaleo.core.model.Scriptable;
import com.liferay.ide.kaleo.core.model.User;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ImpliedElementProperty;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;

/**
 * @author Gregory Amerson
 */
public interface AssignableOp extends Assignable
{

    ElementType TYPE = new ElementType( AssignableOp.class );

    @Type( base = AssignmentType.class )
    @Required
    @DefaultValue( text = "CREATOR" )
    ValueProperty PROP_ASSIGNMENT_TYPE = new ValueProperty( TYPE, "AssignmentType" );

    @Type( base = Role.class )
    @Label( standard = "roles" )
    ImpliedElementProperty PROP_IMPLIED_ROLE = new ImpliedElementProperty( TYPE, "ImpliedRole" );

    @Type( base = User.class )
    @Label( standard = "user" )
    ImpliedElementProperty PROP_IMPLIED_USER = new ImpliedElementProperty( TYPE, "ImpliedUser" );

    @Type( base = Scriptable.class )
    ImpliedElementProperty PROP_IMPLIED_SCRIPTABLE = new ImpliedElementProperty( TYPE, "ImpliedScriptable" );

    @Type( base = RoleName.class )
    ListProperty PROP_ROLE_NAMES = new ListProperty( TYPE, "RoleNames" );

    @Type( base = ScriptLanguageType.class )
    @Label( standard = "default &script type" )
    @DefaultValue( text = "groovy" )
    ValueProperty PROP_DEFAULT_SCRIPT_LANGUAGE = new ValueProperty( TYPE, "DefaultScriptLanguage" );

    Value<AssignmentType> getAssignmentType();

    void setAssignmentType( String value );

    void setAssignmentType( AssignmentType value );

    Role getImpliedRole();

    User getImpliedUser();

    Scriptable getImpliedScriptable();

    ElementList<RoleName> getRoleNames();

    Value<ScriptLanguageType> getDefaultScriptLanguage();

    void setDefaultScriptLanguage( String scriptLanguage );

    void setDefaultScriptLanguage( ScriptLanguageType scriptLanguage );
}
