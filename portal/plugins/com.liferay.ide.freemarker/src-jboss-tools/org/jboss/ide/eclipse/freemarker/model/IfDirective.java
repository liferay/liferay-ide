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
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.source.ISourceViewer;

public class IfDirective extends AbstractDirective {

	private IfEndDirective endDirective;
	private IfElseDirective elseDirective;
	private List elseIfDirectives = new ArrayList(1);

	protected void init(ITypedRegion region, ISourceViewer viewer, IResource resource) throws Exception {
	}

	public boolean isStartItem() {
		return true;
	}

	public void relateItem(Item directive) {
		if (directive instanceof IfElseDirective)
			elseDirective = (IfElseDirective) directive;
		else if (directive instanceof IfEndDirective)
			endDirective = (IfEndDirective) directive;
		else if (directive instanceof ElseIfDirective)
			elseIfDirectives.add(directive);
	}

	public boolean relatesToItem(Item directive) {
		return (directive instanceof IfDirective
				|| directive instanceof IfElseDirective
				|| directive instanceof ElseIfDirective
				|| directive instanceof IfEndDirective);
	}

	public boolean isNestable() {
		return true;
	}

	public IfElseDirective getElseDirective() {
		return elseDirective;
	}

	public IfEndDirective getEndDirective() {
		return endDirective;
	}

	public List getElseIfDirectives () {
		return elseIfDirectives;
	}

	public Item[] getRelatedItems() {
		if (null == relatedItems) {
			ArrayList l = new ArrayList();
			l.add(this);
			if (null != getElseDirective())
				l.add(getElseDirective());
			if (null != getEndDirective())
				l.add(getEndDirective());
			l.addAll(getElseIfDirectives());
			relatedItems = (Item[]) l.toArray(new Item[l.size()]);
		}
		return relatedItems;
	}
	private Item[] relatedItems;

	public String getTreeImage() {
		return "if.png"; //$NON-NLS-1$
	}

	public Item getEndItem() {
		return endDirective;
	}

//	public ICompletionProposal[] getCompletionProposals(int offset, Map context) {
//		ICompletionProposal[] proposals = super.getCompletionProposals(offset, context);
//		if (null == proposals) {
//			ContentWithOffset contentWithOffset = splitContents(offset);
//			if (contentWithOffset.getIndex() > 0 || contentWithOffset.wasLastCharSpace()) {
//				int index = contentWithOffset.getIndex();
//				int offsetInIndex = contentWithOffset.getOffsetInIndex();
//				int indexOffset = contentWithOffset.getIndexOffset();
//				boolean wasLastCharSpace = contentWithOffset.wasLastCharSpace();
//				String contents = contentWithOffset.getContents()[index];
//				if (wasLastCharSpace || contentWithOffset.getContents()[index-1].equals("=") || offsetInIndex == 0) {
//					// first character
//					if (wasLastCharSpace) {
//						proposals = new CompletionInterpolation(
//								"${", offset-2, getItemSet(), getResource())
//								.getCompletionProposals(offset, context);
//					}
//					else {
//						proposals = new CompletionInterpolation(
//								"${" + contents, indexOffset-2, getItemSet(), getResource())
//								.getCompletionProposals(offset, context);
//					}
//				}
//				else {
//					proposals = new CompletionInterpolation(
//							"${" + contents, indexOffset-2, getItemSet(), getResource())
//							.getCompletionProposals(offset, context);
//				}
//			}
//		}
//		return proposals;
//	}
}