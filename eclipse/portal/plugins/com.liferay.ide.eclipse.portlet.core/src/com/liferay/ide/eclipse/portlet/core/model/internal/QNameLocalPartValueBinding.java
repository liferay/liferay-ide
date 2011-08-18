
package com.liferay.ide.eclipse.portlet.core.model.internal;

import static com.liferay.ide.eclipse.portlet.core.util.PortletAppModelConstants.LOCAL_PART_DEFAULT_VALUE;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNamespaceResolver;
import org.eclipse.sapphire.modeling.xml.XmlNode;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;

import com.liferay.ide.eclipse.portlet.core.util.PortletAppModelConstants;

/**
 * @author kamesh.sampath
 */

public final class QNameLocalPartValueBinding

extends XmlValueBindingImpl

{

	private XmlPath path;
	private String[] params;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.modeling.BindingImpl#init(org.eclipse.sapphire.modeling.IModelElement,
	 * org.eclipse.sapphire.modeling.ModelProperty, java.lang.String[])
	 */
	@Override
	public void init( final IModelElement element, final ModelProperty property, final String[] params ) {
		super.init( element, property, params );

		final XmlNamespaceResolver xmlNamespaceResolver = resource().getXmlNamespaceResolver();
		this.path = new XmlPath( params[0], xmlNamespaceResolver );
		this.params = params;

		// System.out.println( "TextNodeValueBinding.init()" + this.path );
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.modeling.ValueBindingImpl#read()
	 */
	@Override
	public String read() {
		String value = LOCAL_PART_DEFAULT_VALUE;

		final XmlElement qNameElement = xml( false );

		// final XmlElement qNameElement = eventDefintion.getChildElement( params[0], false );

		// System.out.println( "Reading VALUE for Element  ___________________ " + qNameElement );

		if ( qNameElement != null ) {

			value = qNameElement.getText();

			if ( value != null ) {
				value = value.trim();
				value = stripPrefix( value );
			}

		}

		// System.out.println( "Read VALUE ___________________ " + value );

		return value;
	}

	/**
	 * A simple utility method that will Strip the namespace prefix
	 * 
	 * @param value
	 *            - the value from which the namespace prefix needs to be removed
	 * @return - the normal text value without prefix
	 */
	private String stripPrefix( String value ) {
		int colonIndex = value.indexOf( ":" );
		if ( colonIndex != -1 ) {
			value = value.substring( colonIndex + 1 );
		}
		return value;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.modeling.ValueBindingImpl#write(java.lang.String)
	 */
	@Override
	public void write( final String value ) {
		String val = value;
		XmlElement parent = xml( true );

		/*
		 * In some cases the parent node and the child nodes will be same, we need to ensure that we dont create them
		 * accidentally again
		 */
		// System.out.println( "QNameLocalPartValueBinding.write() - Parent local name:" + parent.getLocalName() );
		XmlElement qNameElement = null;
		if ( parent.getLocalName().equals( params[0] ) ) {
			qNameElement = parent;
		}
		else {
			qNameElement = parent.getChildElement( params[0], true );
		}

		// System.out.println( "VALUE ___________________ " + val );

		if ( val != null ) {
			val = value.trim();
		}

		// System.out.println( "TextNodeValueBinding.write() - Parent " + xml( true ).getParent() );
		if ( qNameElement != null ) {
			qNameElement.setText( PortletAppModelConstants.DEFAULT_QNAME_PREFIX + ":" + val );
		}

		// // For debugging purposes
		// try {
		// printDocument( qNameElement.getDomNode().getOwnerDocument(), System.out );
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
