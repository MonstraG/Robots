package gui;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;

class GameWindow extends JInternalFrame
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

        JButton removeRobot = new JButton(); //remove robot
        try {
            Image img = ImageIO.read(getClass().getResource("/img/minus.png"));
            removeRobot.setIcon(new ImageIcon(img));
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        removeRobot.setMnemonic(KeyEvent.VK_D);
        removeRobot.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                removeLastRobot();
            }
        });

        menu.add(pause);
        menu.add(addRobot);
        menu.add(removeRobot);

        this.setJMenuBar(menu);

    }
    private void pauseGame() {
        gamePaused = !gamePaused;
    }

    void addNewRobot() {
        robotList.add( new RobotMovement(this));
    }

    private void removeLastRobot() {
        if (robotList.size() > 0)
            robotList.remove(robotList.size() - 1);
    }

    ArrayList<RobotMovement> getRobots() { return robotList; }
    GameVisualizer getVisualizer() {return m_visualizer; }
}
