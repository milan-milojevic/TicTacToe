package com.conichi.tictactoe.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.conichi.tictactoe.R;
import com.conichi.tictactoe.activity.HomeActivity;
import com.conichi.tictactoe.entity.Board;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.app_name));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    //this is the click listener for all 3 buttons
    @OnClick({R.id.btnHumanVsHuman, R.id.btnHumanVsAi, R.id.btnAiVsHuman})
    void clickNewGame(View v) {
        //create the new GameFragment
        GameFragment gameFragment = new GameFragment();
        Bundle bundle = new Bundle();

        //if the user chose to play with the AI, set AI's sign accordingly
        switch (v.getId()) {
            case R.id.btnHumanVsAi:
                bundle.putString(GameFragment.AI_SIGN, Board.Value.O.name());
                break;
            case R.id.btnAiVsHuman:
                bundle.putString(GameFragment.AI_SIGN, Board.Value.X.name());
                break;
        }

        gameFragment.setArguments(bundle);

        //load the GameFragment
        ((HomeActivity) getActivity()).setCurrentFragment(gameFragment);
        getActivity().setTitle(((Button) v).getText());
    }

}
