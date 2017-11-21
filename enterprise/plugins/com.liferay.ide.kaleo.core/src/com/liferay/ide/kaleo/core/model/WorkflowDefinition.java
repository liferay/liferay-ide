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

package com.liferay.ide.kaleo.core.model;

import com.liferay.ide.kaleo.core.model.internal.WorkflowDefinitionMethods;
import com.liferay.ide.kaleo.core.model.internal.WorkflowDefinitionRootElementController;
import com.liferay.ide.kaleo.core.model.internal.WorkflowDefinitionSchemaVersionService;

import java.util.List;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Since;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.Version;
import org.eclipse.sapphire.VersionCompatibilityTarget;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NumericRange;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlRootBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Gregory Amerson
 */
@CustomXmlRootBinding(value = WorkflowDefinitionRootElementController.class)
@VersionCompatibilityTarget(version = "${ SchemaVersion }", versioned = "Workflow Definition")
@XmlBinding(path = "workflow-definition")
public interface WorkflowDefinition extends Node {

	public ElementType TYPE = new ElementType(WorkflowDefinition.class);

	public ElementList<Condition> getConditions();

	@DelegateImplementation(value = WorkflowDefinitionMethods.class)
	public List<WorkflowNode> getDiagramNodes();

	public ElementList<Fork> getForks();

	public ElementList<Join> getJoins();

	public ElementList<JoinXor> getJoinXors();

	public Value<Version> getSchemaVersion();

	public ElementList<State> getStates();

	public ElementList<Task> getTasks();

	public Value<Integer> getVersion();

	public void setSchemaVersion(String value);

	public void setSchemaVersion(Version value);

	public void setVersion(Integer val);

	public void setVersion(String val);

	@Label(standard = "condition")
	@Type(base = Condition.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "condition", type = Condition.class))
	public ListProperty PROP_CONDITIONS = new ListProperty(TYPE, "Conditions");

	@Label(standard = "fork")
	@Type(base = Fork.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "fork", type = Fork.class))
	public ListProperty PROP_FORKS = new ListProperty(TYPE, "Forks");

	@Label(standard = "join XOR")
	@Since(value = "6.2")
	@Type(base = JoinXor.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "join-xor", type = JoinXor.class))
	public ListProperty PROP_JOIN_XORS = new ListProperty(TYPE, "JoinXors");

	@Label(standard = "join")
	@Type(base = Join.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "join", type = Join.class))
	public ListProperty PROP_JOINS = new ListProperty(TYPE, "Joins");

	@Service(impl = WorkflowDefinitionSchemaVersionService.class)
	@Type(base = Version.class)
	public ValueProperty PROP_SCHEMA_VERSION = new ValueProperty(TYPE, "SchemaVersion");

	@Label(standard = "state")
	@Type(base = State.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "state", type = State.class))
	public ListProperty PROP_STATES = new ListProperty(TYPE, "States");

	@Label(standard = "task")
	@Type(base = Task.class)
	@XmlListBinding(mappings = @XmlListBinding.Mapping(element = "task", type = Task.class))
	public ListProperty PROP_TASKS = new ListProperty(TYPE, "Tasks");

	@Label(standard = "&version")
	@NumericRange(min = "0")
	@Required
	@Type(base = Integer.class)
	@XmlBinding(path = "version")
	public ValueProperty PROP_VERSION = new ValueProperty(TYPE, "Version");

}