package cat.atridas.antagonista;

import java.io.InputStream;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

/**
 * <p>
 * Base ResourceManager. It has capabilities to cache all resources. To use this
 * class, extend it and implement its methods.
 * </p>
 * <p>
 * This class saves the loaded resources with a SoftReference, witch means it
 * will not delete any of if it is still referenced from anywhere. Still, if it
 * is not referred, won't be deleted until the JVM runs out of memory. Still,
 * this class knows nothing about Video Memory, so provides a
 * <code>weakify</code> method that turns all Soft References to Weak
 * References, witch means that all unused references will be cleaned in the
 * next garbage collection.
 * </p>
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.1
 * 
 * @see java.lang.ref.SoftReference
 * @see java.lang.ref.WeakReference
 * @see #weakify()
 * 
 * @param <T>
 *          Class of the resources this manager will have.
 */
public abstract class ResourceManager<T extends Resource> {
  private static Logger LOGGER = Logger.getLogger(ResourceManager.class
      .getCanonicalName());

  private final HashMap<HashedString, AReference<T>> resources = new HashMap<>();
  private final ReferenceQueue<? super T> refQueue = new ReferenceQueue<>();

  private String basePath;
  private ArrayList<HashedString> extensions;

  /**
   * Creates a ResourceManager. If you use this Constructor, you must call both
   * <code>setBasePath</code> and <code>setExtensions</code> before loading any
   * resource.
   * 
   * @since 0.1
   */
  protected ResourceManager() {
    basePath = "";
    extensions = new ArrayList<>();
  }

  /**
   * <p>
   * Creates a ResourceManager, with the basic fields initialized.
   * </p>
   * <p>
   * All resources will be searched chaining the base path with the resource
   * name and a extension (preceded with a dot), starting the search with the
   * first extension in the list.
   * </p>
   * 
   * @param _basePath
   *          Path where the resources will be searched.
   * @param _extensions
   *          Extensions of the resources to be loaded.
   * @since 0.1
   */
  public ResourceManager(String _basePath, ArrayList<HashedString> _extensions) {
    basePath = _basePath;
    extensions = new ArrayList<>(_extensions);
  }

  /**
   * Sets the base path where the resources will be searched.
   * 
   * @param _basePath
   *          Path where the resources will be searched.
   * @since 0.1
   */
  protected final void setBasePath(String _basePath) {
    basePath = _basePath;
  }

  /**
   * Sets the extensions that the files containing the resources will have,
   * ordered with the one with the highest priority first.
   * 
   * @param _extensions
   *          Extensions of the resources to be loaded.
   * @since 0.1
   */
  protected final void setExtensions(ArrayList<HashedString> _extensions) {
    extensions = new ArrayList<>(_extensions);
  }

  /**
   * <p>
   * Gets a resource on the manager. If the resource is not cached, the manager
   * will load, using the base path and the extensions setted.
   * </p>
   * <p>
   * On an error loading all possible files a default resource will be loaded.
   * All resources will be cached, even those loaded with the default resource,
   * and the manager will never try to load them again.
   * </p>
   * 
   * @param resourceName
   *          name of the resource to be loaded or getted.
   * @return the resource found.
   * @since 0.1
   */
  public final T getResource(HashedString resourceName) {
    // cleanUnusedReferences();
    assert basePath.compareTo("") != 0;
    assert extensions.size() > 0;

    AReference<T> resourceRef = resources.get(resourceName);
    T resource = null;
    if (resourceRef != null) {
      if (resourceRef instanceof ResourceManager.AWeakReference) {
        // if the reference had been weakified, transform it into a soft
        // reference.
        new ASoftReference((ResourceManager<T>.AWeakReference) resourceRef);
      }
      resource = resourceRef.get();
    }

    if (resource == null) {
      synchronized (this) {
        if (resourceName == null) {
          resource = getDefaultResource();
        } else {
          resource = createNewResource(resourceName);

          // ArrayList<String> extensions = getExtensionsPriorized();
          InputStream is = null;

          HashedString extension = null;
          for (int i = 0; i < extensions.size(); ++i) {
            extension = extensions.get(i);
            String path = basePath + resourceName + "." + extension.toString();
            try { // TODO fer aix� d'una manera m�s decent
              is = Utils.findInputStream(path);
              break;
            } catch (Exception e) {
              // ---
            }
          }

          if (is == null || !resource.load(is, extension)) {
            LOGGER.warning("Resource " + resourceName
                + " not found, loading Default resource ["
                + resource.getClass().getName() + "]");
            resource = getDefaultResource();
          }
        }
        resourceRef = new ASoftReference(resource, resourceName);
      }
    }

    return resource;
  }

