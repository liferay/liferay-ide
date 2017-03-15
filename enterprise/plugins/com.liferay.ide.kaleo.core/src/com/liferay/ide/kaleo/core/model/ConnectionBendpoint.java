/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation
 ******************************************************************************/

package com.liferay.ide.kaleo.core.model;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */
public interface ConnectionBendpoint extends Element
{
    ElementType TYPE = new ElementType( ConnectionBendpoint.class );

    // *** X ***

    @Type( base = Integer.class )
    @DefaultValue( text = "0" )
    ValueProperty PROP_X = new ValueProperty( TYPE, "X");

    Value<Integer> getX();
    void setX(Integer value);
    void setX(String value);

    // *** Y ***

    @Type( base = Integer.class )
    @DefaultValue( text = "0" )
    ValueProperty PROP_Y = new ValueProperty( TYPE, "Y");

    Value<Integer> getY();
    void setY(Integer value);
    void setY(String value);

}
