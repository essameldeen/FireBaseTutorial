package com.example.essam.noytification;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.essam.noytification.Model.Note;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    final static String TAG = "MainActivity";
    final static String KEY_TITLE = "title";
    final static String KEY_DESCRIPTION = "description";

    private EditText title_edit;
    private EditText description_edit;
    private TextView show_note;

    private FirebaseFirestore firebaseFirestore;
    private DocumentReference noteRef;
    private CollectionReference collectionReference;

    @Override
    protected void onStart() {
        super.onStart();
        // real time fetch data
        // path this because when the activity detached the listener removed
        collectionReference.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                String data = "";
                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                    Note note = queryDocumentSnapshot.toObject(Note.class);
                    note.setDocumentId(queryDocumentSnapshot.getId());
                    data += "ID :" + note.getDocumentId() + "\n" +
                            "Title :" + note.getTitle() + "\n" + "Description :" + note.getDescription() + "\n\n";

                }
                displayData(data);
            }

        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        firebaseFirestore = FirebaseFirestore.getInstance();
        noteRef = firebaseFirestore.document("NoteBook/Note");
        collectionReference = firebaseFirestore.collection("NoteBook");


    }

    private void initView() {
        description_edit = findViewById(R.id.description);
        title_edit = findViewById(R.id.title);
        show_note = findViewById(R.id.showNote);
    }

    public void saveNote(View view) {
        String title = title_edit.getText().toString();
        String description = description_edit.getText().toString();

        if (!TextUtils.isEmpty(title) || !TextUtils.isEmpty(description)) {
            saveInFireBase(title, description);
        } else {
            Toast.makeText(this, "Please Fill The Title and Description.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveInFireBase(String title, String description) {
        Note note = new Note(title, description);
//        noteRef.set(note)
//                .addOnSuccessListener(new OnSuccessListener() {
//                    @Override
//                    public void onSuccess(Object o) {
//                        Toast.makeText(getApplicationContext(), "Save  Successful", Toast.LENGTH_SHORT).show();
//                        clearField();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getApplicationContext(), "Failed To Save", Toast.LENGTH_SHORT).show();
//                Log.d("ERROR", e.getMessage());
//            }
//        });

        collectionReference.add(note);
        clearField();
    }

    public void updateNote(View view) {
        String description = description_edit.getText().toString();
        Map<String, Object> value = new HashMap();
        value.put(KEY_DESCRIPTION, description);
        noteRef.update(value);
        clearField();

    }

    // fetch all data in collection
    public void fetchData(View view) {
        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                String data = "";
                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                    Note note = queryDocumentSnapshot.toObject(Note.class);
                    note.setDocumentId(queryDocumentSnapshot.getId());
                    data += "ID :" + note.getDocumentId() + "\n" +
                            "Title :" + note.getTitle() + "\n" + "Description :" + note.getDescription() + "\n\n";

                }
                displayData(data);
            }
        });
    }


// fetch one element
//    public void fetchData(View view) {
//        noteRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if (documentSnapshot.exists()) {
//
//                    Note note = documentSnapshot.toObject(Note.class);
//                    String title = note.getTitle();
//                    String description = note.getDescription();
//                    displayData(title, description);
//
//                } else {
//                    Toast.makeText(MainActivity.this, "Note Not Found", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getApplicationContext(), "Failed To Get Data", Toast.LENGTH_SHORT).show();
//                Log.d("ERROR", e.getMessage());
//            }
//        });
//    }

    public void deleteDescription(View view) {

        Map<String, Object> value = new HashMap();
        value.put(KEY_DESCRIPTION, FieldValue.delete());
        noteRef.update(value);

    }

    public void deleteNote(View view) {
        noteRef.delete();

    }

    private void displayData(String data) {
        show_note.setText(data);
    }

    private void clearField() {
        description_edit.setText("");
        title_edit.setText("");
    }

}
