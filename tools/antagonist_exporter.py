import bpy, math

epsilon = 0#1e-05

class AntagonistVertex:
    def __init__(self, vertex):
        self.x = vertex.co[0]
        self.y = vertex.co[1]
        self.z = vertex.co[2]
        
        self.nx = 0
        self.ny = 0
        self.nz = 0
        
        self.tx = 0
        self.ty = 0
        self.tz = 0
        
        self.bx = 0
        self.by = 0
        self.bz = 0
        
        self.u = 0
        self.v = 0
        
        
        
        weights = []
        w_indices = []
        
        for group in vertex.groups:
          weights.append(group.weight)
          w_indices.append(group.group)
        
        while len(weights) < 4:
          weights.append(0)
          w_indices.append(0)
        
        weight_acum = 0
        
        for i in range(4):
          weight_acum = weight_acum + weights[i]
          
        if weight_acum == 0:
          self.animated = False
          self.weights  = [0,0,0,0]
          self.w_indices = [0,0,0,0]
        else:
          self.animated = True
          self.weights  = []
          self.w_indices = []
          for i in range(4):
            self.weights.append(weights[i] / weight_acum)
            self.w_indices.append(w_indices[i])
          
        
    def __str__(self):
        aux =    "vertex pos:"    + str(self.x ) + ", " + str(self.y ) + ", " + str(self.z )
        aux = aux + " normal:"    + str(self.nx) + ", " + str(self.ny) + ", " + str(self.nz)
        aux = aux + " tangent:"   + str(self.tx) + ", " + str(self.ty) + ", " + str(self.tz)
        aux = aux + " bitangent:" + str(self.bx) + ", " + str(self.by) + ", " + str(self.bz)
        aux = aux +     " uv:"    + str(self.u ) + ", " + str(self.v)
        if self.animated:
          aux = aux + " weights: " + str(self.weights) + " indices: " + str(self.w_indices)
        return aux

    def cmp_pesos(self,other):
        if self.weights[0] == other.weights[0]:
          if self.weights[1] == other.weights[1]:
            if self.weights[2] == other.weights[2]:
              if self.weights[3] == other.weights[3]:
                if self.w_indices[0] == other.w_indices[0]:
                  if self.w_indices[1] == other.w_indices[1]:
                    if self.w_indices[2] == other.w_indices[2]:
                      if self.w_indices[3] == other.w_indices[3]:
                        return 0
                      else:
                        return self.w_indices[3] - other.w_indices[3]
                    else:
                      return self.w_indices[2] - other.w_indices[2]
                  else:
                    return self.w_indices[1] - other.w_indices[1]
                else:
                  return self.w_indices[0] - other.w_indices[0]
              else:
                return self.weights[3] - other.weights[3]
            else:
              return self.weights[2] - other.weights[2]
          else:
            return self.weights[1] - other.weights[1]
        else:
          return self.weights[0] - other.weights[0]
        
    def __cmp__(self, other):
        if self.x == other.x:
            if self.y == other.y:
                if self.z == other.z:
                    if self.nx == other.nx:
                        if self.ny == other.ny:
                            if self.nz == other.nz:
                                if self.u == other.u:
                                    if self.v == other.v:
                                        if self.animated and other.animated:
                                          return self.cmp_pesos(other)
                                        elif self.animated:
                                          return 1
                                        elif other.animated:
                                          return -1
                                        else:
                                          return 0
                                    else:
                                        return self.v - other.v
                                else:
                                    return self.u - other.u
                            else:
                                return self.nz - other.nz
                        else:
                            return self.ny - other.ny
                    else:
                        return self.nx - other.nx
                else:
                    return self.z - other.z
            else:
                return self.y - other.y
        else:
            return self.x - other.x

    def __lt__(self, other):
        return self.__cmp__(other) < 0

    def __gt__(self, other):
        return self.__cmp__(other) > 0

    def __le__(self, other):
        return self.__cmp__(other) <= 0

    def __ge__(self, other):
        return self.__cmp__(other) >= 0

    def __eq__(self, other):
        return self.__cmp__(other) == 0

    def __ne__(self, other):
        return self.__cmp__(other) != 0

    def __hash__(self):
        h1 = hash(self.x) ^ hash(self.y) ^ hash(self.z) ^ hash(self.nx) ^ hash(self.ny) ^ hash(self.nz) ^ hash(self.u) ^ hash(self.v)
        
        if self.animated:
          h2 = hash(self.weights[0]) ^ hash(self.weights[1]) ^ hash(self.weights[2]) ^ hash(self.weights[3])
          h3 = hash(self.w_indices[0]) ^ hash(self.w_indices[1]) ^ hash(self.w_indices[2]) ^ hash(self.w_indices[3])
          h1 = h1 ^ h2 ^ h3
          
        return h1


