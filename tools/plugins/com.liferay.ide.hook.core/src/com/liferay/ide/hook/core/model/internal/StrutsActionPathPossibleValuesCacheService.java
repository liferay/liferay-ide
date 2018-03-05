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

package com.liferay.ide.hook.core.model.internal;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.hook.core.HookCore;

import java.io.File;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.services.Service;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Gregory Amerson
 */
public class StrutsActionPathPossibleValuesCacheService extends Service {

	public TreeSet<String> getPossibleValuesForPath(IPath strutsConfigPath) {
		TreeSet<String> retval = _possibleValuesMap.get(strutsConfigPath);

		if (retval == null) {
			File strutsConfigFile = strutsConfigPath.toFile();

			if (FileUtil.exists(strutsConfigFile)) {
				TreeSet<String> possibleValues = new TreeSet<>();

				try {
					DocumentBuilderFactory newInstance = DocumentBuilderFactory.newInstance();

					newInstance.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
					newInstance.setValidating(false);
					Document doc = newInstance.newDocumentBuilder().parse(strutsConfigFile);

					NodeList actions = doc.getElementsByTagName("action");

					if (actions != null) {
						for (int i = 0; i < actions.getLength(); i++) {
							final Node action = actions.item(i);

							final Node path = action.getAttributes().getNamedItem("path");

							if (path != null) {
								possibleValues.add(path.getNodeValue());
							}
						}
					}

					_possibleValuesMap.put(strutsConfigPath, possibleValues);
					retval = possibleValues;
				}
				catch (Exception e) {
					HookCore.logError("Unable to parse struts config: " + strutsConfigPath, e);
				}
			}
		}

		if (retval == null) {
			retval = new TreeSet<>();
		}

		return retval;
	}

	private final Map<IPath, TreeSet<String>> _possibleValuesMap = new HashMap<>();

}