import rospy
import actionlib

from ontho_robot_msgs.msg import DetectContainerObjectAction, DetectContainerObjectActionGoal

from move_base_msgs.msg import MoveBaseAction, MoveBaseGoal


class DetectContainerObjectServer(object):
    def __init__(self):
        self.server = actionlib.SimpleActionServer('/ontho_container_detection', 
            DetectContainerObjectAction, self.execute)
        self.client = \
            actionlib.SimpleActionClient('/move_base', MoveBaseAction)
    def execute(self, goal):
        g = MoveBaseGoal()
        g.target_pose.header.frame_id = 'odom'
        g.target_pose.pose = goal.goal
        self.client.send_goal(g)
        self.client.wait_for_result()
        self.server.set_succeeded()


if __name__ == '__main__':
  rospy.init_node('detect_container_object')
  server = DetectContainerObjectServer()
  rospy.spin()