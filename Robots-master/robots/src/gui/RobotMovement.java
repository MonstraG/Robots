package gui;

import java.util.Observable;

class RobotMovement extends Observable {

    double[] getRobotData() {
        return new double[] {m_robotPositionX, m_robotPositionY, m_robotDirection,
                            m_targetPositionX, m_targetPositionY};
    }

    void setTarget(int x, int y) {
        m_targetPositionX = x;
        m_targetPositionY = y;
    }

    volatile double m_robotPositionX = 100;
    volatile double m_robotPositionY = 100;
    volatile double m_robotDirection = 0;

    volatile int m_targetPositionX = 150;
    volatile int m_targetPositionY = 100;

    private static final double maxVelocity = 0.1;
    private static final double maxAngularVelocity = 0.002;

    //TODO: rotate-before-moving mode
    //TODO: target list with dots on path.

    private static double distance(double x1, double y1, double x2, double y2)
    {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY)
    {
        return asNormalizedRadians(Math.atan2(toY - fromY, toX - fromX));
    }

    void onModelUpdateEvent()
    {
        double distance = distance(m_targetPositionX, m_targetPositionY, m_robotPositionX, m_robotPositionY);
        if (distance > 0.5) { //if target not reached.
            moveRobot(maxVelocity);
        }
        //here be code that should be run onModelUpdate but not connected to robot
        //if looking at target {
        // moveRobot(); (basically velocity = maxVel)
        //else
        // rotateRobot();

    }

    private static double applyLimits(double value, double min, double max)
    {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    void rotateRobot() {

    }

    void moveRobot() { //default speed is FULL SPEED
        moveRobot(maxVelocity);
    }

    void moveRobot(double velocity)
    {
        double angularVelocity;
        double angleToTarget = angleTo(m_robotPositionX, m_robotPositionY, m_targetPositionX, m_targetPositionY);
        angleToTarget = asNormalizedRadians(angleToTarget - m_robotDirection); //angle from robots perspective
        if (angleToTarget < Math.PI)
            angularVelocity = maxAngularVelocity; //turning left is closer
        else
            angularVelocity = -maxAngularVelocity; //turing right is closer

        velocity = applyLimits(velocity, 0, maxVelocity);
        angularVelocity = applyLimits(angularVelocity, -maxAngularVelocity, maxAngularVelocity);
        int duration = MainApplicationFrame.globalTimeConst;
        double newX = m_robotPositionX + velocity / angularVelocity *
                (Math.sin(m_robotDirection  + angularVelocity * duration) -
                        Math.sin(m_robotDirection));
        if (!Double.isFinite(newX))
        {
            newX = m_robotPositionX + velocity * duration * Math.cos(m_robotDirection);
        }
        double newY = m_robotPositionY - velocity / angularVelocity *
                (Math.cos(m_robotDirection  + angularVelocity * duration) -
                        Math.cos(m_robotDirection));
        if (!Double.isFinite(newY))
        {
            newY = m_robotPositionY + velocity * duration * Math.sin(m_robotDirection);
        }
        m_robotPositionX = newX;
        m_robotPositionY = newY;
        m_robotDirection = asNormalizedRadians(m_robotDirection + angularVelocity * duration);

        notifyObservers();
        setChanged();
    }

    private static double asNormalizedRadians(double angle)
    {
        //convert any angle to [0, 2PI)
        while (angle < 0)
            angle += 2*Math.PI;
        while (angle >= 2*Math.PI)
            angle -= 2*Math.PI;
        return angle;
    }
}
