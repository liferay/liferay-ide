/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay Developer Studio ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */

package com.liferay.ide.kaleo.ui.util;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.server.core.ILiferayServer;

import java.io.ByteArrayInputStream;
import java.util.Locale;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.validation.ValidationResults;
import org.eclipse.wst.validation.ValidatorMessage;
import org.eclipse.wst.validation.internal.ValOperation;
import org.eclipse.wst.validation.internal.ValType;
import org.eclipse.wst.validation.internal.ValidationRunner;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class KaleoUtil
{

    public static String checkWorkflowDefinitionForErrors( IFile workspaceFile )
    {
        String retval = null;
        // first perform manual validation to check for errors
        try
        {
            StringBuilder errorMsgs = new StringBuilder();

            ValOperation result = ValidationRunner.validate( workspaceFile, ValType.Manual, null, true );

            if( result.getResult().getSeverityError() == 1 )
            {
                ValidationResults results = result.getResults();

                for( ValidatorMessage message : results.getMessages() )
                {
                    if( message.getAttribute( IMarker.SEVERITY, -1 ) == IMarker.SEVERITY_ERROR )
                    {
                        errorMsgs.append( message.getAttribute( IMarker.MESSAGE ) ).append( '\n' );
                    }
                }
            }

            retval = errorMsgs.toString();
        }
        catch( Exception e )
        {
        }

        return retval;
    }

    public static String createJSONTitleMap( String title ) throws JSONException
    {
        return createJSONTitleMap( title, Locale.getDefault().toString() );
    }

    public static String createJSONTitleMap( String title, String portalLocale ) throws JSONException
    {
        JSONObject jsonTitleMap = new JSONObject();

        try
        {
            Document doc = FileUtil.readXML( new ByteArrayInputStream( title.getBytes() ), null, new ErrorHandler()
            {
                public void warning( SAXParseException exception ) throws SAXException
                {
                }

                public void fatalError( SAXParseException exception ) throws SAXException
                {
                }

                public void error( SAXParseException exception ) throws SAXException
                {
                }
            });

            String defaultLocale = doc.getDocumentElement().getAttribute( "default-locale" );

            NodeList titles = doc.getElementsByTagName( "Title" );

            for (int i = 0; i < titles.getLength(); i++)
            {
                Node titleNode = titles.item( i );

                String titleValue = titleNode.getTextContent();

                String languageId = titleNode.getAttributes().getNamedItem( "language-id" ).getNodeValue();

                if (languageId.equals( defaultLocale ))
                {
                    jsonTitleMap.put( languageId, titleValue );
                    break;
                }
            }
        }
        catch (Exception e)
        {
            jsonTitleMap.put( portalLocale, title );
        }

        return jsonTitleMap.toString();
    }

    public static ILiferayServer getLiferayServer( IServer server, IProgressMonitor monitor )
    {
        ILiferayServer retval = null;

        if( server != null )
        {
            try
            {
                retval = (ILiferayServer) server.loadAdapter( ILiferayServer.class, monitor );
            }
            catch( Exception e )
            {
            }
        }

        return retval;
    }

}
