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
package org.jboss.ide.eclipse.freemarker.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;


public abstract class AbstractDirective extends AbstractItem {

	String contents;

	public String getContents() {
		if (null == contents) {
			contents = super.getContents();
			if (null != contents) {
				try {
					contents = contents.substring(2, contents.length()-1);
				}
				catch (StringIndexOutOfBoundsException e) {
				}
			}
		}
		return contents;
	}

	public static String[] directives = new String[] {
		"if", "else", "elseif", "switch", "case", "default", "break", "list", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
		"break", "include", "import", "noparse", "compress", "escape", "noescape", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
		"assign", "global", "local", "setting", "macro", "nested", "return", "flush", "function", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
		"stop", "ftl", "t", "lt", "rt", "nt", "attempt", "recover", "visit", "recurse", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
		"fallback" //$NON-NLS-1$
	};
	public ICompletionProposal[] getCompletionProposals(int offset, Map context) {
		if (offset < 2) return null;
		ContentWithOffset contentWithOffset = splitContents(offset);
		int index = contentWithOffset.getIndex();
		if (index == 0) {
			int subOffset = contentWithOffset.getOffsetInIndex();
			int directiveOffset = contentWithOffset.getOffset();
			String[] contents = contentWithOffset.getContents();
			// name
			if (contentWithOffset.wasLastCharSpace()) {
				if (contents.length == 1) {
					// first param
					CompletionInterpolation completionInterpolation = new CompletionInterpolation(
							"${" , offset - contentWithOffset.getOffsetInIndex() - 2, getItemSet(), getResource()); //$NON-NLS-1$
					return completionInterpolation.getCompletionProposals(offset, context);
				}
				else {
					return null;
				}
			}
			String prefix = contents[index].substring(0, subOffset);
			List l = new ArrayList();
			for (int i=0; i<directives.length; i++) {
				String name = directives[i];
				if (name.startsWith(prefix)) {
					l.add(getCompletionProposal(offset, subOffset,
							name, contents[0]));
				}
			}
			return completionProposals(l);
		}
		else if (index == 1 && !contentWithOffset.wasLastCharSpace()) {
			String value = ""; //$NON-NLS-1$
			try {
				value = contentWithOffset.getContents()[index].substring(0, contentWithOffset.getOffsetInIndex());
			}
			catch (Exception e) {}
			CompletionInterpolation completionInterpolation = new CompletionInterpolation(
					"${" + value , offset - contentWithOffset.getOffsetInIndex() - 2, getItemSet(), getResource()); //$NON-NLS-1$
			return completionInterpolation.getCompletionProposals(offset, context);
		}
		return null;
	}

	public ICompletionProposal[] completionProposals (List l) {
		Collections.sort(l, new CompletionProposalComparator());
		return (ICompletionProposal[]) l.toArray(new ICompletionProposal[l.size()]);
	}

	public ICompletionProposal getCompletionProposal (int offset, int subOffset,
			String replacementString, String replacingString) {
		return new CompletionProposal (
				replacementString, offset-subOffset,
				replacingString.length(), replacementString.length());
	}

	public class CompletionProposalComparator implements Comparator {
		public int compare(Object arg0, Object arg1) {
			return ((ICompletionProposal) arg0).getDisplayString().compareTo(((ICompletionProposal) arg1).getDisplayString());
		}
	}
}