def calcTan(u1, u2, u3, p1, p2, p3):
    if u2 - u1 == 0: #fem al voltant del vertex 3
        a = (p2 - p3).scalarMul(1 / (u2 - u3))
        b = (p1 - p3).scalarMul(1 / (u1 - u3))
    elif u3 - u1 == 0: #fem al voltant del vertex 2
        a = (p3 - p2).scalarMul(1 / (u3 - u2))
        b = (p1 - p2).scalarMul(1 / (u1 - u2))
    else: #fem al voltant del vertex 1
        a = (p2 - p1).scalarMul(1 / (u2 - u1))
        b = (p3 - p1).scalarMul(1 / (u3 - u1))
    
    c = b - a
    d = c.scalarMul(-c.dot( a))
    e = a + d
    e = e.scalarMul( 1 / math.sqrt( e.dot(e) ))
    return e
    
        
    
class AntagonistFace:
    def __init__(self, mesh, vertices, v1, v2, v3, uv1, uv2, uv3, n, material):
        self.material = material
        self.i1 = v1 #indexos
        self.i2 = v2
        self.i3 = v3
        self.v1 = AntagonistVertex(mesh.vertices[v1]) #vertexos amb valors
        self.v2 = AntagonistVertex(mesh.vertices[v2])
        self.v3 = AntagonistVertex(mesh.vertices[v3])
        self.nx = n[0] # normal de la cara
        self.ny = n[1]
        self.nz = n[2]
        self.v1.u = uv1[0] # coordenades de textura
        self.v1.v = uv1[1]
        self.v2.u = uv2[0]
        self.v2.v = uv2[1]
        self.v3.u = uv3[0]
        self.v3.v = uv3[1]
        
        #calculs de la tangent i bitangent a la cara
        
        p1 = VectorAux(self.v1.x, self.v1.y, self.v1.z)
        p2 = VectorAux(self.v2.x, self.v2.y, self.v2.z)
        p3 = VectorAux(self.v3.x, self.v3.y, self.v3.z)
        
        tangent   = calcTan(self.v1.u, self.v2.u, self.v3.u, p1, p2, p3)
        bitangent = calcTan(self.v1.v, self.v2.v, self.v3.v, p1, p2, p3)
        
        self.tx = tangent.x
        self.ty = tangent.y
        self.tz = tangent.z
        
        self.bx = bitangent.x
        self.by = bitangent.y
        self.bz = bitangent.z
        
        #afegim la cara a la llista de vertexos->cares (ens ajudar`a a calcular normals/tangents/bitangents de cara
        if not v1 in vertices:
            vertices[v1] = []
        vertices[v1].append((self, self.v1))
        if not v2 in vertices:
            vertices[v2] = []
        vertices[v2].append((self, self.v2))
        if not v3 in vertices:
            vertices[v3] = []
        vertices[v3].append((self, self.v3))
        
    def __str__(self):
        aux =    "face vertexs:" + str(self.i1) + ", " + str(self.i2) + ", " + str(self.i3)
        aux = aux + " normal:" + str(self.nx) + ", " + str(self.ny) + ", " + str(self.nz)
        aux = aux + " tangent:" + str(self.tx) + ", " + str(self.ty) + ", " + str(self.tz)
        aux = aux + " bitangent:" + str(self.bx) + ", " + str(self.by) + ", " + str(self.bz)
        aux = aux + " material:" + str(self.material)
        return aux

class VectorAux:
    def __init__(self, x, y, z):
        self.x = x
        self.y = y
        self.z = z
        
    def __add__(self, other):
        x = self.x + other.x
        y = self.y + other.y
        z = self.z + other.z
        return VectorAux(x, y, z)
        
    def __sub__(self, other):
        x = self.x - other.x
        y = self.y - other.y
        z = self.z - other.z
        return VectorAux(x, y, z)
        
    def scalarMul(self, scalar):
        x = self.x * scalar
        y = self.y * scalar
        z = self.z * scalar
        return VectorAux(x, y, z)

    def dot(self, other):
        return self.x * other.x + self.y * other.y + self.z * other.z


class NormalAux:
    def __init__(self):
        self.nx = 0
        self.ny = 0
        self.nz = 0
        
    def add(self, other):
        self.nx = self.nx + other.nx
        self.ny = self.ny + other.ny
        self.nz = self.nz + other.nz
        
    def addT(self, other):
        self.nx = self.nx + other.tx
        self.ny = self.ny + other.ty
        self.nz = self.nz + other.tz
        
    def addB(self, other):
        self.nx = self.nx + other.bx
        self.ny = self.ny + other.by
        self.nz = self.nz + other.bz
        
    def normalize(self):
        ab = self.nx * self.nx + self.ny * self.ny + self.nz * self.nz
        ab = math.sqrt(ab)
        self.nx = self.nx / ab
        self.ny = self.ny / ab
        self.nz = self.nz / ab
        
    def angle(self, other):
        c = self.nx * other.nx + self.ny * other.ny + self.nz * other.nz
        
        #print(str(self.nx) + ", " + str(self.ny) + ", " + str(self.nz))
        #print(str(other.nx) + ", " + str(other.ny) + ", " + str(other.nz))
        if c > 1:
            c = 1
        elif c < -1:
            c = -1
        
        return math.acos(c)

