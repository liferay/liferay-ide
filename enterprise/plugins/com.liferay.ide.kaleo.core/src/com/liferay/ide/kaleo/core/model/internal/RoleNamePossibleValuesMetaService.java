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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.kaleo.core.model.Assignable;
import com.liferay.ide.kaleo.core.model.Role;
import com.liferay.ide.kaleo.core.model.RoleName;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.core.model.WorkflowNode;
import com.liferay.ide.kaleo.core.op.AssignableOp;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.services.Service;


/**
 * @author Kuo Zhang
 */
public class RoleNamePossibleValuesMetaService extends Service
{
    private final Map<String, Integer> additionalRoleNames = new HashMap<String, Integer>();
    private final static Set<String> originalRoleNames = new TreeSet<String>();
    static
    {
        originalRoleNames.add( "Organization Administrator" );
        originalRoleNames.add( "Organization Content Reviewer" );
        originalRoleNames.add( "Organization Owner" );
        originalRoleNames.add( "Administrator" );
        originalRoleNames.add( "Portal Content Reviewer" );
        originalRoleNames.add( "Site Administrator" );
        originalRoleNames.add( "Site Content Reviewer" );
        originalRoleNames.add( "Site Owner" );
    }

    protected void initIfNecessary( Object object )
    {
        if( object instanceof WorkflowDefinition )
        {
            for( WorkflowNode node : ( (WorkflowDefinition) object ).getDiagramNodes() )
            {
                final Assignable assignable = node.nearest( Assignable.class );

                if( assignable != null )
                {
                    for( Role role : assignable.getRoles() )
                    {
                        final String name = role.getName().content( false );

                        if( ! CoreUtil.isNullOrEmpty( name ) )
                        {
                            originalRoleNames.add( name );
                        }
                    }
                }
            }
        }
        else if( object instanceof AssignableOp )
        {
            ElementList<RoleName> roleNames = ( (AssignableOp) object ).getRoleNames();

            for( RoleName roleName : roleNames )
            {
                final String name = roleName.getName().content( false );

                if( ! CoreUtil.isNullOrEmpty( name ) )
                {
                    originalRoleNames.add( name );
                }
            }
        }
    }

    protected String[] getRoleNames()
    {
        Set<String> retval = new TreeSet<String>();

        for( String roleName : additionalRoleNames.keySet() )
        {
            if( additionalRoleNames.get( roleName ).intValue() > 0 )
            {
                retval.add( roleName );
            }
        }

        retval.addAll( originalRoleNames );

        return retval.toArray( new String[0] );
    }

    protected void updateRoleNames( String previousRoleName, String currentRoleName )
    {
        if( previousRoleName != null && currentRoleName != null && previousRoleName.equals( currentRoleName ) )
        {
            return;
        }

        boolean needsBroadcast = false;

        if( ! CoreUtil.isNullOrEmpty( previousRoleName ) && ! originalRoleNames.contains( previousRoleName ) )
        {
            int times = additionalRoleNames.containsKey( previousRoleName ) ?
                        additionalRoleNames.get( previousRoleName ).intValue() : 0;

            if( times >= 1 )
            {
                additionalRoleNames.put( previousRoleName, new Integer( --times ) );
            }

            needsBroadcast = ( times == 0 );
        }

        if( ! CoreUtil.isNullOrEmpty( currentRoleName ) && ! originalRoleNames.contains( currentRoleName ) )
        {
            int times = additionalRoleNames.containsKey( currentRoleName ) ?
                        additionalRoleNames.get( currentRoleName ).intValue() : 0;

            additionalRoleNames.put( currentRoleName, new Integer( ++times ) );

            needsBroadcast = needsBroadcast ? true : ( times == 1 );
        }

        if( needsBroadcast )
        {
            broadcast();
        }
    }
}
