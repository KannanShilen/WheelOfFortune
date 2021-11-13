/**
 * This is the Java file that defines the InputDialog class which provides
 * a common class for all the input dialogs that are used for this project.
 */

package eecs285.proj3.kshilen;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Class that can create all the input dialogs needed for this project.
 */
public class InputDialog extends JDialog {

  /**
   * This is the constructor that creates a JDialog window to input number of players,
   * a JDialog window to input the puzzle, or the JDialog window to solve the puzzle
   * depending on the boolean param IsInputNumPlayers and the value of the String title.
   *
   * @param mainFrame The JFrame of the game window
   * @param title the title of the input dialog
   * @param isInputNumPlayers If true, creates window to input num players
   *                          else creates window to input puzzle
   * @param setMainWindowVisible If true, sets the main window as visible,
   *                             else makes it invisible
   */
  InputDialog(JFrame mainFrame,
              String title,
              boolean isInputNumPlayers,
              boolean setMainWindowVisible) {
    super(mainFrame, title, true);
    if (!setMainWindowVisible) {
      mainFrame.setVisible(false);
    }
    String label;
    if (isInputNumPlayers) {
      label = "   Enter number of players (must be at least 1)";
    } else {
      if (title.equals("Puzzle Input")) {
        label = "   Ask a non-player to enter a puzzle";
      } else {
        label = "   Enter complete puzzle exactly as displayed";
      }
    }
    //Helper function to make the top and middle panels(label and text field)
    makeTopAndMidPanels(label);

    JPanel botPanel = new JPanel();
    botPanel.setLayout(new FlowLayout());
    JButton okButton = new JButton("Ok");
    addListenerToButton(okButton, isInputNumPlayers);
    botPanel.add(okButton);
    add(botPanel);

    pack();
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setVisible(true);

  }

  /**
   * This is the constructor that creates a JDialog window to input the player names.
   *
   * @param mainFrame the JFrame of the main game window
   * @param title the title of the JDialow window
   * @param num the zero based player index.
   */
  InputDialog(JFrame mainFrame,
              String title,
              int num) {
    super(mainFrame, title, true);
    mainFrame.setVisible(false);

    makeTopAndMidPanels("   Enter name of player #" + num);

    JPanel botPanel = new JPanel();
    botPanel.setLayout(new FlowLayout());
    JButton okButton = new JButton("Ok");
    okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (!inputField.getText().isEmpty()) {
          setVisible(false);
        }
      }
    });
    botPanel.add(okButton);

    add(botPanel);

    pack();
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setVisible(true);
  }


  /**
   * Helper function for constructors to create the top and middle panels
   * for the input windows.
   * @param s the text for the JLabel
   */
  private void makeTopAndMidPanels(String s) {
    setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

    JPanel topPanel = new JPanel();
    topPanel.setLayout(new BorderLayout());
    topPanel.add(new JLabel(s),
        BorderLayout.WEST);

    JPanel middlePanel = new JPanel();
    middlePanel.setLayout(new BorderLayout());
    inputField = new JTextField(100);
    middlePanel.add(inputField, BorderLayout.WEST);

    add(topPanel);
    add(middlePanel);
  }

  /**
   * Helper function to add a new ActionListener to a JButton, depending on the type of
   * input window is required. If it is the window to input number of players, there is added
   * functionalities to display error messages if the input string is not a positive
   * integer.
   *
   * @param okButton The JButton that has to be modified
   * @param needsErrorMessage A boolean variable to indicate whether
   *                          you need the extra functionalities or not
   */
  private void addListenerToButton(JButton okButton, boolean needsErrorMessage) {
    if (needsErrorMessage) {
      JDialog currentFrame = this;
      okButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (!inputField.getText().isEmpty()) {
            try {
              int input = Integer.parseInt(inputField.getText());
              if (input > 0) {
                setVisible(false);
              } else {
                JOptionPane.showMessageDialog(currentFrame,
                    "Input must be a positive integer",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
              }
            } catch (Exception d) {
              JOptionPane.showMessageDialog(currentFrame,
                  "Input must be a positive integer",
                  "Input Error", JOptionPane.ERROR_MESSAGE);
            }
          } else {
            JOptionPane.showMessageDialog(currentFrame,
                "Input must be a positive integer",
                "Input Error", JOptionPane.ERROR_MESSAGE);
          }
        }
      });
    } else {
      okButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (!inputField.getText().isEmpty()) {
            setVisible(false);
          }
        }
      });
    }
  }

  /**
   * Function to return the text in the input field.
   *
   * @return the text in the input field.
   */
  public String getEntry() {
    return inputField.getText();
  }

  private JTextField inputField;
}
