package cat.atridas.antagonista;

import java.io.InputStream;

public abstract class Resource {
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
