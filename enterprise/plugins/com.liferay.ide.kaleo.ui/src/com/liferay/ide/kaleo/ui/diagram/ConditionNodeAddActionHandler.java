/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
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
public class ConditionNodeAddActionHandler extends NewNodeAddActionHandler {

	public ConditionNodeAddActionHandler(DiagramNodeTemplate nodeTemplate) {
		super(nodeTemplate);
	}

	@Override
	public void postDiagramNodePartAdded(NewNodeOp op, CanTransition newNodeFromWizard, CanTransition newNode) {
		Condition newConditionNodeFromWizard = op.nearest(NewConditionNodeOp.class).getNewConditionNode();
		Condition newCondition = newNode.nearest(Condition.class);

		newCondition.setScriptLanguage(newConditionNodeFromWizard.getScriptLanguage().content());
		newCondition.setScript(
			KaleoUI.getDefaultScriptForType(
				newCondition.getScriptLanguage().content(), Condition.TYPE.getSimpleName()));
	}

	@Override
	protected NewNodeOp createOp(Presentation context) {
		NewConditionNodeOp op = NewConditionNodeOp.TYPE.instantiate();

		op.getNewConditionNode().setScriptLanguage(
			KaleoModelUtil.getDefaultValue(
				context.part().getLocalModelElement(), KaleoCore.DEFAULT_SCRIPT_LANGUAGE_KEY,
				ScriptLanguageType.GROOVY));

		return op;
	}

	@Override
	protected String getWizardId() {
		return _WIZARD_ID;
	}

	private static final String _WIZARD_ID = "newConditionNodeWizard";

}