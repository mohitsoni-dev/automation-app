package android.example.checkpoint1;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<ResolveInfo> mList;
    private LayoutInflater mLayoutInflater;
    private PackageManager packageManager;

    public RecyclerViewAdapter(Context mContext, List<ResolveInfo> mList) {
        this.mContext = mContext;
        this.packageManager = mContext.getPackageManager();
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = mLayoutInflater.inflate(R.layout.app_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ResolveInfo app = mList.get(position);
        holder.label.setText(app.activityInfo.loadLabel(packageManager));
        holder.icon.setImageDrawable(app.loadIcon(packageManager));

        holder.itemView.setOnClickListener(v -> Toast.makeText(mContext, app.activityInfo.loadLabel(packageManager) + " is Clicked", Toast.LENGTH_SHORT).show());

        if(mContext instanceof AddAppActivity){
            holder.itemView.setOnClickListener(v -> {
                Toast.makeText(mContext, holder.label.getText() + " is added.", Toast.LENGTH_SHORT).show();
                MainActivity.selectedApps.add(app);
                mList.remove(app);
                notifyDataSetChanged();
            });
        }else if(mContext instanceof MainActivity){
            holder.itemView.setOnLongClickListener(v ->{

                Toast.makeText(mContext, holder.label.getText() + " is removed.", Toast.LENGTH_SHORT).show();
                mList.remove(app);
                notifyDataSetChanged();
                return true;
            });
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView icon;
        TextView label;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.appListItemImageView);
            label = itemView.findViewById(R.id.appListItemTextView);

        }
    }
}
