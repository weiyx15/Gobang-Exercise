package cn.edu.tsinghua;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EnumMap;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private static final int WIDTH = 600, HEIGHT = 600, numRows = 10, numCols = 10, numWins = 5;
	
	public static final EnumMap<Player, String> playerNames = new EnumMap<Player, String>(Player.class);	// Player Enum -> Player name String
	public static final EnumMap<Player, String> winMessages = new EnumMap<Player, String>(Player.class);	// Player Enum -> Player winning message String
	
	static {
		playerNames.put(Player.WHITE, "白");
		playerNames.put(Player.BLACK, "黑");
		
		winMessages.put(Player.WHITE, "白方获胜");
		winMessages.put(Player.BLACK, "黑方获胜");
	}
	
    private ChessState chess = new ChessState(numRows, numCols, numWins);          		// game status, the model

    private ChessButton[][] chessButtons = new ChessButton[numRows][numCols];	// chessboard, the view

    public MainFrame () {
        JButton resetButton = new JButton("reset"),
                redoButton = new JButton("redo"),
                saveButton = new JButton("save"),
        		loadButton = new JButton("load");

        resetButton.addActionListener(e -> {
        	chess.clear();
            for (int i = 0; i < numRows; ++i) {
                for (int j = 0; j < numCols; ++j) {
                    chessButtons[i][j].setText("");
                }
            }
        });
        
        redoButton.addActionListener(e -> {
        	if (chess.operationPathEmpty()) {
        		return;
        	}
        	Position pos = chess.popOperation();
        	int preX = pos.getX(), preY = pos.getY();
        	chessButtons[preX][preY].setText("");
        });
        
        saveButton.addActionListener(e -> {
        	JFileChooser jfc = new JFileChooser();
        	jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        	if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        		File saveFile = jfc.getSelectedFile();
        		ObjectOutputStream oos = null;
        		try {
					oos = new ObjectOutputStream(new FileOutputStream(saveFile));
					oos.writeObject(chess);
					JOptionPane.showMessageDialog(null, "保存成功");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} finally {
					try {
						oos.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
        	}
        });
        
        loadButton.addActionListener(e -> {
        	JFileChooser jfc = new JFileChooser();
        	jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        	if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        		File loadFile = jfc.getSelectedFile();
        		ObjectInputStream ois = null;
        		try {
					ois = new ObjectInputStream(new FileInputStream(loadFile));
					chess = (ChessState) ois.readObject();
					chess.initializeOperationPath();
					for (int i = 0; i < numRows; ++i) {
						for (int j = 0; j < numCols; ++j) {
							if (!chess.getPosition(i, j).equals(Player.EMPTY)) {
								chessButtons[i][j].setText(playerNames.get(chess.getPosition(i, j)));
							} else {
								chessButtons[i][j].setText("");
							}
						}
					}
					JOptionPane.showMessageDialog(null, "加载成功");
				} catch (IOException | ClassNotFoundException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				} finally {
					try {
						ois.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
        	}
        });

        JPanel topPanel = new JPanel(), centralPanel = new JPanel(new GridLayout(numRows, numCols));
        topPanel.add(resetButton);
        topPanel.add(redoButton);
        topPanel.add(saveButton);
        topPanel.add(loadButton);

        for (int i = 0; i < numRows; ++i) {
            for (int j = 0; j < numCols; ++j) {
                chessButtons[i][j] = new ChessButton(i, j);
                centralPanel.add(chessButtons[i][j]);
                chessButtons[i][j].addActionListener(e -> {
                    ChessButton cb = (ChessButton)e.getSource();
                    int x = cb.getRow(), y = cb.getCol();
                    if (!chess.getPosition(x, y).equals(Player.EMPTY)) {
                        return;
                    }
                    
                    Player player = chess.getPlayer();
                    chess.addOperation(x, y, player);
                    cb.setText(playerNames.get(player));
                    Player winner = chess.checkWinner();
                    if (!winner.equals(Player.EMPTY)) {
                    	JOptionPane.showMessageDialog(null, winMessages.get(winner));
                    	resetButton.doClick();
                        return;
                    }
                    if (chess.checkTie()) {
                    	JOptionPane.showMessageDialog(null, "平局");
                    	resetButton.doClick();
                    }
                });
            }
        }

        this.setLayout(new BorderLayout());
        this.add(topPanel, BorderLayout.NORTH);
        this.add(centralPanel, BorderLayout.CENTER);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setTitle("Gobang");
        this.setSize(WIDTH, HEIGHT);
        this.setVisible(true);
    }
    
    public static void main(String[] args) {
        new MainFrame();
    }
}