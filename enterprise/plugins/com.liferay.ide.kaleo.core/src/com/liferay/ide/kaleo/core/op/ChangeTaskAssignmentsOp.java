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

import com.liferay.ide.kaleo.core.op.internal.DefaultOpMethods;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;

/**
 * @author Gregory Amerson
 */
public interface ChangeTaskAssignmentsOp extends AssignableOp
{
    ElementType TYPE = new ElementType( ChangeTaskAssignmentsOp.class );

    @DelegateImplementation( DefaultOpMethods.class )
    Status execute( ProgressMonitor monitor );

}
