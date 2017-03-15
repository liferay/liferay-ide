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

package com.liferay.ide.kaleo.core.model.internal;

import com.liferay.ide.kaleo.core.model.Role;
import com.liferay.ide.kaleo.core.model.RoleEditMode;
import com.liferay.ide.kaleo.core.model.RoleType;

import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;


/**
 * @author Gregory Amerson
 */
public class RoleEditModeBinding extends XmlValueBindingImpl
{

    private RoleEditMode localMode = null;

    @Override
    public String read()
    {
        if ( localMode == null )
        {
            Role role = role();

            Integer roleId = role.getRoleId().content();

            return roleId != null ? RoleEditMode.BY_ROLE_ID.toString() : RoleEditMode.BY_NAME.toString();
        }
        else
        {
            return localMode.toString();
        }
    }

    protected Role role()
    {
        return property().nearest( Role.class );
    }

    @Override
    public void write( String value )
    {
        if ( RoleEditMode.BY_NAME.toString().equals( value ) )
        {
            localMode = RoleEditMode.BY_NAME;
            role().setRoleId( (String) null );
        }
        else
        {
            localMode = RoleEditMode.BY_ROLE_ID;
            role().setName( null );
            role().setRoleType( (RoleType) null );
            role().setAutoCreate( (Boolean) null );
        }
    }

}
