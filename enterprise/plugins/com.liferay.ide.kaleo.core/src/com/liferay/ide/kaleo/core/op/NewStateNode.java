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
import com.liferay.ide.kaleo.core.model.State;
import com.liferay.ide.kaleo.core.op.internal.NewNodeNameValidationService;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author Gregory Amerson
 */
public interface NewStateNode extends State
{
    ElementType TYPE = new ElementType( NewStateNode.class );

    @DefaultValue( text = "New State" )
    @Service( impl = NewNodeNameValidationService.class )
    ValueProperty PROP_NAME = new ValueProperty( TYPE, Node.PROP_NAME );

}
