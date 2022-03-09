package ca.nscc.blackjack.ui.Credits;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.nscc.blackjack.R;
import ca.nscc.blackjack.databinding.FragmentCreditsBinding;

public class CreditsFragment extends Fragment {

    private CreditsViewModel creditsViewModel;
    private FragmentCreditsBinding binding;
    private ConstraintLayout cl;

    public static CreditsFragment newInstance() {
        return new CreditsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        creditsViewModel = new ViewModelProvider(this).get(CreditsViewModel.class);

        binding = FragmentCreditsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Set background image of constraint layout
        cl = (ConstraintLayout) root.findViewById(R.id.cl_credits);
        cl.setBackgroundResource(R.drawable.img_bg);

        return root;
//        return inflater.inflate(R.layout.fragment_credits, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}