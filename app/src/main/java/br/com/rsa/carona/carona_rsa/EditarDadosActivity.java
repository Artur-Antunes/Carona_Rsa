package br.com.rsa.carona.carona_rsa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import br.com.rsa.carona.carona_rsa.controllers.GetRetorno;
import br.com.rsa.carona.carona_rsa.controllers.RequisicoesServidor;
import br.com.rsa.carona.carona_rsa.entidades.Funcoes;
import br.com.rsa.carona.carona_rsa.entidades.ManipulaDados;
import br.com.rsa.carona.carona_rsa.entidades.Usuario;


public class EditarDadosActivity extends AppCompatActivity {

    ManipulaDados mDados;
    Usuario usuarioEditar;
    private EditText nomeEditar;
    private EditText sobrenomeEditar;
    private EditText emailEditar;
    private EditText matriculaEditar;
    private EditText telefoneEditar;
    private Spinner sexoEditar;
    private Switch cnhEditar;
    private ImageView imFoto;
    private String foto = null;
    private ImageButton editarFoto;
    public static final int IMAGEM_CAM = 2;
    public static final int RESULT_SELECT_IMAGE = 5;
    private String extFoto = null;
    public static final int PIC_CROP = 3;
    AlertDialog actions;
    private Button salvarAteracoes;

