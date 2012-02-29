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
   * Time in normalized units.
   */
  private float time = 0;
  
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
  public void update(float _time) {
    time = _time / animation.getDuration();
    assert time >= 0 && time <= 1;
  }

  @Override
  public void updateNormalized(float _time) {
    time = _time;
    assert time >= 0 && time <= 1;
  }

  @Override
  public void getBone(BoneInstance bone_) {
    animation.setBoneNormalized(bone_, time);
  }

  @Override
  public void modifyBone(BoneInstance bone_, float weight) {
    if(weight == 0)
      return;
    else if(weight == 1) {
      getBone(bone_);
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
