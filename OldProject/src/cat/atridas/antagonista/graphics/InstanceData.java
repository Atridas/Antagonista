package cat.atridas.antagonista.graphics;

import javax.vecmath.Color4f;
import javax.vecmath.Matrix4f;

/**
 * Encapsulation of all the data saved within one rendered object.
 * 
 * @author Isaac 'Atridas' Serrano Guasch.
 * @since 0.1
 *
 */
public class InstanceData {
  /**
   * Model View Projection transformation matrix. This matrix transforms a vertex from
   * local coordinates to homogeneous coordinates.
   * @since 0.1
   */
  public final Matrix4f modelViewProj      = new Matrix4f();
  /**
   * Model View transformation matrix. This matrix transforms a vertex from
   * local coordinates to view coordinates (where the camera is the origin).
   * @since 0.1
   */
  public final Matrix4f modelView          = new Matrix4f();
  /**
   * Inverse Model View transformation matrix. This matrix transforms a vertex from
   * view coordinates (where the camera is the origin) to local coordinates.
   * @since 0.1
   */
  public final Matrix4f modelViewInvTransp = new Matrix4f();

  /**
   * Special color used to change the color of certain materials.
   * @since 0.1
   */
  public final Color4f specialColor0 = new Color4f();
  /**
   * Special color used to change the color of certain materials.
   * @since 0.1
   */
  public final Color4f specialColor1 = new Color4f();
  /**
   * Special color used to change the color of certain materials.
   * @since 0.1
   */
  public final Color4f specialColor2 = new Color4f();
  /**
   * Special color used to change the color of certain materials.
   * @since 0.1
   */
  public final Color4f specialColor3 = new Color4f();
  
  /**
   * Matrix palete used to animate bones.
   * @since 0.3
   */
  public Matrix4f[] bonePalete;
}
