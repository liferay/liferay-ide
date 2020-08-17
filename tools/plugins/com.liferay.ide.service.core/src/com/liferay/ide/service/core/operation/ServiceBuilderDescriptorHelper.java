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

package com.liferay.ide.service.core.operation;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.NodeUtil;
import com.liferay.ide.project.core.descriptor.LiferayDescriptorHelper;
import com.liferay.ide.project.core.descriptor.RemoveSampleElementsOperation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Kuo Zhang
 */
@SuppressWarnings("restriction")
public class ServiceBuilderDescriptorHelper extends LiferayDescriptorHelper {

	public ServiceBuilderDescriptorHelper() {
	}

	public ServiceBuilderDescriptorHelper(IFile serviceXMLFile) {
		_serviceXMLFile = serviceXMLFile;
	}

	public ServiceBuilderDescriptorHelper(IProject project) {
		super(project);
	}

	public IStatus addDefaultColumns(String elementName) {
		DOMModelEditOperation editOperation = new DOMModelEditOperation(getDescriptorFile()) {

			@Override
			protected IStatus doExecute(IDOMDocument document) {
				return doAddDefaultColumns(document, elementName);
			}

		};

		return editOperation.execute();
	}

	public IStatus addDefaultEntity() {
		IStatus status = Status.OK_STATUS;

		IFile descriptorFile = getDescriptorFile();

		if (descriptorFile != null) {
			DOMModelEditOperation editOperation = new DOMModelEditOperation(descriptorFile) {

				@Override
				protected IStatus doExecute(IDOMDocument document) {
					return doAddDefaultEntity(document);
				}

			};

			status = editOperation.execute();
		}

		return status;
	}

	public IStatus addEntity(String entityName) {
		IFile descriptorFile = getDescriptorFile();

		if (FileUtil.notExists(descriptorFile)) {
			return Status.OK_STATUS;
		}

		DOMModelEditOperation editOperation = new DOMModelEditOperation(descriptorFile) {

			@Override
			protected IStatus doExecute(IDOMDocument document) {
				return doAddEntity(document, entityName);
			}

		};

		return editOperation.execute();
	}

	@Override
	public IFile getDescriptorFile() {
		if ((_serviceXMLFile != null) && _serviceXMLFile.exists()) {
			return _serviceXMLFile;
		}

		IFile serviceXmlFile = super.getDescriptorFile(ILiferayConstants.SERVICE_XML_FILE);

		if ((serviceXmlFile != null) && serviceXmlFile.exists()) {
			return serviceXmlFile;
		}

		return null;
	}

	public IStatus removeAllEntities() {
		IFile descriptorFile = getDescriptorFile();

		if (FileUtil.notExists(descriptorFile)) {
			return Status.OK_STATUS;
		}

		String tagName = "entity";

		DOMModelEditOperation editOperation = new DOMModelEditOperation(descriptorFile) {

			@Override
			protected IStatus doExecute(IDOMDocument document) {
				return removeAllElements(document, tagName);
			}

		};

		return editOperation.execute();
	}

	@Override
	protected void addDescriptorOperations() {
		addDescriptorOperation(
			new RemoveSampleElementsOperation() {

				@Override
				public IStatus removeSampleElements() {
					return removeAllEntities();
				}

			});
	}

	protected IStatus doAddDefaultColumns(IDOMDocument document, String entityName) {
		Element entityElement = null;

		Element element = document.getDocumentElement();

		NodeList nodes = element.getChildNodes();

		if ((nodes != null) && (nodes.getLength() > 0)) {
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = nodes.item(i);

				if (Objects.equals("entity", node.getNodeName()) && (node instanceof Element)) {
					Element elementNode = (Element)node;

					if (entityName.equals(elementNode.getAttribute("name"))) {
						entityElement = (Element)node;
					}
				}
			}
		}

		if (entityElement == null) {
			return Status.CANCEL_STATUS;
		}

		// <!-- PK fields -->

		_appendComment(entityElement, " PK fields ");

		Element columnElem = NodeUtil.appendChildElement(entityElement, "column");

		columnElem.setAttribute("name", _generateEntityId(entityName));
		columnElem.setAttribute("primary", "true");
		columnElem.setAttribute("type", "long");

