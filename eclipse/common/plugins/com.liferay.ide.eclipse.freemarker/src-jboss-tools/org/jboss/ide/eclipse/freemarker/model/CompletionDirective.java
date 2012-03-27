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


import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TypedRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.ISourceViewer;
import org.jboss.ide.eclipse.freemarker.Plugin;

public class CompletionDirective extends AbstractDirective {

	private String contents;
	private int offset;
	private int length;
	private AbstractDirective directive = this;

	public CompletionDirective (int offset, int length, ItemSet itemSet, ISourceViewer viewer, IResource resource) throws BadLocationException {
		this.contents = viewer.getDocument().get(offset, length);
		ITypedRegion region = new TypedRegion(offset, this.contents.length(), "default"); //$NON-NLS-1$
		this.offset = offset;
		this.length = length;
		String[] arr = splitContents();
		if (null != arr || arr.length > 0) {
			String s = arr[0];
			try {
				if (s.equals("list")) { //$NON-NLS-1$
					directive = new ListDirective();
					directive.load(new TypedRegion(offset, this.contents.length(), "list"), viewer, resource); //$NON-NLS-1$
					directive.setItemSet(itemSet);
				}
				else if (s.equals("if")) { //$NON-NLS-1$
					directive = new IfDirective();
					directive.load(new TypedRegion(offset, this.contents.length(), "if"), viewer, resource); //$NON-NLS-1$
					directive.setItemSet(itemSet);
				}
			}
			catch (Exception e) {
				Plugin.log(e);
				directive = this;
			}
		}
		try {
			load(region, viewer, resource);
		}
		catch (Exception e) {
			Plugin.log(e);
		}
	}

	public int getOffset() {
		return offset;
	}

	public String getFullContents() {
		return contents;
	}

	public int getLength() {
		return length;
	}

	public ICompletionProposal[] getCompletionProposals(int offset, Map context) {
		if (directive.equals(this)) return super.getCompletionProposals(offset, context);
		else return directive.getCompletionProposals(offset, context);
	}

	protected void init(ITypedRegion region, ISourceViewer viewer, IResource resource) throws Exception {
	}
}