class AntagonistMesh:
    def __init__(self, materialFaces, vertices, vertex_groups):
        self.materialFaces = materialFaces
        self.vertices = vertices
        
        self.bones = []
        
        for vertex_group in vertex_groups:
          self.bones.append(vertex_group.name)
        
        self.animated = False
        
        for v in vertices:
          if v.animated:
            self.animated = True
            break

        new_vertex_map = {}
        new_vertex_array = []
        
        for material in self.materialFaces.keys():
          self.materialFaces[material] = self.OptimizeFaces(self.materialFaces[material])
        
          for face in self.materialFaces[material]:
            v1 = face.i1
            if v1 in new_vertex_map.keys():
              face.i1 = new_vertex_map[v1]
            else:
              new_index = len(new_vertex_map.keys())
              old_index = face.i1
              new_vertex_map[old_index] = new_index
              new_vertex_array.append( self.vertices[old_index] )
              face.i1 = new_index
              
            v2 = face.i2
            if v2 in new_vertex_map.keys():
              face.i2 = new_vertex_map[v2]
            else:
              new_index = len(new_vertex_map.keys())
              old_index = face.i2
              new_vertex_map[old_index] = new_index
              new_vertex_array.append( self.vertices[old_index] )
              face.i2 = new_index
              
            v3 = face.i3
            if v3 in new_vertex_map.keys():
              face.i3 = new_vertex_map[v3]
            else:
              new_index = len(new_vertex_map.keys())
              old_index = face.i3
              new_vertex_map[old_index] = new_index
              new_vertex_array.append( self.vertices[old_index] )
              face.i3 = new_index
        
        self.vertices = new_vertex_array
        
    def OptimizeFaces(self, input_faces):
        CACHE_DECAY_POWER = 1.5
        LAST_TRI_SCORE = 0.75
        VALENCE_BOOST_SCALE = 2.0
        VALENCE_BOOST_POWER = 0.5
        MAX_SIZE_VERTEX_CACHE = 32
        
        class Triangle:
          #added s'ha afegit ja a la llista de triangles a renderitzar o no.
          
          #score
          
          #vertices: llista de 3 indexos
          
          def __init__(self, v1, v2, v3):
            self.added = False
            self.vertices = [v1, v2, v3]
          
        class Vertex:
          #unused_triangles: triangles que encara l'utilitzen i no s'han posat a la 
          #                  llista de renderització
          
          #cache_pos: posició a la cache
          
          #score: puntuació per ser renderitzat
          
          #triangles: llista d'indexos als triangles que utilitzen aquest vertex
          
          def __init__(self):
            self.unused_triangles = 0
            self.cache_pos = -1
            self.triangles = []
        
        def FindVertexScore(vertex):
          if vertex.unused_triangles == 0:
            return -1 #no triangle needs this vertex
          score = 0
          cache_position = vertex.cache_pos
          if cache_position >= 0:
            if cache_position < 3:
              score = LAST_TRI_SCORE
            else:
              scaler = 1.0 / (MAX_SIZE_VERTEX_CACHE - 3)
              score = 1 - (cache_position - 3) * scaler
              score = score ** CACHE_DECAY_POWER
          
          valence_boost = vertex.unused_triangles  ** (-VALENCE_BOOST_POWER)
          score = score + valence_boost * VALENCE_BOOST_SCALE
          return score
        
        def FindTriangleScore(triangle, vertices):
          return vertices[triangle.vertices[0]].score + vertices[triangle.vertices[1]].score + vertices[triangle.vertices[2]].score
        
        def UpdateCache(cache, v1, v2, v3):
          discarted = [v1,v2,v3]
          
          replaced = 0
          for i in range(MAX_SIZE_VERTEX_CACHE):
            if v1 == cache[i] or v2 == cache[i] or v3 == cache[i]:
              replaced = replaced + 1
              cache[i] = discarted[0]
              discarted[0] = discarted[1]
              discarted[1] = discarted[2]
              discarted[2] = -1
            else:
              aux = cache[i]
              cache[i] = discarted[0]
              discarted[0] = discarted[1]
              discarted[1] = discarted[2]
              discarted[2] = aux
          
          return discarted
          
        def RecomputeScores(cache, discarted, vertices, triangles):
          for i in range(MAX_SIZE_VERTEX_CACHE):
            if cache[i] < 0:
              break # no hi ha mes vertexos a la cache
            
            vertex = vertices[ cache[i] ]
            vertex.cache_pos = i
            vertex.score = FindVertexScore(vertex)
          if discarted[0] >= 0:
            vertex = vertices[ discarted[0] ]
            vertex.cache_pos = i
            vertex.score = FindVertexScore(vertex)
          if discarted[1] >= 0:
            vertex = vertices[ discarted[1] ]
            vertex.cache_pos = i
            vertex.score = FindVertexScore(vertex)
          if discarted[2] >= 0:
            vertex = vertices[ discarted[2] ]
            vertex.cache_pos = i
            vertex.score = FindVertexScore(vertex)
            
          best_triangle = -1
          for i in range(MAX_SIZE_VERTEX_CACHE):
            if cache[i] < 0:
              break # no hi ha mes vertexos a la cache
            vertex = vertices[ cache[i] ]
            
            for triangle_index in vertex.triangles:
              triangle = triangles[triangle_index]
              if not triangle.added:
                triangle.score = FindTriangleScore(triangle, vertices)
                if best_triangle < 0 or triangle.score > triangles[best_triangle].score:
                  best_triangle = triangle_index
                  
          if discarted[0] >= 0:
            vertex = vertices[ discarted[0] ]
            
            for triangle_index in vertex.triangles:
              triangle = triangles[triangle_index]
              if not triangle.added:
                triangle.score = FindTriangleScore(triangle, vertices)
                if best_triangle < 0 or triangle.score > triangles[best_triangle].score:
                  best_triangle = triangle_index
          if discarted[1] >= 0:
            vertex = vertices[ discarted[1] ]
            
            for triangle_index in vertex.triangles:
              triangle = triangles[triangle_index]
              if not triangle.added:
                triangle.score = FindTriangleScore(triangle, vertices)
                if best_triangle < 0 or triangle.score > triangles[best_triangle].score:
                  best_triangle = triangle_index
          if discarted[2] >= 0:
            vertex = vertices[ discarted[2] ]
            
            for triangle_index in vertex.triangles:
              triangle = triangles[triangle_index]
              if not triangle.added:
                triangle.score = FindTriangleScore(triangle, vertices)
                if best_triangle < 0 or triangle.score > triangles[best_triangle].score:
                  best_triangle = triangle_index
                  
          if best_triangle < 0:
            for i in range(len(triangles)):
              triangle = triangles[i]
              if (not triangle.added) and ( best_triangle < 0 or triangle.score > triangles[best_triangle].score):
                best_triangle = i
          
          return best_triangle
            
        
        #algoritme principal ---------------------------------------------------------------
        vertices = []
        for vertex in self.vertices:
          vertices.append(Vertex())
        
        triangles = []
        for i in range(len(input_faces)):
          triangle = input_faces[i]
          v1 = triangle.i1
          v2 = triangle.i2
          v3 = triangle.i3
          triangles.append(Triangle(v1,v2,v3))
          
          vertex = vertices[v1]
          vertex.unused_triangles = vertex.unused_triangles + 1
          vertex.triangles.append(i)
          
          vertex = vertices[v2]
          vertex.unused_triangles = vertex.unused_triangles + 1
          vertex.triangles.append(i)
          
          vertex = vertices[v3]
          vertex.unused_triangles = vertex.unused_triangles + 1
          vertex.triangles.append(i)
          
        for vertex in vertices:
          vertex.score = FindVertexScore(vertex)
          
        best_triangle = 0
        for i in range(len(triangles)):
          triangle = triangles[i]
          triangle.score = FindTriangleScore(triangle, vertices)
          if triangle.score > triangles[best_triangle].score:
            best_triangle = i
        
        #-------------------------------------------------------
        
        cache = []
        for i in range(MAX_SIZE_VERTEX_CACHE):
          cache.append(-1)
        
        ordered_faces = []
        
        for i in range(len(triangles)):
          ordered_faces.append( input_faces[best_triangle] )
          
          if i == len(triangles) - 1:
            break # si no, catacroker
          
          triangles[best_triangle].added = True
          
          v_index1 = triangles[best_triangle].vertices[0]
          v_index2 = triangles[best_triangle].vertices[1]
          v_index3 = triangles[best_triangle].vertices[2]
          
          v1 = vertices[ v_index1 ]
          v2 = vertices[ v_index2 ]
          v3 = vertices[ v_index3 ]
          
          v1.unused_triangles = v1.unused_triangles - 1
          v2.unused_triangles = v2.unused_triangles - 1
          v3.unused_triangles = v3.unused_triangles - 1
          
          discarted = UpdateCache(cache, v_index1, v_index2, v_index3)
          
          best_triangle = RecomputeScores(cache, discarted, vertices, triangles)
          
        return ordered_faces
        
