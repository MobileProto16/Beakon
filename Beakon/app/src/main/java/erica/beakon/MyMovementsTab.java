package erica.beakon;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MyMovementsTab extends Fragment {


    public MyMovementsTab() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_my_movements_tab, container, false);

        final ArrayList<Movement> movements = new ArrayList<>();

        ((MainActivity)getActivity()).handler.getData(null, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userMovementSnapshot: dataSnapshot.child("UserMovements").getChildren()){
                    if (userMovementSnapshot.child("user").child("id").getValue().equals(((MainActivity)getActivity()).currentUser.getId())) {
                        movements.add(userMovementSnapshot.child("movement").getValue(Movement.class));


                    }
                }

                ListView movementsList = (ListView) view.findViewById(R.id.my_movements_list);
                MyMovementAdapter movementsAdapter = new MyMovementAdapter(getActivity(), movements);
                movementsList.setAdapter(movementsAdapter);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("MyMovementsTab", databaseError.getMessage());

            }
        });
        return view;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
