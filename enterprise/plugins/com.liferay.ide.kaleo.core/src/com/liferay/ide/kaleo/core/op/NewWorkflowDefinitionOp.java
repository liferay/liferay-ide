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

package com.liferay.ide.kaleo.core.op;

import com.liferay.ide.kaleo.core.model.Node;
import com.liferay.ide.kaleo.core.model.TemplateLanguageType;
import com.liferay.ide.kaleo.core.op.NewNodeOp.ConditionForOp;
import com.liferay.ide.kaleo.core.op.NewNodeOp.ForkForOp;
import com.liferay.ide.kaleo.core.op.NewNodeOp.JoinForOp;
import com.liferay.ide.kaleo.core.op.NewNodeOp.JoinXorForOp;
import com.liferay.ide.kaleo.core.op.NewNodeOp.StateForOp;
import com.liferay.ide.kaleo.core.op.NewNodeOp.TaskForOp;
import com.liferay.ide.kaleo.core.op.internal.NewWorkflowDefinitionAdapter;
import com.liferay.ide.kaleo.core.op.internal.NewWorkflowDefinitionOpMethods;
import com.liferay.ide.kaleo.core.op.internal.ProjectNamesPossibleValuesService;
import com.liferay.ide.kaleo.core.op.internal.ProjectReferenceService;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;
import org.eclipse.sapphire.workspace.ProjectRelativePath;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
@Service(impl = NewWorkflowDefinitionAdapter.class)
public interface NewWorkflowDefinitionOp extends ExecutableElement, AssignableOp {

	public ElementType TYPE = new ElementType(NewWorkflowDefinitionOp.class);

	@DelegateImplementation(NewWorkflowDefinitionOpMethods.class)
	public Status execute(ProgressMonitor monitor);

	public ElementList<Node> getConnectedNodes();

	public Value<TemplateLanguageType> getDefaultTemplateLanguage();

	public Value<String> getFinalStateName();

	public Value<Path> getFolder();

	public Value<String> getInitialStateName();

	public Value<String> getInitialTaskName();

	public Value<String> getName();

	public Value<String> getNewFilePath();

	public ReferenceValue<String, IProject> getProject();

	public void setDefaultTemplateLanguage(String templateLanguage);

	public void setDefaultTemplateLanguage(TemplateLanguageType templateLanguage);

	public void setFinalStateName(String name);

	public void setFolder(Path value);

	public void setFolder(String value);

	public void setInitialStateName(String name);

	public void setInitialTaskName(String name);

	public void setName(String name);

	public void setNewFilePath(String name);

	public void setProject(String project);

	@Label(standard = "connected nodes")
	@Type(base = Node.class, possible = {
		ChooseDiagramNode.class, StateForOp.class, TaskForOp.class, ConditionForOp.class, ForkForOp.class,
		JoinForOp.class, JoinXorForOp.class
	}
	)
	public ListProperty PROP_CONNECTED_NODES = new ListProperty(TYPE, "ConnectedNodes");

	@DefaultValue(text = "freemarker")
	@Label(standard = "default t&emplate type")
	@Type(base = TemplateLanguageType.class)
	public ValueProperty PROP_DEFAULT_TEMPLATE_LANGUAGE = new ValueProperty(TYPE, "DefaultTemplateLanguage");

	@DefaultValue(text = "approved")
	@Label(standard = "&final state name")
	public ValueProperty PROP_FINAL_STATE_NAME = new ValueProperty(TYPE, "FinalStateName");

	@Label(standard = "&folder")
	@MustExist
	@ProjectRelativePath
	@Type(base = Path.class)
	public ValueProperty PROP_FOLDER = new ValueProperty(TYPE, "Folder");

	@DefaultValue(text = "created")
	@Label(standard = "&initial state name")
	public ValueProperty PROP_INITIAL_STATE_NAME = new ValueProperty(TYPE, "InitialStateName");

	@DefaultValue(text = "review")
	@Label(standard = "initial &task name")
	public ValueProperty PROP_INITIAL_TASK_NAME = new ValueProperty(TYPE, "InitialTaskName");

	@DefaultValue(text = "New Workflow")
	@Label(standard = "&name")
	@Required
	public ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	public ValueProperty PROP_NEW_FILE_PATH = new ValueProperty(TYPE, "NewFilePath");

	@Label(standard = "&project")
	@Reference(target = IProject.class)
	@Required
	@Services(
		{@Service(impl = ProjectReferenceService.class), @Service(impl = ProjectNamesPossibleValuesService.class)}
	)
	public ValueProperty PROP_PROJECT = new ValueProperty(TYPE, "Project");

}