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

import com.liferay.ide.kaleo.ui.editor.WorkflowDefinitionEditor;

import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;
import org.eclipse.sapphire.ui.def.SapphireActionType;
import org.eclipse.ui.IPropertyListener;

/**
 * @author Gregory Amerson
 */
public class UseNodeWizardsActionHandler extends SapphireActionHandler
{

    @Override
    public void init( SapphireAction action, ActionHandlerDef def )
    {
        super.init( action, def );

        if( action.getType() == SapphireActionType.TOGGLE )
        {
            ISapphirePart diagramPart = this.getPart();

            final WorkflowDefinitionEditor definitionEditor = diagramPart.adapt( WorkflowDefinitionEditor.class );

            if( definitionEditor != null )
            {
                setChecked( definitionEditor.isNodeWizardsEnabled() );

                definitionEditor.addPropertyListener
                (
                    new IPropertyListener()
                    {
                        public void propertyChanged( Object source, int propId )
                        {
                            if( propId == WorkflowDefinitionEditor.PROP_NODE_WIZARDS_ENABLED )
                            {
                                setChecked( definitionEditor.isNodeWizardsEnabled() );
                            }
                        }
                    }
                );
            }
        }
    }

    @Override
    protected Object run( Presentation context )
    {
        final ISapphirePart part = context.part();

        WorkflowDefinitionEditor definitionEditor = part.adapt( WorkflowDefinitionEditor.class );

        definitionEditor.setNodeWizardsEnabled( isChecked() );

        return null;
    }

}
