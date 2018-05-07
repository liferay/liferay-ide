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

package com.liferay.ide.idea.language;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlTag;

import java.util.Arrays;
import java.util.Collection;

import org.jetbrains.annotations.NotNull;

/**
 * @author Dominik Marks
 */
public class LiferayServiceXMLLineMarkerProvider extends RelatedItemLineMarkerProvider {

	@Override
	protected void collectNavigationMarkers(
		@NotNull PsiElement element, Collection<? super RelatedItemLineMarkerInfo> result) {

		if (element instanceof XmlTag) {
			XmlTag xmlTag = (XmlTag)element;

			String name = xmlTag.getLocalName();

			if ("entity".equals(name)) {
				if (xmlTag.getParent() instanceof XmlTag) {
					XmlTag parent = (XmlTag)xmlTag.getParent();

					if ("service-builder".equals(parent.getName())) {
						String packagePath = parent.getAttributeValue("package-path");

						if (packagePath != null) {
							String entityName = xmlTag.getAttributeValue("name");

							if (entityName != null) {
								Project project = element.getProject();

								String targetClassName = packagePath + ".model.impl." + entityName + "Impl";

								JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);

								PsiClass psiClass = javaPsiFacade.findClass(
									targetClassName, GlobalSearchScope.allScope(project));

								if (psiClass != null) {
									NavigationGutterIconBuilder<PsiElement> builder =
										NavigationGutterIconBuilder.create(AllIcons.Gutter.ImplementedMethod);

									builder.setTargets(Arrays.asList(psiClass));
									builder.setTooltipText("Navigate to Implementation");

									result.add(builder.createLineMarkerInfo(element));
								}
							}
						}
					}
				}
			}
		}
	}

}