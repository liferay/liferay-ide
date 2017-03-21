/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay Developer Studio ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */

package com.liferay.ide.kaleo.ui.diagram;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireActionHandlerFactory;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;

/**
 * @author Gregory Amerson
 */
public class KaleoNodeAddActionHandlerFactory extends SapphireActionHandlerFactory
{

    @Override
    public List<SapphireActionHandler> create()
    {
        final List<SapphireActionHandler> handlers = new ArrayList<SapphireActionHandler>();

        final SapphireDiagramEditorPagePart diagramPart = (SapphireDiagramEditorPagePart) getPart();

        for( final DiagramNodeTemplate nodeTemplate : diagramPart.getVisibleNodeTemplates() )
        {
            final NewNodeAddActionHandler addNodeHandler = createKaleoNodeActionHandlerForTemplate( nodeTemplate );

            handlers.add( addNodeHandler );
        }

        return handlers;
    }

    private NewNodeAddActionHandler createKaleoNodeActionHandlerForTemplate( DiagramNodeTemplate nodeTemplate )
    {
        NewNodeAddActionHandler retval = null;

        if( "state".equals( nodeTemplate.definition().getId().content() ) )
        {
            retval = new StateNodeAddActionHandler( nodeTemplate );
        }
        else if( "task".equals( nodeTemplate.definition().getId().content() ) )
        {
            retval = new TaskNodeAddActionHandler( nodeTemplate );
        }
        else if( "condition".equals( nodeTemplate.definition().getId().content() ) )
        {
            retval = new ConditionNodeAddActionHandler( nodeTemplate );
        }
        else if( "fork".equals( nodeTemplate.definition().getId().content() ) )
        {
            retval = new ForkNodeAddActionHandler( nodeTemplate );
        }
        else if( "join".equals( nodeTemplate.definition().getId().content() ) )
        {
            retval = new JoinNodeAddActionHandler( nodeTemplate );
        }
        else if( "join-xor".equals( nodeTemplate.definition().getId().content() ) )
        {
            retval = new JoinXorNodeAddActionHandler( nodeTemplate );
        }

        return retval;
    }

}
