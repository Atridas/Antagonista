from cat.atridas.antagonista.core import Core
from javax.vecmath import Point3f
from javax.vecmath import Color3f
from cat.atridas.antagonista import HashedString
        
inputManager = Core.getCore().getInputManager()
debugRender  = Core.getCore().getDebugRender()
        
def catacrocker():
        
  if inputManager.isActionActive(HashedString("shoot")):
    debugRender.addCross(Point3f(1.1,0.2,0), Color3f(1,1,1), 1, 0.5)
    print('catacrocker')