package spotify.api.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import spotify.api.interfaces.ArtistApi;
import spotify.config.ApiUrl;
import spotify.exceptions.HttpRequestFailedException;
import spotify.exceptions.ResponseChecker;
import spotify.factories.RetrofitClientFactory;
import spotify.models.artists.ArtistFull;
import spotify.retrofit.services.ArtistService;

import java.io.IOException;

public class ArtistApiRetrofit implements ArtistApi {
    private final Logger logger = LoggerFactory.getLogger(ArtistApiRetrofit.class);
    private final String accessToken;
    private ArtistService artistService;

    public ArtistApiRetrofit(String accessToken) {
        this.accessToken = accessToken;
        setup();
    }

    @Override
    public ArtistFull getArtist(String artistId) {
        logger.trace("Constructing HTTP call to fetch an artist.");
        Call<ArtistFull> httpCall = artistService.getArtist("Bearer " + this.accessToken, artistId);

        try {
            logger.info("Executing HTTP call to fetch an artist.");
            logger.debug(String.format("%s / %s", httpCall.request().method(), httpCall.request().url().toString()));
            Response<ArtistFull> response = httpCall.execute();

            ResponseChecker.throwIfRequestHasNotBeenFulfilledCorrectly(response.errorBody());

            logger.info("Artist has been successfully fetched.");
            return response.body();
        } catch (IOException e) {
            logger.error("Fetching artist has failed.");
            throw new HttpRequestFailedException(e.getMessage());
        }
    }

    private void setup() {
        logger.trace("Requesting Retrofit HTTP client.");
        Retrofit httpClient = RetrofitClientFactory.getRetrofitClient(ApiUrl.API_URL_HTTPS + ApiUrl.VERSION);

        artistService = httpClient.create(ArtistService.class);
    }
}
