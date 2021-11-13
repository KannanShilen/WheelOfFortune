package eecs285.proj3.kshilen;

import java.util.Random;

public class testMain {

  public static void main(String[] args){
    long gameVal = 100;
    WheelOfFortuneFrame wheel;
    wheel = new WheelOfFortuneFrame(new Random(gameVal));
  }

}
