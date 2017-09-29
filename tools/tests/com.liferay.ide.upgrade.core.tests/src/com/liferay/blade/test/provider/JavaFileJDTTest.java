/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.blade.test.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.blade.api.JavaFile;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.test.Util;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public class JavaFileJDTTest {

	@Test
	public void checkStaticMethodInvocation() throws Exception {
		File file = new File("tests/files/JavaFileChecker.java");
		final BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();

		final Collection<ServiceReference<JavaFile>> sr = context.getServiceReferences(JavaFile.class,
				"(file.extension=java)");
		JavaFile javaFileChecker = context.getService(sr.iterator().next());
		javaFileChecker.setFile(file);
		List<SearchResult> searchResults = javaFileChecker.findMethodInvocations(null, "String", "valueOf", null);

		assertNotNull(searchResults);

		SearchResult searchResult = searchResults.get(0);

		assertNotNull(searchResult);

		if (Util.isWindows()) {
		assertEquals(14, searchResult.startLine);
		assertEquals(15, searchResult.endLine);
		assertEquals(242, searchResult.startOffset);
		assertEquals(265, searchResult.endOffset);
		}
		else {
		assertEquals(14, searchResult.startLine);
		assertEquals(15, searchResult.endLine);
		assertEquals(229, searchResult.startOffset);
		assertEquals(251, searchResult.endOffset);
		}
	}

	@Test
	public void checkMethodInvocation() throws Exception {
		File file = new File("tests/files/JavaFileChecker.java");
		final BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();

		final Collection<ServiceReference<JavaFile>> sr = context.getServiceReferences(JavaFile.class,
				"(file.extension=java)");
		JavaFile javaFileChecker = context.getService(sr.iterator().next());
		javaFileChecker.setFile(file);
		List<SearchResult> searchResults = javaFileChecker.findMethodInvocations("Foo", null, "bar", null);

		assertNotNull(searchResults);

		assertEquals(4, searchResults.size());

		SearchResult searchResult = searchResults.get(0);

		assertNotNull(searchResult);

		if (Util.isWindows()) {
		assertEquals(10, searchResult.startLine);
		assertEquals(11, searchResult.endLine);
		assertEquals(190, searchResult.startOffset);
		assertEquals(210, searchResult.endOffset);
		}
		else {
		assertEquals(10, searchResult.startLine);
		assertEquals(11, searchResult.endLine);
		assertEquals(181, searchResult.startOffset);
		assertEquals(200, searchResult.endOffset);
		}
	}

	@Test
	public void checkMethodInvocationTypeMatch() throws Exception {
		File file = new File("tests/files/JavaFileCheckerTypeMatch.java");
		final BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();

		final Collection<ServiceReference<JavaFile>> sr = context.getServiceReferences(JavaFile.class,
				"(file.extension=java)");
		JavaFile javaFileChecker = context.getService(sr.iterator().next());
		javaFileChecker.setFile(file);

		List<SearchResult> searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForString",
				new String[] { "String" });

		assertNotNull(searchResults);

		assertEquals(2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForString",
				new String[] { "java.lang.String" });

		assertNotNull(searchResults);

		assertEquals(2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForStringFull",
				new String[] { "String" });

		assertNotNull(searchResults);

		assertEquals(2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForStringFull",
				new String[] { "java.lang.String" });

		assertNotNull(searchResults);

		assertEquals(2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForStringArray",
				new String[] { "String[]" });

		assertNotNull(searchResults);

		assertEquals(2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForStringArray",
				new String[] { "java.lang.String[]" });

		assertNotNull(searchResults);

		assertEquals(2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForStringArrayFull",
				new String[] { "String[]" });

		assertNotNull(searchResults);

		assertEquals(2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForStringArrayFull",
				new String[] { "java.lang.String[]" });

		assertNotNull(searchResults);

		assertEquals(2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForClass", new String[] { "AnyClass" });

		assertNotNull(searchResults);

		assertEquals(2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForClass",
				new String[] { "blade.migrate.liferay70.AnyClass" });

		assertNotNull(searchResults);

		assertEquals(2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForClass",
				new String[] { "anypackage.AnyClass" });

		assertNotNull(searchResults);

		assertEquals(0, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForClassArray",
				new String[] { "AnyClass[]" });

		assertNotNull(searchResults);

		assertEquals(2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForClassArray",
				new String[] { "blade.migrate.liferay70.AnyClass[]" });

		assertNotNull(searchResults);

		assertEquals(2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForClassArray",
				new String[] { "anypackage.AnyClass[]" });

		assertNotNull(searchResults);

		assertEquals(0, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForClassFull",
				new String[] { "AnyClass" });

		assertNotNull(searchResults);

		assertEquals(2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForClassFull",
				new String[] { "anypackage.AnyClass" });

		assertNotNull(searchResults);

		assertEquals(0, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForClassFull",
				new String[] { "blade.migrate.liferay70.AnyClass" });

		assertNotNull(searchResults);

		assertEquals(2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForClassArrayFull",
				new String[] { "AnyClass[]" });

		assertNotNull(searchResults);

		assertEquals(2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForClassArrayFull",
				new String[] { "blade.migrate.liferay70.AnyClass[]" });

		assertNotNull(searchResults);

		assertEquals(2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForClassArrayFull",
				new String[] { "anypackage.AnyClass[]" });

		assertNotNull(searchResults);

		assertEquals(0, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForObject", new String[] { "Object" });

		assertNotNull(searchResults);

		assertEquals(6, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForObject",
				new String[] { "java.lang.Object" });

		assertNotNull(searchResults);

		assertEquals(6, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForObjectFull",
				new String[] { "Object" });

		assertNotNull(searchResults);

		assertEquals(6, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForObjectFull",
				new String[] { "java.lang.Object" });

		assertNotNull(searchResults);

		assertEquals(6, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForLong", new String[] { "long" });

		assertNotNull(searchResults);

		assertEquals(12, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForLongClass", new String[] { "Long" });

		assertNotNull(searchResults);

		assertEquals(3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForLongClassFull",
				new String[] { "java.lang.Long" });

		assertNotNull(searchResults);

		assertEquals(3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForLongArray",
				new String[] { "long[]" });

		assertNotNull(searchResults);

		assertEquals(1, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForLongArrayClass",
				new String[] { "Long[]" });

		assertNotNull(searchResults);

		assertEquals(2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForLongArrayClassFull",
				new String[] { "java.lang.Long[]" });

		assertNotNull(searchResults);

		assertEquals(2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForInt", new String[] { "int" });

		assertNotNull(searchResults);

		assertEquals(9, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForIntClass",
				new String[] { "Integer" });

		assertNotNull(searchResults);

		assertEquals(3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForIntClassFull",
				new String[] { "java.lang.Integer" });

		assertNotNull(searchResults);

		assertEquals(3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForIntArray", new String[] { "int[]" });

		assertNotNull(searchResults);

		assertEquals(1, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForIntArrayClass",
				new String[] { "Integer[]" });

		assertNotNull(searchResults);

		assertEquals(2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForIntArrayClassFull",
				new String[] { "java.lang.Integer[]" });

		assertNotNull(searchResults);

		assertEquals(2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForShort", new String[] { "short" });

		assertNotNull(searchResults);

		assertEquals(6, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForShortClass",
				new String[] { "Short" });

		assertNotNull(searchResults);

		assertEquals(3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForShortClassFull",
				new String[] { "java.lang.Short" });

		assertNotNull(searchResults);

		assertEquals(3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForShortArray",
				new String[] { "short[]" });

		assertNotNull(searchResults);

		assertEquals(1, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForShortArrayClass",
				new String[] { "Short[]" });

		assertNotNull(searchResults);

		assertEquals(2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForShortArrayClassFull",
				new String[] { "java.lang.Short[]" });

		assertNotNull(searchResults);

		assertEquals(2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForByte", new String[] { "byte" });

		assertNotNull(searchResults);

		assertEquals(3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForByteClass", new String[] { "Byte" });

		assertNotNull(searchResults);

		assertEquals(3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForByteClassFull",
				new String[] { "java.lang.Byte" });

		assertNotNull(searchResults);

		assertEquals(3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForByteArray",
				new String[] { "byte[]" });

		assertNotNull(searchResults);

		assertEquals(1, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForByteArrayClass",
				new String[] { "Byte[]" });

		assertNotNull(searchResults);

		assertEquals(2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForByteArrayClassFull",
				new String[] { "java.lang.Byte[]" });

		assertNotNull(searchResults);

		assertEquals(2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForDouble", new String[] { "double" });

		assertNotNull(searchResults);

		assertEquals(6, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForDoubleClass",
				new String[] { "Double" });

		assertNotNull(searchResults);

		assertEquals(3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForDoubleClassFull",
				new String[] { "java.lang.Double" });

		assertNotNull(searchResults);

		assertEquals(3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForDoubleArray",
				new String[] { "double[]" });

		assertNotNull(searchResults);

		assertEquals(1, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForDoubleArrayClass",
				new String[] { "Double[]" });

		assertNotNull(searchResults);

		assertEquals(2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForDoubleArrayClassFull",
				new String[] { "java.lang.Double[]" });

		assertNotNull(searchResults);

		assertEquals(2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForFloat", new String[] { "float" });

		assertNotNull(searchResults);

		assertEquals(3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForFloatClass",
				new String[] { "Float" });

		assertNotNull(searchResults);

		assertEquals(3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForFloatClassFull",
				new String[] { "java.lang.Float" });

		assertNotNull(searchResults);

		assertEquals(3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForFloatArray",
				new String[] { "float[]" });

		assertNotNull(searchResults);

		assertEquals(1, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForFloatArrayClass",
				new String[] { "Float[]" });

		assertNotNull(searchResults);

		assertEquals(2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForFloatArrayClassFull",
				new String[] { "java.lang.Float[]" });

		assertNotNull(searchResults);

		assertEquals(2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForObjectArray",
				new String[] { "Object[]" });

		assertNotNull(searchResults);

		assertEquals(20, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForObjectArrayFull",
				new String[] { "java.lang.Object[]" });

		assertNotNull(searchResults);

		assertEquals(20, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForChar", new String[] { "char" });

		assertNotNull(searchResults);

		assertEquals(3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForCharClass",
				new String[] { "Character" });

		assertNotNull(searchResults);

		assertEquals(3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForCharClassFull",
				new String[] { "java.lang.Character" });

		assertNotNull(searchResults);

		assertEquals(3, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForCharArray",
				new String[] { "char[]" });

		assertNotNull(searchResults);

		assertEquals(1, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForCharArrayClass",
				new String[] { "Character[]" });

		assertNotNull(searchResults);

		assertEquals(2, searchResults.size());

		searchResults = javaFileChecker.findMethodInvocations("Foo", null, "barForCharArrayClassFull",
				new String[] { "java.lang.Character[]" });

		assertNotNull(searchResults);

		assertEquals(2, searchResults.size());
	}

	@Test
	public void checkGuessMethodInvocation() throws Exception {
		File file = new File("tests/files/JavaFileChecker.java");
		final BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();

		final Collection<ServiceReference<JavaFile>> sr = context.getServiceReferences(JavaFile.class,
				"(file.extension=java)");
		JavaFile javaFileChecker = context.getService(sr.iterator().next());
		javaFileChecker.setFile(file);
		List<SearchResult> results = javaFileChecker.findMethodInvocations(null, "JavaFileChecker", "staticCall",
				new String[] { "String", "String", "String" });
		assertNotNull(results);
		assertEquals(4, results.size());
		results = javaFileChecker.findMethodInvocations("JavaFileChecker", null, "call",
				new String[] { "String", "String", "String" });
		assertNotNull(results);
		assertEquals(4, results.size());
	}
}
