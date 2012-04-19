package cat.atridas.antagonista.defensa;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import cat.atridas.antagonista.HashedString;
import cat.atridas.antagonista.Transformation;
import cat.atridas.antagonista.entities.Entity;
import cat.atridas.antagonista.entities.EntityManager;
import cat.atridas.antagonista.entities.components.MeshComponent;
import cat.atridas.antagonista.entities.components.NavigableTerrainComponent;
import cat.atridas.antagonista.entities.components.RTSCameraComponent;
import cat.atridas.antagonista.entities.components.TransformComponent;
import cat.atridas.antagonista.entities.components.bullet.CharacterControllerComponent;
import cat.atridas.antagonista.entities.components.bullet.RigidBodyComponent;
import cat.atridas.antagonista.entities.components.bullet.RigidBodyComponent.PhysicType;
import cat.atridas.antagonista.graphics.MeshManager;
import cat.atridas.antagonista.graphics.RTSCamera;
import cat.atridas.antagonista.physics.PhysicShape;
import cat.atridas.antagonista.physics.bullet.PhysicShapeBullet;

public abstract class EntityFactory {

  public static Entity createRajola(EntityManager em, MeshManager mm,
      Transformation position, HashedString meshName) {
    Entity rajola = em.createEntity();

    TransformComponent tc = em.createComponent(rajola,
        TransformComponent.getComponentStaticType());
    tc.init(position);

    MeshComponent mc = em.createComponent(rajola,
        MeshComponent.getComponentStaticType());
    mc.init(meshName);

    RigidBodyComponent rbc = em.createComponent(rajola,
        RigidBodyComponent.getComponentStaticType());
    PhysicShapeBullet mesh = (PhysicShapeBullet) mm.getResource(meshName)
        .getPhysicsMesh();

    rbc.init(PhysicType.STATIC, mesh);

    NavigableTerrainComponent ntc = em.createComponent(rajola,
        NavigableTerrainComponent.getComponentStaticType());
    ntc.init();

    return rajola;
  }

  public static Entity createMur(EntityManager em, MeshManager mm,
      Transformation position, HashedString meshName) {
    Entity mur = em.createEntity();

    TransformComponent tc = em.createComponent(mur,
        TransformComponent.getComponentStaticType());
    tc.init(position);

    MeshComponent mc = em.createComponent(mur,
        MeshComponent.getComponentStaticType());
    mc.init(meshName);

    RigidBodyComponent rbc = em.createComponent(mur,
        RigidBodyComponent.getComponentStaticType());
    PhysicShapeBullet mesh = (PhysicShapeBullet) mm.getResource(meshName)
        .getPhysicsMesh();

    rbc.init(PhysicType.STATIC, mesh);

    return mur;
  }

  public static Entity createAltar(EntityManager em, MeshManager mm,
      Transformation position, HashedString meshName) {
    Entity mur = em.createEntity();

    TransformComponent tc = em.createComponent(mur,
        TransformComponent.getComponentStaticType());
    tc.init(position);

    MeshComponent mc = em.createComponent(mur,
        MeshComponent.getComponentStaticType());
    mc.init(meshName);

    RigidBodyComponent rbc = em.createComponent(mur,
        RigidBodyComponent.getComponentStaticType());
    PhysicShapeBullet mesh = (PhysicShapeBullet) mm.getResource(meshName)
        .getPhysicsMesh();

    rbc.init(PhysicType.STATIC, mesh);

    return mur;
  }

  public static Entity createMaster(EntityManager em, HashedString name) {
    Entity entityMaster = em.createEntity(name);

    TransformComponent tc = em.createComponent(entityMaster,
        TransformComponent.getComponentStaticType());

    Transformation transform = new Transformation();
    transform.setTranslation(new Vector3f(0, 0, 1.001f));
    tc.init(transform);

    MeshComponent mc = em.createComponent(entityMaster,
        MeshComponent.getComponentStaticType());
    mc.init(new HashedString("MasterTest"));

    CharacterControllerComponent ccc = em.createComponent(entityMaster,
        CharacterControllerComponent.getComponentStaticType());
    ccc.init(new Point3f(5, 5, 1), 1f, 2, .1f, 3f);

    return entityMaster;
  }

  public static void crearNivell(String config, MeshManager mm, EntityManager em) {
    HashedString terraMesh = new HashedString("TerraBasic");
    HashedString murMesh = new HashedString("ParetsBasic");
    Vector3f vecAux = new Vector3f();
    Transformation transAux = new Transformation();

    int x, y;
    String[] lines = config.split("\n");
    String[] coords = lines[0].split(" ");
    assert coords.length == 2;

    x = Integer.parseInt(coords[0]);
    y = Integer.parseInt(coords[1]);

    assert lines.length == y + 1;

    for (int j = 0; j < y; j++) {
      String line = lines[j + 1];
      for (int i = 0; i < x; i++) {
        char tipus = line.charAt(i);

        vecAux.set(i * 2 - x, j * 2 - y, 0);
        transAux.setTranslation(vecAux);

        switch (tipus) {
        case 'M':
          createMur(em, mm, transAux, murMesh);
          break;
        case 'R':
          createRajola(em, mm, transAux, terraMesh);
          break;
        default:
          assert false;
        }

      }
    }
  }

  public static Entity createCamera(EntityManager em, HashedString name) {

    Entity entityCamera = em.createEntity(name);

    RTSCameraComponent cc = em.createComponent(entityCamera,
        RTSCameraComponent.getComponentStaticType());
    RTSCamera camera = new RTSCamera();
    camera.setMaxDistance(30);
    camera.setDistance(20);
    camera.setPitch(60);
    cc.init(camera);

    return entityCamera;
  }
}
