package com.example.creativcyborg.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.creativcyborg.R;
import com.example.creativcyborg.entities.Room;
import com.example.creativcyborg.gateway.ServerAPI;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.util.List;

public class RoomListAdapter extends ArrayAdapter<Room>
{
    List<Room> objects;

    public RoomListAdapter(@NonNull Context context, int resource, @NonNull List<Room> objects)
    {
        super(context, resource, objects);
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View view = convertView;

        if (view == null)
        {
            view = LayoutInflater.from(getContext()).inflate(R.layout.room_listview_row, parent, false);
        }

        Room room = getItem(position);
        TextView textView = view.findViewById(R.id.room_row_name_textview);
        textView.setText(room.getName());

        ImageButton deleteButton = view.findViewById(R.id.room_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle(room.getName())
                        .setMessage("Wirklich löschen?")
                        .setPositiveButton("Löschen", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                new Thread(() ->
                                {
                                    try
                                    {
                                        boolean deleted = ServerAPI.deleteRoom(room.getId());

                                        ((Activity)getContext()).runOnUiThread(new Runnable()
                                        {
                                            public void run()
                                            {
                                                if (deleted)
                                                {
                                                    objects.remove(position);
                                                    notifyDataSetChanged();
                                                    Toast.makeText(getContext(), room.getName() + " gelöscht.", Toast.LENGTH_SHORT).show();
                                                }
                                                else
                                                {
                                                    Toast.makeText(getContext(), room.getName() + " konnte nicht gelöscht werden.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                    catch (IOException e)
                                    {
                                        e.printStackTrace();
                                    }
                                }).start();
                            }
                        })
                        .setNegativeButton("Abbrechen", null)
                        .show();
            }
        });

        return view;
    }
}
