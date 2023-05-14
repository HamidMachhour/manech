package com.manech.manechapp.view.images;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manech.manechapp.R;
import com.manech.manechapp.data.Resource;
import com.manech.manechapp.helper.MyUtils;

import java.util.List;
import java.util.UUID;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ImageViewHolder> {
    private  List<Resource> images;
    private UUID defaultImageId;

    private final ImagesManager parentListener;

    public ImageListAdapter(List<Resource> images, ImagesManager parentListener, UUID defaultImageId) {

        this.images = images;
        this.parentListener = parentListener;
        this.defaultImageId = defaultImageId;
    }

    public void setDefaultImageId(UUID defaultPictureId) {
        this.defaultImageId = defaultPictureId;
    }

    public List<Resource> getImages() {
        return images;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ImageViewHolder(new ResourceClickListener() {
            @Override
            public void deleteClicked(int layoutPosition) {
                parentListener.deleteItem(layoutPosition);
            }

            @Override
            public void setAsDefaultClicked(int layoutPosition) {
                parentListener.setDefaultImage(layoutPosition);
            }
        }, view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Resource resource = images.get(position);
        holder.getRadioSetDefault().setChecked(resource.getId().equals(defaultImageId));
        final ImageView imageView = holder.getImageView();
        MyUtils.loadResourceImage(resource, imageView);
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.image_view;
    }

    @Override
    public int getItemCount() {
        return images.size();
    }


    public Resource getItem(int position) {
        return images.get(position);
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ResourceClickListener resourceClickListener;
        private final ImageView imgDelete;
        private final ImageView imageView;
        private final RadioButton radioSetDefault;

        public ImageViewHolder(ResourceClickListener resourceClickListener, @NonNull View itemView) {
            super(itemView);
            this.resourceClickListener = resourceClickListener;

            imageView = itemView.findViewById(R.id.imageView);
            imgDelete = itemView.findViewById(R.id.imgDeleteForever);
            imgDelete.setOnClickListener(this);

            radioSetDefault = itemView.findViewById(R.id.radioSetDefault);
            radioSetDefault.setOnClickListener(this);
        }

        public ImageView getImageView() {
            return imageView;
        }

        public RadioButton getRadioSetDefault() {
            return radioSetDefault;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.imgDeleteForever:
                    resourceClickListener.deleteClicked(this.getLayoutPosition());
                    break;
                case R.id.radioSetDefault:
                    resourceClickListener.setAsDefaultClicked(this.getLayoutPosition());
                    break;
                default:
                    break;
            }
        }
    }

    public void setImages(List<Resource> images) {
        this.images = images;
    }
}
