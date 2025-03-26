package com.example.pexeso;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private GridView gridView;
    private TextView tvScore;
    private Button btnReset;


    private int[] cardImages;

    private boolean[] cardFlipped;
    private boolean[] cardMatched;
    private int firstSelected = -1;
    private int secondSelected = -1;
    private int score = 0;
    private boolean isProcessing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = findViewById(R.id.gridView);
        tvScore = findViewById(R.id.tvScore);
        btnReset = findViewById(R.id.btnReset);
        initGame();

        btnReset.setOnClickListener(v -> initGame());
    }

    private void initGame() {
        List<Integer> letters = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            char letter = (char) ('A' + i);
            letters.add((int) letter);
            letters.add((int) letter);
        }

        Collections.shuffle(letters);
        cardImages = new int[letters.size()];
        for (int i = 0; i < letters.size(); i++) {
            cardImages[i] = letters.get(i);
        }

        cardFlipped = new boolean[cardImages.length];
        cardMatched = new boolean[cardImages.length];
        firstSelected = -1;
        secondSelected = -1;
        score = 0;
        isProcessing = false;

        tvScore.setText("Score: " + score);
        gridView.setAdapter(new CardAdapter());
    }

    private class CardAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return cardImages.length;
        }

        @Override
        public Object getItem(int position) {
            return cardImages[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.card_item, parent, false);
            }

            View cardBack = view.findViewById(R.id.card_back);
            View cardFront = view.findViewById(R.id.card_front);
            TextView tvCardLetter = view.findViewById(R.id.tvCardLetter);

            if (cardMatched[position]) {
                cardBack.setVisibility(View.INVISIBLE);
                cardFront.setVisibility(View.VISIBLE);
            } else if (cardFlipped[position]) {
                cardBack.setVisibility(View.INVISIBLE);
                cardFront.setVisibility(View.VISIBLE);
            } else {
                cardBack.setVisibility(View.VISIBLE);
                cardFront.setVisibility(View.INVISIBLE);
            }

            tvCardLetter.setText(String.valueOf((char) cardImages[position]));

            view.setOnClickListener(v -> {
                if (isProcessing || cardMatched[position] || cardFlipped[position]) {
                    return;
                }

                if (firstSelected == -1) {
                    firstSelected = position;
                    cardFlipped[position] = true;
                    notifyDataSetChanged();
                } else if (secondSelected == -1 && position != firstSelected) {
                    secondSelected = position;
                    cardFlipped[position] = true;
                    notifyDataSetChanged();

                    checkForMatch();
                }
            });

            return view;
        }
    }

    private void checkForMatch() {
        isProcessing = true;
        score++;
        tvScore.setText("Score: " + score);

        if (cardImages[firstSelected] == cardImages[secondSelected]) {
            cardMatched[firstSelected] = true;
            cardMatched[secondSelected] = true;
            firstSelected = -1;
            secondSelected = -1;
            isProcessing = false;
            notifyDataSetChanged();

            checkGameComplete();
        } else {
            new Handler().postDelayed(() -> {
                cardFlipped[firstSelected] = false;
                cardFlipped[secondSelected] = false;
                firstSelected = -1;
                secondSelected = -1;
                isProcessing = false;
                notifyDataSetChanged();
            }, 1000);
        }
    }

    private void checkGameComplete() {
        for (boolean matched : cardMatched) {
            if (!matched) {
                return;
            }
        }

        tvScore.setText("Game Over! Score: " + score);
    }

    private void notifyDataSetChanged() {
        ((BaseAdapter) gridView.getAdapter()).notifyDataSetChanged();
    }
}