def createMesh(mesh, vertex_groups, consolePrint):
    faces = []
    vertices = {} # mapa vertexId -> cares que el contenen, vertex corresponent
    for faceId in range(len(mesh.faces)):
        face = mesh.faces[faceId]
        uv_textures = mesh.uv_textures['UV1'].data[faceId]
        f = AntagonistFace(mesh, vertices, face.vertices[0], face.vertices[1], face.vertices[2], uv_textures.uv1, uv_textures.uv2, uv_textures.uv3, face.normal, face.material_index)
        
        faces.append(f)
        
        
        if len(face.vertices) == 4:
            f = AntagonistFace(mesh, vertices, face.vertices[2], face.vertices[3], face.vertices[0], uv_textures.uv3, uv_textures.uv4, uv_textures.uv1, face.normal, face.material_index)
            
            faces.append(f)
            
            
    for vId in vertices.keys():
        faceVertexTuples = vertices[vId]
        if mesh.use_auto_smooth:
            for faceVertexTuple in faceVertexTuples:           
                n = NormalAux()
                t = NormalAux() #tangent
                b = NormalAux() #bitangent
                nThis = NormalAux()
                nThis.add(faceVertexTuple[0])
                for otherFVT in faceVertexTuples:
                    if nThis.angle(otherFVT[0]) < mesh.auto_smooth_angle:
                        n.add(otherFVT[0])
                        t.addT(otherFVT[0])
                        b.addB(otherFVT[0])
                        
                n.normalize()
                t.normalize()
                b.normalize()
                        
                faceVertexTuple[1].nx = n.nx
                faceVertexTuple[1].ny = n.ny
                faceVertexTuple[1].nz = n.nz
                faceVertexTuple[1].tx = t.nx
                faceVertexTuple[1].ty = t.ny
                faceVertexTuple[1].tz = t.nz
                faceVertexTuple[1].bx = b.nx
                faceVertexTuple[1].by = b.ny
                faceVertexTuple[1].bz = b.nz
        else:
            n = NormalAux()
            t = NormalAux() #tangent
            b = NormalAux() #bitangent
            for faceVertexTuple in faceVertexTuples:
                n.add(faceVertexTuple[0])
                t.addT(faceVertexTuple[0])
                b.addB(faceVertexTuple[0])
            n.normalize()
            t.normalize()
            b.normalize()
            for faceVertexTuple in faceVertexTuples:
                faceVertexTuple[1].nx = n.nx
                faceVertexTuple[1].ny = n.ny
                faceVertexTuple[1].nz = n.nz
                faceVertexTuple[1].tx = t.nx
                faceVertexTuple[1].ty = t.ny
                faceVertexTuple[1].tz = t.nz
                faceVertexTuple[1].bx = b.nx
                faceVertexTuple[1].by = b.ny
                faceVertexTuple[1].bz = b.nz
        
         
    vertices = {}
    verticesArray = []
            
    i = 0
    for face in faces:
        if consolePrint:
            print(i)
            print(face)
            print('\n')
        
        if face.v1 in vertices:
            face.i1 = vertices[face.v1]
        else:
            vertices[face.v1] = i
            face.i1 = i
            i = i + 1
            verticesArray.append(face.v1)
        
        if face.v2 in vertices:
            face.i2 = vertices[face.v2]
        else:
            vertices[face.v2] = i
            face.i2 = i
            i = i + 1
            verticesArray.append(face.v2)
        
        if face.v3 in vertices:
            face.i3 = vertices[face.v3]
        else:
            vertices[face.v3] = i
            face.i3 = i
            i = i + 1
            verticesArray.append(face.v3)
        
    if consolePrint:
        i = 0
        max_bone = 0
        for v in verticesArray:
            print(i)
            print(v)
            print('\n')
            i = i + 1
            
            if v.w_indices[0] > max_bone:
              max_bone = v.w_indices[0]
            if v.w_indices[1] > max_bone:
              max_bone = v.w_indices[1]
            if v.w_indices[2] > max_bone:
              max_bone = v.w_indices[2]
            if v.w_indices[3] > max_bone:
              max_bone = v.w_indices[3]
        
        print("max bone: " + str(max_bone))
        
    materialFaces = {}
    for face in faces:
        material = mesh.materials[face.material].name
        if not material in materialFaces:
            materialFaces[material] = []
        materialFaces[material].append(face)
        
    mesh = AntagonistMesh(materialFaces, verticesArray, vertex_groups)  
    
    if consolePrint:
      for material in mesh.materialFaces.keys():
        print(material)
        for face in mesh.materialFaces[material]:
          print( str(face.i1) + ", " + str(face.i2) + ", " + str(face.i3) )
    
    return mesh


