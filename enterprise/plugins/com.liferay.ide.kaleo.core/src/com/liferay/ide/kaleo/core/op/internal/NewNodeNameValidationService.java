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

package com.liferay.ide.kaleo.core.op.internal;

import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.core.model.WorkflowNode;

import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Gregory Amerson
 */
public class NewNodeNameValidationService extends ValidationService
{

    @Override
    public Status compute()
    {
        Status retval = Status.createOkStatus();

        WorkflowNode newNode = context( WorkflowNode.class );

        WorkflowDefinition workflowDefinition = newNode.adapt( WorkflowDefinition.class );

        if( workflowDefinition != null )
        {
            for( WorkflowNode node : workflowDefinition.getDiagramNodes() )
            {
                final String name = node.getName().content();

                if( name != null && name.equals( newNode.getName().content() ) )
                {
                    retval = Status.createErrorStatus( "Name already in use." );
                    break;
                }
            }
        }

        return retval;
    }

}
