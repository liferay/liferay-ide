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

package com.liferay.ide.kaleo.core.op.internal;

import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.core.model.Action;
import com.liferay.ide.kaleo.core.model.ExecutionType;
import com.liferay.ide.kaleo.core.model.Position;
import com.liferay.ide.kaleo.core.model.ScriptLanguageType;
import com.liferay.ide.kaleo.core.model.State;
import com.liferay.ide.kaleo.core.model.Task;
import com.liferay.ide.kaleo.core.model.TemplateLanguageType;
import com.liferay.ide.kaleo.core.model.Transition;
import com.liferay.ide.kaleo.core.model.TransitionMetadata;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.core.model.WorkflowNodeMetadata;
import com.liferay.ide.kaleo.core.op.NewWorkflowDefinitionOp;
import com.liferay.ide.kaleo.core.util.KaleoModelUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.sapphire.workspace.WorkspaceFileResourceStore;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class NewWorkflowDefinitionOpMethods {

	public static Status execute(NewWorkflowDefinitionOp op, ProgressMonitor monitor) {
		try {
			ReferenceValue<String, IProject> referProject = op.getProject();

			IProject project = referProject.target();

			Value<Path> opFolder = op.getFolder();

			Path projectPath = opFolder.content();

			IContainer folder = null;

			if (projectPath != null) {
				folder = project.getFolder(projectPath.toPortableString());
			}
			else {
				folder = project;
			}

			if (!folder.exists()) {
				folder = folder.getParent();
			}

			Value<String> opName = op.getName();

			String name = opName.content();

			String lowerCaseName = name.toLowerCase();

			String[] segments = lowerCaseName.split("\\s+");

			StringBuilder newName = new StringBuilder();

			for (String segment : segments) {
				StringBuilder segNewName = newName.append(segment);

				segNewName.append('-');
			}

			String fileNameBase = newName.toString() + "definition";

			String extension = ".xml";

			String fileName = fileNameBase + extension;

			IPath proRelativePath = folder.getProjectRelativePath();

			IFile newDefinitionFile = project.getFile(proRelativePath.append(fileName));

			int count = 1;

			while (newDefinitionFile.exists()) {
				fileName = fileNameBase + " (" + count + ")" + extension;

				newDefinitionFile = project.getFile(proRelativePath.append(fileName));

				count++;
			}

			try (InputStream inputStream = new ByteArrayInputStream("".getBytes())) {
				newDefinitionFile.create(inputStream, true, null);
			}

			Value<ScriptLanguageType> scLanguageType = op.getDefaultScriptLanguage();

			newDefinitionFile.setPersistentProperty(KaleoCore.DEFAULT_SCRIPT_LANGUAGE_KEY, scLanguageType.text(true));

			Value<TemplateLanguageType> deTemplateLanguage = op.getDefaultTemplateLanguage();

			newDefinitionFile.setPersistentProperty(
				KaleoCore.DEFAULT_TEMPLATE_LANGUAGE_KEY, deTemplateLanguage.text(true));

			RootXmlResource rootXmlResource = new RootXmlResource(
				new XmlResourceStore(new WorkspaceFileResourceStore(newDefinitionFile)));

			WorkflowDefinition workflowDefinition = WorkflowDefinition.TYPE.instantiate(rootXmlResource);

			workflowDefinition.setName(name);

			workflowDefinition.setVersion("1");

			Value<String> initStateName = op.getInitialStateName();

			String initialStateName = initStateName.content(true);

			ElementList<State> workflowState = workflowDefinition.getStates();

			State state = workflowState.insert();

			state.setName(initialStateName);
			state.setInitial(true);

			Value<String> initTaskName = op.getInitialTaskName();

			String initialTaskName = initTaskName.content(true);

			ElementList<Task> workflowTask = workflowDefinition.getTasks();

			Task task = workflowTask.insert();

			task.setName(initialTaskName);

			KaleoModelUtil.changeTaskAssignments(task, op);

			ElementList<Transition> transitions = state.getTransitions();

			Transition transition = transitions.insert();

			transition.setName(initialTaskName);
			transition.setTarget(initialTaskName);

			Value<String> opFinalName = op.getFinalStateName();

			String finalStateName = opFinalName.content(true);

			ElementList<State> states = workflowDefinition.getStates();

			State finalState = states.insert();

			finalState.setName(finalStateName);
			finalState.setEnd(true);

			ElementList<Action> actions = finalState.getActions();

			Action finalAction = actions.insert();

			finalAction.setName("approve");
			finalAction.setExecutionType(ExecutionType.ON_ENTRY);
			finalAction.setScriptLanguage(ScriptLanguageType.JAVASCRIPT);

			String updateStatus = "Packages.com.liferay.portal.kernel.workflow.WorkflowStatusManagerUtil.updateStatus";

			String workflowConstants = "(Packages.com.liferay.portal.kernel.workflow.WorkflowConstants.";

			String toStatus = "toStatus(\"approved\"), workflowContext);";

			finalAction.setScript(updateStatus + workflowConstants + toStatus);

			ElementList<Transition> taskTransitions = task.getTransitions();

			Transition taskTransition = taskTransitions.insert();

			taskTransition.setName(finalStateName);
			taskTransition.setTarget(finalStateName);

			ElementHandle<WorkflowNodeMetadata> wfMetadata = state.getMetadata();

			WorkflowNodeMetadata stateMetadata = wfMetadata.content();

			Position statePosition = stateMetadata.getPosition();

			statePosition.setX(100);
			statePosition.setY(50);

			ElementList<TransitionMetadata> transitionMetadata = stateMetadata.getTransitionsMetadata();

			TransitionMetadata insertTransitionMetadata = transitionMetadata.insert();

			insertTransitionMetadata.setName(initialTaskName);

			ElementHandle<WorkflowNodeMetadata> taskWorkflow = task.getMetadata();

			WorkflowNodeMetadata taskMetadata = taskWorkflow.content();

			Position taskPosition = taskMetadata.getPosition();

			taskPosition.setX(300);
			taskPosition.setY(35);

			ElementList<TransitionMetadata> transitionTask = taskMetadata.getTransitionsMetadata();

			TransitionMetadata transitionTaskMetadata = transitionTask.insert();

			transitionTaskMetadata.setName(finalStateName);

			ElementHandle<WorkflowNodeMetadata> finalMetadata = finalState.getMetadata();

			WorkflowNodeMetadata finalStateMetadata = finalMetadata.content();

			Position finalStatePosition = finalStateMetadata.getPosition();

			finalStatePosition.setX(520);
			finalStatePosition.setY(50);

			/*
			 * Document document = rootXmlResource.getDomDocument(); Element
			 * docElement = document.getDocumentElement(); Attr schemaLocation =
			 * docElement.getAttributeNode( "xsi:schemaLocation" ); =
			 * schemaLocation.getNodeValue(); String nodeValue =
			 * schemaLocation.getNodeValue(); schemaLocation.setNodeValue(
			 * nodeValue.replaceAll( " http://www.w3.org/XML/1998/namespace ",
			 * "" ) );
			 */
			rootXmlResource.save();

			IPath fullPath = newDefinitionFile.getFullPath();

			op.setNewFilePath(fullPath.toPortableString());

			return Status.createOkStatus();
		}
		catch (Exception e) {
			KaleoCore.logError("Error creating new kaleo workflow file.", e);

			return Status.createErrorStatus("Error creating new kaleo workflow file.", e);
		}
	}

}