def saveMeshText(mesh, originalMesh, filepath):
    f = open(filepath, 'w')
    f.write("antagonist text")
    f.write("\nExport_Physics")
    if originalMesh.export_physics:
        f.write("\nTrue")
    else:
        f.write("\nFalse")
    f.write("\npos, normal, tangent, bitangent, uv")
    if mesh.animated:
      f.write(", weights, indices")
    f.write("\n%s" % len(mesh.vertices))
    f.write(" "    + str(False)) #Animats
    for v in mesh.vertices:
        f.write("\n%s" % v.x)
        f.write(" %s"  % v.y)
        f.write(" %s"  % v.z)
        
        f.write(" %s"  % v.nx)
        f.write(" %s"  % v.ny)
        f.write(" %s"  % v.nz)
        
        f.write(" %s"  % v.tx)
        f.write(" %s"  % v.ty)
        f.write(" %s"  % v.tz)
        
        f.write(" %s"  % v.bx)
        f.write(" %s"  % v.by)
        f.write(" %s"  % v.bz)
        
        f.write(" %s"  % v.u)
        f.write(" %s"  % v.v)
        
        if mesh.animated:
          f.write(" %s" % v.weights[0])
          f.write(" %s" % v.weights[1])
          f.write(" %s" % v.weights[2])
          f.write(" %s" % v.weights[3])
          
          f.write(" %s" % v.w_indices[0])
          f.write(" %s" % v.w_indices[1])
          f.write(" %s" % v.w_indices[2])
          f.write(" %s" % v.w_indices[3])
          
          
    
    f.write('\n\n%s' % len(mesh.materialFaces))
    for material in mesh.materialFaces.keys():
        f.write('\n%s' % material)
        f.write('\n%s' % len(mesh.materialFaces[material]))
    for material in mesh.materialFaces.keys():
        for face in mesh.materialFaces[material]:
            f.write('\n%s' % face.i1)
            f.write(' %s'  % face.i2)
            f.write(' %s'  % face.i3)
    
    if mesh.animated:
      f.write('\n\n%s' % len(mesh.bones))
      for bone in mesh.bones:
        f.write('\n%s' % bone)

