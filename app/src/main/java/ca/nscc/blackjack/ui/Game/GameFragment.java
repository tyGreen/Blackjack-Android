package ca.nscc.blackjack.ui.Game;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import ca.nscc.blackjack.R;
import ca.nscc.blackjack.databinding.FragmentGameBinding;

// Import custom classes


public class GameFragment extends Fragment {

    private GameViewModel GameViewModel;
    private FragmentGameBinding binding;

    // VARIABLE DECLARATIONS
    // GUI:
    TextView lbl_pScore, lbl_hScore, lbl_pHand, lbl_hHand, lbl_pHandVal, lbl_hHandVal, lbl_pScoreVal, lbl_hScoreVal;
    ImageView pCard1, pCard2, pCard3, pCard4, pCard5, hCard1, hCard2, hCard3, hCard4, hCard5;
    Button btn_start, btn_hit, btn_stay;
    ImageView[] pHand, hHand;
    // Preferences:
    SharedPreferences prefs;
    // Players
    Player player;
    Player house;
    // Array of Card objects:
    Card deck[];
    int pNumCardsFlipped = 0;
    int hNumCardsFlipped = 0;
    int topCard = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GameViewModel =
                new ViewModelProvider(this).get(GameViewModel.class);

        binding = FragmentGameBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // VARIABLE INITIALIZATIONS
        // Players
        player = new Player();
        house = new Player("House");
        // Player score labels
        lbl_pScore = (TextView) root.findViewById(R.id.lbl_pScore);
        lbl_pScoreVal = (TextView) root.findViewById(R.id.lbl_pScoreVal);
        // House score labels
        lbl_hScore = (TextView) root.findViewById(R.id.lbl_hScore);
        lbl_hScoreVal = (TextView) root.findViewById(R.id.lbl_hScoreVal);
        // Player cards
        pCard1 = (ImageView) root.findViewById(R.id.pCard1);
        pCard2 = (ImageView) root.findViewById(R.id.pCard2);
        pCard3 = (ImageView) root.findViewById(R.id.pCard3);
        pCard4 = (ImageView) root.findViewById(R.id.pCard4);
        pCard5 = (ImageView) root.findViewById(R.id.pCard5);
        // Player hand
        pHand = new ImageView [5];
        pHand[0] = pCard1;
        pHand[1] = pCard2;
        pHand[2] = pCard3;
        pHand[3] = pCard4;
        pHand[4] = pCard5;
        // Player hand value labels
        lbl_pHand = (TextView) root.findViewById(R.id.lbl_pHand);
        lbl_pHandVal = (TextView) root.findViewById(R.id.lbl_pHandVal);
        // House cards
        hCard1 = (ImageView) root.findViewById(R.id.hCard1);
        hCard2 = (ImageView) root.findViewById(R.id.hCard2);
        hCard3 = (ImageView) root.findViewById(R.id.hCard3);
        hCard4 = (ImageView) root.findViewById(R.id.hCard4);
        hCard5 = (ImageView) root.findViewById(R.id.hCard5);
        // House hand
        hHand = new ImageView [5];
        hHand[0] = hCard1;
        hHand[1] = hCard2;
        hHand[2] = hCard3;
        hHand[3] = hCard4;
        hHand[4] = hCard5;
        // House hand value labels
        lbl_hHand = (TextView) root.findViewById(R.id.lbl_hHand);
        lbl_hHandVal = (TextView) root.findViewById(R.id.lbl_hHandVal);
        // Buttons
        btn_start = (Button) root.findViewById(R.id.btn_start);
        btn_hit = (Button) root.findViewById(R.id.btn_hit);
        btn_hit.setEnabled(false); // Hit btn disabled til game starts
        btn_stay = (Button) root.findViewById(R.id.btn_stay);
        btn_stay.setEnabled(false); // Stay btn disabled til game starts
        // Shared preferences
        prefs = getActivity().getSharedPreferences("game_data", Context.MODE_PRIVATE);
        // Card array
        deck = new Card[52];
        // Set card's suite
        initializeCards(deck);

