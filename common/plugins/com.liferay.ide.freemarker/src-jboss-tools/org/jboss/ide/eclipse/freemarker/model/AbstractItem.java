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
import java.util.Map;
import java.util.Stack;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.ISourceViewer;
import org.jboss.ide.eclipse.freemarker.Plugin;

public abstract class AbstractItem implements Item {

	private ITypedRegion region;
	private ISourceViewer viewer;
	private IResource resource;
	private List subDirectives;
	private Item parentItem;
	private ItemSet itemSet;
	
	public final void load(ITypedRegion region, ISourceViewer viewer, IResource resource) {
		this.region = region;
		this.viewer = viewer;
		this.resource = resource;
		try {
			init(region, viewer, resource);
		}
		catch (Exception e) {
			Plugin.log(e);
		}
	}

	protected abstract void init (ITypedRegion region, ISourceViewer viewer, IResource resource) throws Exception;

	public boolean isStartItem() {
		return false;
	}

	public boolean isEndItem() {
		return false;
	}

	public boolean relatesToItem(Item directive) {
		return false;
	}

	public void relateItem(Item directive) {
		if (null == relatedItemsArr)
			relatedItemsArr = new ArrayList();
		relatedItemsArr.add(directive);
	}

	public boolean isNestable() {
		return (null != getContents() && !getContents().endsWith("/")); //$NON-NLS-1$
	}

	public ITypedRegion getRegion() {
		return region;
	}

	public List getChildItems() {
		if (null == subDirectives) {
			subDirectives = new ArrayList(0);
		}
		return subDirectives;
	}

	public void addSubDirective(Item directive) {
		getChildItems().add(directive);
		directive.setParentItem(this);
	}

	public ISourceViewer getViewer() {
		return viewer;
	}

	protected Item getRelatedItem() {
		return null;
	}

	protected Item[] relatedItems;
	protected List relatedItemsArr;
	public Item[] getRelatedItems() {
		if (null == relatedItems) {
			if (null != relatedItemsArr) {
				relatedItems = (Item[]) relatedItemsArr.toArray(new Item[relatedItemsArr.size()]);
			}
			else if (null == getRelatedItem()) {
				relatedItems = new Item[0];
			}
			else {
				relatedItems = new Item[] {getRelatedItem()};
			}
		}
		return relatedItems;
	}

	private String contents;
	public String getContents () {
		if (null == contents) {
			contents = getFullContents();
			if (null != contents) contents = contents.trim();
		}
		return contents;
	}

	private ContentWithOffset standardSplit;
	public ContentWithOffset splitContents (int offset) {
		if (offset == -1 && null != standardSplit) return standardSplit;
		String s = getFullContents();
		if (null == s) {
			return new ContentWithOffset(new String[0], -1, -1, -1, -1, -1, -1, false, false);
		}
		int actualIndex = 0;
		int actualIndexOffset = 0;
		int actualOffset = 0;
		int indexOffset = 0;
		int offsetCount = 0;
		int totalOffsetCount = 0;
		int spacesEncountered = 0;
		int totalSpacesEncountered = 0;
		int cursorPos = getCursorPosition(offset);
		List arr = new ArrayList();
		StringBuffer current = new StringBuffer();
		Stack currentStack = new Stack();
		boolean escape = false;
		boolean doEscape = false;
		boolean doAppend = true;
		boolean encounteredSpace = false;
		boolean nextCharSpace = false;
		for (int i=0; i<s.length(); i++) {
			encounteredSpace = false;
			char c = s.charAt(i);
			if (totalOffsetCount == cursorPos) {
				actualIndex = arr.size();
				actualOffset = totalOffsetCount;
				indexOffset = offsetCount;
				actualIndexOffset = offset - cursorPos - indexOffset;
				if (c == ' ') nextCharSpace = true;
			}
			totalOffsetCount++;
			if (c == ' ' || c == '=' || c == '\r' || c == '\n') {
				// we're probably going to split here
				if (current.length() != 0) {
					if (currentStack.size() == 0) {
						arr.add(current.toString());
						current = new StringBuffer();
						offsetCount = 0;
						if (c == '=') {
							arr.add("="); //$NON-NLS-1$
							current = new StringBuffer();
						}
						else {
							encounteredSpace = true;
							spacesEncountered ++;
							totalSpacesEncountered ++;
						}
					}
					doAppend = false;
				}
				else {
					// just continue
				}
			}
			if (!escape) {
				if (c == '\"') {
					if (currentStack.size() > 0) {
						if (currentStack.peek() == "\"") //$NON-NLS-1$
							currentStack.pop();
						else
							currentStack.push("\""); //$NON-NLS-1$
					}
					else
						currentStack.push("\""); //$NON-NLS-1$
					
				}
				else if (c == '(') {
					currentStack.push("("); //$NON-NLS-1$
				}
				else if (c == ')') {
					if (currentStack.size() > 0 && currentStack.peek().equals(")")) //$NON-NLS-1$
						currentStack.pop();
				}
				else if (c == '{') {
					currentStack.push("{"); //$NON-NLS-1$
				}
				else if (c == '}') {
					if (currentStack.size() > 0 && currentStack.peek().equals("}")) //$NON-NLS-1$
						currentStack.pop();
				}
				else if (c == '\\') {
					doEscape = true;
				}
				else {
					for (int j=0; j<getDescriptors().length; j++) {
						if (c == getDescriptors()[j]) {
							doAppend = false;
							break;
						}
					}
				}
			}
			if (doAppend) {
				current.append(c);
				offsetCount++;
			}
			escape = doEscape;
			doEscape = false;
			doAppend = true;
		}
		if (current.length() > 0) {
			arr.add(current.toString());
			if (totalOffsetCount == cursorPos) {
				actualOffset = totalOffsetCount;
				indexOffset = offsetCount;
				actualIndexOffset = offset - cursorPos - indexOffset;
			}
		}
		else if (arr.size() == 0) {
			arr.add(""); //$NON-NLS-1$
		}
		if (totalOffsetCount == cursorPos) {
			actualIndex = arr.size()-1;
			actualOffset = totalOffsetCount;
			indexOffset = offsetCount;
			actualIndexOffset = offset - cursorPos - indexOffset;
		}
		ContentWithOffset contentWithOffset = new ContentWithOffset(
				(String[]) arr.toArray(new String[arr.size()]),
				actualIndex, actualIndexOffset, indexOffset, actualOffset, spacesEncountered,
				totalSpacesEncountered, encounteredSpace, nextCharSpace);
		if (offset == -1) standardSplit = contentWithOffset;
		return contentWithOffset;
	}

