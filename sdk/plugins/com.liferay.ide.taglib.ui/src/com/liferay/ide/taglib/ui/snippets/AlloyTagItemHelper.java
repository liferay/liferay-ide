/*******************************************************************************
 * Copyright (c) 2004, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.liferay.ide.taglib.ui.snippets;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.taglib.ui.TaglibUI;
import com.liferay.ide.taglib.ui.model.Tag;
import com.liferay.ide.ui.snippets.SnippetsUIPlugin;

import java.io.File;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.window.Window;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.snippets.core.ISnippetItem;
import org.eclipse.wst.common.snippets.core.ISnippetsEntry;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class copied from VariableItemHelper.java v1.4
 *
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class AlloyTagItemHelper {

	public static String getInsertString(Shell host, ISnippetItem item, IEditorInput editorInput) {
		return getInsertString(host, item, editorInput, true);
	}

	public static String getInsertString(final Shell host, ISnippetItem item, IEditorInput editorInput, boolean clearModality) {
		if (item == null)
			return StringPool.EMPTY;
		String insertString = null;

		Tag model = getTagModel(editorInput, item);

		AlloyTagInsertDialog dialog = new AlloyTagInsertDialog
        (
            host, model,
            DefinitionLoader.sdef( AlloyTagInsertDialog.class ).dialog( "tagInsertDialog" ), //$NON-NLS-1$
            clearModality
        );

		// VariableInsertionDialog dialog = new TaglibVariableInsertionDialog(host, clearModality);
		// dialog.setItem(item);
		// The editor itself influences the insertion's actions, so we
		// can't
		// allow the active editor to be changed.
		// Disabling the parent shell achieves psuedo-modal behavior
		// without
		// locking the UI under Linux
		int result = Window.CANCEL;
		try {
			if (clearModality) {
				host.setEnabled(false);
				dialog.addDisposeListener(new DisposeListener() {

					public void widgetDisposed(DisposeEvent arg0) {
						/*
						 * The parent shell must be reenabled when the dialog disposes, otherwise it won't automatically
						 * receive focus.
						 */
						host.setEnabled(true);
					}
				});
			}
			result = dialog.open();
		}
		catch (Exception t) {
			SnippetsUIPlugin.logError(t);
		}
		finally {
			if (clearModality) {
				host.setEnabled(true);
			}
		}

		if (result == Window.OK) {
			insertString = dialog.getPreparedText();
		}
		else {
			insertString = null;
		}

		return insertString;
	}

	private static Tag getTagModel(IEditorInput editorInput, ISnippetsEntry item) {
		if (!(editorInput instanceof IFileEditorInput) || item == null) {
			return null;
		}

		IFile tldFile = null;
		XmlResourceStore store = null;
		IFile editorFile = ((IFileEditorInput) editorInput).getFile();

		Document tldDocument = null;
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

		final IProject project = editorFile.getProject();
		// IDE-110 IDE-648
		IVirtualFolder webappRoot = CoreUtil.getDocroot(project);

        if( webappRoot != null )
        {
            for( IContainer container : webappRoot.getUnderlyingFolders() )
            {
                if( container != null && container.exists() )
                {
                    tldFile = container.getFile( new Path( "WEB-INF/tld/alloy.tld") ); //$NON-NLS-1$

                    if (tldFile.exists()) {
                        try {
                            IDOMModel tldModel = (IDOMModel) StructuredModelManager.getModelManager().getModelForRead( tldFile );
                            tldDocument = tldModel.getDocument();
                        }
                        catch (Exception e) {
                            SnippetsUIPlugin.logError(e);
                        }

                        break;
                    }
                }
            }
        }


		if( tldDocument == null )
		{
			// read alloy from plugin
			try {
				URL alloyURL = FileLocator.toFileURL( TaglibUI.getDefault().getBundle().getEntry( "deps/alloy.tld" ) ); //$NON-NLS-1$
				File alloyFile = new File(alloyURL.getFile());
				tldDocument = docFactory.newDocumentBuilder().parse(alloyFile);
			}
			catch (Exception e) {
				SnippetsUIPlugin.logError(e);
			}
		}

		if (tldDocument == null) {
			return null;
		}

		try {
			NodeList tags = tldDocument.getElementsByTagName("tag"); //$NON-NLS-1$

			Element alloyTag = null;

			for (int i = 0; i < tags.getLength(); i++) {
				Element tag = (Element) tags.item(i);

				NodeList children = tag.getElementsByTagName("name"); //$NON-NLS-1$

				if (children.getLength() > 0) {
					String name = children.item(0).getChildNodes().item(0).getNodeValue();

					if (item.getLabel().equals(name)) {
						alloyTag = tag;
						break;
					}
				}
			}

			if (alloyTag == null) {
				return null;
			}

			// build XML model to be used by sapphire dialog

			DocumentBuilder newDocumentBuilder = docFactory.newDocumentBuilder();
			Document doc = newDocumentBuilder.newDocument();
			Element destTag = doc.createElement("tag"); //$NON-NLS-1$

			Element name = doc.createElement("name"); //$NON-NLS-1$
			name.appendChild(doc.createTextNode(item.getLabel()));
			destTag.appendChild(name);

			Element prefix = doc.createElement("prefix"); //$NON-NLS-1$
			prefix.appendChild(doc.createTextNode("alloy")); //$NON-NLS-1$
			destTag.appendChild(prefix);

			Element required = (Element) destTag.appendChild(doc.createElement("required")); //$NON-NLS-1$
			Element events = (Element) destTag.appendChild(doc.createElement("events")); //$NON-NLS-1$
			Element other = (Element) destTag.appendChild(doc.createElement("other")); //$NON-NLS-1$

			NodeList attrs = alloyTag.getElementsByTagName("attribute"); //$NON-NLS-1$
			for (int i = 0; i < attrs.getLength(); i++) {
				try {
					Element attr = (Element) attrs.item(i);
					String desc =
						( (Element) attr.getElementsByTagName( "description" ).item( 0 ) ).getFirstChild().getNodeValue(); //$NON-NLS-1$
					String json = desc.substring(desc.indexOf("<!--") + 4, desc.indexOf("-->")); //$NON-NLS-1$ //$NON-NLS-2$
					JSONObject jsonObject = new JSONObject(json);
					Node newAttr = null;

					if (jsonObject.getBoolean("required")) { //$NON-NLS-1$
						newAttr = required.appendChild(doc.importNode(attr, true));
					}
					else if (jsonObject.getBoolean("event")) { //$NON-NLS-1$
						newAttr = events.appendChild(doc.importNode(attr, true));
					}
					else {
						newAttr = other.appendChild(doc.importNode(attr, true));
					}

					if (jsonObject.has("defaultValue")) { //$NON-NLS-1$
						Element defaultValElement = doc.createElement("default-value"); //$NON-NLS-1$
						defaultValElement.appendChild(doc.createTextNode(jsonObject.get("defaultValue").toString())); //$NON-NLS-1$
						newAttr.appendChild(defaultValElement);
					}
				}
				catch (Exception e) {
					TaglibUI.logError( e );
				}
			}

			doc.appendChild(destTag);

			Transformer trans = TransformerFactory.newInstance().newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); //$NON-NLS-1$

			// create string from xml tree
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(doc);
			trans.transform(source, result);
			String xmlString = sw.toString();

			store = new XmlResourceStore(xmlString.getBytes());
		}
		catch (Exception e) {
			TaglibUI.logError( e );
		}

		return Tag.TYPE.instantiate(new RootXmlResource(store));
	}

}
