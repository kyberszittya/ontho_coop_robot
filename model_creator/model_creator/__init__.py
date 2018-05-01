import cv2
import numpy as np
import qrcode

import os

from lxml import etree

import shutil

import owlready2 as o2

def getOnthology(path, onto_uri):
    o2.onto_path.append(path)
    onto = o2.get_ontology(onto_uri)
    onto.load()
    return onto

def getLeafWares(onto):
    res = []
    for i in onto.search(is_a=onto.Wares):
        if len((onto.search(is_a=i)))==0:
            res += [i.name.lower()]
    return res

def generateGeometryElement(label_text):
    geometry_model_element = etree.Element("geometry")
    mesh_model_element = etree.Element("mesh")
    uri_element = etree.Element("uri")
    uri_element.text = "model://"+"container_"+label_text+"/meshes/model.dae"
    mesh_model_element.append(uri_element)
    geometry_model_element.append(mesh_model_element)
    return geometry_model_element

def generateModelConfig(label_text):
    model_config_element = etree.Element("model")
    name_element = etree.Element("name")
    name_element.text = ("Container_"+label_text)
    model_config_element.append(name_element)
    sdf_version_element = etree.Element("sdf")
    sdf_version_element.set("version",str(1.5))
    sdf_version_element.text = "model.sdf"
    model_config_element.append(sdf_version_element)

    return model_config_element



onto = getOnthology("c:/Users/kyberszittya/onthology_based_robotics/ontho_coop_robot/",
             "RobotSemantic.owl")
labels = getLeafWares(onto)


for label_text in labels:
    if not (os.path.isdir("./gazebo/models/container_"+label_text)):
        os.makedirs("./gazebo/models/container_"+label_text+"/meshes/")
    texture_folder = "./gazebo/models/container_"+label_text+"/meshes/"
    img_qr = qrcode.make(label_text)
    img = (np.asarray(img_qr,np.float32))
    img_texture = cv2.normalize(cv2.imread('textureprocess.png',0).astype('float'), None, 0.0, 1.0, cv2.NORM_MINMAX)

    img_texture[1170:1170+img.shape[0],230:230+img.shape[1]] = img
    img_texture[1170:1170+img.shape[0],600:600+img.shape[1]] = img
    img_texture[800:800+img.shape[0],600:600+img.shape[1]] = img
    img_texture[420:420+img.shape[0],600:600+img.shape[1]] = img
    img_texture[50:50+img.shape[0],600:600+img.shape[1]] = img
    img_texture[1170:1170+img.shape[0],970:970+img.shape[1]] = img
    cv2.imwrite(texture_folder+"texture.png", img_texture*255)

    root = etree.Element("sdf")
    root.set("version","1.5")

    model_root = etree.Element("model")
    model_root.set("name","container_"+label_text)
    link_root = etree.Element("link")
    link_root.set("name", "link_container_" + label_text)
    inertial_element = etree.Element("inertial")
    mass_element = etree.Element("mass")
    mass_element.text = str(10.0)
    inertial_element.append(mass_element)
    link_root.append(inertial_element)
    model_root.append(link_root)

    collision_model_element = etree.Element("collision")
    collision_model_element.set("name", "coll")

    visual_model_element = etree.Element("visual")
    visual_model_element.set("name", "viz")
    collision_model_element.append(generateGeometryElement(label_text))
    visual_model_element.append(generateGeometryElement(label_text))
    link_root.append(collision_model_element)
    link_root.append(visual_model_element)
    root.append(model_root)
    with open("./gazebo/models/container_"+label_text+"/model.sdf",'w') as f:
        f.write(etree.tostring(root, pretty_print=True, xml_declaration=True).decode('utf-8'))
    with open("./gazebo/models/container_"+label_text+"/model.config",'w') as f:
        f.write(etree.tostring(generateModelConfig(label_text), pretty_print=True, xml_declaration=True).decode('utf-8'))
    shutil.copy("./meshes/model.dae","./gazebo/models/container_"+label_text+"/meshes/model.dae")