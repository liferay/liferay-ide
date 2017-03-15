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

import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.core.model.WorkflowNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gregory Amerson
 */
public class WorkflowDefinitionMethods
{

    public static List<WorkflowNode> getDiagramNodes( final WorkflowDefinition definition )
    {
        List<WorkflowNode> nodes = new ArrayList<WorkflowNode>();

        nodes.addAll( definition.getStates() );
        nodes.addAll( definition.getTasks() );
        nodes.addAll( definition.getConditions() );
        nodes.addAll( definition.getForks() );
        nodes.addAll( definition.getJoins() );
        nodes.addAll( definition.getJoinXors() );

        return nodes;
    }
}
