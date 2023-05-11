package com.example.dataset.tab;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dataset.Model;
import com.example.dataset.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;


public class AvailableFragment extends Fragment {

    ProgressBar bar;
    Spinner tool;
    Spinner stage;

    RadioButton vInt;
    RadioButton vExt;

    Button find;
    ImageButton download;
    TextView result;

    FirebaseFirestore mStore;
    FirebaseDatabase mReal;
    FirebaseStorage mDrive;

    ArrayAdapter toolAdapter;
    ArrayAdapter stageAdapter;

    ArrayList<String> tools;
    ArrayList<String> stages;

    Model model;

    int tl;
    int st;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_available, container, false);

        mStore = FirebaseFirestore.getInstance();
        mReal = FirebaseDatabase.getInstance();
        mDrive = FirebaseStorage.getInstance("gs://dataset-d81c9.appspot.com/");
        tools = new ArrayList<>();
        stages = new ArrayList<>();
        model = new Model();

        bar = root.findViewById(R.id.avaiBar);
        bar.setVisibility(View.GONE);
        tool = root.findViewById(R.id.vTool);
        stage = root.findViewById(R.id.vStage);
        vInt = root.findViewById(R.id.vInt);
        vExt = root.findViewById(R.id.vExt);
        find = root.findViewById(R.id.find);
        download = root.findViewById(R.id.download);
        result = root.findViewById(R.id.result);

        setAdapter();

        tool.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                tl = position;
                model.setTool(tools.get(position));
                getStages(tools.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        stage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                st = position;
                model.setStage(stages.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(tl != 0 && st != 0){
                    if(vExt.isChecked()){
                        bar.setVisibility(View.VISIBLE);
                        model.setStage(stage.getSelectedItem().toString());
                        model.setMode("External");
                        findLink();
                        find.setEnabled(false);
                        download.setEnabled(false);
                    }else if(vInt.isChecked()) {
                        bar.setVisibility(View.VISIBLE);
                        model.setStage(stage.getSelectedItem().toString());
                        model.setMode("Internal");
                        findLink();
                        find.setEnabled(false);
                        download.setEnabled(false);
                    }
                }
                else {
                    Toast.makeText(getContext(),"Select",Toast.LENGTH_SHORT).show();
                }


            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download.setEnabled(false);
                find.setEnabled(false);
                bar.setVisibility(View.VISIBLE);
                findFile();
            }
        });


        return root;
    }



    private void findLink(){

        System.out.println(model);

        mStore.collection(model.getTool()).document(model.getStage())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        result.setText(task.getResult().get(model.getMode().toLowerCase(Locale.ROOT)).toString());
                        result.setVisibility(View.VISIBLE);
                        bar.setVisibility(View.GONE);
                        find.setEnabled(true);
                        download.setEnabled(true);
                        download.setVisibility(View.VISIBLE);
                        String hyperlink = result.getText().toString();
                        SpannableStringBuilder builder = new SpannableStringBuilder();
                        SpannableString hyperlinkSpan = new SpannableString(hyperlink);
                        hyperlinkSpan.setSpan(new ClickableSpan() {
                            @Override
                            public void onClick(View widget) {
                                // handle click event
                                try{
                                    ClipboardManager cm = (ClipboardManager)getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                    Toast.makeText(getContext(),"Copied to clipboard",Toast.LENGTH_SHORT).show();
                                    cm.setText(result.getText());
                                    Uri uri = Uri.parse(hyperlink);
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent);
                                }catch (ActivityNotFoundException e){
                                    Toast.makeText(getContext(),"Not a link",Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void updateDrawState(TextPaint ds) {
                                super.updateDrawState(ds);
                                ds.setUnderlineText(false); // remove underline
                                ds.setColor(ContextCompat.getColor(getContext(), R.color.white)); // set link color
                            }

                        }, 0, hyperlink.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        builder.append(hyperlinkSpan);
                        result.setText(builder);
                        result.setMovementMethod(LinkMovementMethod.getInstance());

                    }
                    else {
                        bar.setVisibility(View.GONE);
                        find.setEnabled(true);
                        download.setEnabled(true);
                        Toast.makeText(getContext(), "Dataset NA, Request dataset ", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    bar.setVisibility(View.GONE);
                    find.setEnabled(true);
                    download.setEnabled(true);
                    Toast.makeText(getContext(),"Failed",Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void findFile(){

        final long ONE_MEGABYTE = 1024 * 1024;

        File save = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        StorageReference mRef = mDrive.getReference().child(model.getTool())
                .child(model.getStage())
                .child(model.getMode());
                mRef.list(1).addOnCompleteListener(new OnCompleteListener<ListResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ListResult> task) {

                        if(task.getResult().getItems().isEmpty()){
                            bar.setVisibility(View.GONE);
                            find.setEnabled(true);
                            download.setEnabled(true);
                            Toast.makeText(getContext(),"File Not Found",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (task.isSuccessful()){
                            String saveName = task.getResult().getItems().get(0).getName();
                            mRef.child(task.getResult().getItems().get(0).getName()).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    bar.setVisibility(View.GONE);
                                    find.setEnabled(true);
                                    download.setEnabled(true);
                                    File saveFil = new File(save,saveName);
                                    try {
                                        FileOutputStream fileOutputStream = new FileOutputStream(saveFil);
                                        fileOutputStream.write(bytes);
                                        fileOutputStream.close();
                                        Toast.makeText(getContext(),"File Saved in Downloads",Toast.LENGTH_SHORT).show();
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }

                                }
                            });
                        }else {
                            bar.setVisibility(View.GONE);
                            find.setEnabled(true);
                            download.setEnabled(true);
                            Toast.makeText(getContext(),"File Not Found",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void setAdapter(){

        toolAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item, tools);
        toolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tool.setAdapter(toolAdapter);

        stageAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item, stages);
        stageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stage.setAdapter(stageAdapter);

        getData();
    }

    private void getData(){

        mReal.getReference("Tools").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                tools.clear();
                String tool;

                tools.add("-- Select Tool --");
                for (DataSnapshot shot : snapshot.getChildren()){
                    tool = shot.getValue().toString();
                    tools.add(tool);
                }

                HashSet<String > set = new HashSet<>(tools);

                tools.clear();
                tools.addAll(set);

                toolAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getStages(String s) {

        mReal.getReference(s).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                stages.clear();
                String stage;

                stages.add("-- Select Stages --");
                for (DataSnapshot shot : snapshot.getChildren()){
                    stage = shot.getValue().toString();
                    stages.add(stage);
                }

                stageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}