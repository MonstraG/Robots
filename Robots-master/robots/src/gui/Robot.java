package gui;

import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Robot {
    volatile double m_robotPositionX = 100;
    volatile double m_robotPositionY = 100;
    volatile double m_robotDirection = 0;

    volatile double m_targetPositionX = 150;
    volatile double m_targetPositionY = 100;

    private static final double maxVelocity = 0.1;
    private static final double maxAngularVelocity = 0.002;
    private static final double distanceAtMaxSpeed = maxVelocity * MainApplicationFrame.globalTimeConst;

    volatile CopyOnWriteArrayList<Point> path = new CopyOnWriteArrayList<>(); //dotted path to target
}
