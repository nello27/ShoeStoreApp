package com.example.shoesapp;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.util.Collections;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 1;
    private static final int REQUEST_CODE_PERMISSIONS = 100;
    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private List<String> dataList;
    private List<ImageItem> imageList;
    private ImageAdapter adapter;

    private RecyclerView recyclerView; // Declarar recyclerView como una variable miembro

    //obtener la lista de archivos de la carpeta "MisImagenes":
    private List<File> getFilesFromFolder(File folder) {
        List<File> fileList = new ArrayList<>();

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        fileList.add(file);
                    }
                }
            }
        }

        return fileList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Definiendo una lista
        dataList = new ArrayList<>();
        dataList.add("Productos");
        dataList.add("Servicios");
        dataList.add("Sucursales");

        // Obtén una referencia al ListView
        ListView listView = findViewById(R.id.listView);

        // Crea un ArrayAdapter para cargar los datos en el ListView
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        // Asigna el adaptador al ListView
        listView.setAdapter(arrayAdapter);
        //Toast.makeText(MainActivity.this, "has pulsado"+dataList.get(position),Toast.LENGTH_SHORT);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Has pulsado "+dataList.get(position),Toast.LENGTH_SHORT).show();
            }
        });

        Button button = findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Evento Proximamente disponible para reto2  ",Toast.LENGTH_SHORT).show();
            }
        });

        //Agregar

        // Dentro del método onCreate() o en un método auxiliar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            }
        }

        // Obtén una referencia al botón
        Button btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);

        // Configura el OnClickListener para el botón
        btnSeleccionarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crea un Intent para seleccionar imágenes
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*"); // Solo se muestran las imágenes

                // Inicia el Intent para seleccionar imágenes
                startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
            }
        });

        // Obtén una referencia al RecyclerView
        recyclerView = findViewById(R.id.recyclerView);

        // Crea un LinearLayoutManager para establecer la orientación del RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Crea una instancia de tu ImageAdapter y asigna la lista de imágenes
        imageList = new ArrayList<>();
        adapter = new ImageAdapter(imageList);
        recyclerView.setAdapter(adapter);

        // Obtén la carpeta "MisImagenes"
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "MisImagenes");

        // Verifica si la carpeta existe
        if (!folder.exists()) {
            // Crea la carpeta si no existe
            folder.mkdirs();
        }

        // Obtén la lista de archivos de la carpeta "MisImagenes"
        List<File> fileList = getFilesFromFolder(folder);

        // Crea una lista de ImageItem a partir de los archivos
        for (File file : fileList) {
            ImageItem imageItem = new ImageItem(file.getAbsolutePath());
            imageList.add(imageItem);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            // Obtiene la URI de la imagen seleccionada
            Uri selectedImageUri = data.getData();

            // Crea una carpeta para guardar las imágenes en la carpeta de descargas
            File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "MisImagenes");
            if (!folder.exists()) {
                if (folder.mkdirs()) {
                    // El directorio se creó exitosamente
                    Toast.makeText(this, "DIRECTORIO CREADO", Toast.LENGTH_SHORT).show();
                } else {
                    // Error al crear el directorio
                    Toast.makeText(this, "Error al crear el directorio", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            // Utilizar un identificador único generado mediante la clase UUID
            String uniqueID = UUID.randomUUID().toString();
            String imageFileName = "imagen_" + uniqueID + ".jpg";

            // Crea un archivo en la carpeta
            File file = new File(folder, imageFileName);

            try {
                // Crea un InputStream a partir de la URI de la imagen seleccionada
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);

                // Crea un OutputStream para escribir la imagen en el archivo
                OutputStream outputStream = new FileOutputStream(file);

                // Copia los bytes de la imagen al archivo
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                // Cierra los streams
                outputStream.close();
                inputStream.close();

                // Muestra un mensaje de éxito
                Toast.makeText(this, "Imagen guardada correctamente", Toast.LENGTH_SHORT).show();

                // Agrega la imagen al imageList y notifica al adaptador del cambio
                ImageItem imageItem = new ImageItem(file.getAbsolutePath());
                imageList.add(imageItem); // Agrega la nueva imagen a la lista existente
                // Notifica al adaptador del cambio en la lista de imágenes
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(imageList.size() - 1);

            } catch (IOException e) {
                e.printStackTrace();
                // Muestra un mensaje de error
                Toast.makeText(this, "Error al guardar la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static class ImageItem {
        private String imagePath;

        public ImageItem(String imagePath) {
            this.imagePath = imagePath;
        }

        public String getImagePath() {
            return imagePath;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menur_m, menu);
        return true;



    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId(); //asigna la seleccion actual a una variable
        if (id == R.id.productos) {
            Toast.makeText(this, "Productos", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.servicios) {
            Toast.makeText(this, "Servicios", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.sucursales) {
            Toast.makeText(this, "Sucursales", Toast.LENGTH_SHORT).show();
        }


        return super.onOptionsItemSelected(item);
    }
}

