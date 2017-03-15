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

import com.liferay.ide.kaleo.core.model.Assignable;
import com.liferay.ide.kaleo.core.model.ResourceAction;
import com.liferay.ide.kaleo.core.model.Role;
import com.liferay.ide.kaleo.core.model.Scriptable;
import com.liferay.ide.kaleo.core.model.User;

import org.eclipse.sapphire.DerivedValueService;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;


/**
 * @author Gregory Amerson
 */
public class CurrentAssignmentsDerviedValueService extends DerivedValueService
{

    @Override
    protected String compute()
    {
        StringBuilder data = new StringBuilder();

        Assignable assignable = this.context( Assignable.class );

        User user = assignable.getUser().content( false );
        ElementList<Role> roles = assignable.getRoles();
        Scriptable scriptable = assignable.getScriptedAssignment().content( false );
        ElementList<ResourceAction> resourceActions = assignable.getResourceActions();

        if( user != null )
        {
            if( user.getUserId().content() != null )
            {
                data.append( user.getUserId().content() + ", " );
            }
            else if( user.getScreenName().content() != null )
            {
                data.append( user.getScreenName().content() + ", " );
            }
            else if( user.getEmailAddress().content() != null )
            {
                data.append( user.getEmailAddress().content() + ", " );
            }
            else
            {
                data.append( "User: Asset Creator" );
            }
        }

        if( roles.size() > 0 )
        {
            data.append( "Roles: " );

            for( Role role : roles )
            {
                if( role.getRoleId().content() != null )
                {
                    data.append( role.getRoleId().content() + ", " );
                }
                else
                {
                    data.append( role.getName().text() + ", " );
                }
            }
        }

        if( scriptable != null && scriptable.getScript().content() != null )
        {
            data.append( "Script language: " + scriptable.getScriptLanguage().content() );
        }

        if( resourceActions.size() > 0 )
        {
            data.append( "Resource actions: " );

            for( ResourceAction resourceAction : resourceActions )
            {
                if( resourceAction.getResourceAction().content() != null )
                {
                    data.append( resourceAction.getResourceAction().content() + ", " );
                }
            }
        }

        return data.toString().replaceAll( ", $", "" );
    }

    @Override
    protected void initDerivedValueService()
    {
        final FilteredListener<PropertyContentEvent> listener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            public void handleTypedEvent( final PropertyContentEvent event )
            {
                refresh();
            }
        };

        context( Assignable.class ).attach( listener, "*" );
    }

}
