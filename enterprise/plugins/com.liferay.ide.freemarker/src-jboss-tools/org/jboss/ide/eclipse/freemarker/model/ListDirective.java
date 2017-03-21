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
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.ISourceViewer;


public class ListDirective extends AbstractDirective {

	private ListEndDirective endDirective;

	protected void init(ITypedRegion region, ISourceViewer viewer, IResource resource) throws Exception {
	}

	public boolean isStartItem() {
		return super.isNestable();
	}

	public void relateItem(Item directive) {
		if (directive instanceof ListEndDirective)
			endDirective = (ListEndDirective) directive;
	}

	public boolean relatesToItem(Item directive) {
		return (directive instanceof ListEndDirective);
	}

	public boolean isNestable() {
		return super.isNestable();
	}

	public ListEndDirective getEndDirective() {
		return endDirective;
	}

	public Item getRelatedItem() {
		return getEndDirective();
	}

	public String getTreeImage() {
		return "list.png"; //$NON-NLS-1$
	}

	public ICompletionProposal[] getCompletionProposals(int offset, Map context) {
		ICompletionProposal[] proposals = super.getCompletionProposals(offset, context);
		if (null == proposals) {
			ContentWithOffset contentWithOffset = splitContents(offset);
			int index = contentWithOffset.getIndex();
			if (index == 1 || (index == 0 && contentWithOffset.wasLastCharSpace())) {
				String value = ""; //$NON-NLS-1$
				if (contentWithOffset.getContents().length >= 2)
					value = contentWithOffset.getContents()[1];
				CompletionInterpolation completionInterpolation = new CompletionInterpolation(
							"${" + value, offset - contentWithOffset.getOffsetInIndex() - 2, getItemSet(), getResource()); //$NON-NLS-1$
				proposals = completionInterpolation.getCompletionProposals(offset, context);
			}
		}
		return proposals;
	}

	public void addToContext(Map context) {
		String[] contents = splitContents();
		if (contents.length == 4) {
			String key = contents[3];
			CompletionInterpolation completionInterpolation = new CompletionInterpolation("${" + contents[1], 0, getItemSet(), getResource()); //$NON-NLS-1$
			context.put(key, completionInterpolation.getSingularReturnClass(context));
		}
	}

	public void removeFromContext(Map context) {
		String[] contents = splitContents();
		if (contents.length == 4) {
			String key = contents[3];
			context.remove(key);
		}
	}

	public Item getEndItem() {
		return endDirective;
	}
}