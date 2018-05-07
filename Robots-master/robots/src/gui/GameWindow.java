package gui;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.imageio.ImageIO;
import javax.swing.*;

public class GameWindow extends JInternalFrame
{
    private final GameVisualizer m_visualizer;
    private final RobotMovement m_robotMovement;

    boolean gamePaused = false;

    GameWindow() {
        super("Игровое поле", true, true, true, true);
        m_robotMovement = new RobotMovement(this);
        m_visualizer = new GameVisualizer(m_robotMovement);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();

        JMenuBar menu = new JMenuBar();

        JButton pause = new JButton();
        try {
            Image img = ImageIO.read(getClass().getResource("/img/pause.png"));
            pause.setIcon(new ImageIcon(img));
        } catch (Exception ex) {
            System.out.println(ex);
        }


        pause.setMnemonic(KeyEvent.VK_SPACE);

        pause.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                pauseGame();
            }
        });
        menu.add(pause);

        this.setJMenuBar(menu);
    }
    private void pauseGame() {
        if(m_robotMovement.gameWindow.gamePaused)
            m_robotMovement.gameWindow.gamePaused = false;
        else
            m_robotMovement.gameWindow.gamePaused = true;
    }

    RobotMovement getRobotMovement() { return m_robotMovement; }
    GameVisualizer getVisualizer() {return m_visualizer; }
}
