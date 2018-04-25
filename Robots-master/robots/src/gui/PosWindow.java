package gui;

import java.awt.BorderLayout;
import java.awt.TextArea;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;


public class PosWindow extends JInternalFrame implements Observer {
    private TextArea m_pos;
    private RobotMovement robotMovement;

    public PosWindow(RobotMovement rm) {
        super("Положение робота", true, true, true, true);
        robotMovement = rm;
        m_pos = new TextArea("");
        m_pos.setSize(200, 100);
        rm.addObserver(this);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_pos, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateInfo();
    }


    @Override
    public void update(Observable o, Object arg) {
        updateInfo();
    }


    public void updateInfo() {
        StringBuilder content = new StringBuilder();
        content.append("X: ").append(robotMovement.m_robotPositionX).append("\n");
        content.append("Y: ").append(robotMovement.m_robotPositionY).append("\n");
        content.append("Угол: ").append((int)(robotMovement.m_robotDirection / Math.PI * 180)).append("° \n");
        m_pos.setText(content.toString());
        m_pos.invalidate();
    }
}

