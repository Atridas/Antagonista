from javax.vecmath import Point3f
from javax.vecmath import Color3f
from cat.atridas.antagonista import HashedString
        
def catacrocker():
        
  if INPUT_MANAGER.isActionActive(HashedString("shoot")):
    DEBUG_RENDER.addCross(Point3f(1.1,0.2,0), Color3f(1,1,1), 1, 0.5)
    print('catacrocker')