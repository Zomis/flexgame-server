package net.zomis.spring.games.fx;

import com.sun.rmi.rmid.ExecOptionPermission;
import net.zomis.tttultimate.TTBase;
import net.zomis.tttultimate.TTFactories;
import net.zomis.tttultimate.TTPlayer;
import net.zomis.tttultimate.games.TTController;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class ClickAndTurnGame extends TTController {

    private Function<TTBase, Set<TTBase>> affectFunction;

    public ClickAndTurnGame(TTBase board, Function<TTBase, Set<TTBase>> affectFunction) {
        super(board);
        this.affectFunction = affectFunction;
        this.onReset();
    }

    @Override
    public boolean isAllowedPlay(TTBase ttBase) {
        return true;
    }




    @Override
    protected boolean performPlay(TTBase ttBase) {
        TTBase parent = ttBase.getParent();
        Set<TTBase> affected = affectFunction.apply(ttBase);

        affected.stream().filter(Objects::nonNull).forEach(aff -> aff.setPlayedBy(aff.getWonBy().next()));

        if (checkWin()) {
            parent.setPlayedBy(ttBase.getWonBy());
        }
        return true;
    }

    public boolean checkWin() {
        TTBase board = this.getGame();
        for (int y = 0; y < board.getSizeY(); y++) {
            for (int x = 0; x < board.getSizeX(); x++) {
                if (board.getSub(x, y).getWonBy() != TTPlayer.O) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected final void onReset() {
        TTBase board = this.getGame();
        board.setPlayedBy(TTPlayer.NONE);
        this.currentPlayer = TTPlayer.O;
        for (int y = 0; y < board.getSizeY(); y++) {
            for (int x = 0; x < board.getSizeX(); x++) {
                board.getSub(x, y).setPlayedBy(TTPlayer.X);
            }
        }
    }

    public static TTController cross(int size) {
        return new ClickAndTurnGame(new TTFactories().classicMNK(size), ClickAndTurnGame::cross);
    }

    public static Set<TTBase> cross(TTBase tileClicked) {
        Set<TTBase> affected = new HashSet<>();
        TTBase parent = tileClicked.getParent();
        affected.add(parent.getSub(tileClicked.getX(), tileClicked.getY()));
        affected.add(parent.getSub(tileClicked.getX() + 1, tileClicked.getY()));
        affected.add(parent.getSub(tileClicked.getX() - 1, tileClicked.getY()));
        affected.add(parent.getSub(tileClicked.getX(), tileClicked.getY() + 1));
        affected.add(parent.getSub(tileClicked.getX(), tileClicked.getY() - 1));
        return affected;
    }

    public static Set<TTBase> diagonal(TTBase tileClicked) {
        Set<TTBase> affected = new HashSet<>();
        TTBase parent = tileClicked.getParent();
        affected.add(parent.getSub(tileClicked.getX(), tileClicked.getY()));
        for (int i = 1; i < Math.max(parent.getSizeX(), parent.getSizeY()); i++) {
            affected.add(parent.getSub(tileClicked.getX() - i, tileClicked.getY() - i));
            affected.add(parent.getSub(tileClicked.getX() - i, tileClicked.getY() + i));
            affected.add(parent.getSub(tileClicked.getX() + i, tileClicked.getY() - i));
            affected.add(parent.getSub(tileClicked.getX() + i, tileClicked.getY() + i));
        }
        return affected;
    }

    public static TTController diag(int size) {
        return new ClickAndTurnGame(new TTFactories().classicMNK(size), ClickAndTurnGame::diagonal);
    }

}
