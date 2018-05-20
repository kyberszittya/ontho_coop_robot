import rospy
import actionlib

from tf.transformations import quaternion_from_euler

from ontho_robot_msgs.msg import DetectContainerObjectAction, DetectContainerObjectActionGoal

from ontho_robot_msgs.srv import GetDetectedLabel, GetDetectedLabelRequest

from geometry_msgs.msg import Pose

from math import pi

if __name__ == '__main__':
    rospy.init_node('detect_container_client')
    client = actionlib.SimpleActionClient('/ontho_container_detection', DetectContainerObjectAction)
    client.wait_for_server()
    getlabels = rospy.ServiceProxy('/get_detected_labels', GetDetectedLabel)
    with open("/home/kyberszittya/complex_ai_ws/src/complex_ai_worlds/worlds/goal.txt",'r') as f:
        for l in f:
            g = l.strip().split(' ')
            print(g)
            x = float(g[0])
            y = float(g[1])
            z = float(g[2])
            o_x = float(g[3])
            o_y = float(g[4])
            o_z = float(g[5])
            o_w = float(g[6])
            goal = DetectContainerObjectActionGoal()
            targetPos = Pose()
            targetPos.position.x = x
            targetPos.position.y = y
            
            #q = quaternion_from_euler(0.0, 0.0, o)
            
            targetPos.orientation.x = o_x
            targetPos.orientation.y = o_y
            targetPos.orientation.z = o_z
            targetPos.orientation.w = o_w
            goal.goal = targetPos
            client.send_goal(goal)
            client.wait_for_result()
            print("Labels: "+str(getlabels('').labels))
                
