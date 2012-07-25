/*
 * JBoss by Red Hat
 * Copyright 2006-2009, Red Hat Middleware, LLC, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.ide.eclipse.freemarker.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;
import org.jboss.ide.eclipse.freemarker.Constants;

public class ContentScanner implements ITokenScanner {

	private IDocument document;
	private int offset;
	private int length;
	private int endOffset;
	private Stack stack = new Stack();
	private IToken defaultToken;
	private int tokenOffset;
	private int tokenLength;
	private Stack stringTypes = new Stack();

	private List tokens = new ArrayList();
	private int currentOffset;

	public ContentScanner (IToken defaultToken, ColorManager colorManager) {
		this.defaultToken = defaultToken;
		STRING_TOKEN = new Token(
				new TextAttribute(
						colorManager.getColor(Constants.COLOR_STRING)));
		INTERPOLATION_TOKEN = new Token(
				new TextAttribute(
						colorManager.getColor(Constants.COLOR_INTERPOLATION)));
		DIRECTIVE_TOKEN = new Token(
				new TextAttribute(
						colorManager.getColor(Constants.COLOR_DIRECTIVE)));
	}

	public void setRange(IDocument document, int offset, int length) {
		this.document = document;
		this.offset = offset;
		this.currentOffset = offset;
		this.length = length;
		this.endOffset = offset + length;
		this.stack.clear();
		this.stringTypes.clear();
		this.tokens.clear();
	}

	private static String TYPE_UNKNOWN = "UNKNOWN"; //$NON-NLS-1$
	private static String TYPE_INTERPOLATION = "INTERPOLATION"; //$NON-NLS-1$
	private static String TYPE_DIRECTIVE = "DIRECTIVE"; //$NON-NLS-1$
	private static String TYPE_STRING = "STRING"; //$NON-NLS-1$

	private static IToken STRING_TOKEN;
	private static IToken INTERPOLATION_TOKEN;
	private static IToken DIRECTIVE_TOKEN;

	public IToken nextToken() {
		int offsetStart = currentOffset;
		int i = currentOffset;
		char directiveTypeChar = Character.MIN_VALUE;
		boolean escape = false;
		boolean doEscape = false;
		try {
			char c = document.getChar(i);
			char cNext = Character.MIN_VALUE;
			if (document.getLength() > i + 2)
				cNext = document.getChar(i+1);
			if (i >= endOffset) {
				return Token.EOF;
			}
			while (i < endOffset) {
				doEscape = false;
				if (!escape) {
					String type = peek();
					if (c == '\\') {
						if (type.equals(TYPE_STRING)) doEscape = true;
					}
					else if (c == '\"' || c == '\'') {
						if (type.equals(TYPE_STRING)) {
							if (stringTypes.size() > 0 && c == ((Character) stringTypes.peek()).charValue()) {
								this.tokenOffset = offsetStart;
								this.tokenLength = i - offsetStart + 1;
								this.currentOffset = i + 1;
								pop();
								return STRING_TOKEN;
							}
						}
						else {
							if (i == offsetStart) {
								push(TYPE_STRING);
								stringTypes.push(new Character(c));
							}
							else {
								this.tokenOffset = offsetStart;
								this.tokenLength = i - offsetStart;
								this.currentOffset = i;
								return getToken(type);
							}
						}
					}
					else if (c == '$') {
						if (cNext == '{') {
							// interpolation
							this.tokenOffset = offsetStart;
							this.tokenLength = i - offsetStart;
							this.currentOffset = i;
							if (i == offsetStart) {
								push(TYPE_INTERPOLATION);
							}
							else {
								return getToken(type);
							}
						}
					}
					else if (c == '}') {
						if (type.equals(TYPE_INTERPOLATION)) {
							this.tokenOffset = offsetStart;
							this.tokenLength = i - offsetStart + 1;
							this.currentOffset = i + 1;
							pop();
							return INTERPOLATION_TOKEN;
						}
					}
					else if (c == '(') {
						if (type.equals(TYPE_INTERPOLATION)) {
							push("("); //$NON-NLS-1$
						}
					}
					else if (c == ')') {
						if (type.equals("(")) { //$NON-NLS-1$
							pop();
						}
					}
					else if ((c == '<' || c == '[') && !((stack.contains(TYPE_DIRECTIVE) || stack.contains(TYPE_INTERPOLATION)) && stack.contains(TYPE_STRING))) {
						if (cNext == '#') {
							// directive
							if (i == offsetStart) {
								directiveTypeChar = c;
								push(TYPE_DIRECTIVE);
							}
							else {
								this.tokenOffset = offsetStart;
								this.tokenLength = i - offsetStart - 1;
								this.currentOffset = i;
								return getToken(type);
							}
						}
						else if (cNext == '@') {
							// macro
							if (i == offsetStart) {
								directiveTypeChar = c;
								push(TYPE_DIRECTIVE);
							}
							else {
								this.tokenOffset = offsetStart;
								this.tokenLength = i - offsetStart - 1;
								this.currentOffset = i;
								return getToken(type);
							}
						}
					}
					else if ((c == ']' || c == '>') && !((stack.contains(TYPE_DIRECTIVE) || stack.contains(TYPE_INTERPOLATION)) && stack.contains(TYPE_STRING))) {
						if ((c == ']' && directiveTypeChar == '[') || (c == '>' && directiveTypeChar == '<')
								|| directiveTypeChar == Character.MIN_VALUE) {
							this.tokenOffset = offsetStart;
							this.tokenLength = i - offsetStart + 1;
							this.currentOffset = i + 1;
							if (directiveTypeChar != Character.MIN_VALUE) {
								pop();
								return DIRECTIVE_TOKEN;
							}
							else {
								return defaultToken;
							}
						}
					}
				}
				c = document.getChar(++i);
				cNext = Character.MIN_VALUE;
				if (document.getLength() > i + 2)
					cNext = document.getChar(i+1);
				escape = doEscape;
			}
		}
		catch (BadLocationException e) {
			this.currentOffset = i;
			this.tokenOffset = offsetStart;
			this.tokenLength = endOffset - tokenOffset;
			if (tokenLength > 0) {
				// last token
				return defaultToken;
			}
			else {
				return Token.EOF;
			}
			
		}
		this.currentOffset = i+1;
		this.tokenOffset = offsetStart;
		this.tokenLength = endOffset - tokenOffset;
		return getToken(peek());
	}

	private String peek () {
		if (stack.size() > 0) return (String) stack.peek();
		else return TYPE_UNKNOWN;
	}

	private void push (String s) {
		stack.push(s);
	}

	private String pop () {
		if (stack.size() > 0) return (String) stack.pop();
		else return TYPE_UNKNOWN;
	}

	private IToken getToken (String type) {
		if (type.equals(TYPE_DIRECTIVE)) return DIRECTIVE_TOKEN;
		else if (type.equals(TYPE_INTERPOLATION) || type.equals("(")) return INTERPOLATION_TOKEN; //$NON-NLS-1$
		else if (type.equals(TYPE_STRING)) return STRING_TOKEN;
		else return defaultToken;
	}

	public int getTokenOffset() {
		return tokenOffset;
	}

	public int getTokenLength() {
		return tokenLength;
	}

}
