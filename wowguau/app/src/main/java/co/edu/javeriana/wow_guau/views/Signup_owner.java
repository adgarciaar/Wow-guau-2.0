package co.edu.javeriana.wow_guau.views;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import co.edu.javeriana.wow_guau.R;
import co.edu.javeriana.wow_guau.model.Direccion;
import co.edu.javeriana.wow_guau.model.Dueno;
import co.edu.javeriana.wow_guau.utils.CameraUtils;
import co.edu.javeriana.wow_guau.utils.Permisos;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Signup_owner extends AppCompatActivity {
    EditText et_email;
    EditText et_password;
    EditText et_nombre;
    EditText et_cedula;
    EditText et_fecha_nacimiento;
    EditText et_phone;
    EditText et_direccion;
    RadioGroup rg_genero;
    ImageButton ib_upload_photo;
    Button button_register;
    ScrollView scrollView;
    TextView tv_error;

    DatePickerDialog.OnDateSetListener date;
    Calendar calendar;
    Bitmap selectedImage;
    Address address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_owner);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_nombre = findViewById(R.id.et_nombre);
        et_cedula = findViewById(R.id.et_cedula);
        rg_genero = findViewById(R.id.rg_genero);
        et_fecha_nacimiento = findViewById(R.id.et_date);
        et_phone = findViewById(R.id.et_phone);
        et_direccion = findViewById(R.id.et_direccion);
        scrollView = findViewById(R.id.scrollView);
        tv_error = findViewById(R.id.tv_error3);
        ib_upload_photo = findViewById(R.id.ib_upload_photo);
        button_register = findViewById(R.id.button_register);

        calendar = Calendar.getInstance();

        ib_upload_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraUtils.startDialog(Signup_owner.this);
            }
        });
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dueno user = registrarDueno();
                if(user != null) {
                    // subir a firebase y archivos
                    finish();
                }
                else{
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                    tv_error.setTextSize(18);
                }
            }
        });
        date = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate();
            }
        };
        et_fecha_nacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Signup_owner.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        et_direccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Signup_owner.this, MapsActivity.class);
                startActivityForResult(i, Permisos.ADDRESS_PICKER);
            }
        });
    }

    private void updateDate() {
        String format = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        et_fecha_nacimiento.setText(sdf.format(calendar.getTime()));
    }

    private Dueno registrarDueno() {
        boolean completo = true;
        int radioButtonID = rg_genero.getCheckedRadioButtonId();
        View radioButton = rg_genero.findViewById(radioButtonID);
        int idx = rg_genero.indexOfChild(radioButton);

        String email = et_email.getText().toString();
        String contrasena = et_password.getText().toString();
        String nombre = et_nombre.getText().toString();
        String cedula = et_cedula.getText().toString();
        String fecha = et_fecha_nacimiento.getText().toString();
        String phone = et_phone.getText().toString();
        String direccion = et_direccion.getText().toString();
        String genero;

        if(selectedImage == null)
            completo = false;
        if(email.isEmpty()){
            et_email.setError(getString(R.string.obligatorio));
            completo = false;
        }
        if(contrasena.isEmpty()){
            et_password.setError(getString(R.string.obligatorio));
            completo = false;
        }
        if(nombre.isEmpty()){
            et_nombre.setError(getString(R.string.obligatorio));
            completo = false;
        }
        if(cedula.isEmpty()){
            et_cedula.setError(getString(R.string.obligatorio));
            completo = false;
        }
        if(fecha.isEmpty()){
            et_fecha_nacimiento.setError(getString(R.string.obligatorio));
            completo = false;
        }
        if(phone.isEmpty()){
            et_phone.setError(getString(R.string.obligatorio));
            completo = false;
        }
        if(direccion.isEmpty()){
            et_direccion.setError(getString(R.string.obligatorio));
            completo = false;
        }
        if(radioButtonID==-1)
            completo = false;

        if(!completo)
            return null;
        genero = ((RadioButton)(rg_genero.getChildAt(idx))).getText().toString();

        return new Dueno(email, nombre, Integer.parseInt(cedula), calendar.getTime(), Integer.parseInt(phone), genero, new Direccion(direccion, address));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case Permisos.IMAGE_PICKER_REQUEST:
                if(resultCode == RESULT_OK){
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        ib_upload_photo.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                return;
            case Permisos.REQUEST_IMAGE_CAPTURE:
                if(resultCode == RESULT_OK){
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    ib_upload_photo.setImageBitmap(imageBitmap);
                }
                return;
            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }
}