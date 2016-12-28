package com.example.q.mymessage;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class FragmentD extends Fragment {

    public FragmentD() {
    }

    private ListView listView;

    private static final int PERMISSIONS_REQUEST_READ_SMS = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmentd, container, false);

        listView = (ListView) view.findViewById(R.id.listView2);

        showMessages();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("TEST", "***************************************" + position);
                Log.d("TEST", "***************************************" + id);

                if (MainActivity.starredList.contains(Integer.valueOf((int) id))) {
                    MainActivity.starredList.remove(Integer.valueOf((int) id));
                    //parent.getChildAt(position).setBackgroundColor(Color.TRANSPARENT);
                } else {
                    //parent.getChildAt(position).setBackgroundColor(Color.YELLOW);
                }
                Toast.makeText(getActivity(), "starredList : " + MainActivity.starredList.toString(), Toast.LENGTH_SHORT).show();

                Log.d("ELEMENTS", "********************************************" + MainActivity.starredList.toString());

                return true;
            }
        });

        return view;
    }

    private void showMessages() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_SMS}, PERMISSIONS_REQUEST_READ_SMS);
        } else {
            getMessages();
        }
    }

    private void getMessages() {
        if (!MainActivity.starredList.isEmpty()) {
            // Create Inbox box URI
            Uri inboxURI = Uri.parse("content://sms/inbox");

            // List required columns
            String[] reqCols = new String[] { "_id", "address", "body" };

            // Get Content Resolver object, which will deal with Content Provider
            ContentResolver cr = getActivity().getContentResolver();

            String selection = "";
            for (Integer id: MainActivity.starredList) {
                selection = selection + " or _id = " + id;
            }
            selection = selection.substring(4);
            Log.d("selection", "********************************************" + selection);

            // Fetch Inbox SMS Message from Built-in Content Provider
            Cursor c = cr.query(inboxURI, reqCols, selection, null, null);

            // Attached Cursor with adapter and display in listview
            final SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(
                    getActivity(),
                    android.R.layout.simple_list_item_2,
                    c,
                    new String[] {
                            "body",
                            "address"
                    },
                    new int[] {
                            android.R.id.text1,
                            android.R.id.text2
                    });

            listView.setAdapter(simpleCursorAdapter);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_SMS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showMessages();
            } else {
                Toast.makeText(getActivity(), "Until you grant the permission, we cannot display the messages", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