        // "START" BUTTON-CLICK ROUTINE:
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // SET-UP BOARD
                // Ensure all cards face-down (in case not first hand)
                for(int i = 0; i < 5; i++) {
                    pHand[i].setImageResource(R.drawable.img_facedown);
                    hHand[i].setImageResource(R.drawable.img_facedown);
                }
                // Reset all counters
                pNumCardsFlipped = 0;
                hNumCardsFlipped = 0;
                topCard = 0;
                // Disable start button
                btn_start.setEnabled(false);
                // Enable hit & stay buttons
                btn_hit.setEnabled(true);
                btn_stay.setEnabled(true);
                // Retrieve player name entered from shared prefs:
                player.setName(prefs.getString("playerName", null));
                // Set player score labelS:
                lbl_pScore.setText(player.getName() + "'s Score:");
                lbl_pScoreVal.setText(String.valueOf(player.getScore()));
                lbl_pHand.setText(player.getName() + "'s Hand Value:");
                player.setHandValue(0); // In case not first hand
                lbl_pHandVal.setText(String.valueOf(player.getHandValue()));
                // Set house score labelS:
                lbl_hScore.setText(house.getName() + "'s Score:");
                lbl_hScoreVal.setText(String.valueOf(house.getScore()));
                lbl_hHand.setText(house.getName() + "'s Hand Value:");
                house.setHandValue(0); // In case not first hand
                lbl_hHandVal.setText(String.valueOf(house.getHandValue()));

                // TEST: DECK INITIALIZATION
//                for(int i = 0; i < deck.length; i++) {
//                    Log.d("TEST", "Card " + i + ": " + deck[i].getName() + " of " + deck[i].getSuite());
//                }

                // Randomize entries in card array
                deck = shuffleDeck(deck);
                // TEST: ENSURE DECK SHUFFLED
//                for(int i = 0; i < deck.length; i++) {
//                    Log.d("TEST", "Card " + i + ": " + deck[i].getName() + " of " + deck[i].getSuite());
//                }

