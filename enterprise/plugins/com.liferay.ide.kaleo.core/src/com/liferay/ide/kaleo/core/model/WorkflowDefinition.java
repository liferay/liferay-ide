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
@CustomXmlRootBinding( value = WorkflowDefinitionRootElementController.class )
@VersionCompatibilityTarget( version = "${ SchemaVersion }", versioned = "Workflow Definition" )
@XmlBinding( path = "workflow-definition" )
public interface WorkflowDefinition extends Node
{

    ElementType TYPE = new ElementType( WorkflowDefinition.class );

    // *** SchemaVersion ***
    // this property is used by root element controller and version compatibility target

    @Type( base = Version.class )
    @Service( impl = WorkflowDefinitionSchemaVersionService.class )
    ValueProperty PROP_SCHEMA_VERSION = new ValueProperty( TYPE, "SchemaVersion" ); //$NON-NLS-1$

    Value<Version> getSchemaVersion();
    void setSchemaVersion( String value );
    void setSchemaVersion( Version value );


    // *** Version ***

    @Type( base = Integer.class )
    @Label( standard = "&version" )
    @NumericRange( min = "0" )
    @XmlBinding( path = "version" )
    @Required
    ValueProperty PROP_VERSION = new ValueProperty( TYPE, "Version" );

    Value<Integer> getVersion();
    void setVersion( String val );
    void setVersion( Integer val );

    // *** Conditions ***

    @Type( base = Condition.class )
    @Label( standard = "condition" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "condition", type = Condition.class ) )
    ListProperty PROP_CONDITIONS = new ListProperty( TYPE, "Conditions" );

    ElementList<Condition> getConditions();

    // *** Forks ***

    @Type( base = Fork.class )
    @Label( standard = "fork" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "fork", type = Fork.class ) )
    ListProperty PROP_FORKS = new ListProperty( TYPE, "Forks" );

    ElementList<Fork> getForks();

    // *** Joins ***

    @Type( base = Join.class )
    @Label( standard = "join" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "join", type = Join.class ) )
    ListProperty PROP_JOINS = new ListProperty( TYPE, "Joins" );

    ElementList<Join> getJoins();

    // *** JoinXors ***

    @Type( base = JoinXor.class )
    @Label( standard = "join XOR" )
    @Since( value = "6.2" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "join-xor", type = JoinXor.class ) )
    ListProperty PROP_JOIN_XORS = new ListProperty( TYPE, "JoinXors" );

    ElementList<JoinXor> getJoinXors();

    // *** States ***

    @Type( base = State.class )
    @Label( standard = "state" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "state", type = State.class ) )
    ListProperty PROP_STATES = new ListProperty( TYPE, "States" );

    ElementList<State> getStates();

    // *** Tasks ***

    @Type( base = Task.class )
    @Label( standard = "task" )
    @XmlListBinding( mappings = @XmlListBinding.Mapping( element = "task", type = Task.class ) )
    ListProperty PROP_TASKS = new ListProperty( TYPE, "Tasks" );

    ElementList<Task> getTasks();

    @DelegateImplementation( value = WorkflowDefinitionMethods.class )
    List<WorkflowNode> getDiagramNodes();

}
