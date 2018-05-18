package gui;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.Key;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;

public class GameWindow extends JInternalFrame
{

    private final GameVisualizer m_visualizer;
    private ArrayList<RobotMovement> robotList = new ArrayList<>();

    boolean gamePaused = false;

    GameWindow() {
        super("Игровое поле", true, true, true, true); //window
        addNewRobot();
        m_visualizer = new GameVisualizer(this);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();

        JMenuBar menu = new JMenuBar(); //topbar menu
        JButton pause = new JButton(); //pause
        try {
            Image img = ImageIO.read(getClass().getResource("/img/pause.png"));
            pause.setIcon(new ImageIcon(img));
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        pause.setMnemonic(KeyEvent.VK_SPACE);
        pause.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                pauseGame();
            }
        });

        JButton addRobot = new JButton(); // add robot
        try {
            Image img = ImageIO.read(getClass().getResource("/img/plus.png"));
            addRobot.setIcon(new ImageIcon(img));
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        addRobot.setMnemonic(KeyEvent.VK_N);
        addRobot.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                addNewRobot();
            }
        });

        menu.add(pause);
        menu.add(addRobot);

        this.setJMenuBar(menu);

    }
    private void pauseGame() {
        if(gamePaused)
           gamePaused = false;
        else gamePaused = true;
    }

    void addNewRobot() {
        robotList.add( new RobotMovement(this));
    }

    ArrayList<RobotMovement> getRobots() { return robotList; }
    GameVisualizer getVisualizer() {return m_visualizer; }
}
