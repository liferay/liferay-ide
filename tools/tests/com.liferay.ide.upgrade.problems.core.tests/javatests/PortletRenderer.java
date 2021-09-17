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

package com.liferay.portal.layoutconfiguration.util;

import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletContainerException;
import com.liferay.portal.kernel.portlet.PortletContainerUtil;
import com.liferay.portal.kernel.servlet.BufferCacheServletResponse;
import com.liferay.portal.kernel.util.StringBundler;

import java.io.IOException;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Shuyang Zhou
 * @author Neil Griffin
 */
public class PortletRenderer {

	public PortletRenderer(
		Portlet portlet, String columnId, Integer columnCount,
		Integer columnPos) {

		_portlet = portlet;
		_columnId = columnId;
		_columnCount = columnCount;
		_columnPos = columnPos;
	}

	public Portlet getPortlet() {
		return _portlet;
	}

	public StringBundler render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			Map<String, Object> headerRequestAttributes)
		throws PortletContainerException {

		httpServletRequest = PortletContainerUtil.setupOptionalRenderParameters(
			httpServletRequest, null, _columnId, _columnPos, _columnCount);

		_copyHeaderRequestAttributes(
			headerRequestAttributes, httpServletRequest);

		return _render(httpServletRequest, httpServletResponse);
	}

	public StringBundler renderAjax(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws PortletContainerException {

		httpServletRequest = PortletContainerUtil.setupOptionalRenderParameters(
			httpServletRequest, _RENDER_PATH, _columnId, _columnPos,
			_columnCount);

		return _render(httpServletRequest, httpServletResponse);
	}

	public Map<String, Object> renderHeaders(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			List<String> attributePrefixes)
		throws PortletContainerException {

		httpServletRequest = PortletContainerUtil.setupOptionalRenderParameters(
			httpServletRequest, null, _columnId, _columnPos, _columnCount);

		BufferCacheServletResponse bufferCacheServletResponse =
			new BufferCacheServletResponse(httpServletResponse);

		PortletContainerUtil.renderHeaders(
			httpServletRequest, bufferCacheServletResponse, _portlet);

		Map<String, Object> headerRequestAttributes = new HashMap<>();

		Enumeration<String> enumeration =
			httpServletRequest.getAttributeNames();

		while (enumeration.hasMoreElements()) {
			String attributeName = enumeration.nextElement();

			if (attributeName.contains(
					"javax.portlet.faces.renderResponseOutput")) {

				headerRequestAttributes.put(
					attributeName,
					httpServletRequest.getAttribute(attributeName));
			}
			else if (attributePrefixes != null) {
				for (String attributePrefix : attributePrefixes) {
					if (attributeName.contains(attributePrefix)) {
						headerRequestAttributes.put(
							attributeName,
							httpServletRequest.getAttribute(attributeName));

						break;
					}
				}
			}
		}

		return headerRequestAttributes;
	}

	private void _copyHeaderRequestAttributes(
		Map<String, Object> headerRequestAttributes,
		HttpServletRequest httpServletRequest) {

		if (headerRequestAttributes != null) {
			for (Map.Entry<String, Object> entry :
					headerRequestAttributes.entrySet()) {

				httpServletRequest.setAttribute(
					entry.getKey(), entry.getValue());
			}
		}
	}

	private StringBundler _render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws PortletContainerException {

		BufferCacheServletResponse bufferCacheServletResponse =
			new BufferCacheServletResponse(httpServletResponse);

		try {
			PortletContainerUtil.render(
				httpServletRequest, bufferCacheServletResponse, _portlet);

			return bufferCacheServletResponse.getStringBundler();
		}
		catch (IOException ioException) {
			throw new PortletContainerException(ioException);
		}
	}

	private static final String _RENDER_PATH =
		"/html/portal/load_render_portlet.jsp";

	private final Integer _columnCount;
	private final String _columnId;
	private final Integer _columnPos;
	private final Portlet _portlet;

}