    boolean imagemEditada = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_editar_dados);
        mDados = new ManipulaDados(EditarDadosActivity.this);
        usuarioEditar = mDados.getUsuario();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sexo_usuario, android.R.layout.simple_spinner_item);
        sexoEditar = (Spinner) findViewById(R.id.etSexoUsuario);
        sexoEditar.setAdapter(adapter);
        nomeEditar = (EditText) findViewById(R.id.editarNomeValor);
        nomeEditar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (nomeEditar.length() <= 0) {
                        nomeEditar.setError(" Campo obrigatório !");
                    }
                }
            }
        });
        sobrenomeEditar = (EditText) findViewById(R.id.editarSobrenomeValor);
        matriculaEditar = (EditText) findViewById(R.id.editarMatriculaValor);
        matriculaEditar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (matriculaEditar.length() <= 7) {
                        matriculaEditar.setError(" Digite todos os números !");
                    }
                }
            }
        });
        telefoneEditar = (EditText) findViewById(R.id.editarTelefoneValor);
        telefoneEditar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (telefoneEditar.length() <= 13) {
                        telefoneEditar.setError(" Digite todos os números !");
                    }
                }
            }
        });
        cnhEditar = (Switch) findViewById(R.id.editarCnhValor);
        emailEditar = (EditText) findViewById(R.id.editarEmailValor);
        emailEditar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!new Funcoes().isEmailValid(emailEditar.getText().toString())) {
                        emailEditar.setError(" Formato inválido !");
                    }
                }
            }
        });
        imFoto = (ImageView) findViewById(R.id.editar_foto);
        salvarAteracoes = (Button) findViewById(R.id.b_salvar_alteracoes);
        editarFoto = (ImageButton) findViewById(R.id.button_editarImagem);
        editarFoto.bringToFront();

        String nome = usuarioEditar.getNome();
        String sobrenome = usuarioEditar.getSobrenome();
        String matricula = usuarioEditar.getMatricula();
        String email = usuarioEditar.getEmail();
        String telefone = usuarioEditar.getTelefone();
        boolean cnhUsuario = usuarioEditar.isCnh();
        Log.e(usuarioEditar.getSexo().toString(), "sexo_antigo");
        int sexoUsuarioPosicao = (usuarioEditar.getSexo().equals("M") ? 0 : 1);

        cnhEditar.setChecked(cnhUsuario);
        nomeEditar.setText(nome);
        sobrenomeEditar.setText(sobrenome);
        matriculaEditar.setText(matricula);
        emailEditar.setText(email);
        telefoneEditar.setText(telefone);
        sexoEditar.setSelection(sexoUsuarioPosicao);

        byte[] decodedString = Base64.decode(usuarioEditar.getFoto(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imFoto.setImageBitmap(bitmap);
        imFoto.setScaleType(ImageView.ScaleType.FIT_XY);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Foto do Perfil");
        String[] options = {"Galeria", "Câmera"};
        builder.setItems(options, actionListener);
        builder.setNegativeButton("Cancelar", null);
        actions = builder.create();
        editarFoto.setOnClickListener(buttonListener);

        salvarAteracoes.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {


                final String nome = nomeEditar.getText().toString();
                final String sobrenome = sobrenomeEditar.getText().toString();
                final String matricula = matriculaEditar.getText().toString();
                String email = emailEditar.getText().toString();
                final String telefone = telefoneEditar.getText().toString();
                final boolean cnh = cnhEditar.isChecked();
                String sexo = sexoEditar.getSelectedItem().toString();
                sexo = new Funcoes().retornaSimbolo(sexo);

                final Usuario usuarioEditado = verificaCamposAlterados(matricula, nome, sobrenome, telefone, email, sexo, cnh);

                if (usuarioEditado != null) {
                    if (!nome.equals("") && !sobrenome.equals("") && !matricula.equals("") && !email.equals("")) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(EditarDadosActivity.this);
                        dialog.setTitle(R.string.title_confirmacao)
                                .setMessage(R.string.alert_editar_perfil)
                                .setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialoginterface, int i) {
                                        startActivity(new Intent(EditarDadosActivity.this, ExibirDadosUsuarioActivity.class));
                                    }
                                })
                                .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialoginterface, int i) {
                                        usuarioEditado.setEditado(true);
                                        RequisicoesServidor rs = new RequisicoesServidor(EditarDadosActivity.this);
                                        rs.gravaDadosDoUsuario(usuarioEditado, new GetRetorno() {
                                            @Override
                                            public void concluido(Object object) {

                                                String nomeAlterar = nomeEditar.getText().toString();
                                                String sobrenomeAlterar = sobrenomeEditar.getText().toString();
                                                String matriculaAlterar = matriculaEditar.getText().toString();
                                                String emailAlterar = emailEditar.getText().toString();
                                                String telefoneAlterar = telefoneEditar.getText().toString();
                                                String sexoAlterar = new Funcoes().retornaSimbolo(sexoEditar.getSelectedItem().toString());
                                                boolean cnhAlterar = cnhEditar.isChecked();

                                                BitmapDrawable drawable = (BitmapDrawable) imFoto.getDrawable();
                                                Bitmap bitmap = drawable.getBitmap();
                                                String ft = new Funcoes().BitMapToString(bitmap);

                                                int idAlterar = usuarioEditar.getId();
                                                String senhaAlterar = usuarioEditar.getSenha();
                                                int idCaronaAlterar = usuarioEditar.getIdCaronaSolicitada();

                                                Usuario usuarioLocal = new Usuario(nomeAlterar, sobrenomeAlterar, matriculaAlterar, emailAlterar, telefoneAlterar, sexoAlterar, cnhAlterar);
                                                usuarioLocal.setFoto(ft);
                                                usuarioLocal.setId(idAlterar);
                                                usuarioLocal.setSenha(senhaAlterar);
                                                usuarioLocal.setIdCaronaSolicitada(idCaronaAlterar);

                                                mDados.gravarDados(usuarioLocal);
                                                startActivity(new Intent(EditarDadosActivity.this, ExibirDadosUsuarioActivity.class));
                                            }

                                            @Override
                                            public void concluido(Object object, Object object2) {

                                            }

                                        });
                                    }
                                }).show();
                    } else {
                        Toast.makeText(EditarDadosActivity.this, "CAMPOS EM BRANCO!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(EditarDadosActivity.this, "NENHUM CAMPO ALTERADO!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditarDadosActivity.this, ExibirDadosUsuarioActivity.class));
                }
            }


        });
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

    //,String nome,String sobrenome,String telefone,String email,boolean cnh, String sexo
    private Usuario verificaCamposAlterados(String matricula, String nome, String sobrenome, String telefone, String email, String sexo, boolean cnh) {

        Usuario usuarioEditado = new Usuario(usuarioEditar.getId());
        int alteracaoes = 0;
        if (!usuarioEditar.getMatricula().toString().equals(matricula)) {
            usuarioEditado.setMatricula(matricula);
            alteracaoes++;
        } else {
            usuarioEditado.setMatricula(null);
        }

        if (!usuarioEditar.getNome().equals(nome)) {
            usuarioEditado.setNome(nome);
            alteracaoes++;
        } else {
            usuarioEditado.setNome(null);
        }

        if (!usuarioEditar.getSobrenome().equals(sobrenome)) {
            usuarioEditado.setSobrenome(sobrenome);
            alteracaoes++;
        } else {
            usuarioEditado.setSobrenome(null);
        }

        if (!usuarioEditar.getTelefone().equals(telefone)) {
            usuarioEditado.setTelefone(telefone);
            alteracaoes++;
        } else {
            usuarioEditado.setTelefone(null);
        }

        if (!usuarioEditar.getEmail().equals(email)) {
            usuarioEditado.setEmail(email);
            alteracaoes++;
        } else {
            usuarioEditado.setEmail(null);
        }

        if (!usuarioEditar.getSexo().toString().equals(sexo)) {
            usuarioEditado.setSexo(sexo);
            alteracaoes++;
        } else {
            usuarioEditado.setSexo(null);
        }

        if (imagemEditada) {
            usuarioEditado.setFoto(foto);
            usuarioEditado.setExtFoto(extFoto);
            alteracaoes++;
        } else {
            usuarioEditado.setFoto(null);
            usuarioEditado.setExtFoto(null);
        }
        if (!cnh == usuarioEditar.isCnh()) {
            alteracaoes++;
        }

        usuarioEditado.setChn(cnh);
        if (alteracaoes == 0) {
            usuarioEditado = null;
        }
        return usuarioEditado;
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
                        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
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
                    imagemEditada = true;
                    break;
                }else if(resultCode==Activity.RESULT_CANCELED){
                    Toast.makeText(EditarDadosActivity.this, "Cancelado!", Toast.LENGTH_SHORT).show();
                }
                break;
            case IMAGEM_CAM:
                if(resultCode == Activity.RESULT_OK) {
                    arquivo = new File(android.os.Environment.getExternalStorageDirectory(), "img.png");
                    extFoto = new Funcoes().getExtencaoImagem(arquivo.getPath());
                    performCrop(Uri.fromFile(arquivo));
                }else if(resultCode == Activity.RESULT_CANCELED){
                    Toast.makeText(EditarDadosActivity.this, "Cancelado !", Toast.LENGTH_SHORT).show();
                }
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

    @Override
    protected void onStop() {
        super.onStop();
        // A activity não está mais visível mas permanece em memória
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // A activity está prestes a ser destruída (removida da memória)
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            //Intent intent = new Intent(this, ExibirDadosUsuarioActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}