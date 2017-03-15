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

package com.liferay.ide.kaleo.core.model.internal;

import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.core.op.NewNodeOp;
import com.liferay.ide.kaleo.core.op.NewWorkflowDefinitionOp;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.Version;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class EmailAddressValidationService extends ValidationService
{

    static final Pattern emailAddressPattern = Pattern.compile( "[^@]+@[^\\.]+\\..+" );
    private boolean shouldValidate;

    @Override
    protected void initValidationService()
    {
        super.initValidationService();

        final Version schemaVersion = getSchemaVersion();

        shouldValidate = schemaVersion.compareTo( new Version( "6.2" ) ) >= 0;
    }

    private Version getSchemaVersion()
    {
        Version schemaVersion = new Version( KaleoCore.DEFAULT_KALEO_VERSION );

        if( context( WorkflowDefinition.class ) != null )
        {
            final WorkflowDefinition workflowDefinition = context( WorkflowDefinition.class );

            schemaVersion = workflowDefinition.getSchemaVersion().content();
        }
        else if( context( NewNodeOp.class ) != null  )
        {
            final NewNodeOp newNodeOp = context( NewNodeOp.class );

            schemaVersion = newNodeOp.getWorkflowDefinition().content().getSchemaVersion().content();
        }
        else if( context( NewWorkflowDefinitionOp.class ) != null )
        {
            final NewWorkflowDefinitionOp newWorkflowDenitionOp = context( NewWorkflowDefinitionOp.class );
            final IProject project = newWorkflowDenitionOp.getProject().target();
            final ILiferayProject liferayProj = LiferayCore.create( project );
            final ILiferayPortal portal = liferayProj.adapt( ILiferayPortal.class );

            if( portal != null )
            {
                schemaVersion = new Version( portal.getVersion() );
            }
        }

        return schemaVersion;
    }

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        if( shouldValidate )
        {
            Value<?> value = context( Value.class );

            if( ! value.empty() )
            {
                if( ! emailAddressPattern.matcher( value.content().toString() ).matches() )
                {
                    retval = Status.createErrorStatus( "Email address syntax is not valid" );
                }
            }
        }

        return retval;
    }

}
