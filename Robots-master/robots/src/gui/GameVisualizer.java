package gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;
import obstacles.*;

public class GameVisualizer extends JPanel
{
    private final GameWindow gw;

    ArrayList<AbstractObstacle> obstacles = new ArrayList<>();

    GameVisualizer(GameWindow gameWindow)
    {
        gw = gameWindow;

        Timer m_timer = new Timer("events generator", true);

        m_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                if(!gw.gamePaused) {
                   for(RobotMovement each : gw.getRobots()) {
                       each.onModelUpdateEvent();
                   }
                }
                repaint();
            }
        }, 0, MainApplicationFrame.globalTimeConst);

        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                int mButton = e.getButton();
                switch (mButton) {
                    case MouseEvent.BUTTON1: { //LMB, create target
                        for(RobotMovement each : gw.getRobots())
                            setTargetPosition(each, e.getPoint());
                        if(!gw.gamePaused) {
                            for(RobotMovement each : gw.getRobots()) {
                                each.onModelUpdateEvent();
                            }
                        }
                        break;
                    }
                    case MouseEvent.BUTTON3: { //RMB, create obstacle
                        RectangleObstacle square = new RectangleObstacle(e.getPoint());
                        obstacles.add(square);
                        Point oldTarget;
                        for(RobotMovement each : gw.getRobots()) {
                            if (each.path.size() > 0) { //if there is path, get last in path
                                oldTarget = each.path.get(each.path.size() - 1);
                            } else { //else get wat now is considered "target"
                                oldTarget = new Point((int)each.m_targetPositionX, (int)each.m_robotPositionY);
                            }
                            setTargetPosition(each, oldTarget);
                        }

                        break;
                    }
                    case MouseEvent.BUTTON2: { //MMB, remove last obstacle
                        obstacles.remove(obstacles.size() -1);
                        for(RobotMovement each : gw.getRobots()) {
                            Point oldTarget = new Point((int)each.m_targetPositionX,(int)each.m_robotPositionY);
                            setTargetPosition(each, oldTarget);
                        }

                        break;
                    }
                }

                repaint();
            }
        });

        setDoubleBuffered(true);
    }

    void setTargetPosition(RobotMovement rm, Point p) {
        while (!rm.setTarget(p.x, p.y)) { //try to create new target until succeed
            p = rm.randomPoint();
        }
    }

    private static int round(double value) { return (int)(value + 0.5); }
    
    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;

        for (AbstractObstacle obstacle : obstacles) {
            String obsType = obstacle.getType();
            switch (obsType) {
                case "square":
                    drawRectangle(g2d, (RectangleObstacle)obstacle);
                    break;
                default:
                    break;
            }
        }
        for(RobotMovement each : gw.getRobots()) {
            Point previous = new Point(round(each.m_robotPositionX), round(each.m_robotPositionY));
            for (Point point : each.path) { // draws path between robot and target
                drawPathPoint(g2d, point.x, point.y);
                drawPathLine(g2d, point.x, point.y, previous.x, previous.y);
                previous = point;
            }
            drawTarget(g2d, (int)each.m_targetPositionX,(int)each.m_targetPositionY);
            if (each.path.size() > 0)
                drawTarget(g2d, each.path.get(each.path.size() - 1).x, each.path.get(each.path.size() - 1).y);
            drawRobotHead(g2d, round(each.m_robotPositionX), round(each.m_robotPositionY), each.m_robotDirection);
        }
    }
    
    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }
    
    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2)
    {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }
    
    private void drawRobotHead(Graphics2D g, int x, int y, double direction)
    {
        AffineTransform t = AffineTransform.getRotateInstance(direction, x, y); 
        g.setTransform(t);
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 10, 10);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 10, 10);
        g.setColor(Color.WHITE);
        fillOval(g, x  + 6, y, 4, 4);
        g.setColor(Color.BLACK);
        drawOval(g, x  + 6, y, 4, 4);
    }

    private void drawRobotBody(Graphics2D g, int x, int y, double direction) {
        AffineTransform t = AffineTransform.getRotateInstance(direction, x, y);
        g.setTransform(t);
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 12, 12);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 12, 12);
    }
    
    private void drawTarget(Graphics2D g, int x, int y)
    {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0); 
        g.setTransform(t);
        g.setColor(Color.RED);
        fillOval(g, x, y, 7, 7);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 7, 7);
    }

    private void drawPathLine(Graphics2D g, int x1, int y1, int x2, int y2) {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
        g.setTransform(t);
        g.setColor(Color.BLUE);
        g.drawLine(x1, y1, x2, y2);

    }

    private void drawPathPoint(Graphics2D g, int x, int y) {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
        g.setTransform(t);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }

    private void drawRectangle(Graphics2D g, RectangleObstacle square) {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
        g.setTransform(t);
        Point center = square.getPosition();
        int size = square.getSize();
        g.setColor(Color.BLUE);
        g.fillRect(center.x - size / 2, center.y - size / 2, size, size);
        g.setColor(Color.BLACK);
        g.drawRect(center.x - size / 2, center.y - size / 2, size, size);
    }
}
