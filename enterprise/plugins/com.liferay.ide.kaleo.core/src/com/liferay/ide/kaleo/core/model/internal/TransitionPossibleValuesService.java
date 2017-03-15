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

import static com.liferay.ide.core.util.CoreUtil.empty;

import com.liferay.ide.kaleo.core.model.Node;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;

import java.util.Set;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.Version;

/**
 * @author Gregory Amerson
 */
public class TransitionPossibleValuesService extends PossibleValuesService
{

    @Override
    protected void compute( Set<String> values )
    {
        // if we are a task return states and tasks, if we are a state, find tasks.
        Element modelElement = context( Element.class );

        WorkflowDefinition workflow = modelElement.nearest( WorkflowDefinition.class );

        if( workflow == null )
        {
            workflow = modelElement.adapt( WorkflowDefinition.class );
        }

        if( workflow != null )
        {
            addNodeNames( values, workflow.getTasks() );
            addNodeNames( values, workflow.getStates() );
            addNodeNames( values, workflow.getConditions() );
            addNodeNames( values, workflow.getForks() );
            addNodeNames( values, workflow.getJoins() );

            final Version version = workflow.getSchemaVersion().content();

            if( version.compareTo( new Version( "6.2" ) ) >= 0 )
            {
                addNodeNames( values, workflow.getJoinXors() );
            }

        }
    }

    protected void addNodeNames( Set<String> values, ElementList<?> nodeList )
    {
        Node[] nodes = nodeList.toArray( new Node[0] );

        for( Node node : nodes )
        {
            String name = node.getName().content();

            if( !empty( name ) )
            {
                values.add( name );
            }
        }
    }

}
