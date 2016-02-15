import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;

class Controller implements ActionListener {

    private final Gui myGame;
    public final Map<String, String> actionCommandList = new HashMap<>();
    private int turnCount = 1;
    private Image discImage = null;
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
    }

    private static int whichRowIsItemIn(int item , String dimension)
    {
        int returnValue;

        for(int i=0; i<boardRepresentation.length; i++) {
            for(int j=0; j<boardRepresentation[i].length; j++) {
                if(  boardRepresentation[i][j] == item)
                {
                    returnValue = (dimension.equals("Col")) ? j:i;
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
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() instanceof JButton) {

            turnCount++;
            try {
                decideDiscPosition(Integer.parseInt(e.getActionCommand()));
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (WinException e1) {
                try {
                    showExceptionPanel("Win for " +itsPlayerOnesTurn(), e1.toString());
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    private static void showExceptionPanel(String exceptionName, String optionPaneMessage) {
        JOptionPane.showMessageDialog(null,
                "" + exceptionName,
                "" + optionPaneMessage,
                JOptionPane.ERROR_MESSAGE);
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

        //get the row the disc is in
        int row = whichRowIsItemIn(boardPosition, "Row");
        //we only need to check discs for left an right in that row if we check for right and left win
        //we know the row so get the lowest value in it, start at that and check for each x both ways.
        int lowRange= boardRepresentation[whichRowIsItemIn(boardPosition, "Row")][0];

    for(int x=lowRange; x<lowRange+6; x++)
    {
        if (checkWinDirectionLeft(x, "Left")) throw new WinException();
        if (checkWinDirectionLeft(x,"Right")) throw new WinException();
    }

        int decision = (row < 3  ) ? 0 : 4;

        if(decision>=3)
        {
          if (checkWinDirectionLeft(boardPosition, "Left")) throw new WinException();
        }
        else
        {
            if (checkWinDirectionLeft(boardPosition,"Right")) throw new WinException();
        }
    }

    private Vector seeMyNeighbours(int boardPosition, String direction)
    {
        Vector<String> Neighbours = new Vector<>(4);

        if(direction.equals("Left"))
        {
            for(int x = 0; x<4; x++)
            {
                Neighbours.add(getButtonName(boardPosition-x));
            }
        }

        if(direction.equals("Right"))
        {
            for(int x = 0; x<4; x++)
            {
                Neighbours.add(getButtonName(boardPosition+x));
            }
        }

        return Neighbours;
    }

    private boolean checkWinDirectionLeft(int boardPosition, String direction) {

        Vector<String> Neighbours = seeMyNeighbours(boardPosition, direction);

        try
        {
            if (Neighbours.elementAt(0).equals(Neighbours.elementAt(3))
                    && Neighbours.elementAt(0).equals(Neighbours.elementAt(2))
                    && Neighbours.elementAt(0).equals(Neighbours.elementAt(1))){
                return true;
            }
        }
        catch (Exception e)
        {
            return false;
        }

        return false;
    }

    private String getButtonName(int boardPosition) {
        try
        {
            return myGame.myBoard[boardPosition].getName();
        }
        catch (Exception ignored)
        {

        }

      return null;
    }
}
