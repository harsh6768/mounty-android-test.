package com.technohack.mounty_exam_text.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.technohack.mounty_exam_text.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UploadImageAdapter extends RecyclerView.Adapter<UploadImageAdapter.MyViewHolder> {

    private Context context;

    private List<String> listFileName;
    private List<String> listFileDone;

    public UploadImageAdapter(List<String> listFileName, List<String> listFileDone,Context context) {
        this.listFileName = listFileName;
        this.listFileDone = listFileDone;
        this.context=context;

    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.single_image_list_item,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.fileName.setText(listFileName.get(position));

        String fileDone=listFileDone.get(position);

        //for checking if file uploaded we need to change the image from progress to checked once
        if(fileDone.equals("uploading")){

            holder.fileImage.setImageResource(R.mipmap.progress);

        }else{
            holder.fileImage.setImageResource(R.mipmap.checked);

        }
    }

    @Override
    public int getItemCount() {
        return listFileName.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView fileName;
        private ImageView fileImage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName=itemView.findViewById(R.id.main_fileNameId);
            fileImage=itemView.findViewById(R.id.main_fileImageId2);

        }
    }
}
