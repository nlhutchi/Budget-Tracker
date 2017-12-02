package christophershae.budgettracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import static christophershae.budgettracker.R.id.deleteAnItem;
import static christophershae.budgettracker.R.id.signout;


import com.google.firebase.auth.FirebaseAuth;


import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import static android.R.id.input;
//import static christophershae.budgettracker.R.id.goalInput;
//import static christophershae.budgettracker.R.id.signout;
//import static christophershae.budgettracker.R.id.textView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

//import static christophershae.budgettracker.R.id.signout;

public class SettingsActivity extends AppCompatPreferenceActivity {

    private DatabaseReference mFireBaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private String userId;
    private String currentDate;

    public WeekLongBudget currentWeeksBudget;
    Map<String, WeekLongBudget> usersBudgets = new HashMap<>();


    private Button buttonSignOut;
    private FirebaseAuth firebaseAuth;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        //app bar stuff
        getLayoutInflater().inflate(R.layout.toolbar, (ViewGroup)findViewById(android.R.id.content));
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        int horizontalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        int verticalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        int topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (int) getResources().getDimension(R.dimen.activity_vertical_margin) , getResources().getDisplayMetrics());
        getListView().setPadding(horizontalMargin, topMargin, horizontalMargin, verticalMargin);

        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = Utils.getDatabase();
        mFireBaseDatabase = mFirebaseInstance.getReference("users");
        currentDate = Utils.decrementDate(new Date());
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();

        mFireBaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentWeeksBudget = dataSnapshot.child(userId).child(currentDate).getValue(WeekLongBudget.class);
                findPreference("totalSpent").setTitle("Current Week Total Spent:" +Utils.getStringToTwoDecimalPlaces(currentWeeksBudget.getTotalAmountSpent()));
                findPreference("goalBudget").setTitle("Current Week Goal Budget: "+Utils.getStringToTwoDecimalPlaces(currentWeeksBudget.getGoalTotal()));
                findPreference("income").setTitle("Current Week Income: " +Utils.getStringToTwoDecimalPlaces(currentWeeksBudget.getTotalIncomeAccumulated()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        Preference goalPref = (Preference) findPreference("changeGoal");
        goalPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                changeWeeklyGoal();
                return true;
            }
        });

        Preference incomePerf = (Preference) findPreference("changeIncome");
        incomePerf.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                changeIncome();
                return true;
            }
        });

        Preference deletePref = (Preference) findPreference("deleteItem");
        deletePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                deleteItemFromBudget();
                return true;
            }
        });

        Preference singoutPref = (Preference) findPreference("signout");
        singoutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                signOut();
                return true;
            }
        });
    }

    public void signOut(){
        System.out.println("You did it");
        firebaseAuth.signOut();
        changeToLoginScreen();
    }

    private void changeToLoginScreen(){
        Intent login = new Intent(getApplicationContext(), LoginActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(login);
    }

    private String newGoalBudget;
    public void changeWeeklyGoal()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        //LayoutInflater inflater = this.getLayoutInflater();
        //alertDialogBuilder.setView(inflater.inflate(R.layout.goal_budget_diag, null));
        final EditText goalInput = new EditText(this);
        goalInput.setHint("Weekly Goal");
        alertDialogBuilder.setView(goalInput);

        alertDialogBuilder.setTitle("Set this week's goal!");
        alertDialogBuilder.setPositiveButton("Set",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1)
                    {
                        newGoalBudget = goalInput.getText().toString();
                        currentWeeksBudget.setGoalTotal(Double.valueOf(newGoalBudget));

                        mFireBaseDatabase.child(userId).child(currentDate).setValue(currentWeeksBudget);

                        Toast.makeText(SettingsActivity.this, "Updated Weekly Goal", Toast.LENGTH_LONG).show();

                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private String newIncome;
    public void changeIncome()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        final EditText incomeInput = new EditText(this);
        incomeInput.setHint("Weekly Income");
        alertDialogBuilder.setView(incomeInput);

        alertDialogBuilder.setTitle("Set this week's income!");
        alertDialogBuilder.setPositiveButton("Set",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1)
                    {
                        newIncome = incomeInput.getText().toString();
                        currentWeeksBudget.addMoneyToIncome(Double.valueOf(newIncome));

                        mFireBaseDatabase.child(userId).child(currentDate).setValue(currentWeeksBudget);

                        Toast.makeText(SettingsActivity.this, "Added Income to Week", Toast.LENGTH_LONG).show();

                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    int deletedItemIndex;
    public void deleteItemFromBudget(){
        ArrayList<String> itemNames = new ArrayList<>();
        for(Item item: currentWeeksBudget.allItems){
            itemNames.add(item.name);
        }

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        final EditText incomeInput = new EditText(this);
        incomeInput.setHint("Weekly Income");
        alertDialogBuilder.setView(incomeInput);

        alertDialogBuilder.setTitle("Select an item to delete:");
        alertDialogBuilder.setSingleChoiceItems(itemNames.toArray(new CharSequence[itemNames.size()]),0, null);
        alertDialogBuilder.setPositiveButton("Delete",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1)
                    {
                        deletedItemIndex = ((AlertDialog)arg0).getListView().getCheckedItemPosition();
                        currentWeeksBudget.removeItem(deletedItemIndex);
                        mFireBaseDatabase.child(userId).child(currentDate).setValue(currentWeeksBudget);

                        Toast.makeText(SettingsActivity.this, "Deleted Item", Toast.LENGTH_LONG).show();

                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {

            }
        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /*@Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        final TextView currentspent = (TextView) findViewById(R.id.weekbudget);
        final TextView currentgoal = (TextView) findViewById(R.id.weekGoal);
        final TextView currentincome = (TextView) findViewById(R.id.weekIncome);
        buttonSignOut = (Button) findViewById(R.id.signout);


        firebaseAuth = FirebaseAuth.getInstance();
        mFirebaseInstance = Utils.getDatabase();
        mFireBaseDatabase = mFirebaseInstance.getReference("users");
        currentDate = Utils.decrementDate(new Date());
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();

        mFireBaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentWeeksBudget = dataSnapshot.child(userId).child(currentDate).getValue(WeekLongBudget.class);
                currentspent.setText("Weekly Spent      : $"+Utils.getStringToTwoDecimalPlaces(currentWeeksBudget.getTotalAmountSpent()));//change to display real time
                currentgoal.setText("Weekly Goal Budget: $"+Utils.getStringToTwoDecimalPlaces(currentWeeksBudget.getGoalTotal()));
                currentincome.setText("Weekly Income     : $"+Utils.getStringToTwoDecimalPlaces(currentWeeksBudget.getTotalIncomeAccumulated()));

                System.out.println(currentWeeksBudget.getStartDate());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("You arent reDING CORRECTLTY");
            }
        });

        buttonSignOut = (Button) findViewById(R.id.signout);

    }

    public void signOut(View v){
        System.out.println("You did it");
        firebaseAuth.signOut();
        changeToLoginScreen();
    }

    private void changeToLoginScreen(){
        Intent login = new Intent(getApplicationContext(), LoginActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(login);
    }




    private String newGoalBudget;

    public void changeWeeklyGoal(View v)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        //LayoutInflater inflater = this.getLayoutInflater();
        //alertDialogBuilder.setView(inflater.inflate(R.layout.goal_budget_diag, null));
        final EditText goalInput = new EditText(this);
        goalInput.setHint("Weekly Goal");
        alertDialogBuilder.setView(goalInput);

        alertDialogBuilder.setTitle("Set this week's goal!");
        alertDialogBuilder.setPositiveButton("Set",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1)
                    {
                        newGoalBudget = goalInput.getText().toString();
                        currentWeeksBudget.setGoalTotal(Double.valueOf(newGoalBudget));

                        mFireBaseDatabase.child(userId).child(currentDate).setValue(currentWeeksBudget);

                        Toast.makeText(SettingsActivity.this, "Updated Weekly Goal", Toast.LENGTH_LONG).show();

                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {

            }
        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private String newIncome;

    public void changeIncome(View v)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        //LayoutInflater inflater = this.getLayoutInflater();
        //alertDialogBuilder.setView(inflater.inflate(R.layout.goal_budget_diag, null));
        final EditText incomeInput = new EditText(this);
        incomeInput.setHint("Weekly Income");
        alertDialogBuilder.setView(incomeInput);

        alertDialogBuilder.setTitle("Set this week's income!");
        alertDialogBuilder.setPositiveButton("Set",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1)
                    {
                        newIncome = incomeInput.getText().toString();
                        currentWeeksBudget.addMoneyToIncome(Double.valueOf(newIncome));

                        mFireBaseDatabase.child(userId).child(currentDate).setValue(currentWeeksBudget);

                        Toast.makeText(SettingsActivity.this, "Added Income to Week", Toast.LENGTH_LONG).show();

                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {

            }
        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    int deletedItemIndex;
    public void deleteItemFromBudget(View v){
        ArrayList<String> itemNames = new ArrayList<>();
        for(Item item: currentWeeksBudget.allItems){
            itemNames.add(item.name);
        }

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        //LayoutInflater inflater = this.getLayoutInflater();
        //alertDialogBuilder.setView(inflater.inflate(R.layout.goal_budget_diag, null));
        final EditText incomeInput = new EditText(this);
        incomeInput.setHint("Weekly Income");
        alertDialogBuilder.setView(incomeInput);

        alertDialogBuilder.setTitle("Select an item to delete:");
        alertDialogBuilder.setSingleChoiceItems(itemNames.toArray(new CharSequence[itemNames.size()]),0, null);
        alertDialogBuilder.setPositiveButton("Delete",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1)
                    {
                        deletedItemIndex = ((AlertDialog)arg0).getListView().getCheckedItemPosition();
                        currentWeeksBudget.removeItem(deletedItemIndex);
                        mFireBaseDatabase.child(userId).child(currentDate).setValue(currentWeeksBudget);

                        Toast.makeText(SettingsActivity.this, "Deleted Item", Toast.LENGTH_LONG).show();

                    }
                });

        alertDialogBuilder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {

            }
        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }*/


}
