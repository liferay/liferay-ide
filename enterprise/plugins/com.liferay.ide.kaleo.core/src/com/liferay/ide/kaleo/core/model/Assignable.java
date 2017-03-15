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

import com.liferay.ide.kaleo.core.model.internal.CurrentAssignmentsDerviedValueService;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementProperty;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Derived;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.ReadOnly;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.XmlElementBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Gregory Amerson
 */
public interface Assignable extends Element
{
    ElementType TYPE = new ElementType( Assignable.class );

    // *** resource actions ***

    @Type( base = ResourceAction.class )
    @Label( standard = "resource actions" )
    @XmlListBinding
    (
        path = "assignments/resource-actions",
        mappings =
        {
            @XmlListBinding.Mapping
            (
                element = "resource-action", type = ResourceAction.class
            )
        }
    )
    ListProperty PROP_RESOURCE_ACTIONS = new ListProperty( TYPE, "ResourceActions" );

    ElementList<ResourceAction> getResourceActions();

    // // *** roles ***

    @Type( base = Role.class )
    @Label( standard = "roles" )
    @XmlListBinding
    (
        path = "assignments/roles",
        mappings =
        {
            @XmlListBinding.Mapping
            (
                element = "role", type = Role.class
            )
        }
    )
    ListProperty PROP_ROLES = new ListProperty( TYPE, "Roles" );

    ElementList<Role> getRoles();

    //** scripted assignment **

    @Type( base = Scriptable.class )
    @Label( standard = "scripted assignment" )
    @XmlElementBinding
    (
        path = "assignments",
        mappings =
        {
            @XmlElementBinding.Mapping
            (
                element = "scripted-assignment", type = Scriptable.class
            )
        }
    )
    ElementProperty PROP_SCRIPTED_ASSIGNMENT = new ElementProperty( TYPE, "ScriptedAssignment" );

    ElementHandle<Scriptable> getScriptedAssignment();

    // // ** User **

    @Type( base = User.class )
    @Label( standard = "user" )
    @XmlElementBinding
    (
        path = "assignments",
        mappings =
        {
            @XmlElementBinding.Mapping
            (
                element="user", type=User.class
            )
        }
    )
    ElementProperty PROP_USER = new ElementProperty( TYPE, "User" );

    ElementHandle<User> getUser();

    // *** CurrentAssignments ***

    @Label( standard = "current assignments" )
    @ReadOnly
    @Service( impl = CurrentAssignmentsDerviedValueService.class )
    @Derived
    ValueProperty PROP_CURRENT_ASSIGNMENTS = new ValueProperty( TYPE, "CurrentAssignments" );

    Value<String> getCurrentAssignments();

}
