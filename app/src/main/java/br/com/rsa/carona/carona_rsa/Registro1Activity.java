package br.com.rsa.carona.carona_rsa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;


import br.com.rsa.carona.carona_rsa.entidades.Funcoes;
import br.com.rsa.carona.carona_rsa.entidades.Usuario;

public class Registro1Activity extends AppCompatActivity {
    private EditText nomeRegistro,sobrenomeRegistro;
    private TextView escolherFoto;
    private Button bSalvar;
    ImageView imagem;
    private String foto = null;
    private String extFoto = null;
    public static final int IMAGEM_CAM = 2;
    public static final int PIC_CROP = 3;
    public static final int RESULT_SELECT_IMAGE = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_registro1);
        imagem = (ImageView) findViewById(R.id.c_imagem);
        nomeRegistro = (EditText) findViewById(R.id.tv_nome);
        escolherFoto = (TextView) findViewById(R.id.escolherFoto);
        nomeRegistro.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    if (nomeRegistro.length() <= 0) {
                        nomeRegistro.setError(" Campo obrigatório !");
                    }
                }
            }
        });
        sobrenomeRegistro = (EditText) findViewById(R.id.c_sobrenome);
        bSalvar = (Button) findViewById(R.id.b_salvar);
        bSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (foto != null) {
                    String nome = nomeRegistro.getText().toString();
                    String sobrenome = sobrenomeRegistro.getText().toString();
                    if (!nome.equals("") && !sobrenome.equals("")) {
                        Usuario user=new Usuario(nome);
                        user.setSobrenome(sobrenome);
                        user.setFoto(foto);
                        user.setExtFoto(extFoto);

                        Intent i = new Intent(Registro1Activity.this, Registro2Activity.class);
                        Registro2Activity.usuario = user;
                        startActivity(i);
                    } else {
                        Toast.makeText(Registro1Activity.this, "PREENCHA TODOS OS CAMPOS!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Registro1Activity.this, "COLOQUE UMA FOTO NO PERFIL!", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                }else {
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
                    imagem.setImageBitmap(bitmap);
                    imagem.setScaleType(ImageView.ScaleType.FIT_XY);
                    escolherFoto.setVisibility(View.INVISIBLE);
                }else if(resultCode==Activity.RESULT_CANCELED){
                    Toast.makeText(Registro1Activity.this, "Cancelado!", Toast.LENGTH_SHORT).show();
                }
                break;
            case IMAGEM_CAM:
                if(resultCode == Activity.RESULT_OK) {
                    arquivo = new File(android.os.Environment.getExternalStorageDirectory(), "img.png");
                    extFoto = new Funcoes().getExtencaoImagem(arquivo.getPath());
                    performCrop(Uri.fromFile(arquivo));
                }else if(resultCode == Activity.RESULT_CANCELED){
                    Toast.makeText(Registro1Activity.this, "Cancelado !", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void voltar(View v){
        finish();
    }

    private void fotoGaleria(){
        try {
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_SELECT_IMAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fotoCamera(){
        File file = new File(android.os.Environment.getExternalStorageDirectory(), "img.png");
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, IMAGEM_CAM);
    }

    public void escolherFoto(View v){
        AlertDialog.Builder dialog = new AlertDialog.Builder(Registro1Activity.this);
        dialog.setCancelable(true);
        dialog.setTitle("FOTO PERFIL");
        dialog.setPositiveButton("GALERIA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                        fotoGaleria();
            }
        });

        dialog.setNegativeButton("CÂMERA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                        fotoCamera();
            }
        });


        final AlertDialog alert = dialog.create();
        alert.show();

    }


    private void performCrop(Uri picUri) {
        try {

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 300);
            cropIntent.putExtra("outputY", 300);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, PIC_CROP);
        }
        catch (ActivityNotFoundException anfe) {
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
