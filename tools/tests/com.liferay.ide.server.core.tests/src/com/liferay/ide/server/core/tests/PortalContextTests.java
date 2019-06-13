/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved./
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

package com.liferay.ide.server.core.tests;

import org.junit.Test;
import org.junit.Assert;

import com.liferay.ide.server.tomcat.core.PortalContext;

/**
 * @author Seiphon Wang
 */
public class PortalContextTests {

	@Test
	public void CreateContextInstanceTest() {
		PortalContext cn1 = new PortalContext("foo.xml");
		PortalContext cn2 = new PortalContext("foo.war");
		PortalContext cn3 = new PortalContext("/foo");
		PortalContext cn4 = new PortalContext("foo");
		PortalContext cn5 = new PortalContext("foo/bar");
		PortalContext cn6 = new PortalContext("foo#bar##42");
		PortalContext cn7 = new PortalContext("ROOT.xml");
		PortalContext cn8 = new PortalContext("ROOT");
		PortalContext cn9 = new PortalContext("ROOT##42.xml");

		Assert.assertEquals("/foo", cn1.getPath());
		Assert.assertEquals("/foo", cn1.getName());
		Assert.assertEquals("foo", cn1.getBaseName());

		Assert.assertEquals("/foo", cn2.getPath());
		Assert.assertEquals("/foo", cn2.getName());
		Assert.assertEquals("foo", cn2.getBaseName());

		Assert.assertEquals("/foo", cn3.getPath());
		Assert.assertEquals("/foo", cn3.getName());
		Assert.assertEquals("foo", cn3.getBaseName());

		Assert.assertEquals("/foo", cn4.getPath());
		Assert.assertEquals("/foo", cn4.getName());
		Assert.assertEquals("foo", cn4.getBaseName());

		Assert.assertEquals("/foo/bar", cn5.getPath());
		Assert.assertEquals("/foo/bar", cn5.getName());
		Assert.assertEquals("foo#bar", cn5.getBaseName());

		Assert.assertEquals("/foo/bar", cn6.getPath());
		Assert.assertEquals("/foo/bar##42", cn6.getName());
		Assert.assertEquals("foo#bar##42", cn6.getBaseName());

		Assert.assertEquals("", cn7.getPath());
		Assert.assertEquals("", cn7.getName());
		Assert.assertEquals("ROOT", cn7.getBaseName());

		Assert.assertEquals("", cn8.getPath());
		Assert.assertEquals("", cn8.getName());
		Assert.assertEquals("ROOT", cn8.getBaseName());

		Assert.assertEquals("", cn9.getPath());
		Assert.assertEquals("##42", cn9.getName());
		Assert.assertEquals("ROOT##42", cn9.getBaseName());
	}
}
