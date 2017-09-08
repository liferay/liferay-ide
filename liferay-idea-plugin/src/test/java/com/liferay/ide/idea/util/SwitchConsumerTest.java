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
				s -> s.equals("foo"),
				s -> output.append("case1 " + s + "\n")
			).addCase(
				s -> s.equals("bar"),
				s -> output.append("case2 " + s + "\n")
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

	private final static String[] _strings = { "foo", "bar", "baz","quux" };
	private final static String _expected = "case1 foo\ncase2 bar\ndefault baz\ndefault quux\n";
}
