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

import com.liferay.ide.kaleo.core.model.internal.ExecutionTypePossibleValuesService;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Gregory Amerson
 */
public interface Executable extends Element
{
    String DEFAULT_EXECUTION_TYPE = "onEntry";

    ElementType TYPE = new ElementType( Executable.class );

    @Type( base = ExecutionType.class )
    @Label( standard = "execution type" )
    @XmlBinding( path = "execution-type" )
    @Required
    @Service( impl = ExecutionTypePossibleValuesService.class )
    ValueProperty PROP_EXECUTION_TYPE = new ValueProperty( TYPE, "ExecutionType" );

    Value<ExecutionType> getExecutionType();
    void setExecutionType( String executionType );
    void setExecutionType( ExecutionType executionType );
}
