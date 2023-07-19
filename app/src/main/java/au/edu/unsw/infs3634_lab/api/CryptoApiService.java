package au.edu.unsw.infs3634_lab.api;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CryptoApiService {
    @GET("com.squareup.retrofit2:retrofit:2.9.0") // Replace this with the actual API endpoint URL
    Call<Response> getCryptocurrencies();
}