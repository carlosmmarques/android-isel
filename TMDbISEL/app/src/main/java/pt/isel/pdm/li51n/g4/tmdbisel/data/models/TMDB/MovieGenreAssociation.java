package pt.isel.pdm.li51n.g4.tmdbisel.data.models.TMDB;

import pt.isel.pdm.li51n.g4.tmdbisel.data.models.core.Attributes;

public class MovieGenreAssociation {

    @Attributes(foreignTable = "Movie")
    public Integer movieId;

    @Attributes(foreignTable = "Genre")
    public Integer genreId;
}
