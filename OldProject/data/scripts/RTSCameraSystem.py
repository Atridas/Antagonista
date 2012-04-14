from cat.atridas.antagonista import HashedString

from cat.atridas.antagonista.entities import System
from cat.atridas.antagonista.entities import SystemManager
from cat.atridas.antagonista.entities.components import CameraComponent
from cat.atridas.antagonista.entities.components import RTSCameraComponent

from java.util import ArrayList
from java.util import HashMap
from java.util import HashSet
from java.util import Collections

class RTSCameraSystem(System):

  def __init__(self):
    self.speed = 5;
    self.speedZoom = .01;
    
    self.m_systemID = HashedString("RTSCameraSystem")

    #private final static List<HashedString> usedComponents;
    #private final static List<HashedString> optionalComponents;
    #private final static Set<HashedString> writeToComponents;
    #private final static Set<HashedString> otherComponents;
    #private final static Set<HashedString> usedInterfaces;
    #private final static Set<HashedString> writeToInterfaces;
  
  
    components = ArrayList()
    components.add(RTSCameraComponent.getComponentStaticType())
    self.m_usedComponents = Collections.unmodifiableList(components)
    
    components = ArrayList()
    components.add(CameraComponent.getComponentStaticType())
    self.m_optionalComponents = Collections.unmodifiableList(components)
    
    writes =  HashSet()
    writes.add(RTSCameraComponent.getComponentStaticType())
    writes.add(CameraComponent.getComponentStaticType())
    
    self.m_writeToComponents = Collections.unmodifiableSet(writes)
    
    self.m_otherComponents = Collections.emptySet()

    interfaces = HashSet()
    interfaces.add(SystemManager.inputInteface)
    self.m_usedInterfaces = Collections.unmodifiableSet(interfaces)
    self.m_writeToInterfaces = Collections.unmodifiableSet(HashSet(self.m_usedInterfaces))
  

  # @Override
  
  def addEntity(self, entity, components, currentTime):
    
    assert SystemManager.assertSystemInputParameters(entity,  components, self)

    camera = components[0]
    
    cc = ENTITY_MANAGER.createComponent(entity, CameraComponent.getComponentStaticType())
    
    cc.init(camera.getCamera())

  # @Override
  def updateEntity(self, entity, components, currentTime):

    assert SystemManager.assertSystemInputParameters(entity,  components, self)

    rtsCameraComponent    = components[0]
    cameraComponent       = components[1]
    
    # assert cameraComponent != null;
    
    if rtsCameraComponent.isActive():
      rtsCamera = rtsCameraComponent.getCamera();
      
      if INPUT_MANAGER.isActionActive(CONST_CAM_UP):
        rtsCamera.moveUp(self.speed * currentTime.dt)
      
      if INPUT_MANAGER.isActionActive(CONST_CAM_DOWN):
        rtsCamera.moveUp(-self.speed * currentTime.dt)
      
      if INPUT_MANAGER.isActionActive(CONST_CAM_RIGHT):
        rtsCamera.moveRight(self.speed * currentTime.dt)
      
      if INPUT_MANAGER.isActionActive(CONST_CAM_LEFT):
        rtsCamera.moveRight(-self.speed * currentTime.dt)
      
      if INPUT_MANAGER.isActionActive(CONST_CAM_DISTANCE):
        rtsCamera.addDistance( -self.speedZoom * INPUT_MANAGER.getActionValue(CONST_CAM_DISTANCE) )
      
      
      cameraComponent.setActive(True)
    else:
      cameraComponent.setActive(False)
    

  # @Override
  def deleteEntity(self, entity, currentTime):
    #TODO Core.getCore().getEntityManager().deleteComponent
    return
  
  

  

  def getSystemId(self):
    return self.m_systemID
  
  
  def getUsedComponents(self):
    return self.m_usedComponents
  
  
  def getOptionalComponents(self):
    return self.m_optionalComponents
  

  def getWriteToComponents(self):
    return self.m_writeToComponents
  

  def getUsedInterfaces(self):
    return self.m_usedInterfaces
  

  def getWriteToInterfaces(self):
    return self.m_writeToInterfaces
  

  def getOtherReadComponents(self):
    return self.m_otherComponents
  
