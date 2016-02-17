import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;

class Controller implements ActionListener {

    private final Gui myGame;
    public final Map<String, String> actionCommandList = new HashMap<>();
    private final Map<String, Integer> directionDeviationList = new HashMap<>();
    private int turnCount = 1;
    private Image discImage = null;
    private int playerOneWins = 0;
    private int playerTwoWins = 0;
    private final String[] directions = {"DiagonalLeftTopToBottom",
            "DiagonalRightTopToBottom", "DiagonalLeftBottomToTop", "DiagonalRightBottomToTop", "Left", "Right", "Down",};
    private final int[] deviations = {7, 5, -7, -5, -1, 1, 6};
    private static final int[][] boardRepresentation = {
            {0, 1, 2, 3, 4, 5},
            {6, 7, 8, 9, 10, 11},
            {12, 13, 14, 15, 16, 17},
            {18, 19, 20, 21, 22, 23},
            {24, 25, 26, 27, 28, 29},
            {30, 31, 32, 33, 34, 35},
            {36, 37, 38, 39, 40, 41},
    };

    public Controller(Gui givenGuiView) {
        this.myGame = givenGuiView;
        setActionCommands();
        initialiseDirectionDeviationMapping();
    }

    private static int whichRowIsItemIn(int item, String dimension) {
        int returnValue;

        for (int i = 0; i < boardRepresentation.length; i++) {
            for (int j = 0; j < boardRepresentation[i].length; j++) {
                if (boardRepresentation[i][j] == item) {
                    returnValue = (dimension.equals("Col")) ? j : i;
                    return returnValue;
                }
            }
        }
        return 9999;
    }

    private void setActionCommands() {
        for (int x = myGame.absoluteMin; x < 6; x++) {
            actionCommandList.put(Integer.toString(x), "" + x);
        }
        myGame.resetButton.addActionListener(this);
    }

    private void initialiseDirectionDeviationMapping() {
        for (int x = 0; x < directions.length; x++) {
            directionDeviationList.put(directions[x], deviations[x]);
        }
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand().equals("RESET_GAME")) {

            myGame.resetBoard();
            turnCount = 1;

            return;
        }

        if (e.getSource() instanceof JButton) {

            turnCount++;
            try {
                decideDiscPosition(Integer.parseInt(e.getActionCommand()));
            } catch (IOException e1) {
                try {
                    DealWithVictoryException("Whoops", "Problems");
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            } catch (WinException e1) {
                try {
                    DealWithVictoryException("Win for " + itsPlayerOnesTurn(), e1.toString());
                } catch (IOException e2) {
                    try {
                        DealWithVictoryException("Whoops", "Problems");
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
            }
        }
    }

    private void DealWithVictoryException(String exceptionName, String optionPaneMessage) throws IOException {
        JOptionPane.showMessageDialog(null,
                "" + exceptionName,
                "" + optionPaneMessage,
                JOptionPane.ERROR_MESSAGE);
        myGame.setToGameWonState();
        String player = itsPlayerOnesTurn();

        if (player.equals("Blue")) {
            playerOneWins++;
            myGame.playerOneWins.setText("P1 " + playerOneWins);
        } else {
            playerTwoWins++;
            myGame.playerTwoWins.setText("P2 " + playerTwoWins);
        }
    }

    private void decideDiscPosition(int boardPosition) throws IOException, WinException {
        for (int x = boardPosition + 36; x >= boardPosition; x -= 6) {

            doesPositionHaveDisc(x);
            if (!doesPositionHaveDisc(x) && myGame.myBoard[x].isFocusable()) {

                myGame.myBoard[x].setName(itsPlayerOnesTurn());
                setDiscImageOnButton(x);
                checkWin(x);

                return;
            }
        }
    }

    private boolean doesPositionHaveDisc(int boardPosition) {
        return myGame.myBoard[boardPosition].getIcon() != null;
    }

    private void setDiscImageOnButton(int boardPosition) {
        myGame.myBoard[boardPosition].setIcon(new ImageIcon(discImage));
        myGame.myBoard[boardPosition].setFocusable(false);
    }

    private String itsPlayerOnesTurn() throws IOException {
        if (turnCount % 2 == myGame.absoluteMin) {
            discImage = ImageIO.read(getClass().getResource("blueDisc.png"));
            return "Blue";
        } else {
            discImage = ImageIO.read(getClass().getResource("yellowDisc.png"));
            return "Yellow";
        }
    }

    private void checkWin(int boardPosition) throws WinException {

        int lowRange = boardRepresentation[whichRowIsItemIn(boardPosition, "Row")][0];

        for (int x = lowRange; x < lowRange + 6; x++) {
            if (whichRowIsItemIn(boardPosition, "Col") > 3) {
                if (CheckWinHorizontal(x, "Left")) throw new WinException();
            } else {
                if (CheckWinHorizontal(x, "Right")) throw new WinException();
            }
        }
        for (int x = 0; x < 42; x++) {

            for (int y = 0; y < 4; y++) {
                if (CheckWinHorizontal(x, directions[y])) throw new WinException();
            }
        }
        if (CheckWinHorizontal(boardPosition, "Down")) throw new WinException();
    }

    private Vector seeMyNeighbours(int boardPosition, String direction) {
        Vector<String> Neighbours = new Vector<>(4);
        int sum = 0;
        int deviation;
        deviation = directionDeviationList.get(direction);

        for (int x = 0; x < 4; x++) {
            Neighbours.add(getButtonName(boardPosition - sum));

            sum -= deviation;
        }

        return Neighbours;
    }

    private boolean CheckWinHorizontal(int boardPosition, String direction) {

        Vector Neighbours = seeMyNeighbours(boardPosition, direction);

        try {
            if (Neighbours.elementAt(0).equals(Neighbours.elementAt(3))
                    && Neighbours.elementAt(0).equals(Neighbours.elementAt(2))
                    && Neighbours.elementAt(0).equals(Neighbours.elementAt(1))) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }

    private String getButtonName(int boardPosition) {
        try {
            return myGame.myBoard[boardPosition].getName();
        } catch (Exception ignored) {
        }

        return null;
    }
}
