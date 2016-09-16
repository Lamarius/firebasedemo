package com.projects.bobby.servicefusiondemo.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.projects.bobby.servicefusiondemo.R;
import com.projects.bobby.servicefusiondemo.models.Person;

/**
 * Created by bobby on 9/6/16.
 */
public class PersonViewHolder extends RecyclerView.ViewHolder {

    public TextView nameView;
    public TextView dobView;
    public TextView zipView;
    public ImageButton editButton;
    public ImageButton deleteButton;

    public PersonViewHolder(View itemView) {
        super(itemView);

        nameView = (TextView) itemView.findViewById(R.id.person_name);
        dobView = (TextView) itemView.findViewById(R.id.person_dob);
        zipView = (TextView) itemView.findViewById(R.id.person_zip);
        editButton = (ImageButton) itemView.findViewById(R.id.edit);
        deleteButton = (ImageButton) itemView.findViewById(R.id.delete);
    }

    public void bindToPerson(Person person, View.OnClickListener clickListener) {
        nameView.setText(String.format("%1$s %2$s", person.getFirstName(), person.getLastName()));
        dobView.setText(android.text.format.DateFormat.format("MM/dd/yyyy", person.getDob()));
        zipView.setText(person.getZip());

        editButton.setOnClickListener(clickListener);
        deleteButton.setOnClickListener(clickListener);
    }
}
