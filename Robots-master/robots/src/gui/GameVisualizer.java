package gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JPanel;

public class GameVisualizer extends JPanel
{

    double robX;
    double robY;
    double robAngle;
    private int robTargetX;
    private int robTargetY;
    private final RobotMovement rm;

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
                onRedrawEvent();
                rm.onModelUpdateEvent();
            }
        }, 0, MainApplicationFrame.globalTimeConst);

        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                createNewTargetAndRedraw(e.getPoint());
                rm.onModelUpdateEvent();
            }
        });
        setDoubleBuffered(true);
    }

    void createNewTargetAndRedraw(Point point) {
        setTargetPosition(point);
        repaint();
    }

    private void updateRobData() {
        robX = rm.getRobotData()[0];
        robY = rm.getRobotData()[1];
        robAngle = rm.getRobotData()[2];
        robTargetX = (int)rm.getRobotData()[3];
        robTargetY = (int)rm.getRobotData()[4];
    }

    private void setTargetPosition(Point p)
    {
        rm.setTarget(p.x, p.y);
    }

    private void onRedrawEvent()
    {
        EventQueue.invokeLater(this::repaint);
    }
    

    private static int round(double value)
    {
        return (int)(value + 0.5);
    }
    
    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        updateRobData();
        Graphics2D g2d = (Graphics2D)g; 
        drawRobotHead(g2d, round(robX), round(robY), robAngle);
        for (Point point : rm.path) { // draws path between robot and target
            drawPathPoint(g2d, point.x, point.y);
        }
        AtomicInteger counter = new AtomicInteger();
        for (counter.set(0); counter.intValue() < rm.pointsReached.intValue(); counter.incrementAndGet()) {
            //draw amount of bodies equal to reached points
            Point point = rm.bodyPos.get(counter.intValue());
            drawRobotBody(g2d, point.x, point.y, robAngle);
        }

        drawTarget(g2d, robTargetX, robTargetY);
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
        fillOval(g, x, y, 15, 15);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 15, 15);
        g.setColor(Color.WHITE);
        fillOval(g, x  + 10, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x  + 10, y, 5, 5);
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

    private void drawPathPoint(Graphics2D g, int x, int y) {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
        g.setTransform(t);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }

}
