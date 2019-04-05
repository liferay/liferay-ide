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

package com.liferay.ide.xml.search.ui.resources;

import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.wst.xml.search.core.resource.ResourceBaseURIResolver;

/**
 * Extension of WTP/XML Search resources uri resolver for Liferay to manage css,
 * js, icons used in the descriptor of liferay which starts with "/". Ex :
 *
 * <pre>
 * &lt;header-portlet-css&gt;/html/portlet/directory/css/main.css&lt;/header-portlet-css&gt;
 * </pre>
 * @author Gregory Amerson
 */
public abstract class AbstractWebResourceURIResolver extends ResourceBaseURIResolver {

	public AbstractWebResourceURIResolver(boolean canStartsWithoutSlash) {
		_canStartsWithoutSlash = canStartsWithoutSlash;
	}

	@Override
	public boolean accept(
		Object selectedNode, IResource rootContainer, IResource file, String matching, boolean fullMatch) {

		String extension = file.getFileExtension();

		if ((extension == null) || !getExtensions().contains(extension.toLowerCase())) {
			return false;
		}

		if (matching != null) {
			matching = matching.toLowerCase();
		}

		if (fullMatch) {
			String resolve = resolve(selectedNode, rootContainer, file);

			if (_canStartsWithoutSlash) {
				String uri = resolve.toLowerCase();

				if (uri.equals(matching) || uri.equals("/" + matching)) {
					return true;
				}

				return false;
			}

			return resolve.equals(matching);
		}

		return super.accept(selectedNode, rootContainer, file, matching, fullMatch);
	}

	@Override
	public String resolve(Object selectedNode, IResource rootContainer, IResource file) {
		return "/" + super.resolve(selectedNode, rootContainer, file);
	}

	protected abstract Set<String> getExtensions();

	private final boolean _canStartsWithoutSlash;

}