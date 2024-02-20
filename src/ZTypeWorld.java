import tester.*;
import javalib.worldimages.*;
import javalib.funworld.*;
import java.awt.Color;
import java.util.Random;

interface IZTypeWorld {
  // constants
  int WIDTH = 600;
  int HEIGHT = 400;
  int PLAYER_X = 300;
  int PLAYER_Y = 360;
  WorldScene EMPTY_WS = new WorldScene(WIDTH, HEIGHT);
}

// represents a world class to animate a list of words on a scene
class ZTypeWorld extends World implements IZTypeWorld {

  // list of words
  ILoWord words;
  // speed of falling words
  double speed;
  // current score
  int score;
  // game scene width
  int width;
  // game scene height
  int height;
  // x coordinate of player
  int playerX;
  // y coordinate of player
  int playerY;
  // for generating words
  Utils utils;

  // constructor for actual game
  ZTypeWorld(ILoWord words, double speed) {
    this.speed = speed;
    this.score = 0;
    this.width = WIDTH;
    this.height = HEIGHT;
    this.playerX = PLAYER_X;
    this.playerY = PLAYER_Y;
    this.words = words;
    this.utils = new Utils();
  }

  // constructor for tests
  ZTypeWorld(ILoWord words, double speed, Random random) {
    this.speed = speed;
    this.score = 0;
    this.width = WIDTH;
    this.height = HEIGHT;
    this.playerX = PLAYER_X;
    this.playerY = PLAYER_Y;
    this.words = words;
    this.utils = new Utils(random);
  }

  /*
   * FIELDS:
   * ... this.speed ... -- double
   * ... this.score ... -- int
   * ... this.width ... -- int
   * ... this.height ... -- int
   * ... this.playerX ... -- int
   * ... this.playerY ... -- int
   * ... this.words ... -- ILoWord
   * ... this.utils ... -- Utils
   * 
   * METHODS:
   * ... this.makeScene() ... -- WorldScene
   * ... this.move() ... -- World
   * 
   * METHODS OF FIELDS:
   * ... this.words.draw(WorldScene acc) ... -- WorldScene
   * ... this.words.move() ... -- ILoWord
   * ... this.utils.randomWord() ... -- String
   * ... this.utils.randomCharHelper(String acc, int count) ... -- String
   */

  // draws the words onto the scene background
  public WorldScene makeScene() {
    return this.words.draw(EMPTY_WS);
  }

  // moves all the words downwards
  public World move() {
    return new ZTypeWorld(this.words.move(), this.speed);
  }
  
  // tick function 
  public World onTick() {
    ILoWord addedWords = new ConsLoWord(this.utils.randomIWord(), this.words);
    return new ZTypeWorld(addedWords.move(), this.speed);
  }

  public boolean isActive() {
    return this.words.isActive();
  }

  public ZTypeWorld onKeyEvent(String key) {
    if (key.equals("poop")) {
      return new ZTypeWorld(this.words, this.speed);
    }
  }
 
}

// represents a list of words
interface ILoWord {
  // draws the current scene
  WorldScene draw(WorldScene acc);

  // moves this list of Words
  ILoWord move();

  boolean isActive();
}

// represents an empty list of words
class MtLoWord implements ILoWord {

  /*
   * FIELDS:
   * 
   * METHODS:
   * ... this.draw() ... -- WorldScene
   * ... this.move() ... -- ILoWord
   */

  // returns an empty scene
  public WorldScene draw(WorldScene acc) {
    return acc;
  }

  // moves a non-existant list of word
  public ILoWord move() {
    return this;
  }

  public boolean isActive() {
    return false;
  }
}

// represents an non-empty list of words
class ConsLoWord implements ILoWord {
  IWord first; // first word in the list
  ILoWord rest; // rest of the words

  // constructor
  ConsLoWord(IWord first, ILoWord rest) {
    this.first = first;
    this.rest = rest;
  }

  /*
   * FIELDS:
   * ... this.first ... -- Word
   * ... this.rest ... -- ILoWord
   * 
   * METHODS:
   * ... this.draw(WorldScene acc) ... -- WorldScene
   * ... this.move() ... -- ILoWord
   * 
   * METHODS OF FIELDS:
   * ... this.first.draw() ... -- WorldImage
   * ... this.first.move() ... -- Word
   * ... this.rest.draw(WorldScene acc) ... -- WorldScene
   * ... this.rest.move() ... -- ILoWord
   */

