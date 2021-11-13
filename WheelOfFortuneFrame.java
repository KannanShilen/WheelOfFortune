/**
 *Wheel of Fortune main GUI.
 */

package eecs285.proj3.kshilen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 * Class that defined the main game window.
 */
public class WheelOfFortuneFrame extends JFrame {
  /** Uniqname used for package and file paths. */
  public static final String UNIQNAME = "kshilen";
  // Replace the string above with your uniqname.

  /** Number of wheel spaces in the game. */
  public static final int NUM_WHEEL_SPACES = 24;

  /** Path to images folder. */
  public static final String IMAGES_PATH =
    "eecs285/proj3/" + UNIQNAME + "/images";

  /** File extension for images. */
  public static final String IMAGE_EXTENSION = "jpg";

  /**
   * Loades wheel-space images from the images/ directory.
   *
   * Looks for files that follow the naming pattern
   * <spaceNumber>_<value>.jpg. Ignores all other files in the
   * directory. Assumes that there are exactly NUM_WHEEL_SPACES
   * images, numbered from 1 to NUM_WHEEL_SPACES.
   *
   * @return  array of WheelSpace objects representing the images
   */
  static WheelSpace[] loadImages() {
    File[] fileList;
    File myDir = null;

    // Allocate array for number of spaces, which is set to a constant
    WheelSpace[] wheelSpaces = new WheelSpace[NUM_WHEEL_SPACES];

    // Get a File object for the directory containing the images
    try {
      myDir = new File(WheelOfFortuneFrame.class.getClassLoader()
                       .getResource(IMAGES_PATH).toURI());
    } catch (URISyntaxException uriExcep) {
      System.out.println("Caught a URI syntax exception");
      System.exit(4); // Just bail for simplicity in this project
    }

    // Loop from 1 to the number of spaces expected, so we can look
    // for files named <spaceNumber>_<value>.jpg. Note: Space numbers
    // in image filenames are 1-based, NOT 0-based.
    for (int i = 1; i <= NUM_WHEEL_SPACES; i++) {
      // Get a listing of files named appropriately for an image for
      // wheel space #i. There should only be one, and this will be
      // checked below.
      fileList = myDir.listFiles(new WheelSpaceImageFilter(i));

      if (fileList.length == 1) {
        if (WheelSpaceImageFilter.checkBankrupt(fileList[0])) {
          wheelSpaces[i - 1] =
              new WheelSpace("bankrupt",
                  new ImageIcon((fileList[0].toString())));
        } else if (WheelSpaceImageFilter.checkLoseTurn(fileList[0])) {
          wheelSpaces[i - 1] =
              new WheelSpace("loseATurn",
                  new ImageIcon((fileList[0].toString())));
        } else {
          // Index starts at 0, space numbers start at 1: hence the - 1
          wheelSpaces[i - 1] =
              new WheelSpace(WheelSpaceImageFilter.getSpaceValue(fileList[0]),
                  new ImageIcon(fileList[0].toString()));
        }
      } else {
        System.out.println("ERROR: Invalid number of images for space: " + i);
        System.out.println("       Expected 1, but found " + fileList.length);
      }
    }

    return wheelSpaces;
  }

  // Helper nested class to filter images used for wheel spaces, based
  // on specifically expected filename format.
  private static class WheelSpaceImageFilter implements FileFilter {
    /** Prefix of the requested filename. */
    private String prefix;  // The prefix of the filename we're looking
                            // for - what comes before the first underscore

    /**
     * Constructs a filter with the given prefix.
     *
     * @param inPref  integer corresponding to the prefix
     */
    WheelSpaceImageFilter(int inPref) {
      // Sets the prefix member to string version of space number
      prefix = new Integer(inPref).toString();
    }

    /**
     * Tests whether the file provided should be accepted by our file
     * filter. In the FileFilter interface.
     */
    @Override
    public boolean accept(File imageFile) {
      boolean isAccepted = false;

      // Accepted if matched "<prefix>_<...>.jpg" where
      // IMAGE_EXTENSION is assumed to be "jpg" for this example
      if (imageFile.getName().startsWith(prefix + "_")
          && imageFile.getName().endsWith("." + IMAGE_EXTENSION)) {
        isAccepted = true;
      }

      return isAccepted;
    }

    /**
     * Parses a wheel space image's filename to determine the dollar
     * value associated with it.
     *
     * @param imageFile  the wheel space image
     * @return  the dollar value associated with the image
     */
    public static int getSpaceValue(File imageFile) {
      String file = imageFile.getName();
      String val = getSpaceString(file);
      int dollarVal = Integer.parseInt(val);
      return dollarVal;
    }

