/*
 * Copyright (c) 2015-2016. DCIWE.com
 */

package com.dciwe.mcmaptool;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URISyntaxException;

/** Исполняемый класс.
 * Совокупность пользовательского интерфейса (окна),
 * обработка и исполнения кода в рамках объекта этого класса.
 **/
public class Mcmaptool {
    /**
     * Этот метод выполняеться первым, сразу после запуска программы
     */
    public static void main(String[] args) {
        Mcmaptool mcmaptool = new Mcmaptool(); /* создание объекта этого класса, запуск McMapTool, появляеться окно */
        mcmaptool.config.load(); /* Загрузка настроек программы */
    }

    /* Поля объекта, переменные */
    //public final java.util.Map<String, MapTile> tiles = Collections.synchronizedMap(new HashMap<String, MapTile>()); // для хранения карты в памяти
    public Listener listener = new Listener();
    public Dynmap dynmap = null;
    public final JFrame frame; // окно
    public _Content content; // компоненты окна
    public Config config; // настройки окна
    /*var for text chat*/
    public boolean chat_mousePressed;
    public boolean chat_chatMore = false;
    public int chat_x, chat_y;
    public boolean chat_resize = false;
    public boolean chat_cursor_mousePressed = false;
    public boolean chat_thread_running = false;
    /*var for action*/
    public boolean action_mousePressed = false;
    public int action_x = 0;
    public int action_y = 0;
    public int last_action_x = 0;
    public int last_action_y = 0;
    /*var for render*/
    public int render_x = 0;
    public int render_y = 0;
    public boolean render_first = true;
    public boolean render_map_first = true;
    public boolean thread_map_processing = false;
    public boolean drawing_map = false;
    public int spawn_x = 0;
    public int spawn_y = 0;
    /*var for frame*/
    public int frame_lastWidth = 0;
    public int frame_lastHeight = 0;
    /*var for north*/
    public boolean north_more_show = false;
    public boolean north_start = false;
    public boolean north_stop = false;

    /**
     * Конструктор класса, инициализация
     */
    public Mcmaptool() {
        content = new _Content(); /* Инициализация всех компонентов окна (поля, метки, списки, кнопки и т.п.) */
        config = new Config(); /* Создание объекта настроек со значениями по-умолчанию, далее настройки будут загружены в этот объект */

        /* Инициализация окна */
        frame = new JFrame("McMapTool " + Config.VERSION); /* Заголовок окна */
        frame.setDefaultCloseOperation(3); /* Завершить программу при закрытии окна */
        frame.setMinimumSize(new Dimension(700, 400)); /* Минимально допустимый размер окна */
        frame.setLocationRelativeTo(null); /* Разместить окно по центру экрана */
        frame.setContentPane(content); /* Все компоненты окна с которыми взаимодействует пользователь */
        frame.addWindowStateListener(new WindowStateListener() {
            @Override
            public void windowStateChanged(WindowEvent e) {
                int width = content.center.main.action.getWidth();
                int height = content.center.main.action.getHeight();
                render_x = render_x + ((width - frame_lastWidth) / 2);
                render_y = render_y + ((height - frame_lastHeight) / 2);
                frame_lastWidth = width;
                frame_lastHeight = height;
                content.center.main.action.repaint();

                if (content.center.main.action.chat.getHeight() >= content.center.main.action.getHeight()) {
                    content.center.main.action.chat.setPreferredSize(new Dimension(100, content.center.main.action.getHeight() - 1));
                    content.center.main.action.revalidate();
                    content.center.main.action.repaint();
                }
            }
        });
        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                int width = content.center.main.action.getWidth();
                int height = content.center.main.action.getHeight();
                render_x = render_x + ((width - frame_lastWidth) / 2);
                render_y = render_y + ((height - frame_lastHeight) / 2);
                frame_lastWidth = width;
                frame_lastHeight = height;
                content.center.main.action.repaint();

                if (content.center.main.action.chat.getHeight() >= content.center.main.action.getHeight()) {
                    content.center.main.action.chat.setPreferredSize(new Dimension(100, content.center.main.action.getHeight() - 1));
                    content.center.main.action.revalidate();
                    content.center.main.action.repaint();
                }
            }
        });
        frame.setVisible(true); /* Показать окно */

