package net.zomis.spring.games.ttt;

enum TTPlayer {
    X, O;
}
public class TTTGame {

    private final TTPlayer[][] board;
    private int moves = 0;

    public TTTGame() {
        this.board = new TTPlayer[3][];
        for (int i = 0; i < 3; i++) {
            this.board[i] = new TTPlayer[3];
        }
    }

    public void move(TTPlayer who, int x, int y) {
        board[y][x] = who;
        moves++;
    }

}
