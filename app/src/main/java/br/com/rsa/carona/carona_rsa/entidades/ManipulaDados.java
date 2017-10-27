package br.com.rsa.carona.carona_rsa.entidades;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;

public class ManipulaDados {// Classe normal sem nenhuma herança !
	
	public static final String SP_NOME="detalhesDoUsuario"; //Constante chamada SP_NOME.
	SharedPreferences usuarioLocal;// Variavel do tipo SharedPreferences !

	public ManipulaDados(Context context){	// Metodo construtor que exige o contexto como parametro.
		usuarioLocal=context.getSharedPreferences(SP_NOME,0); //usuarioLocal recebe um identificador e o modo de acesso do metodo do parametro context!
	}
	
	public void gravarDados(Usuario usuario){ // Metodo para guardar os dados do usuario quando logar,exige um parametro do tipo usuario !
		SharedPreferences.Editor editorBancoDeDados=usuarioLocal.edit();
		editorBancoDeDados.putInt("id", usuario.getId());
		editorBancoDeDados.putString("nome", usuario.getNome());
		editorBancoDeDados.putString("sobrenome",usuario.getSobrenome());
		editorBancoDeDados.putString("matricula",usuario.getMatricula());
		editorBancoDeDados.putString("email", usuario.getEmail());
		editorBancoDeDados.putString("telefone", usuario.getTelefone());
		editorBancoDeDados.putString("sexo", usuario.getSexo());
		editorBancoDeDados.putBoolean("cnh", usuario.isCnh());
		editorBancoDeDados.putString("senha", usuario.getSenha());
		editorBancoDeDados.putString("foto", usuario.getFoto());
		editorBancoDeDados.putInt("quant_caronas_aceitas", 0);
		editorBancoDeDados.commit();
	}

	//CARONA OFERECIDA ->
	public void setCaronaOferecida(Carona car){//Setar a carona oferecida
		SharedPreferences.Editor editorBancoDeDados=usuarioLocal.edit();
		Gson gson = new Gson();
		String json = gson.toJson(car);
		editorBancoDeDados.putString("carOf", json);
		editorBancoDeDados.commit();
	}

	public Carona getCaronaOferecida(){//Retorna a carona oferecida
		Gson gson = new Gson();
		String json = usuarioLocal.getString("carOf",null);
		Carona car= gson.fromJson(json, Carona.class);
		return car;
	}

	private int verificaUsuarioBDLocal(Usuario user){//Verifica se existe um determinado usuário
		for(int t=0;t<getTtParCarOf();t++){
			if(getParticipantesCarOferecida(t).getId()==user.getId()){
				return t;//Retorna o ID do User
			}
		}
		return -1;
	}

	public void setParticipantesCarOferecida(Usuario user,int pos){//Setando um usuário localmente, indicando a posição!
		SharedPreferences.Editor editorBancoDeDados=usuarioLocal.edit();
		Gson gson = new Gson();
		String json = gson.toJson(user);
		int vr=verificaUsuarioBDLocal(user);
		if(vr==-1){
			editorBancoDeDados.putString("partCarOf_" + pos, json);
			setTtParCarOf(getTtParCarOf()+1);
		}else{
			editorBancoDeDados.remove("partCarOf_"+vr);
			editorBancoDeDados.putString("partCarOf_"+vr, json);//SUBSTITUIDO
		}
		editorBancoDeDados.commit();
	}

	public void clearParticipanteCarOferecida(Usuario user){
		SharedPreferences.Editor editorBancoDeDados=usuarioLocal.edit();
		int vr=verificaUsuarioBDLocal(user);
		if(vr!=-1){
			editorBancoDeDados.remove("partCarOf_"+vr);
			editorBancoDeDados.commit();
		}
	}

	public Usuario getParticipantesCarOferecida(int pos){//Retorna um usuário
		Gson gson = new Gson();
		String json = usuarioLocal.getString("partCarOf_" + pos, "");
		Usuario caroneiro= gson.fromJson(json, Usuario.class);
		return caroneiro;
	}

	public void setTtParCarOf(int valor){//Inserindo o total de participantes da carona oferecida
		SharedPreferences.Editor editorBancoDeDados=usuarioLocal.edit();
		editorBancoDeDados.putInt("totalParCarOf", valor);
		editorBancoDeDados.commit();
	}

	public int getTtParCarOf(){//Retornando o total de participantes da carona oferecida
		int total=usuarioLocal.getInt("totalParCarOf", 0);
		return total;
	}

	public void clearAtualCarOf(){//Apagando todos os dados da carona oferecida
		SharedPreferences.Editor editorBancoDeDados=usuarioLocal.edit();
		for(int i=0;i<getTtParCarOf();i++){
			editorBancoDeDados.remove("partCarOf_"+i);
		}
		editorBancoDeDados.remove("totalParCarOf");
		editorBancoDeDados.remove("carOf");
		editorBancoDeDados.commit();
	}

