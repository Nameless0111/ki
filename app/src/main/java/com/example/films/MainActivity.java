package com.example.films;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private EditText filmInput;
    private Button searchButton;
    private TextView filmInfo;
    private ImageView filmPoster;

    private static final String BASE_URL = "https://kinopoiskapiunofficial.tech/api/v2.2/";
    private static final String API_KEY = "36a2fd03-ce22-4153-98df-66003f4d6b89";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        filmInput = findViewById(R.id.filmInput);
        filmInfo = findViewById(R.id.filmInfo);
        searchButton = findViewById(R.id.searchButton);
        filmPoster = findViewById(R.id.filmPoster);


        searchButton.setOnClickListener(v -> {
            String input = filmInput.getText().toString();
            if (!input.isEmpty()) {
                getFilm(Integer.parseInt(input));
            } else {
                filmInfo.setText("Введите ID фильма");
            }
        });
    }

    private void getFilm(int filmId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        API filmApi = retrofit.create(API.class);
        Call<FilmResponce> call = filmApi.getCurrentFilm(filmId, API_KEY);

        call.enqueue(new Callback<FilmResponce>() {
            @Override
            public void onResponse(Call<FilmResponce> call, Response<FilmResponce> response) {
                if (response.isSuccessful() && response.body() != null) {
                    FilmResponce filmResponce = response.body();

                    StringBuilder genresString = new StringBuilder();
                    for (Genre genre : filmResponce.getGenres()) {
                        genresString.append(genre.getGenre()).append(", ");
                    }

                    if (genresString.length() > 0) {
                        genresString.setLength(genresString.length() - 2);
                    }

                    String filmDetails =
                            "Название: " + filmResponce.getNameRu() + "\n" +
                            "Год: " + filmResponce.getYear() + "\n" +
                            "Описание: " + filmResponce.getDescription() + "\n" +
                            "Рейтинг: " + filmResponce.getRatingKinopoisk() + "\n" +
                            "Жанры: " + genresString.toString();


                    filmInfo.setText(filmDetails);


                    if (filmResponce.getPosterUrl() != null && !filmResponce.getPosterUrl().isEmpty()) {
                        Picasso.get()
                                .load(filmResponce.getPosterUrl())
                                .into(filmPoster);
                    } else {
                        filmPoster.setImageResource(R.drawable.placeholder);
                    }
                } else {

                    String errorMessage;
                    switch (response.code()) {
                        case 404:
                            errorMessage = "Ошибка: Фильм не найден";
                            break;
                        case 500:
                            errorMessage = "Ошибка: Проблемы на сервере";
                            break;
                        default:
                            errorMessage = "Ошибка: Неизвестная ошибка";
                            break;
                    }
                    Log.e("MainActivity", "Ошибка получения данных: " + response.code() + " " + errorMessage);
                    filmInfo.setText(errorMessage);
                    filmPoster.setImageResource(0);
                    filmPoster.setImageDrawable(null);
                }
            }

            @Override
            public void onFailure(Call<FilmResponce> call, Throwable t) {
                Log.e("MainActivity", "Ошибка: Проверьте подключение к интернету " + t.getMessage());
                filmInfo.setText("Ошибка: Проверьте подключение к интернету");
            }
        });
    }
}