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

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.internal.ui.javaeditor.JarEntryEditorInput;
import org.eclipse.jdt.internal.ui.text.JavaPairMatcher;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.MatchingCharacterPainter;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.MarkerUtilities;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.jboss.ide.eclipse.freemarker.Constants;
import org.jboss.ide.eclipse.freemarker.Messages;
import org.jboss.ide.eclipse.freemarker.Plugin;
import org.jboss.ide.eclipse.freemarker.configuration.ConfigurationManager;
import org.jboss.ide.eclipse.freemarker.model.Item;
import org.jboss.ide.eclipse.freemarker.model.ItemSet;
import org.jboss.ide.eclipse.freemarker.outline.OutlinePage;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class Editor extends TextEditor implements KeyListener, MouseListener {

	private OutlinePage fOutlinePage;
	private org.jboss.ide.eclipse.freemarker.editor.Configuration configuration;
	private ColorManager colorManager = new ColorManager();

	private ItemSet itemSet;
	private Item selectedItem;
	private Item[] relatedItems;
	private static final char[] VALIDATION_TOKENS = new char[]{'\"', '[', ']', ',', '.', '\n', '4'};
	private boolean readOnly = false;

	private boolean mouseDown = false;
	private boolean ctrlDown = false;
	private boolean shiftDown = false;

	public Editor() {
		super();
		configuration = new org.jboss.ide.eclipse.freemarker.editor.Configuration(getPreferenceStore(), colorManager, this);
		setSourceViewerConfiguration(configuration);
		setDocumentProvider(new DocumentProvider());
	}
	public void dispose() {
		ConfigurationManager.getInstance(getProject()).reload();
		super.dispose();
		if(matchingCharacterPainter!=null) {
			matchingCharacterPainter.dispose();
		}
	}

	public Object getAdapter(Class aClass) {
	    Object adapter;
		if (aClass.equals(IContentOutlinePage.class)) {
			if (fOutlinePage == null) {
			    fOutlinePage = new OutlinePage(this);
				if (getEditorInput() != null) {
					fOutlinePage.setInput(getEditorInput());
				}
			}
			adapter = fOutlinePage;
		} else {
		    adapter = super.getAdapter(aClass);
		}
		return adapter;
	}

	protected static final char[] BRACKETS= {'{', '}', '(', ')', '[', ']', '<', '>' };
	private MatchingCharacterPainter matchingCharacterPainter;
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		 getSourceViewer().getTextWidget().addKeyListener(this);
		 getSourceViewer().getTextWidget().addMouseListener(this);
		 //matchingCharacterPainter = new MatchingCharacterPainter(
			//	 getSourceViewer(),
				// new JavaPairMatcher(BRACKETS));
		//((SourceViewer) getSourceViewer()).addPainter(matchingCharacterPainter);
	}

	protected void createActions() {
		super.createActions();
		// Add content assist propsal action
		ContentAssistAction action = new ContentAssistAction(
				Plugin.getDefault().getResourceBundle(),
				"FreemarkerEditor.ContentAssist", this); //$NON-NLS-1$
		action.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
		setAction("FreemarkerEditor.ContentAssist", action); //$NON-NLS-1$
		action.setEnabled(true);
	}

	protected void handleCursorPositionChanged() {
		super.handleCursorPositionChanged();
		if (!mouseDown) {
			int offset = getCaretOffset();
			Item item = getItemSet().getSelectedItem(offset);
			if (null == item && offset > 0) item = getItemSet().getSelectedItem(offset-1);
			if (Plugin.getInstance().getPreferenceStore().getBoolean(
					Constants.HIGHLIGHT_RELATED_ITEMS)) {
				if (null != item && null != item.getRelatedItems() && item.getRelatedItems().length > 0) {
					highlightRelatedRegions(item.getRelatedItems(), item);
				}
				else {
					highlightRelatedRegions(null, item);
				}
			}
			if (null == item) {
				item = getItemSet().getContextItem(getCaretOffset());
			}
			if (null != fOutlinePage)
				fOutlinePage.update(item);
		}
	}
	public void mouseDoubleClick(MouseEvent e) {
	}
	public void mouseDown(MouseEvent e) {
		mouseDown = true;
	}
	public void mouseUp(MouseEvent e) {
		mouseDown = false;
		handleCursorPositionChanged();
	}

	public void select (Item item) {
		selectAndReveal(item.getRegion().getOffset(), item.getRegion().getLength());
	}

	public IDocument getDocument() {
		ISourceViewer viewer = getSourceViewer();
		if (viewer != null) {
			return viewer.getDocument();
		}
		return null;
	}

	public ITextViewer getTextViewer() {
		return getSourceViewer();
	}
	
	public void addProblemMarker(String aMessage, int aLine) {
		IFile file = ((IFileEditorInput)getEditorInput()).getFile(); 
		try {
			Map attributes = new HashMap(5);
			attributes.put(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_ERROR));
			attributes.put(IMarker.LINE_NUMBER, new Integer(aLine));
			attributes.put(IMarker.MESSAGE, aMessage);
			attributes.put(IMarker.TEXT, aMessage);
			MarkerUtilities.createMarker(file, attributes, IMarker.PROBLEM);
		} catch (Exception e) {
			
		}
	}

	private synchronized void highlightRelatedRegions (Item[] items, Item selectedItem) {
		if (null == items || items.length == 0) {
			if (null != relatedItems && relatedItems.length > 0) {
				for (int i=0; i<relatedItems.length; i++) {
					if (getDocument().getLength() >= relatedItems[i].getRegion().getOffset() + relatedItems[i].getRegion().getLength()) {
						if (null == this.selectedItem || !relatedItems[i].equals(this.selectedItem))
							resetRange(relatedItems[i].getRegion());
					}
				}
			}
			relatedItems = null;
		}
		if (null != relatedItems) {
			for (int i=0; i<relatedItems.length; i++) {
				if (getDocument().getLength() >= relatedItems[i].getRegion().getOffset() + relatedItems[i].getRegion().getLength()) {
					if (null == this.selectedItem || !relatedItems[i].equals(this.selectedItem))
						resetRange(relatedItems[i].getRegion());
				}
			}
		}
		if (null != items && items.length > 0) {
			for (int i=0; i<items.length; i++) {
				if (getDocument().getLength() >= items[i].getRegion().getOffset() + items[i].getRegion().getLength()
						&& !items[i].equals(selectedItem)) {
					ITypedRegion region = items[i].getRegion();
					getSourceViewer().getTextWidget().setStyleRange(
							new StyleRange(region.getOffset(),
									region.getLength(), null, 
									colorManager.getColor(
											Constants.COLOR_RELATED_ITEM)));
				}
			}
		}
		relatedItems = items;
		this.selectedItem = selectedItem;
	}

	private void resetRange (ITypedRegion region) {
		if (getSourceViewer() instanceof ITextViewerExtension2)
			((ITextViewerExtension2) getSourceViewer()).invalidateTextPresentation(region.getOffset(), region.getLength());
		else
			getSourceViewer().invalidateTextPresentation();
	}

	public Item getSelectedItem (boolean allowFudge) {
		int caretOffset = getCaretOffset();
		Item item = getItemSet().getSelectedItem(getCaretOffset());
		if (null == item && caretOffset > 0) item = getItemSet().getSelectedItem(caretOffset - 1);
		return item;
	}

	public Item getSelectedItem () {
		return getItemSet().getSelectedItem(getCaretOffset());
	}

	public int getCaretOffset () {
		return getSourceViewer().getTextWidget().getCaretOffset();
	}

	private void clearCache () {
		this.itemSet = null;
	}

	public ItemSet getItemSet () {
		if (null == this.itemSet) {
			IResource resource = null;
			if (getEditorInput() instanceof IFileEditorInput) {
				resource = ((IFileEditorInput) getEditorInput()).getFile();
			}
			else if (getEditorInput() instanceof JarEntryEditorInput) {
				resource = null;
			}
			
			this.itemSet = new ItemSet(
					getSourceViewer(), resource);
		}
		return this.itemSet;
			
	}
	public OutlinePage getOutlinePage() {
		return fOutlinePage;
	}

	public void keyPressed(KeyEvent e) {
		if (e.keyCode == SWT.CTRL) {
			ctrlDown = true;
		}
		if (e.keyCode == SWT.SHIFT) {
			shiftDown = true;
		}
		if (e.keyCode == ']') {
			try {
				char c = getDocument().getChar(getCaretOffset());
				if (c == ']') {
					// remove this
					getDocument().replace(getCaretOffset(), 1, ""); //$NON-NLS-1$
				}
			}
			catch (BadLocationException e1) {}
		}
		else if (e.keyCode == '}') {
			try {
				char c = getDocument().getChar(getCaretOffset());
				if (c == '}') {
					// remove this
					getDocument().replace(getCaretOffset(), 1, "}"); //$NON-NLS-1$
				}
			}
			catch (BadLocationException e1) {}
		}
	}

	public void keyReleased(KeyEvent e) {
		if (e.keyCode == SWT.CTRL) {
			ctrlDown = false;
		}
		else if (e.keyCode == SWT.SHIFT) {
			shiftDown = false;
		}
		try {
			if (shiftDown && (e.keyCode == '3' || e.keyCode == '2')) {
				int offset = getCaretOffset();
				char c = getSourceViewer().getDocument().getChar(offset-2);
				if (c == '[' || c == '<') {
					// directive
					char endChar = Character.MIN_VALUE;
					if (c == '[') endChar = ']'; else endChar = '>';
					if (getSourceViewer().getDocument().getLength() > offset) {
						if (offset > 0) {
							for (int i=offset+1; i<getSourceViewer().getDocument().getLength(); i++) {
								char c2 = getSourceViewer().getDocument().getChar(i);
								if (i == endChar) return;
								else if (i == '\n') break;
							}
							getSourceViewer().getDocument().replace(offset, 0, new String(new char[]{endChar}));
						}
					}
					else {
						getSourceViewer().getDocument().replace(offset, 0, new String(new char[]{endChar}));
					}
				}
			}
			else if (shiftDown && e.keyCode == '[') {
				int offset = getCaretOffset();
				char c = getSourceViewer().getDocument().getChar(offset-2);
				if (c == '$') {
					// interpolation
					if (getSourceViewer().getDocument().getLength() > offset) {
						if (offset > 0) {
							for (int i=offset+1; i<getSourceViewer().getDocument().getLength(); i++) {
								char c2 = getSourceViewer().getDocument().getChar(i);
								if (i == '}') return;
								else if (i == '\n') break;
							}
							getSourceViewer().getDocument().replace(offset, 0, "}"); //$NON-NLS-1$
						}
					}
					else {
						getSourceViewer().getDocument().replace(offset, 0, "}"); //$NON-NLS-1$
					}
				}
			}
		}
		catch (BadLocationException exc) {
			// do nothing
		}

		boolean stale = false;
		if (e.keyCode == SWT.DEL || e.keyCode == SWT.BS) {
			stale = true;
		}
		else if (null != getSelectedItem(true)) {
			stale = true;
		}
		else {
			char c = (char) e.keyCode;
			for (int j=0; j<VALIDATION_TOKENS.length; j++) {
				if (c == VALIDATION_TOKENS[j]) {
					stale = true;
					break;
				}
			}
			if (ctrlDown && (e.keyCode == 'v' || e.keyCode == 'x')) {
				stale = true;
			}
		}
		if (stale) {
			int offset = getCaretOffset();
			Item item = getItemSet().getSelectedItem(offset);
			if (null == item && offset > 0) item = getItemSet().getSelectedItem(offset-1);
			if (Plugin.getInstance().getPreferenceStore().getBoolean(
					Constants.HIGHLIGHT_RELATED_ITEMS)) {
				if (null != item && null != item.getRelatedItems() && item.getRelatedItems().length > 0) {
					highlightRelatedRegions(item.getRelatedItems(), item);
				}
				else {
					highlightRelatedRegions(null, item);
				}
			}
			clearCache();
			validateContents();
			if (null != fOutlinePage)
				fOutlinePage.update(getSelectedItem());
		}
	}

	public static Validator VALIDATOR;
	public synchronized void validateContents () {
		if (null == VALIDATOR) {
			VALIDATOR = new Validator(this);
			VALIDATOR.start();
		}
	}

	public IProject getProject () {
		return ((IFileEditorInput) getEditorInput()).getFile().getProject();
	}

	public IFile getFile () {
		return (null != getEditorInput()) ? 
				((IFileEditorInput) getEditorInput()).getFile() : null;
	}

	private Configuration fmConfiguration;
	public class Validator extends Thread {
		Editor editor;
		public Validator (Editor editor) {
			this.editor = editor;
		}
		public void run () {
			try {
				if (null != getFile()) {
					if (null == fmConfiguration) {
						fmConfiguration = new Configuration();
						fmConfiguration.setTagSyntax(Configuration.AUTO_DETECT_TAG_SYNTAX); 
					}
					getFile().deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
					String pageContents = getDocument().get();
					Reader reader = new StringReader(pageContents);
					new Template(getFile().getName(), reader, fmConfiguration);
					reader.close();
				}
			}
			catch (ParseException e) {
				if (e.getMessage() != null) {
					String errorStr = e.getMessage();
					int errorLine = 0;
					try {
						errorLine = e.getLineNumber();
						if (errorLine == 0) {
							// sometimes they forget to put it in
							int index = e.getMessage().indexOf("line: "); //$NON-NLS-1$
							if (index > 0) {
								int index2 = e.getMessage().indexOf(" ", index+6); //$NON-NLS-1$
								int index3 = e.getMessage().indexOf(",", index+6); //$NON-NLS-1$
								if (index3 < index2 && index3 > 0) index2 = index3;
								String s = e.getMessage().substring(index+6, index2);
								try {
									errorLine = Integer.parseInt(s);
								}
								catch (Exception e2) {}
							}
						}
					} catch (NullPointerException npe) {
						errorLine = 0;
					}
					editor.addProblemMarker(errorStr, errorLine);
				}
			}
			catch (Exception e) {
				Plugin.log(e);
			}
			finally {
				editor.VALIDATOR = null;
			}
		}
	}

	protected void editorSaved() {
		super.editorSaved();
		validateContents();
	}

	public boolean isEditorInputReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
}