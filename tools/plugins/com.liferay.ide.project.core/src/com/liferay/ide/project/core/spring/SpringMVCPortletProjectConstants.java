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

package com.liferay.ide.project.core.spring;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("serial")
public class SpringMVCPortletProjectConstants {

	public static final String[] SPRING_FRAMEWORK = {"PortletMVC4Spring", "Spring Portlet MVC"};

	public static final String[] SPRING_FRAMEWORK_DEPENDENCIES = {"Embedded", "Provided"};

	public static final String[] SPRING_VIEW_TYPE = {"Jsp", "Thymeleaf"};

	public static final Map<String, String> springDependenciesInjectors = new HashMap<String, String>() {
		{
			put("DS", new String("ds"));
			put("Spring", new String("spring"));
		}
	};
	public static final Map<String, String> springFrameworkDependeices = new HashMap<String, String>() {
		{
			put(SPRING_FRAMEWORK_DEPENDENCIES[0], new String("embedded"));
			put(SPRING_FRAMEWORK_DEPENDENCIES[1], new String("provided"));
		}
	};
	public static final Map<String, String> springFrameworks = new HashMap<String, String>() {
		{
			put(SPRING_FRAMEWORK[0], new String("portletmvc4spring"));
			put(SPRING_FRAMEWORK[1], new String("springportletmvc"));
		}
	};
	public static final Map<String, String> springViewTypes = new HashMap<String, String>() {
		{
			put(SPRING_VIEW_TYPE[0], new String("jsp"));
			put(SPRING_VIEW_TYPE[1], new String("thymeleaf"));
		}
	};

}