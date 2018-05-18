package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
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

    double robX;
    double robY;
    double robAngle;
    private int robTargetX;
    private int robTargetY;
    private final RobotMovement rm;

    ArrayList<AbstractObstacle> obstacles = new ArrayList<>();

    GameVisualizer(RobotMovement robotMovement)
    {
        rm = robotMovement;
        updateRobData();

        Timer m_timer = new Timer("events generator", true);

        m_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                if(!rm.gameWindow.gamePaused) {
                    rm.onModelUpdateEvent();
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
                        setTargetPosition(e.getPoint());
                        if(!rm.gameWindow.gamePaused) {
                            rm.onModelUpdateEvent();
                        }
                        break;
                    }
                    case MouseEvent.BUTTON3: { //RMB, create obstacle
                        RectangleObstacle square = new RectangleObstacle(e.getPoint());
                        obstacles.add(square);
                        Point oldTarget;
                        if (rm.path.size() > 0) { //if there is path, get last in path
                            oldTarget = rm.path.get(rm.path.size() - 1);
                        } else { //else get wat now is considered "target"
                            oldTarget = new Point(robTargetX, robTargetY);
                        }
                        setTargetPosition(oldTarget);
                        break;
                    }
                    case MouseEvent.BUTTON2: { //MMB, remove last obstacle
                        obstacles.remove(obstacles.size() -1);
                        Point oldTarget = new Point(robTargetX, robTargetY);
                        setTargetPosition(oldTarget);
                        break;
                    }
                }

                repaint();
            }
        });

        setDoubleBuffered(true);
    }

    private void updateRobData() {
        robX = rm.getRobotData()[0];
        robY = rm.getRobotData()[1];
        robAngle = rm.getRobotData()[2];
        robTargetX = (int)rm.getRobotData()[3];
        robTargetY = (int)rm.getRobotData()[4];
    }

    void setTargetPosition(Point p) {
        while (!rm.setTarget(p.x, p.y)) { //try to create new target until succeed
            p = rm.randomPoint();
        }
    }

    private static int round(double value) { return (int)(value + 0.5); }
    
    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        updateRobData();
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
        Point previous = new Point(round(robX), round(robY));
        for (Point point : rm.path) { // draws path between robot and target
            drawPathPoint(g2d, point.x, point.y);
            drawPathLine(g2d, point.x, point.y, previous.x, previous.y);
            previous = point;
        }
        drawTarget(g2d, robTargetX, robTargetY);
        if (rm.path.size() > 0)
            drawTarget(g2d, rm.path.get(rm.path.size()- 1).x, rm.path.get(rm.path.size()- 1).y);
        drawRobotHead(g2d, round(robX), round(robY), robAngle);
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
