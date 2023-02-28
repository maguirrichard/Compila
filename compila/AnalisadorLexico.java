import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

class AnalisadorLexico{
     public static String linha = "";
	public static BufferedReader leitor;
     public static RegistroLexico registroLexico;
	public static TabelaSimbolos tabela_simbolos;
	private static boolean fim = false;
	private static int num_linha = 1;
	private String fonte;

    //Inicializa o Analisador Léxico pegando o arquivo fonte e inicializando a tabela de símbolos
	public AnalisadorLexico() throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Digite o nome do arquivo: ");
		fonte = br.readLine();
		leitor = new BufferedReader(new FileReader(fonte+=".l"));
		tabela_simbolos = new TabelaSimbolos();
	}
    
    //Mesmo construtor anterior, mas com o nome do arquivo já inserido
	public AnalisadorLexico(String nome_arquivo) throws IOException{
		fonte = nome_arquivo;
		leitor = new BufferedReader(new FileReader(fonte));
		tabela_simbolos = new TabelaSimbolos();
	}

    //Retorna a tabela para que o Sintático possa usar
    public TabelaSimbolos getTabela(){
        return tabela_simbolos;
    }

    //Observa se é um símbolo possível de ser utilizado na linguagem

    public boolean simbolo(char c){
        boolean eSimbolo = false;
        if(
            c == '+' ||
            c == '-' ||
            c == '*' ||
            c == '(' ||
            c == ')' ||
            c == ';' ||
            c == ',' ||
            c == '='
        ){
            eSimbolo = true;
        }

        return eSimbolo;
    }


    //Observa se é um dígito entre 0 e 9
    public boolean digito(char c){
        boolean eDigito = false;
        if(
            (int) c >= 48 && (int) c <= 57
        ){
            eDigito = true;
        }

        return eDigito;
    }

    //Observa se é uma letra, minúscula ou maiúscula
    public boolean letra(char c){
        boolean eLetra = false;

        if(
            ((int) c >= 65 && (int) c <= 90) ||
            ((int) c >= 97 && (int) c <= 122)
        ){
            eLetra = true;
        }

        return eLetra;
    }
    
    //Observa se é um valor compatível de um hexadecimal, ou seja, 0-9 ou A-F
    public boolean hexadecimal(char c){
        boolean eHexadecimal = false;

        if(
            digito(c) || 
            (int) c >= 65 && (int) c <= 70
        ){
            eHexadecimal = true;
        }

        return eHexadecimal;
    }

    //Observa se é um caractere válido dentro das específicações da linguagem
    public boolean valido(char c){
        boolean eValido = false;

        if(
            digito(c) ||
            letra(c) ||
            simbolo(c) ||
            (int) c == 32 ||
            (int) c == 10 ||
            (int) c == 13 ||
            (int) c == 39 ||
            (int) c == 34 ||
            (int) c == 65535 ||
            c == '_' ||
            c == '.' ||
            c == '/' ||
            c == '&' ||
            c == '[' ||
            c == ']' ||
            c == '{' ||
            c == '}' ||
            c == '!' ||
            c == '?' ||
            c == ':' ||
            c == ';' ||
            c == '<' ||
            c == '>'
        ){
            eValido = true;
        }
        return eValido;
    }

    //É onde ocorre a análise léxica em si
    public RegistroLexico automato(boolean concluido, char char_atual) throws IOException{
        int estadoAtual = 0;
        int estadoFinal = 1;
        //Executa enquanto o automato não chegar no seu estado final
        while(estadoAtual != estadoFinal){
            switch(estadoAtual){
                case 0:
                    if(concluido){ // se o token não tiver concluido como valido
                        char_atual = (char) leitor.read();
                        if(!valido(char_atual)){
                            System.out.println(num_linha + ":caractere invalido." );
                            System.exit(0);
                        }

                    }

                    concluido = true;
                    if(char_atual == '/'){  //Caso de comentário
                        estadoAtual = 2;
                        linha += char_atual;
                    }
                    else if((int) char_atual == 32){ //Caso de espaço em branco
                        estadoAtual = 0;
                    }
                    else if((int) char_atual == 10){ //Caso de quebra de linha
                        estadoAtual = 0;
                        num_linha++;
                    }
                    else if((int) char_atual == 13){ //Caso de quebra de linha sem mudar de linha
                        estadoAtual = 0;
                    }
                    else if(char_atual == '0'){ // Caso de hexadecimal
                        estadoAtual = 5;
                        linha += char_atual;
                    }
                    else if(digito(char_atual)){ // Caso de digito
                        estadoAtual = 8;
                        linha+= char_atual;
                    }
                    else if(simbolo(char_atual)){ // Caso dos simbolos
                        estadoAtual = estadoFinal;
                        linha += char_atual;
                    }
                    else if(char_atual == '>' || char_atual == '<' || char_atual == '='){ // Caso dos comparadores
                        estadoAtual = 9;
                        linha += char_atual;
                    }
                    else if(char_atual == '!'){ // Caso do diferente
                        estadoAtual = 10;
                        linha += char_atual;
                    }
                    else if((int) char_atual == 39){ //Caso de iniciar string, com apóstrofo
                        estadoAtual = 11;
                        linha += char_atual;
                    }
                    else if(char_atual == '_'){ //Início de id com _
                        estadoAtual = 12;
                        linha += char_atual;
                    }
                    else if(letra(char_atual)){ //Início de id com letra
                        estadoAtual = 13;
                        linha += char_atual;
                    }
                    else if(!valido(char_atual)){ //Verifica se é um caractere válido
                        linha += char_atual;
                        System.out.println(num_linha + ":lexema nao identificado[" + linha + "].");
                        System.exit(0);
                    }
                    else { //Acabou o arquivo
                        estadoAtual = estadoFinal;
                        fim = true;
                        leitor.close();
                    }
                    break;
                case 2: // Início do comentário
                    char_atual = (char) leitor.read();
                    if(char_atual == '*'){
                        estadoAtual = 3;
                        linha = "";
                    }
                    else {
                        estadoAtual = estadoFinal;
                        concluido = false;
                    }
                    break;
                case 3: //Comentário continua, mas se parecer que vai acabar, vai pro próximo estado
                    char_atual = (char) leitor.read();
                    if(char_atual == '*'){
                        estadoAtual = 4;
                    }
                    else if ((int) char_atual == 10){ //Mudando de linha
                        estadoAtual = 3;
                        num_linha++;
                    }
                    else if((int) char_atual == 13){
                        estadoAtual = 3;
                    }
                    else if((int) char_atual == 65535){
                        System.out.println(num_linha + ":fim de arquivo nao esperado.");
					    System.exit(0);
                    }
                    else {
                        estadoAtual = 3; //Tem mais comentário
                    }
                    break;
                case 4: //Comentário acaba, mas se continuar, volta pro estado anterior
                    char_atual = (char) leitor.read();
                    if(char_atual == '*'){ //Ainda parece que vai acabar
                        estadoAtual = 4;
                    }
                    else if(char_atual == '/'){ //Comentário acabou
                        estadoAtual = 0;
                    }
                    else if((int) char_atual == 10){ //Mudando de linha
                        estadoAtual = 4;
                        num_linha++;
                    }
                    else if((int) char_atual == 13){
                        estadoAtual = 4;
                    }
                    else if(char_atual != '*' && char_atual != '/'){ //Ainda tem mais comentário
                        estadoAtual = 3;
                    }
                    else if((int) char_atual == 65535){
                        System.out.println(num_linha + ":fim de arquivo nao esperado.");
					    System.exit(0);
                    }
                    break;
                case 5: //Ver se é um número decimal ou hexadecimal
                    char_atual = (char) leitor.read();
                    if(char_atual == 'h'){ //hexadecimal
                        estadoAtual = 6;
                        linha += char_atual;
                    }
                    else if(digito(char_atual)){ //decimal
                        estadoAtual = 8;
                        linha += char_atual;
                    }
                    else {
                        estadoAtual = estadoFinal; //é só um zero
                        concluido = false;
                    }
                    break;
                case 6:
                    char_atual = (char) leitor.read();
                    if(hexadecimal(char_atual)){ //parece hexadecimal, então vamos ver se tem os dígitos possíveis
                        estadoAtual = 7;
                        linha += char_atual;
                    }
                    else {
                        System.out.println(num_linha + ":lexema nao identificado [" + linha + "].");
                        System.exit(0);
                    }
                    break;
                case 7:
                    char_atual = (char) leitor.read();
                    if(hexadecimal(char_atual)){ //ainda parece hexadecimal, vamos ver se tem o último dígito possível
                        estadoAtual = estadoFinal;
                        linha += char_atual;
                    }
                    else {
                        System.out.println(num_linha + ":lexema nao identificado [" + linha + "].");
                        System.exit(0);
                    }
                    break;
                case 8: //é um dígito
                    char_atual = (char) leitor.read();
                    if(digito(char_atual)){ //mais de um dígito
                        estadoAtual = 8;
                        linha += char_atual;
                    }
                    else if(!valido(char_atual)){
                        System.out.println(num_linha + ":lexema nao identificado [" + linha + "].");
                        System.exit(0);
                    }
                    else {
                        estadoAtual = estadoFinal; //era só um dígito
                        concluido = false;
                    }
                    break;
                case 9: //comparadores
                    char_atual = (char) leitor.read();
                    if(char_atual == '='){ //se for >=, <= ou ==
                        estadoAtual = estadoFinal;
                        linha += char_atual;
                    }
                    else {
                        estadoAtual = estadoFinal; //é só um >, < ou =
                        concluido = false;
                    }
                    break;
                case 10: //verifica se é um comparador de diferença
                    char_atual = (char) leitor.read();
                    if(char_atual == '='){ 
                        estadoAtual = estadoFinal;
                        linha += char_atual;
                    }
                    else {
                        System.out.println(num_linha + ":lexema nao identificado [" + linha + "].");
                        System.exit(0);
                    }
                    break;
                case 11: //caso da string
                    char_atual = (char) leitor.read();
                    if((int) char_atual == 39){ //terminou a string
                        estadoAtual = estadoFinal;
                        linha += char_atual;
                    }
                    else if((int) char_atual == 10 || (int) char_atual == 13){ //string não pode ter quebra de linha
                        System.out.println(num_linha + ":lexema nao identificado [" + linha + "].");
                        System.exit(0);
                    }
                    else if(!valido(char_atual)){ //string tem um caractere que não é permitido pela linguagem
                        System.out.println(num_linha + ":lexema nao identificado [" + linha + "].");
                        System.exit(0);
                    }
                    else if((int) char_atual == 65535){
                        System.out.println(num_linha + ":fim de arquivo nao esperado.");
					    System.exit(0);
                    }
                    else {
                        linha += char_atual; //continua lendo caracteres permitidos para completar a string
                    }
                    break;
                case 12: //id começado com _
                    char_atual = (char) leitor.read();
                    if(char_atual == '_'){ //continua lendo _
                        estadoAtual = 12;
                        linha += char_atual;
                    }
                    else if(letra(char_atual) || digito(char_atual)){ //lê alguma letra ou dígito e muda pro estado de começar com letra
                        estadoAtual = 13;
                        linha += char_atual;
                    }
                    else if((int) char_atual == 10){ //não pode ter quebra de linha em id com apenas _
                        System.out.println(num_linha + ":lexema nao identificado [" + linha + "].");
                        System.exit(0);
                    }
                    else if((int) char_atual == 65535){
                        System.out.println(num_linha + ":fim de arquivo nao esperado.");
					    System.exit(0);
                    }
                    else { //id contém apenas _, logo não é permitido
                        System.out.println(num_linha + ":lexema nao identificado [" + linha + "].");
                        System.exit(0);
                    }
                    break;
                case 13: //id começando com letra
                    char_atual = (char) leitor.read();
                    if(letra(char_atual) || digito(char_atual) || char_atual == '_'){ //continua lendo letra, dígito e _
                        estadoAtual = 13;
                        linha+= char_atual;
                    }
                    else if((int) char_atual == 10){ //termina de ler o id e muda de linha
                        estadoAtual = estadoFinal;
                        num_linha++;
                    }
                    else {
                        estadoAtual = estadoFinal; //terminou o id
                        concluido = false;
                    }
                    break;
                    
                default:
                    //o caractere encontrado não corresponde a nenhum possível
                    System.out.println(num_linha + "caractere invalido " + char_atual); 
                    break;
            }
        }
        if(fim){
            System.out.println("Fim do arquivo!");
            registroLexico = new RegistroLexico((byte) 65535, linha, num_linha, char_atual, concluido);
        }
        //verifica se o lexema é um id ou palavra reservada
        else if(!(digito(linha.charAt(0))) && !(((int) linha.charAt(0)) == 39)){ 
            if(!tabela_simbolos.existe(linha)){ //verifica se é um id e salva o id no registro léxico
                Simbolo id = tabela_simbolos.inserir(linha, "", "");
                registroLexico = new RegistroLexico(id.getToken(), linha, num_linha, char_atual, concluido, id);
            }
            else { //o lexema é um símbolo reservado, então salva ele no registro léxico
                
                byte token = tabela_simbolos.pesquisa(linha);
                registroLexico = new RegistroLexico(token, linha, num_linha, char_atual, concluido);
            }
        }
        else { //pode ser uma constante
            boolean constante = true;
            boolean zero = false;
            String tipo = "";
            if(digito(linha.charAt(0))){ //verifica se o primeiro caractere é um número
                if(linha.charAt(0) != '0'){ //se o primeiro caractere não for 0, vamos olhar se o resto é um número
                    for(int i = 0; i < linha.length() && constante; i++){
                        constante = digito(linha.charAt(i));
                    }

                    if(constante){
                        tipo = "decimal";
                    }
                }
                else { // se não for um número, pode ser um hexadecimal ou só 0
                    if(!
                        (
                            linha.length() == 4 &&
                            linha.charAt(0) == '0' &&
                            linha.charAt(1) == 'h' &&
                            hexadecimal(linha.charAt(2)) &&
                            hexadecimal(linha.charAt(3))
                        ) 
                    ){ // se não for um hexadecimal, então não é uma constante nem nada
                        constante = false;
                    }
                    else {
                        tipo = "hexadecimal";
                    }

                    if(linha.length() > 1){ //se começar com 0 mas tiver mais caracteres e não for um hexadecimal
                        for(int i = 0; i < linha.length() && zero; i++){ //vemos se tem mais 0s
                            zero = linha.charAt(i) == '0';
                        }

                        if(zero){
                            tipo = "decimal";
                        }
                    }
                    else { // é zero
                        zero = true;
                        tipo = "decimal";
                    }
                }
            }
            //não é uma string, então não é uma constante
            else if((int) linha.charAt(0) != 39 && ((int) (linha.length() - 1)) != 39 ) { 
                constante = false;
            }
            else {
                tipo = "string";
            }
            
            if(constante || zero){ // se for decimal, hexadecimal, string ou zero, registramos no registro léxico como constante
                registroLexico = new RegistroLexico(tabela_simbolos.pesquisa("constante"), linha, num_linha, char_atual, concluido, tipo);
            }
            
        }

        linha = "";
        estadoAtual = 0;
        return registroLexico;
        
    }
}