  // draws words from this non-empty list onto the accumulated
  // image of the scene so far
  public WorldScene draw(WorldScene acc) {
    return this.rest.draw(acc.placeImageXY(this.first.draw(), this.first.x, this.first.y));
  }

  // move the words in this non-empty list
  public ILoWord move() {
    return new ConsLoWord(this.first.move(), this.rest.move());
  }

  public boolean isActive() {
    return this.first.isActive() || this.rest.isActive();
  }
}

interface IWord {
  WorldImage draw();

  IWord move();

  boolean isActive();
}

// represents a word
abstract class AWord implements IWord {
  String word; // the word itself
  int x; // its x coordinate
  int y; // its y coordinate

  // constructor
  AWord(String word, int x, int y) {
    this.word = word;
    this.x = x;
    this.y = y;
  }

  /*
   * FIELDS: 
   * ... this.word ... -- String
   * ... this.x ... -- int
   * ... this.y ... -- int
   * 
   * METHODS:
   * ... this.draw() ... -- WorldImage
   * ... this.move() ... -- Word
   * 
   * METHODS OF FIELDS:
   * 
   */

  // draws the word
  public WorldImage draw();

  // moves the word
  IWord move();

  boolean isActive();
}

class ActiveWord extends AWord {
  ActiveWord(String word, int x, int y) {
    super(word, x, y);
  }

  public WorldImage draw() {
    return new TextImage(this.word, 18, Color.RED);
  }

  public IWord move() {
    return new ActiveWord(this.word, this.x, this.y + 1);
  }

  public boolean isActive() {
    return true;
  }
}

class InactiveWord extends AWord {
  InactiveWord(String word, int x, int y) {
    super(word, x, y);
  }
  public WorldImage draw() {
    return new TextImage(this.word, 18, Color.BLACK);
  }

  public IWord move() {
    return new InactiveWord(this.word, this.x, this.y + 1);
  }

  public boolean isActive() {
    return false;
  }
}

// represents class that generates random string
class Utils {
  Random random = new Random();
  String az = "abcdefghijklmnopqrstuvwxyz";

  // constructor used for game
  Utils() {
  }

  // constructor used for testing
  Utils(Random random) {
    this.random = random;
  }

  // returns a random word
  public String randomWord() {
    return randomCharHelper("", 0);
  }

  // returns a random character from the alphabet
  public String randomCharHelper(String acc, int count) {
    if (count >= 6) {
      return acc;
    }
    else {
      int index = this.random.nextInt(this.az.length() - 2);
      String character = this.az.substring(index, index + 1);
      return this.randomCharHelper(acc + character, count + 1);
    }
  }
  
  public Word randomIWord() {
    return new Word(this.randomWord(), this.random.nextInt(501), this.random.nextInt(50));
  }
}

// represents the tests for this file
class ExamplesZType {
  Word one = new Word("one", 20, 40);
  Word two = new Word("two", 50, 45);
  Word three = new Word("three", 90, 130);

  Word oneMoved = new Word("one", 20, 40 + 5);
  Word twoMoved = new Word("two", 50, 45 + 5);
  Word threeMoved = new Word("three", 90, 130 + 5);

  ILoWord mt = new MtLoWord();
  ILoWord low1 = new ConsLoWord(this.one, this.mt);
  ILoWord low2 = new ConsLoWord(this.two, this.low1);
  ILoWord low3 = new ConsLoWord(this.three, this.low2);

  WorldScene ws = new WorldScene(600, 400);
  ZTypeWorld emptyWorld = new ZTypeWorld(this.mt, 1.0);
  ZTypeWorld world1 = new ZTypeWorld(this.low1, 1.3);
  ZTypeWorld world2 = new ZTypeWorld(this.low2, 1.5);
  ZTypeWorld world3 = new ZTypeWorld(this.low3, 1.7);

  Random random = new Random(0);
  Utils utils = new Utils(this.random);
  ZTypeWorld randomTestWorld = new ZTypeWorld(this.low1, 1.3, this.random);

