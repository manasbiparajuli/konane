//************************************************************
//        * Name:  Manasbi Parajuli                                    *
//        * Project:  Project 2- Konane            *
//        * Class:  AI 331                       *
//        * Date:  3/7/18                           *
//        ************************************************************
package edu.ramapo.mparajul.konane.Adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import edu.ramapo.mparajul.konane.R;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;   // the application context

    // Store the values for which we extend to make a dynamic grid view
    private final String[] values;

    private int[] phoneDimensions = new int[2];
    private int boardSize;

    //Constructor
    public ImageAdapter(Context mContext, String[] values, int[] phoneDimensions, int boardSize) {
        this.mContext = mContext;
        this.values = values;
        this.phoneDimensions = phoneDimensions;
        this.boardSize = boardSize;
    }

    public int getCount() {return values.length;}

    public Object getItem(int position) {return null;}

    public long getItemId(int position) {return 0;}

    // create a new ImageView for each item referenced by the Adapter
    // Receives: position -> the position in the grid view
    //           convertView -> the view that will be used to create a dynamic view
    //           parent -> the parent of the current view
    // Returns: the image view that will be placed in the grid view
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        LinearLayout lLayout;

        if (convertView == null) {
            // if it's not recycled, initialize some attributes

            lLayout = new LinearLayout(mContext);
            lLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            lLayout.setGravity(Gravity.CENTER);

            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(setImageViewHeightWidth()[0],
                    setImageViewHeightWidth()[1]));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setPadding(0, 2, 0, 2);

            String tag = "id" + position;
            imageView.setTag(tag);
            lLayout.addView(imageView);
        } else {
            lLayout = (LinearLayout) convertView;
        }

        // set the appropriate image in the board
        if (values[position].equals("B")) {
            ((ImageView)lLayout.getChildAt(0)).setImageResource(R.drawable.blackbutton);
        }
        if (values[position].equals("W")) {
            ((ImageView)lLayout.getChildAt(0)).setImageResource(R.drawable.whitebutton);
        }
        if (values[position].equals("E")) {
            ((ImageView)lLayout.getChildAt(0)).setImageResource(R.drawable.emptybutton);
        }
        return lLayout;
    }

    private int[] setImageViewHeightWidth() {
        int[] temp = new int[2];
        temp[1] = (phoneDimensions[1] - 35) / boardSize;
        temp[0] = (phoneDimensions[0] - (35*6)) /(boardSize + 3);
        return temp;
    }
}

