package br.com.rsa.carona.carona_rsa.entidades;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import br.com.rsa.carona.carona_rsa.entidades.Usuario;

public class ManipulaDados {// Classe normal sem nenhuma herança !
	
	public static final String SP_NOME="detalhesDoUsuario"; //Constante chamada SP_NOME.
	SharedPreferences usuarioLocal;// Variavel do tipo SharedPreferences !
	
	public ManipulaDados(Context context){	// Metodo construtor que exige o contexto como parametro.
		usuarioLocal=context.getSharedPreferences(SP_NOME,0); //usuarioLocal recebe um identificador e o modo de acesso do metodo do parametro context!
	}
	
	public void gravarDados(Usuario usuario){ // Metodo para guardar os dados do usuario quando logar,exige um parametro do tipo usuario !

		SharedPreferences.Editor editorBancoDeDados=usuarioLocal.edit();
		editorBancoDeDados.putInt("id", usuario.getId());//Editardo os valores que serao guardados por meio da interface Editor do Shered prefeneces!
		editorBancoDeDados.putString("nome",usuario.getNome()); 	// Guardando o nome de usuario com uma clave chamada "nome".
		editorBancoDeDados.putString("sobrenome",usuario.getSobrenome());
		editorBancoDeDados.putString("matricula",usuario.getMatricula());
		editorBancoDeDados.putString("email", usuario.getEmail());	// Guardando a idade do usuario com uma clave chamada "idade".
		editorBancoDeDados.putString("telefone",usuario.getTelefone()); // Guardando o usuario do usuario com uma clave chamada "usuario".
		editorBancoDeDados.putString("sexo", usuario.getSexo());
		editorBancoDeDados.putBoolean("cnh", usuario.isCnh());
		editorBancoDeDados.putInt("id_carona", usuario.getIdCaronaSolicitada());
		editorBancoDeDados.putString("senha", usuario.getSenha());
		editorBancoDeDados.putString("foto", usuario.getFoto());
		editorBancoDeDados.commit(); //Executando a ediçao.
	}

	public void gravarUltimaCarona(int id){ // Metodo para guardar os dados do usuario quando logar,exige um parametro do tipo usuario !
		SharedPreferences.Editor editorBancoDeDados=usuarioLocal.edit();
		editorBancoDeDados.putInt("ultimo_id_carona", id);
		editorBancoDeDados.commit(); //Executando a ediçao.
	}
	public void gravarUltimaCaronaAceita(int id){ // Metodo para guardar os dados do usuario quando logar,exige um parametro do tipo usuario !
		SharedPreferences.Editor editorBancoDeDados=usuarioLocal.edit();
		editorBancoDeDados.putInt("ultimo_id_carona_aceita", id);
		editorBancoDeDados.commit(); //Executando a ediçao.
	}
	public void setCaronaSolicitada(int id){
		SharedPreferences.Editor editorBancoDeDados=usuarioLocal.edit();
		editorBancoDeDados.putInt("id_carona", id);
		editorBancoDeDados.commit();
	}

	public int getCaronaSolicitada(){
		int id_carona=usuarioLocal.getInt("id_carona", -1);
		return id_carona;
	}
	public int getUltimoIdCarona(){
		int id_carona=usuarioLocal.getInt("ultimo_id_carona",0);
		return id_carona;
	}
	public int getUltimoIdCaronaAceita(){
		int id_carona=usuarioLocal.getInt("ultimo_id_carona_aceita",0);
		return id_carona;
	}
	
	public void limparDados(){ //Metodo para limpar od dados.
		SharedPreferences.Editor editorBancoDeDados=usuarioLocal.edit();// editando..
		editorBancoDeDados.clear();//limpar.
		editorBancoDeDados.commit();// Executar limpeza.
	}
	
	public void setLogado(boolean logado){ //Verifica se tem algum usuario ja logado.
		SharedPreferences.Editor editorBancoDeDados=usuarioLocal.edit();// Editar.
		editorBancoDeDados.putBoolean("logado",logado); // guardando falso/verdade numa chave chamada "logado".
		editorBancoDeDados.commit();	// Executar ediçao.
		
	}

	public Usuario getUsuario(){ //Metodo que retorna o usuario logado.
		if(usuarioLocal.getBoolean("logado",false)==false){ //Caso nao tenha ninguem logado fara.
			return null;// se nao tiver nenhum usuario logado retornar vazio!
		}
		//caso contrario->
		int id=usuarioLocal.getInt("id",-1);
		String nome=usuarioLocal.getString("nome", ""); // Nome recebe o valor atribuido a chave nome.
		String sobrenome=usuarioLocal.getString("sobrenome","");// usuario recebe o valor atribuido a chave usuario.
		String matricula=usuarioLocal.getString("matricula","");// usuario recebe o valor atribuido a chave usuario.
		String email=usuarioLocal.getString("email","");// usuario recebe o valor atribuido a chave usuario.
		String telefone=usuarioLocal.getString("telefone","");// usuario recebe o valor atribuido a chave usuario.
		String sexo=usuarioLocal.getString("sexo","");// usuario recebe o valor atribuido a chave usuario.
		Boolean cnh=usuarioLocal.getBoolean("cnh", true);
		String foto=usuarioLocal.getString("foto", "");
		String senha=usuarioLocal.getString("senha","");// usuario recebe o valor atribuido a chave usuario.
		int id_carona=usuarioLocal.getInt("id_carona",-1);

		Usuario usuarioLogado = new Usuario(nome, sobrenome, matricula, email, telefone, sexo, cnh);    //Novo obj de usuário.
		usuarioLogado.setId(id);
		usuarioLogado.setSenha(senha);
		usuarioLogado.setFoto(foto);
		usuarioLogado.setIdCaronaSolicitada(id_carona);
		return usuarioLogado;//Retorna o objeto usuarioLogado !
	}

}
