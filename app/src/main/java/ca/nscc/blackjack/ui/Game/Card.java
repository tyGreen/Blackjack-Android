package ca.nscc.blackjack.ui.Game;

import android.provider.MediaStore;
import android.widget.ImageView;

public class Card {

    // Attributes
    private String suite;
    private String name;
    private int value;
    private int image;

    // Getters
    public String getSuite() { return suite; }
    public String getName() { return name; }
    public int getValue() { return value; }
    public int getImage() {return image;}

    // Setters
    public void setSuite(String suite) { this.suite = suite; }
    public void setName(String name) { this.name = name; }
    public void setValue(int value) { this.value = value; }
    public void setImage(int image) { this.image = image; }


    // Default Constructor
    Card() {
        this.suite = "";
        this.name = "";
        this.value = 0;
        this.image = 0;
    }
}
