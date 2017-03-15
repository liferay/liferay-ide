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

import com.liferay.ide.kaleo.core.model.State;
import com.liferay.ide.kaleo.core.model.WorkflowNodeMetadata;

import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;

/**
 * @author Gregory Amerson
 */
public class StateEndValueBinding extends XmlValueBindingImpl
{

    @Override
    public String read()
    {
        WorkflowNodeMetadata metadata = state().getMetadata().content();

        if( metadata != null )
        {
            boolean terminal = metadata.isTerminal().content();

            if( terminal )
            {
                return "true";
            }
        }

        return null;
    }

    @Override
    public void write( String value )
    {
        state().getMetadata().content( true ).setTerminal( Boolean.parseBoolean( value ) );
    }

    protected State state()
    {
        return property().nearest( State.class );
    }

}