def addMaterials(mesh, materials):
    for material in mesh.materialFaces.keys():
        materials.add( AntagonistMaterial( bpy.data.materials[material] ) )

# ------------------------------------------------------------------------------------------------------------------------
# ------------------------------------------------------------------------------------------------------------------------
# ------------------------------------------------------------------------------------------------------------------------
# ------------------------------------------------------------------------------------------------------------------------

class AntagonistMaterial:
    def __init__(self, mat):
        self.name = mat.name
        
        self.specular_intensity = mat.specular_intensity
        self.specular_power     = mat.specular_hardness
        
        self.alpha              = False
        self.height_intensity   = 0.2
        
        self.albedo_texture = False
        self.normal_texture = False
        self.height_texture = False
        
        for texName in mat.texture_slots.keys():
            tex = mat.texture_slots[texName]
            if tex.use_map_color_diffuse:
                self.albedo_texture = True
                self.albedo_texture_name = tex.texture.image.name
                self.albedo_texture_img  = tex.texture.image
                self.alpha               = tex.use_map_alpha
            elif tex.use_map_normal:
                    self.normal_texture = True
                    self.normal_texture_name = tex.texture.image.name
                    self.normal_texture_img  = tex.texture.image
            elif tex.use_map_displacement:            
                    self.height_texture = True
                    self.height_texture_name = tex.texture.image.name
                    self.height_texture_img  = tex.texture.image
                    self.height_intensity    = tex.displacement_factor
                    
        self.effect = "BasicEffect" #TODO
                    
    def __str__(self):
        aux = "material specular params: " + str(self.specular_intensity) + ", " + str(self.specular_power)
        aux = aux + " alpha: %s" % self.alpha
        aux = aux + " height: " + str(self.height_intensity)
        if self.albedo_texture:
            aux = aux + " albedo: '" + self.albedo_texture_name + "'"
        if self.normal_texture:
            aux = aux + " normal: '" + self.normal_texture_name + "'"
        if self.height_texture:
            aux = aux + " height: '" + self.height_texture_name + "'"
        aux = aux + " effect: " + self.effect
        return aux


def createMaterial(mat, printInfo):
    material = AntagonistMaterial(mat)
    if printInfo:
        print(material)
        
    return material
    
        
def ckeckMaterial(mat):
    albedo = False
    normal = False
    height = False
    for texName in mat.texture_slots.keys():
        tex = mat.texture_slots[texName]
        if tex.texture.type != 'IMAGE':
            return "Texture " + texName + " is not an image"
        elif tex.use_map_color_diffuse:
            if albedo:
                return "There are 2 color textures"
            albedo = True
        elif tex.use_map_normal:
            if normal:
                return "There are 2 normal textures"
            normal = True
        elif tex.use_map_displacement:
            if height:
                return "There are 2 height textures"
            height = True
        else:
            return "Texture '" + texName + "' hasn't got color, normal or displacement checked"
        
    if not albedo:
        return "There is no color texture"
    if (height and not normal) or (normal and not height):
        return "There must be a normal AND a heightmap, or neither"
    return True

def saveMaterialText(mat, filepath):
    f = open(filepath, 'w')
    f.write("antagonist text")
    f.write("\nspecular factor, specular power, height, alpha")
    f.write("\n" + str(mat.specular_intensity) + " " + str(mat.specular_power) + " " + str(mat.height_intensity) + " %s" % mat.alpha)
    f.write("\n" + mat.effect)
            
    if mat.albedo_texture:
        f.write("\nalbedo\n" + mat.albedo_texture_name)
    if mat.normal_texture:
        f.write("\nnormal\n" + mat.normal_texture_name)
    if mat.height_texture:
        f.write("\nheight\n" + mat.height_texture_name)

def addTextures(mat, textures):
    if mat.albedo_texture:
        textures.add( mat.albedo_texture_img )
    if mat.normal_texture:
        textures.add( mat.normal_texture_img )
    if mat.height_texture:
        textures.add( mat.height_texture_img )
        

# ------------------------------------------------------------------------------------------------------------------------
# ------------------------------------------------------------------------------------------------------------------------
# ------------------------------------------------------------------------------------------------------------------------
# ------------------------------------------------------------------------------------------------------------------------

