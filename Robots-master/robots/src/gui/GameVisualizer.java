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

import javax.swing.JPanel;

public class GameVisualizer extends JPanel
{

    protected double robX;
    protected double robY;
    protected double robAngle;
    protected int robTargetX;
    protected int robTargetY;
    private final RobotMovement rm;

    public GameVisualizer(RobotMovement robotMovement)
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
                rm.moveRobot();
            }
        }, 0, MainApplicationFrame.globalTimeConst);

        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                setTargetPosition(e.getPoint());
                repaint();
                rm.onModelUpdateEvent();
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

    protected void setTargetPosition(Point p)
    {
        rm.setTarget(p.x, p.y);
    }

    protected void onRedrawEvent()
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
        drawRobot(g2d, round(robX), round(robY), robAngle);
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
    
    private void drawRobot(Graphics2D g, int x, int y, double direction)
    {
        AffineTransform t = AffineTransform.getRotateInstance(direction, x, y); 
        g.setTransform(t);
        g.setColor(Color.MAGENTA);
        fillOval(g, x, y, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, x  + 10, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x  + 10, y, 5, 5);
    }
    
    private void drawTarget(Graphics2D g, int x, int y)
    {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0); 
        g.setTransform(t);
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }
}
