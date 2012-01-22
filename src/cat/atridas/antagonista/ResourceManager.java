package cat.atridas.antagonista;

import java.io.InputStream;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

public abstract class ResourceManager<T extends Resource> {
  private static Logger LOGGER = Logger.getLogger(ResourceManager.class.getCanonicalName());
  
  private final HashMap<HashedString, AReference<T>> resources = new HashMap<>();
  private final ReferenceQueue<? super T> refQueue = new ReferenceQueue<>();

  
  private String basePath;
  private ArrayList<HashedString> extensions;
  
  protected ResourceManager() {
    basePath = "";
    extensions = new ArrayList<>();
  }
  
  protected ResourceManager(String _basePath, ArrayList<HashedString> _extensions) {
    basePath = _basePath;
    extensions = new ArrayList<>(_extensions);
  }
  
  protected final void setBasePath(String _basePath) {
    basePath = _basePath;
  }
  
  protected final void setExtensions(ArrayList<HashedString> _extensions) {
    extensions = new ArrayList<>(_extensions);
  }
  
  public final T getResource(HashedString resourceName) {
    //cleanUnusedReferences();
    
    AReference<T> resourceRef = resources.get(resourceName);
    T resource = null;
    if(resourceRef != null)
    {
      if(resourceRef instanceof ResourceManager.AWeakReference) {
        new ASoftReference((ResourceManager<T>.AWeakReference)resourceRef);
      }
      resource = resourceRef.get();
    }
    
    if(resource == null) {
      synchronized(this) {
        if(resourceName == null) {
          resource = getDefaultResource();
        } else {
          resource = createNewResource(resourceName);
          
          //ArrayList<String> extensions = getExtensionsPriorized();
          InputStream is = null;
          
          HashedString extension = null;
          for(int i = 0; i < extensions.size(); ++i) {
            extension = extensions.get(i);
            String path = basePath + resourceName + "." + extension.toString();
            try { //TODO fer aix� d'una manera m�s decent
              is = Utils.findInputStream(path);
              break;
            } catch(Exception e) {
              // ---
            }
          }
          
          if(is == null || !resource.load(is, extension)) {
            LOGGER.warning("Resource " + resourceName + " not found, loading Default resource [" + resource.getClass().getName() + "]");
            resource = getDefaultResource();
          }
        }
        resourceRef = new ASoftReference(resource, resourceName);
      }
    }
    
    return resource;
  }
  
  @SuppressWarnings("unchecked")
  public final synchronized void cleanUnusedReferences() {
    AReference<T> ref;
    while((ref = (AReference<T>)refQueue.poll()) != null) {
      resources.remove(ref.getResourceName());
    }
  }
  
  
  public final synchronized void weakify() {
    for(Entry<HashedString, AReference<T>> entry : resources.entrySet() ) {
      new AWeakReference(entry.getValue());
    }
  }
  
  protected abstract T createNewResource(HashedString name);
  public abstract T getDefaultResource();
  
  public final int getRAMBytesEstimation() {
    cleanUnusedReferences();
    
    int cont = 0;
    for(AReference<T> resourceRef : resources.values()) {
      T resource = resourceRef.get();
      if(resource != null)
        cont += resource.getRAMBytesEstimation();
    }
    return cont;
  }
  
  public final int getVRAMBytesEstimation() {
    cleanUnusedReferences();
    
    int cont = 0;
    for(AReference<T> resourceRef : resources.values()) {
      T resource = resourceRef.get();
      if(resource != null)
        cont += resource.getVRAMBytesEstimation();
    }
    return cont;
  }
  
  private static interface AReference<K> {
    HashedString getResourceName();
    
    K get();
  }
  
  private final class ASoftReference extends SoftReference<T> implements AReference<T> {
    HashedString resourceName;


    public ASoftReference(T referent, HashedString _resourceName) {
      super(referent, refQueue);
      resourceName = _resourceName;
      resources.put(resourceName, this);
    }

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
  
  private final class AWeakReference extends WeakReference<T> implements AReference<T> {
    HashedString resourceName;
    


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
