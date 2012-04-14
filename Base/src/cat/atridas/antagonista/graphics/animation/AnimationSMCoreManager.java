package cat.atridas.antagonista.graphics.animation;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.ResourceManager;

public class AnimationSMCoreManager extends ResourceManager<AnimationSMCore> {
  
  private AnimationSMCore defaultResource = new AnimationSMCore(new HashedString("default"));

  @Override
  protected AnimationSMCore createNewResource(HashedString name) {
    return new AnimationSMCore(name);
  }

  @Override
  public AnimationSMCore getDefaultResource() {
    return defaultResource;
  }

}
