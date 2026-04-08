package eu.esupec.ttj_mobile.api;

import java.util.List;
import eu.esupec.ttj_mobile.entity.Offre;
import eu.esupec.ttj_mobile.entity.LoginRequest;
import eu.esupec.ttj_mobile.entity.LoginResponse;
import eu.esupec.ttj_mobile.entity.RegisterRequest;
import eu.esupec.ttj_mobile.entity.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Header;

public interface ApiService {
    @GET("offres")
    Call<List<Offre>> getOffres();

    @GET("offres/{id}")
    Call<Offre> getOffre(@Path("id") int id);

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<LoginResponse> register(@Body RegisterRequest request);

    @GET("auth/me")
    Call<User> getProfile();

    @GET("admin/offres")
    Call<List<Offre>> getAdminOffres(@Header("Authorization") String bearerToken);
}