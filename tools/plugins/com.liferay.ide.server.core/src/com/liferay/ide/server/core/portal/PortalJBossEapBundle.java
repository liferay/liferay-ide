/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/

package com.liferay.ide.server.core.portal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;

/**
 * @author Simon Jiang
 */
public class PortalJBossEapBundle extends PortalJBossBundle
{

    public PortalJBossEapBundle( IPath path )
    {
        super( path );
    }

    public PortalJBossEapBundle( Map<String, String> appServerProperties )
    {
        super( appServerProperties );
    }

    @Override
    public String[] getRuntimeStartVMArgs()
    {
        final List<String> args = new ArrayList<String>();

        args.add( "-Dorg.jboss.resolver.warning=true" );
        args.add( "-Djava.net.preferIPv4Stack=true" );
        args.add( "-Dsun.rmi.dgc.client.gcInterval=3600000" );
        args.add( "-Dsun.rmi.dgc.server.gcInterval=3600000" );
        args.add( "-Djboss.modules.system.pkgs=org.jboss.byteman" );
        args.add( "-Djava.awt.headless=true" );
        args.add( "-Dfile.encoding=UTF8" );

        args.add( "-server" );
        args.add( "-Djava.util.logging.manager=org.jboss.logmanager.LogManager" );

        args.add( "-Xbootclasspath/p:" + "\"" + this.bundlePath +
            "/modules/system/layers/base/org/jboss/logmanager/main/jboss-logmanager-1.5.4.Final-redhat-1.jar" + "\"" );
        args.add( "-Xbootclasspath/p:" +
            "\"" +
            this.bundlePath +
            "/modules/system/layers/base/org/jboss/log4j/logmanager/main/log4j-jboss-logmanager-1.1.1.Final-redhat-1.jar" +
            "\"" );
        args.add( "-Djboss.modules.system.pkgs=org.jboss.logmanager" );

        args.add( "-Dorg.jboss.boot.log.file=" + "\"" + this.bundlePath.append( "/standalone/log/boot.log" ) + "\"" );
        args.add( "-Dlogging.configuration=file:" + "\"" + this.bundlePath +
            "/standalone/configuration/logging.properties" + "\"" );
        args.add( "-Djboss.home.dir=" + "\"" + this.bundlePath + "\"" );
        args.add( "-Djboss.bind.address.management=localhost" );
        args.add( "-Duser.timezone=GMT" );
        args.add( "-Dorg.jboss.logmanager.nocolor=true" );

        return args.toArray( new String[0] );
    }

    @Override
    public String getType()
    {
        return "jboss";
    }

    @Override
    public String getDisplayName()
    {
        return "JBoss EAP";
    }
}
