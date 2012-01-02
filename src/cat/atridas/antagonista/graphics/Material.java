package cat.atridas.antagonista.graphics;

import java.io.InputStream;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Resource;

public abstract class Material extends Resource {
  //TODO

  public Material(HashedString _resourceName) {
    super(_resourceName);
  }
  
  @Override
  public boolean load(InputStream is, String extension) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public int getRAMBytesEstimation() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int getVRAMBytesEstimation() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public void cleanUp() {
    // TODO Auto-generated method stub

  }

}
