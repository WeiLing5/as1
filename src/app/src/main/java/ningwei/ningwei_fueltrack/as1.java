package ningwei.ningwei_fueltrack;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class as1 extends Activity {

    private static final String FILENAME = "file.sav";
    private static final int EDIT = 0, DELETE = 1;

    EditText datetxt, stationtxt, odometertxt, fuelgradetxt, fuelamounttxt, fuelunitcosttxt;
    //List<FuelTrack> FuelTracks = new ArrayList<FuelTrack>();
    List<FuelTrack> FuelTracks = new ArrayList<>();
    ListView FuelTrackListView;
    int longClickedItemIndex;
    ArrayAdapter<FuelTrack> fueltrackAdapter;
    private boolean isEditMode;

    // What to do when the app open
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_as1);

        // WHat each input is for
        datetxt = (EditText) findViewById(R.id.Datetxt);
        stationtxt = (EditText) findViewById(R.id.Station);
        odometertxt = (EditText) findViewById(R.id.Odometer);
        fuelgradetxt = (EditText) findViewById(R.id.FuelGrade);
        fuelamounttxt = (EditText) findViewById(R.id.FuelAmount);
        fuelunitcosttxt = (EditText) findViewById(R.id.FuelUnitCost);
        FuelTrackListView = (ListView) findViewById(R.id.listView);
        // TabHost tabHost= (TabHost) findViewById(R.id.tabHost);

        registerForContextMenu(FuelTrackListView);
        FuelTrackListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int positions, long l) {
                longClickedItemIndex = positions;
                return false;
            }
        });

        final TabHost tabHost= (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        // Naming the tab as Data
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("creator");
        tabSpec.setContent(R.id.tabCreator);
        tabSpec.setIndicator("Data");
        tabHost.addTab(tabSpec);

        // Naming the tab as Fuel Track
        tabSpec = tabHost.newTabSpec("fueltrack");
        tabSpec.setContent(R.id.tabFuelTrack);
        tabSpec.setIndicator("Fuel Track");
        tabHost.addTab(tabSpec);

        // When the add buttom clicked
        final Button addBtn = (Button) findViewById(R.id.FTAdd);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEditMode){
                    addFuelTracks(datetxt.getText().toString(), stationtxt.getText().toString(),
                            odometertxt.getText().toString(), fuelgradetxt.getText().toString(),
                            fuelamounttxt.getText().toString(), fuelunitcosttxt.getText().toString());
                    isEditMode = false;
                    addBtn.setText("Add");
                    fueltrackAdapter.notifyDataSetChanged();
                    saveInFile();
                    Toast.makeText(getApplicationContext(), "Fuel Track has been changed!",
                            Toast.LENGTH_SHORT).show();
                    resetAddFuelTrackTab();
                    tabHost.setCurrentTab(1);
                } else {
                    addFuelTracks(datetxt.getText().toString(), stationtxt.getText().toString(),
                            odometertxt.getText().toString(), fuelgradetxt.getText().toString(),
                            fuelamounttxt.getText().toString(), fuelunitcosttxt.getText().toString());
                    populateList();
                    fueltrackAdapter.notifyDataSetChanged();
                    resetAddFuelTrackTab();
                    //fueltrackAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), "Fuel Track has been added!",
                            Toast.LENGTH_SHORT).show();
                    saveInFile();

                }
            }
        });

        // A Button that clear the inputs
        final Button BtnClear = (Button) findViewById(R.id.BtnClear);
        BtnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetAddFuelTrackTab();
            }
        });


            datetxt.addTextChangedListener(new

            TextWatcher() {
                @Override
                public void beforeTextChanged (CharSequence charSequence,int i, int i1, int i2){

                }

                @Override
                public void onTextChanged (CharSequence charSequence,int i, int i1, int i2){
                    // addBtn.setEnabled(!charSequence.equals(""));
                    addBtn.setEnabled(!datetxt.getText().toString().trim().isEmpty());

                }

                @Override
                public void afterTextChanged (Editable editable){

                }
            }

            );
        }

    // Load file when the app starts
        @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        // String[] tweets = loadFromFile();
        loadFromFile();
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        //fueltrackAdapter = new ArrayAdapter<FuelTrack>(this,
        //        R.layout.listview_item, R.id.tabHost, FuelTracks);
        fueltrackAdapter = new ArrayAdapter<>(this,
                R.layout.listview_item, R.id.tabHost, FuelTracks);
        FuelTrackListView.setAdapter(fueltrackAdapter);
        fueltrackAdapter.notifyDataSetChanged();
        populateList();
        saveInFile();
    }

    // When hold click on a data make a popup with Edit and Delete
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        // icon is from http://icons.iconarchive.com/icons/hopstarter/soft-scraps/48/Text-Edit-icon.png Jan 25 2016
        menu.setHeaderIcon(R.drawable.editicon);
        menu.setHeaderTitle("Fuel Track Options");
        menu.add(Menu.NONE, EDIT, menu.NONE, "Edit Fuel Track");
        menu.add(Menu.NONE, DELETE, menu.NONE, "Delete Fuel Track");
    }

    // The two option when hold click on data EDIT/DELETE
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Allow Edit
            case EDIT:
                enableEditMode(FuelTracks.get(longClickedItemIndex));
                FuelTracks.remove(longClickedItemIndex);
                populateList();
                fueltrackAdapter.notifyDataSetChanged();
                saveInFile();
                break;
            // Delete
            case DELETE:
                FuelTracks.remove(longClickedItemIndex);
                fueltrackAdapter.notifyDataSetChanged();
                saveInFile();
                break;
        }
        return super.onContextItemSelected(item);
    }

    // Allow Edit
    private void enableEditMode(FuelTrack fueltrack) {
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        // Change to edit tab
        tabHost.setCurrentTab(0);
        datetxt.setText(fueltrack.get_date());
        stationtxt.setText(fueltrack.get_station());
        odometertxt.setText(fueltrack.get_odometer());
        fuelgradetxt.setText(fueltrack.get_fuelgrade());
        fuelamounttxt.setText(fueltrack.get_fuelamount());
        fuelunitcosttxt.setText(fueltrack.get_fuelunitcost());
        Button edit = (Button) findViewById(R.id.FTAdd);

        edit.setText("Update");
        fueltrackAdapter.notifyDataSetChanged();
        saveInFile();


        isEditMode = true;
    }

    // Clear input
    private void resetAddFuelTrackTab() {
        datetxt.setText("");
        stationtxt.setText("");
        odometertxt.setText("");
        fuelgradetxt.setText("");
        fuelamounttxt.setText("");
        fuelunitcosttxt.setText("");

    }

    // populate with data
    private void populateList() {
        fueltrackAdapter = new FuelTrackListAdapter();
        FuelTrackListView.setAdapter(fueltrackAdapter);
        saveInFile();
    }

    // Add Fule Track
    private void addFuelTracks(String date, String station, String odometer,
                               String fuelgrade, String fuelamount, String fuelunitcost) {
        FuelTracks.add(new FuelTrack(date, station, odometer, fuelgrade, fuelamount, fuelunitcost));
    }

    // Load file
    private void loadFromFile() {
        try {
            FileInputStream fis = openFileInput(FILENAME);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));

            Gson gson = new Gson();

            // Took from https://google-gson.googlecode.com/svn/trunk/gson/docs/javadocs/com/google/gson/Gson.html 01-19 2016
            Type listType = new TypeToken<ArrayList<FuelTrack>>() {}.getType();
            FuelTracks = gson.fromJson(in, listType);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            FuelTracks = new ArrayList<>();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        }
    }

    // Save file
    private void saveInFile() {
        try {
            FileOutputStream fos = openFileOutput(FILENAME,
            					0);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));
            Gson gson = new Gson();
            gson.toJson(FuelTracks, out);
            out.flush();

            fos.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        }
    }

    //
    private class FuelTrackListAdapter extends ArrayAdapter<FuelTrack> {
        private Double fuelcostdecimal, odometerdecimal, fuelamountdecimal, fuelunitcostdecimal;
        public FuelTrackListAdapter() {
            super(as1.this, R.layout.listview_item, FuelTracks);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null)
                view = getLayoutInflater().inflate(R.layout.listview_item, parent, false);

            // Which Fuel is currently tracking
            FuelTrack currentFuelTrack = FuelTracks.get(position);

            // Get input and set the text in Fuel Track tab

            // Date:
            TextView date = (TextView) view.findViewById(R.id.fuelDate);
            date.setText("Date: " + currentFuelTrack.get_date());

            // Station:
            TextView station = (TextView) view.findViewById(R.id.fuelStation);
            station.setText("Station: " + currentFuelTrack.get_station());

            // Odometer: 0.0 km
            TextView odometer = (TextView) view.findViewById(R.id.fuelOdometer);
            odometerdecimal = Double.parseDouble(currentFuelTrack.get_odometer());
            odometer.setText(String.format("Odometer: %.1f km", odometerdecimal));
            //odometer.setText("Odometer: " + currentFuelTrack.get_odometer() + " km");

            // Fuel Grade:
            TextView fuelgrade = (TextView) view.findViewById(R.id.fuelGrade);
            fuelgrade.setText("Fuel Grade: " + currentFuelTrack.get_fuelgrade());

            // Fuel Amount: 0.000 L
            TextView fuelamount = (TextView) view.findViewById(R.id.fuelAmount);
            fuelamountdecimal = Double.parseDouble(currentFuelTrack.get_fuelamount());
            fuelamount.setText(String.format("Fuel Amount: %.3f L", fuelamountdecimal));
            //fuelamount.setText("Fuel Amount: " + currentFuelTrack.get_fuelamount()+ " L");

            // Fuel Unit Cost: 0.0 cents/L
            TextView fuelunitcost = (TextView) view.findViewById(R.id.fuelUnitCost);
            fuelunitcostdecimal = Double.parseDouble(currentFuelTrack.get_fuelunitcost());
            fuelunitcost.setText(String.format("Fuel Unit Cost: %.1f cents/L", fuelunitcostdecimal));
            //fuelunitcost.setText("Fuel Unit Cost: " + currentFuelTrack.get_fuelunitcost() + " cents/L");

            // Fuel Cost: 0.00 Dollar
            TextView fuelcost = (TextView) view.findViewById(R.id. fuelCost);
            fuelcostdecimal = Double.parseDouble(currentFuelTrack.get_fuelamount()) *
                    Double.parseDouble(currentFuelTrack.get_fuelunitcost());
            // Fuel Cost = Fuel Amount * Fuel Unit Cost
            fuelcost.setText(String.format("Fuel Cost: %.2f Dollar", fuelcostdecimal/100));

            return view;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_as1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
