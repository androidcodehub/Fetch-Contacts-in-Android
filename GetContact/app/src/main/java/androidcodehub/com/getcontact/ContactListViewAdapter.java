package androidcodehub.com.getcontact;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.graphics.Bitmap;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ContactListViewAdapter extends RecyclerView.Adapter<ContactListViewAdapter.MyViewHolder> {

    List<ContactList> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;
    int layoutResID;

    public ContactListViewAdapter(Context context, int layoutResID, List<ContactList> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.layoutResID = layoutResID;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ContactList current = data.get(position);
        holder.contactName.setText(current.getContactName());
        holder.contactNumber.setText(current.getContactNumber());
        holder.contactEmail.setText(current.getContactEmail());
        holder.contactImage.setImageBitmap(current.getContactImage());
        holder.contactId = current.getContactId();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        TextView contactName, contactNumber, contactEmail;
        ImageView contactImage;
        String contactId;

        public MyViewHolder(View itemView) {
            super(itemView);

            contactName = (TextView) itemView.findViewById(R.id.txtName);
            contactNumber = (TextView) itemView.findViewById(R.id.txtNumber);
            contactImage = (ImageView) itemView.findViewById(R.id.imgContact);
            contactEmail = (TextView) itemView.findViewById(R.id.txtEmail);

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setTitle("Manage Contact").setMessage("Please choose any action.");
            alertDialog.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    int pos = getPosition();
                    removeAt(pos);
                    deleteContact(contactId);
                }
            }).setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                    Intent go = new Intent(context, AddContactActivity.class);
                    AddContactActivity.isUpdate = true;

                    contactImage.setDrawingCacheEnabled(true);
                    Bitmap bitmap = contactImage.getDrawingCache();
                    go.putExtra("cID", contactId);
                    go.putExtra("cName", contactName.getText().toString());
                    go.putExtra("cNumber", contactNumber.getText().toString());
                    go.putExtra("cEmail", contactEmail.getText().toString());
                    go.putExtra("cPhoto",bitmap);
                    context.startActivity(go);


                }
            });
            alertDialog.show();
            return false;
        }

    }

    void removeAt(int position) {
        data.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, data.size());
    }

    public void deleteContact(String contactID) {
        ContentResolver contactHelper = context.getContentResolver();
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        String[] args = new String[]{contactID};
        ops.add(ContentProviderOperation.newDelete(RawContacts.CONTENT_URI).withSelection(RawContacts.CONTACT_ID + "=?", args).build());
        try {
            contactHelper.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

}
