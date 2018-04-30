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
        String content = "X: " + rounded(robotMovement.m_robotPositionX, 2) + "\n" +
                "Y: " + rounded(robotMovement.m_robotPositionY, 2) + "\n" +
                "Угол: " + (int) (robotMovement.m_robotDirection / Math.PI * 180) + "° \n" +
                "X цели: " + robotMovement.m_targetPositionX + "\n" +
                "Y цели: " + robotMovement.m_targetPositionY + "\n";
        try {
            m_pos.setText(content);
        }
        catch (Exception e) {
            System.out.print(e.toString()); // It looks like very small window sizes give lots of errors.
        }
        m_pos.invalidate();
    }

    private double rounded(double num, int accuracy) { //default accuracy is 0, look at overload below
        num = Math.floor(num * Math.pow(10,accuracy));
        return num / Math.pow(10,accuracy);
    }

    private double rounded(double x) {
        return rounded(x, 0);
    }
}

