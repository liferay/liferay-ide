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

package com.liferay.ide.kaleo.core.util;

import com.liferay.ide.kaleo.core.model.ResourceAction;
import com.liferay.ide.kaleo.core.model.Role;
import com.liferay.ide.kaleo.core.model.Scriptable;
import com.liferay.ide.kaleo.core.model.Task;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.core.model.internal.Point;
import com.liferay.ide.kaleo.core.op.AssignableOp;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.modeling.annotations.EnumSerialization;

/**
 * @author Gregory Amerson
 */
public class KaleoModelUtil
{
    public static final Point DEFAULT_POINT = new Point( -1, -1 );

    public static void changeTaskAssignments( Task task, AssignableOp op )
    {
        if( task == null || op == null )
        {
            return;
        }

        task.getUser().clear();
        task.getScriptedAssignment().clear();
        task.getResourceActions().clear();
        task.getRoles().clear();

        switch( op.getAssignmentType().content( true ) )
        {
            case CREATOR:
                task.getUser().content( true );
                break;

            case USER:
                task.getUser().content( true ).copy( op.getImpliedUser() );
                break;

            case ROLE:
                final Role newRole = task.getRoles().insert();
                newRole.copy( op.getImpliedRole() );
                break;

            case ROLE_TYPE:
                for( Role role : op.getRoles() )
                {
                    final Role newRoleType = task.getRoles().insert();
                    newRoleType.copy( role );
                    newRoleType.setRoleType( role.getRoleType().content(true) );

                    if( role.getAutoCreate().content() != null )
                    {
                        newRoleType.setAutoCreate( role.getAutoCreate().content() );
                    }
                }
                break;

            case SCRIPTED_ASSIGNMENT:
                final Scriptable scriptable = task.getScriptedAssignment().content( true );
                scriptable.setScriptLanguage( op.getImpliedScriptable().getScriptLanguage().content( true ) );
                scriptable.setScript( "/*specify script assignment */" );
                break;

            case RESOURCE_ACTIONS:
                for( ResourceAction ra : op.getResourceActions() )
                {
                    task.getResourceActions().insert().copy( ra );
                }
                break;
        }
    }

    public static String getEnumSerializationAnnotation( Enum<?> type )
    {
        try
        {
            return type.getClass().getField( type.name() ).getAnnotation( EnumSerialization.class ).primary();
        }
        catch( Exception e )
        {
            return null;
        }
    }

    public static String getDefaultValue( Element modelElement, QualifiedName key, Enum<?> defaultValue )
    {
        String value = null;

        IFile definitionFile = null;

        WorkflowDefinition workflowDefinition = modelElement.nearest( WorkflowDefinition.class );

        if( workflowDefinition == null )
        {
            workflowDefinition = modelElement.adapt( WorkflowDefinition.class );
        }

        if( workflowDefinition != null )
        {
            definitionFile = workflowDefinition.adapt( IFile.class );
        }

        if( definitionFile != null )
        {
            try
            {
                value = definitionFile.getPersistentProperty( key );
            }
            catch( CoreException e )
            {
            }
        }

        if( value == null )
        {
            value = getEnumSerializationAnnotation( defaultValue );
        }

        return value;
    }

}