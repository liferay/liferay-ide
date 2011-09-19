/*******************************************************************************
 * Copyright (c) 2000-2011 Accenture Services Pvt Ltd., All rights reserved.
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
 *
 * Contributors:
 *    Kamesh Sampath - initial implementation
 ******************************************************************************/

package com.liferay.ide.eclipse.portlet.core.model.internal;

import static org.eclipse.sapphire.modeling.xml.XmlUtil.createQualifiedName;
import static org.eclipse.sapphire.modeling.xml.XmlUtil.equal;

import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.LayeredElementBindingImpl;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.ChildXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlAttribute;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlNamespaceResolver;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.sapphire.modeling.xml.XmlResource;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlElementBinding;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
public class DefaultXmlBinding extends LayeredElementBindingImpl {

	private QName xmlElementName;
	private ModelElementType modelElementType;
	private XmlPath path;
	private String pathName;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.modeling.BindingImpl#init(org.eclipse.sapphire.modeling.IModelElement,
	 * org.eclipse.sapphire.modeling.ModelProperty, java.lang.String[])
	 */
	@Override
	public void init( IModelElement element, ModelProperty property, String[] params ) {
		super.init( element, property, params );

		final XmlElementBinding xmlElementBindingAnnotation = property.getAnnotation( XmlElementBinding.class );

		final XmlNamespaceResolver xmlNamespaceResolver =
			( (XmlResource) element.resource() ).getXmlNamespaceResolver();

		if ( xmlElementBindingAnnotation == null ) {
			// System.out.println( "DefaultXmlBinding.init() - Xml Element Binding is null" );
			final XmlBinding xmlBindingAnnotation = property.getAnnotation( XmlBinding.class );

			if ( xmlBindingAnnotation != null && property.getAllPossibleTypes().size() == 1 ) {
				this.pathName = xmlBindingAnnotation.path();
				initElementAndModel( property, xmlNamespaceResolver, pathName );
			}
			else if ( params != null && params[0] != null ) {
				this.pathName = params[0];
				initElementAndModel( property, xmlNamespaceResolver, pathName );
			}
			else {
				throw new IllegalStateException();
			}
		}
		else {
			// NO-OP for now TODO
			// System.out.println( "DefaultXmlBinding.init() - Xml Element Binding is available" );
			throw new IllegalStateException( "This Annotation combination currently not supported" );
		}

	}

	/**
	 * @param property
	 * @param xmlNamespaceResolver
	 * @param path
	 */
	private void initElementAndModel(
		ModelProperty property, final XmlNamespaceResolver xmlNamespaceResolver, final String path ) {
		final int slashIndex = path.lastIndexOf( '/' );
		if ( slashIndex == -1 ) {
			this.xmlElementName = createQualifiedName( pathName, xmlNamespaceResolver );
			this.modelElementType = property.getType();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.modeling.LayeredElementBindingImpl#readUnderlyingObject()
	 */
	@Override
	protected Object readUnderlyingObject() {

		// System.out.println( "DefaultXmlBinding.readUnderlyingObject()" );

		XmlElement parent = ( (XmlResource) element().resource() ).getXmlElement( false );

		if ( this.path != null ) {
			// System.out.println( "DefaultXmlBinding.readUnderlyingObject()-2" );
			parent = (XmlElement) parent.getChildNode( this.path, false );
		}
		else {
			XmlElement childElement = parent.getChildElement( this.pathName, true );
			// System.out.println( "DefaultXmlBinding.readUnderlyingObject()-3" );
			List<ModelProperty> modelProperties = this.modelElementType.getProperties();

			for ( ModelProperty modelProperty : modelProperties ) {
				// System.out.println( "Model Prop " + modelProperty );
				final Required reqAnnotation = modelProperty.getAnnotation( Required.class );
				if ( reqAnnotation != null ) {
					final XmlBinding xmlBindingAnnotation = modelProperty.getAnnotation( XmlBinding.class );
					final DefaultValue defaultValueAnnotation = modelProperty.getAnnotation( DefaultValue.class );
					String modPropName = xmlBindingAnnotation.path();
					if ( isAttribute( modPropName ) ) {
						XmlAttribute modPropAttribute = childElement.getAttribute( modPropName, true );
						String attribCurrValue = modPropAttribute.getText();
						if ( defaultValueAnnotation != null &&
							( attribCurrValue == null || attribCurrValue.trim().length() == 0 ) ) {
							modPropAttribute.setText( defaultValueAnnotation.text() );
						}
					}
					else {
						XmlElement modPropElement = childElement.getChildElement( modPropName, true );
						String elementCurrValue = modPropElement.getText();
						// System.out.println( "Prop Curr Value Element:" + elementCurrValue );
						if ( defaultValueAnnotation != null &&
							( elementCurrValue == null || elementCurrValue.trim().length() == 0 ) ) {
							modPropElement.setText( defaultValueAnnotation.text() );
						}
					}
				}
			}
			return childElement;
		}

		if ( parent != null ) {
			for ( XmlElement element : parent.getChildElements() ) {
				final QName xmlElementName = createQualifiedName( element.getDomNode() );

				if ( equal( this.xmlElementName, xmlElementName, xmlElementName.getNamespaceURI() ) ) {
					return element;
				}
			}
		}
		return null;
	}

	/**
	 * @param path
	 * @return
	 */
	private boolean isAttribute( String path ) {
		return ( path != null ? ( path.indexOf( '@' ) > -1 ) : false );
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.sapphire.modeling.LayeredElementBindingImpl#createUnderlyingObject(org.eclipse.sapphire.modeling.
	 * ModelElementType)
	 */
	@Override
	protected Object createUnderlyingObject( ModelElementType type ) {
		final DefaultValue defaultValueAnnotation = type.getAnnotation( DefaultValue.class );

		XmlElement parent = ( (XmlResource) element().resource() ).getXmlElement( true );

		if ( this.path != null ) {
			parent = (XmlElement) parent.getChildNode( this.path, true );
		}

		QName xmlElementName = this.xmlElementName;

		if ( xmlElementName.getNamespaceURI().equals( "" ) ) {
			xmlElementName = new QName( parent.getNamespace(), xmlElementName.getLocalPart() );
		}
		XmlElement xmlElement = null;
		if ( defaultValueAnnotation != null && xmlElementName != null ) {
			xmlElement = parent.getChildElement( xmlElementName, true );
			xmlElement.setText( defaultValueAnnotation.text() );
		}

		return xmlElement;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.modeling.LayeredElementBindingImpl#createResource(java.lang.Object)
	 */
	@Override
	protected Resource createResource( Object obj ) {
		final XmlElement xmlElement = (XmlElement) obj;
		final XmlResource parentXmlResource = (XmlResource) element().resource();

		return new ChildXmlResource( parentXmlResource, xmlElement );
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.modeling.ElementBindingImpl#type(org.eclipse.sapphire.modeling.Resource)
	 */
	@Override
	public ModelElementType type( Resource resource ) {
		final XmlElement xmlElement = ( (XmlResource) resource ).getXmlElement();
		final QName xmlElementName = createQualifiedName( xmlElement.getDomNode() );
		final String xmlElementNamespace = xmlElementName.getNamespaceURI();

		if ( equal( this.xmlElementName, xmlElementName, xmlElementNamespace ) ) {
			return this.modelElementType;
		}

		throw new IllegalStateException();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.modeling.ElementBindingImpl#removable()
	 */
	@Override
	public boolean removable() {
		return true;
	}

}
