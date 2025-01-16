package com.example.staffprofile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.List;

public class ProfileAdapter extends ArrayAdapter<Profile> {

    private Context mContext;
    private List<Profile> profileList;

    public ProfileAdapter(@NonNull Context context, List<Profile> list) {
        super(context, 0, list);
        mContext = context;
        profileList = list;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.profile_list_item, parent, false);

        Profile currentProfile = profileList.get(position);

        // Set name
        TextView name = listItem.findViewById(R.id.textViewName);
        name.setText(currentProfile.getName());

        // Set job title
        TextView jobTitle = listItem.findViewById(R.id.textViewJobTitle);
        jobTitle.setText(currentProfile.getJobTitle());

        // Set skills and certifications (optional)
        TextView skills = listItem.findViewById(R.id.textViewSkills);
        skills.setText(currentProfile.getSkills());

        TextView certifications = listItem.findViewById(R.id.textViewCertifications);
        certifications.setText(currentProfile.getCertifications());

        // Set profile image
        ImageView imageViewProfile = listItem.findViewById(R.id.imageViewProfilePhoto);
        String photoPath = currentProfile.getPhotoPath();
        if (photoPath != null && !photoPath.isEmpty()) {
            File imgFile = new File(photoPath);
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageViewProfile.setImageBitmap(myBitmap);
            } else {
                imageViewProfile.setImageResource(R.drawable.img); // Default placeholder
            }
        } else {
            imageViewProfile.setImageResource(R.drawable.img); // Default placeholder
        }

        return listItem;
    }
}
