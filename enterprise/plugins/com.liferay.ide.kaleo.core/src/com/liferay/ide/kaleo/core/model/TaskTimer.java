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

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ImpliedElementProperty;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlElementBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Gregory Amerson
 */
@Image( path = "images/timer_16x16.png" )
public interface TaskTimer extends TimerAction
{
    ElementType TYPE = new ElementType( TaskTimer.class );

    // *** Blocking ***

    @Type( base = Boolean.class )
    @Label( standard = "&blocking" )
    @XmlBinding( path = "blocking" )
    ValueProperty PROP_BLOCKING = new ValueProperty( TYPE, "Blocking" );

    Value<Boolean> isBlocking();

    void setBlocking( String value );

    void setBlocking( Boolean value );

    // *** re-assignments node ***

    // *** resource actions ***

    @Type( base = ResourceAction.class )
    @Label( standard = "resource actions" )
    @XmlListBinding
    (
        path = "reassignments/resource-actions",
        mappings = @XmlListBinding.Mapping
        (
            element = "resource-action",
            type = ResourceAction.class
        )
    )
    ListProperty PROP_RESOURCE_ACTIONS = new ListProperty( TYPE, "ResourceActions" );

    ElementList<ResourceAction> getResourceActions();

    // *** roles ***

    @Type( base = Role.class )
    @Label( standard = "roles" )
    @XmlListBinding
    (
        path = "reassignments/roles",
        mappings = @XmlListBinding.Mapping
        (
            element = "role",
            type = Role.class
        )
    )
    ListProperty PROP_ROLES = new ListProperty( TYPE, "Roles" );

    ElementList<Role> getRoles();

    // ** scripted assignment **

    @Type( base = Scriptable.class )
    @Label( standard = "scripted assignment" )
    @XmlElementBinding( path = "reassignments/scripted-assignment" )
    ImpliedElementProperty PROP_SCRIPTED_ASSIGNMENT = new ImpliedElementProperty( TYPE, "ScriptedAssignment" );

    Scriptable getScriptedAssignment();

    // ** User **

    @Type( base = User.class )
    @Label( standard = "user" )
    @XmlElementBinding( path = "reassignments/user" )
    ImpliedElementProperty PROP_USER = new ImpliedElementProperty( TYPE, "User" );

    User getUser();

    // *** re-assignments node end ***

}
