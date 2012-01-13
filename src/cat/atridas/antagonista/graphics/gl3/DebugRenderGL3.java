package cat.atridas.antagonista.graphics.gl3;


import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;

import java.nio.BufferOverflowException;
import java.nio.FloatBuffer;
import java.util.Collections;

import javax.vecmath.Matrix4f;

import org.lwjgl.BufferUtils;

import cat.atridas.antagonista.Quality;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.graphics.DebugRender;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.SceneData;
import cat.atridas.antagonista.graphics.Technique;
import cat.atridas.antagonista.graphics.TechniquePass;
import cat.atridas.antagonista.graphics.Effect.TechniqueType;

public class DebugRenderGL3 extends DebugRender {

  private FloatBuffer globalDataBuffer = BufferUtils.createFloatBuffer(InstanceBufferUtils.BUFFER_SIZE);
  private FloatBuffer buffer1, buffer2;
  private int glBuffer = -1, glVAO = -1, glGlobalDataBuffer = -1;
  
  private static final int POS_COL_VERTEX_SIZE = (3 + 3); //Floats
  
  {
    buffer1 = BufferUtils.createFloatBuffer(POS_COL_VERTEX_SIZE * 50);
    buffer2 = BufferUtils.createFloatBuffer(POS_COL_VERTEX_SIZE * 50);
  }
  
  private void initBuffers() {
    assert !cleaned;
    if(glBuffer < 0) {
      glBuffer = glGenBuffers();
      glBindBuffer(GL_ARRAY_BUFFER, glBuffer);
      glBufferData(GL_ARRAY_BUFFER, POS_COL_VERTEX_SIZE * 50, GL_DYNAMIC_DRAW);
      
      
      glGlobalDataBuffer = glGenBuffers();
      glBindBuffer(GL_UNIFORM_BUFFER, glGlobalDataBuffer);
      glBufferData(GL_UNIFORM_BUFFER, InstanceBufferUtils.BUFFER_SIZE, GL_DYNAMIC_DRAW);
      
      
      glVAO = glGenVertexArrays();
      glBindVertexArray(glVAO);

      glBindBuffer(GL_ARRAY_BUFFER, glBuffer);

      glEnableVertexAttribArray(TechniquePass.POSITION_ATTRIBUTE);
      glEnableVertexAttribArray(TechniquePass.COLOR_ATTRIBUTE);

      glVertexAttribPointer(TechniquePass.POSITION_ATTRIBUTE, 3, GL_FLOAT, false, 6*Utils.FLOAT_SIZE, 0);
      glVertexAttribPointer(TechniquePass.COLOR_ATTRIBUTE, 3, GL_FLOAT, false, 6*Utils.FLOAT_SIZE, 3*Utils.FLOAT_SIZE);
      

      glBindVertexArray(0);
      glBindBuffer(GL_ARRAY_BUFFER, 0);
      glBindBuffer(GL_UNIFORM_BUFFER, 0);
      glDisableVertexAttribArray(TechniquePass.POSITION_ATTRIBUTE);
      glDisableVertexAttribArray(TechniquePass.COLOR_ATTRIBUTE);
    }
  }
  
  private void render(int mode, int elements, RenderManager rm) {
    debugMaterial.setUpUniforms(rm);

    Technique technique = debugMaterial.getEffect().getTechnique(TechniqueType.FORWARD, Quality.MID);
    for(TechniquePass pass: technique.passes) {
      pass.activate(rm);
      debugMaterial.setUpUniforms(pass, rm);

      glDrawArrays(mode, 0, elements);
    }
  }
  
