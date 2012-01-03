package cat.atridas.antagonista.graphics;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Resource;

public abstract class Texture extends Resource {
  
  protected int id;
  protected int minFilter, magFilter;
  
  protected Texture(HashedString resourceName) {
    super(resourceName);
  }


  protected abstract void loadDefault();
  
  public abstract void activate(int unit);
  
  public abstract void noTexture();
  
  public static enum FilterQuality {LOW, MID, HIGH, ULTRA}
  
  protected abstract int getTarget();
  protected abstract boolean isMipMapped();
  
  public abstract int getMinParameter(FilterQuality quality);
  public abstract int getMagParameter(FilterQuality quality);
  
  public final void setMinFilter(FilterQuality quality) {
    assert !cleaned;
    minFilter = getMinParameter(quality);
    
  }
  public final void setMagFilter(FilterQuality quality) {
    assert !cleaned;
    magFilter = getMagParameter(quality);
  }
}
