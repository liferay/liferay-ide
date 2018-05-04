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

package com.liferay.blade.test.provider;

import com.liferay.blade.api.JavaFile;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.test.Util;

import java.io.File;

import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class JavaFileJDTTest {

	@Test
	public void checkGuessMethodInvocation() throws Exception {
		File file = new File("tests/files/JavaFileChecker.java");

		BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();

		Collection<ServiceReference<JavaFile>> sr = context.getServiceReferences(
			JavaFile.class, "(file.extension=java)");

		JavaFile javaFileChecker = context.getService(sr.iterator().next());

		javaFileChecker.setFile(file);

		List<SearchResult> results = javaFileChecker.findMethodInvocations(
			null, "JavaFileChecker", "staticCall", new String[] {"String", "String", "String"});

		Assert.assertNotNull(results);

		Assert.assertEquals(results.toString(), 4, results.size());

		results = javaFileChecker.findMethodInvocations(
			"JavaFileChecker", null, "call", new String[] {"String", "String", "String"});

		Assert.assertNotNull(results);

		Assert.assertEquals(results.toString(), 4, results.size());
	}

	@Test
	public void checkMethodInvocation() throws Exception {
		File file = new File("tests/files/JavaFileChecker.java");

		BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();

		Collection<ServiceReference<JavaFile>> sr = context.getServiceReferences(
			JavaFile.class, "(file.extension=java)");

		JavaFile javaFileChecker = context.getService(sr.iterator().next());

		javaFileChecker.setFile(file);
		List<SearchResult> searchResults = javaFileChecker.findMethodInvocations("Foo", null, "bar", null);

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 4, searchResults.size());

		SearchResult searchResult = searchResults.get(0);

		Assert.assertNotNull(searchResult);

		if (Util.isWindows()) {
			Assert.assertEquals(searchResults.toString(), 10, searchResult.startLine);
			Assert.assertEquals(searchResults.toString(), 11, searchResult.endLine);
			Assert.assertEquals(searchResults.toString(), 190, searchResult.startOffset);
			Assert.assertEquals(searchResults.toString(), 210, searchResult.endOffset);
		}
		else {
			Assert.assertEquals(searchResults.toString(), 10, searchResult.startLine);
			Assert.assertEquals(searchResults.toString(), 11, searchResult.endLine);
			Assert.assertEquals(searchResults.toString(), 181, searchResult.startOffset);
			Assert.assertEquals(searchResults.toString(), 200, searchResult.endOffset);
		}
	}

	@Test
	public void checkMethodInvocationTypeMatch() throws Exception {
		File file = new File("tests/files/JavaFileCheckerTypeMatch.java");

		BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();

		Collection<ServiceReference<JavaFile>> sr = context.getServiceReferences(
			JavaFile.class, "(file.extension=java)");

		JavaFile javaFileChecker = context.getService(sr.iterator().next());

		javaFileChecker.setFile(file);

		List<SearchResult> searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForString", new String[] {"String"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForString", new String[] {"java.lang.String"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForStringFull", new String[] {"String"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForStringFull", new String[] {"java.lang.String"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForStringArray", new String[] {"String[]"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForStringArray", new String[] {"java.lang.String[]"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForStringArrayFull", new String[] {"String[]"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForStringArrayFull", new String[] {"java.lang.String[]"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForClass", new String[] {"AnyClass"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForClass", new String[] {"blade.migrate.liferay70.AnyClass"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForClass", new String[] {"anypackage.AnyClass"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 0, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForClassArray", new String[] {"AnyClass[]"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForClassArray", new String[] {"blade.migrate.liferay70.AnyClass[]"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForClassArray", new String[] {"anypackage.AnyClass[]"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 0, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForClassFull", new String[] {"AnyClass"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForClassFull", new String[] {"anypackage.AnyClass"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 0, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForClassFull", new String[] {"blade.migrate.liferay70.AnyClass"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForClassArrayFull", new String[] {"AnyClass[]"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForClassArrayFull", new String[] {"blade.migrate.liferay70.AnyClass[]"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForClassArrayFull", new String[] {"anypackage.AnyClass[]"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 0, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForObject", new String[] {"Object"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 6, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForObject", new String[] {"java.lang.Object"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 6, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForObjectFull", new String[] {"Object"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 6, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForObjectFull", new String[] {"java.lang.Object"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 6, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForLong", new String[] {"long"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 12, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForLongClass", new String[] {"Long"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForLongClassFull", new String[] {"java.lang.Long"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForLongArray", new String[] {"long[]"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 1, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForLongArrayClass", new String[] {"Long[]"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForLongArrayClassFull", new String[] {"java.lang.Long[]"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForInt", new String[] {"int"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 9, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForIntClass", new String[] {"Integer"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForIntClassFull", new String[] {"java.lang.Integer"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForIntArray", new String[] {"int[]"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 1, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForIntArrayClass", new String[] {"Integer[]"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForIntArrayClassFull", new String[] {"java.lang.Integer[]"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForShort", new String[] {"short"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 6, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForShortClass", new String[] {"Short"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForShortClassFull", new String[] {"java.lang.Short"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForShortArray", new String[] {"short[]"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 1, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForShortArrayClass", new String[] {"Short[]"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForShortArrayClassFull", new String[] {"java.lang.Short[]"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForByte", new String[] {"byte"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForByteClass", new String[] {"Byte"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForByteClassFull", new String[] {"java.lang.Byte"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForByteArray", new String[] {"byte[]"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 1, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForByteArrayClass", new String[] {"Byte[]"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForByteArrayClassFull", new String[] {"java.lang.Byte[]"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForDouble", new String[] {"double"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 6, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForDoubleClass", new String[] {"Double"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForDoubleClassFull", new String[] {"java.lang.Double"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForDoubleArray", new String[] {"double[]"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 1, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForDoubleArrayClass", new String[] {"Double[]"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForDoubleArrayClassFull", new String[] {"java.lang.Double[]"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForFloat", new String[] {"float"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForFloatClass", new String[] {"Float"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForFloatClassFull", new String[] {"java.lang.Float"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForFloatArray", new String[] {"float[]"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 1, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForFloatArrayClass", new String[] {"Float[]"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForFloatArrayClassFull", new String[] {"java.lang.Float[]"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForObjectArray", new String[] {"Object[]"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 20, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForObjectArrayFull", new String[] {"java.lang.Object[]"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 20, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForChar", new String[] {"char"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForCharClass", new String[] {"Character"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForCharClassFull", new String[] {"java.lang.Character"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForCharArray", new String[] {"char[]"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 1, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForCharArrayClass", new String[] {"Character[]"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations(
			"Foo", null, "barForCharArrayClassFull", new String[] {"java.lang.Character[]"});

		Assert.assertNotNull(searchResults);

		Assert.assertEquals(searchResults.toString(), 2, searchResults.size());
	}

	@Test
	public void checkStaticMethodInvocation() throws Exception {
		File file = new File("tests/files/JavaFileChecker.java");

		BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();

		Collection<ServiceReference<JavaFile>> sr = context.getServiceReferences(
			JavaFile.class, "(file.extension=java)");

		JavaFile javaFileChecker = context.getService(sr.iterator().next());

		javaFileChecker.setFile(file);
		List<SearchResult> searchResults = javaFileChecker.findMethodInvocations(null, "String", "valueOf", null);

		Assert.assertNotNull(searchResults);

		SearchResult searchResult = searchResults.get(0);

		Assert.assertNotNull(searchResult);

		if (Util.isWindows()) {
			Assert.assertEquals(searchResults.toString(), 14, searchResult.startLine);
			Assert.assertEquals(searchResults.toString(), 15, searchResult.endLine);
			Assert.assertEquals(searchResults.toString(), 242, searchResult.startOffset);
			Assert.assertEquals(searchResults.toString(), 265, searchResult.endOffset);
		}
		else {
			Assert.assertEquals(searchResults.toString(), 14, searchResult.startLine);
			Assert.assertEquals(searchResults.toString(), 15, searchResult.endLine);
			Assert.assertEquals(searchResults.toString(), 229, searchResult.startOffset);
			Assert.assertEquals(searchResults.toString(), 251, searchResult.endOffset);
		}
	}

}