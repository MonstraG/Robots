package gui;

import java.awt.BorderLayout;
import java.awt.TextArea;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;


public class PosWindow extends JInternalFrame {
    private TextArea m_pos;

    public PosWindow(GameVisualizer visualizer) {
        super("Положение робота", true, true, true, true);
        m_pos = new TextArea("");
        m_pos.setSize(200, 100);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_pos, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
        updateInfo(visualizer);
        //TODO: updates and observe/observable link
    }

    public void updateInfo(GameVisualizer gv) {
        StringBuilder content = new StringBuilder();
        content.append("X: ").append(gv.robX).append("\n");
        content.append("Y: ").append(gv.robY).append("\n");
        content.append("Угол: ").append(gv.robAngle).append("\n");
        m_pos.setText(content.toString());
        m_pos.invalidate();
    }
}

