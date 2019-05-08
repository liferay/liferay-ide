/*******************************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/

package com.liferay.ide.upgrade.plan.core.internal;

import com.liferay.ide.upgrade.plan.core.IMemento;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
/**
 * A Memento is a class independent container for persistence
 * info.  It is a reflection of 3 storage requirements.
 *
 * 1)   We need the ability to persist an object and restore it.
 * 2)   The class for an object may be absent.  If so we would
 *      like to skip the object and keep reading.
 * 3)   The class for an object may change.  If so the new class
 *      should be able to read the old persistence info.
 *
 * We could ask the objects to serialize themselves into an
 * ObjectOutputStream, DataOutputStream, or Hashtable.  However
 * all of these approaches fail to meet the second requirement.
 *
 * Memento supports binary persistance with a version ID.
 */
public final class XMLMemento implements IMemento {
	private Document factory;
	private Element element;

	/**
	 * Answer a memento for the document and element.  For simplicity
	 * you should use createReadRoot and createWriteRoot to create the initial
	 * mementos on a document.
	 */
	private XMLMemento(Document doc, Element el) {
		factory = doc;
		element = el;
	}

	/*
	 * @see IMemento
	 */
	public IMemento createChild(String type) {
		Element child = factory.createElement(type);
		element.appendChild(child);
		return new XMLMemento(factory, child);
	}

