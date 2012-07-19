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
package org.jboss.ide.eclipse.freemarker.editor.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class DirectiveRuleEnd extends DirectiveRule {

	public DirectiveRuleEnd(String name, IToken token) {
		super(name, token, true);
	}

	public DirectiveRuleEnd(String name, IToken token, boolean nameOnly) {
		super(name, token, nameOnly);
	}

	/**
	 * Evaluates this rules without considering any column constraints. Resumes
	 * detection, i.e. look sonly for the end sequence required by this rule if the
	 * <code>resume</code> flag is set.
	 *
	 * @param scanner the character scanner to be used
	 * @param resume <code>true</code> if detection should be resumed, <code>false</code> otherwise
	 * @return the token resulting from this evaluation
	 * @since 2.0
	 */
	protected IToken doEvaluate(ICharacterScanner scanner, boolean resume) {
		if (resume) {
			if (endSequenceDetected(scanner))
				return fToken;
		} else {
			int c= scanner.read();
			char cCheck = (char) c;
			if (c == START_SEQUENCES[0] || c == START_SEQUENCES[1]) {
				int c2 = scanner.read();
				if (c2 == '/') {
					// check for the sequence identifier
					c2 = scanner.read();
					if (c2 == getIdentifierChar()) {
						if (sequenceDetected(scanner, c, false)) {
							if (endSequenceDetected(scanner, c))
								return fToken;
						}
					}
					scanner.unread();
				}
				scanner.unread();
			}
		}
		scanner.unread();
		return Token.UNDEFINED;
	}
}