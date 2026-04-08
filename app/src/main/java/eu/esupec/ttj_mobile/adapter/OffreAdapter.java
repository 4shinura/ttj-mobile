package eu.esupec.ttj_mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import eu.esupec.ttj_mobile.MainActivity;
import eu.esupec.ttj_mobile.R;
import eu.esupec.ttj_mobile.entity.Offre;

public class OffreAdapter extends RecyclerView.Adapter<OffreAdapter.OffreViewHolder> {

    private List<Offre> offres;
    private MainActivity activity;

    public OffreAdapter(List<Offre> offres, MainActivity activity) {
        this.offres = offres;
        this.activity = activity;
    }

    @NonNull
    @Override
    public OffreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.api_item_offre, parent, false);
        return new OffreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OffreViewHolder holder, int position) {
        Offre offre = offres.get(position);
        holder.tvTitre.setText(offre.getTitre());
        holder.tvType.setText(offre.getTypeContrat());
        holder.tvDate.setText(offre.getDatePublication());
        holder.tvDescription.setText(offre.getDescription());

        holder.itemView.setOnClickListener(v -> {
            if (activity != null) {
                activity.afficherModalOffre(offre);
            }
        });
    }

    @Override
    public int getItemCount() {
        return offres != null ? offres.size() : 0;
    }

    public static class OffreViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitre, tvDescription, tvType, tvDate;

        public OffreViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitre = itemView.findViewById(R.id.tv_titre);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvType = itemView.findViewById(R.id.tv_type);
            tvDate = itemView.findViewById(R.id.tv_date);
        }
    }
}