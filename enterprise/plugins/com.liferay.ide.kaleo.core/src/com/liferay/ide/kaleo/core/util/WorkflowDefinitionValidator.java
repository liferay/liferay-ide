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

package com.liferay.ide.kaleo.core.util;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.project.core.BaseValidator;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.server.util.ServerUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.ValidatorMessage;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( { "restriction", "deprecation" } )
public class WorkflowDefinitionValidator extends BaseValidator
{
    public static final String MARKER_TYPE = "com.liferay.ide.kaleo.core.workflowDefinitionMarker";
    public static final String PREFERENCE_NODE_QUALIFIER = KaleoCore.getDefault().getBundle().getSymbolicName();
    public static final String WORKFLOW_DEFINITION_VALIDATE = "validation-workflow-definition-validate";

    public WorkflowDefinitionValidator()
    {
        super();
    }

    @SuppressWarnings( "unchecked" )
    Map<String, Object>[] detectProblems( IFile workflowDefinitionXml, IScopeContext[] preferenceScopes )
        throws CoreException
    {
        List<Map<String, Object>> problems = new ArrayList<Map<String, Object>>();

        IStructuredModel model = null;

        try
        {
            model = StructuredModelManager.getModelManager().getModelForRead( workflowDefinitionXml );

            if( model != null && model instanceof IDOMModel )
            {
                IDOMDocument document = ( (IDOMModel) model ).getDocument();

                try
                {
                    IWorkflowValidation workflowValidation =
                        KaleoCore.getWorkflowValidation( ServerUtil.getRuntime( workflowDefinitionXml.getProject() ) );

                    Exception error = workflowValidation.validate( workflowDefinitionXml.getContents() );

                    if( error != null && ! CoreUtil.isNullOrEmpty( error.getMessage() ) )
                    {
                        Map<String, Object> problem = createMarkerValues( PREFERENCE_NODE_QUALIFIER, preferenceScopes,
                            WORKFLOW_DEFINITION_VALIDATE, (IDOMNode)document.getFirstChild(), error.getMessage() );
                        problems.add( problem );
                    }
                }
                catch (Exception e)
                {
                    // if workflowValidation fails just skip marking any errors
                }
            }

        }
        catch( Exception e )
        {
        }
        finally
        {
            if( model != null )
            {
                model.releaseFromRead();
            }
        }

        Map<String, Object>[] retval = new Map[problems.size()];

        return (Map<String, Object>[]) problems.toArray( retval );
    }

    @Override
    public ValidationResult validate( IResource resource, int kind, ValidationState state, IProgressMonitor monitor )
    {
        if( resource.getType() != IResource.FILE )
        {
            return null;
        }

        ValidationResult result = new ValidationResult();

        IFile workflowDefinitionXml = (IFile) resource;

        if( workflowDefinitionXml.isAccessible() && CoreUtil.isLiferayProject( resource.getProject() ) )
        {
            IScopeContext[] scopes = new IScopeContext[] { new InstanceScope(), new DefaultScope() };

            ProjectScope projectScope = new ProjectScope( workflowDefinitionXml.getProject() );

            boolean useProjectSettings =
                projectScope.getNode( PREFERENCE_NODE_QUALIFIER ).getBoolean(
                    ProjectCore.USE_PROJECT_SETTINGS, false );

            if( useProjectSettings )
            {
                scopes = new IScopeContext[] { projectScope, new InstanceScope(), new DefaultScope() };
            }

            try
            {
                Map<String, Object>[] problems = detectProblems( workflowDefinitionXml, scopes );

                for( int i = 0; i < problems.length; i++ )
                {
                    ValidatorMessage message =
                        ValidatorMessage.create( problems[i].get( IMarker.MESSAGE ).toString(), resource );
                    message.setType( MARKER_TYPE );
                    message.setAttributes( problems[i] );
                    result.add( message );
                }

                if( problems.length > 0 )
                {
                    result.setDependsOn( new IResource[] { workflowDefinitionXml } );
                }
            }
            catch( Exception e )
            {
                KaleoCore.logError( e );
            }
        }

        return result;
    }

}
