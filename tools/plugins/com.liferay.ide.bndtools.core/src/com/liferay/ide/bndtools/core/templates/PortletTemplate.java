/**
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
 */

package com.liferay.ide.bndtools.core.templates;

import aQute.bnd.build.model.BndEditModel;
import aQute.bnd.build.model.clauses.ImportPattern;
import aQute.bnd.build.model.clauses.VersionedClause;
import aQute.bnd.header.Attrs;
import aQute.bnd.osgi.resource.CapReqBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bndtools.api.BndProjectResource;
import org.bndtools.api.IBndProject;
import org.bndtools.api.ProjectPaths;

import org.osgi.framework.namespace.IdentityNamespace;
import org.osgi.resource.Namespace;

/**
 * @author Gregory Amerson
 */
public class PortletTemplate extends AbstractProjectTemplate {

	public boolean enableTestSourceFolder() {
		return false;
	}

	@Override
	public void modifyInitialBndModel(BndEditModel model, String projectName, ProjectPaths projectPaths) {
		super.modifyInitialBndModel(model, projectName, projectPaths);

		String buildpath = "${buildpath-rule}";

		model.setBuildPath(Collections.singletonList(new VersionedClause(buildpath, Attrs.EMPTY_ATTRS)));

		model.setIncludeResource(Collections.singletonList("${includeresource-rule}"));

		ImportPattern[] patterns =
			{new ImportPattern("${imports-rule}", Attrs.EMPTY_ATTRS), new ImportPattern("*", new Attrs())};

		patterns[1].setOptional(true);
		model.setImportPatterns(Arrays.asList(patterns));

		model.setGenericString("Web-ContextPath", "/" + projectName);

		String safePackageName = safePackageName(projectName);

		String wholePackageName = "(osgi.identity=" + safePackageName + ")";

		CapReqBuilder cap = new CapReqBuilder(IdentityNamespace.IDENTITY_NAMESPACE);

		cap.addDirective(Namespace.REQUIREMENT_FILTER_DIRECTIVE, wholePackageName);

		model.setRunRequires(Collections.singletonList(cap.buildSyntheticRequirement()));
	}

	public void modifyInitialBndProject(IBndProject project, String projectName, ProjectPaths projectPaths) {
		String src = projectPaths.getSrc();

		String safePackageName = safePackageName(projectName);

		String lowerCase = safePackageName.toLowerCase();

		String pkgPath = lowerCase.replaceAll("\\.", "/");

		String safeJavaClassName = safeJavaClassName(projectName);

		String noEmptyClassName = safeJavaClassName.replaceAll("^Rule", "");

		String ruleJavaClassName = noEmptyClassName.replaceAll("Test$", "");

		Map<String, String> exprs = new LinkedHashMap<>();

		exprs.put("@rule.java.package.name@", safePackageName);
		exprs.put("@rule.java.class.name@", ruleJavaClassName);

		project.addResource(
			src + "/" + pkgPath + "/" + ruleJavaClassName + "Rule.java", _newResource("ExampleRule.java.txt", exprs));

		project.addResource("/META-INF/liferay-hook.xml", _newResource("liferay-hook.xml", exprs));
		project.addResource(src + "/content/Language.properties", _newResource("Language.properties.txt", exprs));
		project.addResource(src + "/templates/ct_fields.ftl", _newResource("ct_fields.ftl", exprs));
	}

	private BndProjectResource _newResource(String resource, Map<String, String> exprs) {
		return new BndProjectResource(PortletTemplate.class.getResource(resource), exprs);
	}

}