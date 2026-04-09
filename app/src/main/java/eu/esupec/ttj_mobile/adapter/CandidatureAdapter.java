package eu.esupec.ttj_mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import eu.esupec.ttj_mobile.R;
import eu.esupec.ttj_mobile.entity.Candidature;

public class CandidatureAdapter extends RecyclerView.Adapter<CandidatureAdapter.ViewHolder> {

    private List<Candidature> candidatures;
    private OnCandidatureListener listener;

    public interface OnCandidatureListener {
        void onDeleteClick(Candidature candidature);
        void onItemClick(Candidature candidature);
    }

    public CandidatureAdapter(List<Candidature> candidatures, OnCandidatureListener listener) {
        this.candidatures = candidatures;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.api_item_candidature, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Candidature candidature = candidatures.get(position);
        if (candidature.getOffre() != null) {
            holder.tvTitre.setText(candidature.getOffre().getTitre());
        } else {
            holder.tvTitre.setText("Offre inconnue");
        }
        holder.tvDate.setText("Postulé le : " + candidature.getDate());
        holder.tvStatut.setText("Statut : " + candidature.getStatut());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(candidature);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(candidature);
            }
        });
    }

    @Override
    public int getItemCount() {
        return candidatures != null ? candidatures.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitre, tvDate, tvStatut;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitre = itemView.findViewById(R.id.tv_candidature_titre);
            tvDate = itemView.findViewById(R.id.tv_candidature_date);
            tvStatut = itemView.findViewById(R.id.tv_candidature_statut);
            btnDelete = itemView.findViewById(R.id.btn_delete_candidature);
        }
    }
}