  // test makeScene in ZTypeWorld
  boolean testMakeScene(Tester t) {
    // test for drawing empty scene
    return t.checkExpect(this.emptyWorld.makeScene(), ws)
        // test for drawing scene with one word
        && t.checkExpect(this.world1.makeScene(), (new WorldScene(600, 400))
            .placeImageXY((new TextImage("one", 18, Color.BLACK)), 20, 40))
        // test for drawing scene with two words
        && t.checkExpect(this.world2.makeScene(),
            ((new WorldScene(600, 400)).placeImageXY((new TextImage("one", 18, Color.BLACK)), 20,
                40)).placeImageXY(new TextImage("two", 18, Color.BLACK), 50, 45))
        // test for drawing scene with three words
        && t.checkExpect(this.world3.makeScene(),
            (((new WorldScene(600, 400)).placeImageXY((new TextImage("one", 18, Color.BLACK)), 20,
                40)).placeImageXY(new TextImage("two", 18, Color.BLACK), 50, 45))
                .placeImageXY(new TextImage("three", 18, Color.BLACK), 90, 130));
  }

  // test draw helper
  boolean testDraw(Tester t) {
    // draw for Word
    return t.checkExpect(this.one.draw(), new TextImage(this.one.word, 18, Color.BLACK))
        && t.checkExpect(this.two.draw(), new TextImage(this.two.word, 18, Color.BLACK))
        && t.checkExpect(this.three.draw(), new TextImage(this.three.word, 18, Color.BLACK))
        // draw for MtLoWord
        && t.checkExpect(this.mt.draw(this.ws), this.ws)
        // draw for ConsLoWord
        && t.checkExpect(this.low1.draw(this.ws),
            this.ws.placeImageXY(new TextImage("one", 18, Color.BLACK), 20, 40))
        && t.checkExpect(this.low2.draw(this.ws),
            (this.ws.placeImageXY(new TextImage("one", 18, Color.BLACK), 20, 40))
                .placeImageXY(new TextImage("two", 18, Color.BLACK), 50, 45));
  }

  // test move()
  boolean testMove(Tester t) {
    // move for Word
    return t.checkExpect(this.one.move(), new Word("one", 20, 45))
        && t.checkExpect(this.two.move(), new Word("two", 50, 50))
        && t.checkExpect(this.three.move(), new Word("three", 90, 135))
        // move for MtLoWord
        && t.checkExpect(this.mt.move(), this.mt)
        // move for ConsLoWord
        && t.checkExpect(this.low1.move(), new ConsLoWord(this.oneMoved, this.mt))
        && t.checkExpect(this.low2.move(),
            new ConsLoWord(this.twoMoved, new ConsLoWord(this.oneMoved, this.mt)))
        && t.checkExpect(this.low3.move(), new ConsLoWord(this.threeMoved,
            new ConsLoWord(this.twoMoved, new ConsLoWord(this.oneMoved, this.mt))))
        // move for ZTypeWorld
        && t.checkExpect(this.emptyWorld.move(), this.emptyWorld)
        && t.checkExpect(this.world1.move(),
            new ZTypeWorld(new ConsLoWord(this.oneMoved, this.mt), 1.3))
        && t.checkExpect(this.world2.move(), new ZTypeWorld(
            new ConsLoWord(this.twoMoved, new ConsLoWord(this.oneMoved, this.mt)), 1.5));
  }

  // test randomizer
  boolean testRandomWord(Tester t) {
    return t.checkExpect(this.utils.randomWord(), "aenxlf")
        && t.checkExpect(this.utils.randomWord(), "ljpoff")
        && t.checkExpect(this.utils.randomWord(), "rwxued")
        && t.checkExpect(this.randomTestWorld.utils.randomWord(), "rihaul")
        && t.checkExpect(this.randomTestWorld.utils.randomWord(), "uepkmx")
        && t.checkExpect(this.randomTestWorld.utils.randomWord(), "rfflco");
  }
  // note: could not test randomCharHelper because it ruined the values of all
  // other random words no matter what we tried.
}

class TEST {
  public static void main(String[] args)
  {
    Word one = new Word("one", 200, 40);
   
    ILoWord mt = new MtLoWord();
    ILoWord low1 = new ConsLoWord(one, mt);
    ZTypeWorld world1 = new ZTypeWorld(low1, 1.3);
    world1.bigBang(600, 400, 0.1);
  }
}
