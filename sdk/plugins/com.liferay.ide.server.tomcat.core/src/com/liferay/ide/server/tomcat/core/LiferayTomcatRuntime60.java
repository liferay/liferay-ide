/*******************************************************************************
 * Copyright (c) 2003, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *     Greg Amerson <gregory.amerson@liferay.com>
 *******************************************************************************/

package com.liferay.ide.server.tomcat.core;

import com.liferay.ide.core.ILiferayConstants;

import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 */
public class LiferayTomcatRuntime60 extends LiferayTomcatRuntime
{

    protected Version getLeastSupportedVersion()
    {
        return ILiferayConstants.LEAST_SUPPORTED_VERSION;
    }

}