# ExportHelper is a helper class, defines filename and
# invoke() function which calls the file selector.
from bpy_extras.io_utils import ExportHelper
from bpy.props import StringProperty, BoolProperty, EnumProperty


class AntagonistExportHelper(ExportHelper):

    def invoke(self, context, event):
        import os
        if not self.filepath:
            blend_filepath = context.blend_data.filepath
            if not blend_filepath:
                blend_filepath = ""
            else:
                blend_filepath = os.path.dirname(blend_filepath)

            self.filepath = blend_filepath + "/" + self.getDefaultFileName(context) + self.filename_ext

        print("------------------------------------------------------")
        print(self.filepath)
        context.window_manager.fileselect_add(self)
        return {'RUNNING_MODAL'}
    

# ------------------------------------------------------------------------------------------------------------------------
# ------------------------------------------------------------------------------------------------------------------------
# ------------------------------------------------------------------------------------------------------------------------
# ------------------------------------------------------------------------------------------------------------------------



class OBJECT_PT_export_mesh(bpy.types.Panel):
    bl_space_type = 'PROPERTIES'
    bl_region_type = 'WINDOW'
    bl_context = "data"
    bl_label = "Export mesh"
    bl_idname = "OBJECT_PT_export_mesh"
    
    display = 0
    
    @classmethod
    def poll(cls, context):
        engine = context.scene.render.engine
        return context.object is not None and context.object.data is not None and isinstance(context.object.data, bpy.types.Mesh) and engine == 'BLENDER_RENDER'
    
    def draw_header(self, context):
        layout = self.layout
        layout.label(text="", icon="SCRIPTPLUGINS")
    
    def draw(self, context):
        layout = self.layout

        obj = context.object

        row = layout.row()
        row.label(text="The currently selected mesh is: " + obj.data.name, icon='MESH_DATA')

        row = layout.row()
        row.prop(obj.data, "export_physics")
        
        row = layout.row()
        row.prop(obj.data, "antagonist_class")
        
        
        row = layout.row()
        row.operator("export.antagonist_mesh", text="Export current Mesh")




class OBJECT_OT_print_vertices(bpy.types.Operator):
    bl_label = "PingPong operator"
    bl_idname = "antagonist.print_verts"
    bl_description = "Print the vertices of the current object-mesh"

    def invoke(self, context, event):
        import bpy
        
        createMesh(context.object.data, True)
            
        
        return{"FINISHED"}
    

class ExportAntagonistMesh(bpy.types.Operator, AntagonistExportHelper):
    '''Export a mesh in antagonist format.'''
    bl_idname = "export.antagonist_mesh"  # this is important since its how bpy.ops.export.some_data is constructed
    bl_label = "Export Antagonist Mesh"

    # ExportHelper mixin class uses this
    filename_ext = ".mesh"

    filter_glob = StringProperty(
            default="*.mesh",
            options={'HIDDEN'},
            )

    # List of operator properties, the attributes will be assigned
    # to the class instance from the operator settings before calling.
    binary_format = BoolProperty(
            name="Binary",
            description="Binary Format",
            default=True,
            )
            
    save_materials = BoolProperty(
            name="Save Materials",
            description="Save Materials",
            default=False,
            )
            
    save_textures = BoolProperty(
            name="Save Textures",
            description="Save Textures",
            default=False,
            )
    
    def getDefaultFileName(self, context):
        return context.object.data.name

    @classmethod
    def poll(cls, context):
        return context.active_object is not None and context.active_object.data is not None and isinstance(context.active_object.data, bpy.types.Mesh)

    def execute(self, context):
        import os
        mesh = createMesh(context.object.data, context.object.vertex_groups, False)
        
        #return {'FINISHED'}
        #path = os.path.dirname(self.filepath) + "/" + context.object.data.name
        path = self.filepath
        
        
        if(self.binary_format):
            print("binary not yet implemented, using text format") #TODO
            saveMeshText(mesh, context.object.data, path)
        else:
            saveMeshText(mesh, context.object.data, path)
            
            
        materials = set()
        addMaterials(mesh, materials)    
        
        if self.save_materials:
            pathdir = os.path.dirname(path) + "/../materials/"
            for material in materials:
                if(self.binary_format):
                    saveMaterialText(material, pathdir + material.name + ".mat")
                else:
                    saveMaterialText(material, pathdir + material.name + ".mat")
                    
            
        if self.save_textures:
            pathdir = os.path.dirname(path) + "/../textures/"
            textures = set()
            for material in materials:
                addTextures(material, textures)
            for image in textures:
                oldpath = image.filepath_raw
                image.filepath_raw = pathdir + image.name + ".png"
                image.save()
                image.filepath_raw = oldpath
            
        
        return {'FINISHED'} # write_some_data(context, self.filepath, self.use_setting, self.type)


# ------------------------------------------------------------------------------------------------------------------------
# ------------------------------------------------------------------------------------------------------------------------
# ------------------------------------------------------------------------------------------------------------------------

