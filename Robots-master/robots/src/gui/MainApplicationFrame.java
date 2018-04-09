package gui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import javax.swing.*;

import log.Logger;

public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    
    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset, screenSize.width  - inset*2, screenSize.height - inset*2);
        setContentPane(desktopPane);

        read();

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

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
                switch (parts[5]) {
                    case "log":
                        addWindow(createLogWindow(converted[1],converted[2],converted[3],converted[4]));
                        break;
                    case "game":
                        addWindow(createGameWindow(converted[1],converted[2],converted[3],converted[4]));
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
                BufferedWriter br = new BufferedWriter(new FileWriter("pos.txt"));
                Component[] components = getContentPane().getComponents();
                for (Component component : components) {
                    br.append(component.getX() + " " + component.getY() + " "
                            + component.getWidth() + " " + component.getHeight() + " "
                            + component.getName() + "\n");
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
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    private LogWindow createLogWindow()
    {
        return createLogWindow(10,10 ,300, 800);
    }

    private GameWindow createGameWindow(int x, int y, int width, int height) {
        GameWindow gameWindow = new GameWindow();
        gameWindow.setName("game");
        gameWindow.setLocation(x,y);
        gameWindow.setSize(width, height);
        return gameWindow;
    }

    private GameWindow createGameWindow() {
        return createGameWindow(0, 0, 400, 400);
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
        generateMenuItem(fileMenu,"Новая пара окон", (event) -> createDefaultPair());
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