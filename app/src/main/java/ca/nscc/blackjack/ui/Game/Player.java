package ca.nscc.blackjack.ui.Game;

public class Player {

    // Attributes
    private String name;
    private int handValue;
    private int score;

    // Getters
    public String getName() { return name; }
    public int getHandValue() { return handValue; }
    public int getScore() { return score; }


    // Setters
    public void setName(String name) { this.name = name; }
    public void setHandValue(int handValue) { this.handValue = handValue; }
    public void setScore(int score) { this.score = score; }

    // Default Constructor
    Player() {
        this.name = "Player";
        this.handValue = 0;
        this.score = 0;
    }

    // Secondary Constructor
    Player(String name) {
        this.name = name;
        this.handValue = 0;
        this.score = 0;
    }
}
