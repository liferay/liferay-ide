/**
 * 
 */

package com.liferay.ide.eclipse.portlet.core.model.internal;

import static com.liferay.ide.eclipse.portlet.core.util.PortletAppModelConstants.NAMESPACE_URI_DEFAULT_VALUE;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.xml.XmlAttribute;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNamespaceResolver;
import org.eclipse.sapphire.modeling.xml.XmlNode;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;
import org.w3c.dom.Element;

import com.liferay.ide.eclipse.portlet.core.util.PortletAppModelConstants;

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
		String value = NAMESPACE_URI_DEFAULT_VALUE;
		XmlElement qNameElement = xml( true );
		// XmlElement qNameElement = parent.getChildElement( params[0], false );
		if ( qNameElement != null ) {
			XmlAttribute xmlAttribute =
				qNameElement.getAttribute( PortletAppModelConstants.DEFAULT_QNAME_PREFIX, false );
			if ( xmlAttribute != null ) {
				value = xmlAttribute.getText();
			}
		}
		return value != null ? value.trim() : value;
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

		XmlElement parent = xml( true );
		/*
		 * In some cases the parent node and the child nodes will be same, we need to ensure that we dont create them
		 * accidentally again
		 */
		// System.out.println( "QNamespaceValueBinding.write() - Parent local name:" + parent.getLocalName() );
		XmlElement qNameElement = null;
		if ( parent.getLocalName().equals( params[0] ) ) {
			qNameElement = parent;
		}
		else {
			qNameElement = parent.getChildElement( params[0], true );
		}

		if ( qNameElement != null ) {
			// System.out.println( "QNamespaceValueBinding.write() - 1" );
			Element qnameDef = qNameElement.getDomNode();
			qnameDef.setAttributeNS( "http://www.w3.org/2000/xmlns/", "xmlns:" +
				PortletAppModelConstants.DEFAULT_QNAME_PREFIX, val );
		}
		// parent.getDomNode().appendChild( qnameDef );

		// // Only for debugging purposes
		// try {
		// printDocument( parent.getDomNode().getOwnerDocument(), System.out );
		// }
		// catch ( IOException e ) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// catch ( TransformerException e ) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

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

	// private void printDocument( Document doc, OutputStream out ) throws IOException, TransformerException {
	// TransformerFactory tf = TransformerFactory.newInstance();
	// Transformer transformer = tf.newTransformer();
	// transformer.setOutputProperty( OutputKeys.OMIT_XML_DECLARATION, "no" );
	// transformer.setOutputProperty( OutputKeys.METHOD, "xml" );
	// transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
	// transformer.setOutputProperty( OutputKeys.ENCODING, "UTF-8" );
	// transformer.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", "4" );
	//
	// transformer.transform( new DOMSource( doc ), new StreamResult( new OutputStreamWriter( out, "UTF-8" ) ) );
	// }

}
