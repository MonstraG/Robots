package gui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.*;

import log.Logger;

class MainApplicationFrame extends JFrame
{

    //TODO: creating new random points when reached mode;
    private final JDesktopPane desktopPane = new JDesktopPane();
    private HashMap<Component, Component> windowRegistry = new HashMap<>();

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
                int converted[] = new int[9];

                switch (parts[0]) {
                    case "log":
                        for (int i = 1; i < 5; i++) //x, y, w, h
                            converted[i] = Integer.parseInt(parts[i]);
                        addWindow(createLogWindow(converted[1],converted[2],converted[3],converted[4]));
                        break;
                    case "game":
                        for (int i = 1; i < 9; i++) // game(x,y,w,n) pos(x, y, w, h)
                            converted[i] = Integer.parseInt(parts[i]);
                        GameWindow gameWindow = createGameWindow(converted[1],converted[2],converted[3],converted[4]);
                        addWindow(gameWindow);
                        addWindow(createPosWindow(gameWindow, converted[5], converted[6], converted[7], converted[8]));
                        break;
                    default:
                        break;

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
                PrintWriter writer = new PrintWriter("pos.txt");
                writer.close(); // to clear file before writing
                BufferedWriter br = new BufferedWriter(new FileWriter("pos.txt"));
                for (Component component : windowRegistry.keySet()) {
                    br.append(component.getName()).append(" ")
                      .append(String.valueOf(component.getX())).append(" ")
                      .append(String.valueOf(component.getY())).append(" ")
                      .append(String.valueOf(component.getWidth())).append(" ")
                      .append(String.valueOf(component.getHeight())).append(" ");
                    if (component.getName().equals("game") ) { // if game get paired pos window
                        Component window = windowRegistry.get(component);
                        br.append(String.valueOf(window.getX())).append(" ")
                                .append(String.valueOf(window.getY())).append(" ")
                                .append(String.valueOf(window.getWidth())).append(" ")
                                .append(String.valueOf(window.getHeight())).append(" ");
                        //result should be
                        //game x y w h x y w h
                    }
                    br.append(" \n");
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
        windowRegistry.put(logWindow, null);
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
        PosWindow posWindow = createPosWindow(gameWindow);
        addWindow(posWindow); //create paired PosWindow
        windowRegistry.put(gameWindow, posWindow);
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
