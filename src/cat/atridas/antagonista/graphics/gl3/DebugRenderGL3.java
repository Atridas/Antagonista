package cat.atridas.antagonista.graphics.gl3;


import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL33.*;

import java.nio.BufferOverflowException;
import java.nio.FloatBuffer;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import org.lwjgl.BufferUtils;

import cat.atridas.antagonista.Quality;
import cat.atridas.antagonista.Transformation;
import cat.atridas.antagonista.Utils;
import cat.atridas.antagonista.graphics.DebugRender;
import cat.atridas.antagonista.graphics.RenderManager;
import cat.atridas.antagonista.graphics.SceneData;
import cat.atridas.antagonista.graphics.Technique;
import cat.atridas.antagonista.graphics.TechniquePass;
import cat.atridas.antagonista.graphics.Effect.TechniqueType;

public class DebugRenderGL3 extends DebugRender {
  
  public final static int SPHERE_STACKS = 15;
  public final static int SPHERE_SUBDIV = 15;
  

  private FloatBuffer globalDataBuffer = BufferUtils.createFloatBuffer(InstanceBufferUtils.BUFFER_SIZE);
  private FloatBuffer buffer1, buffer2, buffer3, buffer4;
  private int linesBuffer = -1, linesVAO = -1, singleInstanceGlobalDataBuffer = -1;
  
  
  private int multipleInstancesGlobalDataBuffer = -1, instancesColorBuffer = -1;
  
  //esferes
  private int sphereIndexBuffer = -1, sphereVertexBuffer = -1, sphereVAO = -1;
  
  //creus
  private int crossesVertexBuffer = -1, crossesVAO = -1;
  
  //cercles
  private int circlesVertexBuffer = -1, circlesVAO = -1;
  
  //axes
  private int axesVertexBuffer = -1, axesVAO = -1;
  
  //triangles ho farem com les línees
  
  //BBs
  private int bbIndexBuffer = -1, bbVertexBuffer = -1, bbVAO = -1;
  
  private static final int POS_COL_VERTEX_SIZE = (3 + 3); //Floats
  
  {
    buffer1 = BufferUtils.createFloatBuffer(POS_COL_VERTEX_SIZE * 50);
    buffer2 = BufferUtils.createFloatBuffer(POS_COL_VERTEX_SIZE * 50);
    buffer3 = BufferUtils.createFloatBuffer(POS_COL_VERTEX_SIZE * 50);
    buffer4 = BufferUtils.createFloatBuffer(POS_COL_VERTEX_SIZE * 50);
  }
  
  private void initSphereBuffers() {
    assert !cleaned;
    

    sphereVAO = glGenVertexArrays();
    glBindVertexArray(sphereVAO);
    
    sphereVertexBuffer = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, sphereVertexBuffer);
    glBufferData(GL_ARRAY_BUFFER,createSphereVertexBuffer(), GL_STATIC_DRAW);
    
