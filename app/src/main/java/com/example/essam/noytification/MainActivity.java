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
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    final static String TAG = "MainActivity";
    final static String KEY_TITLE = "title";
    final static String KEY_DESCRIPTION = "description";

    private EditText title_edit;
    private EditText description_edit;
    private TextView show_note;
    private EditText priority_edit;

    private FirebaseFirestore firebaseFirestore;
    private DocumentReference noteRef;
    private CollectionReference collectionReference;

    // for paging
    private DocumentSnapshot lastNote;

//    @Override
//    protected void onStart() {
//        super.onStart();
//        // real time fetch data
//        // path this because when the activity detached the listener removed
//        collectionReference.orderBy("priority", Query.Direction.DESCENDING).addSnapshotListener(this, new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                if (e != null) {
//                    return;
//                }
//                String data = "";
//                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
//                    Note note = queryDocumentSnapshot.toObject(Note.class);
//                    note.setDocumentId(queryDocumentSnapshot.getId());
//                    data += "ID :" + note.getDocumentId() + "\n" +
//                            "Title :" + note.getTitle() + "\n" + "Description :" + note.getDescription()
//                            + "Priority:" + note.getPriority()
//                            + "\n\n";
//
//
//                }
//                displayData(data);
//            }
//
//        });
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        firebaseFirestore = FirebaseFirestore.getInstance();
        noteRef = firebaseFirestore.document("NoteBook/Note");
        collectionReference = firebaseFirestore.collection("NoteBook");

        //createBatchedWrite();
       // createTransactions();

    }


    private void initView() {
        description_edit = findViewById(R.id.description);
        title_edit = findViewById(R.id.title);
        show_note = findViewById(R.id.showNote);
        priority_edit = findViewById(R.id.priority);
    }

    public void saveNote(View view) {
        String title = title_edit.getText().toString();
        String description = description_edit.getText().toString();
        int priority = 0;
        if (priority_edit.getText().toString().length() == 0) {
            priority = 0;
        } else
            priority = Integer.valueOf(priority_edit.getText().toString());

        if (!TextUtils.isEmpty(title) || !TextUtils.isEmpty(description)) {
            saveInFireBase(title, description, priority);
        } else {
            Toast.makeText(this, "Please Fill The Title and Description.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveInFireBase(String title, String description, int priority) {
        Note note = new Note(title, description, priority);
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
        // add sub note to note "collection to document
        collectionReference.document().collection("Child Notes ").add(note);
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
        //create two query and merge in one
//        Task task1 = collectionReference
//                .whereGreaterThan("priority", 2)
//                .orderBy("priority", Query.Direction.DESCENDING)
//                .get();
//
//        Task task2 = collectionReference
//                .whereLessThan("priority", 2)
//                .orderBy("priority", Query.Direction.DESCENDING)
//                .get();
//
//        Task<List<QuerySnapshot>> allTask = Tasks.whenAllSuccess(task1, task2);
//        allTask.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
//            @Override
//            public void onSuccess(List<QuerySnapshot> querySnapshots) {
//
//                String data = "";
//                for (QuerySnapshot queryDocumentSnapshots : querySnapshots) {
//                    for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
//                        Note note = queryDocumentSnapshot.toObject(Note.class);
//                        note.setDocumentId(queryDocumentSnapshot.getId());
//                        data += "ID :" + note.getDocumentId() + "\n" +
//                                "Title :" + note.getTitle() + "\n" + "Description :" + note.getDescription()
//                                + "Priority:" + note.getPriority()
//                                + "\n\n";
//
//                    }
//                }
//                displayData(data);
//
//            }
//        });

        // create paging when fetch data
        Query query;
        if (lastNote == null) {
            query = collectionReference.orderBy("priority").limit(3);
        } else {
            query = collectionReference.orderBy("priority")
                    .startAfter(lastNote)
                    .limit(3);
        }
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                String data = "";
                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                    Note note = queryDocumentSnapshot.toObject(Note.class);
                    note.setDocumentId(queryDocumentSnapshot.getId());
                    data += "ID :" + note.getDocumentId() + "\n" +
                            "Title :" + note.getTitle() + "\n" + "Description :" + note.getDescription()
                            + "Priority:" + note.getPriority() + "\n"
                            + "\n\n";

                }
                if (queryDocumentSnapshots.size() > 0) {
                    data += "__________" + "\n";
                    displayData(data);
                    lastNote = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                }

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
        show_note.append(data);
    }

    private void clearField() {
        description_edit.setText("");
        title_edit.setText("");
    }

    // atomic operation make multiple of operations and must  all success to change happen if one failed not change happen
    private void createBatchedWrite() {
        WriteBatch batch = firebaseFirestore.batch();
        // first one
        DocumentReference doc1 = collectionReference.document("New Note");
        batch.set(doc1, new Note("new note ", "new note", 1));
        // second operation

        DocumentReference doc2 = collectionReference.document("Ws5tCrHhdlDNiT1wC2Ud");
        batch.update(doc2, "title", "updated note");
        // third operation
        DocumentReference doc3 = collectionReference.document();
        batch.set(doc3, new Note("new note", "new with random id ", 1));
        // operation four
        DocumentReference doc4 = collectionReference.document("nfWvz0wcVefzxH49Mb0M");
        batch.delete(doc4);

        batch.commit().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                show_note.setText(e.getMessage());
            }
        });


    }

    private void createTransactions() {
        firebaseFirestore.runTransaction(new Transaction.Function<Long>() {
            @android.support.annotation.Nullable
            @Override
            public Long apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                // read  operation first
                DocumentReference noteReference = collectionReference.document("New Note");
                DocumentSnapshot documentSnapshot = transaction.get(noteReference);
                Long priorityNew = documentSnapshot.getLong("priority") + 1;
                // write operations second
                transaction.update(noteReference, "priority", priorityNew);

                return priorityNew;
            }
        }).addOnSuccessListener(new OnSuccessListener<Long>() {
            @Override
            public void onSuccess(Long aLong) {
                Toast.makeText(MainActivity.this, "New Value  : " + aLong, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
