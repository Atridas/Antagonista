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
   * Gets the state of a bone.
   * 
   * @param bone_ to retrieve (output parameter).
   * @param time (from 0 to duration).
   * @since 0.3
   */
  void getBone(BoneInstance bone_, float time);
  /**
   * Gets the state of a bone.
   * 
   * @param bone_ to retrieve (output parameter).
   * @param time (from 0 to 1).
   * @since 0.3
   */
  void getBoneNormalized(BoneInstance bone_, float time);

  /**
   * Gets the state of a bone. This function blends the current state with the state of this animation.
   * If the weight is 0, the bone is left unmodified, if it is 1, it gets all the power of this 
   * animation.
   * 
   * @param bone_ to retrieve (output parameter).
   * @param weight of the blending.
   * @param time (from 0 to duration).
   * @since 0.3
   */
  void modifyBone(BoneInstance bone_, float weight, float time);
  /**
   * Gets the state of a bone. This function blends the current state with the state of this animation.
   * If the weight is 0, the bone is left unmodified, if it is 1, it gets all the power of this 
   * animation.
   * 
   * @param bone_ to retrieve (output parameter).
   * @param weight of the blending.
   * @param time (from 0 to 1).
   * @since 0.3
   */
  void modifyBoneNormalized(BoneInstance bone_, float weight, float time);
  
  /**
   * Gets the duration of the animation.
   * 
   * @return the duration of the animation.
   */
  float getDuration();
}
