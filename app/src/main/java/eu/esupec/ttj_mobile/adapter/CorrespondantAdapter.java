package eu.esupec.ttj_mobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import eu.esupec.ttj_mobile.R;
import eu.esupec.ttj_mobile.entity.User;

public class CorrespondantAdapter extends RecyclerView.Adapter<CorrespondantAdapter.ViewHolder> {

    private List<User> correspondents;
    private OnCorrespondantClickListener listener;

    public interface OnCorrespondantClickListener {
        void onCorrespondantClick(User user);
    }

    public CorrespondantAdapter(List<User> correspondents, OnCorrespondantClickListener listener) {
        this.correspondents = correspondents;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_correspondant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = correspondents.get(position);
        holder.tvName.setText(user.getPrenom() + " " + user.getNom());
        holder.tvEmail.setText(user.getEmail());
        holder.itemView.setOnClickListener(v -> listener.onCorrespondantClick(user));
    }

    @Override
    public int getItemCount() {
        return correspondents.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_correspondant_name);
            tvEmail = itemView.findViewById(R.id.tv_correspondant_email);
        }
    }
}
