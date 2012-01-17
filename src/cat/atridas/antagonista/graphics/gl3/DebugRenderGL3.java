package cat.atridas.antagonista.graphics.gl3;


import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL33.*;

import java.nio.BufferOverflowException;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

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
  private int shpereNumIndices;
  
  //creus
  private int crossesVertexBuffer = -1, crossesVAO = -1;
  private static final int crossesNumVertexs = 6;
  
  //cercles
  private int circlesVertexBuffer = -1, circlesVAO = -1;
  private int circlesNumVertexs;
  
  //axes
  private int axesVertexBuffer = -1, axesVAO = -1;
  private static final int axesNumVertexs = 6;
  
  //triangles ho farem com les línees
  
  //BBs
  private int bbIndexBuffer = -1, bbVertexBuffer = -1, bbVAO = -1;
  private int bbNumIndices;
  
  private static final int POS_COL_VERTEX_SIZE = (3 + 3); //Floats
  
  {
    buffer1 = BufferUtils.createFloatBuffer(POS_COL_VERTEX_SIZE * 50);
    buffer2 = BufferUtils.createFloatBuffer(POS_COL_VERTEX_SIZE * 50);
    buffer3 = BufferUtils.createFloatBuffer(POS_COL_VERTEX_SIZE * 50);
    buffer4 = BufferUtils.createFloatBuffer(POS_COL_VERTEX_SIZE * 50);
  }
  
  private void initSphereBuffers() {
    assert !cleaned;
    ArrayList<Float> vertices = new ArrayList<>();

    vertices.add( 0.f);
    vertices.add( 0.f);
    vertices.add( 1.f); //top
    
    vertices.add( 0.f);
    vertices.add( 0.f);
    vertices.add(-1.f); //botom
    
    for(int i = 1; i < SPHERE_STACKS; i++) {
      float z = i / (SPHERE_STACKS/2.f) - 1;
      for(int j = 0; j < SPHERE_SUBDIV; ++j) {
        float len = (float)Math.sqrt(1 - z*z);
        float x = (float) Math.sin( j * Math.PI * 2 / SPHERE_SUBDIV) * len;
        float y = (float) Math.cos( j * Math.PI * 2 / SPHERE_SUBDIV) * len;
        

        vertices.add(x);
        vertices.add(y);
        vertices.add(z);
      }
    }
    //////////////////////////////////////////////////////////////
    Float faux1[] = vertices.toArray(new Float[vertices.size()]);
    float faux2[] = new float[faux1.length];
    for(int i = 0; i < faux1.length; i++) {
      faux2[i] = faux1[i];
    }
    faux1 = null;
    vertices = null;
    //////////////////////////////////////////////////////////////
    
    ArrayList<Short> indexes = new ArrayList<>();
    
    //part de sota
    indexes.add((short)1);
    for(short j = 0; j < SPHERE_SUBDIV - 1; j += 2) {
      indexes.add( (short)( 2 + j    ) );
      indexes.add( (short)( 2 + j +1 ) );
      indexes.add((short)1);
    }
    short baseStack;
    
    //paral·lels
    for(short i = 1; i < SPHERE_STACKS; i++) {
      baseStack = (short)( (i-1) * SPHERE_SUBDIV + 2 );
      for(short j = 0; j < SPHERE_SUBDIV -1; ++j) {
        indexes.add( (short)( baseStack + j    ) );
      }
      indexes.add( (short)( baseStack ) );
    }


    //part de sobre
    baseStack = (short)( (SPHERE_STACKS-2) * SPHERE_SUBDIV + 2 );
    indexes.add((short)0);
    for(short j = 0; j < SPHERE_SUBDIV - 1; j += 2) {
      indexes.add( (short)( baseStack + j    ) );
      indexes.add( (short)( baseStack + j +1 ) );
      indexes.add((short)0);
    }
    
    //meridians
    for(short j = 1; j < SPHERE_SUBDIV; ++j) {
      if(j % 2 == 1) {
        //de dalt a baix
        //indexes.add( (short)( 1 ) );
        for(short i = SPHERE_STACKS-1; i > 0; i--) {
          baseStack = (short)( (i-1) * SPHERE_SUBDIV + 2 );
          indexes.add( (short)( baseStack + j    ) );
        }
        indexes.add( (short)( 1 ) );
      } else {
        // de baix a dalt
        //indexes.add( (short)( 0 ) );
        for(short i = 1; i < SPHERE_STACKS; i++) {
          baseStack = (short)( (i-1) * SPHERE_SUBDIV + 2 );
          indexes.add( (short)( baseStack + j    ) );
        }
        indexes.add( (short)( 0 ) );
      }
    }
    
    //////////////////////////////////////////////////////////////
    Short saux1[] = indexes.toArray(new Short[indexes.size()]);
    short saux2[] = new short[saux1.length];
    for(int i = 0; i < saux1.length; i++) {
      saux2[i] = saux1[i];
    }
    saux1 = null;
    indexes = null;
    //////////////////////////////////////////////////////////////

    sphereVAO = glGenVertexArrays();
    glBindVertexArray(sphereVAO);
    
    sphereVertexBuffer = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, sphereVertexBuffer);
    FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(faux2.length);
    vertexBuffer.put(faux2);
    vertexBuffer.flip();
    glBufferData(GL_ARRAY_BUFFER,vertexBuffer, GL_STATIC_DRAW);
    
    sphereIndexBuffer = glGenBuffers();
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, sphereIndexBuffer);
    ShortBuffer indexBuffer = BufferUtils.createShortBuffer(saux2.length);
    shpereNumIndices = saux2.length;
    indexBuffer.put(saux2);
    indexBuffer.flip();
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
    


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
    ArrayList<Float> vertices = new ArrayList<>();

    vertices.add(1.f);
    vertices.add(0.f);
    vertices.add(0.f);
    
    vertices.add(-1.f);
    vertices.add(0.f);
    vertices.add(0.f);

    
    vertices.add(0.f);
    vertices.add(1.f);
    vertices.add(0.f);
    
    vertices.add(0.f);
    vertices.add(-1.f);
    vertices.add(0.f);

    
    vertices.add(0.f);
    vertices.add(0.f);
    vertices.add(1.f);
    
    vertices.add(0.f);
    vertices.add(0.f);
    vertices.add(-1.f);
    //////////////////////////////////////////////////////////////
    Float faux1[] = vertices.toArray(new Float[vertices.size()]);
    float faux2[] = new float[faux1.length];
    for(int i = 0; i < faux1.length; i++) {
      faux2[i] = faux1[i];
    }
    faux1 = null;
    vertices = null;
    //////////////////////////////////////////////////////////////
    

    crossesVAO = glGenVertexArrays();
    glBindVertexArray(crossesVAO);
    
    crossesVertexBuffer = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, crossesVertexBuffer);
    FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(faux2.length);
    vertexBuffer.put(faux2);
    vertexBuffer.flip();
    glBufferData(GL_ARRAY_BUFFER,vertexBuffer, GL_STATIC_DRAW);
    


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
    ArrayList<Float> vertices = new ArrayList<>();

    vertices.add( 1.f);
    vertices.add(-1.f);
    vertices.add( 1.f);

    vertices.add( 1.f);
    vertices.add( 1.f);
    vertices.add( 1.f);

    vertices.add(-1.f);
    vertices.add( 1.f);
    vertices.add( 1.f);

    vertices.add(-1.f);
    vertices.add(-1.f);
    vertices.add( 1.f);

    vertices.add( 1.f);
    vertices.add(-1.f);
    vertices.add(-1.f);

    vertices.add( 1.f);
    vertices.add( 1.f);
    vertices.add(-1.f);

    vertices.add(-1.f);
    vertices.add( 1.f);
    vertices.add(-1.f);

    vertices.add(-1.f);
    vertices.add(-1.f);
    vertices.add(-1.f);
    //////////////////////////////////////////////////////////////
    Float faux1[] = vertices.toArray(new Float[vertices.size()]);
    float faux2[] = new float[faux1.length];
    for(int i = 0; i < faux1.length; i++) {
      faux2[i] = faux1[i];
    }
    faux1 = null;
    vertices = null;
    //////////////////////////////////////////////////////////////
    ArrayList<Short> indexes = new ArrayList<>();

    //bot
    indexes.add((short) 0);
    indexes.add((short) 1);

    indexes.add((short) 1);
    indexes.add((short) 2);

    indexes.add((short) 2);
    indexes.add((short) 3);

    indexes.add((short) 3);
    indexes.add((short) 0);

    //top
    indexes.add((short) 4);
    indexes.add((short) 5);

    indexes.add((short) 5);
    indexes.add((short) 6);

    indexes.add((short) 6);
    indexes.add((short) 7);

    indexes.add((short) 7);
    indexes.add((short) 4);

    //up
    indexes.add((short) 0);
    indexes.add((short) 4);

    indexes.add((short) 1);
    indexes.add((short) 5);

    indexes.add((short) 2);
    indexes.add((short) 6);

    indexes.add((short) 3);
    indexes.add((short) 7);
    
    //////////////////////////////////////////////////////////////
    Short saux1[] = indexes.toArray(new Short[indexes.size()]);
    short saux2[] = new short[saux1.length];
    for(int i = 0; i < saux1.length; i++) {
      saux2[i] = saux1[i];
    }
    saux1 = null;
    indexes = null;
    //////////////////////////////////////////////////////////////

    bbVAO = glGenVertexArrays();
    glBindVertexArray(bbVAO);
    
    bbVertexBuffer = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, bbVertexBuffer);
    FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(faux2.length);
    vertexBuffer.put(faux2);
    vertexBuffer.flip();
    glBufferData(GL_ARRAY_BUFFER,vertexBuffer, GL_STATIC_DRAW);
    
    bbIndexBuffer = glGenBuffers();
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bbIndexBuffer);
    ShortBuffer indexBuffer = BufferUtils.createShortBuffer(saux2.length);
    bbNumIndices = saux2.length;
    indexBuffer.put(saux2);
    indexBuffer.flip();
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);
    


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
    ArrayList<Float> vertices = new ArrayList<>();

    for(int j = 0; j < SPHERE_SUBDIV; ++j) {
      float x = (float) Math.sin( j * Math.PI * 2 / SPHERE_SUBDIV);
      float y = (float) Math.cos( j * Math.PI * 2 / SPHERE_SUBDIV);
      

      vertices.add(x);
      vertices.add(y);
      vertices.add(0.f);
    }
    //////////////////////////////////////////////////////////////
    Float faux1[] = vertices.toArray(new Float[vertices.size()]);
    circlesNumVertexs = faux1.length / 3;
    float faux2[] = new float[faux1.length];
    for(int i = 0; i < faux1.length; i++) {
      faux2[i] = faux1[i];
    }
    faux1 = null;
    vertices = null;
    //////////////////////////////////////////////////////////////
    

    circlesVAO = glGenVertexArrays();
    glBindVertexArray(circlesVAO);
    
    circlesVertexBuffer = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, circlesVertexBuffer);
    FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(faux2.length);
    vertexBuffer.put(faux2);
    vertexBuffer.flip();
    glBufferData(GL_ARRAY_BUFFER,vertexBuffer, GL_STATIC_DRAW);
    


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
    ArrayList<Float> vertices = new ArrayList<>();

    vertices.add(0.f);////////////////////////
    vertices.add(0.f);
    vertices.add(0.f);
    
    vertices.add(1.f); //color
    vertices.add(0.f);
    vertices.add(0.f);

    vertices.add(1.f);
    vertices.add(0.f);
    vertices.add(0.f);
    
    vertices.add(1.f); //color
    vertices.add(0.f);
    vertices.add(0.f);

    vertices.add(0.f);////////////////////////
    vertices.add(0.f);
    vertices.add(0.f);
    
    vertices.add(0.f); //color
    vertices.add(1.f);
    vertices.add(0.f);

    vertices.add(0.f);
    vertices.add(1.f);
    vertices.add(0.f);
    
    vertices.add(0.f); //color
    vertices.add(1.f);
    vertices.add(0.f);

    vertices.add(0.f);////////////////////////
    vertices.add(0.f);
    vertices.add(0.f);
    
    vertices.add(0.f); //color
    vertices.add(0.f);
    vertices.add(1.f);

    vertices.add(0.f);
    vertices.add(0.f);
    vertices.add(1.f);
    
    vertices.add(0.f); //color
    vertices.add(0.f);
    vertices.add(1.f);
    
    //////////////////////////////////////////////////////////////
    Float faux1[] = vertices.toArray(new Float[vertices.size()]);
    circlesNumVertexs = faux1.length / 3;
    float faux2[] = new float[faux1.length];
    for(int i = 0; i < faux1.length; i++) {
      faux2[i] = faux1[i];
    }
    faux1 = null;
    vertices = null;
    //////////////////////////////////////////////////////////////
    

    axesVAO = glGenVertexArrays();
    glBindVertexArray(axesVAO);
    
    axesVertexBuffer = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, axesVertexBuffer);
    FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(faux2.length);
    vertexBuffer.put(faux2);
    vertexBuffer.flip();
    glBufferData(GL_ARRAY_BUFFER,vertexBuffer, GL_STATIC_DRAW);
    


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
      
        renderElementsInstanced(GL_LINE_STRIP, shpereNumIndices, spheresToDraw1, rm);

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

      
        renderElementsInstanced(GL_LINE_STRIP, shpereNumIndices, spheresToDraw2, rm);

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
  
  @Override
  protected void renderStrings(RenderManager rm) {
    assert !cleaned;
    //TODO
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
