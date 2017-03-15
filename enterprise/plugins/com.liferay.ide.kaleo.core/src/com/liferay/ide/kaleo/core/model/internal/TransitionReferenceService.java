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

import com.liferay.ide.kaleo.core.model.Node;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.Version;
import org.eclipse.sapphire.services.ReferenceService;

/**
 * @author Gregory Amerson
 */
public class TransitionReferenceService extends ReferenceService<Node>
{

    @Override
    public Node compute()
    {
        final String reference = context( Value.class ).text();

        if( reference != null )
        {
            final WorkflowDefinition workflow = context( WorkflowDefinition.class );

            List<Node> nodes = new ArrayList<Node>();

            nodes.addAll( workflow.getTasks() );
            nodes.addAll( workflow.getStates() );
            nodes.addAll( workflow.getConditions() );
            nodes.addAll( workflow.getForks() );
            nodes.addAll( workflow.getJoins() );

            final Version version = workflow.getSchemaVersion().content();

            if( version.compareTo( new Version( "6.2" ) ) >= 0 )
            {
                nodes.addAll( workflow.getJoinXors() );
            }

            for( Node node : nodes )
            {
                if( reference.equals( node.getName().content() ) )
                {
                    return node;
                }
            }
        }

        return null;
    }

}
