package cat.atridas.antagonista;

import java.io.InputStream;

public interface Resource {
  boolean load(InputStream is); //TODO throws
  
  int getRAMBytesEstimation();
  int getVRAMBytesEstimation();
}
