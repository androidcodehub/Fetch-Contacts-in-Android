package androidcodehub.com.getcontact;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.RawContacts;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class AddContactActivity extends AppCompatActivity {

    EditText edtTxtContactName, edtTxtContactNumber, edtTxtContactEmail;
    ImageView imgContactPhoto;
    Button btnAddContact;
    View view;
    String imagePath = null;
    Uri uri;
    ExifInterface exif;
    boolean isButtonClicked = false;
    View snackView;
    Bitmap rotateBitmap;
    InputMethodManager inm;
    public static boolean isUpdate;
    String cID, cName, cNumber, cEmail;
    Bitmap cPhoto;
    public boolean FLAG = true;
    Toolbar mToolbar;
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.addcontact);

        view = findViewById(R.id.rootView);
        edtTxtContactName = (EditText) findViewById(R.id.edtTxtName);
        edtTxtContactNumber = (EditText) findViewById(R.id.edtTxtNumber);
        edtTxtContactEmail = (EditText) findViewById(R.id.edtTxtEmail);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Add Contact");
        mToolbar.setTitleTextColor(Color.WHITE);
        imgContactPhoto = (ImageView) findViewById(R.id.imgPhoto);
        btnAddContact = (Button) findViewById(R.id.btnAddContact);
        inm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (isUpdate) {

            cID = getIntent().getExtras().getString("cID");
            cName = getIntent().getExtras().getString("cName");
            cNumber = getIntent().getExtras().getString("cNumber");
            cEmail = getIntent().getExtras().getString("cEmail");
            cPhoto = getIntent().getParcelableExtra("cPhoto");
            btnAddContact.setText("Add Contact");
            edtTxtContactName.setText(cName);
            edtTxtContactNumber.setText(cNumber);
            edtTxtContactEmail.setText(cEmail);
            imgContactPhoto.setImageBitmap(cPhoto);
            FLAG = false;

        } else {

            btnAddContact.setText("Add Contact");
        }

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //
                onBackPressed();

            }
        });

        btnAddContact.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (btnAddContact.getText().toString().matches("Add Contact")) {

                    if (edtTxtContactName.getText().toString().trim().isEmpty()) {

                        Snackbar.make(v, "You must provide name.", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).show();
                    } else if (edtTxtContactNumber.getText().toString().trim().isEmpty()) {
                        Snackbar.make(v, "You must provide number.", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).show();
                    } else {
                        isButtonClicked = true;
                        snackView = v;
                        inm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        addContact();
                    }
                } else if (btnAddContact.getText().toString().equals("Add Contact")) {

                    String strName = edtTxtContactName.getText().toString();
                    String strNumber = edtTxtContactNumber.getText().toString();
                    String strEmail = edtTxtContactEmail.getText().toString();
                    imgContactPhoto.setDrawingCacheEnabled(true);
                    Bitmap bitmap = imgContactPhoto.getDrawingCache();

                    if (updateContact(cID, strName, strNumber, strEmail, bitmap)) {

                        Snackbar.make(v, "Contact updated successfully.", Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(v, "Failed to update contact.", Snackbar.LENGTH_LONG).show();
                    }

                } else {

                    Snackbar.make(v, "Some internal error occured, Please try after some time.", Snackbar.LENGTH_LONG).show();
                }

            }
        });

        imgContactPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectImage();

            }

        });

    }

    private void selectImage() {

        final CharSequence[] option = {"Take Photo", "Choose from Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(
                AddContactActivity.this);
        builder.setTitle("Add Photo!");
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        builder.setItems(option, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (option[item].equals("Take Photo")) {

                    clickPhotoFromCamera();

                } else if (option[item].equals("Choose from Gallery"))

                {
                    uploadPhotoFromCamera();
                }
            }

        });
        builder.show();
    }

    private void clickPhotoFromCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), imageFileName);
        uri = Uri.fromFile(imageStorageDir);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, 1);

    }

    private void uploadPhotoFromCamera() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {

                imagePath = uri.getPath();
                displayImageBitmap(imagePath);

            }
            else if (requestCode == 2) {

                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath,
                        null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                displayImageBitmap(picturePath);
            }
        }
    }

    public void displayImageBitmap(String image_path) {

        File mediaFile = new File(image_path);
        Bitmap myBitmap = BitmapFactory.decodeFile(mediaFile.getAbsolutePath());
        int height = (myBitmap.getHeight() * 512 / myBitmap.getWidth());
        Bitmap scale = Bitmap.createScaledBitmap(myBitmap, 512, height, true);
        int rotate = 0;

        try {
            exif = new ExifInterface(mediaFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                rotate = 0;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        rotateBitmap = Bitmap.createBitmap(scale, 0, 0, scale.getWidth(), scale.getHeight(), matrix, true);
        imgContactPhoto.setImageBitmap(rotateBitmap);
    }

    public void addContact() {

        ArrayList<ContentProviderOperation> insertOperation = new ArrayList<ContentProviderOperation>();

        int rawContactID = insertOperation.size();

        // Adding insert operation to operations list
        // For insert a new raw contact in the ContactsContract.RawContacts
        insertOperation.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        ByteArrayOutputStream stream = new ByteArrayOutputStream();


        if (rotateBitmap != null) {    // If an image is selected successfully

            rotateBitmap.compress(Bitmap.CompressFormat.PNG, 75, stream);
            insertOperation.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                    .withValue(ContactsContract.Data.IS_SUPER_PRIMARY, 1)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, stream.toByteArray())
                    .build());
            try {
                stream.flush();
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
        // For insert display name in the ContactsContract.Data
        insertOperation.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, edtTxtContactName.getText().toString())
                .build());
        // For insert Mobile Number in the ContactsContract.Data

        insertOperation.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, edtTxtContactNumber.getText().toString())
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());
        // For insert Work Email in the ContactsContract.Data

        insertOperation.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, edtTxtContactEmail.getText().toString())
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .build());

        try {
            // Executing all the insert operations as a single database transaction
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, insertOperation);

            if (isButtonClicked == true) {

                edtTxtContactName.setText("");
                edtTxtContactNumber.setText("");
                edtTxtContactEmail.setText("");
                imgContactPhoto.setImageResource(R.mipmap.ic_launcher);
                Snackbar.make(snackView, "Contact added successfully", Snackbar.LENGTH_INDEFINITE).setAction("Hide", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
            } else {

                Toast.makeText(getBaseContext(), "Contact is successfully added", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {

            e.printStackTrace();
        }
    }

    boolean updateContact(String contactID, String contactName, String contactNumber, String contactEmailAdd, Bitmap bitmap) {

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        ops.add(ContentProviderOperation
                .newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE
                        + "=?", new String[]{contactID, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE})
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contactName)
                .build());

        ops.add(ContentProviderOperation
                .newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE
                                + "=? AND " + ContactsContract.CommonDataKinds.Organization.TYPE + "=?"
                        , new String[]{contactID, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                                , String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)})
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contactNumber)
                .build());


        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE
                                + "=? AND " + ContactsContract.CommonDataKinds.Organization.TYPE + "=?"
                        , new String[]{contactID, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
                                , String.valueOf(ContactsContract.CommonDataKinds.Email.TYPE_WORK)})
                .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, contactEmailAdd)
                .build());

        try {


            ByteArrayOutputStream image = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, image);
            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " +
                            ContactsContract.Data.MIMETYPE + "=?", new String[]{contactID, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE})
                    .withValue(ContactsContract.Data.IS_SUPER_PRIMARY, 1)
                    .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, image.toByteArray())
                    .build());

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



}






