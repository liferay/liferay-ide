/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.ide.kaleo.ui.xml;

import com.liferay.ide.core.util.ListUtil;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.DocumentRewriteSession;
import org.eclipse.jface.text.DocumentRewriteSessionType;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.undo.IStructuredTextUndoManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class KaleoEdits {

	public static final String ARTIFACT_ID = "artifactId";

	public static final String BUILD = "build";

	public static final String CI_MANAGEMENT = "ciManagement";

	public static final String CLASSIFIER = "classifier";

	public static final String CONFIGURATION = "configuration";

	public static final String CONNECTION = "connection";

	public static final String DEPENDENCIES = "dependencies";

	public static final String DEPENDENCY = "dependency";

	public static final String DEPENDENCY_MANAGEMENT = "dependencyManagement";

	public static final String DESCRIPTION = "description";

	public static final String DEV_CONNECTION = "developerConnection";

	public static final String EXCLUSION = "exclusion";

	public static final String EXCLUSIONS = "exclusions";

	public static final String EXECUTION = "execution";

	public static final String EXECUTIONS = "executions";

	public static final String EXTENSION = "extension";

	public static final String EXTENSIONS = "extensions";

	public static final String GOAL = "goal";

	public static final String GOALS = "goals";

	public static final String GROUP_ID = "groupId";

	public static final String ID = "id";

	public static final String INCEPTION_YEAR = "inceptionYear";

	public static final String ISSUE_MANAGEMENT = "issueManagement";

	public static final String MODULE = "module";

	public static final String MODULES = "modules";

	public static final String NAME = "name";

	public static final String OPTIONAL = "optional";

	public static final String ORGANIZATION = "organization";

	public static final String PACKAGING = "packaging";

	public static final String PARENT = "parent";

	public static final String PLUGIN = "plugin";

	public static final String PLUGIN_MANAGEMENT = "pluginManagement";

	public static final String PLUGINS = "plugins";

	public static final String PROFILE = "profile";

	public static final String PROFILES = "profiles";

	public static final String PROPERTIES = "properties";

	public static final String RELATIVE_PATH = "relativePath";

	public static final String SCM = "scm";

	public static final String SCOPE = "scope";

	public static final String SYSTEM = "system";

	public static final String SYSTEM_PATH = "systemPath";

	public static final String TAG = "tag";

	public static final String TYPE = "type";

	public static final String URL = "url";

	public static final String VERSION = "version";

	/**
	 * keeps internal state, needs to be recreated for each query, when used in
	 * conjunction with out matchers shall probably be placed last.
	 *
	 * @param elementName
	 * @param index
	 * @return
	 */
	public static Matcher childAt(int index) {
		return new Matcher() {

			public boolean matches(Element child) {
				if (count == index) {
					return true;
				}

				count++;

				return false;
			}

			public int count = 0;

		};
	}

	public static Matcher childEquals(String elementName, String matchingValue) {
		return new Matcher() {

			public boolean matches(Element child) {
				String toMatch = KaleoEdits.getTextValue(KaleoEdits.findChild(child, elementName));

				if ((toMatch != null) && toMatch.trim().equals(matchingValue)) {
					return true;
				}

				return false;
			}

		};
	}

	public static Matcher childMissingOrEqual(String elementName, String matchingValue) {
		return new Matcher() {

			public boolean matches(Element child) {
				Element match = KaleoEdits.findChild(child, elementName);

				if (match == null) {
					return true;
				}

				String toMatch = KaleoEdits.getTextValue(match);

				if ((toMatch != null) && toMatch.trim().equals(matchingValue)) {
					return true;
				}

				return false;
			}

		};
	}

	/**
	 * helper method, creates a subelement, does not format result.
	 *
	 * @param parent
	 *            the parent element
	 * @param name
	 *            the name of the new element
	 * @return the created element
	 */
	public static Element createElement(Element parent, String name) {
		Document doc = parent.getOwnerDocument();

		Element newElement = doc.createElement(name);

		parent.appendChild(newElement);

		return newElement;
	}

	/**
	 * helper method, creates a subelement with text embedded. does not format
	 * the result. primarily to be used in cases like
	 * <code>&lt;goals&gt;&lt;goal&gt;xxx&lt;/goal&gt;&lt;/goals&gt;</code>
	 *
	 * @param parent
	 * @param name
	 * @param value
	 * @return
	 */
	public static Element createElementWithText(Element parent, String name, String value) {
		Document doc = parent.getOwnerDocument();

		Element newElement = doc.createElement(name);

		parent.appendChild(newElement);

		newElement.appendChild(doc.createTextNode(value));

		return newElement;
	}

	/**
	 * finds the element at offset, if other type of node at offset, will return
	 * it's parent element (if any)
	 *
	 * @param doc
	 * @param offset
	 * @return
	 */
	public static Element elementAtOffset(Document doc, int offset) {
		if (doc instanceof IDOMDocument) {
			IDOMDocument domDoc = (IDOMDocument)doc;

			IndexedRegion ir = domDoc.getModel().getIndexedRegion(offset);

			if (ir instanceof Element) {
				return (Element)ir;
			}
			else {
				Node parent = ((Node)ir).getParentNode();

				if (parent instanceof Element) {
					return (Element)parent;
				}
			}
		}

		return null;
	}

	public static Element findChild(Element parent, String name) {
		if (parent == null) {
			return null;
		}

		NodeList rootList = parent.getChildNodes();

		for (int i = 0; i < rootList.getLength(); i++) {
			Node nd = rootList.item(i);

			if (nd instanceof Element) {
				Element el = (Element)nd;

				if (name.equals(el.getNodeName())) {
					return el;
				}
			}
		}

		return null;
	}

	/**
	 * finds exactly one (first) occurence of child element with the given name
	 * (eg. dependency) that fulfills conditions expressed by the Matchers (eg.
	 * groupId/artifactId match)
	 *
	 * @param parent
	 * @param name
	 * @param matchers
	 * @return
	 */
	public static Element findChild(Element parent, String name, Matcher... matchers) {
		OUTTER: for (Element el : findChilds(parent, name)) {
			for (Matcher match : matchers) {
				if (!match.matches(el)) {
					continue OUTTER;
				}
			}

			return el;
		}

		return null;
	}

	public static List<Element> findChilds(Element parent, String name) {
		List<Element> toRet = new ArrayList<>();

		if (parent != null) {
			NodeList rootList = parent.getChildNodes();

			for (int i = 0; i < rootList.getLength(); i++) {
				Node nd = rootList.item(i);

				if (nd instanceof Element) {
					Element el = (Element)nd;

					if (name.equals(el.getNodeName())) {
						toRet.add(el);
					}
				}
			}
		}

		return toRet;
	}

	/**
	 * formats the node (and content). please make sure to only format the node
	 * you have created..
	 *
	 * @param newNode
	 */
	public static void format(Node newNode) {
		if ((newNode.getParentNode() != null) && newNode.equals(newNode.getParentNode().getLastChild())) {

			// add a new line to get the newly generated content correctly
			// formatted.

			Node parentNode = newNode.getParentNode();

			newNode.getParentNode().appendChild(parentNode.getOwnerDocument().createTextNode("\n"));
		}

		FormatProcessorXML formatProcessor = new FormatProcessorXML();

		// ignore any line width settings, causes wrong formatting of
		// <foo>bar</foo>

		formatProcessor.getFormatPreferences().setLineWidth(2000);
		formatProcessor.formatNode(newNode);
	}

	/**
	 * unlike the findChild() equivalent, this one creates the element if not
	 * present and returns it. Therefore it shall only be invoked within the
	 * PomEdits.Operation
	 *
	 * @param parent
	 * @param names
	 *            chain of element names to find/create
	 * @return
	 */
	public static Element getChild(Element parent, String... names) {
		Element toFormat = null;
		Element toRet = null;

		if (ListUtil.isEmpty(names)) {
			throw new IllegalArgumentException("At least one child name has to be specified");
		}

		for (String name : names) {
			toRet = findChild(parent, name);

			if (toRet == null) {
				Document ownerDocument = parent.getOwnerDocument();

				toRet = ownerDocument.createElement(name);

				parent.appendChild(toRet);

				if (toFormat == null) {
					toFormat = toRet;
				}
			}

			parent = toRet;
		}

		if (toFormat != null) {
			format(toFormat);
		}

		return toRet;
	}

	public static String getTextValue(Node element) {
		if (element == null) {
			return null;
		}

		StringBuffer buff = new StringBuffer();
		NodeList list = element.getChildNodes();

		for (int i = 0; i < list.getLength(); i++) {
			Node child = list.item(i);

			if (child instanceof Text) {
				Text text = (Text)child;

				buff.append(text.getData().trim()); // 352416 the value is

				// trimmed because of the
				// multiline values
				// that get trimmed by maven itself as well, any comparison to
				// resolved model needs to do the trimming
				// or risks false negative results.

			}
		}

		return buff.toString();
	}

	public static Element insertAt(Element newElement, int offset) {
		Document doc = newElement.getOwnerDocument();

		if (doc instanceof IDOMDocument) {
			IDOMDocument domDoc = (IDOMDocument)doc;

			IndexedRegion ir = domDoc.getModel().getIndexedRegion(offset);

			Node parent = ((Node)ir).getParentNode();

			if (ir instanceof Text) {
				Text txt = (Text)ir;

				String data = txt.getData();

				int dataSplitIndex = offset - ir.getStartOffset();

				String beforeText = data.substring(0, dataSplitIndex);

				String afterText = data.substring(dataSplitIndex);

				Text after = doc.createTextNode(afterText);

				Text before = doc.createTextNode(beforeText);

				parent.replaceChild(after, txt);

				parent.insertBefore(newElement, after);

				parent.insertBefore(before, newElement);
			}
			else if (ir instanceof Element) {
				((Element)ir).appendChild(newElement);
			}
			else {
				throw new IllegalArgumentException();
			}
		}
		else {
			throw new IllegalArgumentException();
		}

		return newElement;
	}

	/**
	 * performs an modifying operation on top the
	 *
	 * @param file
	 * @param operation
	 * @throws IOException
	 * @throws CoreException
	 */
	public static void performOnDOMDocument(KaleoEdits.OperationTuple... fileOperations)
		throws CoreException, IOException {

		for (OperationTuple tuple : fileOperations) {
			IDOMModel domModel = null;
			/*
			 * TODO we might want to attempt iterating opened editors and
			 * somehow initialize those that were not yet initialized. Then we
			 * could avoid saving a file that is actually opened, but was never
			 * used so far (after restart)
			 */
			try {
				DocumentRewriteSession session = null;
				IStructuredTextUndoManager undo = null;

				if (tuple.isReadOnly()) {
					IModelManager modelManager = StructuredModelManager.getModelManager();

					domModel = (IDOMModel)modelManager.getExistingModelForRead(tuple.getDocument());

					if (domModel == null) {
						domModel = (IDOMModel)modelManager.getModelForRead((IStructuredDocument)tuple.getDocument());
					}
				}
				else {
					if (tuple.getModel() != null) {
						domModel = tuple.getModel();
					}
					else {
						if (tuple.getFile() != null) {
							domModel =
								(IDOMModel)StructuredModelManager.getModelManager().getModelForEdit(tuple.getFile());
						}
						else {
							IModelManager modelManager = StructuredModelManager.getModelManager();

							domModel = (IDOMModel)modelManager.getExistingModelForEdit(tuple.getDocument());
						}
					}

					// shall be ok here..let the model know we make changes

					domModel.aboutToChangeModel();

					undo = domModel.getStructuredDocument().getUndoManager();

					// let the document know we make changes

					if (domModel.getStructuredDocument() instanceof IDocumentExtension4) {
						IDocumentExtension4 ext4 = (IDocumentExtension4)domModel.getStructuredDocument();

						session = ext4.startRewriteSession(DocumentRewriteSessionType.UNRESTRICTED_SMALL);
					}

					undo.beginRecording(domModel);
				}

				try {
					tuple.getOperation().process(domModel.getDocument());
				}
				finally {
					if (!tuple.isReadOnly()) {
						undo.endRecording(domModel);

						if ((session != null) && domModel.getStructuredDocument() instanceof IDocumentExtension4) {
							IDocumentExtension4 ext4 = (IDocumentExtension4)domModel.getStructuredDocument();

							ext4.stopRewriteSession(session);
						}

						domModel.changedModel();
					}
				}
			}
			finally {
				if (domModel != null) {
					if (tuple.isReadOnly()) {
						domModel.releaseFromRead();
					}
					else {
						/*
						 * for ducuments saving shall generally only happen when
						 * the model is not held elsewhere (eg. in opened view)
						 * for files, save always
						 */
						if (tuple.isForceSave() || (domModel.getReferenceCountForEdit() == 1)) {
							domModel.save();
						}

						domModel.releaseFromEdit();
					}
				}
			}
		}
	}

	/**
	 * proper remove of a child element
	 */
	public static void removeChild(Element parent, Element child) {
		if (child != null) {
			Node prev = child.getPreviousSibling();

			if (prev instanceof Text) {
				Text txt = (Text)prev;

				int lastnewline = _getLastEolIndex(txt.getData());

				if (lastnewline >= 0) {
					txt.setData(txt.getData().substring(0, lastnewline));
				}
			}

			parent.removeChild(child);
		}
	}

	/**
	 * remove the current element if it doesn't contain any sublements, useful
	 * for lists etc, works recursively removing all parents up that don't have
	 * any children elements.
	 *
	 * @param el
	 */
	public static void removeIfNoChildElement(Element el) {
		NodeList nl = el.getChildNodes();
		boolean hasChilds = false;

		for (int i = 0; i < nl.getLength(); i++) {
			Node child = nl.item(i);

			if (child instanceof Element) {
				hasChilds = true;
			}
		}

		if (!hasChilds) {
			Node parent = el.getParentNode();

			if ((parent != null) && parent instanceof Element) {
				removeChild((Element)parent, el);
				removeIfNoChildElement((Element)parent);
			}
		}
	}

	/**
	 * sets text value to the given element. any existing text children are
	 * removed and replaced by this new one.
	 *
	 * @param element
	 * @param value
	 */
	public static void setText(Element element, String value) {
		NodeList list = element.getChildNodes();
		List<Node> toRemove = new ArrayList<>();

		for (int i = 0; i < list.getLength(); i++) {
			Node child = list.item(i);

			if (child instanceof Text) {
				toRemove.add(child);
			}
		}

		for (Node rm : toRemove) {
			element.removeChild(rm);
		}

		Document doc = element.getOwnerDocument();

		element.appendChild(doc.createTextNode(value));
	}

	public static Matcher textEquals(String matchingValue) {
		return new Matcher() {

			public boolean matches(Element child) {
				String toMatch = KaleoEdits.getTextValue(child);

				if ((toMatch != null) && toMatch.trim().equals(matchingValue)) {
					return true;
				}

				return false;
			}

		};
	}

	/**
	 * an Operation instance that aggregates multiple operations and performs
	 * then in given order.
	 *
	 * @author mkleint
	 */
	public static final class CompoundOperation implements Operation {

		public CompoundOperation(Operation... operations) {
			_operations = operations;
		}

		public void process(Document document) {
			for (Operation oper : _operations) {
				oper.process(document);
			}
		}

		private final Operation[] _operations;

	}

	public static final class OperationTuple {

		/**
		 * operation on top of IDocument is only saved when noone else is
		 * editing the document.
		 *
		 * @param document
		 * @param operation
		 */
		public OperationTuple(IDocument document, KaleoEdits.Operation operation) {
			this(document, operation, false);
		}

		/**
		 * operation on top of IDocument is only saved when noone else is
		 * editing the document.
		 *
		 * @param document
		 * @param operation
		 * @param readonly
		 *            operation that doesn't modify the content. Will only get
		 *            the read, not edit model, up to the user of the code to
		 *            ensure no edits happen
		 */
		public OperationTuple(IDocument document, KaleoEdits.Operation operation, boolean readOnly) {
			assert operation != null;
			_document = document;
			_operation = operation;
			_file = null;
			_model = null;
			_readOnly = readOnly;
		}

		/**
		 * only use for unmanaged models
		 *
		 * @param model
		 * @param operation
		 */
		public OperationTuple(IDOMModel model, KaleoEdits.Operation operation) {
			assert model != null;
			_model = model;
			_operation = operation;
			_document = null;
			_file = null;
		}

		/**
		 * operation on top of IFile is always saved
		 *
		 * @param file
		 * @param operation
		 */
		public OperationTuple(IFile file, KaleoEdits.Operation operation) {
			assert file != null;
			assert operation != null;
			_file = file;
			_operation = operation;
			_document = null;
			_model = null;
			_forceSave = true;
		}

		public IDocument getDocument() {
			return _document;
		}

		public IFile getFile() {
			return _file;
		}

		public IDOMModel getModel() {
			return _model;
		}

		public KaleoEdits.Operation getOperation() {
			return _operation;
		}

		public boolean isForceSave() {
			return _forceSave;
		}

		/**
		 * @return Returns the readOnly.
		 */
		public boolean isReadOnly() {
			return _readOnly;
		}

		/**
		 * force saving the document after performing the operation
		 */
		public void setForceSave() {
			_forceSave = true;
		}

		private IDocument _document;
		private IFile _file;
		private boolean _forceSave = false;
		private IDOMModel _model;
		private KaleoEdits.Operation _operation;
		private boolean _readOnly = false;

	}

	/**
	 * an interface for identifying child elements that fulfill conditions
	 * expressed by the matcher.
	 *
	 * @author mkleint
	 */
	public interface Matcher {

		/**
		 * returns true if the given element matches the condition.
		 *
		 * @param child
		 * @return
		 */
		public boolean matches(Element element);

	}

	/**
	 * operation to perform on top of the DOM document. see
	 * performOnDOMDocument()
	 *
	 * @author mkleint
	 */
	public interface Operation {

		public void process(Document document);

	}

	private static int _getLastEolIndex(String s) {
		if ((s == null) || (s.length() == 0)) {
			return -1;
		}

		for (int i = s.length() - 1; i >= 0; i--) {
			char c = s.charAt(i);

			if (c == '\r') {
				return i;
			}

			if (c == '\n') {
				if ((i > 0) && (s.charAt(i - 1) == '\r')) {
					return i - 1;
				}

				return i;
			}
		}

		return -1;
	}

}