  @Override
  protected void renderLines(RenderManager rm) {
    assert !cleaned;

    if(lines.size() == 0)
      return;
    
    try {      
      float[] vertex = new float[POS_COL_VERTEX_SIZE * 2];

      int linesToDraw1 = 0;
      int linesToDraw2 = 0;
      buffer1.rewind();
      buffer2.rewind();

      
      for(Line line: lines) {
        
        vertex[0] = line.origin.x;
        vertex[1] = line.origin.y;
        vertex[2] = line.origin.z;

        vertex[3] = line.color.x;
        vertex[4] = line.color.y;
        vertex[5] = line.color.z;
        
        vertex[6] = line.destination.x;
        vertex[7] = line.destination.y;
        vertex[8] = line.destination.z;

        vertex[9]  = line.color.x;
        vertex[10] = line.color.y;
        vertex[11] = line.color.z;
        
        if(line.depthEnabled) {
          buffer1.put(vertex);
          linesToDraw1++;
        } else {
          buffer2.put(vertex);
          linesToDraw2++;
        }
      }
      
      if(linesToDraw1>0) {
        buffer1.rewind();
      
        glEnable(GL_DEPTH_TEST);
      
        glBufferData(GL_ARRAY_BUFFER, buffer1, GL_DYNAMIC_DRAW);
      
        render(GL_LINES, linesToDraw1 * 2, rm);

        assert !Utils.hasGLErrors();
      }
      
      if(linesToDraw2>0) {
        buffer2.rewind();
      
        glDisable(GL_DEPTH_TEST);
      
        glBufferData(GL_ARRAY_BUFFER, buffer2, GL_DYNAMIC_DRAW);
      
        render(GL_LINES, linesToDraw2 * 2, rm);

        assert !Utils.hasGLErrors();
      }
      
    } catch(BufferOverflowException e) {
      //fem creixer el buffer i rellancem el métode.
      //brut pq és una classe per fer debug.
      buffer1 = BufferUtils.createFloatBuffer( buffer1.capacity() / Utils.FLOAT_SIZE + POS_COL_VERTEX_SIZE * 50 );
      buffer2 = BufferUtils.createFloatBuffer( buffer2.capacity() / Utils.FLOAT_SIZE + POS_COL_VERTEX_SIZE * 50 );
      renderLines(rm);
    }
    
  }
  
  @Override
  protected void renderCrosses() {
    assert !cleaned;
    //TODO
  }
  
  @Override
  protected void renderSpheres() {
    assert !cleaned;
    //TODO
  }
  
  @Override
  protected void renderCircles() {
    assert !cleaned;
    //TODO
  }
  
  @Override
  protected void renderAxes() {
    assert !cleaned;
    //TODO
  }
  
  @Override
  protected void renderTriangles() {
    assert !cleaned;
    //TODO
  }
  
  @Override
  protected void renderAABBs() {
    assert !cleaned;
    //TODO
  }
  
  @Override
  protected void renderOBBs() {
    assert !cleaned;
    //TODO
  }
  
  @Override
  protected void renderStrings() {
    assert !cleaned;
    //TODO
  }

  private boolean prevDepthMask;
  
  @Override
  protected void beginRender(RenderManager rm) {
    assert !cleaned;
    initBuffers();
    prevDepthMask = glGetBoolean(GL_DEPTH_WRITEMASK);
    glDepthMask(false);
    
    glBindVertexArray(glVAO);
    glBindBuffer(GL_ARRAY_BUFFER, glBuffer);
    


    SceneData sd = rm.getSceneData();
    
    Matrix4f mat = new Matrix4f();
    mat.setIdentity();

    globalDataBuffer.rewind();
    sd.getViewProjectionMatrix(mat);
    Utils.matrixToBuffer(mat, globalDataBuffer);
 // a partir d'aqui no se si realment cal
    mat.setIdentity();
    sd.getViewMatrix(mat);
    Utils.matrixToBuffer(mat, globalDataBuffer);
    mat.invert();
    mat.transpose();
    Utils.matrixToBuffer(mat, globalDataBuffer);
 // fins aqui
    globalDataBuffer.rewind();
    glBindBuffer(GL_UNIFORM_BUFFER, glGlobalDataBuffer);
    glBufferData(GL_UNIFORM_BUFFER, globalDataBuffer, GL_DYNAMIC_DRAW);
    glBindBufferBase( GL_UNIFORM_BUFFER, 
                      TechniquePass.BASIC_INSTANCE_UNIFORMS_BINDING, 
                      glGlobalDataBuffer);

    assert !Utils.hasGLErrors();
  }

  @Override
  protected void endRender() {
    assert !cleaned;
    glDepthMask(prevDepthMask);

    glBindVertexArray(0);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glBindBuffer(GL_UNIFORM_BUFFER, 0);
    
    assert !Utils.hasGLErrors();
  }


  protected boolean cleaned = false;
  
  @Override
  public void finalize() {
    if(!cleaned) {
      if(glBuffer > 0) {
        glDeleteBuffers(glBuffer);
        glDeleteBuffers(glGlobalDataBuffer);
        glDeleteVertexArrays(glVAO);
      }
      
      cleaned = true;
    }
  }
}
