/*
 * 
 */

package com.liferay.ide.eclipse.portlet.core.model.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNamespaceResolver;
import org.eclipse.sapphire.modeling.xml.XmlNode;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;
import org.w3c.dom.Document;

/**
 * @author kamesh.sampath
 */

public final class QNameValueBinding extends XmlValueBindingImpl {

	private String[] params;
	private XmlPath path;

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
		final XmlElement parent = xml( false );
		String value = null;
		if ( parent != null ) {
			final XmlElement qNameElement = parent.getChildElement( params[0], false );
			value = qNameElement.getText();
		}

		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.modeling.ValueBindingImpl#write(java.lang.String)
	 */
	@Override
	public void write( final String value ) {
		final XmlElement parent = xml( true );
		// System.out.println( "EventDefinitionValueBinding.write()" + parent );
		final XmlElement qNameElement = parent.getChildElement( params[0], true );
		qNameElement.setChildNodeText( this.path, value, true );

		// Only for debugging purposes
		try {
			printDocument( parent.getDomNode().getOwnerDocument(), System.out );
		}
		catch ( IOException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch ( TransformerException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public XmlNode getXmlNode() {
		final XmlElement parent = xml();

		XmlElement element = parent.getChildElement( params[0], false );

		if ( element != null ) {
			return element;
		}

		return null;
	}

	private void printDocument( Document doc, OutputStream out ) throws IOException, TransformerException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "no" );
		transformer.setOutputProperty( OutputKeys.METHOD, "xml" );
		transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
		transformer.setOutputProperty( OutputKeys.ENCODING, "UTF-8" );
		transformer.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", "4" );

		transformer.transform( new DOMSource( doc ), new StreamResult( new OutputStreamWriter( out, "UTF-8" ) ) );
	}

}
