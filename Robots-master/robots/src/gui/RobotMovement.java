package gui;

import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

public class    RobotMovement extends Observable {

    public double[] getRobotData() { //X,Y,Angle,TargX,TargY
        return new double[] {m_robotPositionX, m_robotPositionY, m_robotDirection,
                            m_targetPositionX, m_targetPositionY};
    }

    public void setTarget(int x, int y) {
        m_targetPositionX = x;
        m_targetPositionY = y;
    }

    volatile double m_robotPositionX = 100;
    volatile double m_robotPositionY = 100;
    volatile double m_robotDirection = 0;

    private volatile int m_targetPositionX = 150;
    private volatile int m_targetPositionY = 100;

    private static final double maxVelocity = 0.1;
    private static final double maxAngularVelocity = 0.001;

    private double velocity;
    private double angularVelocity;
    private double angleToTarget;


    private static double distance(double x1, double y1, double x2, double y2)
    {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY)
    {
        double diffX = toX - fromX;
        double diffY = toY - fromY;

        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    protected void onModelUpdateEvent()
    {

        moveRobot();

    }

    private static double applyLimits(double value, double min, double max)
    {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    protected void moveRobot()
    {
        double distance = distance(m_targetPositionX, m_targetPositionY,
            m_robotPositionX, m_robotPositionY);
        if (distance < 0.5) {
            return;
        }
        velocity = maxVelocity;
        angleToTarget = angleTo(m_robotPositionX, m_robotPositionY, m_targetPositionX, m_targetPositionY);
        angularVelocity = 0;
        if (angleToTarget > m_robotDirection)
        {
            angularVelocity = maxAngularVelocity;
        }
        if (angleToTarget < m_robotDirection)
        {
            angularVelocity = -maxAngularVelocity;
        }

        int duration = MainApplicationFrame.globalTimeConst;
        velocity = applyLimits(velocity, 0, maxVelocity);
        angularVelocity = applyLimits(angularVelocity, -maxAngularVelocity, maxAngularVelocity);

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
    }

    private static double asNormalizedRadians(double angle)
    {
        while (angle < 0)
            angle += 2*Math.PI;
        while (angle >= 2*Math.PI)
            angle -= 2*Math.PI;
        return angle;
    }
}