                // Show first two player cards (1st & 2nd entries in array)
                // Check for ace
                checkForAce(deck[topCard], player);
                // Show first card
                pHand[pNumCardsFlipped].setImageResource(deck[topCard].getImage());
                // Adjust player's hand value
                player.setHandValue(deck[topCard].getValue());
                // Increment to next card
                pNumCardsFlipped++;
                topCard++;
                // Repeat for card 2
                checkForAce(deck[topCard], player);
                pHand[pNumCardsFlipped].setImageResource(deck[topCard].getImage());
                player.setHandValue(player.getHandValue() + deck[topCard].getValue());
                pNumCardsFlipped++;
                topCard++;
                // Update player hand value label
                lbl_pHandVal.setText(String.valueOf(player.getHandValue()));
            }
        });

        // "HIT" BUTTON-CLICK ROUTINE:
        btn_hit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForAce(deck[pNumCardsFlipped], player);
                // Show new card
                pHand[pNumCardsFlipped].setImageResource(deck[topCard].getImage());
                // Adjust player's hand value
                player.setHandValue(player.getHandValue() + deck[topCard].getValue());
                // Increment to next card
                pNumCardsFlipped++;
                topCard++;
                // Update player hand value label
                lbl_pHandVal.setText(String.valueOf(player.getHandValue()));
                // Check for "bust"
                if(player.getHandValue() > 21) {
                    // Display winner
                    Toast.makeText(getActivity(), player.getName() + " has busted. House wins...", Toast.LENGTH_LONG).show();
                    houseWinsRoutine(house, player, lbl_hScoreVal, lbl_pScoreVal);
                    // Disable hit/stay buttons
                    btn_hit.setEnabled(false);
                    btn_stay.setEnabled(false);
                    // Enable start button
                    btn_start.setEnabled(true);
                }
                // Check hand count
                if(pNumCardsFlipped > 4) {
                    // No more cards left to flip! Disable hit button
                    btn_hit.setEnabled(false); // Player can only stay now!
                }
            }
        });

        // "STAY" BUTTON-CLICK ROUTINE:
        btn_stay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // While value of house's hand is less than player's
                while( ( house.getHandValue() < player.getHandValue() ) && (hNumCardsFlipped < 5) ) {
                    // House keeps drawing cards
                    checkForAce(deck[hNumCardsFlipped], house);
                    // Show new card
                    hHand[hNumCardsFlipped].setImageResource(deck[topCard].getImage());
                    // Adjust house's hand value
                    house.setHandValue(house.getHandValue() + deck[topCard].getValue());
                    // Increment to next card
                    hNumCardsFlipped++;
                    topCard++;
                    // Update house hand value label
                    lbl_hHandVal.setText(String.valueOf(house.getHandValue()));
                }

                // Check for "bust"
                if(house.getHandValue() > 21) {
                    // Display winner
                    Toast.makeText(getActivity(),"House has busted. " + player.getName() + " wins!", Toast.LENGTH_LONG).show();
                    // House loses 50pts
                    house.setScore(house.getScore() - 50);
                    // Update house score label
                    lbl_hScoreVal.setText(String.valueOf(house.getScore()));
                    // Player gets 50pts
                    player.setScore(player.getScore() + 50);
                    // Update player score label
                    lbl_pScoreVal.setText(String.valueOf(player.getScore()));
                }
                else {
                    // Display winner
                    Toast.makeText(getActivity(),"House hand of " + house.getHandValue() + " beats " + player.getName() + "'s hand of " + player.getHandValue() + ". House wins...", Toast.LENGTH_LONG).show();
                    // Display winner
                    houseWinsRoutine(house, player, lbl_hScoreVal, lbl_pScoreVal);
                }
                // Disable hit/stay buttons
                btn_hit.setEnabled(false);
                btn_stay.setEnabled(false);
                // Enable start button
                btn_start.setEnabled(true);
            }
        });
        return root;
    }

    // Fisher-Yates shuffle
    static Card[] shuffleDeck(Card[] array) {
        Random rnd = ThreadLocalRandom.current();
        for(int i = array.length - 1; i > 0; i--) {
            final int swapIndex = (int) ( (Math.random() * (i + 1) ) );
            final Card currentCard = array[i];
            final Card cardToSwap = array[swapIndex];
            array[i] = cardToSwap;
            array[swapIndex] = currentCard;
        }
        return array;
    }

    // Dynamically sets value of aces based on player's current hand value
    static void checkForAce(Card c, Player p) {
        if(c.getName().equals("ace")) {
            // Set value depending on player's hand value
            if(p.getHandValue() < 12) {
                c.setValue(10);
            }
            else {
                c.setValue(1);
            }
        }
    }

    // Routine that runs when house wins
    static void houseWinsRoutine(Player h, Player p, TextView hs, TextView ps) {
        // House wins 50pts
        h.setScore(h.getScore() + 50);
        // Update house score label
        hs.setText(String.valueOf(h.getScore()));
        // Player loses 50pts
        p.setScore(p.getScore() - 50);
        // Update player score label
        ps.setText(String.valueOf(p.getScore()));
    }

    static void initializeCards(Card[] deck) {
        for (int i = 0; i < deck.length; i++) {
            deck[i] = new Card();
            switch (i) {
                case 0:
                    deck[i].setSuite("Clubs");
                    deck[i].setName("ace");
                    deck[i].setValue(0);
                    deck[i].setImage(R.drawable.img_clubs_ace);
                    break;
                case 1:
                    deck[i].setSuite("Clubs");
                    deck[i].setName("two");
                    deck[i].setValue(2);
                    deck[i].setImage(R.drawable.img_clubs_2);
                    break;
                case 2:
                    deck[i].setSuite("Clubs");
                    deck[i].setName("three");
                    deck[i].setValue(3);
                    deck[i].setImage(R.drawable.img_clubs_3);
                    break;
                case 3:
                    deck[i].setSuite("Clubs");
                    deck[i].setName("four");
                    deck[i].setValue(4);
                    deck[i].setImage(R.drawable.img_clubs_4);
                    break;
                case 4:
                    deck[i].setSuite("Clubs");
                    deck[i].setName("five");
                    deck[i].setValue(5);
                    deck[i].setImage(R.drawable.img_clubs_5);
                    break;
                case 5:
                    deck[i].setSuite("Clubs");
                    deck[i].setName("six");
                    deck[i].setValue(6);
                    deck[i].setImage(R.drawable.img_clubs_6);
                    break;
                case 6:
                    deck[i].setSuite("Clubs");
                    deck[i].setName("seven");
                    deck[i].setValue(7);
                    deck[i].setImage(R.drawable.img_clubs_7);
                    break;
                case 7:
                    deck[i].setSuite("Clubs");
                    deck[i].setName("eight");
                    deck[i].setValue(8);
                    deck[i].setImage(R.drawable.img_clubs_8);
                    break;
                case 8:
                    deck[i].setSuite("Clubs");
                    deck[i].setName("nine");
                    deck[i].setValue(9);
                    deck[i].setImage(R.drawable.img_clubs_9);
                    break;
                case 9:
                    deck[i].setSuite("Clubs");
                    deck[i].setName("ten");
                    deck[i].setValue(10);
                    deck[i].setImage(R.drawable.img_clubs_10);
                    break;
                case 10:
                    deck[i].setSuite("Clubs");
                    deck[i].setName("jack");
                    deck[i].setValue(10);
                    deck[i].setImage(R.drawable.img_clubs_jack);
                    break;
                case 11:
                    deck[i].setSuite("Clubs");
                    deck[i].setName("queen");
                    deck[i].setValue(10);
                    deck[i].setImage(R.drawable.img_clubs_queen);
                    break;
                case 12:
                    deck[i].setSuite("Clubs");
                    deck[i].setName("king");
                    deck[i].setValue(10);
                    deck[i].setImage(R.drawable.img_clubs_king);
                    break;
                case 13:
                    deck[i].setSuite("Diamonds");
                    deck[i].setName("ace");
                    deck[i].setValue(0);
                    deck[i].setImage(R.drawable.img_diamonds_ace);
                    break;
                case 14:
                    deck[i].setSuite("Diamonds");
                    deck[i].setName("two");
                    deck[i].setValue(2);
                    deck[i].setImage(R.drawable.img_diamonds_2);
                    break;
                case 15:
                    deck[i].setSuite("Diamonds");
                    deck[i].setName("three");
                    deck[i].setValue(3);
                    deck[i].setImage(R.drawable.img_diamonds_3);
                    break;
                case 16:
                    deck[i].setSuite("Diamonds");
                    deck[i].setName("four");
                    deck[i].setValue(4);
                    deck[i].setImage(R.drawable.img_diamonds_4);
                    break;
                case 17:
                    deck[i].setSuite("Diamonds");
                    deck[i].setName("five");
                    deck[i].setValue(5);
                    deck[i].setImage(R.drawable.img_diamonds_5);
                    break;
                case 18:
                    deck[i].setSuite("Diamonds");
                    deck[i].setName("six");
                    deck[i].setValue(6);
                    deck[i].setImage(R.drawable.img_diamonds_6);
                    break;
                case 19:
                    deck[i].setSuite("Diamonds");
                    deck[i].setName("seven");
                    deck[i].setValue(7);
                    deck[i].setImage(R.drawable.img_diamonds_7);
                    break;
                case 20:
                    deck[i].setSuite("Diamonds");
                    deck[i].setName("eight");
                    deck[i].setValue(8);
                    deck[i].setImage(R.drawable.img_diamonds_8);
                    break;
                case 21:
                    deck[i].setSuite("Diamonds");
                    deck[i].setName("nine");
                    deck[i].setValue(9);
                    deck[i].setImage(R.drawable.img_diamonds_9);
                    break;
                case 22:
                    deck[i].setSuite("Diamonds");
                    deck[i].setName("ten");
                    deck[i].setValue(10);
                    deck[i].setImage(R.drawable.img_diamonds_10);
                    break;
                case 23:
                    deck[i].setSuite("Diamonds");
                    deck[i].setName("jack");
                    deck[i].setValue(10);
                    deck[i].setImage(R.drawable.img_diamonds_jack);
                    break;
                case 24:
                    deck[i].setSuite("Diamonds");
                    deck[i].setName("queen");
                    deck[i].setValue(10);
                    deck[i].setImage(R.drawable.img_diamonds_queen);
                    break;
                case 25:
                    deck[i].setSuite("Diamonds");
                    deck[i].setName("king");
                    deck[i].setValue(10);
                    deck[i].setImage(R.drawable.img_diamonds_king);
                    break;
                case 26:
                    deck[i].setSuite("Hearts");
                    deck[i].setName("ace");
                    deck[i].setValue(0);
                    deck[i].setImage(R.drawable.img_hearts_ace);
                    break;
                case 27:
                    deck[i].setSuite("Hearts");
                    deck[i].setName("two");
                    deck[i].setValue(2);
                    deck[i].setImage(R.drawable.img_hearts_2);
                    break;
                case 28:
                    deck[i].setSuite("Hearts");
                    deck[i].setName("three");
                    deck[i].setValue(3);
                    deck[i].setImage(R.drawable.img_hearts_3);
                    break;
                case 29:
                    deck[i].setSuite("Hearts");
                    deck[i].setName("four");
                    deck[i].setValue(4);
                    deck[i].setImage(R.drawable.img_hearts_4);
                    break;
                case 30:
                    deck[i].setSuite("Hearts");
                    deck[i].setName("five");
                    deck[i].setValue(5);
                    deck[i].setImage(R.drawable.img_hearts_5);
                    break;
                case 31:
                    deck[i].setSuite("Hearts");
                    deck[i].setName("six");
                    deck[i].setValue(6);
                    deck[i].setImage(R.drawable.img_hearts_6);
                    break;
                case 32:
                    deck[i].setSuite("Hearts");
                    deck[i].setName("seven");
                    deck[i].setValue(7);
                    deck[i].setImage(R.drawable.img_hearts_7);
                    break;
                case 33:
                    deck[i].setSuite("Hearts");
                    deck[i].setName("eight");
                    deck[i].setValue(8);
                    deck[i].setImage(R.drawable.img_hearts_8);
                    break;
                case 34:
                    deck[i].setSuite("Hearts");
                    deck[i].setName("nine");
                    deck[i].setValue(9);
                    deck[i].setImage(R.drawable.img_hearts_9);
                    break;
                case 35:
                    deck[i].setSuite("Hearts");
                    deck[i].setName("ten");
                    deck[i].setValue(10);
                    deck[i].setImage(R.drawable.img_hearts_10);
                    break;
                case 36:
                    deck[i].setSuite("Hearts");
                    deck[i].setName("jack");
                    deck[i].setValue(10);
                    deck[i].setImage(R.drawable.img_hearts_jack);
                    break;
                case 37:
                    deck[i].setSuite("Hearts");
                    deck[i].setName("queen");
                    deck[i].setValue(10);
                    deck[i].setImage(R.drawable.img_hearts_queen);
                    break;
                case 38:
                    deck[i].setSuite("Hearts");
                    deck[i].setName("king");
                    deck[i].setValue(10);
                    deck[i].setImage(R.drawable.img_hearts_king);
                    break;
                case 39:
                    deck[i].setSuite("Spades");
                    deck[i].setName("ace");
                    deck[i].setValue(0);
                    deck[i].setImage(R.drawable.img_spades_ace);
                    break;
                case 40:
                    deck[i].setSuite("Spades");
                    deck[i].setName("two");
                    deck[i].setValue(2);
                    deck[i].setImage(R.drawable.img_spades_2);
                    break;
                case 41:
                    deck[i].setSuite("Spades");
                    deck[i].setName("three");
                    deck[i].setValue(3);
                    deck[i].setImage(R.drawable.img_spades_3);
                    break;
                case 42:
                    deck[i].setSuite("Spades");
                    deck[i].setName("four");
                    deck[i].setValue(4);
                    deck[i].setImage(R.drawable.img_spades_4);
                    break;
                case 43:
                    deck[i].setSuite("Spades");
                    deck[i].setName("five");
                    deck[i].setValue(5);
                    deck[i].setImage(R.drawable.img_spades_5);
                    break;
                case 44:
                    deck[i].setSuite("Spades");
                    deck[i].setName("six");
                    deck[i].setValue(6);
                    deck[i].setImage(R.drawable.img_spades_6);
                    break;
                case 45:
                    deck[i].setSuite("Spades");
                    deck[i].setName("seven");
                    deck[i].setValue(7);
                    deck[i].setImage(R.drawable.img_spades_7);
                    break;
                case 46:
                    deck[i].setSuite("Spades");
                    deck[i].setName("eight");
                    deck[i].setValue(8);
                    deck[i].setImage(R.drawable.img_spades_8);
                    break;
                case 47:
                    deck[i].setSuite("Spades");
                    deck[i].setName("nine");
                    deck[i].setValue(9);
                    deck[i].setImage(R.drawable.img_spades_9);
                    break;
                case 48:
                    deck[i].setSuite("Spades");
                    deck[i].setName("ten");
                    deck[i].setValue(10);
                    deck[i].setImage(R.drawable.img_spades_10);
                    break;
                case 49:
                    deck[i].setSuite("Spades");
                    deck[i].setName("jack");
                    deck[i].setValue(10);
                    deck[i].setImage(R.drawable.img_spades_jack);
                    break;
                case 50:
                    deck[i].setSuite("Spades");
                    deck[i].setName("queen");
                    deck[i].setValue(10);
                    deck[i].setImage(R.drawable.img_spades_queen);
                    break;
                case 51:
                    deck[i].setSuite("Spades");
                    deck[i].setName("king");
                    deck[i].setValue(10);
                    deck[i].setImage(R.drawable.img_spades_king);
                    break;
                default: break;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
/*
====================================================================================================
                                            Works Cited
====================================================================================================
    Huynh, A.-T. (2018, July 26). Randomness is hard: Learning about the fisher-yates shuffle
        algorithm &amp; random number generation. Medium. Retrieved November 22, 2021,
        from https://medium.com/@oldwestaction/randomness-is-hard-e085decbcbb2.
 */

