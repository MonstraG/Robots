package gui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

import javax.swing.*;

import com.sun.rmi.rmid.ExecPermission;
import log.Logger;
import sun.applet.Main;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается. 
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    
    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
            screenSize.width  - inset*2,
            screenSize.height - inset*2);

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

    protected void closeAll() {
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
            //clear pos.txt
            Component[] components = getContentPane().getComponents();
            for (Component component : components) {
                close(component);
            }
            System.exit(0);
        }
    }


    protected void close(Component component) {
        try {
            BufferedWriter br = new BufferedWriter(new FileWriter("pos.txt", true));
            br.write(component.getName() + " " + component.getX() + " " + component.getY() + " "
                    + component.getWidth() + " " + component.getHeight());
            br.newLine();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void read() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("pos.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                int converted[] = new int[5];
                for (int i = 0; i < 5; i++)
                    converted[i] = Integer.parseInt(parts[i]);
                if (converted[0] == 0) { // 0 - log
                    LogWindow logWindow = createLogWindow(converted[1],converted[2],converted[3],converted[4]);
                    addWindow(logWindow);
                    logWindow.setName("0");
                }
                if (converted[0] == 1) { // 1 - game
                    GameWindow gameWindow = createGameWindow(converted[1],converted[2],converted[3],converted[4]);
                    addWindow(gameWindow);
                    gameWindow.setName("1");
                }
            }
        } catch (Exception e) {
            createDefaultPair();
            Logger.debug("Ошибка чтения, созданы окна по-умолчанию.");
        } finally {
            File f = new File("pos.txt");
            try {
                PrintWriter writer = new PrintWriter(f);
                writer.close();
            } //clearing file and ignoring if doesn't exist.
            catch (FileNotFoundException e) {}
        }
    }

    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected LogWindow createLogWindow(int x, int y, int width, int heigh) {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(x,y);
        logWindow.setSize(width, heigh);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }


    protected GameWindow createGameWindow(int x, int y, int width, int heigh) {
        GameWindow gameWindow = new GameWindow();
        gameWindow.setLocation(x,y);
        gameWindow.setSize(width, heigh);
        return gameWindow;
    }

    protected void createDefaultPair() {
        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);
        logWindow.setName("0");
        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(400,  400);
        addWindow(gameWindow);
        gameWindow.setName("1");
    }

    
    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }



    private JMenuBar generateMenuBar()
    {

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Файл");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.getAccessibleContext().setAccessibleDescription(
                "Управление приложением");
        fileMenu.add(generateMenuItem("Новая пара окон", (event) -> createDefaultPair()));
        fileMenu.add(generateMenuItem("Выход", (event) -> closeAll()));
        
        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");
        lookAndFeelMenu.add(generateMenuItem(
                "Системная схема",
                (event) -> { setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    this.invalidate(); }));
        lookAndFeelMenu.add(generateMenuItem(
                "Универсальная схема",
                (event) -> { setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                    this.invalidate(); }));

        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription(
                "Тестовые команды");
        testMenu.add(generateMenuItem(
                "Сообщение в лог",
                (event) -> Logger.debug("Новая строка")));

        menuBar.add(fileMenu);
        menuBar.add(lookAndFeelMenu);
        menuBar.add(testMenu);
        return menuBar;
    }

    private JMenuItem generateMenuItem(String name, ActionListener action) {
        JMenuItem item = new JMenuItem(name, KeyEvent.VK_S);
        item.addActionListener(action);
        return item;
    }
    
    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }
}
