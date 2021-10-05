package spyr;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class MainWindow {
    private final JMenuBar menuBar;
    private JPanel panelMain;
    private JTextArea songInfoGoesHereTextArea;
    private JTextField songField;
    private JButton addSongButton;
    private JSlider slider1;
    private JList<String> list1;
    private JButton pauseButton;
    private JTable recentSongTable;
    private JCheckBox loopCheckBox;
    private JTree tree1;
    private LivestreamWindow livestreamWindow;
    DefaultListModel<String> listModel = new DefaultListModel<>();
    SongManager songManager;
    private int playingIndex;

    // inline classes
    class SongPopup extends JPopupMenu {
        JMenuItem deleteSong;
        JMenuItem playSong;

        public SongPopup() {
            deleteSong = new JMenuItem("Remove from queue");
            playSong = new JMenuItem("Play from here");
            add(playSong);
            add(deleteSong);
            playSong.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!songManager.songTitleList.isEmpty()) {
                        App.audioPlayer.start(songManager.getSongUrl(list1.getSelectedIndex()));
                        playingIndex = list1.getSelectedIndex();
                    }
                }
            });
            deleteSong.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!listModel.isEmpty()) {
                        songManager.removeSong(list1.getSelectedIndex());
                        listModel.remove(list1.getSelectedIndex());
                    }
                }
            });
        }
    }

    class RecentsPopup extends JPopupMenu {
        JMenuItem deleteSong;
        JMenuItem addSong;

        public RecentsPopup() {
            deleteSong = new JMenuItem("Remove from recents");
            addSong = new JMenuItem("Add to queue");
            add(addSong);
            add(deleteSong);
            addSong.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (recentSongTable.getSelectedColumn() != -1) {
                        songManager.addExistingSong(songManager.configManager.getYoutubeId(recentSongTable.getSelectedRow()));
                        listModel.add(listModel.size(), songManager.songTitleList.get(listModel.size()));
                    }
                }
            });
            deleteSong.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (recentSongTable.getSelectedColumn() != -1) {
                        songManager.configManager.removeSongFromJson(recentSongTable.getSelectedRow());
                        refreshJList();
                    }
                }
            });
        }
    }

    class SongClickListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                doPop(e);
            }
        }

        public void mouseReleased(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                doPop(e);
            }
        }

        private void doPop(MouseEvent e) {
            SongPopup songPopup = new SongPopup();
            songPopup.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    class RecentsClickListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                doPop(e);
            }
        }

        public void mouseReleased(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                doPop(e);
            }
        }

        private void doPop(MouseEvent e) {
            RecentsPopup recentsPopup = new RecentsPopup();
            recentsPopup.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    // methods
    public void refreshJList() {
        //this is a bit unoptimized, should fix later
        list1.repaint();
        recentSongTable.repaint();
    }

    public int getPlayingIndex() {
        return playingIndex;
    }

    public void uptickPlayingIndex() {
        list1.setSelectedIndex(list1.getSelectedIndex() + 1);
        playingIndex++;
    }

    public void setPlaying() {
        pauseButton.setText("Pause");
    }

    public void removeSelectedSong() {
        int index = list1.getSelectedIndex();
        listModel.remove(index);
        songManager.removeSong(index);
    }

    public boolean isLoopEnabled() {
        return loopCheckBox.isSelected();
    }

    public MainWindow() {
        songManager = new SongManager(AudioQuality.HIGH);
        $$$setupUI$$$();
        addSongButton.putClientProperty("JButton.buttonType", "square");
        menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu playMenu = new JMenu("Play");
        menuBar.add(fileMenu);
        menuBar.add(playMenu);
        list1.addMouseListener(new SongClickListener());
        recentSongTable.addMouseListener(new RecentsClickListener());
        JMenuItem settingsMenuItem = new JMenuItem("Settings");
        JMenuItem livestreamMenuItem = new JMenuItem("Play livestream");
        settingsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SettingsWindow settingsWindow = new SettingsWindow();
                settingsWindow.setup();
            }
        });
        livestreamMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                livestreamWindow = new LivestreamWindow();
            }
        });
        fileMenu.add(settingsMenuItem);
        playMenu.add(livestreamMenuItem);
        // actionlisteners n' timers
        addSongButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String songQuery = songField.getText();
                if (SongManager.isYoutubeURL(songQuery)) {
                    songManager.addSongFromURL(songField.getText());
                    songField.setText("");
                    listModel.add(listModel.size(), songManager.songTitleList.get(songManager.songTitleList.size() - 1));
                } else {
                    try {
                        songManager.addSongLinkFromOdesli(songQuery);
                        songField.setText("");
                        listModel.add(listModel.size(), songManager.songTitleList.get(songManager.songTitleList.size() - 1));
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(panelMain, "Song must be a YouTube URL!");
                        ex.printStackTrace();
                    }

                }

            }
        });
        // can these two be merged?
        slider1.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                App.audioPlayer.setPosition((float) slider1.getValue() / 100);
            }
        });
        slider1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                App.audioPlayer.setPosition((float) slider1.getValue() / 100);
            }
        });
        Timer sliderTimer = new Timer(200, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: do this from AudioPlayer rather than polling it every 200ms
                slider1.setValue(App.audioPlayer.getPercentage());
            }
        });
        sliderTimer.start();

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (App.audioPlayer.isPlaying()) {
                    App.audioPlayer.pause();
                    pauseButton.setText("Play");
                } else if (pauseButton.getText().equals("Play")) {
                    App.audioPlayer.play();
                    pauseButton.setText("Pause");
                } else {
                    if (!songManager.songTitleList.isEmpty()) {
                        try {
                            App.audioPlayer.start(songManager.songURLList.get(list1.getSelectedIndex()));
                        } catch (IndexOutOfBoundsException ignored) {
                            App.audioPlayer.start(songManager.songURLList.get(0));
                        }
                        pauseButton.setText("Pause");
                    }
                }
            }
        });
        songField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (songField.getText().equals("Add song URLs here...")) {
                    songField.setText("");
                }
            }
        });
        songField.addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {
                if (songField.getText().isEmpty()) {
                    songField.setText("Add song URLs here...");
                }
            }

            @Override
            public void focusGained(FocusEvent e) {
                if (songField.getText().equals("Add song URLs here...")) {
                    songField.setText("");
                }
            }
        });
    }

    public void setTime(int percentage) {
        slider1.setValue(percentage);
    }

    public String getNextSongUrl() {
        return songManager.getNextSongUrl();
    }


    public void setup() {
        JFrame frame = new JFrame("Spyr");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                App.writeOutJson();
                App.audioPlayer.exit();
                App.embeddedMediaPlayerComponent.release();
                System.exit(0);
            }
        });
        frame.setContentPane(panelMain);
        frame.setPreferredSize(new Dimension(600, 600));
        frame.setJMenuBar(menuBar);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        panelMain = new JPanel();
        panelMain.setLayout(new GridLayoutManager(6, 13, new Insets(0, 0, 0, 0), -1, -1));
        panelMain.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JScrollPane scrollPane1 = new JScrollPane();
        panelMain.add(scrollPane1, new GridConstraints(0, 0, 4, 7, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane1.setViewportView(list1);
        songField = new JTextField();
        songField.setText("Add song URLs here...");
        panelMain.add(songField, new GridConstraints(4, 0, 1, 11, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        pauseButton = new JButton();
        pauseButton.setText("Start");
        panelMain.add(pauseButton, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        recentSongTable.setEnabled(true);
        recentSongTable.setFillsViewportHeight(false);
        panelMain.add(recentSongTable, new GridConstraints(1, 7, 3, 6, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        addSongButton = new JButton();
        addSongButton.setText("Add song!");
        panelMain.add(addSongButton, new GridConstraints(4, 11, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Recently Listened");
        panelMain.add(label1, new GridConstraints(0, 7, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        slider1 = new JSlider();
        slider1.setValue(0);
        panelMain.add(slider1, new GridConstraints(5, 2, 1, 11, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        loopCheckBox = new JCheckBox();
        loopCheckBox.setText("Loop");
        panelMain.add(loopCheckBox, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panelMain;
    }

    private void createUIComponents() {
        list1 = new JList<>(listModel);
        AbstractTableModel recentSongTableModel = new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return songManager.configManager.getNumSongs();
            }

            @Override
            public int getColumnCount() {
                return 2;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                if (columnIndex == 0) {
                    return songManager.configManager.getTitle(rowIndex);
                } else {
                    return songManager.configManager.getTimesListenedTo(rowIndex);
                }
            }
        };
        recentSongTable = new JTable(recentSongTableModel);
        recentSongTable.getColumnModel().getColumn(1).setMaxWidth(40);
    }
}