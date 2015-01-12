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
package com.liferay.ide.bndtools.core.templates;

import aQute.bnd.build.model.BndEditModel;
import aQute.bnd.build.model.clauses.ExportedPackage;
import aQute.bnd.build.model.clauses.ImportPattern;
import aQute.bnd.build.model.clauses.VersionedClause;
import aQute.bnd.header.Attrs;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.WordUtils;
import org.bndtools.api.BndProjectResource;
import org.bndtools.api.IBndProject;
import org.bndtools.api.IProjectTemplate;
import org.bndtools.api.ProjectPaths;


/**
 * @author Gregory Amerson
 */
public class ContentTargetingRuleTemplate implements IProjectTemplate
{

    public ContentTargetingRuleTemplate()
    {
    }

    @Override
    public void modifyInitialBndModel( BndEditModel model, String projectName, ProjectPaths projectPaths )
    {
        final ExportedPackage export =  new ExportedPackage( projectName, new Attrs() );
        model.setExportedPackages( Collections.singletonList( export ) );

        // Bundle-Version: 1.0.0
        model.setBundleVersion( "1.0.0.${tstamp}" );
        model.setBundleDescription( projectName + " rule" );

        model.setBuildPath( Collections.singletonList( new VersionedClause( "${ctdeps-std}", Attrs.EMPTY_ATTRS ) ) );

        model.setIncludeResource( Collections.singletonList( "${ctincluderesource-std}" ) );

        model.setIncludeSources( false );


        final ImportPattern[] patterns =
        {
            new ImportPattern( "${ctimports-std}", Attrs.EMPTY_ATTRS ),
            new ImportPattern( "*", Attrs.EMPTY_ATTRS )
        };

        patterns[1].setOptional( true );

        model.setImportPatterns( Arrays.asList( patterns ) );

        // TODO set runrequires
        // model.setRunRequires( Collections.singletonList( new org.osgi.resource.Requirement() ) );
    }

    @Override
    public void modifyInitialBndProject( IBndProject project, String projectName, ProjectPaths projectPaths )
    {
        final String src = projectPaths.getSrc();
        final String safePackageName = projectName.replaceAll( "[_|\\-|\\s+]", "" );
        final String pkgPath = safePackageName.toLowerCase().replaceAll( "\\.", "/" );
        final String ruleJavaClassName =
            WordUtils.capitalizeFully( projectName, new char[] { '_', '-', '.', ' ' } ).replaceAll(
                "[_|\\-|\\.|\\s+]", "" );

        final Map<String, String> exprs = new LinkedHashMap<String, String>();
        exprs.put( "@rule.java.package.name@", safePackageName );
        exprs.put( "@rule.java.class.name@", ruleJavaClassName );

        project.addResource(
            src + "/" + pkgPath + "/" + ruleJavaClassName + "Rule.java",
            newResource( "ExampleRule.java.txt", exprs ) );
        project.addResource(
            src + "/content/Language.properties", newResource( "Language.properties.txt", exprs ) );
        project.addResource( src + "/templates/ct_fields.ftl", newResource( "ct_fields.ftl", exprs ) );

        project.addResource( "/META-INF/liferay-hook.xml", newResource( "liferay-hook.xml", exprs ) ) ;
    }

    private BndProjectResource newResource( String resource, Map<String, String> exprs )
    {
        return new BndProjectResource( ContentTargetingRuleTemplate.class.getResource( resource ), exprs );
    }

    @Override
    public boolean enableTestSourceFolder()
    {
        return false;
    }

}
