/*******************************************************************************
 *  Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *  
 *   This library is free software; you can redistribute it and/or modify it under
 *   the terms of the GNU Lesser General Public License as published by the Free
 *   Software Foundation; either version 2.1 of the License, or (at your option)
 *   any later version.
 *  
 *   This library is distributed in the hope that it will be useful, but WITHOUT
 *   ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 *   FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 *   details.
 *  
 *   Contributors:
 *          Kamesh Sampath - initial implementation
 *          Gregory Amerson - IDE-355 create liferay-hook.xml editor
 *******************************************************************************/

package com.liferay.ide.eclipse.hook.core.model;

import com.liferay.ide.eclipse.hook.core.model.internal.DocrootRelativePathService;
import com.liferay.ide.eclipse.hook.core.model.internal.HookVersion;
import com.liferay.ide.eclipse.hook.core.model.internal.SrcFoldersRelativePathService;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Transient;
import org.eclipse.sapphire.modeling.TransientProperty;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.FileExtensions;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
@GenerateImpl
public interface IHook extends IModelElement
{

	ModelElementType TYPE = new ModelElementType( IHook.class );

	// *** Version ***

	@Type( base = HookVersion.class )
	@Label( standard = "Version" )
	TransientProperty PROP_VERSION = new TransientProperty( TYPE, "Version" );

	Transient<HookVersion> getVersion();

	void setVersion( HookVersion value );

	// *** PortalProperties ***

	@Service( impl = SrcFoldersRelativePathService.class )
	@Type( base = Path.class )
	@Label( standard = "Portal Properties" )
	@XmlBinding( path = "portal-properties" )
	@ValidFileSystemResourceType( FileSystemResourceType.FILE )
	@FileExtensions( expr = "properties" )
	@MustExist
	ValueProperty PROP_PORTAL_PROPERTIES = new ValueProperty( TYPE, "PortalProperties" );

	Value<Path> getPortalProperties();

	void setPortalProperties( Path value );

	void setPortalProperties( String value );


	// *** LanguageProperties ***

	@Type( base = ILanguageProperty.class )
	@Label( standard = "Language Properties" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "language-properties", type = ILanguageProperty.class ) } )
	ListProperty PROP_LANGUAGE_PROPERTIES = new ListProperty( TYPE, "LanguageProperties" );

	ModelElementList<ILanguageProperty> getLanguageProperties();

	// *** CustomJspDir ***

	@Type( base = Path.class )
	@Label( standard = "Custom JSP Dir" )
	@XmlBinding( path = "custom-jsp-dir" )
	@Service( impl = DocrootRelativePathService.class )
	@ValidFileSystemResourceType( FileSystemResourceType.FOLDER )
	@MustExist
	ValueProperty PROP_CUSTOM_JSP_DIR = new ValueProperty( TYPE, "CustomJspDir" );

	Value<Path> getCustomJspDir();

	void setCustomJspDir( String value );

	void setCustomJspDir( Path value );

	// *** CustomJspGlobal ***

	@Type( base = Boolean.class )
	@Label( standard = "Custom JSP Global" )
	@XmlBinding( path = "custom-jsp-global" )
	ValueProperty PROP_CUSTOM_JSP_GLOBAL = new ValueProperty( TYPE, "CustomJspGlobal" );

	Value<Boolean> getCustomJspGlobal();

	void setCustomJspGlobal( String value );

	void setCustomJspGlobal( Boolean value );

	// *** IndexerPostProcessors ***

	@Type( base = IIndexerPostProcessor.class )
	@Label( standard = "Index Post Processors" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "indexer-post-processor", type = IIndexerPostProcessor.class ) } )
	ListProperty PROP_INDEXER_POST_PROCESSORS = new ListProperty( TYPE, "IndexerPostProcessors" );

	ModelElementList<IIndexerPostProcessor> getIndexerPostProcessors();

	// *** Services ***

	@Type( base = IService.class )
	@Label( standard = "Services" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "service", type = IService.class ) } )
	ListProperty PROP_SERVICES = new ListProperty( TYPE, "Services" );

	ModelElementList<IService> getServices();

	// *** ServletFilters ***

	@Type( base = IServletFilter.class )
	@Label( standard = "Servlet filters" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "servlet-filter", type = IServletFilter.class ) } )
	ListProperty PROP_SERVLET_FILTERS = new ListProperty( TYPE, "ServletFilters" );

	ModelElementList<IServletFilter> getServletFilters();

	// *** ServletFilterMappings ***

	@Type( base = IServletFilterMapping.class )
	@Label( standard = "Servlet Filter Mappings" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "servlet-filter-mapping", type = IServletFilterMapping.class ) } )
	ListProperty PROP_SERVLET_FILTER_MAPPINGS = new ListProperty( TYPE, "ServletFilterMappings" );

	ModelElementList<IServletFilterMapping> getServletFilterMappings();

	// *** StrutsActions ***

	@Type( base = IStrutsAction.class )
	@Label( standard = "Struts Actions" )
	@XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "struts-action", type = IStrutsAction.class ) } )
	ListProperty PROP_STRUTS_ACTIONS = new ListProperty( TYPE, "StrutsActions" );

	ModelElementList<IStrutsAction> getStrutsActions();

}
