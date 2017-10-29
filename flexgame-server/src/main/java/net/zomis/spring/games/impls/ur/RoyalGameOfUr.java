package net.zomis.spring.games.impls.ur;

import java.util.Arrays;
import java.util.Random;

public class RoyalGameOfUr {
    private static final int NOT_ROLLED = -1;
    private static final int NO_WINNER = -1;
    private final Random random = new Random();
    public static final int EXIT = 15;

/*
// 0 = Outside
// 4 = First flower
Fxxx  Fx
xxxFxxxx
Fxxx  Fx
*/

    private final int[][] pieces; // pieces[PLAYER][PIECE]
    private int currentPlayer;
    private int roll = NOT_ROLLED;

    public RoyalGameOfUr() {
        this(0, NOT_ROLLED, new int[2][7]);
    }

    public RoyalGameOfUr(int currentPlayer, int roll, int[][] pieces) {
        this.currentPlayer = currentPlayer;
        this.roll = roll;
        this.pieces = pieces;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isRollTime() {
        return roll == NOT_ROLLED;
    }

    public int roll() {
        if (!isRollTime()) {
            throw new IllegalStateException("Not time to roll. Current roll is " + roll);
        }
        int sum = 0;
        for (int i = 0; i < 4; i++) {
            sum += random.nextBoolean() ? 1 : 0;
        }
        if (canMove(sum)) {
            this.roll = sum;
        } else {
            this.nextPlayer();
        }
        return sum;
    }

    private void nextPlayer() {
        this.currentPlayer = (this.currentPlayer + 1) % 2;
    }

    public boolean isMoveTime() {
        return roll > 0;
    }

    public int getRoll() {
        return roll;
    }

    public boolean canMove(int roll) {
        if (roll == 0) {
            return false;
        }
        // Loop through player's pieces and check if they can move `this.roll` steps.
        for (int i = 0; i < this.pieces[currentPlayer].length; i++) {
            int position = this.pieces[currentPlayer][i];
            int nextPosition = position + roll;

            if (canMoveTo(currentPlayer, nextPosition)) {
                return true;
            }
        }
        return false;
    }

    private boolean canMoveTo(int currentPlayer, int nextPosition) {
        if (nextPosition == EXIT) {
            return true;
        }
        if (nextPosition > EXIT) {
            return false;
        }
        if (nextPosition >= 5 && nextPosition <= EXIT - 3) {
            // Shared area. Can knock out except on flower.
            if (isFlower(nextPosition)) {
                for (int player = 0; player < this.pieces.length; player++) {
                    if (playerOccupies(player, nextPosition)) {
                        return false;
                    }
                }
                return true;
            } else {
                return !playerOccupies(currentPlayer, nextPosition);
            }
        } else {
            // Private area
            return !playerOccupies(currentPlayer, nextPosition);
        }
    }

    public boolean playerOccupies(int currentPlayer, int position) {
        for (int piece = 0; piece < this.pieces[currentPlayer].length; piece++) {
            if (this.pieces[currentPlayer][piece] == position) {
                return true;
            }
        }
        return false;
    }

    public boolean canMove(int playerIndex, int position, int steps) {
        return canMoveTo(playerIndex, position + steps);
    }

    public boolean move(int playerIndex, int position, int steps) {
        if (!canMove(playerIndex, position, steps)) {
            return false;
        }
        for (int i = 0; i < this.pieces[playerIndex].length; i++) {
            if (this.pieces[playerIndex][i] == position) {
                this.pieces[playerIndex][i] = position + steps;
                performKnockout(playerIndex, position + steps);
                this.roll = NOT_ROLLED;
                if (!isFlower(position + steps)) {
                    this.nextPlayer();
                }
                return true;
            }
        }
        return false;
    }

    private void performKnockout(int playerIndex, int position) {
        int opponent = (playerIndex + 1) % this.pieces.length;
        if (position <= 4 || position > 12) {
            return;
        }
        if (isFlower(position)) {
            return;
        }
        for (int j = 0; j < this.pieces[opponent].length; j++) {
            if (this.pieces[opponent][j] == position) {
                this.pieces[opponent][j] = 0;
            }
        }
    }

    public boolean isFlower(int position) {
        return (position == 4) || (position == 8) || (position == EXIT - 1);
    }

    public int[][] getPieces() {
        return pieces;
    }

    public boolean isFinished() {
        return getWinner() != NO_WINNER;
    }

    public int getWinner() {
        for (int i = 0; i < this.pieces.length; i++) {
            if (Arrays.stream(this.pieces[i]).allMatch(v -> v == EXIT)) {
                return i;
            }
        }
        return NO_WINNER;
    }

}
