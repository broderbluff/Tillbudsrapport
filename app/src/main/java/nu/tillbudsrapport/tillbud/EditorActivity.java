package nu.tillbudsrapport.tillbud;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;


public class EditorActivity extends AppCompatActivity {

    private String action;
    private EditText editorPlace;

    private EditText editorReporter;
    private EditText editorWhy;
    private EditText editorDescription;

    private String noteFilter;
    private String oldPlace;
    private String oldWhy;
    private String oldReporter;
    private String oldDescription;
    private String oldTime;
    private String oldDate;
    private String monthString;
    private TextView timePicker;
    private TextView datePicker;
    private String mottagaren = null;
    private int year, month, day;
    private static final int DIALOG_ID = 0;
    ProgressDialog dialog;
    private static final int TEXT_ID = 0;
    private static final int SENDING = 2;
    private EditText reciverInput;
    private EditText senderInput;
    private String sender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        final Calendar cal = Calendar.getInstance();

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);


        timePicker = (TextView) findViewById(R.id.tvTime);
        datePicker = (TextView) findViewById(R.id.tvDate);
        editorPlace = (EditText) findViewById(R.id.etPlace);
        editorReporter = (EditText) findViewById(R.id.etReporter);
        editorWhy = (EditText) findViewById(R.id.etWhy);
        editorDescription = (EditText) findViewById(R.id.etDescription);


        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(RapportProvider.CONTENT_ITEM_TYPE);
        if (uri == null) {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_note_title));

            datePicker.setText(day + " " + theMonth(month) + " " + year);
            timePicker.setText(String.format("%02d:%02d", hour, minute));
            editorPlace.requestFocus();
        } else {
            action = Intent.ACTION_EDIT;
            noteFilter = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();
            setTitle(getString(R.string.edit_tillbud));
            Cursor cursor = getContentResolver().query(uri, DBOpenHelper.ALL_COLUMNS, noteFilter, null, null);

            if(cursor.moveToFirst()) {
                oldPlace = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
                oldWhy = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_WHY));
                oldReporter = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_REPORTER));
                oldDescription = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_DESCRIPTION));
                oldTime = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TIME));
                oldDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_DATE));
                editorPlace.setText(oldPlace);
                editorWhy.setText(oldWhy);
                editorReporter.setText(oldReporter);
                editorDescription.setText(oldDescription);
                timePicker.setText(oldTime);
                datePicker.setText(oldDate);
            }
            cursor.close();

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (action.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.menu_editor, menu);

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finishEditing();
                break;
            case R.id.action_delete:
                deleteNote();
        }

        return true;
    }

    private void finishEditing() {
        String newPlace = editorPlace.getText().toString().trim();
        String newWhy = editorWhy.getText().toString().trim();
        String newReporter = editorReporter.getText().toString().trim();
        String newDescription = editorDescription.getText().toString().trim();
        String newTime = timePicker.getText().toString();
        String newDate = datePicker.getText().toString();

        switch (action) {
            case Intent.ACTION_INSERT:
                if (newPlace.length() == 0 && newWhy.length() == 0 && newReporter.length() == 0 && newDescription.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertNote(newPlace, newWhy, newReporter, newDescription, newTime, newDate);

                }
                break;
            case Intent.ACTION_EDIT:
                if (newPlace.length() == 0 && newWhy.length() == 0 && newReporter.length() == 0 && newDescription.length() == 0) {
                    deleteNote();
                } else if (oldPlace.equals(newPlace) && oldWhy.equals(newWhy) && oldDescription.equals(newDescription) && oldReporter.equals(newReporter) && oldTime.equals(newTime) && oldDate.equals(newDate)) {
                    setResult(RESULT_CANCELED);
                } else {
                    updateNote(newPlace, newWhy, newDescription, newReporter, newTime, newDate);


                }

        }

        finish();
    }

    private void updateNote(String notePlace, String noteWhy, String noteDescription, String noteReporter, String noteTime, String noteDate) {

        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, notePlace);
        values.put(DBOpenHelper.NOTE_WHY, noteWhy);
        values.put(DBOpenHelper.NOTE_DESCRIPTION, noteDescription);
        values.put(DBOpenHelper.NOTE_REPORTER, noteReporter);
        values.put(DBOpenHelper.NOTE_TIME, noteTime);
        values.put(DBOpenHelper.NOTE_DATE, noteDate);
        getContentResolver().update(RapportProvider.CONTENT_URI, values, noteFilter, null);
        Toast.makeText(this, getString(R.string.note_updated), Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void deleteNote() {

        getContentResolver().delete(RapportProvider.CONTENT_URI, noteFilter, null);
        Toast.makeText(this, getString(R.string.note_deleted), Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void insertNote(String notePlace, String noteWhy, String noteReporter, String noteDescription, String noteTime, String noteDate) {

        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, notePlace);
        values.put(DBOpenHelper.NOTE_WHY, noteWhy);
        values.put(DBOpenHelper.NOTE_REPORTER, noteReporter);
        values.put(DBOpenHelper.NOTE_DESCRIPTION, noteDescription);
        values.put(DBOpenHelper.NOTE_TIME, noteTime);
        values.put(DBOpenHelper.NOTE_DATE, noteDate);
        getContentResolver().insert(RapportProvider.CONTENT_URI, values);
        setResult(RESULT_OK);
    }


    public static String theMonth(int month){
        String[] monthNames = {"Januari", "Februari", "Mars", "April", "Maj", "Juni", "Juli", "Augusti", "September", "Oktober", "November", "December"};
        return monthNames[month];
    }

    @Override
    public void onBackPressed() {
        finishEditing();
    }

    public void setTime(View view) {

        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(EditorActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePickerD, int selectedHour, int selectedMinute) {
                timePicker.setText(String.format("%02d:%02d", selectedHour,selectedMinute));
            }
        }, hour, minute, true);//Yes 24 hour time

        mTimePicker.show();
    }


    public void setDate(View view) {
        showDatePicker();

    }


    public void showDatePicker() {
        showDialog(DIALOG_ID);


    }




    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_ID)
            return new DatePickerDialog(this, dpickerListener, year, month, day);


        return null;
    }

    private DatePickerDialog.OnDateSetListener dpickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePickerL, int yearL, int monthL, int dayL) {
            year = yearL;
            month = monthL;
            day = dayL;
            datePicker.setText(day + " " +theMonth(month) + " " + year);


        }
    };

    public void sendButton(View view) {

        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {


                            String value = reciverInput.getText().toString().trim();
                            sender = senderInput.getText().toString().trim();

                            mottagaren =value;
                            new SendEmailAsyncTask().execute();


                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater=EditorActivity.this.getLayoutInflater();
        //this is what I did to added the layout to the alert dialog
        @SuppressLint("InflateParams") View layout=inflater.inflate(R.layout.dialog,null);

        builder.setView(layout);
        reciverInput=(EditText)layout.findViewById(R.id.etReciver);
        senderInput=(EditText)layout.findViewById(R.id.etSender);

        builder .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();







    }

    public void closeButton(View view) {

        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {

                            finish();


                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.closewithoutsave))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();

    }

    public void saveButton(View view) {
        finishEditing();
        finish();
    }

    public void getContacts(View view) {




        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        intent.setType(ContactsContract.CommonDataKinds.Email.CONTENT_TYPE);
        startActivityForResult(intent, 1);




    }


    // Sending the email
    public class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {

            dialog = new ProgressDialog(EditorActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.setMessage("Skickar mail");
            dialog.show();
        }

        MailSender m = new MailSender();

        public SendEmailAsyncTask() {
            String place = editorPlace.getText().toString().trim();
            String why = editorWhy.getText().toString().trim();
            String reporter = editorReporter.getText().toString().trim();
            String description = editorDescription.getText().toString().trim();
            String time = timePicker.getText().toString().trim();
            String date = datePicker.getText().toString().trim();

            if (BuildConfig.DEBUG) {
                Log.v(SendEmailAsyncTask.class.getName(),
                        "SendEmailAsyncTask()");
            }
            m.setTo(mottagaren);
            m.setSubject(getString(R.string.tillbudsrapport));


            m.setBody(getString(R.string.part1)+place+
                    getString(R.string.part2)+reporter+ " \n"+sender+getString(R.string.part3)+date+", "+time+getString(R.string.part4)+description+
                    getString(R.string.part5)+ why+getString(R.string.part6));
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            if (BuildConfig.DEBUG)
                Log.v(SendEmailAsyncTask.class.getName(), "doInBackground()");
            try {
                m.send();
                return true;
            } catch (AuthenticationFailedException e) {
                Log.e(SendEmailAsyncTask.class.getName(), "Bad account details");
                e.printStackTrace();
                return false;
            } catch (MessagingException e) {
                Log.e(SendEmailAsyncTask.class.getName(), m.getTo(null)
                        + "failed");
                e.printStackTrace();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;

            }

        }

        protected void onProgressUpdate(Integer... progress) {

        }

        @Override
        protected void onPostExecute(Boolean result) {


            dialog.dismiss();


        }

    }



    public void onActivityResult(int reqCode, int resultCode, Intent data) {
//what should i have to write to fetch email address of selected contact
// I wrote like below but i could not get result

        if (resultCode == Activity.RESULT_OK) {
            try{
                Uri contactData = data.getData();

                Cursor cursorEmail = getContentResolver().query(contactData,null,null,null,null);
                cursorEmail.moveToFirst();
                String emailAdd = cursorEmail.getString(cursorEmail.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                reciverInput.setText(emailAdd);
                cursorEmail.close();
            }catch(Exception e){
                Toast.makeText(EditorActivity.this, "No Email Add found", Toast.LENGTH_LONG).show();
            }

        }



}}





