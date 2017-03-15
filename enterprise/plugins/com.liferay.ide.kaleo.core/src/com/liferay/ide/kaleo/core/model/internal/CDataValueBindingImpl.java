/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay IDE ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */

package com.liferay.ide.kaleo.core.model.internal;

import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlPath;
import org.eclipse.sapphire.modeling.xml.XmlValueBindingImpl;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author Gregory Amerson
 */
public class CDataValueBindingImpl extends XmlValueBindingImpl
{
    private XmlPath path;

    @Override
    public void init( Property property )
    {
        super.init( property );

        final XmlBinding bindingAnnotation = property.definition().getAnnotation( XmlBinding.class );

        this.path = new XmlPath( bindingAnnotation.path(), resource().getXmlNamespaceResolver() );
    }

    @Override
    public String read()
    {
        String retval = null;

        XmlElement xmlElement = xml( false );

        if( xmlElement != null )
        {
            retval = xmlElement.getChildNodeText( this.path );
        }

        return retval;
    }

    @Override
    public void write( String value )
    {
        XmlElement xmlElement = xml( true );

        Node cdataNode = xmlElement.getChildNode( this.path, true ).getDomNode();

        CoreUtil.removeChildren( cdataNode );

        Document document = cdataNode.getOwnerDocument();

        cdataNode.insertBefore( document.createCDATASection( value ), null );
    }

}