//        for (int i = 0; i < 100; i++) {
//            for (int j = 0; j < 100; j++) {
//                BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
//                Graphics g = image.getGraphics();
//                g.setColor(Color.GREEN);
//                g.drawRect(0, 0, 127, 127);
//                g.drawString(i + "_" + j, 10, 16);
//                int xx = (i + 1) * 128 - 128;
//                int yy = (j + 1) * 128 - 128;
//                MapTile tile = new MapTile(image, xx, yy);
//                synchronized (tiles) {
//                    tiles.put(xx + "_" + yy, tile);
//                }
//                g.dispose();
//                image.flush();
//            }
//        }
    }

    /**
     * Класс в котором описаны все компоненты окна
     */
    public class _Content extends JPanel {
        public _North north = new _North();
        public _Center center = new _Center();
        public _South south = new _South();
        public _CenterMore centerMore = new _CenterMore();
        public _CenterMoreAdd centerMoreAdd = new _CenterMoreAdd();
        public _CenterMoreChange centerMoreChange = new _CenterMoreChange();
        public _CenterMoreInfo centerMoreInfo = new _CenterMoreInfo();

        public _Content() {
            super(new BorderLayout());
            setBackground(new Color(27, 27, 27));
            add(north, BorderLayout.NORTH);
            add(center, BorderLayout.CENTER);
            add(south, BorderLayout.SOUTH);
        }

        public class _North extends JPanel {
            public _Start start = new _Start();
            public _URL url = new _URL();
            public _More more = new _More();

            public _North() {
                super(new BorderLayout());
                setBackground(new Color(27, 27, 27));
                add(start, BorderLayout.WEST);
                add(url, BorderLayout.CENTER);
                add(more, BorderLayout.EAST);
            }

            public class _Start extends JPanel {
                public JButton button = new JButton(new ImageIcon("resources/img/start.png"));

                public _Start() {
                    super(new BorderLayout());
                    setBackground(new Color(27, 127, 127));
                    setBorder(new EmptyBorder(3, 3, 3, 3));
                    button.setBackground(new Color(27, 127, 127));
                    button.setBorderPainted(false);
                    button.setMargin(new Insets(0, 0, 0, 0));
                    button.setFocusPainted(false);
                    button.setActionCommand("north_start");
                    button.addActionListener(listener);
                    add(button);
                }
            }

            public class _URL extends JPanel {
                public JTextField textField = new JTextField();

                public _URL() {
                    super(new BorderLayout());
                    setBackground(new Color(27, 127, 127));
                    setBorder(new EmptyBorder(3, 0, 3, 0));
                    textField.setMargin(new Insets(2, 2, 2, 2));
                    textField.setBorder(new CompoundBorder(
                            new LineBorder(new Color(17, 86, 86), 1),
                            new EmptyBorder(2, 2, 2, 2)));
                    add(textField);
                }
            }

            public class _More extends JPanel {
                public JButton button = new JButton(new ImageIcon("resources/img/more_d.png"));

                public _More() {
                    super(new BorderLayout());
                    setBackground(new Color(27, 127, 127));
                    setBorder(new EmptyBorder(3, 3, 3, 3));
                    button.setBackground(new Color(27, 127, 127));
                    button.setBorderPainted(false);
                    button.setMargin(new Insets(0, 0, 0, 0));
                    button.setFocusPainted(false);
                    button.setActionCommand("north_more");
                    button.addActionListener(listener);
                    add(button);
                }
            }
        }

        public class _Center extends JPanel {
            public _West west = new _West();
            public _Main main = new _Main();

            public _Center() {
                super(new BorderLayout());
                setBackground(new Color(27, 27, 27));
                add(west, BorderLayout.WEST);
                add(main, BorderLayout.CENTER);
            }

            public class _West extends JPanel {
                public _Online online = new _Online();
                public _Tabbed tabbed = new _Tabbed();
                public _Fun fun = new _Fun();

                public _West() {
                    super(new BorderLayout());
                    setBackground(new Color(12, 12, 12));
                    setBorder(new EmptyBorder(2, 3, 0, 4));
                    add(online, BorderLayout.NORTH);
                    add(tabbed, BorderLayout.CENTER);
                    add(fun, BorderLayout.SOUTH);
                }

                public class _Online extends JPanel {
                    public JLabel online = new JLabel(new ImageIcon("resources/img/players.png"));
                    public JLabel count = new JLabel("0 / 0");
                    public JLabel serverTime = new JLabel("00:00");

                    public _Online() {
                        super(new BorderLayout());
                        setBackground(new Color(12, 12, 12));
                        setBorder(new EmptyBorder(2, 2, 0, 2));

                        add(online, BorderLayout.WEST);

                        count.setHorizontalAlignment(SwingConstants.CENTER);
                        count.setForeground(new Color(198, 198, 198));
                        add(count, BorderLayout.CENTER);

                        serverTime.setForeground(new Color(198, 198, 198));
                        add(serverTime, BorderLayout.EAST);
                    }
                }

                public class _Tabbed extends JPanel {
                    public _PlayerList playerList = new _PlayerList();
                    public _Setting setting = new _Setting();
                    public JTabbedPane tabbedPane = new JTabbedPane();

                    public _Tabbed() {
                        super(new BorderLayout());
                        setBackground(new Color(27, 27, 27));
                        tabbedPane.addTab("players", playerList);
                        tabbedPane.addTab("setting", setting);
                        tabbedPane.setUI(new BasicTabbedPaneUI() {
                            @Override
                            protected void paintContentBorderRightEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {

                            }

                            @Override
                            protected void paintTab(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect) {

                            }

                            @Override
                            protected int calculateMaxTabHeight(int tabPlacement) {
                                return 0;
                            }

                            @Override
                            protected Insets getContentBorderInsets(int tabPlacement) {
                                return new Insets(-1, 0, 0, 0);
                            }
                        });
                        add(tabbedPane);
                    }

                    public class _PlayerList extends JPanel {
                        public DefaultListModel<String> listModel = new DefaultListModel<>();
                        public JList<String> list;

                        public _PlayerList() {
                            super(new BorderLayout());
                            setBackground(new Color(27, 27, 27));
                            setPreferredSize(new Dimension(190, 100));
                            list = new JList<>(listModel);
                            list.setBackground(new Color(45, 45, 45));
                            list.setForeground(new Color(140, 140, 140));
                            list.setCellRenderer(new DefaultListCellRenderer() {
                                @Override
                                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                                    JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                                    label.setForeground(Color.WHITE);

                                    if (isSelected) {
                                        label.setBackground(new Color(71, 71, 71));
                                        label.setForeground(new Color(220, 220, 220));
                                        label.setBorder(null);
                                    }

                                    return new JLabel("" + index);
                                }
                            });

                            JScrollPane scrollPane = new JScrollPane(list);
                            scrollPane.setBorder(null);
                            add(scrollPane);
                        }
                    }

                    public class _Setting extends JPanel {
                        public _Setting() {
                            super(new BorderLayout());
                            setBackground(new Color(27, 27, 27));
                        }
                    }
                }

                public class _Fun extends JPanel {
                    public _P1 p1 = new _P1();
                    public _P2 p2 = new _P2();
                    public _P3 p3 = new _P3();
                    public _P4 p4 = new _P4();
                    public _P5 p5 = new _P5();
                    public _P6 p6 = new _P6();

                    public _Fun() {
                        super(new GridLayout(1, 6));
                        setBackground(new Color(12, 12, 12));
                        add(p1);
                        add(p2);
                        add(p3);
                        add(p4);
                        add(p5);
                        add(p6);
                    }

                    public class _P1 extends JPanel {
                        public JButton button = new JButton(new ImageIcon("resources/img/button_him.png"));

                        public _P1() {
                            setBackground(new Color(12, 12, 12));
                            setBorder(new EmptyBorder(0, 0, 0, 0));
                            button.setBackground(new Color(240, 240, 240));
                            button.setBorderPainted(false);
                            button.setFocusPainted(false);
                            button.setMargin(new Insets(0, 0, 0, 0));
                            button.setActionCommand("center_west_fun_pl");
                            //button.addActionListener(listener);
                            add(button, BorderLayout.EAST);
                        }
                    }

                    public class _P2 extends JPanel {
                        public JButton button = new JButton(new ImageIcon("resources/img/button_settings.png"));

                        public _P2() {
                            setBackground(new Color(12, 12, 12));
                            setBorder(new EmptyBorder(0, 0, 0, 0));
                            button.setBackground(new Color(110, 110, 110));
                            button.setBorderPainted(false);
                            button.setFocusPainted(false);
                            button.setMargin(new Insets(0, 0, 0, 0));
                            button.setActionCommand("center_west_fun_pl");
                            //button.addActionListener(listener);
                            add(button, BorderLayout.EAST);
                        }
                    }

                    public class _P3 extends JPanel {
                        public JButton button = new JButton(new ImageIcon("resources/img/button_i.png"));

                        public _P3() {
                            setBackground(new Color(12, 12, 12));
                            setBorder(new EmptyBorder(0, 0, 0, 0));
                            button.setBackground(new Color(110, 110, 110));
                            button.setBorderPainted(false);
                            button.setFocusPainted(false);
                            button.setMargin(new Insets(0, 0, 0, 0));
                            button.setActionCommand("center_west_fun_pl");
                            //button.addActionListener(listener);
                            //add(button, BorderLayout.EAST);
                        }
                    }

                    public class _P4 extends JPanel {
                        public JButton button = new JButton(new ImageIcon("resources/img/button_i.png"));

                        public _P4() {
                            setBackground(new Color(12, 12, 12));
                            setBorder(new EmptyBorder(0, 0, 0, 0));
                            button.setBackground(new Color(110, 110, 110));
                            button.setBorderPainted(false);
                            button.setFocusPainted(false);
                            button.setMargin(new Insets(0, 0, 0, 0));
                            button.setActionCommand("center_west_fun_pl");
                            //button.addActionListener(listener);
                            //add(button, BorderLayout.EAST);
                        }
                    }

                    public class _P5 extends JPanel {
                        public JButton button = new JButton(new ImageIcon("resources/img/button_i.png"));

                        public _P5() {
                            setBackground(new Color(12, 12, 12));
                            setBorder(new EmptyBorder(0, 0, 0, 0));
                            button.setBackground(new Color(110, 110, 110));
                            button.setBorderPainted(false);
                            button.setFocusPainted(false);
                            button.setMargin(new Insets(0, 0, 0, 0));
                            button.setActionCommand("center_west_fun_pl");
                            //button.addActionListener(listener);
                            //add(button, BorderLayout.EAST);
                        }
                    }

                    public class _P6 extends JPanel {
                        public JButton button = new JButton(new ImageIcon("resources/img/sound2.png"));

                        public _P6() {
                            setBackground(new Color(12, 12, 12));
                            setBorder(new EmptyBorder(0, 0, 0, 0));
                            button.setBackground(new Color(57, 57, 57));
                            button.setBorderPainted(false);
                            button.setFocusPainted(false);
                            button.setMargin(new Insets(0, 0, 0, 0));
                            button.setActionCommand("center_west_fun_pl");
                            //button.addActionListener(listener);
                            add(button, BorderLayout.EAST);
                        }
                    }
                }
            }

            public class _Main extends JPanel {
                public _Action action = new _Action();
                public _SendMessage sendMessage = new _SendMessage();
                public _MapTool mapTool = new _MapTool();

                public _Main() {
                    super(new BorderLayout());
                    setBackground(new Color(27, 27, 27));
                    add(action, BorderLayout.CENTER);
                    add(sendMessage, BorderLayout.SOUTH);
                    add(mapTool, BorderLayout.EAST);
                }

                public class _Action extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
                    public _Chat chat = new _Chat();
                    public JPanel panel = new JPanel(null);

                    public _Action() {
                        super(new BorderLayout());
                        setBackground(new Color(27, 27, 27));
                        add(chat, BorderLayout.SOUTH);

                        panel.setSize(new Dimension(2000, 2000));
                        panel.setOpaque(false);
                        panel.addMouseMotionListener(this);
                        panel.addMouseListener(this);
                        panel.addMouseWheelListener(this);
                        add(panel);
                    }

                    public class _Chat extends JPanel {
                        private HTMLEditorKit kit = new HTMLEditorKit();
                        private HTMLDocument doc = new HTMLDocument();
                        private JTextPane textPane = new JTextPane();
                        private JPanel cursorPanel = new JPanel(new BorderLayout());

                        public _Chat() {
                            super(new BorderLayout());
                            setPreferredSize(new Dimension(100, 50));
                            setBackground(new Color(27, 27, 27, 128));
                            setBorder(new EmptyBorder(0, 0, 0, 0));

                            cursorPanel.setBackground(new Color(70, 70, 70, 176));
                            cursorPanel.setPreferredSize(new Dimension(100, 5));
                            cursorPanel.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
                            cursorPanel.addMouseListener(new MouseAdapter() {
                                @Override
                                public void mousePressed(final MouseEvent e) {
                                    if (!chat_cursor_mousePressed && e.getButton() == MouseEvent.BUTTON1) {
                                        chat_cursor_mousePressed = true;
                                        chat_resize = true;
                                        if (!chat_thread_running) {
                                            chat_thread_running = true;
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    while (chat_resize) {
                                                        int a_height = content.center.main.action.getHeight();
                                                        int ch_height = 0;
                                                        try {
                                                            ch_height = a_height - content.center.main.action.getMousePosition().y;
                                                        } catch (NullPointerException e) {
                                                            continue;
                                                        }
                                                        if (ch_height < 5) ch_height = 5;
                                                        if (ch_height >= a_height) ch_height = a_height - 1;
                                                        content.center.main.action.chat.setPreferredSize(new Dimension(100, ch_height));
                                                        content.center.main.action.revalidate();
                                                        content.center.main.action.repaint();
                                                    }
                                                    chat_thread_running = false;
                                                }
                                            }).start();
                                        }
                                    }
                                }

                                @Override
                                public void mouseReleased(MouseEvent e) {
                                    if (chat_cursor_mousePressed && e.getButton() == MouseEvent.BUTTON1) {
                                        chat_cursor_mousePressed = false;
                                        chat_resize = false;
                                    }
                                }

                                @Override
                                public void mouseExited(MouseEvent e) {
                                    //chat_resize = false;
                                }
                            });
                            add(cursorPanel, BorderLayout.NORTH);

                            textPane.setEditable(false);
                            textPane.setBackground(new Color(45, 45, 45, 128));
                            textPane.setForeground(Color.WHITE);
                            textPane.setBorder(new EmptyBorder(2, 2, 0, 2));
                            textPane.setFont(new Font(Font.DIALOG, Font.BOLD, 13));
                            textPane.setContentType("text/html");
                            textPane.setText("<html></html>");
                            textPane.setEditorKit(kit);
                            textPane.setDocument(doc);
                            textPane.addMouseListener(new MouseAdapter() {
                                @Override
                                public void mouseReleased(MouseEvent e) {
                                    super.mouseReleased(e);
                                    if (chat_mousePressed) {
                                        chat_mousePressed = false;
                                        if (e.getX() == chat_x && e.getY() == chat_y) {
                                            if (chat_chatMore) {
                                                chat_chatMore = false;
                                                content.center.main.action.removeAll();
                                                content.center.main.action.panel.removeAll();
                                                content.center.main.action.panel.setLayout(null);
                                                content.center.main.action.chat.cursorPanel.setVisible(true);
                                                content.center.main.action.add(content.center.main.action.panel);
                                                content.center.main.action.add(content.center.main.action.chat, BorderLayout.SOUTH);
                                                content.center.main.action.chat.setPreferredSize(new Dimension(100, 50));
                                                content.center.main.action.revalidate();
                                                content.center.main.action.repaint();
                                            } else {
                                                chat_chatMore = true;
                                                content.center.main.action.removeAll();
                                                content.center.main.action.panel.removeAll();
                                                content.center.main.action.panel.setLayout(new BorderLayout());
                                                content.center.main.action.chat.cursorPanel.setVisible(false);
                                                content.center.main.action.add(content.center.main.action.panel);
                                                content.center.main.action.panel.add(content.center.main.action.chat);
                                                content.center.main.action.revalidate();
                                                content.center.main.action.repaint();
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void mousePressed(MouseEvent e) {
                                    super.mousePressed(e);
                                    if (!chat_mousePressed) {
                                        chat_mousePressed = true;
                                        chat_x = e.getX();
                                        chat_y = e.getY();
                                    }
                                }
                            });

                            JScrollPane scrollPane = new JScrollPane(textPane);
                            scrollPane.setBorder(null);

                            add(scrollPane);
                        }
                    }

                    @Override
                    public void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        draw((Graphics2D) g);
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {

                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (!action_mousePressed && e.getButton() == MouseEvent.BUTTON1) {
                            action_mousePressed = true;
                            action_x = e.getX() - render_x;
                            action_y = e.getY() - render_y;
                        }
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (action_mousePressed && e.getButton() == MouseEvent.BUTTON1) {
                            action_mousePressed = false;
                        }
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {

                    }

                    @Override
                    public void mouseExited(MouseEvent e) {

                    }

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        if (action_mousePressed) {
                            render_x = e.getX() - action_x;
                            render_y = e.getY() - action_y;
                            content.center.main.action.repaint();
                        }
                    }

                    @Override
                    public void mouseMoved(MouseEvent e) {

                    }

                    @Override
                    public void mouseWheelMoved(MouseWheelEvent e) {

                    }
                }

                public class _SendMessage extends JPanel {
                    public JTextField field = new JTextField();

                    public _SendMessage() {
                        super(new BorderLayout());
                        setBackground(new Color(12, 12, 12));
                        setBorder(new EmptyBorder(5, 0, 5, 5));
                        field.setBorder(new CompoundBorder(new LineBorder(new Color(200, 200, 200)), new EmptyBorder(2, 2, 2, 2)));
                        field.setBackground(new Color(150, 150, 150));
                        field.setForeground(Color.BLACK);
                        field.setMargin(new Insets(4, 4, 4, 4));
                        field.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
                        add(field);
                    }
                }

                public class _MapTool extends JPanel {
                    public _MapTool() {
                        super(new BorderLayout());
                        setBackground(new Color(12, 12, 12));
                        setBorder(new EmptyBorder(0, 2, 2, 2));
                        setPreferredSize(new Dimension(30, 0));

                        JPanel panel = new JPanel(new FlowLayout());
                        panel.setBackground(new Color(12, 12, 12));

                        JButton button1 = new JButton(new ImageIcon("resources/img/tool_center.png"));
                        button1.setBackground(new Color(110, 110, 110));
                        button1.setBorderPainted(false);
                        button1.setFocusPainted(false);
                        button1.setMargin(new Insets(0, 0, 0, 0));
                        button1.setPreferredSize(new Dimension(20, 20));
                        button1.setActionCommand("center_main_maptool_btn1");
                        button1.addActionListener(listener);
                        button1.setToolTipText("Передвинуть карту в центр");
                        panel.add(button1);

                        JButton button2 = new JButton(new ImageIcon("resources/img/tool_center.png"));
                        button2.setBackground(new Color(110, 110, 110));
                        button2.setBorderPainted(false);
                        button2.setFocusPainted(false);
                        button2.setMargin(new Insets(0, 0, 0, 0));
                        button2.setPreferredSize(new Dimension(20, 20));
                        button2.setActionCommand("center_main_maptool_btn2");
                        //button2.addActionListener(listener);
                        button2.setToolTipText("Передвинуть карту в центр");
                        //panel.add(button2);

                        add(panel);
                    }
                }
            }
        }

        public class _South extends JPanel {
            public JLabel label = new JLabel();

            public _South() {
                super(new BorderLayout());
                setBackground(new Color(27, 71, 71));
                setBorder(new EmptyBorder(2, 4, 2, 4));
                setPreferredSize(new Dimension(22, 22));
                label.setForeground(Color.WHITE);
                label.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
                add(label);
            }
        }

        public class _CenterMore extends JPanel {
            public DefaultListModel<String> listModel = new DefaultListModel<>();
            public JList<String> list = new JList<>(listModel);
            public _ButtonPanel buttonPanel = new _ButtonPanel();

            public _CenterMore() {
                super(new BorderLayout());
                setBackground(new Color(7, 61, 61));
                setBorder(new EmptyBorder(10, 10, 0, 10));

                list.setBackground(new Color(0, 31, 31));
                list.setForeground(new Color(140, 140, 140));
                list.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
                list.setCellRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                        if (isSelected) {
                            label.setBackground(new Color(45, 120, 120));
                            label.setBorder(null);
                        }

                        if (!label.getText().isEmpty()) {
                            Config.ServerProfil profil = null;
                            synchronized (config.serverProfils) {
                                profil = config.serverProfils.get(Integer.parseInt(label.getText()));
                            }

                            BufferedImage image = new BufferedImage(
                                    content.centerMore.getWidth() - 22, 40, BufferedImage.TYPE_INT_ARGB);
                            Graphics g = image.getGraphics();

                            g.setColor(Color.WHITE);
                            g.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
                            g.drawString(profil.name, 5, 18);

                            g.setColor(new Color(190, 190, 190));
                            g.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
                            g.drawString(profil.url, 5, 34);

                            label.setText("");
                            label.setIcon(new ImageIcon(image));
                        }

                        return label;
                    }
                });
                list.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent evt) {
                        JList list = (JList) evt.getSource();
                        if (evt.getClickCount() == 2) {
                            // Double-click detected
                            int index = list.locationToIndex(evt.getPoint());
                            Config.ServerProfil profil = null;
                            synchronized (config.serverProfils) {
                                profil = config.serverProfils.get(index);
                            }
                            content.north.url.textField.setText(profil.url);
                        }
                    }
                });
                JScrollPane scrollPane = new JScrollPane(list);
                scrollPane.setBorder(null);
                scrollPane.setHorizontalScrollBar(null);
                add(scrollPane);

                add(buttonPanel, BorderLayout.SOUTH);
            }

            public class _ButtonPanel extends JPanel {
                public _Start start = new _Start();
                public _Add add = new _Add();
                public _Change change = new _Change();
                public _Del del = new _Del();
                public _Up up = new _Up();
                public _Down down = new _Down();

                public _ButtonPanel() {
                    super(new GridLayout(1, 8));
                    setBackground(new Color(7, 61, 61));
                    add(new _Empty());
                    add(start);
                    add(add);
                    add(change);
                    add(del);
                    add(up);
                    add(down);
                    add(new _Empty());
                }

                public class _Empty extends JPanel {
                    public _Empty() {
                        super(new BorderLayout());
                        setBackground(new Color(7, 61, 61));
                    }
                }

                public class _Start extends JPanel {
                    public JButton button = new JButton("Запустить");

                    public _Start() {
                        super(new BorderLayout());
                        setBackground(new Color(7, 61, 61));
                        setBorder(new EmptyBorder(5, 5, 5, 5));
                        button.setBackground(new Color(27, 127, 127));
                        button.setForeground(Color.WHITE);
                        button.setBorderPainted(false);
                        button.setMargin(new Insets(0, 0, 0, 0));
                        button.setFocusPainted(false);
                        add(button);
                    }
                }

                public class _Add extends JPanel {
                    public JButton button = new JButton("Добавить");

                    public _Add() {
                        super(new BorderLayout());
                        setBackground(new Color(7, 61, 61));
                        setBorder(new EmptyBorder(5, 5, 5, 5));
                        button.setBackground(new Color(27, 127, 127));
                        button.setForeground(Color.WHITE);
                        button.setBorderPainted(false);
                        button.setMargin(new Insets(0, 0, 0, 0));
                        button.setFocusPainted(false);
                        button.setActionCommand("centerMore_add");
                        button.addActionListener(listener);
                        add(button);
                    }
                }

                public class _Change extends JPanel {
                    public JButton button = new JButton("Изменить");

                    public _Change() {
                        super(new BorderLayout());
                        setBackground(new Color(7, 61, 61));
                        setBorder(new EmptyBorder(5, 5, 5, 5));
                        button.setBackground(new Color(27, 127, 127));
                        button.setForeground(Color.WHITE);
                        button.setBorderPainted(false);
                        button.setMargin(new Insets(0, 0, 0, 0));
                        button.setFocusPainted(false);
                        button.setActionCommand("centerMore_change");
                        button.addActionListener(listener);
                        add(button);
                    }
                }

                public class _Del extends JPanel {
                    public JButton button = new JButton("Удалить");

                    public _Del() {
                        super(new BorderLayout());
                        setBackground(new Color(7, 61, 61));
                        setBorder(new EmptyBorder(5, 5, 5, 5));
                        button.setBackground(new Color(27, 127, 127));
                        button.setForeground(Color.WHITE);
                        button.setBorderPainted(false);
                        button.setMargin(new Insets(0, 0, 0, 0));
                        button.setFocusPainted(false);
                        button.setActionCommand("centerMore_del");
                        button.addActionListener(listener);
                        add(button);
                    }
                }

                public class _Up extends JPanel {
                    public JButton button = new JButton("Вверх");

                    public _Up() {
                        super(new BorderLayout());
                        setBackground(new Color(7, 61, 61));
                        setBorder(new EmptyBorder(5, 5, 5, 5));
                        button.setBackground(new Color(27, 127, 127));
                        button.setForeground(Color.WHITE);
                        button.setBorderPainted(false);
                        button.setMargin(new Insets(0, 0, 0, 0));
                        button.setFocusPainted(false);
                        button.setActionCommand("centerMore_up");
                        button.addActionListener(listener);
                        add(button);
                    }
                }

                public class _Down extends JPanel {
                    public JButton button = new JButton("Вниз");

                    public _Down() {
                        super(new BorderLayout());
                        setBackground(new Color(7, 61, 61));
                        setBorder(new EmptyBorder(5, 5, 5, 5));
                        button.setBackground(new Color(27, 127, 127));
                        button.setForeground(Color.WHITE);
                        button.setBorderPainted(false);
                        button.setMargin(new Insets(0, 0, 0, 0));
                        button.setFocusPainted(false);
                        button.setActionCommand("centerMore_down");
                        button.addActionListener(listener);
                        add(button);
                    }
                }
            }
        }

        public class _CenterMoreAdd extends JPanel {
            public JTextField fieldName = new JTextField();
            public JTextField fieldURL = new JTextField();

            public _CenterMoreAdd() {
                super(new BorderLayout());
                setBackground(new Color(27, 27, 27));

                Box box = new Box(BoxLayout.X_AXIS);
                box.add(Box.createGlue());

                JPanel panel = new JPanel(new BorderLayout());
                panel.setBackground(new Color(7, 61, 61));
                panel.setBorder(new CompoundBorder(
                        new LineBorder(new Color(57, 197, 197)),
                        new EmptyBorder(8, 8, 8, 8)));
                panel.setPreferredSize(new Dimension(500, 136));
                panel.setMinimumSize(new Dimension(500, 136));
                panel.setMaximumSize(new Dimension(500, 136));
                box.add(panel, Box.createHorizontalStrut(50));

                JLabel title = new JLabel("Добавить");
                title.setForeground(Color.WHITE);
                title.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
                title.setHorizontalAlignment(SwingConstants.CENTER);
                panel.add(title, BorderLayout.NORTH);

                JPanel panelCenter = new JPanel(new GridLayout(2, 1));
                panelCenter.setBackground(new Color(7, 61, 61));
                panelCenter.setBorder(new EmptyBorder(8, 0, 8, 0));
                //
                JPanel panelName = new JPanel(new BorderLayout());
                panelName.setBackground(new Color(7, 61, 61));
                panelName.setBorder(new EmptyBorder(3, 0, 3, 0));
                JLabel titleName = new JLabel("Название");
                titleName.setPreferredSize(new Dimension(65, 20));
                titleName.setForeground(Color.WHITE);
                titleName.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
                panelName.add(titleName, BorderLayout.WEST);
                panelName.add(fieldName, BorderLayout.CENTER);
                panelCenter.add(panelName);
                //
                JPanel panelURL = new JPanel(new BorderLayout());
                panelURL.setBackground(new Color(7, 61, 61));
                panelURL.setBorder(new EmptyBorder(3, 0, 3, 0));
                JLabel titleURL = new JLabel("Адресс");
                titleURL.setPreferredSize(new Dimension(65, 20));
                titleURL.setForeground(Color.WHITE);
                titleURL.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
                panelURL.add(titleURL, BorderLayout.WEST);
                panelURL.add(fieldURL, BorderLayout.CENTER);
                panelCenter.add(panelURL);
                panel.add(panelCenter, BorderLayout.CENTER);

                JPanel panelButtons = new JPanel(new GridLayout(1, 4));
                panelButtons.add(new JLabel());
                panelButtons.setBackground(new Color(7, 61, 61));
                JPanel buttonPanelAdd = new JPanel(new BorderLayout());
                buttonPanelAdd.setBackground(new Color(7, 61, 61));
                buttonPanelAdd.setBorder(new EmptyBorder(2, 2, 2, 2));
                JButton add = new JButton("Добавить");
                add.setBackground(new Color(27, 127, 127));
                add.setForeground(Color.WHITE);
                add.setBorderPainted(false);
                add.setMargin(new Insets(0, 0, 0, 0));
                add.setFocusPainted(false);
                add.setActionCommand("centerMoreAdd_add");
                add.addActionListener(listener);
                buttonPanelAdd.add(add);
                panelButtons.add(buttonPanelAdd);
                JPanel buttonPanelCancel = new JPanel(new BorderLayout());
                buttonPanelCancel.setBackground(new Color(7, 61, 61));
                buttonPanelCancel.setBorder(new EmptyBorder(2, 2, 2, 2));
                JButton cancel = new JButton("Отмена");
                cancel.setBackground(new Color(27, 127, 127));
                cancel.setForeground(Color.WHITE);
                cancel.setBorderPainted(false);
                cancel.setMargin(new Insets(0, 0, 0, 0));
                cancel.setFocusPainted(false);
                cancel.setActionCommand("centerMoreAdd_cancel");
                cancel.addActionListener(listener);
                buttonPanelCancel.add(cancel);
                panelButtons.add(buttonPanelCancel);
                panelButtons.add(new JLabel());
                panel.add(panelButtons, BorderLayout.SOUTH);

                box.add(Box.createGlue());

                add(box);
            }
        }

        public class _CenterMoreChange extends JPanel {
            public JTextField fieldName = new JTextField();
            public JTextField fieldURL = new JTextField();

            public _CenterMoreChange() {
                super(new BorderLayout());
                setBackground(new Color(27, 27, 27));

                Box box = new Box(BoxLayout.X_AXIS);
                box.add(Box.createGlue());

                JPanel panel = new JPanel(new BorderLayout());
                panel.setBackground(new Color(7, 61, 61));
                panel.setBorder(new CompoundBorder(
                        new LineBorder(new Color(57, 197, 197)),
                        new EmptyBorder(8, 8, 8, 8)));
                panel.setPreferredSize(new Dimension(500, 136));
                panel.setMinimumSize(new Dimension(500, 136));
                panel.setMaximumSize(new Dimension(500, 136));
                box.add(panel, Box.createHorizontalStrut(50));

                JLabel title = new JLabel("Изменить");
                title.setForeground(Color.WHITE);
                title.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
                title.setHorizontalAlignment(SwingConstants.CENTER);
                panel.add(title, BorderLayout.NORTH);

                JPanel panelCenter = new JPanel(new GridLayout(2, 1));
                panelCenter.setBackground(new Color(7, 61, 61));
                panelCenter.setBorder(new EmptyBorder(8, 0, 8, 0));
                //
                JPanel panelName = new JPanel(new BorderLayout());
                panelName.setBackground(new Color(7, 61, 61));
                panelName.setBorder(new EmptyBorder(3, 0, 3, 0));
                JLabel titleName = new JLabel("Название");
                titleName.setPreferredSize(new Dimension(65, 20));
                titleName.setForeground(Color.WHITE);
                titleName.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
                panelName.add(titleName, BorderLayout.WEST);
                panelName.add(fieldName, BorderLayout.CENTER);
                panelCenter.add(panelName);
                //
                JPanel panelURL = new JPanel(new BorderLayout());
                panelURL.setBackground(new Color(7, 61, 61));
                panelURL.setBorder(new EmptyBorder(3, 0, 3, 0));
                JLabel titleURL = new JLabel("Адресс");
                titleURL.setPreferredSize(new Dimension(65, 20));
                titleURL.setForeground(Color.WHITE);
                titleURL.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
                panelURL.add(titleURL, BorderLayout.WEST);
                panelURL.add(fieldURL, BorderLayout.CENTER);
                panelCenter.add(panelURL);
                panel.add(panelCenter, BorderLayout.CENTER);

                JPanel panelButtons = new JPanel(new GridLayout(1, 4));
                panelButtons.add(new JLabel());
                panelButtons.setBackground(new Color(7, 61, 61));
                JPanel buttonPanelAdd = new JPanel(new BorderLayout());
                buttonPanelAdd.setBackground(new Color(7, 61, 61));
                buttonPanelAdd.setBorder(new EmptyBorder(2, 2, 2, 2));
                JButton add = new JButton("Изменить");
                add.setBackground(new Color(27, 127, 127));
                add.setForeground(Color.WHITE);
                add.setBorderPainted(false);
                add.setMargin(new Insets(0, 0, 0, 0));
                add.setFocusPainted(false);
                add.setActionCommand("centerMoreChange_change");
                add.addActionListener(listener);
                buttonPanelAdd.add(add);
                panelButtons.add(buttonPanelAdd);
                JPanel buttonPanelCancel = new JPanel(new BorderLayout());
                buttonPanelCancel.setBackground(new Color(7, 61, 61));
                buttonPanelCancel.setBorder(new EmptyBorder(2, 2, 2, 2));
                JButton cancel = new JButton("Отмена");
                cancel.setBackground(new Color(27, 127, 127));
                cancel.setForeground(Color.WHITE);
                cancel.setBorderPainted(false);
                cancel.setMargin(new Insets(0, 0, 0, 0));
                cancel.setFocusPainted(false);
                cancel.setActionCommand("centerMoreAdd_cancel");
                cancel.addActionListener(listener);
                buttonPanelCancel.add(cancel);
                panelButtons.add(buttonPanelCancel);
                panelButtons.add(new JLabel());
                panel.add(panelButtons, BorderLayout.SOUTH);

                box.add(Box.createGlue());

                add(box);
            }
        }

        public class _CenterMoreInfo extends JPanel {
            public JPanel panel = new JPanel(new BorderLayout());
            public JPanel panelText = new JPanel(new GridLayout(5, 1));
            public JLabel text1 = new JLabel();
            public JLabel text2 = new JLabel();
            public JLabel text3 = new JLabel();
            public JLabel text4 = new JLabel();
            public JLabel text5 = new JLabel();
            public JButton ok = new JButton("OK");

            public _CenterMoreInfo() {
                super(new BorderLayout());
                setBackground(new Color(27, 27, 27));

                Box box = new Box(BoxLayout.X_AXIS);
                box.add(Box.createGlue());

                panel.setBackground(new Color(7, 61, 61));
                panel.setBorder(new CompoundBorder(
                        new LineBorder(new Color(57, 197, 197)),
                        new EmptyBorder(8, 8, 8, 8)));
                panel.setPreferredSize(new Dimension(500, 136));
                panel.setMinimumSize(new Dimension(500, 136));
                panel.setMaximumSize(new Dimension(500, 136));
                box.add(panel, Box.createHorizontalStrut(50));

                panelText.setBackground(new Color(7, 61, 61));
                panelText.setBorder(new EmptyBorder(8, 0, 8, 0));
                //
                text1.setForeground(Color.WHITE);
                text1.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
                panelText.add(text1);
                //
                text2.setForeground(Color.WHITE);
                text2.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
                panelText.add(text2);
                //
                text3.setForeground(Color.WHITE);
                text3.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
                panelText.add(text3);
                //
                text4.setForeground(Color.WHITE);
                text4.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
                panelText.add(text4);
                //
                text5.setForeground(Color.WHITE);
                text5.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
                panelText.add(text5);
                panel.add(panelText);

                JPanel buttonPanel = new JPanel(new GridLayout(1, 5));
                buttonPanel.setBackground(new Color(7, 61, 61));
                buttonPanel.add(new JLabel());
                buttonPanel.add(new JLabel());
                JPanel buttonPanelOk = new JPanel(new BorderLayout());
                buttonPanelOk.setBackground(new Color(7, 61, 61));
                buttonPanelOk.setBorder(new EmptyBorder(2, 2, 2, 2));
                ok.setBackground(new Color(27, 127, 127));
                ok.setForeground(Color.WHITE);
                ok.setBorderPainted(false);
                ok.setMargin(new Insets(0, 0, 0, 0));
                ok.setFocusPainted(false);
                ok.setActionCommand("centerMoreInfo_ok");
                ok.addActionListener(listener);
                buttonPanelOk.add(ok);
                buttonPanel.add(buttonPanelOk);
                buttonPanel.add(new JLabel());
                buttonPanel.add(new JLabel());
                panel.add(buttonPanel, BorderLayout.SOUTH);

                box.add(Box.createGlue());

                add(box);
            }
        }
    }

    public class Listener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equals("north_start")) north_start();
            if (command.equals("north_more")) north_more();
            //            if (command.equals("centerMore_up")) centerMore_up();
            //            if (command.equals("centerMore_down")) centerMore_down();
            //            if (command.equals("centerMore_add")) centerMore_add(true);
            //            if (command.equals("centerMore_change")) centerMore_change();
            //            if (command.equals("centerMoreAdd_add")) centerMoreAdd_add();
            //            if (command.equals("centerMoreChange_change")) centerMoreChange_change();
            //            if (command.equals("centerMoreAdd_cancel")) centerMoreAdd_cancel();
            //            if (command.equals("centerMore_backFromInfoToAdd")) centerMore_backFromInfoToAdd();
            //            if (command.equals("centerMore_del")) centerMore_del();
            //            if (command.equals("center_west_fun_pl")) center_west_fun_pl();
            //            if (command.equals("center_west_fun_setting")) center_west_fun_setting();
            if (command.equals("center_main_maptool_btn1")) center_main_maptool_btn1();
        }
    }

    /**
     * Paint field for map
     */
    private void draw(Graphics2D g) {
        if (render_first) {
            render_x = content.center.main.action.getWidth() / 2 - spawn_x;
            render_y = content.center.main.action.getHeight() / 2 - spawn_y;
            render_first = false;
        }

        int x = render_x;
        int y = render_y;
        int center_x = -render_x + content.center.main.action.getWidth() / 2;
        int center_y = -render_y + content.center.main.action.getHeight() / 2;

        //spawn center
        g.setColor(Color.RED);
        g.drawLine(x + spawn_x, y + spawn_y, x + spawn_x + 10, y + spawn_y);
        g.drawLine(x + spawn_x, y + spawn_y, x + spawn_x, y + spawn_y + 10);
        g.drawLine(x + spawn_x - 10, y + spawn_y, x + spawn_x, y + spawn_y);
        g.drawLine(x + spawn_x, y + spawn_y - 10, x + spawn_x, y + spawn_y);
        g.drawString("Спавн", x + spawn_x + 2, y + spawn_y - 2);

        //action center
        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(x, y, x + 20, y);
        g.drawLine(x, y, x, y + 20);
        g.drawLine(x - 20, y, x, y);
        g.drawLine(x, y - 20, x, y);

        //-------------------------------------------------------------
        //if (dynmap != null) {
            int dynmap_zoom = 4;
            Dimension action_size = content.center.main.action.getSize(); //получение размера видимой области
            int i_zoom = getZoom(dynmap_zoom); //конв. из кол-во 'z' в цыфровой множитель
            int col_tile_w = (int) Math.ceil(action_size.getWidth() / 128d) + 3; //расчет кол-во видимых ячеек для отображения
            int col_tile_h = (int) Math.ceil(action_size.getHeight() / 128d) + 3;
            for (int yt = 0; yt < col_tile_h; yt++) {
                for (int xt = 0; xt < col_tile_w; xt++) {
                    int offset_tile_x = xt - (int) Math.floor(col_tile_w / 2d); //получение координат текущей ячейки относительно центра видимой области
                    int offset_tile_y = yt - (int) Math.floor(col_tile_h / 2d);
                    int offset_x = center_x + (offset_tile_x * 128); //из полученных координат ячейки, получаем смещенную координату центра видимой области
                    int offset_y = center_y + (offset_tile_y * 128);
                    int tile_in_b = (int) (128d / (16d / (double) i_zoom)); //получение кол-ва кв.блоков в одном тайле (1пиксель = 1блок при зуме 2)
                    //формирование полученных данных для адреса
                    int f_x, f_y, i_x, i_y;
                    if (offset_x >= 0) {
                        i_x = (int) (Math.floor(offset_x / (double) tile_in_b) * i_zoom);
                        f_x = (int) (Math.floor(i_x / 32d));
                    }
                    else {
                        i_x = (int) (Math.ceil(offset_x / (double) tile_in_b) * i_zoom);
                        f_x = (int) (Math.floor(i_x / 32d));
                    }
                    if (offset_y >= 0) {
                        i_y = (int) -(Math.floor(offset_y / (double) tile_in_b) * i_zoom);
                        f_y = (int) (Math.floor(i_y / 32d));
                    }
                    else {
                        i_y = (int) -(Math.ceil(offset_y / (double) tile_in_b) * i_zoom);
                        f_y = (int) (Math.floor(i_y / 32d));
                    }
                    //формирование адреса
                    String zoom = "zzzzzzzzzzzzzzzz".substring(0, dynmap_zoom).intern();
                    String typeTile = "flat";
                    if (!zoom.isEmpty()) zoom += "_";
                    String link = "maps/46_174_48_224_28565/tiles/world"+
                            "/"+typeTile+"/"+f_x+"_"+f_y+"/"+zoom+i_x+"_"+i_y+".png";
                    //String link = dynmap.options.url.server+dynmap.options.url.tiles+dynmap.options.defaultworld_name+
                    //         "/"+typeTile+"/0_0/0_0.png?"+dynmap.options.timestamp;
                    //System.out.println(link);

                    //запись ссылки и координат тайла в массив
                    int xx, yy;
                    if (x >= 0) xx = (int) (Math.floor(offset_x / 128) * 128);
                    else xx = (int) (Math.ceil(offset_x / 128) * 128);
                    if (y >= 0) yy = (int) (Math.floor(offset_y / 128) * 128);
                    else yy = (int) (Math.ceil(offset_y / 128) * 128);

                    g.drawImage(new ImageIcon(link).getImage(), xx + x, yy + y, null);
                    g.drawString(f_x+"_"+f_y+"/"+zoom+i_x+"_"+i_y, xx + x + 10, yy + y + 16);
                }
            }
        //}
        //-------------------------------------------------------------

        g.drawString("action_x: " + x, 3, 12);
        g.drawString("action_y: " + y, 3, 22);
        g.drawString("spawn_x: " + spawn_x, 3, 32);
        g.drawString("spawn_y: " + spawn_y, 3, 42);
        g.drawString("center_x: " + center_x, 3, 52);
        g.drawString("center_y: " + center_y, 3, 62);
    }

    private void north_start() {
        if (!north_start) {
            north_start = true;
            final String text = content.north.url.textField.getText();
            final Mcmaptool ui = this;
            if (!text.isEmpty()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        content.north.start.button.setIcon(new ImageIcon("resources/img/stop.png"));
                        content.north.url.textField.setEnabled(false);
                        if (text.contains("http://") && text.substring(0, 7).intern().equals("http://")
                                && text.length() > 7) {
                            URI uri = null;
                            try {
                                uri = new URI(text);
                            } catch (URISyntaxException ignored) {
                            }
                            if (uri != null) {
                                content.south.label.setText("Соединение...");
                                Dynmap.start(ui, text);
                            } else {
                                north_start = false;
                                north_stop = false;
                                content.north.start.button.setIcon(new ImageIcon("resources/img/start.png"));
                                content.north.more.button.setEnabled(false);
                                content.north.url.textField.setEnabled(true);
                                if (Dynmap.dynmap != null && Dynmap.dynmap.isConnecting())
                                    content.south.label.setText("");
                                else content.south.label.setText("Ошибка сети или введен неверный адресс!");
//                                synchronized (tiles) {
//                                    tiles.clear();
//                                }
                                content.center.main.action.repaint();
                                frame.setTitle("McMapTool " + Config.VERSION);
                            }
                        } else {
                            north_start = false;
                            north_stop = false;
                            content.north.start.button.setIcon(new ImageIcon("resources/img/start.png"));
                            content.north.more.button.setEnabled(true);
                            content.north.url.textField.setEnabled(true);
                            if (Dynmap.dynmap != null && Dynmap.dynmap.isConnecting()) content.south.label.setText("");
                            else content.south.label.setText("Ошибка сети или введен неверный адресс!");
//                            synchronized (tiles) {
//                                tiles.clear();
//                            }
                            content.center.main.action.repaint();
                            frame.setTitle("McMapTool " + Config.VERSION);
                        }
                    }
                }).start();
            } else north_start = false;
        } else {
            if (!north_stop) {
                north_stop = true;
                content.south.label.setText("Остановка...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (!Dynmap.dynmap.isStoped()) {
                            Dynmap.dynmap.stop();
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                        content.center.west.online.count.setText("0 / 0");
                        content.center.west.online.serverTime.setText("00:00");
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                content.center.west.tabbed.playerList.listModel.clear();
                            }
                        });
                        content.center.west.tabbed.playerList.repaint();
                        content.center.main.action.chat.textPane.setText("");
                        north_start = false;
                        north_stop = false;
                        content.north.start.button.setIcon(new ImageIcon("resources/img/start.png"));
                        content.north.more.button.setEnabled(true);
                        content.north.url.textField.setEnabled(true);
                        if (Dynmap.dynmap != null && Dynmap.dynmap.isConnecting()) content.south.label.setText("");
                        else content.south.label.setText("Ошибка сети или введен неверный адресс!");
