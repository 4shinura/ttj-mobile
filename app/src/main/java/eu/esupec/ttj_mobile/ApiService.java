package eu.esupec.ttj_mobile;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("offres") // À adapter selon votre endpoint réel
    Call<List<Offre>> getOffres();
}