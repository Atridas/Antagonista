package cat.atridas.antagonista.graphics;

import javax.vecmath.Color4f;
import javax.vecmath.Matrix4f;

public class InstanceData {
  public final Matrix4f modelViewProj      = new Matrix4f();
  public final Matrix4f modelView          = new Matrix4f();
  public final Matrix4f modelViewInvTransp = new Matrix4f();

  public final Color4f specialColor0 = new Color4f();
  public final Color4f specialColor1 = new Color4f();
  public final Color4f specialColor2 = new Color4f();
  public final Color4f specialColor3 = new Color4f();
}
