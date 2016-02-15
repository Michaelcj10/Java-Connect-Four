import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

class Gui extends JFrame {
    JButton[] myBoard;
    private JPanel gridPanel;
    private Controller control;
    private final int boardGridSize = 42;
    private final int rowCount = 6;
    public final int absoluteMin = 0;

    private Gui() {
        super("Connect Four");
        initialisePanels();
    }

    private void initialisePanels() {
        myBoard = new JButton[boardGridSize];
        gridPanel = new JPanel();
        control = new Controller(this);
        makeGrid();
        gridPanel.setLayout(new GridLayout(rowCount+1, rowCount));

        setSize(650, 650);
        setResizable(false);
        setVisible(true);
    }

    private void makeGrid() {
        for (int i = absoluteMin; i < boardGridSize; i++) {


            myBoard[i] = new JButton();

            myBoard[i].setSize(250, 20);
            myBoard[i].setHorizontalAlignment(JTextField.CENTER);
            myBoard[i].setFont(new Font("SansSerif", Font.BOLD, 14));
            myBoard[i].setForeground(Color.WHITE);

            gridPanel.add(myBoard[i]);
            myBoard[i].setBackground(Color.WHITE);
            myBoard[i].setOpaque(false);

            String buttonText = (i % 2 == 0) ? "Text" : "AltText";
            myBoard[i].setName(buttonText);
            if (i < rowCount) {
                myBoard[i].addActionListener(control);
                myBoard[i].setActionCommand(control.actionCommandList.get(Integer.toString(i)));
            }
        }

        setBounds(100, 100, 450, 300);
        JPanel contentPane = new JPanel() {

            public void paintComponent(Graphics g) {
                Image img = Toolkit.getDefaultToolkit().getImage(
                        Gui.class.getResource("blue.png"));
                g.drawImage(img, absoluteMin, absoluteMin, this.getWidth(), this.getHeight(), this);
            }
        };

        setContentPaneParams(contentPane);
        setPanelOpacity();
    }

    public void resetBoard()
    {
        for (int i = absoluteMin; i < boardGridSize; i++) {

            myBoard[i].setIcon(null);
            myBoard[i].setName(null);

        }
    }



    private void setPanelOpacity() {
        gridPanel.setOpaque(false);
    }

    private void setContentPaneParams(JPanel contentPane) {
        contentPane.setBorder(new EmptyBorder(absoluteMin+5, absoluteMin+5, absoluteMin+5, absoluteMin+5));
        contentPane.setLayout(new BorderLayout(absoluteMin, absoluteMin));
        setContentPane(contentPane);
        contentPane.add(gridPanel);
    }

    public static void main(String[] args) {
        new Gui();
    }
}