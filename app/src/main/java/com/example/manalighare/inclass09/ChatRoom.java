package com.example.manalighare.inclass09;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

public class ChatRoom extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private FirebaseStorage storage;
    private StorageReference storageRef;

    private TextView User_Name;
    private ImageView Logout;
    private ImageView Select_Image;
    private ImageView Send_Message;
    private EditText MessageText;
    private Uri imageUri;
    private String imageUriString="";

    public static final int RESULT_GALLERY = 0;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    ArrayList<Message> Messages=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        setTitle("Chat Room");

        User_Name=(TextView)findViewById(R.id.User_Name);
        Logout=(ImageView)findViewById(R.id.Logout);
        Select_Image=(ImageView)findViewById(R.id.Add_Image);
        Send_Message=(ImageView)findViewById(R.id.Send_Message);
        MessageText=(EditText)findViewById(R.id.NewMessage);

        mAuth=FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Chatroom/");
        storage=FirebaseStorage.getInstance();
        storageRef=storage.getReference();


        User_Name.setText(user.getDisplayName());

        Logout.setOnClickListener(this);
        Select_Image.setOnClickListener(this);
        Send_Message.setOnClickListener(this);


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Messages.clear();

                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    Message obj=dataSnapshot1.getValue(Message.class);

                    Messages.add(obj);
                }

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRecyclerView=(RecyclerView)findViewById(R.id.RecyclerViewMessages);

        mRecyclerView.setHasFixedSize(true);


        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);



        mAdapter = new MyAdapter(Messages,mAuth);
        mRecyclerView.setAdapter(mAdapter);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_GALLERY :
                if (data != null) {
                    imageUri = data.getData();
                    Log.d("demo","URI of image is : "+imageUri);
                    Picasso.get().load(imageUri).into(Select_Image);
                    imageUriString=imageUri.toString();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.Logout:
                mAuth.getInstance().signOut();
                Intent logout_intent=new Intent(ChatRoom.this,MainActivity.class);
                startActivity(logout_intent);
                finish();
                break;


            case R.id.Add_Image:
                Intent galleryIntent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent , RESULT_GALLERY );

                break;

            case R.id.Send_Message:



                final StorageReference ref=storageRef.child("images/"+ UUID.randomUUID().toString());

                if(!imageUriString.equals("")) {
                    ref.putFile(imageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Log.d("demo","Image Uploaded");
                                    Message message_object=new Message();
                                    message_object.UserID=mAuth.getUid().toString();
                                    message_object.MessageText=MessageText.getText().toString();
                                    message_object.Time=String.valueOf(Calendar.getInstance().getTime());
                                    message_object.Name=user.getDisplayName();
                                    message_object.imageUrl=taskSnapshot.getDownloadUrl().toString();

                                    String key=myRef.push().getKey();

                                    message_object.MessageKey=key;

                                    myRef.child(key).setValue(message_object);
                                    Select_Image.setImageResource(R.drawable.addimage);
                                    MessageText.setText("");
                                    imageUri=null;
                                    imageUriString="";
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("demo", "cannot upload Image");
                        }
                    });
                }else{

                    Message message_object=new Message();
                    message_object.UserID=mAuth.getUid().toString();
                    message_object.MessageText=MessageText.getText().toString();
                    message_object.Time=String.valueOf(Calendar.getInstance().getTime());
                    message_object.Name=user.getDisplayName();
                    message_object.imageUrl="";


                    String key=myRef.push().getKey();
                    message_object.MessageKey=key;
                    myRef.child(key).setValue(message_object);
                    Select_Image.setImageResource(R.drawable.addimage);
                    MessageText.setText("");
                }

                break;
        }
    }



    static class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
        ArrayList<Message> Messages;
        FirebaseAuth mAuth;

        public MyAdapter(ArrayList<Message> Messages,FirebaseAuth mAuth) {
            this.Messages =Messages;
            this.mAuth=mAuth;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chats_row_layout, parent, false);
            MyViewHolder vh = new MyViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Message obj = Messages.get(position);
            holder.delete.setVisibility(View.INVISIBLE);
            holder.Name.setText(obj.Name.substring(0,obj.Name.indexOf(" ")));
            holder.Message.setText(obj.MessageText);
            if(!obj.imageUrl.equals("")) {
                Picasso.get().load(obj.imageUrl).into(holder.display_image);
            }else{
                holder.display_image.setImageDrawable(null);
            }
            holder.MessageKey=obj.MessageKey;
            holder.id=obj.UserID;

            if(mAuth.getUid().equals(obj.UserID)){
                holder.delete.setVisibility(View.VISIBLE);
            }

            SimpleDateFormat simpleDateFormat= new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            try{
                Date date=simpleDateFormat.parse(obj.Time);
                PrettyTime prettyTime=new PrettyTime();
                Log.d("prettyTime time is : ",""+prettyTime.format(date));
                holder.display_time.setText(prettyTime.format(date));

            }catch (ParseException e){
                e.printStackTrace();
            }




        }

        @Override
        public int getItemCount() {
            return Messages.size();
        }



        public static class MyViewHolder extends RecyclerView.ViewHolder {

            public TextView Message;
            public TextView Name;
            public TextView display_time;

            public ImageView display_image;
            public ImageView delete;

            public String id;
            public String MessageKey;

            public MyViewHolder(View v) {
                super(v);
                this.Message = v.findViewById(R.id.MsgTitle);
                this.Name = v.findViewById(R.id.first_name);
                this.display_time=v.findViewById(R.id.display_time);
                this.delete=v.findViewById(R.id.deleteButton);
                this.display_image=v.findViewById(R.id.display_image);



              this.delete.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      FirebaseDatabase database = FirebaseDatabase.getInstance();
                      DatabaseReference myRef = database.getReference("Chatroom/");
                      myRef.child(MessageKey).setValue(null);
                      Toast.makeText(v.getContext(), "Message is Deleted", Toast.LENGTH_SHORT).show();

                  }
              });
            }
        }




    }
}
