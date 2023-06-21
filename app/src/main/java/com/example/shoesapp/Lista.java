package com.example.shoesapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Lista extends AppCompatActivity {

    private static final int REQUEST_CODE_SELECT_IMAGE = 1;
    private static final int REQUEST_CODE_PERMISSIONS = 100;
    private static final String[] REQUIRED_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    // Obtén una referencia al ListView
    //ListView listView = findViewById(R.id.listView);
    private ImageAdapter adapter;
    private List<MainActivity.ImageItem> imageList;
    private RecyclerView recyclerView; // Declarar recyclerView como una variable miembro

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
        setContentView(R.layout.activity_lista);

        //Agregar

        // Dentro del método onCreate() o en un método auxiliar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            }
        }

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
            MainActivity.ImageItem imageItem = new MainActivity.ImageItem(file.getAbsolutePath());
            imageList.add(imageItem);
        }

        // Obtener el contexto adecuado (por ejemplo, el contexto de la actividad)
        Context context = getApplicationContext();

        // Crear la lista de elementos
        List<MainActivity.ListItem> items = new ArrayList<>();
        items.add(new MainActivity.ListItem(1, context.getDrawable(R.drawable.image1), "Nombre 1"));
        items.add(new MainActivity.ListItem(2, context.getDrawable(R.drawable.image2), "Nombre 2"));
        items.add(new MainActivity.ListItem(3, context.getDrawable(R.drawable.image3), "Nombre 3"));
        // Agregar más elementos según sea necesario

        //items.add(new ListItem(2, "ruta_imagen_2"));
        // Agregar más elementos según sea necesario

        // Crear y asignar el adaptador personalizado al ListView
        CustomAdapter adapter = new CustomAdapter(this, items);
       //listView.setAdapter(adapter);

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
                MainActivity.ImageItem imageItem = new MainActivity.ImageItem(file.getAbsolutePath());
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


}