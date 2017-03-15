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

package com.liferay.ide.kaleo.core.op.internal;

import com.liferay.ide.kaleo.core.op.NewWorkflowDefinitionOp;

import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.UniversalConversionService;

/**
 * @author Gregory Amerson
 */
public class NewWorkflowDefinitionAdapter extends UniversalConversionService
{

    @Override
    public <A> A convert( Object object, Class<A> adapterType )
    {
        if( adapterType.equals( IProject.class ) )
        {
            NewWorkflowDefinitionOp op = context().find( NewWorkflowDefinitionOp.class );

            if( op.getProject() != null )
            {
                IProject project = op.getProject().target();

                if( project != null )
                {
                    return adapterType.cast( project );
                }
            }
        }

        return null;
    }

}
