package cat.atridas.antagonista;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class ResourceManager<T extends Resource> {
  
  private final HashMap<HashedString, Reference> resources = new HashMap<>();
  private final ReferenceQueue<? super T> refQueue = new ReferenceQueue<>();
  
  
  public final T getResource(HashedString resourceName) {
    //cleanUnusedReferences();
    
    SoftReference<T> resourceRef = resources.get(resourceName);
    T resource = null;
    if(resourceRef != null)
    {
      resource = resourceRef.get();
    }
    
    if(resource == null) {
      synchronized(this) {
        resource = createNewResource();
        
        ArrayList<String> extensions = getExtensionsPriorized();
        InputStream is = null;
        
        String extension = null;
        for(int i = 0; i < extensions.size(); ++i) {
          extension = extensions.get(i);
          String path = getBasePath() + resourceName + "." + extension;
          try { //TODO fer aix� d'una manera m�s decent
            is = Utils.findInputStream(path);
            break;
          } catch(FileNotFoundException e) {
            // ---
          }
        }
        
        if(is == null || !resource.load(is, extension))
          resource = getDefaultResource();
        
        resourceRef = new Reference(resource, resourceName);
      }
    }
    
    return resource;
  }
  
  @SuppressWarnings("unchecked")
  public final synchronized void cleanUnusedReferences() {
    Reference ref;
    while((ref = (Reference)refQueue.poll()) != null) {
      resources.remove(ref.resourceName);
    }
  }
  
  protected abstract String getBasePath();
  protected abstract ArrayList<String> getExtensionsPriorized();
  protected abstract T createNewResource();
  protected abstract T getDefaultResource();
  
  public final int getRAMBytesEstimation() {
    cleanUnusedReferences();
    
    int cont = 0;
    for(Reference resourceRef : resources.values()) {
      T resource = resourceRef.get();
      if(resource != null)
        cont += resource.getRAMBytesEstimation();
    }
    return cont;
  }
  
  public final int getVRAMBytesEstimation() {
    cleanUnusedReferences();
    
    int cont = 0;
    for(Reference resourceRef : resources.values()) {
      T resource = resourceRef.get();
      if(resource != null)
        cont += resource.getVRAMBytesEstimation();
    }
    return cont;
  }
  
  private final class Reference extends SoftReference<T> {
    HashedString resourceName;

    public Reference(T referent, HashedString _resourceName) {
      super(referent, refQueue);
      resourceName = _resourceName;
      resources.put(resourceName, this);
    }
    
  }
}
