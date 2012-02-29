package cat.atridas.antagonista.graphics.animation;

import cat.atridas.antagonista.HashedString;

/**
 * Interface representing an animation.
 * 
 * @author Isaac 'Atridas' Serrano Guasch
 * @since 0.3
 */
public interface AnimationInstance {
  /**
   * Sets a blending parameter.
   * 
   * @param parameter identifier.
   * @param value to the parameter.
   * @since 0.3
   */
  void setParameter(HashedString parameter, float value);

  /**
   * Updates the state of the animation.
   * 
   * @param time in seconds.
   * @since 0.3
   */
  void update(float time);
  /**
   * Updates the state of the animation.
   * 
   * @param time in normalized units (0 is the beginning, 1 the end of the animation).
   * @since 0.3
   */
  void updateNormalized(float time);

  /**
   * Gets the state of a bone.
   * 
   * @param bone_ to retrieve (output parameter).
   * @since 0.3
   */
  void getBone(BoneInstance bone_);
  /**
   * Gets the state of a bone. This function blends the current state with the state of this animation.
   * If the weight is 0, the bone is left unmodified, if it is 1, it gets all the power of this 
   * animation.
   * 
   * @param bone_ to retrieve (output parameter).
   * @param weight of the blending.
   * @since 0.3
   */
  void modifyBone(BoneInstance bone_, float weight);
  
  /**
   * Gets the duration of the animation.
   * 
   * @return the duration of the animation.
   */
  float getDuration();
}
