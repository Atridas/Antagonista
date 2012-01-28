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
        
    def __str__(self):
        aux =    "vertex pos:"    + str(self.x ) + ", " + str(self.y ) + ", " + str(self.z )
        aux = aux + " normal:"    + str(self.nx) + ", " + str(self.ny) + ", " + str(self.nz)
        aux = aux + " tangent:"   + str(self.tx) + ", " + str(self.ty) + ", " + str(self.tz)
        aux = aux + " bitangent:" + str(self.bx) + ", " + str(self.by) + ", " + str(self.bz)
        aux = aux +     " uv:"    + str(self.u ) + ", " + str(self.v)
        return aux

    def __cmp__(self, other):
        if self.x == other.x:
            if self.y == other.y:
                if self.z == other.z:
                    if self.nx == other.nx:
                        if self.ny == other.ny:
                            if self.nz == other.nz:
                                if self.u == other.u:
                                    if self.v == other.v:
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
        return hash(self.x) ^ hash(self.y) ^ hash(self.z) ^ hash(self.nx) ^ hash(self.ny) ^ hash(self.nz) ^ hash(self.u) ^ hash(self.v)


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
    def __init__(self, materialFaces, vertices):
        self.materialFaces = materialFaces
        self.vertices = vertices

def createMesh(mesh, consolePrint):
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
        for v in verticesArray:
            print(i)
            print(v)
            print('\n')
            i = i + 1
        
    materialFaces = {}
    for face in faces:
        material = mesh.materials[face.material].name
        if not material in materialFaces:
            materialFaces[material] = []
        materialFaces[material].append(face)
        
    return AntagonistMesh(materialFaces, verticesArray)  


def saveMeshText(mesh, originalMesh, filepath):
    f = open(filepath, 'w')
    f.write("antagonist text")
    f.write("\nExport_Physics")
    if originalMesh.export_physics:
        f.write("\nTrue")
    else:
        f.write("\nFalse")
    f.write("\npos, normal, tangent, bitangent, uv")
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
    
    f.write('\n\n%s' % len(mesh.materialFaces))
    for material in mesh.materialFaces.keys():
        f.write('\n%s' % material)
        f.write('\n%s' % len(mesh.materialFaces[material]))
    for material in mesh.materialFaces.keys():
        for face in mesh.materialFaces[material]:
            f.write('\n%s' % face.i1)
            f.write(' %s'  % face.i2)
            f.write(' %s'  % face.i3)


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
        mesh = createMesh(context.object.data, False)
        
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
