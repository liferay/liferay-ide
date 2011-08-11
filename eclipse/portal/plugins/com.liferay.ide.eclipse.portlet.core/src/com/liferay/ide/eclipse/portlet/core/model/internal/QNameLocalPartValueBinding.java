
package com.liferay.ide.eclipse.portlet.core.model.internal;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNamespaceResolver;
import org.eclipse.sapphire.modeling.xml.XmlNode;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;

/**
 * @author kamesh.sampath
 */

public final class QNameLocalPartValueBinding

extends XmlValueBindingImpl

{

	private XmlPath path;

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

		// System.out.println( "TextNodeValueBinding.init()" + this.path );
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.modeling.ValueBindingImpl#read()
	 */
	@Override
	public String read() {
		String value = null;

		final XmlElement element = xml( false );

		if ( element != null ) {

			value = xml( true ).getText();

			// System.out.println( "Reading VALUE ___________________ " + value );

			if ( value != null ) {
				value = value.trim();
			}
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

		// System.out.println( "VALUE ___________________ " + val );

		if ( val != null ) {
			val = value.trim();
		}

		// System.out.println( "TextNodeValueBinding.write() - Parent " + xml( true ).getParent() );

		xml( true ).setText( val );
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
