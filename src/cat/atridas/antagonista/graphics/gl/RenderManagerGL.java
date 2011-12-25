package cat.atridas.antagonista.graphics.gl;

import cat.atridas.antagonista.graphics.RenderManager;

import static org.lwjgl.opengl.GL11.*;

public final class RenderManagerGL extends RenderManager {

  @Override
  public void initGL() {
    glClearColor(1,0,1,0);
    
    glEnable(GL_BLEND);
    
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    
    glEnable(GL_DEPTH_TEST);
    glClearDepth(1);
  }

  @Override
  public void initFrame() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  }
}
