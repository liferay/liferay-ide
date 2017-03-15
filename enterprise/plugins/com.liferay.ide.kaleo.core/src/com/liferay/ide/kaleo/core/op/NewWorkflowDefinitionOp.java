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
@Service( impl = NewWorkflowDefinitionAdapter.class )
public interface NewWorkflowDefinitionOp extends ExecutableElement, AssignableOp
{
    ElementType TYPE = new ElementType( NewWorkflowDefinitionOp.class );

    @Reference( target = IProject.class )
    @Services
    (
        {
            @Service( impl = ProjectReferenceService.class ),
            @Service( impl = ProjectNamesPossibleValuesService.class )
        }
    )
    @Label( standard = "&project" )
    @Required
    ValueProperty PROP_PROJECT = new ValueProperty( TYPE, "Project" );


    @Type( base = Path.class )
    @Label( standard = "&folder" )
    @ProjectRelativePath
    @MustExist
    ValueProperty PROP_FOLDER = new ValueProperty( TYPE, "Folder" );


    @Label( standard = "&name" )
    @DefaultValue( text = "New Workflow" )
    @Required
    ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" );


    @Label( standard = "&initial state name" )
    @DefaultValue( text = "created" )
    ValueProperty PROP_INITIAL_STATE_NAME = new ValueProperty( TYPE, "InitialStateName" );


    @Type( base = TemplateLanguageType.class )
    @Label( standard = "default t&emplate type" )
    @DefaultValue( text = "freemarker" )
    ValueProperty PROP_DEFAULT_TEMPLATE_LANGUAGE = new ValueProperty( TYPE, "DefaultTemplateLanguage" );


    @Label( standard = "initial &task name" )
    @DefaultValue( text = "review" )
    ValueProperty PROP_INITIAL_TASK_NAME = new ValueProperty( TYPE, "InitialTaskName" );


    @Label( standard = "&final state name" )
    @DefaultValue( text = "approved" )
    ValueProperty PROP_FINAL_STATE_NAME = new ValueProperty( TYPE, "FinalStateName" );

    ValueProperty PROP_NEW_FILE_PATH = new ValueProperty( TYPE, "NewFilePath" );

    @Type
    (
        base = Node.class,
        possible =
        {
            ChooseDiagramNode.class,
            StateForOp.class,
            TaskForOp.class,
            ConditionForOp.class,
            ForkForOp.class,
            JoinForOp.class,
            JoinXorForOp.class
        }
    )
    @Label( standard = "connected nodes" )
    ListProperty PROP_CONNECTED_NODES = new ListProperty( TYPE, "ConnectedNodes" );

    ElementList<Node> getConnectedNodes();

    // Methods

    ReferenceValue<String, IProject> getProject();
    void setProject( String project );

    Value<Path> getFolder();
    void setFolder( String value );
    void setFolder( Path value );

    Value<String> getName();
    void setName( String name );

    Value<String> getInitialStateName();
    void setInitialStateName( String name );



    Value<TemplateLanguageType> getDefaultTemplateLanguage();
    void setDefaultTemplateLanguage( String templateLanguage );
    void setDefaultTemplateLanguage( TemplateLanguageType templateLanguage );

    Value<String> getInitialTaskName();
    void setInitialTaskName( String name );

    Value<String> getFinalStateName();
    void setFinalStateName( String name );

    Value<String> getNewFilePath();
    void setNewFilePath( String name );

    // *** Method: execute ***

    @DelegateImplementation( NewWorkflowDefinitionOpMethods.class )

    Status execute( ProgressMonitor monitor );

}