//                        synchronized (tiles) {
//                            tiles.clear();
//                        }
                        content.center.main.action.repaint();
                        frame.setTitle("McMapTool " + Config.VERSION);
                    }
                }).start();
            }
        }
    }

    private void north_more() {
        if (!north_more_show) {
            content.removeAll();
            content.add(content.centerMore, BorderLayout.CENTER);
            content.add(content.north, BorderLayout.SOUTH);
            content.revalidate();
            content.repaint();
            content.north.more.button.setIcon(new ImageIcon("resources/img/more_u.png"));
            north_more_show = true;
        } else {
            content.removeAll();
            content.add(content.north, BorderLayout.NORTH);
            content.add(content.center);
            content.add(content.south, BorderLayout.SOUTH);
            content.revalidate();
            content.repaint();
            content.north.more.button.setIcon(new ImageIcon("resources/img/more_d.png"));
            north_more_show = false;
        }
    }

    private void center_main_maptool_btn1() {
        render_x = content.center.main.action.getWidth() / 2 - spawn_x;
        render_y = content.center.main.action.getHeight() / 2 - spawn_y;
        content.center.main.action.repaint();
    }

    private int getZoom(int colZ) {
        int res = 1;
        for (int i = 0; i < colZ; i++) {
            if (i == 0) res = 2;
            else res += res;
        }
        return res;

    }
}