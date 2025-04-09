package com.example.films;

import android.provider.MediaStore;

import java.util.List;

public class FilmResponce {

    private int kinopoiskId;
    private  String nameRu;
    private String description;
    private int year;
    private float ratingKinopoisk;
    private String posterUrl;
    private List<Genre> genres;

    public String getName() {
        return nameRu;
    }

    public String getDescription() {
        return description;
    }

    public float getRatingKinopoisk() {
        return ratingKinopoisk;
    }

    public int getKinopoiskId() {
        return kinopoiskId;
    }

    public int getYear() {
        return year;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public String getNameRu() {
        return nameRu;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    @Override
    public String toString() {
        return "FilmResponce{" +
                "kinopoiskId=" + kinopoiskId +
                ", nameRu='" + nameRu + '\'' +
                ", description='" + description + '\'' +
                ", year=" + year +
                ", ratingKinopoisk=" + ratingKinopoisk +
                ", posterUrl='" + posterUrl + '\'' +
                ", genres=" + genres +
                '}';
    }
}