		// <!-- Group instance -->

		_appendComment(entityElement, " Group instance ");
		columnElem = NodeUtil.appendChildElement(entityElement, "column");

		columnElem.setAttribute("name", "groupId");
		columnElem.setAttribute("type", "long");

		// <!-- Aduit fields -->

		_appendComment(entityElement, " Audit fields ");
		columnElem = NodeUtil.appendChildElement(entityElement, "column");

		columnElem.setAttribute("name", "companyId");
		columnElem.setAttribute("type", "long");

		columnElem = NodeUtil.appendChildElement(entityElement, "column");

		columnElem.setAttribute("name", "userId");
		columnElem.setAttribute("type", "long");

		columnElem = NodeUtil.appendChildElement(entityElement, "column");

		columnElem.setAttribute("name", "userName");
		columnElem.setAttribute("type", "String");

		columnElem = NodeUtil.appendChildElement(entityElement, "column");

		columnElem.setAttribute("name", "createDate");
		columnElem.setAttribute("type", "Date");

		columnElem = NodeUtil.appendChildElement(entityElement, "column");

		columnElem.setAttribute("name", "modifiedDate");
		columnElem.setAttribute("type", "Date");

		entityElement.appendChild(document.createTextNode(_NEW_LINE));

		new FormatProcessorXML().formatNode(entityElement);

