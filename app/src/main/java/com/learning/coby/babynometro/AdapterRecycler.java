package com.learning.coby.babynometro;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterRecycler extends RecyclerView.Adapter
        <AdapterRecycler.ViewHolderArtista>
{


    private final Context context;
    private List<PornStar> datos;


    public AdapterRecycler(Context context, List<PornStar> datos) {
        this.context = context;
        this.datos = datos;
    }

    @NonNull
    @Override
    public ViewHolderArtista onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item, parent, false);
        return new ViewHolderArtista(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderArtista holder, int position) {
        PornStar artista = datos.get(position);
        holder.txtnombre.setText(artista.getName());
        holder.txtTelefono.setText(artista.getPhone());
        holder.ratingBar.setRating((float)artista.getRating());
        if(artista.checarImagen())
        {
            /// hacer algo
            Bitmap bitmap = BitmapFactory.decodeByteArray(artista.getPhoto(),0,artista.getPhoto().length);
            holder.cImage.setImageBitmap(bitmap);
        } else {
            holder.cImage.setImageResource(R.drawable.img_none);
        }
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    public static class ViewHolderArtista extends RecyclerView.ViewHolder
    {

        @BindView(R.id.nombre_artista)
        TextView txtnombre;
        @BindView(R.id.rating)
        RatingBar ratingBar;
        @BindView(R.id.telefono)
        TextView txtTelefono;
        @BindView(R.id.avatar)
        CircleImageView cImage;

        public ViewHolderArtista(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