def check_material(mat):
    if mat is not None:
        if mat.use_nodes:
            if mat.active_node_material is not None:
                return True
            return False
        return True
    return False



class OBJECT_PT_export_material(bpy.types.Panel):
    bl_space_type = 'PROPERTIES'
    bl_region_type = 'WINDOW'
    bl_context = "material"
    bl_label = "Export material"
    bl_idname = "OBJECT_PT_export_material"
    
    display = 0
    
    @classmethod
    def poll(cls, context):
        engine = context.scene.render.engine
        return check_material(context.material) and engine == 'BLENDER_RENDER'
    
    def draw_header(self, context):
        layout = self.layout
        layout.label(text="", icon="SCRIPTPLUGINS")
    
    def draw(self, context):
        layout = self.layout

        mat = context.material

        row = layout.row()
        row.label(text="The currently selected material is: " + context.material.name, icon='MATERIAL')

        col = layout.column()
        
        
        check = ckeckMaterial(mat)
        if check == True:
            row = layout.row()
            row.operator("antagonist.print_mat", text="Print Material")
            row = layout.row()
            row.operator("export.antagonist_material", text="Export Material")
        else:
            row = layout.row()
            row.label(text=check)
        
        #row = layout.row()
        #row.operator("export.antagonist_material", text="Export current Material")

class OBJECT_OT_print_material(bpy.types.Operator):
    bl_label = "Material printer operator"
    bl_idname = "antagonist.print_mat"
    bl_description = "Print the current material"

    def invoke(self, context, event):
        import bpy
        
        createMaterial(context.material, True)
            
        
        return{"FINISHED"}



class ExportAntagonistMaterial(bpy.types.Operator, AntagonistExportHelper):
    '''Export a material in antagonist format.'''
    bl_idname = "export.antagonist_material"  # this is important since its how bpy.ops.export.some_data is constructed
    bl_label = "Export Antagonist Material"

    # ExportHelper mixin class uses this
    filename_ext = ".mat"

    filter_glob = StringProperty(
            default="*.mat",
            options={'HIDDEN'},
            )

    # List of operator properties, the attributes will be assigned
    # to the class instance from the operator settings before calling.
    binary_format = BoolProperty(
            name="Binary",
            description="Binary Format",
            default=True,
            )
            
    save_textures = BoolProperty(
            name="Save Textures",
            description="Save Textures",
            default=False,
            )
    
    def getDefaultFileName(self, context):
        return context.material.name

    @classmethod
    def poll(cls, context):
        return check_material(context.material)

    def execute(self, context):
        import os
        
        mat = AntagonistMaterial(context.material)
        
        
        #path = os.path.dirname(self.filepath) + "/" + context.object.data.name
        path = self.filepath
        
        if(self.binary_format):
            print("binary not yet implemented, using text format") #TODO
            saveMaterialText(mat, path)
        else:
            saveMaterialText(mat, path)
        
        if self.save_textures:
            pathdir = os.path.dirname(path) + "/../textures/"
            textures = set()
            addTextures(mat, textures)
            for image in textures:
                oldpath = image.filepath_raw
                image.filepath_raw = pathdir + image.name + ".png"
                image.save()
                image.filepath_raw = oldpath
        
        return {'FINISHED'} # write_some_data(context, self.filepath, self.use_setting, self.type)

# ------------------------------------------------------------------------------------------------------------------------
# ------------------------------------------------------------------------------------------------------------------------
# ------------------------------------------------------------------------------------------------------------------------

# Only needed if you want to add into a dynamic menu
def menu_func_export(self, context):
    self.layout.operator(ExportAntagonistMesh.bl_idname, text="Antagonist Mesh Exporter")
    self.layout.operator(ExportAntagonistMaterial.bl_idname, text="Antagonist Material Exporter")

def register():
    bpy.utils.register_class(ExportAntagonistMesh)
    bpy.utils.register_class(ExportAntagonistMaterial)
    bpy.utils.register_class(OBJECT_PT_export_mesh)
    bpy.utils.register_class(OBJECT_PT_export_material)
    bpy.utils.register_class(OBJECT_OT_print_vertices)
    bpy.utils.register_class(OBJECT_OT_print_material)
    bpy.types.INFO_MT_file_export.append(menu_func_export)


def unregister():
    bpy.utils.unregister_class(ExportAntagonistMesh)
    bpy.utils.unregister_class(ExportAntagonistMaterial)
    bpy.utils.unregister_class(OBJECT_PT_export_mesh)
    bpy.utils.unregister_class(OBJECT_PT_export_material)
    bpy.utils.unregister_class(OBJECT_OT_print_vertices)
    bpy.utils.unregister_class(OBJECT_OT_print_material)
    bpy.types.INFO_MT_file_export.remove(menu_func_export)

if __name__ == "__main__":
    register()
    
    #afegim les propietats dels objectes, si cal
    bpy.types.Mesh.export_physics = bpy.props.BoolProperty(name="export physic mesh")
    bpy.types.Mesh.antagonist_class = bpy.props.StringProperty(name="antagonist object class")
