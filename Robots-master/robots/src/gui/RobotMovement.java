package gui;

import log.Logger;
import obstacles.AbstractObstacle;


import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

class RobotMovement extends Observable {

    RobotMovement(GameWindow gw) {
        gameWindow = gw;
    }

    HashMap<Point, ArrayList<Point>> pathGraph = new HashMap<>();
    ArrayList<Point> graphPoints = new ArrayList<>();
    ArrayList<Point> obstaclePoints = new ArrayList<>();
    ArrayList<Line> collisionLines = new ArrayList<>();

    double[] getRobotData() {
        return new double[] {m_robotPositionX, m_robotPositionY, m_robotDirection,
                            m_targetPositionX, m_targetPositionY};
    }

    void setTarget(int x, int y) {
        m_targetPositionX = x;
        m_targetPositionY = y;
        Point target = new Point(x, y);

        //path
        path.clear();
        if (gameWindow.getVisualizer().obstacles.size() == 0) //if no obstacles
        {
            double distance = distance(m_robotPositionX, m_robotPositionY, x, y);
            int amount = (int) Math.floor(distance / 20);
            double diffX = (x - m_robotPositionX) / amount;
            double diffY = (y - m_robotPositionY) / amount;
            double curX = m_robotPositionX;
            double curY = m_robotPositionY;

            while (path.size() < amount) {
                Point point = new Point();
                curX += diffX;
                curY += diffY;
                point.setLocation(curX, curY);
                path.add(point);
            }
            path.add(new Point(x, y));
        }
        else //if obstacles are present
        {
            //clear everything
            pathGraph.clear();
            graphPoints.clear();
            obstaclePoints.clear();
            collisionLines.clear();

            //initialize graph without edges
            pathGraph.put(new Point((int)m_robotPositionX, (int)m_robotPositionY), new ArrayList<>());
            for(Point p : graphPoints) {
                pathGraph.put(p,new ArrayList<>());
            }
            pathGraph.put(target, new ArrayList<>());

            //take all anchors from all objects, take all collision lines
            for (AbstractObstacle obs : gameWindow.getVisualizer().obstacles) {
                graphPoints.addAll(obs.getAnchors());
                collisionLines.addAll(obs.getCollisionPairs());
            }

            //take all collision lines

            //create edges
            for(Point i : graphPoints) {
                for(Point j : graphPoints) {
                    if (i != j) { //ignore loops
                        Line edge = new Line(i, j);
                        for (Line col : collisionLines) {
                            if (!edge.intersect(col)) {
                                pathGraph.get(i).add(j);
                            }
                        }
                    }
                }
            }
            //it will run SO SLOOOW, I'm sure.

            //run pathfinder
            //TODO: add pathfinder

            //done
        }

        updateTarget();
    }

    void updateTarget() {
        m_targetPositionX = path.get(0).x;
        m_targetPositionY = path.get(0).y;
    }

    final GameWindow gameWindow;

    volatile double m_robotPositionX = 100;
    volatile double m_robotPositionY = 100;
    volatile double m_robotDirection = 0;

    volatile double m_targetPositionX = 150;
    volatile double m_targetPositionY = 100;

    private static final double maxVelocity = 0.1;
    private static final double maxAngularVelocity = 0.002;

    volatile CopyOnWriteArrayList<Point> path = new CopyOnWriteArrayList<>(); //dotted path to target
    volatile AtomicInteger pointsReached = new AtomicInteger(0);

    static double distance(double x1, double y1, double x2, double y2)
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

    void onModelUpdateEvent()
    {
        pointIsReached();
        //here be code that should be run onModelUpdate but not connected to robot
    }

    void pointIsReached() {
        double distance;
        //checking for points from path
        if (path.size() > 0) { //if points do exist
            distance = distance(m_robotPositionX, m_robotPositionY, path.get(0).x, path.get(0).y);
            if (distance < 20) { //if close enough
                path.remove(0);
                if (path.size() > 0) {
                    updateTarget();
                    gameWindow.getVisualizer().repaint();
                }
                else
                    gameWindow.getVisualizer().createNewTargetAndRedraw(randomPoint());
            }
        }

        distance = distance(m_targetPositionX, m_targetPositionY, m_robotPositionX, m_robotPositionY);
        double distanceAtMaxSpeed = maxVelocity * MainApplicationFrame.globalTimeConst;
        if (distance > 10) { //if target not reached, picks rotate or move
            if (lookingAtTarget()) {
                if (distance > distanceAtMaxSpeed) //if far enough, move at max speed
                    moveRobot(maxVelocity, 0);
                else {
                    double velocity = maxAngularVelocity * (0.5 + Math.sqrt(25 * distance/distanceAtMaxSpeed));//50% - 100%
                    moveRobot(velocity, 0);
                }
            }
            else // if not lookingAtTarget
                rotateRobot();
        } else { //if too close
            moveRobot(0, 0);
            Point target = randomPoint();
            gameWindow.getVisualizer().createNewTargetAndRedraw(target);
            pointsReached.incrementAndGet();
            Logger.debug("Цель достигнута.");
        }
    }

    private Point randomPoint() { //create new target somewhere inside game window
        double x = Math.random() * (gameWindow.getWidth() - 100) + 50;
        double y = Math.random() * (gameWindow.getHeight() - 100) + 50;
        Point result = new Point();
        result.setLocation(x, y);
        return result;
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
        int rotation;
        double angle = angleFromRobot();
        if (angle < Math.PI)
            rotation = 1; //turning left is closer
        else {
            rotation = -1; //turing right is closer
            angle -= Math.PI;
        }
        angularVelocity = rotation * maxAngularVelocity * (0.5 + Math.sqrt(25 * angle/Math.PI)); //50% - 100%
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
