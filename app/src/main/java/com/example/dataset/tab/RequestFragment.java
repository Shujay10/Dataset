package com.example.dataset.tab;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.dataset.Model;
import com.example.dataset.MyClass;
import com.example.dataset.R;
import com.example.dataset.adapter.StatusAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RequestFragment extends Fragment {

    Dialog status_dia;
    RecyclerView viewStat;
    ArrayList<Model> list;
    StatusAdapter adapter;

    ProgressBar bar;
    EditText tool;
    EditText stage;
    EditText comment;

    RadioButton rInt;
    RadioButton rExt;

    Button request;
    FloatingActionButton history;

    FirebaseFirestore mStore;
    Model model;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_request, container, false);

        status_dia = new Dialog(getContext());
        list = new ArrayList<>();
        mStore = FirebaseFirestore.getInstance();
        model = new Model();

        bar = root.findViewById(R.id.reqBar);
        bar.setVisibility(View.GONE);
        tool = root.findViewById(R.id.rTool);
        stage = root.findViewById(R.id.rStagr);
        comment = root.findViewById(R.id.rComments);
        rInt = root.findViewById(R.id.rInt);
        rExt = root.findViewById(R.id.rExt);
        request = root.findViewById(R.id.req);
        history = root.findViewById(R.id.history);

        status_dia.setContentView(R.layout.check_status);
        viewStat = status_dia.findViewById(R.id.viewStatus);

        setAdapter();


        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(getTool()&&getStage()&&getMode()&&getComment()){
                    bar.setVisibility(View.VISIBLE);
                    request.setEnabled(false);
                    setData();
                }else {
                    Toast.makeText(getContext(),"Fill all Fields",Toast.LENGTH_SHORT).show();
                }

            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
                bar.setVisibility(View.VISIBLE);
            }
        });

        return root;
    }

    private void setData(){

        model.setSatisfied(0);
        model.setUserName(MyClass.getUserName());

        CollectionReference collectionRef = mStore.collection("Request");
        DocumentReference newDocRef = collectionRef.document();
        String newDocId = newDocRef.getId();

        model.setId(newDocId);

        mStore.collection("Request").document(newDocId).set(model)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    clearData();
                    bar.setVisibility(View.GONE);
                    request.setEnabled(true);
                    Toast.makeText(getContext(),"Sent",Toast.LENGTH_SHORT).show();
                }else {
                    bar.setVisibility(View.GONE);
                    request.setEnabled(true);
                    Toast.makeText(getContext(),"Failed",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void getData(){

        list.clear();
        adapter.notifyDataSetChanged();

        CollectionReference sportsRef = mStore.collection("Request");
        Query query = sportsRef.whereEqualTo("userName", MyClass.getUserName());

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                list.clear();
                adapter.notifyDataSetChanged();
                if (task.isSuccessful()) {


                    if(task.getResult().isEmpty()){
                        bar.setVisibility(View.GONE);
                        Toast.makeText(getContext(),"No new Request",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Model model = document.toObject(Model.class);
                        list.add(model);
                        adapter.notifyDataSetChanged();
                    }

                    status_dia.show();
                    bar.setVisibility(View.GONE);

                    adapter.notifyDataSetChanged();

                } else {
                    bar.setVisibility(View.GONE);
                    Toast.makeText(getContext(),"Failed",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void clearData() {

        tool.setText("");
        stage.setText("");
        comment.setText("");

    }

    boolean getTool(){

        String field = tool.getText().toString();
        if(field.isEmpty()){
            return false;
        }else {
            model.setTool(field);
            return true;
        }

    }

    boolean getStage(){

        String field = stage.getText().toString();
        if(field.isEmpty()){
            return false;
        }else {
            model.setStage(field);
            return true;
        }

    }

    boolean getMode(){

        if(rInt.isChecked()){
            model.setMode("Internal");
            return true;
        } else if (rExt.isChecked()) {
            model.setMode("External");
            return true;
        }else {
            return false;
        }
    }

    boolean getComment(){

        String field = comment.getText().toString();
        if(field.isEmpty()){
            model.setComments("NIL");
        }else {
            model.setComments(field);
        }

        return true;

    }

    private void setAdapter(){
        adapter = new StatusAdapter(list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        viewStat.setItemAnimator(new DefaultItemAnimator());
        viewStat.setLayoutManager(layoutManager);
        viewStat.setAdapter(adapter);

    }

}