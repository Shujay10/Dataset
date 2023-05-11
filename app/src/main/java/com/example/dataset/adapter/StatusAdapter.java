package com.example.dataset.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dataset.Model;
import com.example.dataset.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.MyViewHolder> {

    ArrayList<Model> list;

    Dialog delDialog;
    ProgressBar bar;
    TextView tool;
    TextView stage;
    Button cancel;
    Button ok;

    FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    public StatusAdapter(ArrayList<Model> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public StatusAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.tool.setText(list.get(position).getTool());
        holder.stage.setText(list.get(position).getStage());
        holder.mode.setText(list.get(position).getMode());

        switch (list.get(position).getSatisfied()){

            case 0:{
                holder.comments.setText("Requested");
                break;
            }
            case 1:{
                holder.comments.setText("Uploaded");
                break;
            }
            case 2:{
                holder.comments.setText("Not Possible");
                break;
            }

        }

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                delDialog = new Dialog(v.getContext());
                delDialog.setContentView(R.layout.del_request);
                delDialog.setCanceledOnTouchOutside(false);
                delDialog.setCancelable(false);

                bar = delDialog.findViewById(R.id.perTodelbar);
                bar.setVisibility(View.GONE);
                tool = delDialog.findViewById(R.id.dTool);
                stage = delDialog.findViewById(R.id.dStage);

                cancel = delDialog.findViewById(R.id.cancel);
                ok = delDialog.findViewById(R.id.okDelete);

                tool.setText(list.get(position).getTool());
                stage.setText(list.get(position).getStage());

                delDialog.show();

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delDialog.dismiss();
                    }
                });

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        delete(v.getContext(),position);

                    }
                });

            }
        });

    }

    private void delete(Context context, int position){

        CollectionReference collectionRef = mStore.collection("Request");
        DocumentReference documentRef = collectionRef.document(list.get(position).getId());

        documentRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    bar.setVisibility(View.GONE);
                    delDialog.dismiss();
                    list.remove(position);
                    notifyItemRemoved(position);
                }else {
                    bar.setVisibility(View.GONE);
                    Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CardView card;
        TextView tool;
        TextView stage;
        TextView mode;
        TextView comments;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            card = itemView.findViewById(R.id.card);
            tool = itemView.findViewById(R.id.viewTool);
            stage = itemView.findViewById(R.id.viewStage);
            mode = itemView.findViewById(R.id.viewMode);
            comments = itemView.findViewById(R.id.viewComments);

        }
    }
}