		return Status.OK_STATUS;
	}

	protected IStatus doAddDefaultEntity(IDOMDocument document) {
		String entityName = _generateSampleEntityName(document);

		Element rootElement = document.getDocumentElement();

		// new <entity> element

		Element entityElement = document.createElement("entity");

		entityElement.setAttribute("local-service", "true");
		entityElement.setAttribute("name", entityName);
		entityElement.setAttribute("remote-service", "true");

		// <!-- PK fields -->

		_appendComment(entityElement, " PK fields ");

		Element columnElem = NodeUtil.appendChildElement(entityElement, "column");

		columnElem.setAttribute("name", _generateEntityId(entityName));
		columnElem.setAttribute("primary", "true");
		columnElem.setAttribute("type", "long");

		// <!-- Group instance -->

		_appendComment(entityElement, " Group instance ");

		columnElem = NodeUtil.appendChildElement(entityElement, "column");

		columnElem.setAttribute("name", "groupId");
		columnElem.setAttribute("type", "long");

		// <!-- Aduit fields -->

		_appendComment(entityElement, " Audit fields ");

		columnElem = NodeUtil.appendChildElement(entityElement, "column");

		columnElem.setAttribute("name", "companyId");
		columnElem.setAttribute("type", "long");

		columnElem = NodeUtil.appendChildElement(entityElement, "column");

		columnElem.setAttribute("name", "userId");
		columnElem.setAttribute("type", "long");

		columnElem = NodeUtil.appendChildElement(entityElement, "column");

		columnElem.setAttribute("name", "userName");
		columnElem.setAttribute("type", "String");

		columnElem = NodeUtil.appendChildElement(entityElement, "column");

		columnElem.setAttribute("name", "createDate");
		columnElem.setAttribute("type", "Date");

		columnElem = NodeUtil.appendChildElement(entityElement, "column");

		columnElem.setAttribute("name", "modifiedDate");
		columnElem.setAttribute("type", "Date");

		// <!-- Other fields -->

		_appendComment(entityElement, " Other fields ");

		columnElem = NodeUtil.appendChildElement(entityElement, "column");

		columnElem.setAttribute("name", "field1");
		columnElem.setAttribute("type", "String");

		columnElem = NodeUtil.appendChildElement(entityElement, "column");

		columnElem.setAttribute("name", "field2");
		columnElem.setAttribute("type", "boolean");

		columnElem = NodeUtil.appendChildElement(entityElement, "column");

		columnElem.setAttribute("name", "field3");
		columnElem.setAttribute("type", "int");

		columnElem = NodeUtil.appendChildElement(entityElement, "column");

		columnElem.setAttribute("name", "field4");
		columnElem.setAttribute("type", "Date");

		columnElem = NodeUtil.appendChildElement(entityElement, "column");

		columnElem.setAttribute("name", "field5");
		columnElem.setAttribute("type", "String");

		// <!-- Order -->

		_appendComment(entityElement, " Order ");

		Element orderElem = NodeUtil.appendChildElement(entityElement, "order");

		orderElem.setAttribute("by", "asc");

		Element orderColumn = NodeUtil.appendChildElement(orderElem, "order-column");

		orderColumn.setAttribute("name", "field1");

		// <!-- Finder methods -->

		_appendComment(entityElement, " Finder methods ");

		Element finderElem = NodeUtil.appendChildElement(entityElement, "finder");

		finderElem.setAttribute("name", "Field2");
		finderElem.setAttribute("return-type", "Collection");

		Element finderColumn = NodeUtil.appendChildElement(finderElem, "finder-column");

		finderColumn.setAttribute("name", "field2");

		// Insert the <entity> element

		Node refNode = NodeUtil.findFirstChild(rootElement, "exceptions");

		if (refNode == null) {
			NodeUtil.findFirstChild(rootElement, "service-builder-import");
		}

		rootElement.insertBefore(entityElement, refNode);

		new FormatProcessorXML().formatNode(entityElement);

		rootElement.appendChild(document.createTextNode(_NEW_LINE));

		return Status.OK_STATUS;
	}

	protected IStatus doAddEntity(IDOMDocument document, String entityName) {
		NodeList entities = document.getElementsByTagName("entity");

		// If there is entity named "entityName", do nothing

		for (int i = 0; i < entities.getLength(); ++i) {
			Node entity = entities.item(i);

			if (entity instanceof Element) {
				Element entityElement = (Element)entity;

				String name = entityElement.getAttribute("name");

				if ((name != null) && name.equals(entityName)) {
					return Status.OK_STATUS;
				}
			}
		}

		Element rootElement = document.getDocumentElement();

		// new <entity> element

		Element entityElement = document.createElement("entity");

		entityElement.setAttribute("name", entityName);

		// Insert the <entity> element

		Node refNode = NodeUtil.findFirstChild(rootElement, "exceptions");

		if (refNode == null) {
			NodeUtil.findFirstChild(rootElement, "service-builder-import");
		}

		rootElement.insertBefore(entityElement, refNode);

		new FormatProcessorXML().formatNode(entityElement);

		rootElement.appendChild(document.createTextNode(_NEW_LINE));

		return Status.OK_STATUS;
	}

	private void _appendComment(Element element, String comment) {
		Document document = element.getOwnerDocument();

		element.appendChild(document.createTextNode(_NEW_LINE + _NEW_LINE));
		element.appendChild(document.createComment(comment));
		element.appendChild(document.createTextNode(_NEW_LINE + _NEW_LINE));
	}

	private String _generateEntityId(String entityName) {
		if (entityName == null) {
			return "Id";
		}

		return Character.toLowerCase(entityName.charAt(0)) +
			((entityName.length() > 1) ? entityName.substring(1) : "") + "Id";
	}

	private String _generateSampleEntityName(IDOMDocument document) {
		String retval = "Sample";

		List<String> entityNames = new ArrayList<>();

		Element element = document.getDocumentElement();

		NodeList nodes = element.getChildNodes();

		if ((nodes != null) && (nodes.getLength() > 0)) {
			Node node = null;

			for (int i = 0; i < nodes.getLength(); i++) {
				node = nodes.item(i);

				if (Objects.equals("entity", node.getNodeName())) {
					Element elementNode = (Element)node;

					String entityName = elementNode.getAttribute("name");

					if (!CoreUtil.isNullOrEmpty(entityName)) {
						entityNames.add(entityName);
					}
				}
			}
		}

		while (entityNames.contains(retval)) {
			retval = _nextSuffix(retval);
		}

		return retval;
	}

	private String _nextSuffix(String val) {
		Matcher matcher = _pattern.matcher(val);

		if (matcher.matches()) {
			int num = 0;

			try {
				num = Integer.parseInt(matcher.group(2));
			}
			catch (NumberFormatException nfe) {
			}

			return matcher.group(1) + (num + 1);
		}

		return val + "1";
	}

	private static final String _NEW_LINE = System.getProperty("line.separator");

	private Pattern _pattern = Pattern.compile("(Sample)([0-9]+)$");
	private IFile _serviceXMLFile;

}