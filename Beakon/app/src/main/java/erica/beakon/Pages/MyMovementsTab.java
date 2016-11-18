package erica.beakon.Pages;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import erica.beakon.Objects.Movement;
import erica.beakon.Adapters.MyMovementAdapter;
import erica.beakon.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class MyMovementsTab extends MovementsTab {

    public static final String TAG = "MY MOVEMENTS TAB";
    MyMovementAdapter adapter;
    View view;

    public MyMovementsTab() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_my_movements_tab, container, false);
//        movements = new ArrayList<Movement>();
        setUpChangeFragmentsButton(view, new RecommendedMovementsTab(), R.id.movements);
        setUsersMovementsListener();
        adapter = new MyMovementAdapter(getContext(), movements);
        if (!movements.isEmpty()) {
            setUpListView(view);
        }

        ArrayList<Movement> movements = new ArrayList<>();
        ArrayList<String> hashtags = new ArrayList<>();
        hashtags.add("#stillwithher");
        hashtags.add("#feelthebern");
        hashtags.add("#yay");
        hashtags.add("#bob");
        Movement rally = new Movement("2", "Rally", "description", "steps", "resources", hashtags);
        movements.add(rally);
        movements.add(rally);

        return view;
    }


    private void setUsersMovementsListener() {
        getMainActivity().firebaseHandler.getUserChild(getMainActivity().currentUser.getId(), "movements", populateMovementsEventListener());
    }

    private void setUpListView(View view) {
        ListView listView = (ListView) view.findViewById(R.id.my_movements_list);
        listView.setAdapter(adapter);
    }

    protected ValueEventListener getMovementAddedValueEventListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                movements.add(dataSnapshot.getValue(Movement.class));
                if (movements.size() == 1) {
                    setUpListView(view);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("MyMovementsTab", "there is a problem on the listener for the movement added to my movements");
            }
        };
    }

    protected ChildEventListener populateMovementsEventListener() {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                addMovement(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                removeMovement(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("MyMovementsTab", databaseError.getMessage());
            }
        };
    }
}
