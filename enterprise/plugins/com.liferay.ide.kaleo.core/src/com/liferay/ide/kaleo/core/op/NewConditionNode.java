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

import com.liferay.ide.kaleo.core.model.CanTransition;
import com.liferay.ide.kaleo.core.model.Condition;
import com.liferay.ide.kaleo.core.model.Node;
import com.liferay.ide.kaleo.core.model.internal.DefaultScriptLanguageService;
import com.liferay.ide.kaleo.core.op.internal.NewNodeNameValidationService;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Length;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author Gregory Amerson
 */
public interface NewConditionNode extends Condition
{
    ElementType TYPE = new ElementType( NewConditionNode.class );

    @DefaultValue( text = "New Condition" )
    @Service( impl = NewNodeNameValidationService.class )
    ValueProperty PROP_NAME = new ValueProperty( TYPE, Node.PROP_NAME );

    @Length( min = 0 )
    ListProperty PROP_TRANSITIONS = new ListProperty( TYPE, CanTransition.PROP_TRANSITIONS );

    @Service( impl = DefaultScriptLanguageService.class )
    ValueProperty PROP_SCRIPT_LANGUAGE = new ValueProperty( TYPE, Condition.PROP_SCRIPT_LANGUAGE );

}
