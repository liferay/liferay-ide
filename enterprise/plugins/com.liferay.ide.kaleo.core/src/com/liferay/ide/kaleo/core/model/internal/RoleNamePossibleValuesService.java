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

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.kaleo.core.model.Role;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.core.op.AssignableOp;

import java.util.Set;

import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Status;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class RoleNamePossibleValuesService extends PossibleValuesService
{

    private RoleNamePossibleValuesMetaService metaService;
    private String previousRoleName;
    private String currentRoleName;
    private Listener metaRoleNamesListener;
    private Listener roleNameListener;

    @Override
    protected void initPossibleValuesService()
    {
        invalidValueSeverity = Status.Severity.OK;
        metaService = context().service( RoleNamePossibleValuesMetaService.class );
        previousRoleName = StringPool.EMPTY;
        currentRoleName = StringPool.EMPTY;

        initMetaServiceIfNecessary();

        metaRoleNamesListener = new FilteredListener<Event>()
        {
            @Override
            protected void handleTypedEvent( Event event )
            {
                if( !context( Role.class ).disposed() )
                {
                    try
                    {
                        refresh();
                    }
                    catch( Exception e )
                    {
                        // The previous refreshing is not done; 
                    }
                }
            }
        };

        roleNameListener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                if( ! event.property().element().disposed() )
                {
                    currentRoleName = event.property().element().nearest( Role.class ).getName().content();
                    metaService.updateRoleNames( previousRoleName, currentRoleName );
                    previousRoleName = currentRoleName;
                }
            }
        };

        metaService.attach( metaRoleNamesListener );
        op().getName().attach( roleNameListener );

        super.initPossibleValuesService();
    }

    @Override
    protected void compute( Set<String> values )
    {
        for( String roleName : metaService.getRoleNames() )
        {
            values.add( roleName );
        }
    }

    @Override
    public void dispose()
    {
        metaService.updateRoleNames( previousRoleName, StringPool.EMPTY );

        if( op() != null && ! op().disposed() )
        {
            op().getName().detach( roleNameListener );
        }

        metaService.detach( metaRoleNamesListener );

        previousRoleName = null;
        currentRoleName = null;
        roleNameListener = null;
        metaRoleNamesListener = null;
        metaService = null;

        super.dispose();
    }

    private void initMetaServiceIfNecessary()
    {
        final WorkflowDefinition definition = op().nearest( WorkflowDefinition.class );

        if( definition != null )
        { 
            metaService.initIfNecessary( definition );
        }

        final AssignableOp assignableOp = op().nearest( AssignableOp.class );

        if( assignableOp != null )
        {
            metaService.initIfNecessary( assignableOp );
        }
    }

    private Role op()
    {
        return context( Role.class );
    }
}