    sphereIndexBuffer = glGenBuffers();
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, sphereIndexBuffer);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, createSphereIndexBuffer(), GL_STATIC_DRAW);
    


    glEnableVertexAttribArray(TechniquePass.POSITION_ATTRIBUTE);
    glEnableVertexAttribArray(TechniquePass.COLOR_ATTRIBUTE);

    glVertexAttribPointer(TechniquePass.POSITION_ATTRIBUTE, 3, GL_FLOAT, false, 0, 0);
    
    
    glBindBuffer(GL_ARRAY_BUFFER, instancesColorBuffer);
    
    glVertexAttribPointer(TechniquePass.COLOR_ATTRIBUTE, 3, GL_FLOAT, false, 0, 0);
    glVertexAttribDivisor(TechniquePass.COLOR_ATTRIBUTE, 1);

    
    
    glBindVertexArray(0);
    glDisableVertexAttribArray(TechniquePass.POSITION_ATTRIBUTE);
    glDisableVertexAttribArray(TechniquePass.COLOR_ATTRIBUTE);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    
    assert !Utils.hasGLErrors();
  }
  
  private void initCrossesBuffers() {
    assert !cleaned;
    

    crossesVAO = glGenVertexArrays();
    glBindVertexArray(crossesVAO);
    
    crossesVertexBuffer = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, crossesVertexBuffer);
    glBufferData(GL_ARRAY_BUFFER,createCrossVertexBuffer(), GL_STATIC_DRAW);
    


    glEnableVertexAttribArray(TechniquePass.POSITION_ATTRIBUTE);
    glEnableVertexAttribArray(TechniquePass.COLOR_ATTRIBUTE);

    glVertexAttribPointer(TechniquePass.POSITION_ATTRIBUTE, 3, GL_FLOAT, false, 0, 0);
    
    
    glBindBuffer(GL_ARRAY_BUFFER, instancesColorBuffer);
    
    glVertexAttribPointer(TechniquePass.COLOR_ATTRIBUTE, 3, GL_FLOAT, false, 0, 0);
    glVertexAttribDivisor(TechniquePass.COLOR_ATTRIBUTE, 1);

    
    
    glBindVertexArray(0);
    glDisableVertexAttribArray(TechniquePass.POSITION_ATTRIBUTE);
    glDisableVertexAttribArray(TechniquePass.COLOR_ATTRIBUTE);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    
    assert !Utils.hasGLErrors();
  }
  
  private void initBBsBuffers() {
    assert !cleaned;

    bbVAO = glGenVertexArrays();
    glBindVertexArray(bbVAO);
    
    bbVertexBuffer = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, bbVertexBuffer);
    glBufferData(GL_ARRAY_BUFFER,createBBVertexBuffer(), GL_STATIC_DRAW);
    
    bbIndexBuffer = glGenBuffers();
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bbIndexBuffer);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, createBBIndexBuffer(), GL_STATIC_DRAW);
    


    glEnableVertexAttribArray(TechniquePass.POSITION_ATTRIBUTE);
    glEnableVertexAttribArray(TechniquePass.COLOR_ATTRIBUTE);

    glVertexAttribPointer(TechniquePass.POSITION_ATTRIBUTE, 3, GL_FLOAT, false, 0, 0);
    
    
    glBindBuffer(GL_ARRAY_BUFFER, instancesColorBuffer);
    
    glVertexAttribPointer(TechniquePass.COLOR_ATTRIBUTE, 3, GL_FLOAT, false, 0, 0);
    glVertexAttribDivisor(TechniquePass.COLOR_ATTRIBUTE, 1);

    
    
    glBindVertexArray(0);
    glDisableVertexAttribArray(TechniquePass.POSITION_ATTRIBUTE);
    glDisableVertexAttribArray(TechniquePass.COLOR_ATTRIBUTE);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    
    assert !Utils.hasGLErrors();
  }
  
  private void initCirclesBuffers() {
    assert !cleaned;
    

    circlesVAO = glGenVertexArrays();
    glBindVertexArray(circlesVAO);
    
    circlesVertexBuffer = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, circlesVertexBuffer);
    glBufferData(GL_ARRAY_BUFFER,createCircleVertexBuffer(), GL_STATIC_DRAW);
    


    glEnableVertexAttribArray(TechniquePass.POSITION_ATTRIBUTE);
    glEnableVertexAttribArray(TechniquePass.COLOR_ATTRIBUTE);

    glVertexAttribPointer(TechniquePass.POSITION_ATTRIBUTE, 3, GL_FLOAT, false, 0, 0);
    
    
    glBindBuffer(GL_ARRAY_BUFFER, instancesColorBuffer);
    
    glVertexAttribPointer(TechniquePass.COLOR_ATTRIBUTE, 3, GL_FLOAT, false, 0, 0);
    glVertexAttribDivisor(TechniquePass.COLOR_ATTRIBUTE, 1);

    
    
    glBindVertexArray(0);
    glDisableVertexAttribArray(TechniquePass.POSITION_ATTRIBUTE);
    glDisableVertexAttribArray(TechniquePass.COLOR_ATTRIBUTE);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    
    assert !Utils.hasGLErrors();
  }
  
  private void initAxesBuffers() {
    assert !cleaned;
    

    axesVAO = glGenVertexArrays();
    glBindVertexArray(axesVAO);
    
    axesVertexBuffer = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, axesVertexBuffer);
    glBufferData(GL_ARRAY_BUFFER,createAxesVertexBuffer(), GL_STATIC_DRAW);
    


    glEnableVertexAttribArray(TechniquePass.POSITION_ATTRIBUTE);
    glEnableVertexAttribArray(TechniquePass.COLOR_ATTRIBUTE   );

    glVertexAttribPointer(TechniquePass.POSITION_ATTRIBUTE, 3, GL_FLOAT, false, 6 * Utils.FLOAT_SIZE, 0                   );
    glVertexAttribPointer(TechniquePass.COLOR_ATTRIBUTE   , 3, GL_FLOAT, false, 6 * Utils.FLOAT_SIZE, 3 * Utils.FLOAT_SIZE);

    
    
    glBindVertexArray(0);
    glDisableVertexAttribArray(TechniquePass.POSITION_ATTRIBUTE);
    glDisableVertexAttribArray(TechniquePass.COLOR_ATTRIBUTE);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    
    assert !Utils.hasGLErrors();
  }
  
  private void initBuffers(RenderManager rm) {
    assert !cleaned;
    if(linesBuffer < 0) {
      linesBuffer = glGenBuffers();
      glBindBuffer(GL_ARRAY_BUFFER, linesBuffer);
      glBufferData(GL_ARRAY_BUFFER, POS_COL_VERTEX_SIZE * 50, GL_DYNAMIC_DRAW);
      
      
      singleInstanceGlobalDataBuffer = glGenBuffers();
      glBindBuffer(GL_UNIFORM_BUFFER, singleInstanceGlobalDataBuffer);
      glBufferData(GL_UNIFORM_BUFFER, TechniquePass.BASIC_INSTANCE_UNIFORMS_BLOCK_SIZE, GL_DYNAMIC_DRAW);
      
      
      linesVAO = glGenVertexArrays();
      glBindVertexArray(linesVAO);

      glBindBuffer(GL_ARRAY_BUFFER, linesBuffer);

      glEnableVertexAttribArray(TechniquePass.POSITION_ATTRIBUTE);
      glEnableVertexAttribArray(TechniquePass.COLOR_ATTRIBUTE);

      glVertexAttribPointer(TechniquePass.POSITION_ATTRIBUTE, 3, GL_FLOAT, false, 6*Utils.FLOAT_SIZE, 0);
      glVertexAttribPointer(TechniquePass.COLOR_ATTRIBUTE, 3, GL_FLOAT, false, 6*Utils.FLOAT_SIZE, 3*Utils.FLOAT_SIZE);
      

      glBindVertexArray(0);
      glBindBuffer(GL_ARRAY_BUFFER, 0);
      glBindBuffer(GL_UNIFORM_BUFFER, 0);
      glDisableVertexAttribArray(TechniquePass.POSITION_ATTRIBUTE);
      glDisableVertexAttribArray(TechniquePass.COLOR_ATTRIBUTE);

      assert !Utils.hasGLErrors();
      

      instancesColorBuffer = glGenBuffers();
      glBindBuffer(GL_ARRAY_BUFFER, instancesColorBuffer);
      glBufferData(GL_ARRAY_BUFFER, rm.getMaxInstancesBasic() * 3 * Utils.FLOAT_SIZE, GL_DYNAMIC_DRAW);
      
      multipleInstancesGlobalDataBuffer = glGenBuffers();
      glBindBuffer(GL_UNIFORM_BUFFER, multipleInstancesGlobalDataBuffer);
      glBufferData(GL_UNIFORM_BUFFER, TechniquePass.BASIC_INSTANCE_UNIFORMS_BLOCK_SIZE * rm.getMaxInstancesBasic(), GL_DYNAMIC_DRAW);

      glBindBuffer(GL_ARRAY_BUFFER, 0);
      glBindBuffer(GL_UNIFORM_BUFFER, 0);

      assert !Utils.hasGLErrors();

      initSphereBuffers();
      initCrossesBuffers();
      initCirclesBuffers();
      initAxesBuffers();
      initBBsBuffers();
    }
  }
  
  private void renderArrays(int mode, int numElements, RenderManager rm) {
    debugMaterial.setUpUniforms(rm);

    Technique technique = debugMaterial.getEffect().getTechnique(TechniqueType.FORWARD, Quality.MID);
    for(TechniquePass pass: technique.passes) {
      pass.activate(rm);
      debugMaterial.setUpUniforms(pass, rm);

      glDrawArrays(mode, 0, numElements);
    }
    assert !Utils.hasGLErrors();
  }
  
  private void renderArraysInstanced(int mode, int numElements, int numInstances, RenderManager rm) {
    debugMaterial.setUpUniforms(rm);

    Technique technique = debugMaterial.getEffect().getTechnique(TechniqueType.FORWARD, Quality.MID);
    for(TechniquePass pass: technique.passes) {
      pass.activate(rm);
      debugMaterial.setUpUniforms(pass, rm);

      glDrawArraysInstanced(mode, 0, numElements, numInstances);
    }
    assert !Utils.hasGLErrors();
  }
  
  @SuppressWarnings("unused")
  private void renderElements(int mode, int numIndices, RenderManager rm) {
    debugMaterial.setUpUniforms(rm);

    Technique technique = debugMaterial.getEffect().getTechnique(TechniqueType.FORWARD, Quality.MID);
    for(TechniquePass pass: technique.passes) {
      pass.activate(rm);
      debugMaterial.setUpUniforms(pass, rm);

      glDrawElements(mode, numIndices, GL_UNSIGNED_SHORT, 0);
    }
    assert !Utils.hasGLErrors();
  }
  
  private void renderElementsInstanced(int mode, int numIndices, int numInstances, RenderManager rm) {
    debugMaterial.setUpUniforms(rm);

    Technique technique = debugMaterial.getEffect().getTechnique(TechniqueType.FORWARD, Quality.MID);
    for(TechniquePass pass: technique.passes) {
      pass.activate(rm);
      debugMaterial.setUpUniforms(pass, rm);

      glDrawElementsInstanced(mode, numIndices, GL_UNSIGNED_SHORT, 0, numInstances);
    }
    assert !Utils.hasGLErrors();
  }
  
  private void growBuffers() {
    int newCapacity = buffer1.capacity() / Utils.FLOAT_SIZE + POS_COL_VERTEX_SIZE * 50;
    buffer1 = BufferUtils.createFloatBuffer( newCapacity );
    buffer2 = BufferUtils.createFloatBuffer( newCapacity );
    buffer3 = BufferUtils.createFloatBuffer( newCapacity );
    buffer4 = BufferUtils.createFloatBuffer( newCapacity );
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
      buffer1.clear();
      buffer2.clear();

      
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
      
      if(linesToDraw1 + linesToDraw2 > 0) {
        setGlobalMatrixes(rm);
        glBindVertexArray(linesVAO);
      }
      
      if(linesToDraw1>0) {
        buffer1.flip();
      
        glEnable(GL_DEPTH_TEST);

        glBindBuffer(GL_ARRAY_BUFFER, linesBuffer);
        glBufferData(GL_ARRAY_BUFFER, buffer1, GL_DYNAMIC_DRAW);
      
        renderArrays(GL_LINES, linesToDraw1 * 2, rm);

        assert !Utils.hasGLErrors();
      }
      
      if(linesToDraw2>0) {
        buffer2.flip();
      
        glDisable(GL_DEPTH_TEST);

        glBindBuffer(GL_ARRAY_BUFFER, linesBuffer);
        glBufferData(GL_ARRAY_BUFFER, buffer2, GL_DYNAMIC_DRAW);
      
        renderArrays(GL_LINES, linesToDraw2 * 2, rm);

        assert !Utils.hasGLErrors();
      }
      
    } catch(BufferOverflowException e) {
      //fem creixer el buffer i rellancem el métode.
      //brut pq és una classe per fer debug.
      growBuffers();
      renderLines(rm);
    }
    
  }
  
  @Override
  protected void renderCrosses(RenderManager rm) {
    assert !cleaned;

    if(crosses.size() == 0)
      return;
    
    try {
      float[] color = new float[3];

      int crossesToDraw1 = 0;
      int crossesToDraw2 = 0;

      //per esferes amb ztest
      buffer1.clear(); // colors
      buffer2.clear(); // matrius
      
      //per esferes SENSE ztest
      buffer3.clear(); // colors
      buffer4.clear(); // matrius

      //////////////////////////////////////////////
      Vector3f v3Aux = new Vector3f();

      Matrix4f viewProj           = new Matrix4f();
      Matrix4f view               = new Matrix4f();
      Matrix4f model              = new Matrix4f();
      Matrix4f modelViewProj      = new Matrix4f();
      Matrix4f modelView          = new Matrix4f();
      Matrix4f modelViewInvTransp = new Matrix4f();
      viewProj.setIdentity();
      view .setIdentity();
      
      rm.getSceneData().getViewMatrix(view);
      rm.getSceneData().getViewProjectionMatrix(viewProj);
      ///////////////////////////////////////////////
      
      for(Cross cross: crosses) {
        FloatBuffer colorBuffer, matrixesBuffer;
        if(cross.depthEnabled) {
          colorBuffer = buffer1;
          matrixesBuffer = buffer2;
          crossesToDraw1++;
        } else {
          colorBuffer = buffer3;
          matrixesBuffer = buffer4;
          crossesToDraw2++;
        }
        
        color[0] = cross.color.x;
        color[1] = cross.color.y;
        color[2] = cross.color.z;
        
        colorBuffer.put(color);

        model.setIdentity();
        v3Aux.set(cross.center);
        model.setTranslation(v3Aux);
        model.setScale(cross.size);


        modelView.mul(view, model);
        modelViewProj.mul(viewProj, model);
        
        modelViewInvTransp.invert(modelView);
        modelViewInvTransp.transpose();

        Utils.matrixToBuffer(modelViewProj, matrixesBuffer);
        Utils.matrixToBuffer(modelView, matrixesBuffer);
        Utils.matrixToBuffer(modelViewInvTransp, matrixesBuffer);
      }

      
      if(crossesToDraw1 + crossesToDraw2 > 0) {
        glBindVertexArray(crossesVAO);

        glBindBufferBase( GL_UNIFORM_BUFFER, 
                          TechniquePass.BASIC_INSTANCE_UNIFORMS_BINDING, 
                          multipleInstancesGlobalDataBuffer);
      }
      
      if(crossesToDraw1>0) {
        buffer1.flip();
        buffer2.flip();
      
        glEnable(GL_DEPTH_TEST);

        glBindBuffer(GL_ARRAY_BUFFER, instancesColorBuffer);
        glBufferSubData(GL_ARRAY_BUFFER, 0, buffer1);
        
        glBindBuffer(GL_UNIFORM_BUFFER, multipleInstancesGlobalDataBuffer);
        glBufferSubData(GL_UNIFORM_BUFFER, 0, buffer2);
      
        renderArraysInstanced(GL_LINES, crossesNumVertexs, crossesToDraw1, rm);

        assert !Utils.hasGLErrors();
      }
      
      if(crossesToDraw2>0) {
        buffer3.flip();
        buffer4.flip();
      
        glDisable(GL_DEPTH_TEST);

        glBindBuffer(GL_ARRAY_BUFFER, instancesColorBuffer);
        glBufferSubData(GL_ARRAY_BUFFER, 0, buffer3);
        
        glBindBuffer(GL_UNIFORM_BUFFER, multipleInstancesGlobalDataBuffer);
        glBufferSubData(GL_UNIFORM_BUFFER, 0, buffer4);

      
        renderArraysInstanced(GL_LINES, crossesNumVertexs, crossesToDraw2, rm);

        assert !Utils.hasGLErrors();
      }
        
    } catch(BufferOverflowException e) {
      //fem creixer el buffer i rellancem el métode.
      //brut pq és una classe per fer debug.
      growBuffers();
      renderCrosses(rm);
    }
  }
  
  @Override
  protected void renderSpheres(RenderManager rm) {
    assert !cleaned;

    if(spheres.size() == 0)
      return;
    
    try {
      float[] color = new float[3];

      int spheresToDraw1 = 0;
      int spheresToDraw2 = 0;

      //per esferes amb ztest
      buffer1.clear(); // colors
      buffer2.clear(); // matrius
      
      //per esferes SENSE ztest
      buffer3.clear(); // colors
      buffer4.clear(); // matrius

      //////////////////////////////////////////////
      Vector3f v3Aux = new Vector3f();

      Matrix4f viewProj           = new Matrix4f();
      Matrix4f view               = new Matrix4f();
      Matrix4f model              = new Matrix4f();
      Matrix4f modelViewProj      = new Matrix4f();
      Matrix4f modelView          = new Matrix4f();
      Matrix4f modelViewInvTransp = new Matrix4f();
      viewProj.setIdentity();
      view .setIdentity();
      
      rm.getSceneData().getViewMatrix(view);
      rm.getSceneData().getViewProjectionMatrix(viewProj);
      ///////////////////////////////////////////////
      
      for(Sphere sphere: spheres) {
        FloatBuffer colorBuffer, matrixesBuffer;
        if(sphere.depthEnabled) {
          colorBuffer = buffer1;
          matrixesBuffer = buffer2;
          spheresToDraw1++;
        } else {
          colorBuffer = buffer3;
          matrixesBuffer = buffer4;
          spheresToDraw2++;
        }
        
        color[0] = sphere.color.x;
        color[1] = sphere.color.y;
        color[2] = sphere.color.z;
        
        colorBuffer.put(color);

        model.setIdentity();
        v3Aux.set(sphere.center);
        model.setTranslation(v3Aux);
        model.setScale(sphere.radius);


        modelView.mul(view, model);
        modelViewProj.mul(viewProj, model);
        
        modelViewInvTransp.invert(modelView);
        modelViewInvTransp.transpose();

        Utils.matrixToBuffer(modelViewProj, matrixesBuffer);
        Utils.matrixToBuffer(modelView, matrixesBuffer);
        Utils.matrixToBuffer(modelViewInvTransp, matrixesBuffer);
      }

      
      if(spheresToDraw1 + spheresToDraw2 > 0) {
        glBindVertexArray(sphereVAO);

        glBindBufferBase( GL_UNIFORM_BUFFER, 
                          TechniquePass.BASIC_INSTANCE_UNIFORMS_BINDING, 
                          multipleInstancesGlobalDataBuffer);
      }
      
      if(spheresToDraw1>0) {
        buffer1.flip();
        buffer2.flip();
      
        glEnable(GL_DEPTH_TEST);

        glBindBuffer(GL_ARRAY_BUFFER, instancesColorBuffer);
        glBufferSubData(GL_ARRAY_BUFFER, 0, buffer1);
        
        glBindBuffer(GL_UNIFORM_BUFFER, multipleInstancesGlobalDataBuffer);
        glBufferSubData(GL_UNIFORM_BUFFER, 0, buffer2);
      
        renderElementsInstanced(GL_LINE_STRIP, sphereNumIndices, spheresToDraw1, rm);

        assert !Utils.hasGLErrors();
      }
      
      if(spheresToDraw2>0) {
        buffer3.flip();
        buffer4.flip();
      
        glDisable(GL_DEPTH_TEST);

        glBindBuffer(GL_ARRAY_BUFFER, instancesColorBuffer);
        glBufferSubData(GL_ARRAY_BUFFER, 0, buffer3);
        
        glBindBuffer(GL_UNIFORM_BUFFER, multipleInstancesGlobalDataBuffer);
        glBufferSubData(GL_UNIFORM_BUFFER, 0, buffer4);

      
        renderElementsInstanced(GL_LINE_STRIP, sphereNumIndices, spheresToDraw2, rm);

        assert !Utils.hasGLErrors();
      }
        
    } catch(BufferOverflowException e) {
      //fem creixer el buffer i rellancem el métode.
      //brut pq és una classe per fer debug.
      growBuffers();
      renderSpheres(rm);
    }
  }
  
  @Override
  protected void renderCircles(RenderManager rm) {
    assert !cleaned;

    if(circles.size() == 0)
      return;
    
    try {
      float[] color = new float[3];

      int circlesToDraw1 = 0;
      int circlesToDraw2 = 0;

      //per esferes amb ztest
      buffer1.clear(); // colors
      buffer2.clear(); // matrius
      
      //per esferes SENSE ztest
      buffer3.clear(); // colors
      buffer4.clear(); // matrius
      

      Vector3f v3Aux = new Vector3f();
      Quat4f   qAux  = new Quat4f();

      //////////////////////////////////////////////

      Matrix4f viewProj           = new Matrix4f();
      Matrix4f view               = new Matrix4f();
      Matrix4f model              = new Matrix4f();
      Matrix4f modelViewProj      = new Matrix4f();
      Matrix4f modelView          = new Matrix4f();
      Matrix4f modelViewInvTransp = new Matrix4f();
      viewProj.setIdentity();
      view .setIdentity();
      
      rm.getSceneData().getViewMatrix(view);
      rm.getSceneData().getViewProjectionMatrix(viewProj);
      ///////////////////////////////////////////////
      
      for(Circle circle: circles) {
        FloatBuffer colorBuffer, matrixesBuffer;
        if(circle.depthEnabled) {
          colorBuffer = buffer1;
          matrixesBuffer = buffer2;
          circlesToDraw1++;
        } else {
          colorBuffer = buffer3;
          matrixesBuffer = buffer4;
          circlesToDraw2++;
        }
        
        color[0] = circle.color.x;
        color[1] = circle.color.y;
        color[2] = circle.color.z;
        
        colorBuffer.put(color);

        model.setIdentity();
        v3Aux.set(circle.center);
        model.setTranslation(v3Aux);
        model.setScale(circle.radius);

        v3Aux.set(0,0,1);
        Transformation.getClosestRotation(v3Aux, circle.planeNormal, qAux);
        model.setRotation(qAux);

        modelView.mul(view, model);
        modelViewProj.mul(viewProj, model);
        
        modelViewInvTransp.invert(modelView);
        modelViewInvTransp.transpose();

        Utils.matrixToBuffer(modelViewProj, matrixesBuffer);
        Utils.matrixToBuffer(modelView, matrixesBuffer);
        Utils.matrixToBuffer(modelViewInvTransp, matrixesBuffer);
      }

      
      if(circlesToDraw1 + circlesToDraw2 > 0) {
        glBindVertexArray(circlesVAO);

        glBindBufferBase( GL_UNIFORM_BUFFER, 
                          TechniquePass.BASIC_INSTANCE_UNIFORMS_BINDING, 
                          multipleInstancesGlobalDataBuffer);
      }
      
      if(circlesToDraw1>0) {
        buffer1.flip();
        buffer2.flip();
      
        glEnable(GL_DEPTH_TEST);
        assert !Utils.hasGLErrors();

        glBindBuffer(GL_ARRAY_BUFFER, instancesColorBuffer);
        glBufferSubData(GL_ARRAY_BUFFER, 0, buffer1);
        assert !Utils.hasGLErrors();
        
        glBindBuffer(GL_UNIFORM_BUFFER, multipleInstancesGlobalDataBuffer);
        glBufferSubData(GL_UNIFORM_BUFFER, 0, buffer2);
        assert !Utils.hasGLErrors();
      
        renderArraysInstanced(GL_LINE_LOOP, circlesNumVertexs, circlesToDraw1, rm);

        assert !Utils.hasGLErrors();
      }
      
      if(circlesToDraw2>0) {
        buffer3.flip();
        buffer4.flip();
      
        glDisable(GL_DEPTH_TEST);

        glBindBuffer(GL_ARRAY_BUFFER, instancesColorBuffer);
        glBufferSubData(GL_ARRAY_BUFFER, 0, buffer3);
        
        glBindBuffer(GL_UNIFORM_BUFFER, multipleInstancesGlobalDataBuffer);
        glBufferSubData(GL_UNIFORM_BUFFER, 0, buffer4);

      
        renderArraysInstanced(GL_LINE_LOOP, circlesNumVertexs, circlesToDraw2, rm);

        assert !Utils.hasGLErrors();
      }
        
    } catch(BufferOverflowException e) {
      //fem creixer el buffer i rellancem el métode.
      //brut pq és una classe per fer debug.
      growBuffers();
      renderCircles(rm);
    }
  }
  
  @Override
  protected void renderAxes(RenderManager rm) {
    assert !cleaned;

    if(axes.size() == 0)
      return;
    
    try {

      int axesToDraw1 = 0;
      int axesToDraw2 = 0;

      buffer1.clear();
      buffer2.clear();

      //////////////////////////////////////////////

      Matrix4f viewProj           = new Matrix4f();
      Matrix4f view               = new Matrix4f();
      Matrix4f model              = new Matrix4f();
      Matrix4f modelViewProj      = new Matrix4f();
      Matrix4f modelView          = new Matrix4f();
      Matrix4f modelViewInvTransp = new Matrix4f();
      viewProj.setIdentity();
      view .setIdentity();
      
      rm.getSceneData().getViewMatrix(view);
      rm.getSceneData().getViewProjectionMatrix(viewProj);
      ///////////////////////////////////////////////
      
      for(Axes axe: axes) {
        FloatBuffer matrixesBuffer;
        if(axe.depthEnabled) {
          matrixesBuffer = buffer1;
          axesToDraw1++;
        } else {
          matrixesBuffer = buffer2;
          axesToDraw2++;
        }

        model.set(axe.transformation);
        model.setScale(axe.size);

        modelView.mul(view, model);
        modelViewProj.mul(viewProj, model);
        
        modelViewInvTransp.invert(modelView);
        modelViewInvTransp.transpose();

        Utils.matrixToBuffer(modelViewProj, matrixesBuffer);
        Utils.matrixToBuffer(modelView, matrixesBuffer);
        Utils.matrixToBuffer(modelViewInvTransp, matrixesBuffer);
      }

      
      if(axesToDraw1 + axesToDraw2 > 0) {
        glBindVertexArray(axesVAO);

        glBindBufferBase( GL_UNIFORM_BUFFER, 
                          TechniquePass.BASIC_INSTANCE_UNIFORMS_BINDING, 
                          multipleInstancesGlobalDataBuffer);
      }
      
      if(axesToDraw1>0) {
        buffer1.flip();
      
        glEnable(GL_DEPTH_TEST);
        
        glBindBuffer(GL_UNIFORM_BUFFER, multipleInstancesGlobalDataBuffer);
        glBufferSubData(GL_UNIFORM_BUFFER, 0, buffer1);
        assert !Utils.hasGLErrors();
      
        renderArraysInstanced(GL_LINES, axesNumVertexs, axesToDraw1, rm);

        assert !Utils.hasGLErrors();
      }
      
      if(axesToDraw2>0) {
        buffer2.flip();
      
        glDisable(GL_DEPTH_TEST);
        
        glBindBuffer(GL_UNIFORM_BUFFER, multipleInstancesGlobalDataBuffer);
        glBufferSubData(GL_UNIFORM_BUFFER, 0, buffer1);
        assert !Utils.hasGLErrors();
      
        renderArraysInstanced(GL_LINES, axesNumVertexs, axesToDraw2, rm);

        assert !Utils.hasGLErrors();
      }
        
    } catch(BufferOverflowException e) {
      //fem creixer el buffer i rellancem el métode.
      //brut pq és una classe per fer debug.
      growBuffers();
      renderCircles(rm);
    }
  }
  
  @Override
  protected void renderTriangles(RenderManager rm) {
    assert !cleaned;
    

    if(triangles.size() == 0)
      return;
    
    try {      
      float[] vertex = new float[POS_COL_VERTEX_SIZE * 6];

      int trianglesToDraw1 = 0;
      int trianglesToDraw2 = 0;
      buffer1.clear();
      buffer2.clear();

      
      for(Triangle triangle: triangles) {

        ///////////////////////////////////////
        vertex[0] = triangle.v0.x;
        vertex[1] = triangle.v0.y;
        vertex[2] = triangle.v0.z;

        vertex[3] = triangle.color.x;
        vertex[4] = triangle.color.y;
        vertex[5] = triangle.color.z;
        
        vertex[6] = triangle.v1.x;
        vertex[7] = triangle.v1.y;
        vertex[8] = triangle.v1.z;

        vertex[9]  = triangle.color.x;
        vertex[10] = triangle.color.y;
        vertex[11] = triangle.color.z;
        
        
        ///////////////////////////////////////
        vertex[12] = triangle.v0.x;
        vertex[13] = triangle.v0.y;
        vertex[14] = triangle.v0.z;

        vertex[15] = triangle.color.x;
        vertex[16] = triangle.color.y;
        vertex[17] = triangle.color.z;
        
        vertex[18] = triangle.v2.x;
        vertex[19] = triangle.v2.y;
        vertex[20] = triangle.v2.z;

        vertex[21] = triangle.color.x;
        vertex[22] = triangle.color.y;
        vertex[23] = triangle.color.z;
        

        ///////////////////////////////////////
        vertex[24] = triangle.v2.x;
        vertex[25] = triangle.v2.y;
        vertex[26] = triangle.v2.z;

        vertex[27] = triangle.color.x;
        vertex[28] = triangle.color.y;
        vertex[29] = triangle.color.z;
        
        vertex[30] = triangle.v1.x;
        vertex[31] = triangle.v1.y;
        vertex[32] = triangle.v1.z;

        vertex[33] = triangle.color.x;
        vertex[34] = triangle.color.y;
        vertex[35] = triangle.color.z;

        ///////////////////////////////////////
        if(triangle.depthEnabled) {
          buffer1.put(vertex);
          trianglesToDraw1++;
        } else {
          buffer2.put(vertex);
          trianglesToDraw2++;
        }
      }
      
      if(trianglesToDraw1 + trianglesToDraw2 > 0) {
        setGlobalMatrixes(rm);
        glBindVertexArray(linesVAO);
      }
      
      if(trianglesToDraw1>0) {
        buffer1.flip();
      
        glEnable(GL_DEPTH_TEST);

        glBindBuffer(GL_ARRAY_BUFFER, linesBuffer);
        glBufferData(GL_ARRAY_BUFFER, buffer1, GL_DYNAMIC_DRAW);
      
        renderArrays(GL_LINES, trianglesToDraw1 * 6, rm);

        assert !Utils.hasGLErrors();
      }
      
      if(trianglesToDraw2>0) {
        buffer2.flip();
      
        glDisable(GL_DEPTH_TEST);

        glBindBuffer(GL_ARRAY_BUFFER, linesBuffer);
        glBufferData(GL_ARRAY_BUFFER, buffer2, GL_DYNAMIC_DRAW);
      
        renderArrays(GL_LINES, trianglesToDraw2 * 6, rm);

        assert !Utils.hasGLErrors();
      }
      
    } catch(BufferOverflowException e) {
      //fem creixer el buffer i rellancem el métode.
      //brut pq és una classe per fer debug.
      growBuffers();
      renderTriangles(rm);
    }
    
  }
  
  @Override
  protected void renderBBs(RenderManager rm) {
    assert !cleaned;

    if(aabbs.size() == 0)
      return;
    
    try {
      float[] color = new float[3];

      int bbToDraw1 = 0;
      int bbToDraw2 = 0;

      //per esferes amb ztest
      buffer1.clear(); // colors
      buffer2.clear(); // matrius
      
      //per esferes SENSE ztest
      buffer3.clear(); // colors
      buffer4.clear(); // matrius

      //////////////////////////////////////////////
      Vector3f v3Aux = new Vector3f();

      Matrix4f viewProj           = new Matrix4f();
      Matrix4f view               = new Matrix4f();
      Matrix4f model              = new Matrix4f();
      Matrix4f modelViewProj      = new Matrix4f();
      Matrix4f modelView          = new Matrix4f();
      Matrix4f modelViewInvTransp = new Matrix4f();
      viewProj.setIdentity();
      view .setIdentity();
      
      rm.getSceneData().getViewMatrix(view);
      rm.getSceneData().getViewProjectionMatrix(viewProj);
      ///////////////////////////////////////////////
      
      for(AABB aabb: aabbs) {
        FloatBuffer colorBuffer, matrixesBuffer;
        if(aabb.depthEnabled) {
          colorBuffer = buffer1;
          matrixesBuffer = buffer2;
          bbToDraw1++;
        } else {
          colorBuffer = buffer3;
          matrixesBuffer = buffer4;
          bbToDraw2++;
        }
        
        color[0] = aabb.color.x;
        color[1] = aabb.color.y;
        color[2] = aabb.color.z;
        
        colorBuffer.put(color);

        model.setIdentity();
        v3Aux.interpolate(aabb.minCoords, aabb.maxCoords, .5f);
        model.setTranslation(v3Aux);
        v3Aux.sub(aabb.maxCoords, aabb.minCoords);
        model.setM00(v3Aux.x);
        model.setM11(v3Aux.y);
        model.setM22(v3Aux.z);


        modelView.mul(view, model);
        modelViewProj.mul(viewProj, model);
        
        modelViewInvTransp.invert(modelView);
        modelViewInvTransp.transpose();

        Utils.matrixToBuffer(modelViewProj, matrixesBuffer);
        Utils.matrixToBuffer(modelView, matrixesBuffer);
        Utils.matrixToBuffer(modelViewInvTransp, matrixesBuffer);
      }
      
      for(OBB obb: obbs) {
        FloatBuffer colorBuffer, matrixesBuffer;
        if(obb.depthEnabled) {
          colorBuffer = buffer1;
          matrixesBuffer = buffer2;
          bbToDraw1++;
        } else {
          colorBuffer = buffer3;
          matrixesBuffer = buffer4;
          bbToDraw2++;
        }
        
        color[0] = obb.color.x;
        color[1] = obb.color.y;
        color[2] = obb.color.z;
        
        colorBuffer.put(color);

        model.set(obb.centerTransformation);
        model.setM00(obb.scaleXYZ.x * model.getM00());
        model.setM01(obb.scaleXYZ.y * model.getM01());
        model.setM02(obb.scaleXYZ.z * model.getM02());
        
        model.setM10(obb.scaleXYZ.x * model.getM10());
        model.setM11(obb.scaleXYZ.y * model.getM11());
        model.setM12(obb.scaleXYZ.z * model.getM12());
        
        model.setM20(obb.scaleXYZ.x * model.getM20());
        model.setM21(obb.scaleXYZ.y * model.getM21());
        model.setM22(obb.scaleXYZ.z * model.getM22());


        modelView.mul(view, model);
        modelViewProj.mul(viewProj, model);
        
        modelViewInvTransp.invert(modelView);
        modelViewInvTransp.transpose();

        Utils.matrixToBuffer(modelViewProj, matrixesBuffer);
        Utils.matrixToBuffer(modelView, matrixesBuffer);
        Utils.matrixToBuffer(modelViewInvTransp, matrixesBuffer);
      }

      
      if(bbToDraw1 + bbToDraw2 > 0) {
        glBindVertexArray(bbVAO);

        glBindBufferBase( GL_UNIFORM_BUFFER, 
                          TechniquePass.BASIC_INSTANCE_UNIFORMS_BINDING, 
                          multipleInstancesGlobalDataBuffer);
      }
      
      if(bbToDraw1>0) {
        buffer1.flip();
        buffer2.flip();
      
        glEnable(GL_DEPTH_TEST);

        glBindBuffer(GL_ARRAY_BUFFER, instancesColorBuffer);
        glBufferSubData(GL_ARRAY_BUFFER, 0, buffer1);
        
        glBindBuffer(GL_UNIFORM_BUFFER, multipleInstancesGlobalDataBuffer);
        glBufferSubData(GL_UNIFORM_BUFFER, 0, buffer2);
      
        renderElementsInstanced(GL_LINES, bbNumIndices, bbToDraw1, rm);

        assert !Utils.hasGLErrors();
      }
      
      if(bbToDraw2>0) {
        buffer3.flip();
        buffer4.flip();
      
        glDisable(GL_DEPTH_TEST);

        glBindBuffer(GL_ARRAY_BUFFER, instancesColorBuffer);
        glBufferSubData(GL_ARRAY_BUFFER, 0, buffer3);
        
        glBindBuffer(GL_UNIFORM_BUFFER, multipleInstancesGlobalDataBuffer);
        glBufferSubData(GL_UNIFORM_BUFFER, 0, buffer4);

      
        renderElementsInstanced(GL_LINES, bbNumIndices, bbToDraw2, rm);

        assert !Utils.hasGLErrors();
      }
        
    } catch(BufferOverflowException e) {
      //fem creixer el buffer i rellancem el métode.
      //brut pq és una classe per fer debug.
      growBuffers();
      renderSpheres(rm);
    }
  }
  
  private void setGlobalMatrixes(RenderManager rm) {
    SceneData sd = rm.getSceneData();
    
    Matrix4f mat = new Matrix4f();
    mat.setIdentity();

    globalDataBuffer.clear();
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
    globalDataBuffer.flip();
    glBindBuffer(GL_UNIFORM_BUFFER, singleInstanceGlobalDataBuffer);
    glBufferData(GL_UNIFORM_BUFFER, globalDataBuffer, GL_DYNAMIC_DRAW);
    glBindBufferBase( GL_UNIFORM_BUFFER, 
                      TechniquePass.BASIC_INSTANCE_UNIFORMS_BINDING, 
                      singleInstanceGlobalDataBuffer);
  }

  private boolean prevDepthMask;
  
  @Override
  protected void beginRender(RenderManager rm) {
    assert !cleaned;
    initBuffers(rm);
    prevDepthMask = glGetBoolean(GL_DEPTH_WRITEMASK);
    glDepthMask(false);
    
    setGlobalMatrixes(rm);
    

    assert !Utils.hasGLErrors();
  }

  @Override
  protected void endRender() {
    assert !cleaned;
    glDepthMask(prevDepthMask);

    glBindVertexArray(0);
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    glBindBuffer(GL_UNIFORM_BUFFER, 0);
    
    assert !Utils.hasGLErrors();
  }


  protected boolean cleaned = false;
  
  @Override
  public void finalize() {
    if(!cleaned) {
      if(linesBuffer > 0) {
        glDeleteBuffers(linesBuffer);
        glDeleteBuffers(singleInstanceGlobalDataBuffer);
        
        glDeleteBuffers(sphereIndexBuffer);
        glDeleteBuffers(sphereVertexBuffer);
        
        glDeleteBuffers(crossesVertexBuffer);
        
        glDeleteBuffers(circlesVertexBuffer);

        glDeleteBuffers(bbIndexBuffer);
        glDeleteBuffers(bbVertexBuffer);
        
        
        glDeleteBuffers(instancesColorBuffer);
        glDeleteBuffers(multipleInstancesGlobalDataBuffer);
        
        
        glDeleteVertexArrays(linesVAO);
        glDeleteVertexArrays(sphereVAO);
        glDeleteVertexArrays(crossesVAO);
        glDeleteVertexArrays(circlesVAO);
        glDeleteVertexArrays(bbVAO);
      }
      
      cleaned = true;
    }
  }
}
