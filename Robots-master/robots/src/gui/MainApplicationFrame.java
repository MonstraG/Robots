package gui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import javax.swing.*;

import log.Logger;

class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    static final int globalTimeConst = 10;

    MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset, screenSize.width  - inset*2, screenSize.height - inset*2);
        setContentPane(desktopPane);

        read();

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeAll();
            }
        });
    }

    private void read() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("pos.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                int converted[] = new int[4];
                for (int i = 0; i < 4; i++)
                    converted[i] = Integer.parseInt(parts[i]);
                switch (parts[4]) {
                    case "log":
                        addWindow(createLogWindow(converted[0],converted[1],converted[2],converted[3]));
                        break;
                    case "game":
                        addWindow(createGameWindow(converted[0],converted[1],converted[2],converted[3]));
                        break;
                    default:
                        break;
                    //TODO: pos serialisation
                    //idea: game x y w h [ pos x y w h, log x y w h ] <- like this.

                }
            }
        } catch (Exception e) {
            createDefaultPair();
            Logger.debug("Ошибка: " + e.toString());
            Logger.debug("Созданы окна по-умолчанию.");
        }
    }

    private void closeAll() {
        Object[] options = {"Да", "Нет"};
        int dialog = new JOptionPane().showOptionDialog( this,
                "Вы уверены, что хотите выйти?",
                "Подтверждение",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);
        if (dialog == JOptionPane.YES_OPTION) {
            try {
                BufferedWriter br = new BufferedWriter(new FileWriter("pos.txt"));
                Component[] components = getContentPane().getComponents();
                for (Component component : components) {
                    br.append(component.getName()).append("\n")
                      .append(String.valueOf(component.getX())).append(" ")
                      .append(String.valueOf(component.getY())).append(" ")
                      .append(String.valueOf(component.getWidth())).append(" ")
                      .append(String.valueOf(component.getHeight())).append(" ");
                }
                br.flush();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }
    }

    private LogWindow createLogWindow(int x, int y, int width, int height) {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setName("log");
        logWindow.setLocation(x,y);
        logWindow.setSize(width, height);
        setMinimumSize(logWindow.getSize());
        Logger.debug("Протокол работает");
        return logWindow;
    }

    private LogWindow createLogWindow() { //default params
        return createLogWindow(10,10 ,300, 800);
    }

    private GameWindow createGameWindow(int x, int y, int width, int height) {
        GameWindow gameWindow = new GameWindow();
        gameWindow.setName("game");
        gameWindow.setLocation(x, y);
        gameWindow.setSize(width, height);
        addWindow(createPosWindow(gameWindow)); //create paired PosWindow
        return gameWindow;
    }

    private GameWindow createGameWindow() { //default params
        return createGameWindow(0, 0, 400, 400);
    }

    private PosWindow createPosWindow(GameWindow gw, int x, int y, int width, int height) {
        PosWindow posWindow = new PosWindow(gw.getRobotMovement());
        posWindow.setName("pos");
        posWindow.setLocation(x, y);
        posWindow.setSize(width, height);
        return posWindow;
    }

    private PosWindow createPosWindow(GameWindow gw) { //default params
        return createPosWindow(gw, 0, 0, 200, 100);
    }

    private void createDefaultPair() {
        addWindow(createLogWindow());
        addWindow(createGameWindow());
    }


    private void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private JMenuBar generateMenuBar()
    {

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Файл");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.getAccessibleContext().setAccessibleDescription("Управление приложением");
        generateMenuItem(fileMenu,"Новый лог", (event) -> addWindow(createLogWindow()));
        generateMenuItem(fileMenu,"Новая игра", (event) -> addWindow(createGameWindow()));
        generateMenuItem(fileMenu,"Новый набор окон", (event) -> createDefaultPair());
        generateMenuItem(fileMenu,"Выход", (event) -> closeAll());

        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription("Управление режимом отображения приложения");
        generateMenuItem(lookAndFeelMenu,"Системная схема", (event) ->
        { setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate(); });
        generateMenuItem(lookAndFeelMenu,"Универсальная схема", (event) ->
        { setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate(); });

        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription("Тестовые команды");
        generateMenuItem(testMenu,"Сообщение в лог", (event) -> Logger.debug("Новая строка"));

        menuBar.add(fileMenu);
        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        return menuBar;
    }

    private void generateMenuItem(JMenu menu, String name, ActionListener action) {
        JMenuItem item = new JMenuItem(name);
        item.addActionListener(action);
        menu.add(item);
    }

    private void setLookAndFeel(String className)
    {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException e)
        { /* Just ignore */ }
    }
}
