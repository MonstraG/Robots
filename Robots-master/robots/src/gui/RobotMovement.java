package gui;

import log.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicInteger;

class RobotMovement extends Observable {

    RobotMovement(GameWindow gw) {
        gameWindow = gw;
    }

    double[] getRobotData() {
        return new double[] {m_robotPositionX, m_robotPositionY, m_robotDirection,
                            m_targetPositionX, m_targetPositionY};
    }

    void setTarget(int x, int y) {
        m_targetPositionX = x;
        m_targetPositionY = y;
    }
    private final GameWindow gameWindow;

    volatile double m_robotPositionX = 100;
    volatile double m_robotPositionY = 100;
    volatile double m_robotDirection = 0;

    volatile double m_targetPositionX = 150;
    volatile double m_targetPositionY = 100;

    private static final double maxVelocity = 10;
    private static final double maxAngularVelocity = 0.002;
    //TODO: smooth turning

    volatile ArrayList<Point> path = new ArrayList<>();
    volatile AtomicInteger pointsReached = new AtomicInteger(0);

    //TODO: target list with dots on path.

    private static double distance(double x1, double y1, double x2, double y2)
    {
        //rewrite evertyhing to Point??
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY)
    {
        return asNormalizedRadians(Math.atan2(toY - fromY, toX - fromX));
    }

    void onModelUpdateEvent() //this now picks, to rotate or to move
    {
        path.clear();
        createPath(m_robotPositionX, m_robotPositionY, m_targetPositionX, m_targetPositionY);

        //here should be dotted path method call

        double distance = distance(m_targetPositionX, m_targetPositionY, m_robotPositionX, m_robotPositionY);
        if (distance > 10) { //if target not reached.
            if (lookingAtTarget())
                moveRobot(maxVelocity, 0);
            else
                rotateRobot();
        } else {
            moveRobot(0, 0);
            gameWindow.getVisualizer().createNewTargetAndRedraw(randomPoint());
            pointsReached.incrementAndGet();
            Logger.debug("Цель достигнута.");
        }

        //here be code that should be run onModelUpdate but not connected to robot
    }

    private Point randomPoint() {
        double x = Math.random() * (gameWindow.getWidth() - 100) + 50;
        double y = Math.random() * (gameWindow.getHeight() - 100) + 50;
        Point result = new Point();
        result.setLocation(x, y);
        Logger.debug("H: " + gameWindow.getHeight());
        Logger.debug("w: " + gameWindow.getWidth());
        Logger.debug(result.toString());
        return result;
    }

    private void createPath(double fromX, double fromY, double toX, double toY) {
        double distance = distance(fromX, fromY, toX, toY);
        int amount = (int)Math.floor(distance / 10);
        double diffX = (toX - fromX) / amount;
        double diffY = (toY - fromY) / amount;
        double curX = fromX;
        double curY = fromY;

        while(path.size() < amount){
            curX += diffX;
            curY += diffY;
            Point point = new Point();
            point.setLocation(curX, curY);
            path.add(point);
        }
    }

    private static double applyLimits(double value, double min, double max)
    {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    private double angleFromRobot() {
        double angleToTarget = angleTo(m_robotPositionX, m_robotPositionY, m_targetPositionX, m_targetPositionY);
        return asNormalizedRadians(angleToTarget - m_robotDirection); //angle from robots perspective
    }

    private boolean lookingAtTarget()  { return rounded(angleFromRobot(), 1) == 0; }


    private void rotateRobot() {
        double angularVelocity;
        if (angleFromRobot() < Math.PI)
            angularVelocity = maxAngularVelocity; //turning left is closer
        else
            angularVelocity = -maxAngularVelocity; //turing right is closer
        moveRobot(0, angularVelocity);
    }


    private void moveRobot(double velocity, double angularVelocity)
    {
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

    static double rounded(double num, int accuracy) { //default accuracy is 0, look at overload below
        num = Math.floor(num * Math.pow(10,accuracy));
        return num / Math.pow(10,accuracy);
    }
}
