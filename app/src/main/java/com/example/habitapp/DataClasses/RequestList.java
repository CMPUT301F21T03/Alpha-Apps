package com.example.habitapp.DataClasses;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.habitapp.R;

import java.io.Serializable;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
public class RequestList extends ArrayAdapter<Request> implements Serializable {
    private ArrayList<Request> requests;
    private Context context;

    public RequestList(Context context, ArrayList<Request> notifications) {
        super(context,0,notifications);
        this.context = context;
        this.requests = notifications;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.noti_item, parent, false);
        }

        Request request = requests.get(position);

        String username = request.getUsername();
        String suffix = request.getSUFFIX();

        TextView notiText = view.findViewById(R.id.noti_text);
        notiText.setText(username + suffix);

        return view;


    }
}
