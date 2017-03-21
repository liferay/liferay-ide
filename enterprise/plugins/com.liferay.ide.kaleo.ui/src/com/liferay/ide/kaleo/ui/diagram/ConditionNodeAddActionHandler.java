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

import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.core.model.CanTransition;
import com.liferay.ide.kaleo.core.model.Condition;
import com.liferay.ide.kaleo.core.model.ScriptLanguageType;
import com.liferay.ide.kaleo.core.op.NewConditionNodeOp;
import com.liferay.ide.kaleo.core.op.NewNodeOp;
import com.liferay.ide.kaleo.core.util.KaleoModelUtil;
import com.liferay.ide.kaleo.ui.KaleoUI;

import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;

/**
 * @author Gregory Amerson
 */
public class ConditionNodeAddActionHandler extends NewNodeAddActionHandler
{

    private static final String WIZARD_ID = "newConditionNodeWizard";

    public ConditionNodeAddActionHandler( DiagramNodeTemplate nodeTemplate )
    {
        super( nodeTemplate );
    }

    @Override
    protected NewNodeOp createOp( Presentation context )
    {
        NewConditionNodeOp op = NewConditionNodeOp.TYPE.instantiate();

        op.getNewConditionNode().setScriptLanguage(
            KaleoModelUtil.getDefaultValue(
                context.part().getLocalModelElement(), KaleoCore.DEFAULT_SCRIPT_LANGUAGE_KEY, ScriptLanguageType.GROOVY ) );

        return op;
    }

    @Override
    protected String getWizardId()
    {
        return WIZARD_ID;
    }

    @Override
    public void postDiagramNodePartAdded( NewNodeOp op, CanTransition newNodeFromWizard, CanTransition newNode )
    {
        Condition newConditionNodeFromWizard = op.nearest( NewConditionNodeOp.class ).getNewConditionNode();
        Condition newCondition = newNode.nearest( Condition.class );

        newCondition.setScriptLanguage( newConditionNodeFromWizard.getScriptLanguage().content() );
        newCondition.setScript( KaleoUI.getDefaultScriptForType(
            newCondition.getScriptLanguage().content(), Condition.TYPE.getSimpleName() ) );
    }

}
