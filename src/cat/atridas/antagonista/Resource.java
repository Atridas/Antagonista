package cat.atridas.antagonista;

import java.io.InputStream;

public abstract class Resource {
  
  protected final HashedString resourceName;
  
  protected Resource(HashedString _resourceName) {
    resourceName = _resourceName;
  }
  
  public abstract boolean load(InputStream is, String extension); //TODO throws
  
  public abstract int getRAMBytesEstimation();
  public abstract int getVRAMBytesEstimation();
  
  protected boolean cleaned = false;
      
  public abstract void cleanUp();
  
  @Override
  public void finalize() {
    if(!cleaned) {
      cleanUp();
    }
  }
}