	public void gravarUltimaCarona(int id){ //!
		SharedPreferences.Editor editorBancoDeDados=usuarioLocal.edit();
		editorBancoDeDados.putInt("ultimo_id_carona", id);
		editorBancoDeDados.commit();
	}
	public void gravarUltimaCaronaAceita(int id){
		SharedPreferences.Editor editorBancoDeDados=usuarioLocal.edit();
		editorBancoDeDados.putInt("ultimo_id_carona_aceita", id);
		editorBancoDeDados.commit();
	}
	public void setCaronaSolicitada(Carona car){//Setando a carona solicitada
		SharedPreferences.Editor editorBancoDeDados=usuarioLocal.edit();
		Gson gson = new Gson();
		String json = gson.toJson(car);
		editorBancoDeDados.putString("carSltd", json);
		editorBancoDeDados.commit();
	}

	public Carona getCaronaSolicitada(){
		Gson gson = new Gson();
		String json = usuarioLocal.getString("carSltd",null);
		Carona car= gson.fromJson(json, Carona.class);
		return car;
	}

	public void closeCaronaSolicitada(){
		SharedPreferences.Editor editorBancoDeDados=usuarioLocal.edit();
		editorBancoDeDados.remove("carSltd");
		editorBancoDeDados.commit();
	}

	public void gravarCaronasAceita(Carona car){//NÃO UTILIZADO AINDA
		SharedPreferences.Editor editorBancoDeDados=usuarioLocal.edit();
		editorBancoDeDados.putInt("id_car_"+getQuantCarAceitas(), car.getId());
		editorBancoDeDados.putString("des_car_"+getQuantCarAceitas(), car.getDestino());
		editorBancoDeDados.putString("saida_car_" + getQuantCarAceitas(), car.getHorario());
		editorBancoDeDados.commit();
	}

	public void retornaCaronaAceita(int pos){//NÃO UTILIZADO AINDA
		int id=usuarioLocal.getInt("id_car_" + pos, -1);
		String destino=usuarioLocal.getString("des_car_" + pos, null);
		String horario=usuarioLocal.getString("saida_car_"+pos, null);
	}

	public void incrementarCaronasAceitas(){//NÃO UTILIZADO AINDA
		SharedPreferences.Editor editorBancoDeDados=usuarioLocal.edit();
		editorBancoDeDados.putInt("quant_caronas_aceitas", (getQuantCarAceitas() + 1));
		editorBancoDeDados.commit();
	}
	public int getQuantCarAceitas(){
		int quant=usuarioLocal.getInt("quant_caronas_aceitas", -1);
		return quant;
	}

	public int getUltimoIdCarona(){
		int id_carona=usuarioLocal.getInt("ultimo_id_carona",0);
		return id_carona;
	}
	public int getUltimoIdCaronaAceita(){
		int id_carona=usuarioLocal.getInt("ultimo_id_carona_aceita", 0);
		return id_carona;
	}
	
	public boolean limparDados(){ //Metodo para limpar od dados.
		SharedPreferences.Editor editorBancoDeDados=usuarioLocal.edit();// editando..
		editorBancoDeDados.clear();//limpar.
		if(editorBancoDeDados.commit()){// Executar limpeza.
			return true;
		}
		return false;
	}
	
	public void setLogado(boolean logado){
		SharedPreferences.Editor editorBancoDeDados=usuarioLocal.edit();
		editorBancoDeDados.putBoolean("logado",logado);
		editorBancoDeDados.commit();
	}

	public Usuario getUsuario(){
		if(usuarioLocal.getBoolean("logado",false)==false){
			return null;
		}
		//caso contrario->
		int id=usuarioLocal.getInt("id",-1);
		String nome=usuarioLocal.getString("nome", "");
		String sobrenome=usuarioLocal.getString("sobrenome","");
		String matricula=usuarioLocal.getString("matricula","");
		String email=usuarioLocal.getString("email","");
		String telefone=usuarioLocal.getString("telefone","");
		String sexo=usuarioLocal.getString("sexo","");
		Boolean cnh=usuarioLocal.getBoolean("cnh", true);
		String foto=usuarioLocal.getString("foto", "");
		String senha=usuarioLocal.getString("senha","");
		int id_carona=usuarioLocal.getInt("id_carona",-1);
		Usuario usuarioLogado = new Usuario(nome, sobrenome, matricula, email, telefone, sexo, cnh);
		usuarioLogado.setId(id);
		usuarioLogado.setSenha(senha);
		usuarioLogado.setFoto(foto);
		usuarioLogado.setIdCaronaSolicitada(id_carona);
		return usuarioLogado;
	}
}
