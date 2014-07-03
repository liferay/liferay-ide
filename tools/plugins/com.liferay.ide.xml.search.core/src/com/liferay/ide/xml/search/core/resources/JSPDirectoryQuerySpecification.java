package com.liferay.ide.xml.search.core.resources;

import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.wst.xml.search.core.queryspecifications.container.IResourceProvider;
import org.eclipse.wst.xml.search.core.resource.IResourceRequestor;
import org.eclipse.wst.xml.search.core.resource.IResourceRequestorProvider;
import org.eclipse.wst.xml.search.core.resource.IURIResolver;
import org.eclipse.wst.xml.search.core.resource.IURIResolverProvider;

/**
 * @author Kuo Zhang
 */
public class JSPDirectoryQuerySpecification implements IResourceRequestorProvider,IURIResolverProvider, IResourceProvider
{

    @Override
    public IURIResolver getURIResolver( IFile file, Object selectedNode )
    {
        return JSPDirectoryURIResolver.INSTANCE;
    }

    @Override
    public IResourceRequestor getRequestor()
    {
        return JSPDirectoryResourcesRequestor.INSTANCE;
    }

    @Override
    public IResource getResource( Object selectedNode, IResource resource )
    {
        return CoreUtil.getDefaultDocrootFolder( resource.getProject() );
    }

}
