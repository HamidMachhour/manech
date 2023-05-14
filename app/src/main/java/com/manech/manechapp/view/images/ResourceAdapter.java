package com.manech.manechapp.view.images;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.manech.manechapp.R;
import com.manech.manechapp.data.Resource;
import com.manech.manechapp.helper.MyUtils;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class ResourceAdapter extends SliderViewAdapter<ResourceAdapter.ViewHolder> {
    private List<Resource> resourceList;
    private OnItemClickListener mListener;
    public ResourceAdapter(OnItemClickListener mListener, List<Resource> resources) {
        this.mListener = mListener;
        resourceList = resources;
    }

    public List<Resource> getResourceList() {
        return resourceList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item, parent, false);

        ViewHolder holder =new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Resource resource = resourceList.get(position);

        final ImageView imageView = viewHolder.getImageView();
        imageView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClick(position);
            }
        });

       MyUtils.loadResourceImage(resource,imageView);
    }

    @Override
    public int getCount() {
        return resourceList.size();
    }

    public class ViewHolder extends SliderViewAdapter.ViewHolder {
        public ImageView getImageView() {
            return imageView;
        }

        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}

