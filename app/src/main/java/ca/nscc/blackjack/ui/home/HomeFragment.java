package ca.nscc.blackjack.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import ca.nscc.blackjack.R;
import ca.nscc.blackjack.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    // VARIABLE DECLARATIONS
    // Keyboard Dismiss:
    private ConstraintLayout cLayout;

    // GUI:
    TextView lbl_enterName;
    EditText txtbx_enterName;
    Button btn_submitName;

    // Preferences:
    SharedPreferences prefs;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // VARIABLE INITIALIZATIONS:
        cLayout = (ConstraintLayout) root.findViewById(R.id.cl_home);
        cLayout.setBackgroundResource(R.drawable.img_bg);

        lbl_enterName = (TextView) root.findViewById(R.id.lbl_enterName);
        txtbx_enterName = (EditText) root.findViewById(R.id.txtbx_enterName);
        btn_submitName = (Button) root.findViewById(R.id.btn_submitName);
        btn_submitName.setBackgroundColor(Color.RED);
        btn_submitName.getBackground().setAlpha(192);

        prefs = getActivity().getSharedPreferences("game_data", Context.MODE_PRIVATE);

        // ROUTINE THAT DISMISSES ON-SCREEN KEYBOARD ON BACKGROUND TAP:
        cLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager =
                        (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
            }
        });

        // ROUTINE TRIGGERED BY CLICKING "SUBMIT NAME" BUTTON:
        btn_submitName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String playerName = txtbx_enterName.getText().toString();

                // If textbox NOT empty (name entered):
                if(!playerName.equals("")) {
                    // Store player name in shared preferences:
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("playerName", playerName);
                    editor.commit();

                    // TEST:
                    // Retrieve player name entered from shared prefs:
                    String testPlayerName = prefs.getString("playerName", null);
                    // Display retrieved name in toast msg:
                    Toast.makeText(getActivity(), "Welcome, " + testPlayerName + "!", Toast.LENGTH_SHORT).show();
                }

                // Dismiss on-screen keyboard:
                InputMethodManager inputMethodManager =
                        (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}