package com.liferay.ide.velocity.editor.completion;

import java.util.ArrayList;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * The {@link ProviderManager} manages all
 * {@link com.liferay.ide.velocity.editor.completion.ICompletionProvider} extensions.
 *
 * @author Peter Friese
 */
public final class ProviderManager {

    /** Singleton instance of this {@link ProviderManager}. */
    private static ProviderManager instance;

    /** This list contains all provider proxies. */
    private ArrayList providerProxies;

    /** The ID for the completion providers extenstion point. */
	private String ID_EXTENSION_PROVIDERS = "com.liferay.ide.velocity.completionproviders";

    /** The ID of the provider config element. */
    private String ID_EXTENSION_PROVIDER = "provider";

    /**
     * Hide constructor.
     */
    private ProviderManager() {
        super();
    }

    /**
     * @return The singleton instance of this provider manager.
     */
    public static ProviderManager getInstance() {
        if (instance == null) {
            instance = new ProviderManager();
            instance.init();
        }
        return instance;
    }

    /**
     * Initialize the provider manager. All provider extension declared in the extension registry
     * will be returned as proxy instances.
     */
    private void init() {
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint longrunnerProvidersExtensionPoint = registry
                .getExtensionPoint(ID_EXTENSION_PROVIDERS );

        // get all extensions registered to this extension point
        IExtension[] extensions = longrunnerProvidersExtensionPoint.getExtensions();

        instantiateProxies(extensions);
    }

    /**
     * This method instantiates proxies for extensions registered to the
     * <code>com.lhsystems.rcp.longrunner.providers</code> extension point.
     *
     * @param extensions An array of extensions to the
     * <code>com.lhsystems.rcp.longrunner.providers</code> extension point.
     */
    private void instantiateProxies(IExtension[] extensions) {
        providerProxies = new ArrayList();
        for (int i = 0; i < extensions.length; i++) {
            IExtension extension = extensions[i];
            IConfigurationElement[] configurationElements = extension.getConfigurationElements();
            for (int j = 0; j < configurationElements.length; j++) {
                IConfigurationElement element = configurationElements[j];
                if (element.getName().equals(ID_EXTENSION_PROVIDER)) {
                    providerProxies.add(new CompletionProviderProxy(element));
                }
            }
        }
    }

    /**
     * @return An array containing all providers managed by this manager.
     */
    public ICompletionProvider[] getProviders() {
        return (ICompletionProvider[]) providerProxies
                .toArray(new ICompletionProvider[(providerProxies.size())]);
    }

}
