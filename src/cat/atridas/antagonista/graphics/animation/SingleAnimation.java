package cat.atridas.antagonista.graphics.animation;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.core.Core;

/**
 * Animation state of a single animation.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.3
 *
 */
public final class SingleAnimation implements AnimationInstance {
  
  /**
   * Encapsulated animation.
   * @since 0.3
   */
  private final AnimationCore animation;
  
  /**
   * Builds this object with the animation identified.
   * 
   * @param animationID animation identifier.
   * @since 0.3
   */
  public SingleAnimation(HashedString animationID) {
    animation = Core.getCore().getAnimationManager().getResource(animationID);
  }

  @Override
  public void setParameter(HashedString parameter, float value) {
    // --
  }

  @Override
  public void getBone(BoneInstance bone_, float time) {
    animation.setBone(bone_, time);
  }

  @Override
  public void getBoneNormalized(BoneInstance bone_, float time) {
    animation.setBoneNormalized(bone_, time);
  }

  @Override
  public void modifyBone(BoneInstance bone_, float weight, float time) {
    if(weight == 0)
      return;
    else if(weight == 1) {
      getBone(bone_, time);
      return;
    }
    assert weight > 0 && weight < 1;
    
    animation.setBone(bone_, time, weight);
  }

  @Override
  public void modifyBoneNormalized(BoneInstance bone_, float weight, float time) {
    if(weight == 0)
      return;
    else if(weight == 1) {
      getBoneNormalized(bone_, time);
      return;
    }
    assert weight > 0 && weight < 1;
    
    animation.setBoneNormalized(bone_, time, weight);
  }

  @Override
  public float getDuration() {
    return animation.getDuration();
  }
}