  /**
   * This method must be called from time to time in order to clean unused
   * references.
   * 
   * @since 0.1
   */
  @SuppressWarnings("unchecked")
  public final synchronized void cleanUnusedReferences() {
    AReference<T> ref;
    while ((ref = (AReference<T>) refQueue.poll()) != null) {
      resources.remove(ref.getResourceName());
    }
  }

  /**
   * Turns all soft references into weak references. This references will be
   * cleaned if are not used anywhere else in the engine in the next garbage
   * collection. When used again, references will turn into soft references
   * again.
   * 
   * @since 0.1
   */
  public final synchronized void weakify() {
    for (Entry<HashedString, AReference<T>> entry : resources.entrySet()) {
      new AWeakReference(entry.getValue());
    }
  }

  /**
   * Implementations will overwrite this class to create new resources before
   * loading them.
   * 
   * @param name
   *          name of the resource to be loaded. Just set it in the Resource
   *          Constructor.
   * @return a new, unitialized, resource.
   * @since 0.1
   */
  protected abstract T createNewResource(HashedString name);

  /**
   * Gets a default resource, that must not fail ever.
   * 
   * @return the default resource.
   * @since 0.1
   */
  public abstract T getDefaultResource();

  /**
   * Gets the added RAM Bytes estimation of all resources.
   * 
   * @return The memory used by all resources of this manager.
   * @see Resource#getRAMBytesEstimation()
   * @since 0.1
   */
  public final int getRAMBytesEstimation() {
    cleanUnusedReferences();

    int cont = 0;
    for (AReference<T> resourceRef : resources.values()) {
      T resource = resourceRef.get();
      if (resource != null)
        cont += resource.getRAMBytesEstimation();
    }
    return cont;
  }

  /**
   * Gets the added VRAM Bytes estimation of all resources.
   * 
   * @return The memory used by all resources of this manager.
   * @see Resource#getVRAMBytesEstimation()
   * @since 0.1
   */
  public final int getVRAMBytesEstimation() {
    cleanUnusedReferences();

    int cont = 0;
    for (AReference<T> resourceRef : resources.values()) {
      T resource = resourceRef.get();
      if (resource != null)
        cont += resource.getVRAMBytesEstimation();
    }
    return cont;
  }

  /**
   * Used in the map to contain a resource. Can either be a weak or soft
   * reference. It also contains a reference to its key, to easy map cleaning.
   * 
   * @author Isaac 'Atridas' Serrano Guasch
   * @since 0.1
   * 
   * @param <K>
   *          will be the same as {@link ResourceManager}'s &lt;T&gt;
   */
  private static interface AReference<K> {
    /**
     * Returns the resource identifier.
     * 
     * @return resource identifier.
     * @since 0.1
     */
    HashedString getResourceName();

    /**
     * Returns the resource referenced, or <code>null</code> if it has been
     * garbage collected.
     * 
     * @return <code>null</code> if the resource has been garbage collected.
     * @since 0.1
     */
    K get();
  }

  /**
   * Encapsulates a soft reference with the map key.
   * 
   * @author Isaac 'Atridas' Serrano Guasch
   * @since 0.1
   * 
   */
  private final class ASoftReference extends SoftReference<T> implements
      AReference<T> {
    HashedString resourceName;

    /**
     * Creates the reference and inserts it into the map.
     * 
     * @param referent
     * @param _resourceName
     * @since 0.1
     */
    public ASoftReference(T referent, HashedString _resourceName) {
      super(referent, refQueue);
      resourceName = _resourceName;
      resources.put(resourceName, this);
    }

    /**
     * Transforms a weak reference into a soft reference.
     * 
     * @param _ref
     * @since 0.1
     */
    public ASoftReference(AWeakReference _ref) {
      super(_ref.get(), refQueue);
      resourceName = _ref.getResourceName();
      resources.put(resourceName, this);
    }

    @Override
    public HashedString getResourceName() {
      return resourceName;
    }

  }

  /**
   * Encapsulates a weak reference with the map key.
   * 
   * @author Isaac 'Atridas' Serrano Guasch
   * @since 0.1
   * 
   */
  private final class AWeakReference extends WeakReference<T> implements
      AReference<T> {
    HashedString resourceName;

    /**
     * Transforms a soft reference into a weak reference.
     * 
     * @param _ref
     * @since 0.1
     */
    public AWeakReference(AReference<T> _ref) {
      super(_ref.get(), refQueue);
      resourceName = _ref.getResourceName();
      resources.put(resourceName, this);
    }

    @Override
    public HashedString getResourceName() {
      return resourceName;
    }

  }
}
