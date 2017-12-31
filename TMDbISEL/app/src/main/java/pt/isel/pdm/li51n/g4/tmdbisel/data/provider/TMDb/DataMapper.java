package pt.isel.pdm.li51n.g4.tmdbisel.data.provider.TMDb;

import android.support.annotation.NonNull;

import java.util.List;

import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.Movie;
import pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB.MovieResults;

class DataMapper {

    @NonNull
    public List<Movie> convertToList(MovieResults dto) {

        return dto.getResults();
    }


}
