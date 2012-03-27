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


import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.swt.graphics.Image;
import org.jboss.ide.eclipse.freemarker.Plugin;
import org.jboss.ide.eclipse.freemarker.configuration.ConfigurationManager;
import org.jboss.ide.eclipse.freemarker.configuration.ContextValue;
import org.jboss.ide.eclipse.freemarker.model.CompletionDirective;
import org.jboss.ide.eclipse.freemarker.model.CompletionInterpolation;
import org.jboss.ide.eclipse.freemarker.model.CompletionMacroInstance;
import org.jboss.ide.eclipse.freemarker.model.Item;
import org.jboss.ide.eclipse.freemarker.model.ItemSet;
import org.jboss.ide.eclipse.freemarker.model.MacroInstance;

/**
 * @author <a href="mailto:joe@binamics.com">Joe Hudson</a>
 */
public class CompletionProcessor extends TemplateCompletionProcessor implements IContentAssistProcessor {

	private Editor editor;
	
	private static final ICompletionProposal[] NO_COMPLETIONS = new ICompletionProposal[0];
	
	public CompletionProcessor (Editor editor) {
		this.editor = editor;
	}
	
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		try {
			ItemSet directiveSet = editor.getItemSet();
	
			Map context = new HashMap();
			ContextValue[] values = ConfigurationManager.getInstance(editor.getProject()).getContextValues(editor.getFile(), true);
			for (int i=0; i<values.length; i++) {
				context.put(values[i].name, values[i].objClass);
			}
			
			Item directive = directiveSet.getSelectedItem(offset);
			if (null != directive) {
				return directive.getCompletionProposals(offset, context);
			}
			else {
				// we might be starting something
				Item item = editor.getItemSet().getPreviousItem(offset);
				int topOffset = 0;
				if (null != item) topOffset = item.getRegion().getOffset() + item.getRegion().getLength();
				// check for directives and macro calls
				try {
					for (int i=offset-1; i>=topOffset; i--) {
						char c = editor.getDocument().getChar(i);
						if (c == '>' || c == ']') break;
						if (c == '<' || c == '[') {
							if (editor.getDocument().getLength() > i) {
								char c2 = editor.getDocument().getChar(i+1);
								if (c2 == '#') {
									CompletionDirective completionDirective = new CompletionDirective(
											i, offset - i, editor.getItemSet(), (ISourceViewer) viewer, (IResource) editor.getFile());
									completionDirective.setItemSet(editor.getItemSet());
									return completionDirective.getCompletionProposals(offset, context);
								}
								else if (c2 == '@') {
									CompletionMacroInstance completionMacroInstance = new CompletionMacroInstance(
											editor.getDocument().get(i, offset - i), i, editor.getItemSet(), editor.getFile());
									completionMacroInstance.setItemSet(editor.getItemSet());
									return completionMacroInstance.getCompletionProposals(offset, context);
								}
								else if (c2 == '/') {
									if (editor.getDocument().getLength() < i+3
											|| editor.getDocument().getChar(i+2) == ' '
												|| editor.getDocument().getChar(i+2) == '\r'
													|| editor.getDocument().getChar(i+2) == '\n') {
										Item stackItem = editor.getItemSet().getPreviousStartItem(offset);
										StringBuffer value = new StringBuffer();
										if (null != stackItem && stackItem instanceof MacroInstance)
											value.append("@"); //$NON-NLS-1$
										else
											value.append("#"); //$NON-NLS-1$
										String name = null;
										if (null != stackItem) name = stackItem.getFirstToken();
										if (null != name)
											value.append(name);
										if (c == '<')
											value.append('>');
										else 
											value.append(']');
										ICompletionProposal completionProposal = new CompletionProposal(
												value.toString(), offset, 0, offset+value.toString().length());
										return new ICompletionProposal[]{completionProposal};
									}
								}
								else {
									return NO_COMPLETIONS;
								}
							}
						}
					}
				}
				catch (BadLocationException e) {
					return NO_COMPLETIONS;
				}
				// check for interpolations
				try {
					for (int i=offset-1; i>=topOffset; i--) {
						char c = editor.getDocument().getChar(i);
						if (c == '\n') break;
						else if (c == '$') {
							if (editor.getDocument().getLength() > i) {
								char c2 = editor.getDocument().getChar(i+1);
								if (c2 == '{') {
									int j = offset;
									while (editor.getDocument().getLength() > j) {
										char c3 = editor.getDocument().getChar(j);
										if (Character.isWhitespace(c3) || c3 == '(' || c3 == '.' || c3 == ')' || c3 == '}' || c3 == '?') {
											// j = j-1;
											break;
										}
										j++;
									}
									CompletionInterpolation interpolation = new CompletionInterpolation(
											editor.getDocument().get(i, j - i), i, editor.getItemSet(), editor.getFile());
									interpolation.setParentItem(editor.getItemSet().getPreviousStartItem(offset));
									return interpolation.getCompletionProposals(offset, context);
								}
							}
						}
					}
				}
				catch (BadLocationException e) {
					return NO_COMPLETIONS;
				}
			}
		}
		catch (Exception e) {
			Plugin.log(e);
		}
		return NO_COMPLETIONS;
	}

	protected TemplateContextType getContextType(ITextViewer viewer, IRegion region) {
		return null;
	}

	protected Image getImage(Template template) {
		return null;
	}

	protected Template[] getTemplates(String contextTypeId) {
		return null;
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[]{'.', '$', '#', '@', '/', '?', '{'};
	}
}