package androidcodehub.com.getcontact;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    ContactListViewAdapter adapter;
    private static final int RC_LOCATION_CONTACTS_PERM = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView)findViewById(R.id.contactlistView);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this.getApplicationContext()));
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                AddContactActivity.isUpdate = false;
                Intent goToAddContact = new Intent(MainActivity.this,AddContactActivity.class);
                startActivity(goToAddContact);


            }
        });

    }


    private class GetContact extends AsyncTask<String, String, List<ContactList>> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Fetching Contacts...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected List<ContactList> doInBackground(String... params) {

            List<ContactList> data = new ArrayList<ContactList>();
            String phoneNumber = "";
            String email = null;
            String displayName = "";
            Bitmap contactImage;
            Bitmap default_photo = BitmapFactory.decodeResource(MainActivity.this.getApplicationContext().getResources(), R.mipmap.ic_launcher);
            Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
            String _ID = ContactsContract.Contacts._ID;
            String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
            String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
            Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
            String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
            Uri EmailCONTENT_URI =  ContactsContract.CommonDataKinds.Email.CONTENT_URI;
            String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
            String DATA = ContactsContract.CommonDataKinds.Email.DATA;
            //Uri contactPhoto_URI = Uri.parse(ContactsContract.Contacts.Photo.PHOTO_THUMBNAIL_URI);
            ContentResolver contentResolver = MainActivity.this.getContentResolver();
            Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null);

            if (cursor.getCount() > 0) {

                while (cursor.moveToNext()) {

                    String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                    Uri my_contact_Uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contact_id);
                    InputStream photo_stream = ContactsContract.Contacts.openContactPhotoInputStream(MainActivity.this.getContentResolver(), my_contact_Uri);

                    if(photo_stream != null){

                        BufferedInputStream buf = new BufferedInputStream(photo_stream);
                        Bitmap my_btmp = BitmapFactory.decodeStream(buf);
                        contactImage = my_btmp;

                    }

                    else{

                        contactImage = default_photo;
                    }

                    displayName = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                    int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));

                    if (hasPhoneNumber > 0) {
                        // Query and loop for every phone number of the contact
                        Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);

                        while (phoneCursor.moveToNext()) {

                            phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER)).trim() + (phoneCursor.isLast() ? "" : "\n");

                            if(phoneNumber.trim().isEmpty()){

                                continue;
                            }
                        }
                        phoneCursor.close();
                        // Query and loop for every email of the contact
                        Cursor emailCursor = contentResolver.query(EmailCONTENT_URI,null, EmailCONTACT_ID+ " = ?", new String[] { contact_id }, null);
                        while (emailCursor.moveToNext()) {

                            email = emailCursor.getString(emailCursor.getColumnIndex(DATA));

                        }

                        emailCursor.close();
                    }

                    else{

                        continue;
                    }
                    data.add(new ContactList(contact_id,contactImage, displayName,phoneNumber,email));
                    phoneNumber = "";
                    email = null;
                }
            }
            return data;
        }

        @Override
        protected void onPostExecute(List<ContactList> result) {
            super.onPostExecute(result);

            Collections.sort(result,new CustomComparator());

            if (result != null) {

                adapter = new ContactListViewAdapter(MainActivity.this,R.layout.list_item,result);
                recyclerView.setAdapter(adapter);

            }

            if (pDialog.isShowing())

                pDialog.dismiss();

        }
    }

    public class CustomComparator implements Comparator<ContactList> {

        @Override
        public int compare(ContactList o1, ContactList o2) {

            return o1.getContactName().toLowerCase().compareTo(o2.getContactName().toLowerCase());

        }

    }


    public void locationAndContactsTask() {

        String[] perms = {  Manifest.permission.READ_CONTACTS };

        if (EasyPermissions.hasPermissions(MainActivity.this, perms)) {
            // Have permissions, do the thing!
            new GetContact().execute();

        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(this, getString(R.string.allow),RC_LOCATION_CONTACTS_PERM, perms);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        locationAndContactsTask();

    }
}
