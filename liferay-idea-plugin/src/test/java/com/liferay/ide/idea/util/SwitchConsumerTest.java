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

package com.liferay.ide.idea.util;

import com.liferay.ide.idea.util.SwitchConsumer.SwitchConsumerBuilder;

import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Christopher Bryan Boyd
 * @author Gregory Amerson
 */
public class SwitchConsumerTest {

	@Test
	public void testSwitchConsumerBuilder() throws Exception {
		StringBuilder output = new StringBuilder();

		SwitchConsumerBuilder<String> switch_ = SwitchConsumer.newBuilder();

		SwitchConsumer<String> switchConsumer = switch_.addCase(
			s -> s.equals("foo"), s -> output.append("case1 " + s + "\n")
		).addCase(
			s -> s.equals("bar"), s -> output.append("case2 " + s + "\n")
		).setDefault(
			s -> output.append("default " + s + "\n")
		).build();

		Stream.of(
			_STRINGS
		).filter(
			s -> s != null
		).forEach(
			switchConsumer
		);

		Assert.assertEquals(_EXPECTED, output.toString());
	}

	@Test
	public void testTraditionalIfElseStatement() throws Exception {
		StringBuilder output = new StringBuilder();

		for (String s : _STRINGS) {
			if (s != null) {
				if (s.equals("foo")) {
					output.append("case1 " + s + "\n");
				}
				else if (s.equals("bar")) {
					output.append("case2 " + s + "\n");
				}
				else {
					output.append("default " + s + "\n");
				}
			}
		}

		Assert.assertEquals(_EXPECTED, output.toString());
	}

	private static final String _EXPECTED = "case1 foo\ncase2 bar\ndefault baz\ndefault quux\n";

	private static final String[] _STRINGS = {"foo", "bar", "baz", "quux"};

}