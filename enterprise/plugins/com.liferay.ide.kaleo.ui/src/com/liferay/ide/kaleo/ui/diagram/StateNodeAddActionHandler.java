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

import static com.liferay.ide.core.util.CoreUtil.empty;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.core.model.Action;
import com.liferay.ide.kaleo.core.model.CanTransition;
import com.liferay.ide.kaleo.core.model.Executable;
import com.liferay.ide.kaleo.core.model.ScriptLanguageType;
import com.liferay.ide.kaleo.core.model.State;
import com.liferay.ide.kaleo.core.model.Transition;
import com.liferay.ide.kaleo.core.op.NewNodeOp;
import com.liferay.ide.kaleo.core.op.NewStateNode;
import com.liferay.ide.kaleo.core.op.NewStateNodeOp;
import com.liferay.ide.kaleo.core.op.NewStateType;
import com.liferay.ide.kaleo.core.util.KaleoModelUtil;
import com.liferay.ide.kaleo.ui.IKaleoEditorHelper;
import com.liferay.ide.kaleo.ui.KaleoUI;

import java.io.File;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;

/**
 * @author Gregory Amerson
 */
public class StateNodeAddActionHandler extends NewNodeAddActionHandler {

	public StateNodeAddActionHandler(DiagramNodeTemplate nodeTemplate) {
		super(nodeTemplate);
	}

	@Override
	public void postDiagramNodePartAdded(NewNodeOp op, CanTransition newNodeFromWizard, CanTransition newNode) {
		NewStateNodeOp newStateNodeOp = op.nearest(NewStateNodeOp.class);

		NewStateNode newStateNode = newNodeFromWizard.nearest(NewStateNode.class);

		State state = newNode.nearest(State.class);

		if ((newStateNode != null) && (state != null)) {
			state.setName(newStateNode.getName().content());

			NewStateType newStateType = newStateNodeOp.getType().content();

			if (newStateType.equals(NewStateType.START)) {
				state.setInitial(true);
			}
			else if (newStateType.equals(NewStateType.END)) {
				state.setEnd(true);
			}

			String workflowStatus = newStateNodeOp.getWorkflowStatus().content(false);

			if (!empty(workflowStatus)) {
				Action newAction = state.getActions().insert();

				newAction.setName(state.getName().content());
				newAction.setScriptLanguage(
					KaleoModelUtil.getDefaultValue(
						state, KaleoCore.DEFAULT_SCRIPT_LANGUAGE_KEY, ScriptLanguageType.GROOVY));
				newAction.setExecutionType(Executable.DEFAULT_EXECUTION_TYPE);

				IKaleoEditorHelper editorHelper = KaleoUI.getKaleoEditorHelper(newAction.getScriptLanguage().text());

				if (editorHelper != null) {
					try {
						File statusUpdatesFolder = new File(
							FileLocator.toFileURL(
								Platform.getBundle(editorHelper.getContributorName()).getEntry("palette/Status Updates")
							).getFile());

						File statusSnippet = new File(
							statusUpdatesFolder, workflowStatus + "." + editorHelper.getFileExtension());

						if (FileUtil.exists(statusSnippet)) {
							newAction.setScript(FileUtil.readContents(statusSnippet, true));
						}
					}
					catch (Exception e) {
					}
				}
			}

			if (!newStateType.equals(NewStateType.END) && (newStateNodeOp.getExitTransitionName().content() != null)) {
				Transition newTransition = state.getTransitions().insert();

				newTransition.setTarget(newStateNodeOp.getExitTransitionName().content());
				newTransition.setName(newStateNodeOp.getExitTransitionName().content());
			}
		}
	}

	@Override
	protected NewNodeOp createOp(Presentation context) {
		return NewStateNodeOp.TYPE.instantiate();
	}

	@Override
	protected String getWizardId() {
		return _WIZARD_ID;
	}

	private static final String _WIZARD_ID = "newStateNodeWizard";

}