package co.edu.javeriana.wow_guau.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import co.edu.javeriana.wow_guau.R;
import co.edu.javeriana.wow_guau.model.Perro;

public class ListaMascotasActivity extends AppCompatActivity
{
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    Button btn_add;
    RecyclerView recycleView;

    private ArrayList<Perro> listaPerros;

    private PerroAdapter mPerroAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_mascotas);

        btn_add = findViewById(R.id.btn_add);
        recycleView = findViewById(R.id.recycleViewList);
        listaPerros = new ArrayList<Perro>();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycleView.setLayoutManager(mLayoutManager);
        recycleView.setItemAnimator(new DefaultItemAnimator());

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        fillPets();

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListaMascotasActivity.this, Signup_dog.class);
                startActivity(i);
            }
        });
        /*btn_monitorear = findViewById(R.id.cl_monitorear);

        btn_monitorear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ActividadSeleccionMascotaMonitoreo.class);
                i.putExtra("texto", "monitorear");
                startActivity(i);
            }
        });

        btn_actualizar_mascotas.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent i= new Intent(getApplicationContext(), ActividadSeleccionMascotaMonitoreo.class);
                i.putExtra("texto", "actualizar");
                startActivity(i);
            }
        });*/
    }

    public void fillPets()
    {
        db.collection("Mascotas")
                .whereEqualTo("ownerID",currentUser.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot document : task.getResult())
                            {
                                Perro perro = new Perro();
                                perro.setPerroID(document.getId());
                                perro.setNombre(document.getString("nombre"));
                                perro.setDireccionFoto(document.getString("direccionFoto"));

                                listaPerros.add(perro);
                            }
                            mostrarPerros();
                        }
                        else
                        {
                            Log.d("Errror Perron", "Error al buscar perritos ", task.getException());
                        }
                    }
                });

    }

    private void mostrarPerros()
    {
        if(listaPerros.size()>0)
        {
            mPerroAdapter = new PerroAdapter(listaPerros);
            recycleView.setAdapter(mPerroAdapter);
        }
        else
        {
            Toast.makeText(this.getApplicationContext(),
                    "Aun no tienes consentidos agregados",
                    Toast.LENGTH_LONG).show();
        }
}
}
