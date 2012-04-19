package cat.atridas.antagonista.graphics.animation;

public interface AnimationInterface {

  /**
   * Sets the bone parameters to this animation state, in the time specified. If
   * the time isn't between 0 and the duration of this animation, the result is
   * unknown (may throw an exception).
   * 
   * @param bone_
   *          to be animated.
   * @param time
   *          in seconds, of this animation.
   */
  void setBone(BoneInstance bone_, float time);

  /**
   * Sets the bone parameters to this animation, in the time specified, as if
   * the whole animation would last 1 unit. If the time isn't between 0 and 1,
   * the result is unknown (may throw an exception).
   * 
   * @param bone_
   *          to be animated.
   * @param normalizedTime
   *          a value between 0 and 1
   */
  void setBoneNormalized(BoneInstance bone_, float normalizedTime);
}
