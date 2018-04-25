package gui;

import java.awt.BorderLayout;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class GameWindow extends JInternalFrame
{
    private final GameVisualizer m_visualizer;
    private final RobotMovement m_robotMovement;

    public GameWindow()
    {
        super("Игровое поле", true, true, true, true);
        m_robotMovement = new RobotMovement();
        m_visualizer = new GameVisualizer(m_robotMovement);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    public double[] getRobotPos() {
        return new double[] {m_visualizer.robX,
                             m_visualizer.robY,
                             m_visualizer.robAngle};
    }

    public GameVisualizer getVisualizer() {
        return m_visualizer;
    }

    public RobotMovement getRobotMovement() {
        return m_robotMovement;
    }
}
