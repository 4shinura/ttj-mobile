package eu.esupec.ttj_mobile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OffreAdapter extends RecyclerView.Adapter<OffreAdapter.OffreViewHolder> {

    private List<Offre> offres;

    public OffreAdapter(List<Offre> offres) {
        this.offres = offres;
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
        holder.tvDescription.setText(offre.getDescription());
        holder.tvType.setText(offre.getTypeContrat());
        holder.tvDate.setText(offre.getDatePublication());

        // Petite animation lors de l'apparition
        holder.itemView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.fade_in));
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