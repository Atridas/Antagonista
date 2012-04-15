package cat.atridas.antagonista.physics.bullet;

import javax.vecmath.Color3f;
import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.core.Core;
import cat.atridas.antagonista.graphics.DebugRender;
import cat.atridas.antagonista.graphics.Font;

import com.bulletphysics.linearmath.DebugDrawModes;
import com.bulletphysics.linearmath.IDebugDraw;

/**
 * Utility class to draw debug information from the jBullet engine.
 * 
 * @author Isaac 'Atridas' Serrano Guasch.
 * @since 0.2
 *
 */
public class PhysicsDebugDrawer extends IDebugDraw {

  /**
   * Marks if debug primitives should be rendered with the zTest activated.
   * @since 0.2
   */
  public boolean zTest = false;
  
  /**
   * Height of 3d text.
   * @since 0.2
   */
  public float text3DHeight = 0.2f;
  /**
   * Height of 2d text.
   * @since 0.2
   */
  public float text2DHeight = 0.05f;
  
  /**
   * Default debug color.
   * @since 0.2
   */
  public final Color3f defaultDebugColor = new Color3f(0,0,1);
  
  /**
   * Font used in 3d text.
   * @since 0.2
   */
  public Font font = Core.getCore().getFontManager().getResource(new HashedString("font14")); 
  
  
  private final DebugRender dr = Core.getCore().getDebugRender();
  
  @Override
  public void draw3dText(Vector3f position, String text) {
    assert font != null;
    dr.addString(new Point3f(position), font, text, text3DHeight, defaultDebugColor,zTest);
  }

  @Override
  public void drawContactPoint(Vector3f PointOnB, Vector3f normalOnB, float distance,
      int lifeTime, Vector3f color) {
    dr.addCross(new Point3f(PointOnB), new Color3f(color), 1,lifeTime,zTest);
    Point3f destination = new Point3f(PointOnB);
    destination.add(normalOnB);
    dr.addLine(new Point3f(PointOnB), destination, new Color3f(color),lifeTime,zTest);
  }

  @Override
  public void drawLine(Vector3f from, Vector3f to, Vector3f color) {
    dr.addLine(new Point3f(from), new Point3f(to), new Color3f(color),zTest);
  }

  @Override
  public void reportErrorWarning(String text) {
    dr.addString2D(new Point2f(0,0), font, text, text2DHeight, defaultDebugColor);
  }

  //private int debugMode = DebugDrawModes.MAX_DEBUG_DRAW_MODE;
  private int debugMode = DebugDrawModes.DRAW_WIREFRAME;
  //private int debugMode = DebugDrawModes.DRAW_WIREFRAME | DebugDrawModes.DRAW_AABB;
  
  @Override
  public int getDebugMode() {
    return debugMode;
  }

  @Override
  public void setDebugMode(int _debugMode) {
    debugMode = _debugMode;
  }

}
