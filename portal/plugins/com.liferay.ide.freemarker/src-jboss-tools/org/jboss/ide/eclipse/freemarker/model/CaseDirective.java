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

public class CaseDirective extends AbstractDirective {

	protected void init(ITypedRegion region, ISourceViewer viewer, IResource resource) throws Exception {
	}

	public boolean isStartAndEndItem() {
		return true;
	}

	public boolean relatesToItem(Item directive) {
		return (directive instanceof CaseDirective
				|| directive instanceof CaseDefaultDirective);
	}

	public boolean isNestable() {
		return true;
	}

	public String getTreeImage() {
		return "case.png"; //$NON-NLS-1$
	}

	public void relateItem(Item directive) {
		if (null == relatedItemsArr) {
			List dRelatedItems = null;
			if (directive instanceof CaseDirective)
				dRelatedItems = ((CaseDirective) directive).getRelatedItemsArray();
			else if (directive instanceof CaseDirective)
				dRelatedItems = ((CaseDefaultDirective) directive).getRelatedItemsArray();
			relatedItemsArr = dRelatedItems;
		}
		if (null == relatedItemsArr)
			relatedItemsArr = new ArrayList();
		if (!relatedItemsArr.contains(directive))
			relatedItemsArr.add(directive);
	}

	public void relateItemNoRecurse (Item directive) {
		if (null == relatedItemsArr)
			relatedItemsArr = new ArrayList();
		if (!relatedItemsArr.contains(directive))
			relatedItemsArr.add(directive);
	}

	public List getRelatedItemsArray () {
		return relatedItemsArr;
	}
}