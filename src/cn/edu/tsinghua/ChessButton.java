package cn.edu.tsinghua;

import javax.swing.*;

public class ChessButton extends JButton {
	private static final long serialVersionUID = 1L;
	
	private int xpos, ypos;

    public ChessButton(int xpos, int ypos) {
        this.xpos = xpos;
        this.ypos = ypos;
    }

    public int getRow() {
        return xpos;
    }

    public int getCol() {
        return ypos;
    }

    public void setRow(int x) {
        this.xpos = x;
    }

    public void setCol(int y) {
        this.ypos = y;
    }
    
    public void setStatus(int val) {
    	if (val == 1) {
    		this.setText("°×");
    	} else if (val == -1) {
    		this.setText("ºÚ");
    	}
    }
}