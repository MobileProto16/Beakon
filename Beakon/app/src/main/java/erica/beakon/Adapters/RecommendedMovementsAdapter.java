package erica.beakon.Adapters;

import erica.beakon.Objects.Movement;
import erica.beakon.MainActivity;
import erica.beakon.Pages.ExpandedHashtagPage;
import erica.beakon.Pages.ExpandedMovementPage;
import erica.beakon.R;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class RecommendedMovementsAdapter extends MovementAdapter {

    private int tintColor = 220; //color we're tinting the X when the check mark is selected
    private ArrayList<Movement> movements;
    private View previousView;
    private int previousPosition;

    public RecommendedMovementsAdapter(Context context, ArrayList<Movement> movements) {
        super(context, movements, R.layout.recommended_movement_item);
    }

    protected void setUpView(final MainActivity activity,final Movement movement, View convertView, final int position) {
        final Button join = (Button) convertView.findViewById(R.id.join);

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

    public LinearLayout nextHashtagLayout(View view, LinearLayout currentLayout) {
        if (currentLayout.getId() == R.id.hashtag_layout0) {
            return (LinearLayout) view.findViewById(R.id.hashtag_layout1);
        } else {
            return(LinearLayout) view.findViewById(R.id.hashtag_layout2);
        }
    }

    public int addHashtagtoView(String hashtag, View view, LinearLayout hashtagLayout, int totalWidth, int rowWidth) {
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
