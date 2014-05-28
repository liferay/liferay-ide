package com.liferay.ide.portlet.jsf.core.operation;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.NodeUtil;
import com.liferay.ide.portlet.core.dd.LiferayPortletDescriptorHelper;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;
import org.osgi.framework.Version;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * @author Greg Amerson
 * @author Simon Jiang
 * @author Kuo Zhang
 */
@SuppressWarnings( "restriction" )
public class JSFLiferayPortletDescriptorHelper extends LiferayPortletDescriptorHelper 
                                               implements INewJSFPortletClassDataModelProperties
{
    public JSFLiferayPortletDescriptorHelper()
    {
        super();
    }

    public JSFLiferayPortletDescriptorHelper( IProject project )
    {
        super( project );
    }

    public IStatus addNewPortlet( IDataModel model )
    {
        IStatus status = Status.OK_STATUS;

        if( ! canAddNewPortlet( model ) )
        {
            return status;
        }

        status = super.addNewPortlet( model );

        if( ! status.isOK() )
        {
            return status;
        }

        final Version runtimeVersion = ServerUtil.getRuntimeVersion( project );

        // Runtime version should be equal or greater than 6.2.
        if( CoreUtil.compareVersions( runtimeVersion, ILiferayConstants.V620 ) >= 0 )
        {
            final IFile descriptorFile = getDescriptorFile();

            if( descriptorFile != null )
            {
                final DOMModelOperation op = new DOMModelEditOperation( descriptorFile )
                {
                    @Override
                    protected void createDefaultFile()
                    {
                        // Getting document from super( descriptorFile );
                    }

                    @Override
                    protected IStatus doExecute( IDOMDocument document )
                    {
                        return updateJSFLiferayPortletXML( document );
                    }
                };

                status = op.execute();
            }
        }

        return status;
    }

    @Override
    public boolean canAddNewPortlet( IDataModel model )
    {
        return model.getID().contains( "NewJSFPortlet" );
    }

    private IStatus updateJSFLiferayPortletXML( IDOMDocument document )
    {
        final Element rootElement = document.getDocumentElement();

        final NodeList portletNodes = rootElement.getElementsByTagName( "portlet" );

        if( portletNodes.getLength() > 1 )
        {
            final Element lastPortletElement = (Element) portletNodes.item( portletNodes.getLength() - 1 );
            final Node headerPortletClassElement =
                lastPortletElement.getElementsByTagName( "header-portlet-css" ).item( 0 );

            NodeUtil.insertChildElement(
                lastPortletElement, headerPortletClassElement, "requires-namespaced-parameters", "false" );

            new FormatProcessorXML().formatNode( lastPortletElement );
        }

        return Status.OK_STATUS;
    }
}
