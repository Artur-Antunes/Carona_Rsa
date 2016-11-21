package br.com.rsa.carona.carona_rsa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import br.com.rsa.carona.carona_rsa.entidades.Funcoes;
import br.com.rsa.carona.carona_rsa.entidades.ManipulaDados;
import br.com.rsa.carona.carona_rsa.entidades.Usuario;


public class EditarDadosActivity extends AppCompatActivity {

    ManipulaDados mDados;
    Usuario usuarioEditar;
    private Spinner sexoEditar;
    private EditText nomeEditar;
    private EditText emailEditar;
    private EditText matriculaEditar;
    private EditText telefoneEditar;
    private ImageView imFoto;
    private String foto = null;
    private ImageButton editarFoto;
    public static final int IMAGEM_CAM = 2;
    public static final int RESULT_SELECT_IMAGE = 5;
    private String extFoto = null;
    public static final int PIC_CROP = 3;
    AlertDialog actions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_dados);
        mDados = new ManipulaDados(EditarDadosActivity.this);
        usuarioEditar = mDados.getUsuario();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sexo_usuario, android.R.layout.simple_spinner_item);
        sexoEditar = (Spinner) findViewById(R.id.etSexoUsuario);
        sexoEditar.setAdapter(adapter);
        nomeEditar = (EditText) findViewById(R.id.editarNomeValor);
        matriculaEditar = (EditText) findViewById(R.id.editarMatriculaValor);
        telefoneEditar = (EditText) findViewById(R.id.editarTelefoneValor);
        emailEditar = (EditText) findViewById(R.id.editarEmailValor);
        imFoto = (ImageView) findViewById(R.id.editar_foto);
        editarFoto = (ImageButton) findViewById(R.id.button_editarImagem);
        editarFoto.bringToFront();

        String nome = usuarioEditar.getNome();
        String matricula = usuarioEditar.getMatricula();
        String email = usuarioEditar.getEmail();
        String telefone = usuarioEditar.getTelefone();
        nomeEditar.setText(nome);
        matriculaEditar.setText(matricula);
        emailEditar.setText(email);
        telefoneEditar.setText(telefone);
        byte[] decodedString = Base64.decode(usuarioEditar.getFoto(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imFoto.setImageBitmap(bitmap);
        imFoto.setScaleType(ImageView.ScaleType.FIT_XY);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Foto de Perfil");
        String[] options = {"GALERIA", "CÂMERA", "REMOVER" };
        builder.setItems(options, actionListener);
        builder.setNegativeButton("Cancelar", null);
        actions = builder.create();

        editarFoto.setOnClickListener(buttonListener);
        //Log.e("dados->",usuarioEditar.getNome()+"-"+usuarioEditar.getSobrenome()+"-"+usuarioEditar.getMatricula()+"-"+usuarioEditar.getSexo()+"-"+usuarioEditar.getSenha());
    }

    DialogInterface.OnClickListener actionListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case 0: // geleria
                    Galeria();
                    break;
                case 1: // camera
                    Camera();
                    break;
                case 2: // remover
                    break;
                default:
                    break;
            }
        }
    };


    View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            actions.show();
        }
    };

    public void Galeria() {
        try {
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_SELECT_IMAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Camera() {

        File file = new File(android.os.Environment.getExternalStorageDirectory(), "img.png");
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, IMAGEM_CAM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        File arquivo;
        switch (requestCode) {
            case RESULT_SELECT_IMAGE:

                if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                    try {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        cursor.close();
                        arquivo = new File(picturePath);
                        extFoto = new Funcoes().getExtencaoImagem(arquivo.getPath());
                        performCrop(Uri.fromFile(arquivo));
                   /*
                        //return Image Path to the Main Activity
                        Intent returnFromGalleryIntent = new Intent();
                        returnFromGalleryIntent.putExtra("picturePath",picturePath);
                        setResult(RESULT_OK,returnFromGalleryIntent);
                        finish();*/
                    } catch (Exception e) {
                        e.printStackTrace();
                        Intent returnFromGalleryIntent = new Intent();
                        setResult(RESULT_CANCELED, returnFromGalleryIntent);
                        finish();
                    }
                } else {
                    Log.i("erro", "RESULT_CANCELED");
                    Intent returnFromGalleryIntent = new Intent();
                    setResult(RESULT_CANCELED, returnFromGalleryIntent);
                    finish();
                }
                break;

            case PIC_CROP:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap bitmap = extras.getParcelable("data");
                    foto = new Funcoes().BitMapToString(bitmap);
                    imFoto.setImageResource(0);
                    imFoto.setImageBitmap(bitmap);
                    imFoto.setScaleType(ImageView.ScaleType.FIT_XY);
                    break;
                }
            case IMAGEM_CAM:
                arquivo = new File(android.os.Environment.getExternalStorageDirectory(), "img.png");
                extFoto = new Funcoes().getExtencaoImagem(arquivo.getPath());
                performCrop(Uri.fromFile(arquivo));
                break;

        }
    }

    private void performCrop(Uri picUri) {
        try {

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 300);
            cropIntent.putExtra("outputY", 300);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();// A activity está prestes a se tornar visíve

    }
}