	protected int getCursorPosition (int offset) {
		return offset - getOffset();
	}

	public String[] splitContents () {
		ContentWithOffset rtn = splitContents(-1);
		return rtn.getContents();
	}

	public class ContentWithOffset {
		private String[] contents;
		private int index;
		private int indexOffset;
		private int offsetInIndex;
		private int offset;
		private int spacesEncountered;
		private int totalSpacesEncountered;
		private boolean wasLastCharSpace;
		private boolean isNextCharSpace;

		public ContentWithOffset (String[] contents, int index, int indexOffset, int offsetInIndex,
				int offset, int spacesEncountered, int totalSpacesEncountered,
				boolean wasLastCharSpace, boolean isNextCharSpace) {
			this.contents = contents;
			this.index = index;
			this.offsetInIndex = offsetInIndex;
			this.indexOffset = indexOffset;
			this.offset = offset;
			this.spacesEncountered = spacesEncountered;
			this.totalSpacesEncountered = totalSpacesEncountered;
			this.wasLastCharSpace = wasLastCharSpace;
			this.isNextCharSpace = isNextCharSpace;
		}
		
		public String[] getContents() {
			return contents;
		}
		public void setContents(String[] contents) {
			this.contents = contents;
		}
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public int getOffset() {
			return offset;
		}
		public void setOffset(int offset) {
			this.offset = offset;
		}

		public int getOffsetInIndex() {
			return offsetInIndex;
		}

		public int getSpacesEncountered() {
			return spacesEncountered;
		}

		public int getTotalSpacesEncountered() {
			return totalSpacesEncountered;
		}

		public boolean wasLastCharSpace() {
			return wasLastCharSpace;
		}

		public boolean isNextCharSpace() {
			return isNextCharSpace;
		}

		public int getIndexOffset() {
			return indexOffset;
		}
	}

	public Item getParentItem() {
		return parentItem;
	}

	public void setParentItem(Item parentItem) {
		this.parentItem = parentItem;
	}

	public Item getStartItem () {
		return this;
	}

	public boolean equals(Object arg0) {
		if (arg0 instanceof Item) {
			return ((Item) arg0).getRegion().equals(getRegion());
		}
		else return false;
	}

	public int hashCode() {
		return getRegion().hashCode();
	}

	private String treeDisplay;
	public String getTreeDisplay() {
		if (null == treeDisplay) {
			treeDisplay = getContents();
			if (null != treeDisplay && treeDisplay.endsWith("/")) //$NON-NLS-1$
				treeDisplay = treeDisplay.substring(0, treeDisplay.length()-1);
		}
		return treeDisplay;
	}

	public String getTreeImage() {
		return null;
	}

	public boolean isStartAndEndItem() {
		return false;
	}

	public String getSplitValue (int index) {
		String[] values = splitContents();
		if (null != values && values.length > index)
			return values[index];
		else return null;
	}

	public ICompletionProposal[] getCompletionProposals(int offset, Map context) {
		return null;
	}

	private static final char[] descriptorTokens = new char[]{'/','#','@','[',']','<','>'};
	public char[] getDescriptors () {
		return descriptorTokens;
	}

	public ItemSet getItemSet() {
		return itemSet;
	}

	public void setItemSet(ItemSet itemSet) {
		this.itemSet = itemSet;
	}

	public String getFullContents () {
		try {
			return viewer.getDocument().get(
					region.getOffset(), region.getLength());
		}
		catch (BadLocationException e) {
			return null;
		}
	}

	public int getOffset () {
		return getRegion().getOffset();
	}

	public int getLength () {
		return getRegion().getLength();
	}

	String firstToken = null;
	public String getFirstToken() {
		if (null == firstToken) {
			StringBuffer sb = new StringBuffer();
			String content = getContents();
			for (int i=0; i<content.length(); i++) {
				char c = content.charAt(i);
				if (c == '\"') return null;
				else if (c == '?') {
					firstToken = sb.toString();
					break;
				}
				else if (c == ' ' || c == '(' || c == ')' && sb.length() > 0) {
					firstToken = sb.toString();
					break;
				}
				else sb.append(c);
			}
		}
		return firstToken;
	}

	public IResource getResource() {
		return resource;
	}

	public void setResource(IResource resource) {
		this.resource = resource;
	}

	public void addToContext(Map context) {
	}

	public void removeFromContext(Map context) {
	}

	public Item getEndItem() {
		return null;
	}

	public String getName() {
		return getFirstToken();
	}
}