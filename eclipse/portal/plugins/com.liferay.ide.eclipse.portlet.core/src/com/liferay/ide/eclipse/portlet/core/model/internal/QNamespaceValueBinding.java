/**
 * 
 */

package com.liferay.ide.eclipse.portlet.core.model.internal;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.xml.XmlAttribute;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNamespaceResolver;
import org.eclipse.sapphire.modeling.xml.XmlNode;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;

/**
 * @author kamesh.sampath
 */
public class QNamespaceValueBinding extends XmlValueBindingImpl {

	String[] params;
	XmlPath path;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.modeling.BindingImpl#init(org.eclipse.sapphire.modeling.IModelElement,
	 * org.eclipse.sapphire.modeling.ModelProperty, java.lang.String[])
	 */
	@Override
	public void init( IModelElement element, ModelProperty property, String[] params ) {
		super.init( element, property, params );
		this.params = params;
		final XmlNamespaceResolver xmlNamespaceResolver = resource().getXmlNamespaceResolver();
		this.path = new XmlPath( params[0], xmlNamespaceResolver );
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.modeling.ValueBindingImpl#read()
	 */
	@Override
	public String read() {
		XmlElement qNameElement = xml( true );
		qNameElement.getDomNode().setAttributeNS( "http://www.w3.org/2000/xmlns/", "xmlns:qNs", "" );
		XmlAttribute xmlAtribute = qNameElement.getAttribute( "xmlns:qNs", true );

		System.out.println( "QNamespaceValueBinding.read()" + xmlAtribute );
		return xmlAtribute.getText();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.modeling.ValueBindingImpl#write(java.lang.String)
	 */
	@Override
	public void write( String value ) {
		String val = value;

		// System.out.println( "VALUE ___________________ " + val );

		if ( val != null ) {
			val = value.trim();
		}

		System.out.println( "TextNodeValueBinding.write() - Curre element " + xml( true ) );

		XmlElement qNameElement = xml( true );
		qNameElement.getDomNode().setAttributeNS( "http://www.w3.org/2000/xmlns/", "xmlns:qNs", val );

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl#getXmlNode()
	 */
	@Override
	public XmlNode getXmlNode() {
		final XmlElement element = xml( false );

		if ( element != null ) {
			return element.getChildNode( this.path, false );
		}

		return null;
	}

}
