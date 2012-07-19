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


import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.jboss.ide.eclipse.freemarker.Constants;
import org.jboss.ide.eclipse.freemarker.editor.rules.InterpolationRule;
import org.jboss.ide.eclipse.freemarker.editor.rules.StringSubRule;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class XMLTagScanner extends RuleBasedScanner {

	private IToken lastToken;
	
	public IToken nextToken() {
		lastToken = super.nextToken();
		return lastToken;
	}

	public IToken getLastToken () {
		return lastToken;
	}

	public XMLTagScanner(ColorManager manager) {
		IToken string =
			new Token(
				new TextAttribute(
						manager.getColor(Constants.COLOR_STRING)));
		IToken interpolation =
			new Token(
				new TextAttribute(
						manager.getColor(Constants.COLOR_INTERPOLATION)));

		List l = new ArrayList();

		l.add(new StringSubRule("\"", "${", 2, string)); //$NON-NLS-1$ //$NON-NLS-2$
		l.add(new InterpolationRule('$', interpolation));
		l.add(new InterpolationRule('#', interpolation));

		l.add(new SingleLineRule("\"", "\"", string, '\\')); //$NON-NLS-1$ //$NON-NLS-2$
		l.add(new SingleLineRule("'", "'", string, '\\')); //$NON-NLS-1$ //$NON-NLS-2$
		l.add(new WhitespaceRule(new WhitespaceDetector()));
		
		setRules((IRule[]) l.toArray(new IRule[l.size()]));
	}
}
