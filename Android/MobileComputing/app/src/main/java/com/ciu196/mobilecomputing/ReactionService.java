package com.ciu196.mobilecomputing;

import android.content.Context;
import android.content.res.ColorStateList;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by alexanderalvmo on 2017-10-18.
 */

public class ReactionService {


    public static ImageView getReactionImageView(Context context, Reaction reaction){
        ImageView reactionImageView = new ImageView(context);
        switch (reaction){
            case HEART:
                reactionImageView.setImageResource(R.drawable.ic_favorite_white_24dp);
                reactionImageView.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.myLocationRed)));
                break;
            case HAPPY: reactionImageView.setImageResource(R.drawable.ic_reaction_happy); break;
            case THUMBS_UP:reactionImageView.setImageResource(R.drawable.ic_thumb_up_white_24dp); break;
        }
        reactionImageView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        return reactionImageView;
    };


}
