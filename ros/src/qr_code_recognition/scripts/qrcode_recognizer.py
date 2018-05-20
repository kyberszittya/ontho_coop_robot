import pyzbar.pyzbar as zbar

import sys
import rospy
import cv2
from std_msgs.msg import String
from sensor_msgs.msg import Image
from std_msgs.msg import String
from cv_bridge import CvBridge, CvBridgeError

from ontho_robot_msgs.srv import GetDetectedLabel, GetDetectedLabelResponse

import numpy as np

import time

class QRImageSubscriber(object):

    def handle_get_labels(self,req):
        req = GetDetectedLabelResponse()
        req.labels = self.labels
        return req

    def __init__(self):
        self.bridge = CvBridge()
        self.labels = []
        self.image_sub = rospy.Subscriber("/camera/rgb/image_raw", Image, self.callback)
        self.proxy_labels = rospy.Service('/get_detected_labels', GetDetectedLabel, self.handle_get_labels)
        
    
    def callback(self, data):
        try:
            d = time.time()
            cv_img_raw = cv2.resize(self.bridge.imgmsg_to_cv2(data, "mono8"),(1200,675))
            img_min = np.min(cv_img_raw)
            img_max = np.max(cv_img_raw)
            #print(time.time()-d)
            #cv_img = cv2.threshold(cv_img_raw,0,255,cv2.THRESH_BINARY+cv2.THRESH_OTSU)[1]
            cv_img = cv_img_raw
        except CvBridgeError as e:
            print(e)
        d = time.time()
        #cv2.imshow("THRE",cv_img)
        #cv2.waitKey(1)
        decoded_objects = zbar.decode(cv_img)
        print(time.time()-d)
        self.labels = []
        for d in decoded_objects:
            #print(d.type,d.data)
            self.labels.append(d.data)
        
        


def main(args):
    ic = QRImageSubscriber()
    rospy.init_node('qr_image_recog', anonymous=True)
    try:
        rospy.spin()
    except KeyboardInterrupt:
        print("Shutting down")
    cv2.destroyAllWindows()

if __name__ == "__main__":
    main(sys.argv)