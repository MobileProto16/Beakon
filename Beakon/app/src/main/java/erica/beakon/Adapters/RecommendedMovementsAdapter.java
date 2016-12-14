package erica.beakon.Adapters;

import erica.beakon.Objects.Movement;
import erica.beakon.MainActivity;
import erica.beakon.Pages.ExpandedHashtagPage;
import erica.beakon.Pages.ExpandedMovementPage;
import erica.beakon.R;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class RecommendedMovementsAdapter extends ArrayAdapter<Movement> {

    private int tintColor = 220; //color we're tinting the X when the check mark is selected
    private ArrayList<Movement> movements;
    private View previousView;
    private int previousPosition;

    public RecommendedMovementsAdapter(Context context, ArrayList<Movement> movements) {
        super(context, 0, movements);
        this.movements = movements;
        previousView = null;
        previousPosition = -1;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Movement movement = getItem(position);
        final MainActivity activity = (MainActivity)getContext();

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card_item, parent, false);
        }

        TextView movementNameView = (TextView) convertView.findViewById(R.id.card_movement_name);
        movementNameView.setText(movement.getName());
        setOnClickMovement(movementNameView, movement);
        final Button join = (Button) convertView.findViewById(R.id.join);
        //final Button reject = (Button) convertView.findViewById(R.id.reject);

        if (activity.currentUser.getMovements().keySet().contains(movement.getId())) {
            //rejectBtn.setColorFilter(Color.argb(tintColor, tintColor, tintColor, tintColor)); // White Tint) {
            movement.joined = true;
        }

        join.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View currentView) {
                activity.firebaseHandler.addUsertoMovement(activity.currentUser, movement);

                //make sure that only the add button on the current element disappears
                if(previousView!=null){
                    Movement previousMovement = movements.get(previousPosition);
                    previousMovement.joined = false;
                }

                movement.joined = true;
                previousView = currentView;
                previousPosition = position;
                notifyDataSetChanged();
            }

        });

        if(movement.joined){
            join.setVisibility(View.GONE);
        } else {
            join.setVisibility(View.VISIBLE);
        }

        //Todo: Make it so it will permanently stop suggesting a deleted item -- DONE?
//        reject.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                movements.remove(movement);
//                notifyDataSetChanged();
//            }
//        });

        //create the hashtag table
//        TableRow.LayoutParams tableParams = new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT,1.0f);
//        final TableLayout hashtagTable = (TableLayout) convertView.findViewById(R.id.hashtag_table);
//        //get list of hashtags from movement
//        ArrayList<String> hashtagList = movement.getHashtagList();
//        //initialize some variables here
//        int rowWidth = ((MainActivity)getContext()).screenSize; //placeholder value -- ideally, we'd programatically find out view width since it's dynamic
//        int counter = 0; //keep track of number of characters in row
//
//        //loop through hashtag list, put them in one row, set onClickListeners, etc
//        if (hashtagList != null && hashtagTable.getChildCount() != 1) { //make sure no rows already exist -- weird bug there
//            final TableRow hashtagRow = new TableRow(getContext()); //create table row
//            for (int i = 0; i < hashtagList.size(); i++) {
//                String hashtagName = "#" + hashtagList.get(i) + " "; //add space at end to shows diff between hashtags
//                TextView hashtagTV = new TextView(getContext()); //create hashtag TV
//                hashtagTV.setTextColor(getContext().getResources().getColor(R.color.colorPrimaryDark));
//                hashtagTV.setText(hashtagName); //set text of hashtag TV
//                setOnClickHashtag(hashtagTV); //set on click listener
//                hashtagTV.measure(0, 0); //measure hashtag TV dimensions
//                int hashtagWidth = hashtagTV.getMeasuredWidth(); //save measured width in variable
//                //add to existing row or make a new one based on length of string
//                if (counter + hashtagWidth < rowWidth) { //if adding new hashtag won't go over the limit
//                    hashtagRow.addView(hashtagTV); //add the TV to the row
//                    counter += hashtagWidth; //update the counter
//                }
//            }
//            hashtagTable.addView(hashtagRow, tableParams); //adds row to table
//        }

        ArrayList<String> hashtagList = movement.getHashtagList();
        LinearLayout hashtagLayout = (LinearLayout) convertView.findViewById(R.id.hashtag_layout0);
        if (hashtagLayout.getChildCount() == 0) {
            int rowWidth = ((MainActivity)getContext()).screenSize;
            int totalWidth = 0;
            if (hashtagList != null) {
                for (String hashtag: hashtagList) {
                    //add the hashtag to the view and update totalWidth with the new value
                    totalWidth = addHashtagtoView(hashtag, convertView, hashtagLayout, totalWidth, rowWidth);
                }
            }
        }


        return convertView;
    }

    private void setOnClickMovement(final TextView tv, final Movement movement){
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpandedMovementPage movementFragment = new ExpandedMovementPage();
                Bundle bundle = new Bundle();
                bundle.putString("name", movement.getName()); //give new fragment the hashtag it's expanding
                bundle.putString("ID", movement.getId()); //give new fragment the hashtag it's expanding
                movementFragment.setArguments(bundle);
                ((MainActivity) getContext()).changeFragment(movementFragment, "expandedMovementPage"); //changes fragments
            }
        });
    }

    //basically the HashtagAdapter, but since I'm using a Table Layout I did it differently (i.e. this instead)
    private void setOnClickHashtag(final TextView tv){
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hashtagName = (String) tv.getText();
                ExpandedHashtagPage hashtagFragment = new ExpandedHashtagPage();
                Bundle bundle = new Bundle();
                bundle.putString("name", hashtagName); //give new fragment the hashtag it's expanding
                hashtagFragment.setArguments(bundle);
                ((MainActivity) getContext()).changeFragment(hashtagFragment, "expandedHashtagPage"); //changes fragments
            }
        });
    }

    private String getFormattedHashtag(String hashtag) {
        return "#" + hashtag + " ";
    }

    private TextView createHashtagTextView(String hashtag) {
        TextView tv = new TextView(getContext());
        tv.setText(getFormattedHashtag(hashtag));
        tv.setTextColor(getContext().getResources().getColor(R.color.colorPrimaryDark));
        setOnClickHashtag(tv);
        return  tv;
    }

    private int getHashtagTextViewWidth(TextView tv) {
        tv.measure(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        return tv.getMeasuredWidth();
    }

    private LinearLayout nextHashtagLayout(View view, LinearLayout currentLayout) {
        if (currentLayout.getId() == R.id.hashtag_layout0) {
            return (LinearLayout) view.findViewById(R.id.hashtag_layout1);
        } else {
            return(LinearLayout) view.findViewById(R.id.hashtag_layout2);
        }
    }

    private int addHashtagtoView(String hashtag, View view, LinearLayout hashtagLayout, int totalWidth, int rowWidth) {
        TextView tv = createHashtagTextView(hashtag);
        int hashtagWidth = getHashtagTextViewWidth(tv);
        if (totalWidth + hashtagWidth >= rowWidth) { //if adding new hashtag won't go over the limit
            hashtagLayout = nextHashtagLayout(view, hashtagLayout); //make hashtagLayout the linearlayout below it
            totalWidth = 0;
        }
        hashtagLayout.addView(tv);
        totalWidth += hashtagWidth;
        return totalWidth;
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }
}
