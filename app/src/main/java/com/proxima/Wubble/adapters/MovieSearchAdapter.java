package com.proxima.Wubble.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.proxima.Wubble.R;
import com.proxima.Wubble.URLBuilder;
import com.proxima.Wubble.activities.MovieActivity;
import com.proxima.Wubble.movie.MovieInformation;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Epokhe on 10.03.2015.
 */
public class MovieSearchAdapter extends RecyclerView.Adapter<MovieSearchAdapter.ViewHolder> {

    protected Context myContext;
    protected List<MovieInformation> movieList;
    protected SearchView searchView;

    // Provide a suitable constructor (depends on the kind of data set)
    public MovieSearchAdapter(Context context, List<MovieInformation> movieList) {
        this.movieList = movieList;
        myContext = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_row, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return (new ViewHolder(v));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        MovieInformation currentMovie = movieList.get(position);


        String date = currentMovie.release_date;
        holder.releaseDate.setText(date);

        String imageUrl = URLBuilder.getImageURL(currentMovie.poster_path);
        Picasso.with(myContext).load(imageUrl)
                .error(R.drawable.ic_action_user)
                .into(holder.movieImage);

        holder.movieTitle.setText(currentMovie.original_title);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentMovieName = holder.movieTitle.getText().toString();

                Intent changeIntent = new Intent(myContext, MovieActivity.class);
                changeIntent.putExtra("movieId", movieList.get(position).id);
                changeIntent.putExtra("movieName", currentMovieName);


                changeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                myContext.startActivity(changeIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (movieList == null)
            return 0;
        else
            return movieList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView movieTitle;
        private TextView releaseDate;
        private ImageView movieImage;

        public ViewHolder(View v) {
            super(v);
            movieTitle = (TextView) v.findViewById(R.id.movieRowNameView);
            releaseDate = (TextView) v.findViewById(R.id.movieRowDateView);
            movieImage = (ImageView) v.findViewById(R.id.movieRowImageView);

        }
    }

}