/**
 * 
 */

package com.liferay.ide.eclipse.portlet.core.model.internal;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ImageData;
import org.eclipse.sapphire.modeling.ImageService;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;

import com.liferay.ide.eclipse.portlet.core.model.ICustomPortletMode;
import com.liferay.ide.eclipse.portlet.core.model.IPortletMode;

/**
 * @author kamesh.sampath
 */
public class PortletModeImageService extends ImageService {

	private static final ImageData IMG_DEFAULT = ImageData.readFromClassLoader(
		PortletModeImageService.class, "images/portlet.png" );

	private static final ImageData IMG_VIEW = ImageData.readFromClassLoader(
		PortletModeImageService.class, "images/view.png" );

	private static final ImageData IMG_EDIT = ImageData.readFromClassLoader(
		PortletModeImageService.class, "images/edit.png" );

	private static final ImageData IMG_HELP = ImageData.readFromClassLoader(
		PortletModeImageService.class, "images/help.png" );

	private ModelPropertyListener listener;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.modeling.ModelElementService#init(org.eclipse.sapphire.modeling.IModelElement,
	 * java.lang.String[])
	 */
	@Override
	public void init( IModelElement element, String[] params ) {

		super.init( element, params );

		this.listener = new ModelPropertyListener() {

			@Override
			public void handlePropertyChangedEvent( final ModelPropertyChangeEvent event ) {
				notifyListeners( new ImageChangedEvent( PortletModeImageService.this ) );
			}
		};

		element.addListener( this.listener, IPortletMode.PROP_PORTLET_MODE.getName() );
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.modeling.ImageService#provide()
	 */
	@Override
	public ImageData provide() {
		String portletMode = null;
		if ( element() instanceof ICustomPortletMode ) {
			ICustomPortletMode iCustomPortletMode = (ICustomPortletMode) element();
			portletMode = String.valueOf( iCustomPortletMode.getPortletMode().getContent() );
		}
		else if ( element() instanceof IPortletMode ) {
			IPortletMode iPortletMode = (IPortletMode) element();
			portletMode = iPortletMode.getPortletMode().getContent();
		}

		if ( portletMode != null ) {
			if ( "VIEW".equalsIgnoreCase( portletMode ) ) {
				return IMG_VIEW;
			}
			else if ( "EDIT".equalsIgnoreCase( portletMode ) ) {
				return IMG_EDIT;
			}
			else if ( "HELP".equalsIgnoreCase( portletMode ) ) {
				return IMG_HELP;
			}

		}
		return IMG_DEFAULT;
	}

	/**
	 * 
	 */
	@Override
	public void dispose() {
		super.dispose();

		element().removeListener( this.listener, IPortletMode.PROP_PORTLET_MODE.getName() );
	}

}
