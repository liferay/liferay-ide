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

import com.liferay.ide.kaleo.core.model.WorkflowNode;
import com.liferay.ide.kaleo.core.model.internal.TransitionPossibleValuesService;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author Gregory Amerson
 */
@Label( standard = "existing node" )
@Image( path = "images/arrow_16x16.png" )
public interface ChooseDiagramNode extends WorkflowNode
{

    ElementType TYPE = new ElementType( ChooseDiagramNode.class );

    // *** Name ***

    @Service( impl = TransitionPossibleValuesService.class )
    ValueProperty PROP_NAME = new ValueProperty( TYPE, WorkflowNode.PROP_NAME );

}
