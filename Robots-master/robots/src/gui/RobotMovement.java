package gui;

import log.Logger;
import obstacles.AbstractObstacle;


import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

class RobotMovement extends Observable {

    RobotMovement(GameWindow gw) {
        gameWindow = gw;
    }

    volatile CopyOnWriteArrayList<Point> path = new CopyOnWriteArrayList<>(); //dotted path to target



    double[] getRobotData() {
        return new double[] {m_robotPositionX, m_robotPositionY, m_robotDirection,
                            m_targetPositionX, m_targetPositionY};
    }

    void setTarget(int x, int y) {
        m_targetPositionX = x;
        m_targetPositionY = y;
        Point start = new Point((int)m_robotPositionX, (int)m_robotPositionY);
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
            HashMap<Point, ArrayList<Point>> graph = new HashMap<>();
            ArrayList<Point> graphPoints = new ArrayList<>();
            ArrayList<Line> collisionLines = new ArrayList<>();
            //clear everything

            //take all anchors from all objects, take all collision lines
            for (AbstractObstacle obs : gameWindow.getVisualizer().obstacles) {
                graphPoints.add(start);
                graphPoints.addAll(obs.getAnchors());
                graphPoints.add(target);
                collisionLines.addAll(obs.getCollisionPairs());
                if (collisionLines.isEmpty())
                    try {
                        throw new Exception("Obstacles are present, but have no collision");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            }

            //initialize graph verticies
            for (Point p : graphPoints) {
                graph.put(p, new ArrayList<>());
            }

            //add edges to graph
            for (int i = 0; i < graphPoints.size(); i++) {
                for (int j = i + 1; j < graphPoints.size(); j++) {
                    Point a = graphPoints.get(i);
                    Point b = graphPoints.get(j);
                    for (Line col : collisionLines) {
                        if (col.intersectsLine(new Line(a, b))) {
                            ArrayList<Point> next;
                            next = graph.get(a);
                            next.add(b);
                            graph.replace(a, next);
                            next = graph.get(b);
                            next.add(a);
                            graph.replace(b, next);
                        }
                    }
                }
            }

            for(Point each : graph.keySet()) {
                System.out.print(each.toString().replace("java.awt.Point", ""));
                System.out.print(": ");
                for(Point each2 : graph.get(each)) {
                    System.out.print(each2.toString().replace("java.awt.Point", ""));
                }
                System.out.println("");
            }
            System.out.println("");


            //run pathfinder
//            HashMap<Point, Double> dist = new HashMap<>();
//            HashMap<Point, Point> prev = new HashMap<>();
//            ArrayList<Point> queue = new ArrayList<>(graphPoints);
//            for(Point each : graphPoints)
//                dist.put(each, Double.POSITIVE_INFINITY);
//            if(!dist.containsKey(start))
//                dist.put(start, 0.0);
//            else
//                dist.replace(start, 0.0);
//            queue.add(start);
//
//            while(!queue.isEmpty()) {
//                Point p = new Point();
//                //find minimum in queue
//                dist.put(p, Double.POSITIVE_INFINITY);
//                for(Point each : queue)
//                    if (dist.containsKey(each))
//                        if (dist.get(each) < dist.get(p)) {
//                            p = each;
//                        }
//                    else
//                        if (distance(p.x, p.y, each.x, each.y) < dist.get(p))
//                            p = each;
//
//                queue.remove(p);
//
//                //find neighbors
//                double altDist;
//                    for (Point neighbor : graph.get(p)) {
//                            altDist = dist.get(p) + distance(neighbor.x, neighbor.y, p.x, p.y);
//                            if (altDist < dist.get(neighbor)) {
//                                dist.replace(neighbor, altDist);
//                                prev.put(neighbor, p);
//                            }
//                        }
//            }
//            //get path
//            Point next = target;
//            while (prev.containsKey(next)) {
//                path.add(0, next);
//                next = prev.get(next);
//            }
//            path.add(0, next);
//            //done

            //BFS
            ArrayList<Point> queue = new ArrayList<>();
            HashMap<Point, Double> dist = new HashMap<>();
            HashMap<Point, Point> prev = new HashMap<>();
            queue.add(start);
            for(Point each : graphPoints) {
                dist.put(each, Double.POSITIVE_INFINITY);
            }
            dist.replace(start, 0.0);

            while (!queue.isEmpty()) {
                Point current = queue.get(0);
                ArrayList<Point> adjacent = graph.get(current);
                queue.addAll(adjacent);
                for(Point each : adjacent) {
                    double currentDist = dist.get(each);
                    double newDist = dist.get(current) + distance(current.x, current.y, each.x, each.y);
                    if (newDist < currentDist) {
                        dist.put(current, distance(current.x, current.y, each.x, each.y));
                        prev.put(each, current);
                    }
                }
                //end for
                if (current.equals(target))
                    break;

                queue.remove(0);
            }
            Point next = target;
            while (prev.containsKey(next)) {
                path.add(0, next);
                next = prev.get(next);
            }
            path.add(0, next);
//            for(Point each : prev.keySet()) {
//                System.out.print(each.toString().replace("java.awt.Point", ""));
//                System.out.print(": ");
//                    System.out.print(prev.get(each).toString().replace("java.awt.Point", ""));
//                System.out.println("");
//            }

            if(prev.isEmpty()) {
                try {
                    throw  new Exception("Algorithm did nothing!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
