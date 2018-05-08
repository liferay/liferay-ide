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

package com.liferay.ide.servlet;

import com.liferay.portal.kernel.jsonwebservice.JSONWebServiceActionMapping;
import com.liferay.portal.kernel.jsonwebservice.JSONWebServiceActionsManagerUtil;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.MethodParameter;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Document;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.kernel.xml.SAXReaderUtil;

import java.io.File;
import java.io.IOException;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Terry Jia
 */
public class WebServicesServlet extends HttpServlet {

	protected Map<String, Set<JSONWebServiceActionMapping>> getJSONWebServiceClazz() {
		List<JSONWebServiceActionMapping> jsonWebServiceActionMappings =
			JSONWebServiceActionsManagerUtil.getJSONWebServiceActionMappings(StringPool.BLANK);

		Map<String, Set<JSONWebServiceActionMapping>> jsonWebServiceClazz = new LinkedHashMap<>();

		for (JSONWebServiceActionMapping jsonWebServiceActionMapping : jsonWebServiceActionMappings) {
			Class<?> actionClass = jsonWebServiceActionMapping.getActionClass();

			String actionClassName = actionClass.getSimpleName();

			if (actionClassName.endsWith("ServiceUtil")) {
				actionClassName = actionClassName.substring(0, actionClassName.length() - 11);
			}

			Set<JSONWebServiceActionMapping> jsonWebServiceMappings = jsonWebServiceClazz.get(actionClassName);

			if (Validator.isNull(jsonWebServiceMappings)) {
				jsonWebServiceMappings = new LinkedHashSet<>();

				jsonWebServiceClazz.put(actionClassName, jsonWebServiceMappings);
			}

			jsonWebServiceMappings.add(jsonWebServiceActionMapping);
		}

		return jsonWebServiceClazz;
	}

	protected String getWebServicesXML() {
		Map<String, Set<JSONWebServiceActionMapping>> jsonWebServiceClazz = getJSONWebServiceClazz();

		Document document = SAXReaderUtil.createDocument("UTF-8");

		Element root = SAXReaderUtil.createElement("templates");

		document.add(root);

		for (String jsonWebServiceClassName : jsonWebServiceClazz.keySet()) {
			Set<JSONWebServiceActionMapping> jsonWebServiceMappings = jsonWebServiceClazz.get(jsonWebServiceClassName);

			String className = jsonWebServiceClassName;

			if (className.endsWith("Impl")) {
				className = className.substring(0, className.length() - 4);
			}

			if (className.endsWith("Service")) {
				className = className.substring(0, className.length() - 7);
			}

			for (JSONWebServiceActionMapping jsonWebServiceActionMapping : jsonWebServiceMappings) {
				Element element = SAXReaderUtil.createElement("template");

				String path = jsonWebServiceActionMapping.getPath();

				int pos = path.lastIndexOf(CharPool.SLASH);

				String actionName = path.substring(pos + 1);

				element.add(SAXReaderUtil.createAttribute(element, "name", "jsonws-" + className + "-" + actionName));

				element.add(
					SAXReaderUtil.createAttribute(element, "description", "jsonws-" + className + "-" + actionName));

				element.add(SAXReaderUtil.createAttribute(element, "context", "javaScript"));
				element.add(SAXReaderUtil.createAttribute(element, "enabled", "true"));
				element.add(SAXReaderUtil.createAttribute(element, "autoinsert", "true"));

				StringBuffer sb = new StringBuffer();

				sb.append("Liferay.Service(\n '");
				sb.append(path);
				sb.append("',\n {\n");

				MethodParameter[] methodParameters = jsonWebServiceActionMapping .getMethodParameters();

				if (methodParameters.length > 0) {
					for (int t = 0; t < methodParameters.length; t++) {
						String parameterName = methodParameters[t].getName();

						sb.append("  ");
						sb.append(parameterName);
						sb.append(":");
						sb.append("${");
						sb.append(parameterName);
						sb.append("}");

						if (t < (methodParameters.length - 1)) {
							sb.append(",\n");
						}
					}

					element.add(
						SAXReaderUtil.createAttribute(
							element, "id",
							"com.liferay.ide.ui.templates." + className + "." + actionName + methodParameters.length));
				}
				else {
					element.add(
						SAXReaderUtil.createAttribute(
							element, "id", "com.liferay.ide.ui.templates." + className + "." + actionName));
				}

				sb.append("\n },\n function(obj) {\n  console.log(obj);\n }\n);");

				element.add(SAXReaderUtil.createText(sb.toString()));

				root.add(element);
			}
		}

		return document.asXML();
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String path = getServletContext().getRealPath("/") + "jsonws-js-templates.xml";

		ServletOutputStream out = response.getOutputStream();

		try {
			String xml = getWebServicesXML();

			File file = new File(path);

			if (!file.exists()) {
				file.createNewFile();
			}

			FileUtil.write(file, xml);

			out.println("success:" + path);

			System.out.println("success:" + path);
		}
		catch (Exception e) {
			out.println("faild:" + path);

			System.out.println("faild:" + path);
		}
	}

}