    /**
     * Parses a wheel space image's filename and determines
     * if it is wheel for lose a turn.
     *
     * @param imageFile the wheel space image's filepath
     * @return true if it's the lose a turn space, else false
     */
    public static boolean checkLoseTurn(File imageFile) {
      String file = imageFile.getName();
      String checkString = getSpaceString(file);
      if (checkString.equals("loseATurn")) {
        return true;
      }
      return false;
    }

    /**
     * Parses a wheel space image's filename and determines
     * if it is wheel for bankrupt.
     *
     * @param imageFile the wheel space image's filepath
     * @return true if it's the bankrupt space, else false
     */
    public static boolean checkBankrupt(File imageFile) {
      String file = imageFile.getName();
      String checkString = getSpaceString(file);
      if (checkString.equals("bankrupt")) {
        return true;
      }
      return false;
    }

    /**
     * For the file string <prefix>_value.jpg, it splits
     * the string and return "value".
     *
     * @param file
     * @return the value of the wheel space
     */
    public static String getSpaceString(String file) {
      String[] splitFile = file.split("_");
      String[] splitFile2 = splitFile[1].split("\\.");
      return splitFile2[0];
    }
  }

  /**
   * Create and start a game of Wheel of Fortune.
   *
   * @param generator  the random-number generator to use
   */
  public WheelOfFortuneFrame(Random generator) {
    this.generator = generator;
    mainFrame = this;
    numVowelsGuessed = 0;
    numConsonantsGuessed = 0;
    currentPlayer = 0;
    letterAvailable = new boolean[26];
    //All letters are available in the beginning
    for (int i = 0; i < 26; ++i) {
      letterAvailable[i] = true;
    }
    wheelSpaces = loadImages();
    currentWheel = wheelSpaces[0];
    setLayout(new BorderLayout());
    setTitle("Wheel of Fortune");

    //First Dialog Box
    InputDialog numPlayer = new InputDialog(this,
        "Number of Players Input", true, false);
    String tempString = numPlayer.getEntry();
    numPlayers = Integer.parseInt(tempString);

    //Second Dialog Box
    players = new String[numPlayers];
    playerCash = new int[numPlayers];
    for (int i = 0; i < numPlayers; ++i) {
      InputDialog playerNames = new InputDialog(this,
          "Player Name Input", i);
      players[i] = playerNames.getEntry();
    }

    //Third Dialog Box
    InputDialog getPuzzle = new InputDialog(this,
        "Puzzle Input", false, false);
    puzzle = getPuzzle.getEntry();

    //Make the panel with player names
    topPanel = new JPanel();
    makePlayersPanel();

    //Creates the panel with buy vowel, spin the wheel and solve puzzle buttons and adds the image
    middlePanel = new JPanel();
    addButtons();
    middlePanel.setLayout(new FlowLayout());

    //initialize image with the first wheelSpace image
    ImageIcon image = wheelSpaces[0].getImage();
    imageLabel = new JLabel(image);
    middlePanel.add(imageLabel);
    add(middlePanel, BorderLayout.CENTER);

    //Creates the panel with the vowels and consonants.
    botPanel = new JPanel();
    botPanel.setLayout(new BorderLayout());
    vowels = new JButton[5];
    consonants = new JButton[21];
    letterBox = new JPanel();
    letterBox.setLayout(new BorderLayout());
    vowelBox = new JPanel();
    vowelBox.setLayout(new GridLayout(3, 2));
    vowelBox.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.CYAN),
        "Vowels", TitledBorder.LEFT, TitledBorder.TOP));
    consonantsBox = new JPanel();
    consonantsBox.setLayout(new GridLayout(3, 7));
    consonantsBox.setBorder(BorderFactory.createTitledBorder(BorderFactory
            .createLineBorder(Color.CYAN),
        "Consonants", TitledBorder.LEFT, TitledBorder.TOP));
    //Add vowels and consonants to letterBox panel
    addLettersToLetterBox();
    botPanel.add(letterBox, BorderLayout.NORTH);

    //Add the panel with the hidden puzzle string
    puzzlePanel = new JPanel();
    puzzlePanel.setLayout(new FlowLayout());
    addPuzzlePanel();


    add(botPanel, BorderLayout.SOUTH);
    setVisible(true);
    pack();
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  /**
   * Creates the player panel and highlights the current player with a red border color.
   * If a player panel already exists, recreate it.
   */
  private void makePlayersPanel() {
    topPanel.removeAll();
    JPanel[] playerPanels = new JPanel[numPlayers];
    topPanel.setLayout(new GridLayout(1, numPlayers));
    for (int i = 0; i < numPlayers; ++i) {
      playerPanels[i] = new JPanel();
      playerPanels[i].setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
          players[i], TitledBorder.LEFT, TitledBorder.TOP));
      //Set border colour red for current Player and black for others
      if (i == currentPlayer) {
        playerPanels[i].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(
            Color.RED), players[i], TitledBorder.LEFT, TitledBorder.TOP));
      } else {
        playerPanels[i].setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(
            Color.BLACK), players[i], TitledBorder.LEFT, TitledBorder.TOP));
      }
      //Add player amounts
      JLabel playerMoney = new JLabel(Integer.toString(playerCash[i]));
      playerPanels[i].add(playerMoney);
      topPanel.add(playerPanels[i]);
    }
    add(topPanel, BorderLayout.NORTH);
    revalidate();
  }

  /**
   * Helper function to add vowels and consonants to the letterBox panel.
   * Also sets all the buttons to be disables.
   */
  private void addLettersToLetterBox() {
    int countVowel = 0;
    int countConsonants = 0;
    for (char i = 'A'; i <= 'Z'; ++i) {
      JButton letterButton = new JButton(Character.toString(i));
      letterButton.setEnabled(false);
      //Vowels
      if (i == 'A' || i == 'E' || i == 'I' || i == 'O' || i == 'U') {
        letterButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            numVowelsGuessed++;
            String letter = letterButton.getText();
            char c = letter.charAt(0);
            //c - 'A' gives the relative position of the letter
            letterAvailable[c - 'A'] = false;
            boolean letterExits = updateHiddenPuzzle(letter);
            if (!letterExits) {
              updatePlayer();
            }
            makePlayersPanel();
            disableLetters();
            enableButtons();
          }
        });
        vowels[countVowel] = letterButton;
        vowelBox.add(vowels[countVowel]);
        countVowel++;
      } else {
        letterButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            numConsonantsGuessed++;
            String letter = letterButton.getText();
            char c = letter.charAt(0);
            letterAvailable[c - 'A'] = false;
            boolean letterExists = updateHiddenPuzzle(letter);
            if (letterExists) {
              int dollarValue = currentWheel.getDollarValue();
              playerCash[currentPlayer] += dollarValue;
            } else {
              updatePlayer();
            }
            makePlayersPanel();
            disableLetters();
            enableButtons();
          }
        });
        consonants[countConsonants] = letterButton;
        consonantsBox.add(consonants[countConsonants]);
        countConsonants++;
      }
    }
    letterBox.add(vowelBox, BorderLayout.WEST);
    letterBox.add(consonantsBox, BorderLayout.EAST);
  }

  /**
   * Enables the spin the wheel, buy a vowel, and solve a puzzle buttons if available.
   */
  private void enableButtons() {
    //if not all vowels have guessed and player has money
    if (numVowelsGuessed < 5 && playerCash[currentPlayer] >= 250) {
      buyVowelButton.setEnabled(true);
    } else {
      buyVowelButton.setEnabled(false);
    }

    //if not all the consonants are guessed, then enable the spin the wheel button
    if (numConsonantsGuessed < 21) {
      spinButton.setEnabled(true);
    }

    solveButton.setEnabled(true);

  }

  /**
   * Disables all vowels and consonants.
   */
  private void disableLetters() {
    for (int i = 0; i < 5; ++i) {
      vowels[i].setEnabled(false);
    }
    for (int j = 0; j < 21; ++j) {
      consonants[j].setEnabled(false);
    }
  }

  /**
   * Helper function to add the spin the wheel,
   * buy a vowel, and solve puzzle buttons.
   */
  private void addButtons() {
    JPanel middleLeftPanel = new JPanel();
    middleLeftPanel.setLayout(new GridLayout(6, 1));
    middleLeftPanel.add(new JLabel(""));
    buyVowelButton = new JButton("Buy a Vowel");
    buyVowelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        playerCash[currentPlayer] -= 250;
        buyVowelButton.setEnabled(false);
        spinButton.setEnabled(false);
        solveButton.setEnabled(false);
        enableAvailableVowels();
      }
    });
    //in the start of the game the buy vowel button will be greyed out
    buyVowelButton.setEnabled(false);
    middleLeftPanel.add(buyVowelButton);
    middleLeftPanel.add(new JLabel(""));
    spinButton = new JButton(("Spin the Wheel"));
    spinButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        buyVowelButton.setEnabled(false);
        spinButton.setEnabled(false);
        solveButton.setEnabled(false);
        //get next Index
        int index = generator.nextInt(NUM_WHEEL_SPACES);
        currentWheel = wheelSpaces[index];
        imageLabel.setIcon(currentWheel.getImage());
        if (currentWheel.getString().equals("bankrupt")) {
          playerCash[currentPlayer] = 0;
          updatePlayer();
          makePlayersPanel();
          enableButtons();
        } else if (currentWheel.getString().equals("loseATurn")) {
          updatePlayer();
          makePlayersPanel();
          enableButtons();
        } else {
          enableAvailableConsonants();
        }
      }
    });
    middleLeftPanel.add(spinButton);
    middleLeftPanel.add(new JLabel(""));
    solveButton = new JButton("Solve the Puzzle");
    solveButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        spinButton.setEnabled(false);
        buyVowelButton.setEnabled(false);
        solveButton.setEnabled(false);
        InputDialog solveDialog = new InputDialog(mainFrame,
            "Solve Puzzle", false, true);
        String enteredString = solveDialog.getEntry();
        enteredString = enteredString.toUpperCase(Locale.ROOT);
        if (enteredString.equals(puzzle)) {
            String message = players[currentPlayer] + " wins $" + playerCash[currentPlayer];
            JOptionPane.showMessageDialog(mainFrame, message,
                "Game Over", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
          String message = "Guess by " + players[currentPlayer] + " was incorrect!";
          JOptionPane.showMessageDialog(mainFrame, message,
              "Error Message", JOptionPane.ERROR_MESSAGE);
          updatePlayer();
          enableButtons();
          makePlayersPanel();

        }
      }
    });
    middleLeftPanel.add(solveButton);
    middlePanel.add(middleLeftPanel);
  }

  /**
   * Function to enable all vowels that have not been guessed.
   */
  private void enableAvailableVowels() {
    char[] vowelsArray = {'A', 'E', 'I', 'O', 'U'};
    for (int i = 0; i < 5; ++i) {
      if (letterAvailable[vowelsArray[i] - 'A']) {
        vowels[i].setEnabled(true);
      }
    }
  }

  /**
   * Function to enable all consonants that have not been guessed.
   */
  private void enableAvailableConsonants() {
    char[] consonantsArray = new char[21];
    int count = 0;
    for (char c = 'A'; c <= 'Z'; ++c) {
      if (c != 'A' && c != 'E' && c != 'I' && c != 'O' && c != 'U') {
        consonantsArray[count] = c;
        count++;
      }
    }
    for (int i = 0; i < 21; ++i) {
      if (letterAvailable[consonantsArray[i] - 'A']) {
        consonants[i].setEnabled(true);
      }
    }
  }

  /**
   * Helper function to add the panel that contains the hidden puzzle.
   */
  private void addPuzzlePanel() {
    hiddenPuzzle = "";
    puzzle = puzzle.toUpperCase(Locale.ROOT);
    for (int i = 0; i < puzzle.length(); ++i) {
      if (isAlphabet(puzzle.charAt(i))) {
        hiddenPuzzle += "- ";
      } else {
        hiddenPuzzle += puzzle.charAt(i) + " ";
      }
    }
    puzzleLabel = new JLabel(hiddenPuzzle);
    puzzlePanel.add(puzzleLabel);
    botPanel.add(puzzlePanel, BorderLayout.SOUTH);
  }

  /**
   * Function to check whether a guessed letter is in the puzzle, and updates
   * the hidden string to show all instances of that letter.
   *
   * @param letter the guessed letter
   * @return true if the letter is in the puzzle, else false
   */
  private boolean updateHiddenPuzzle(String letter) {
    if (puzzle.contains(letter)) {
      char c = letter.charAt(0);
      char[] tempArray = hiddenPuzzle.toCharArray();
      for (int i = 0; i < puzzle.length(); ++i) {
        if (c == puzzle.charAt(i)) {
          tempArray[i * 2] = puzzle.charAt(i);
        }
      }
      hiddenPuzzle = String.valueOf(tempArray);
      puzzleLabel.setText(hiddenPuzzle);
      return true;
    }
    return false;
  }

  /**
   * Helper function to check whether a char is an alphabet.
   *
   * @param c the char variable
   * @return true if the char variable is an alphabet, else false
   */
  private boolean isAlphabet(char c) {
    if (c >= 'A' && c <= 'Z') {
      return true;
    }
    return false;
  }

  /**
   * Function that updates current player to the next player.
   */
  private void updatePlayer() {
    if (currentPlayer < numPlayers - 1) {
      currentPlayer++;
    } else {
      currentPlayer = 0;
    }
  }


  private JFrame mainFrame;
  private final JPanel topPanel;
  private final JPanel middlePanel;
  private final JPanel botPanel;
  private final JPanel letterBox;
  private final JPanel vowelBox;
  private final JPanel consonantsBox;
  private final JPanel puzzlePanel;

  private JButton spinButton;
  private JButton solveButton;
  private JButton buyVowelButton;

  private JLabel imageLabel;
  private JLabel puzzleLabel;

  private JButton[] vowels;
  private JButton[] consonants;

  private WheelSpace currentWheel;
  private WheelSpace[] wheelSpaces;

  private boolean[] letterAvailable;
  private int currentPlayer;
  private int numPlayers;
  private int numVowelsGuessed;
  private int numConsonantsGuessed;
  private int[] playerCash;
  private String hiddenPuzzle;
  private String puzzle;
  private String[] players;
  private Random generator;

}
