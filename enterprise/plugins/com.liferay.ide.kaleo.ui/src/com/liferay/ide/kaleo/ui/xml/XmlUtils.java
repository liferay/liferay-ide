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

import java.io.File;
import java.io.IOException;

import java.util.List;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Mkleint
 */
public class XmlUtils {

	/**
	 * what is this method supposed to do? for the sourceViewer find the
	 * associated file on disk and for that one find the IProject it belongs to.
	 * The required condition for the IProject instance is that project relative
	 * path of the file shall only be pom.xml (thus no nested, unopened maven
	 * pom). So that when MavenPlugin.getMavenProjectManager().getProject(prj);
	 * is called later on the instance, it actually returns the maven model
	 * facade for the pom.xml backing the sourceViewer.
	 *
	 * @param sourceViewer
	 * @return
	 */
	public static IProject extractProject(ITextViewer sourceViewer) {
		ITextFileBuffer buf = FileBuffers.getTextFileBufferManager().getTextFileBuffer(sourceViewer.getDocument());

		if (buf == null) {

			// eg. for viewers of pom files in local repository

			return null;
		}

		IFileStore folder = buf.getFileStore();

		File file = new File(folder.toURI());

		IPath path = Path.fromOSString(file.getAbsolutePath());

		/*
		 * Stack<IFile> stack = new Stack<>(); here we need to find the most
		 * inner project to the path. we do so by shortening the path and
		 * remembering all the resources identified. at the end we pick the last
		 * one from the stack. is there a catch to it?
		 */
		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		IWorkspaceRoot workspaceRoot = workspace.getRoot();

		IFile ifile = workspaceRoot.getFileForLocation(path);

		if (ifile != null) {
			return ifile.getProject();

			// stack.push(ifile);

		}
		/*
		 * while(path.segmentCount() > 1) { IResource ires =
		 * ResourcesPlugin.getWorkspace().getRoot().findMember(path); if((ires
		 * != null) && ires instanceof IFile) { stack.push((IFile)ires); } path
		 * = path.removeFirstSegments(1); } IFile res = stack.empty() ? null :
		 * stack.pop(); if (res != null) { IProject prj = res.getProject(); the
		 * project returned is in a way unrelated to nested child poms that
		 * don't have an opened project, in that case we pass along a wrong
		 * parent/aggregator if (res.getProjectRelativePath().segmentCount() !=
		 * 1) { if the project were the pom's project, the relative path would
		 * be just "pom.xml", if it's not just throw it out of the window.. prj
		 * = null; } return prj;
		 */
		return null;
	}

	public static Element findChild(Element parent, String name) {
		return KaleoEdits.findChild(parent, name);
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
	public static Element findChild(Element parent, String name, KaleoEdits.Matcher... matchers) {
		return KaleoEdits.findChild(parent, name, matchers);
	}

	public static List<Element> findChilds(Element parent, String name) {
		return KaleoEdits.findChilds(parent, name);
	}

	public static String getTextValue(Node element) {
		return KaleoEdits.getTextValue(element);
	}

	/**
	 * calculates the path of the node up in the hierarchy, example of result is
	 * project/build/plugins/plugin level parameter designates the number of
	 * parents to climb eg. for level 2 the result would be plugins/plugin level
	 * -1 means all the way to the top.
	 */
	public static String pathUp(Node node, int level) {
		StringBuffer buf = new StringBuffer();

		int current = level;

		while ((node != null) && (current > 0)) {
			if (node instanceof Element) {
				if (buf.length() > 0) {
					buf.insert(0, "/");
				}

				buf.insert(0, node.getNodeName());
				current = current - 1;
			}

			node = node.getParentNode();
		}

		return buf.toString();
	}

	/**
	 * originally copied from
	 * org.eclipse.wst.xml.ui.internal.hyperlink.XMLHyperlinkDetector this
	 * method grabs the IDOMModel for the IDocument, performs the passed
	 * operation on the node at the offset and then releases the IDOMModel
	 * operation's Node value is also an instance of IndexedRegion
	 *
	 * @param offset
	 */
	public static void performOnCurrentElement(IDocument document, int offset, NodeOperation<Node> operation) {
		assert document != null;
		assert operation != null;
		/*
		 * get the current node at the offset (returns either: element, doctype,
		 * text)
		 */
		IStructuredModel sModel = null;

		try {
			sModel = StructuredModelManager.getModelManager().getExistingModelForRead(document);

			if (sModel != null) {
				IndexedRegion inode = sModel.getIndexedRegion(offset);

				if (inode == null) {
					inode = sModel.getIndexedRegion(offset - 1);
				}

				if (inode instanceof Node) {
					operation.process((Node)inode, sModel.getStructuredDocument());
				}
			}
		}
		finally {
			if (sModel != null) {
				sModel.releaseFromRead();
			}
		}
	}

	/**
	 * this method grabs the IDOMModel for the IDocument, performs the passed
	 * operation on the root element of the document and then releases the
	 * IDOMModel root Element value is also an instance of IndexedRegion
	 *
	 * @param doc
	 * @param operation
	 */
	public static void performOnRootElement(IDocument doc, NodeOperation<Element> operation) {
		assert doc != null;
		assert operation != null;

		IDOMModel domModel = null;

		try {
			domModel = (IDOMModel)StructuredModelManager.getModelManager().getExistingModelForRead(doc);

			if (domModel == null) {
				throw new IllegalArgumentException("Document is not structured: " + doc);
			}

			IStructuredDocument document = domModel.getStructuredDocument();

			Element root = domModel.getDocument().getDocumentElement();

			operation.process(root, document);
		}
		finally {
			if (domModel != null) {
				domModel.releaseFromRead();
			}
		}
	}

	public static void performOnRootElement(IFile resource, NodeOperation<Element> operation)
		throws CoreException, IOException {

		assert resource != null;
		assert operation != null;

		IDOMModel domModel = null;

		try {
			domModel = (IDOMModel)StructuredModelManager.getModelManager().getModelForRead(resource);

			if (domModel == null) {
				throw new IllegalArgumentException("Document is not structured: " + resource);
			}

			IStructuredDocument document = domModel.getStructuredDocument();

			Element root = domModel.getDocument().getDocumentElement();

			operation.process(root, document);
		}
		finally {
			if (domModel != null) {
				domModel.releaseFromRead();
			}
		}
	}

}