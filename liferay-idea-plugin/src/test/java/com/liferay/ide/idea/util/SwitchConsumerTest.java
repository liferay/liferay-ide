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

public class SwitchConsumerTest {

	@Test
	public void testSwitchConsumerBuilder() throws Exception {
		StringBuilder output = new StringBuilder();

		SwitchConsumerBuilder<String> switch_ = SwitchConsumer.newBuilder();

		Stream.of(_strings).filter(
			s -> s != null
		).forEach(
			switch_.addCase(
				s -> s.equals("foo"), s -> output.append("case1 " + s + "\n")
			).addCase(
				s -> s.equals("bar"), s -> output.append("case2 " + s + "\n")
			).setDefault(
				s -> output.append("default " + s + "\n")
			).build()
		);

		Assert.assertEquals(_expected, output.toString());
	}

	@Test
	public void testTraditionalIfElseStatement() throws Exception {
		StringBuilder output = new StringBuilder();

		for (String s : _strings) {
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

		Assert.assertEquals(_expected, output.toString());
	}

	private static final String _expected = "case1 foo\ncase2 bar\ndefault baz\ndefault quux\n";
	private static final String[] _strings = {"foo", "bar", "baz", "quux"};

}