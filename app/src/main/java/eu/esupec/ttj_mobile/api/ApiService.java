package eu.esupec.ttj_mobile.api;

import java.util.List;
import eu.esupec.ttj_mobile.entity.Candidature;
import eu.esupec.ttj_mobile.entity.ConversationResponse;
import eu.esupec.ttj_mobile.entity.LoginRequest;
import eu.esupec.ttj_mobile.entity.LoginResponse;
import eu.esupec.ttj_mobile.entity.Offre;
import eu.esupec.ttj_mobile.entity.RegisterRequest;
import eu.esupec.ttj_mobile.entity.User;
import eu.esupec.ttj_mobile.entity.Message;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Header;

public interface ApiService {

    // --- Authentification ---

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/candidat/register")
    Call<User> registerCandidat(@Body RegisterRequest request);

    @GET("auth/profile")
    Call<User> getProfile();

    // --- Offres ---

    @GET("offres")
    Call<List<Offre>> getOffres();

    // --- Administration ---

    @GET("admin/utilisateurs")
    Call<List<User>> getUtilisateurs();

    @GET("admin/utilisateurs/{id}")
    Call<User> getUtilisateur(@Path("id") int id);

    // --- Candidatures (Candidat) ---

    @GET("candidats/candidatures")
    Call<List<Candidature>> getMyCandidatures();

    @GET("candidats/candidatures/{id}")
    Call<Candidature> getCandidature(@Path("id") int id);

    @POST("candidats/candidatures")
    Call<Candidature> postuler(@Body java.util.Map<String, Integer> body);

    @DELETE("candidats/candidatures/{id}")
    Call<Void> deleteCandidature(@Path("id") int id);

    // --- Messagerie ---
    @GET("users/messages/correspondants")
    Call<List<User>> getCorrespondants();

    @GET("users/messages/{correspondantId}")
    Call<ConversationResponse> getConversation(@Path("correspondantId") int correspondantId);

    @POST("users/messages/send/{destinataireId}")
    Call<Message> sendMessage(@Path("destinataireId") int destinataireId, @Body java.util.Map<String, String> body);

    @GET("users/messages/sent/{id}")
    Call<Message> getSentMessage(@Path("id") int id);

    @GET("users/messages/received/{id}")
    Call<Message> getReceivedMessage(@Path("id") int id);
}
