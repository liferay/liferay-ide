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

package com.liferay.ide.core.tests;

import com.liferay.ide.core.util.StringUtil;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Anthony Chu
 */
public class StringUtilTests extends BaseTests {

	@Test
	public void equalsIgnoreCase() {

		// java.lang.String

		Assert.assertTrue(StringUtil.equalsIgnoreCase("foo", "foo"));
		Assert.assertTrue(StringUtil.equalsIgnoreCase("FOO", "foo"));
		Assert.assertTrue(StringUtil.equalsIgnoreCase("Foo", "fOO"));
		Assert.assertFalse(StringUtil.equalsIgnoreCase("foo", "bar"));
		Assert.assertFalse(StringUtil.equalsIgnoreCase(null, "foo"));
		Assert.assertFalse(StringUtil.equalsIgnoreCase("foo", (String)null));
		Assert.assertFalse(StringUtil.equalsIgnoreCase(null, (String)null));

		// java.lang.Object

		Assert.assertTrue(StringUtil.equalsIgnoreCase("foo", (Object)"FOO"));
		Assert.assertTrue(StringUtil.equalsIgnoreCase("foo", new StringBuilder("FOO")));
		Assert.assertFalse(StringUtil.equalsIgnoreCase("foo", (Object)"bar"));
		Assert.assertFalse(StringUtil.equalsIgnoreCase("foo", (Object)null));
		Assert.assertFalse(StringUtil.equalsIgnoreCase(null, (Object)"foo"));

		// Locale.ROOT — Turkish locale lowercases "I" to dotless "ı"

		Locale defaultLocale = Locale.getDefault();

		try {
			Locale.setDefault(new Locale("tr", "TR"));

			Assert.assertTrue(StringUtil.equalsIgnoreCase("TITLE", "title"));
		}
		finally {
			Locale.setDefault(defaultLocale);
		}
	}

	@Test
	public void toLowerCase() {

		// java.lang.String

		Assert.assertEquals("foo", StringUtil.toLowerCase("FOO"));
		Assert.assertEquals("foo", StringUtil.toLowerCase("Foo"));
		Assert.assertEquals("", StringUtil.toLowerCase((String)null));

		// java.lang.StringBuilder

		Assert.assertEquals("foo", StringUtil.toLowerCase(new StringBuilder("FOO")));
		Assert.assertEquals("", StringUtil.toLowerCase((StringBuilder)null));
	}

	@Test
	public void toUpperCase() {
		Assert.assertEquals("FOO", StringUtil.toUpperCase("foo"));
		Assert.assertEquals("FOO", StringUtil.toUpperCase("Foo"));
		Assert.assertEquals("", StringUtil.toUpperCase(null));
	}

}
