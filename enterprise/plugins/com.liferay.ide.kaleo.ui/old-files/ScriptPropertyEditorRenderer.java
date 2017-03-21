package com.liferay.ide.kaleo.ui.editor;

import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdfill;
import static org.eclipse.sapphire.ui.swt.renderer.GridLayoutUtil.gdhhint;

import com.liferay.ide.kaleo.core.model.IScriptable;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.ui.PropertyEditorPart;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.renderers.swt.PropertyEditorRenderer;
import org.eclipse.sapphire.ui.renderers.swt.PropertyEditorRendererFactory;
import org.eclipse.sapphire.ui.renderers.swt.ValuePropertyEditorRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.internal.PartPane;
import org.eclipse.ui.internal.PartSite;
import org.eclipse.ui.internal.WorkbenchPartReference;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;


@SuppressWarnings( "restriction" )
public class ScriptPropertyEditorRenderer extends ValuePropertyEditorRenderer
{

	class ScriptLanguageModelPropertyListener extends ModelPropertyListener
	{

		@Override
		public void handlePropertyChangedEvent( ModelPropertyChangeEvent event )
		{

		}
	}

	private class ScriptEditorReference extends WorkbenchPartReference implements IEditorReference
	{

		private IEditorInput editorInput;
		private IEditorPart editor;

		public ScriptEditorReference( IEditorPart editor, IEditorInput input )
		{
			super();
			this.editor = editor;
			this.editorInput = input;
		}

		public IWorkbenchPage getPage()
		{
			return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		}

		public String getFactoryId()
		{
			return null;
		}

		public String getName()
		{
			return null;
		}

		public IEditorPart getEditor( boolean restore )
		{
			return editor;
		}

		public IEditorInput getEditorInput() throws PartInitException
		{
			return this.editorInput;
		}

		@Override
		protected IWorkbenchPart createPart()
		{
			return null;
		}

		@Override
		protected PartPane createPane()
		{
			return null;
		}

	}

	private class ScriptEditorSite extends PartSite implements IEditorSite
	{

		public ScriptEditorSite( IWorkbenchPartReference ref, IWorkbenchPart part, IWorkbenchPage page )
		{
			super( ref, part, page );
		}

		@Override
		public Shell getShell()
		{
			return this.getPage().getActivePart().getSite().getShell();
		}

		@Override
		public IActionBars getActionBars()
		{
			IActionBars bars = ( (PartSite) this.getPage().getActivePart().getSite() ).getActionBars();

			return bars;
		}

		public IEditorActionBarContributor getActionBarContributor()
		{
			return null;
		}

		public void registerContextMenu(
			MenuManager menuManager, ISelectionProvider selectionProvider, boolean includeEditorInput )
		{
		}

		public void registerContextMenu(
			String menuId, MenuManager menuManager, ISelectionProvider selectionProvider, boolean includeEditorInput )
		{
		}

	}

	private ITextEditor textEditor;

	public ScriptPropertyEditorRenderer( SapphireRenderingContext context, PropertyEditorPart part )
	{
		super( context, part );

		IScriptable scriptable = context.getPart().getLocalModelElement().nearest( IScriptable.class );

		ScriptLanguageModelPropertyListener listener = new ScriptLanguageModelPropertyListener();

		scriptable.addListener( listener, "ScriptLanguage" );
	}

	@Override
	protected void createContents( Composite parent )
	{
		final PropertyEditorPart part = getPart();
		final Composite scriptEditorParent = createMainComposite( parent, new CreateMainCompositeDelegate( part )
		{
			@Override
			public boolean canScaleVertically()
			{
				return true;
			}
		} );

		this.context.adapt( scriptEditorParent );

		scriptEditorParent.setLayout( new FillLayout( SWT.HORIZONTAL ) );
		scriptEditorParent.setLayoutData( gdhhint( gdfill(), 300 ) );

		this.textEditor = new TextEditor();
		IFileEditorInput fileInput =
			new FileEditorInput( context.getPart().getLocalModelElement().adapt( IFile.class ).getParent().getFile(
				new Path( "review.txt" ) ) );
        
		IEditorReference ref = new ScriptEditorReference( this.textEditor, fileInput );
		IEditorSite site =
			new ScriptEditorSite(
				ref, this.textEditor, PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage() );
		
		try
		{
			this.textEditor.init( site, fileInput );
		}
		catch ( PartInitException e )
		{
			e.printStackTrace();
		}
		
		this.textEditor.createPartControl( scriptEditorParent );
	}

	@Override
	protected void handlePropertyChangedEvent()
	{
		super.handlePropertyChangedEvent();
	}

	@Override
	protected void handleFocusReceivedEvent()
	{
		super.handleFocusReceivedEvent();
	}

	@Override
	protected boolean canScaleVertically()
	{
		return true;
	}


	public static final class Factory extends PropertyEditorRendererFactory
	{

		@Override
		public boolean isApplicableTo( final PropertyEditorPart propertyEditorDefinition )
		{
			return ( propertyEditorDefinition.getProperty() instanceof ValueProperty );
		}

		@Override
		public PropertyEditorRenderer create( final SapphireRenderingContext context, final PropertyEditorPart part )
		{
			return new ScriptPropertyEditorRenderer( context, part );
		}
	}

}
