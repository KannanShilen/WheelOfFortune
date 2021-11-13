/**
 * This Java file is used to define the WheelSpace class which holds information
 * and methods for a single Wheel Space.
 */

package eecs285.proj3.kshilen;

import javax.swing.ImageIcon;

/**
 * The class WheelSpace defines an object for a single wheel space and
 * provides methods to retrieve information.
 */
public class WheelSpace {
  /**
   * Parametrized constructor to initialize a wheel
   * space that has a numeric amount.
   *
   * @param dollarValue the dollar value associated with the wheel space
   * @param image the ImageIcon variable that refers to the
   *              picture that has to be displayed
   */
  WheelSpace(int dollarValue,
             ImageIcon image) {
    this.dollarValue = dollarValue;
    this.image = image;
    this.stringValue = "";
  }

  /**
   * Parametrized constructor to initialize the losATurn or bankrupt wheel space.
   *
   * @param stringVal bankrupt or loseATurn
   * @param image the ImageIcon variable that refers to the
   *              picture that has to be displayed
   */
  WheelSpace(String stringVal,
             ImageIcon image) {
    this.dollarValue = 0;
    this.image = image;
    this.stringValue = stringVal;
  }

  /**
   * Function to get the dollar value associated with the wheel space.
   *
   * @return the dollar value
   */
  public int getDollarValue() {
    return dollarValue;
  }

  /**
   * Function to get the ImageIcon of the image to be displayed.
   *
   * @return an ImageIcon variable
   */
  public ImageIcon getImage() {
    return image;
  }

  /**
   * Function to get the String value related to the wheel space.
   *
   * @return  the String value associated with the wheel space
   */
  public String getString() {
    return stringValue;
  }

  private int dollarValue;
  private String stringValue;
  private ImageIcon image;

}
