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

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.sapphire.PossibleValuesService;

/**
 * @author Gregory Amerson
 */
public class ProjectNamesPossibleValuesService extends PossibleValuesService
{

    @Override
    protected void compute( Set<String> values )
    {
        for( IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects() )
        {
            if( project != null && project.isAccessible() )
            {
                values.add( project.getName() );
            }
        }
    }

}