	/**
	 * Create a Document from a Reader and answer a root memento for reading
	 * a document.
	 */
	protected static XMLMemento createReadRoot(InputStream in) {
		Document document = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser = factory.newDocumentBuilder();
			document = parser.parse(new InputSource(in));
			Node node = document.getFirstChild();
			if (node instanceof Element)
				return new XMLMemento(document, (Element) node);
		} catch (Exception e) {
			// ignore
		} finally {
			try {
				in.close();
			} catch (Exception e) {
				// ignore
			}
		}
		return null;
	}

	/**
	 * Answer a root memento for writing a document.
	 *
	 * @param type a type
	 * @return a memento
	 */
	public static XMLMemento createWriteRoot(String type) {
		Document document;
		try {
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element element = document.createElement(type);
			document.appendChild(element);
			return new XMLMemento(document, element);
		} catch (ParserConfigurationException e) {
			throw new Error(e);
		}
	}

	/*
	 * @see IMemento
	 */
	public IMemento getChild(String type) {
		// Get the nodes.
		NodeList nodes = element.getChildNodes();
		int size = nodes.getLength();
		if (size == 0)
			return null;

		// Find the first node which is a child of this node.
		for (int nX = 0; nX < size; nX ++) {
			Node node = nodes.item(nX);
			if (node instanceof Element) {
				Element element2 = (Element)node;
				if (element2.getNodeName().equals(type))
					return new XMLMemento(factory, element2);
			}
		}

		// A child was not found.
		return null;
	}

	/*
	 * @see IMemento
	 */
	public IMemento [] getChildren(String type) {
		// Get the nodes.
		NodeList nodes = element.getChildNodes();
		int size = nodes.getLength();
		if (size == 0)
			return new IMemento[0];

		// Extract each node with given type.
		List<Element> list = new ArrayList<Element>(size);
		for (int nX = 0; nX < size; nX ++) {
			Node node = nodes.item(nX);
			if (node instanceof Element) {
				Element element2 = (Element)node;
				if (element2.getNodeName().equals(type))
					list.add(element2);
			}
		}

		// Create a memento for each node.
		size = list.size();
		IMemento [] results = new IMemento[size];
		for (int x = 0; x < size; x ++) {
			results[x] = new XMLMemento(factory, list.get(x));
		}
		return results;
	}

	/**
	 * Return the contents of this memento as a byte array.
	 *
	 * @return byte[]
	 * @throws IOException if anything goes wrong
	 */
	public byte[] getContents() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		save(out);
		return out.toByteArray();
	}

	/**
	 * Returns an input stream for writing to the disk with a local locale.
	 *
	 * @return java.io.InputStream
	 * @throws IOException if anything goes wrong
	 */
	public InputStream getInputStream() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		save(out);
		return new ByteArrayInputStream(out.toByteArray());
	}

	/*
	 * @see IMemento
	 */
	public Float getFloat(String key) {
		Attr attr = element.getAttributeNode(key);
		if (attr == null)
			return null;
		String strValue = attr.getValue();
		try {
			return new Float(strValue);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/*
	 * @see IMemento
	 */
	public Integer getInteger(String key) {
		Attr attr = element.getAttributeNode(key);
		if (attr == null)
			return -1;
		String strValue = attr.getValue();
		try {
			return new Integer(strValue);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	/*
	 * @see IMemento
	 */
	public Long getLong(String key) {
		Attr attr = element.getAttributeNode(key);
		if (attr == null)
			return -1l;
		String strValue = attr.getValue();
		try {
			return new Long(strValue);
		} catch (NumberFormatException e) {
			return -1l;
		}
	}

	/*
	 * @see IMemento
	 */
	public String getString(String key) {
		Attr attr = element.getAttributeNode(key);
		if (attr == null)
			return "";
		return attr.getValue();
	}

	public List<String> getNames() {
		NamedNodeMap map = element.getAttributes();
		int size = map.getLength();
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < size; i++) {
			Node node = map.item(i);
			String name = node.getNodeName();
			list.add(name);
		}
		return list;
	}

	/**
	 * Loads a memento from the given filename.
	 *
	 * @param in java.io.InputStream
	 * @return org.eclipse.ui.IMemento
	 */
	public static IMemento loadMemento(InputStream in) {
		return createReadRoot(in);
	}

	/**
	 * Loads a memento from the given filename.
	 *
	 * @param filename java.lang.String
	 * @return org.eclipse.ui.IMemento
	 * @exception java.io.IOException
	 */
	public static IMemento loadMemento(String filename) throws IOException {
		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(filename));
			return XMLMemento.createReadRoot(in);
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (Exception e) {
				// ignore
			}
		}
	}

	/*
	 * @see IMemento
	 */
	public void putInteger(String key, int n) {
		element.setAttribute(key, String.valueOf(n));
	}

	/*
	 * @see IMemento
	 */
	public void putString(String key, String value) {
		if (value == null)
			return;
		element.setAttribute(key, value);
	}

	/**
	 * Save this Memento to a Writer.
	 *
	 * @throws IOException if there is a problem saving
	 */
	public void save(OutputStream os) throws IOException {
		Result result = new StreamResult(os);
		Source source = new DOMSource(factory);
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");
			transformer.transform(source, result);
		} catch (Exception e) {
			throw (IOException) (new IOException().initCause(e));
		}
	}

	/**
	 * Saves the memento to the given file.
	 *
	 * @param filename java.lang.String
	 * @exception java.io.IOException
	 */
	public void saveToFile(String filename) throws IOException {
		BufferedOutputStream w = null;
		try {
			w = new BufferedOutputStream(new FileOutputStream(filename));
			save(w);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException(e.getLocalizedMessage());
		} finally {
			if (w != null) {
				try {
					w.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}

	public String saveToString() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		save(out);
		return out.toString("UTF-8");
	}

	/*
	 * @see IMemento#getBoolean(String)
	 */
	public Boolean getBoolean(String key) {
		Attr attr = element.getAttributeNode(key);
		if (attr == null)
			return null;
		String strValue = attr.getValue();
		if ("true".equalsIgnoreCase(strValue))
			return new Boolean(true);
		return new Boolean(false);
	}

	/*
	 * @see IMemento#putBoolean(String, boolean)
	 */
	public void putBoolean(String key, boolean value) {
		element.setAttribute(key, value ? "true" : "false");
	}

	/**
    * Returns the Text node of the memento. Each memento is allowed only
    * one Text node.
    *
    * @return the Text node of the memento, or <code>null</code> if
    * the memento has no Text node.
    */
   private Text getTextNode() {
       // Get the nodes.
       NodeList nodes = element.getChildNodes();
       int size = nodes.getLength();
       if (size == 0) {
			return null;
		}
       for (int nX = 0; nX < size; nX++) {
           Node node = nodes.item(nX);
           if (node instanceof Text) {
               return (Text) node;
           }
       }
       // a Text node was not found
       return null;
   }

	/* (non-Javadoc)
    */
   public void putTextData(String data) {
       Text textNode = getTextNode();
       if (textNode == null) {
           textNode = factory.createTextNode(data);
			// Always add the text node as the first child (fixes bug 93718)
			element.insertBefore(textNode, element.getFirstChild());
       } else {
           textNode.setData(data);
       }
   }

	@Override
	public void removeChildren(String type) {
		NodeList childNodes = element.getChildNodes();

		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);

			if (type.equals(node.getNodeName())) {
				element.removeChild(node);
			}
		}
	}

	@Override
	public void removeChild(IMemento child) {
		XMLMemento childMemento = (XMLMemento)child;

		Element childElement = childMemento.element;

		NodeList childNodes = element.getChildNodes();

		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);

			if (childElement.equals(node)) {
				element.removeChild(node);
			}
		}
	}

}