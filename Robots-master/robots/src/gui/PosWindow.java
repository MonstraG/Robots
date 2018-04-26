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

    PosWindow(RobotMovement rm) {
        super("Положение робота", true, true, true, true);
        robotMovement = rm;
        m_pos = new TextArea("");
        m_pos.setSize(200, 300);
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


    private void updateInfo() {
        String content = "X: " + robotMovement.m_robotPositionX + "\n" +
                "Y: " + robotMovement.m_robotPositionY + "\n" +
                "Угол: " + (int) (robotMovement.m_robotDirection / Math.PI * 180) + "° \n" +
                "X цели: " + robotMovement.m_targetPositionX + "\n" +
                "Y цели: " + robotMovement.m_targetPositionY + "\n";
        m_pos.setText(content);
        m_pos.invalidate();
    }
}

