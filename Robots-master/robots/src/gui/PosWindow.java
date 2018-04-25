package gui;

import java.awt.BorderLayout;
import java.awt.TextArea;
import java.util.Observable;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;


public class PosWindow extends JInternalFrame {
    private TextArea m_pos;

    private class PosObservance extends Observable {

    }

    public PosWindow(GameWindow gameWindow) {
        super("Положение робота", true, true, true, true);
        m_pos = new TextArea("");
        m_pos.setSize(200, 100);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_pos, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateInfo(gameWindow);
        //TODO: updates and observe/observable link
    }

    public void updateInfo(GameWindow gameWindow) {
        double[] result = gameWindow.GetRobotPos();
        StringBuilder content = new StringBuilder();
        content.append("X: ").append(result[0]).append("\n");
        content.append("Y: ").append(result[1]).append("\n");
        content.append("Угол: ").append(result[2]).append("\n");
        m_pos.setText(content.toString());
        m_pos.invalidate();
    }
}

