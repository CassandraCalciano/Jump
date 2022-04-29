package jump61;

import java.util.ArrayList;
import java.util.Random;

import static jump61.Side.*;

/** An automated Player.
 *  @author P. N. Hilfinger
 */
class AI extends Player {

    /** A new player of GAME initially COLOR that chooses moves automatically.
     *  SEED provides a random-number seed used for choosing moves.
     */
    AI(Game game, Side color, long seed) {
        super(game, color);
        _random = new Random(seed);
    }

    @Override
    String getMove() {
        Board board = getGame().getBoard();

        assert getSide() == board.whoseMove();
        int choice = searchForMove();
        getGame().reportMove(board.row(choice), board.col(choice));
        return String.format("%d %d", board.row(choice), board.col(choice));
    }

    /** Returns an arraylist with all validMoves for a PLAYER on BOARD. */
    private ArrayList<Integer> findMoves(Board board, Side player) {
        ArrayList<Integer> validMoves = new ArrayList<>();
        for (int i = 0; i < board.size() * board.size(); i += 1) {
            if (board.isLegal(player, i)) {
                validMoves.add(i);
            }
        }
        return validMoves;
    }


    /** Return a move after searching the game tree to DEPTH>0 moves
     *  from the current position. Assumes the game is not over. */
    private int searchForMove() {
        Board work = new Board(getBoard());
        int value;
        assert getSide() == work.whoseMove();
        _foundMove = -1;
        if (getSide() == RED) {
            value = minMax(work, 4, true, -1,
                    Integer.MIN_VALUE, Integer.MAX_VALUE);
        } else {
            value = minMax(work, 4, true, 1,
                    Integer.MIN_VALUE, Integer.MAX_VALUE);
        }
        return _foundMove;
    }


    /** Find a move from position BOARD and return its value, recording
     *  the move found in _foundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _foundMove. If the game is over
     *  on BOARD, does not set _foundMove. */
    private int minMax(Board board, int depth, boolean saveMove,
                       int sense, int alpha, int beta) {
        if (depth == 0 || board.getWinner() != null) {
            return staticEval(board, Integer.MAX_VALUE);
        }
        int finalMove = 0;
        int score = -sense * Integer.MAX_VALUE;

        for (int M = 0; M < board.size() * board.size(); M += 1) {
            if (findMoves(board, board.whoseMove()).contains(M)) {
                board.addSpot(board.whoseMove(), M);
            } else {
                continue;
            }
            int response = minMax(board, depth - 1, true, -1, alpha, beta);
            if (sense * response >= sense * score) {
                finalMove = M;
                score = response;
            }
            board.undo();
            if (sense > 0) {
                alpha = Math.max(beta, score);
            } else {
                beta = Math.min(beta, score);
            }
            if (alpha > beta) {
                break;
            }
        }
        if (saveMove) {
            _foundMove = finalMove;
        }
        return score;
    }

    /** Return a heuristic estimate of the value of board position B.
     *  Use WINNINGVALUE to indicate a win for Red and -WINNINGVALUE to
     *  indicate a win for Blue. */
    private int staticEval(Board b, int winningValue) {
        if (b.getWinner() != null) {
            if (b.getWinner() == RED) {
                return winningValue * -1;
            } else {
                return winningValue;
            }
        }
        return b.numOfSide(RED) - b.numOfSide(BLUE);
    }

    /** A random-number generator used for move selection. */
    private Random _random;

    /** Used to convey moves discovered by minMax. */
    private int _foundMove;
}
