package cn.edu.tsinghua;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * game state
 * @author weiyuxuan
 *
 */
public class ChessState implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final Position[] STEPS = {new Position(1, 0), new Position(0, 1), new Position(1, 1), new Position(1, -1)};
	
	private int numWins;						// winning condition
	
	private int counter;									// number of playing chess, counter = 0 mod 2 for white, counter = 1 mod 2 for black
	
	private Player[][] state;								// status of the chessboard, 1 for white; -1 for black; 0 for no chess
	
	private transient LinkedList<Position> operationPath; 	// path of operations, for `redo`, able to `save`/`load`
	
	public ChessState(int numRows, int numCols, int numWins) {
		this.numWins = numWins;
		this.counter = 0;
		this.state = new Player[numRows][numCols];
		for (Player[] arr: state) {
			Arrays.fill(arr, Player.EMPTY);
		}
		this.operationPath = new LinkedList<>();
	}
	
	/**
	 * initialize transient property {@code operationPath} after `readObject`
	 */
	public void initializeOperationPath() {
		operationPath = new LinkedList<>();
	}
	
	public void setPosition(int x, int y, Player player) {
		state[x][y] = player;
	}
	
	public Player getPosition(int x, int y) {
		return state[x][y];
	}
	
	public void clear() {
		counter = 0;
		for (Player[] arr: state) {
			Arrays.fill(arr, Player.EMPTY);
		}
		operationPath.clear();
	}
	
	public boolean operationPathEmpty() {
		return operationPath.isEmpty();
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param player
	 */
	public void addOperation(int x, int y, Player player) {
		++counter;
		state[x][y] = player;
		operationPath.add(new Position(x, y));
	}
	
	public Position popOperation() {
		--counter;
		Position pos = operationPath.removeLast();
		state[pos.getX()][pos.getY()] = Player.EMPTY;
		return pos;
	}
	
	/**
	 * Get which player to play according to {@code counter}
	 * @return player on current round
	 */
	public Player getPlayer() {
		return counter % 2 == 0? Player.WHITE: Player.BLACK;
	}
	
	public Player checkWinner() {
		int rows = state.length, cols = state[0].length, i = 0, j = 0, x = 0, y = 0, cnt = 0;
		for (i = 0; i < rows; ++i) {
			for (j = 0; j < cols; ++j) {
				if (state[i][j].equals(Player.EMPTY)) {
					continue;
				}
				for (Position step: STEPS) {
					x = i;
					y = j;
					cnt = 0;
					Player player = state[i][j];
					while (x >= 0 && x < rows && y >= 0 && y < cols) {
						if (state[x][y].equals(player)) {
							++cnt;
						} else {
							break;
						}
						if (cnt == numWins) {
							return player;
						}
						x += step.getX();
						y += step.getY();
					}
				}
			}
		}
		return Player.EMPTY;
	}
	
	public boolean checkTie() {
		return counter == state.length * state[0].length;
	}
}