import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

class AnalisadorSintatico {
    private final byte CONST = 0;
    private final byte INTEGER = 1;
    private final byte BYTE = 2;
    private final byte STRING = 3;
    private final byte BOOLEAN = 4;
    private final byte WHILE = 5;
    private final byte IF = 6;
    private final byte ELSE = 7;
    private final byte AND = 8;
    private final byte OR = 9;
    private final byte NOT = 10;
    private final byte ATRIBUI = 11;
    private final byte IGUAL = 12;
    private final byte MENORIGUAL = 13;
    private final byte MAIORIGUAL = 14;
    private final byte MAIOR = 15;
    private final byte MENOR = 16;
    private final byte DIFERENTE = 17;
    private final byte ABREPAREN = 18;
    private final byte FECHAPAREN = 19;
    private final byte VIRGULA = 20;
    private final byte PONTOVIRG = 21;
    private final byte MAIS = 22;
    private final byte MENOS = 23;
    private final byte MULT = 24;
    private final byte DIVI = 25;
    private final byte BEGIN = 26;
    private final byte END = 27;
    private final byte THEN = 28;
    private final byte READLN = 29;
    private final byte MAIN = 30;
    private final byte WRITE = 31;
    private final byte WRITELN = 32;
    private final byte TRUE = 33;
    private final byte FALSE = 34;
    private final byte CONSTANTE = 35;

    RegistroLexico registro;
    AnalisadorLexico anLex;

    //cria uma instancia do analisador lexico para fazer a analise léxica no resto do código
    public AnalisadorSintatico() throws IOException{ 
        anLex = new AnalisadorLexico();
        registro = anLex.automato(true, ' ');

        //se o lexema não for vazio, então há algo no programa, logo chamar o início da gramática
        if (registro.getLexema() != "") { 
            procS();
       //como o procS faz todo o trabalho de analisador sintatico, se ainda tiver um token após ele ser executado, então é um erro
            if (registro.getLexema() != "") { 
                System.out.println(registro.getContLinha() + ":token não esperado  [" + registro.getLexema() + "].");
                System.exit(0);
            }
        } 
        else { //se o lexema é vazio, então o arquivo está vazio e chegou no fim
            System.out.println(registro.getContLinha() + ":fim de arquivo não esperado.");
            System.exit(0);
        }
    } 

    //compara o token recebido com o token que deveria ter aparecido de acordo com a gramática
    public void casaToken(byte tokenRecebido) throws IOException{ 
        if(tokenRecebido == (byte) registro.getNumToken()){
            registro = anLex.automato(registro.getConcluido(), registro.getC());
        }
        else {
            if(registro.getNumToken() == (byte) 65535){
                System.out.println(registro.getContLinha() + ":fim de arquivo nao esperado.");
			    System.exit(0);
            }
            else { //o token recebido não é o que deveria ter aparecido
                System.out.println(registro.getContLinha() + ":token nao esperado [" + registro.getLexema() + "].");
                System.exit(0);
            }
        }
    }

    public void casaTokenBooleano() throws IOException {
        if((byte) registro.getNumToken() == TRUE || (byte) registro.getNumToken() == FALSE){
            registro = anLex.automato(registro.getConcluido(), registro.getC());
        }
        else {
            if(registro.getNumToken() == (byte) 65535){
                System.out.println(registro.getContLinha() + ":fim de arquivo nao esperado.");
			    System.exit(0);
            }
            else { //o token recebido não é o que deveria ter aparecido
                System.out.println(registro.getContLinha() + ":token nao esperado [" + registro.getLexema() + "].");
                System.exit(0);
            }
        }
    }


    public Simbolo casaTokenId(String classe, String tipo) throws IOException{ //verifica se é um id, e prossegue
        
        Simbolo id = null;
        
        if((byte) registro.getNumToken() > 35){ //> 35 porque é a partir do 36 que são armazenados os ids
            if(registro.getNumToken() == (byte) 65535){
                System.out.println(registro.getContLinha() + ":fim de arquivo nao esperado.");
			    System.exit(0);
            }
            else { //é um id, então chama o próximo caractere

                if(!anLex.getTabela().existe(registro.getLexema()) || !anLex.getTabela().getSimbolo(registro.getLexema()).getTipo().equals("")){
                    System.out.println(registro.getContLinha() + ":identificador ja declarado [" + registro.getLexema() + "]." );
                    System.exit(0);
                }
                else {
                    id = anLex.getTabela().getSimbolo(registro.getLexema());
                    id.setClasse(classe);
                    id.setTipo(tipo);
                    registro = anLex.automato(registro.getConcluido(), registro.getC());
                }

            }
        }
        else { //não é um id, como deveria ser
            System.out.println(registro.getContLinha() + ":token nao esperado [" + registro.getLexema() + "].");
            System.exit(0);
        }

        return id;
    }

    public String casaTokenId(String tipoAtual) throws IOException{ //verifica se é um id, e prossegue
        String tipo = "";
        if((byte) registro.getNumToken() > 35){ //> 35 porque é a partir do 36 que são armazenados os ids
            if(registro.getNumToken() == (byte) 65535){
                System.out.println(registro.getContLinha() + ":fim de arquivo nao esperado.");
			    System.exit(0);
            }
            else { //é um id, então chama o próximo caractere
                if(tipoAtual.equals("") && anLex.getTabela().getSimbolo(registro.getLexema()).getClasse().equals("") && !anLex.getTabela().getSimbolo(registro.getLexema()).getTipo().equals("")){
                    System.out.println(registro.getContLinha() + ":classe de identificador incompativel [" + registro.getLexema() + "].");
                    System.exit(0);
                }
                if(anLex.getTabela().existe(registro.getLexema())){
                    if(!anLex.getTabela().getSimbolo(registro.getLexema()).getTipo().equals("")){
                        tipo = anLex.getTabela().getSimbolo(registro.getLexema()).getTipo();
                        registro = anLex.automato(registro.getConcluido(), registro.getC());
                    }
                    else {
                        System.out.println(registro.getContLinha() + ":identificador nao declarado [" + registro.getLexema() + "].");
                        System.exit(0);
                    }
                }
            }
        }
        else { //não é um id, como deveria ser
            System.out.println(registro.getContLinha() + ":token nao esperado [" + registro.getLexema() + "].");
            System.exit(0);
        }

        if(tipoAtual.equals("inteiro") || tipoAtual.equals("byte")){
            if(!tipo.equals("inteiro") && !tipo.equals("byte")){
                System.out.println(registro.getContLinha() + ":token nao esperado [" + registro.getLexema() + "].");
                System.exit(0);
            }
        }
        else if(!tipoAtual.equals("") && !tipoAtual.equals(tipo)){
            System.out.println(registro.getContLinha() + ":token nao esperado [" + registro.getLexema() + "].");
            System.exit(0);
        }

        return tipo;
    }

    public String casaTokenConstante(String tipo) throws IOException{ //verifica se é uma constante e retorna qual tipo de constante é
        String qualConstante = "";
        String lexema = registro.getLexema();
        if (registro.getNumToken() == (byte) 65535) {
            System.out.println(registro.getContLinha() + ":fim de arquivo não esperado.");
            System.exit(0);
        }
        else if((int) lexema.charAt(0) == 39 && ((int) lexema.charAt(lexema.length() - 1)) == 39){ //verifica se é uma string e se for prossegue
            if(tipo.equals("string") || tipo.equals("")){
                qualConstante = "string";
            }
            else {
                System.out.println(registro.getContLinha() + ":tipos incompativeis.");
                System.exit(0);
            }
            registro = anLex.automato(registro.getConcluido(), registro.getC());
        }
        else if(lexema.charAt(0) == '0'){ //verifica se começa com 0
            boolean digito = true;
            for(int i = 0; i < lexema.length() && digito; i++){ //se começar com 0, vamos ver se o resto são números também
                digito = anLex.digito(lexema.charAt(i));
            }

            if(digito){ //se for dígito, prossegue para o próximo caractere
                if(tipo.equals("")){
                    qualConstante = "inteiro";
                }
                else {
                    if(Integer.parseInt(lexema) <= 255 || Integer.parseInt(lexema) >= 0){
                        
                        if(tipo.equals("inteiro")){
                            qualConstante = "inteiro";
                        }
                        else if(tipo.equals("byte")){
                            qualConstante = "byte";
                        }
                        else {
                            System.out.println(registro.getContLinha() + ":tipos incompativeis.");
                            System.exit(0);
                        }
                    }
                    else if(Integer.parseInt(lexema) <= 32767 || Integer.parseInt(lexema) >= -32768) {
                        if(tipo.equals("inteiro")){
                            qualConstante = "inteiro";
                        }
                        else {
                            System.out.println(registro.getContLinha() + ":tipos incompativeis.");
                            System.exit(0);
                        }
                    }
                    else {
                        System.out.println(registro.getContLinha() + ":tipos incompativeis.");
                        System.exit(0);
                    }
                }
                registro = anLex.automato(registro.getConcluido(), registro.getC());
            }
            else { //não é composto só por números, então pode ser um hexadecimal
                if(
                    lexema.length() == 4 &&
                    lexema.charAt(1) == 'h' &&
                    anLex.hexadecimal(lexema.charAt(2)) &&
                    anLex.hexadecimal(lexema.charAt(3))
                ){
                    if(tipo.equals("")){
                        qualConstante = "inteiro";
                    }
                    else {
                        if(Integer.parseInt(lexema.substring(2), 16) <= 255 || Integer.parseInt(lexema.substring(2), 16) >= 0){
                            if(tipo.equals("inteiro")){
                                qualConstante = "inteiro";
                            }
                            else if(tipo.equals("byte")){
                                qualConstante = "byte";
                            }
                            else {
                                System.out.println(registro.getContLinha() + ":tipos incompativeis.");
                                System.exit(0);
                            }
                        }
                        else if(Integer.parseInt(lexema.substring(2), 16) <= 32767 && Integer.parseInt(lexema.substring(2), 16) >= -32768) {
                            if(tipo.equals("inteiro")){
                                qualConstante = "inteiro";
                            }
                            else {
                                System.out.println(registro.getContLinha() + ":tipos incompativeis.");
                                System.exit(0);
                            }
                        }
                        else {
                            System.out.println(registro.getContLinha() + ":tipos incompativeis.");
                            System.exit(0);
                        }
                    }
                    registro = anLex.automato(registro.getConcluido(), registro.getC()); //como é um hexadecimal, prossegue
                }
            }
        }
        else {
            boolean digito = true;
            for(int i = 0; i < lexema.length() && digito; i++){ //se começar com 0, vamos ver se o resto são números também
                digito = anLex.digito(lexema.charAt(i));
            }

            if(digito){ //se for dígito, prossegue para o próximo caractere
                if(tipo.equals("")){
                    qualConstante = "inteiro";
                }
                else {
                    if(Integer.parseInt(lexema) <= 255 || Integer.parseInt(lexema) >= 0){
                        if(tipo.equals("inteiro")){
                            qualConstante = "inteiro";
                        }
                        else if(tipo.equals("byte")){
                            qualConstante = "byte";
                        }
                        else {
                            System.out.println(registro.getContLinha() + ":tipos incompativeis.");
                            System.exit(0);
                        }
                    }
                    else if(Integer.parseInt(lexema) <= 32767 || Integer.parseInt(lexema) >= -32768) {
                        if(tipo.equals("inteiro")){
                            qualConstante = "inteiro";
                        }
                        else {
                            System.out.println(registro.getContLinha() + ":tipos incompativeis.");
                            System.exit(0);
                        }
                    }
                    else {
                        System.out.println(registro.getContLinha() + ":tipos incompativeis.");
                        System.exit(0);
                    }
                }
                registro = anLex.automato(registro.getConcluido(), registro.getC());
            }
        }

        return qualConstante;
    }

    //primeiro processo -> começa com declaração, depois main, depois comandos, depois end
    public void procS() throws IOException{ 
        if(
            registro.getNumToken() == CONST ||
            registro.getNumToken() == INTEGER ||
            registro.getNumToken() == BYTE ||
            registro.getNumToken() == STRING || 
            registro.getNumToken() == BOOLEAN
        ){
            do {
                procD();
            }while(registro.getNumToken() != MAIN); //é uma declaração
            casaToken(MAIN);
            do {
                procC(); 
            }while(registro.getNumToken() != END); //é um comando
            casaToken(END);
        }
        else if(registro.getNumToken() == MAIN){
            casaToken(MAIN);
            do {
                procC(); 
            }while(registro.getNumToken() != END); //é um comando
            casaToken(END);
        }
    }

    public void procD() throws IOException{ //declarações de integer, boolean, byte, string e const, com vários ids separados por virgula
        if(registro.getNumToken() == INTEGER){
            casaToken(INTEGER);
            casaTokenId("variavel", "inteiro");
            while(registro.getNumToken() == VIRGULA){
                casaToken(VIRGULA);
                casaTokenId("variavel", "inteiro");
            }
            if(registro.getNumToken() == ATRIBUI){
                casaToken(ATRIBUI);
                procV("inteiro", "d");
                casaToken(PONTOVIRG);
            }
            else if(registro.getNumToken() == PONTOVIRG){
                casaToken(PONTOVIRG);
            }

        }
        else if(registro.getNumToken() == BOOLEAN){
            casaToken(BOOLEAN);
            casaTokenId("variavel", "booleano");
            while(registro.getNumToken() == VIRGULA){
                casaToken(VIRGULA);
                casaTokenId("variavel", "booleano");
            }
            if(registro.getNumToken() == ATRIBUI){
                casaToken(ATRIBUI);
                casaTokenBooleano();
                casaToken(PONTOVIRG);
            }
            else if(registro.getNumToken() == PONTOVIRG){
                casaToken(PONTOVIRG);
            }
        }
        else if(registro.getNumToken() == BYTE){
            casaToken(BYTE);
            casaTokenId("variavel", "byte");
            while(registro.getNumToken() == VIRGULA){
                casaToken(VIRGULA);
                casaTokenId("variavel", "byte");
            }
            if(registro.getNumToken() == ATRIBUI){
                casaToken(ATRIBUI);
                procV("byte", "d");
                casaToken(PONTOVIRG);
            }
            else if(registro.getNumToken() == PONTOVIRG){
                casaToken(PONTOVIRG);
            }
        }
        else if(registro.getNumToken() == STRING){
            casaToken(STRING);
            casaTokenId("variavel", "string");
            while(registro.getNumToken() == VIRGULA){
                casaToken(VIRGULA);
                casaTokenId("variavel", "string");
            }
            if(registro.getNumToken() == ATRIBUI){
                casaToken(ATRIBUI);
                procV("string", "d");
                casaToken(PONTOVIRG);
            }
            else if(registro.getNumToken() == PONTOVIRG){
                casaToken(PONTOVIRG);
            }
        }
        else if(registro.getNumToken() == CONST){
            casaToken(CONST);
            Simbolo id = casaTokenId("", "");
            if(registro.getNumToken() == ATRIBUI){
                casaToken(ATRIBUI);
                procV(id);
                casaToken(PONTOVIRG);
            }
            else if(registro.getNumToken() == PONTOVIRG){
                casaToken(PONTOVIRG);
            }
        }
    }

    public void procV(Simbolo id) throws IOException{ //possibilidades de atribuições à variáveis
        String tipoAtual;

        if (registro.getNumToken() == MENOS) { // começa com uma constante negativa
            casaToken(MENOS);
            tipoAtual = casaTokenConstante("");
            if(tipoAtual.equals("byte")){
                id.setTipo("inteiro");
            }
            else {
                id.setTipo(tipoAtual);
            }
        } else if (registro.getNumToken() == MAIS) { // começa com uma constante explicitamente positiva
            casaToken(MAIS);
            tipoAtual = casaTokenConstante("");
            if(tipoAtual.equals("byte")){
                id.setTipo("inteiro");
            }
            else {
                id.setTipo(tipoAtual);
            }
        } else if (registro.getNumToken() == ABREPAREN) {
            
            casaToken(ABREPAREN);
            procV(id);
            casaToken(FECHAPAREN);

        } else if (registro.getNumToken() == CONSTANTE) { // é uma constante
            tipoAtual = casaTokenConstante("");
            if(tipoAtual.equals("byte")){
                id.setTipo("inteiro");
            }
            else {
                id.setTipo(tipoAtual);
            }
        }
    }

    public void procV(String tipo, String acao) throws IOException{ //possibilidades de atribuições à variáveis
        String tipoAtual;
        
        if(tipo.equals("writeln")){
            if(registro.getNumToken() == 35){
                tipoAtual = casaTokenConstante("");
            }
            else {
                tipoAtual = casaTokenId("");
            }

            procV(tipoAtual, "a");
        }
        else if(tipo.equals("string")){
            if (acao.equals("a")) {
                if (registro.getNumToken() == CONSTANTE || registro.getNumToken() > 35) { // é uma constante
                    if (registro.getNumToken() == 35) {
                        tipoAtual = casaTokenConstante(tipo);
                    } else {
                        tipoAtual = casaTokenId(tipo);
                    }
                    while (registro.getNumToken() == MAIS) {
                        casaToken(MAIS);
                        if (registro.getNumToken() == 35) {
                            tipoAtual = casaTokenConstante(tipoAtual);
                        } else {
                            tipoAtual = casaTokenId(tipoAtual);
                        }
                    }
                }
                else if(registro.getNumToken() == MAIS){
                    tipoAtual = tipo;
                    while(registro.getNumToken() == MAIS){ 
                        casaToken(MAIS);
                        if(registro.getNumToken() == 35){
                            tipoAtual = casaTokenConstante(tipoAtual);
                        }
                        else {
                            tipoAtual = casaTokenId(tipoAtual);
                        }
                    }
                } 
                else if(registro.getNumToken() == ABREPAREN){
                    if(acao.equals("a")){
                        casaToken(ABREPAREN);
                        procA(tipo);
                        casaToken(FECHAPAREN);
                    }
                    else {
                        casaToken(ABREPAREN);
                        procV(tipo, "d");
                        casaToken(FECHAPAREN);
                    }
                }
            }
            else {
                tipoAtual = casaTokenConstante(tipo);
            }        
        }
        else {

            if(registro.getNumToken() == MENOS){ //começa com uma constante negativa
                casaToken(MENOS);
                if(acao.equals("a")){
                    if(tipo.equals("byte")){
                        procA("inteiro");
                    }
                    else {
                        procA(tipo);
                    }
                }
                else {
                    if(tipo.equals("byte")){
                        System.out.println(registro.getContLinha() + ":tipos incompativeis.");
                        System.exit(0);
                    }
                    casaTokenConstante(tipo);
                }
            }
            else if(registro.getNumToken() == MAIS){ //começa com uma constante explicitamente positiva
                casaToken(MAIS);
                if(acao.equals("a")){
                    procA(tipo);
                }
                else {
                    casaTokenConstante(tipo);
                }
            }
            else if(registro.getNumToken() == ABREPAREN){
                if(acao.equals("a")){
                    casaToken(ABREPAREN);
                    procA(tipo);
                    casaToken(FECHAPAREN);
                }
                else {
                    casaToken(ABREPAREN);
                    procV(tipo, "d");
                    casaToken(FECHAPAREN);
                }
            }
            else if(registro.getNumToken() == CONSTANTE || registro.getNumToken() > 35){ //é uma constante
                if(acao.equals("a")){
                    procA(tipo);
                }
                else {
                    casaTokenConstante(tipo);
                }
            }
        }
    }

    public void procA(String tipo) throws IOException{ //verifica se é uma constante string e sua possibilidade de concatenação, ou números e suas operações de +, -, /, *
        String tipoAtual;
        
        if(registro.getNumToken() == ABREPAREN){
            casaToken(ABREPAREN);
            procA(tipo);
            casaToken(FECHAPAREN);
        }
        else {
            if(registro.getNumToken() == 35){
                tipoAtual = casaTokenConstante(tipo);
            }
            else {
                tipoAtual = casaTokenId(tipo);
            }

            if(registro.getNumToken() == MAIS){
                casaToken(MAIS);
                procA(tipoAtual);
            }
            else if(registro.getNumToken() == MENOS){
                casaToken(MENOS);
                procA(tipoAtual);
            }
            else if(registro.getNumToken() == MULT){
                casaToken(MULT);
                procA(tipoAtual);
            }
            else if(registro.getNumToken() == DIVI){
                casaToken(DIVI);
                procA(tipoAtual);
            }
        }
        
    }

    public void procK() throws IOException{ //concatena OR e AND enquanto tiver OR e AND
        while(registro.getNumToken() == OR || registro.getNumToken() == AND){
            if(registro.getNumToken() == OR){
                casaToken(OR);
                if(registro.getNumToken() == NOT){
                    casaToken(NOT);
                    procL("not");
                }
                else if(registro.getNumToken() == ABREPAREN){
                    casaToken(ABREPAREN);
                    procL("parenteses");
                    casaToken(FECHAPAREN);
                }
                else {
                    procL("constante");
                }
            }
            else {
                casaToken(AND);
                if(registro.getNumToken() == NOT){
                    casaToken(NOT);
                    procL("not");
                }
                else if(registro.getNumToken() == ABREPAREN){
                    casaToken(ABREPAREN);
                    procL("parenteses");
                    casaToken(FECHAPAREN);
                }
                else {
                    procL("constante");
                }
            }
        }
    }

    public void procL(String tipo) throws IOException{ //expressões lógicas
        String tipoAtual;
        
        if(tipo.equals("constante")){
            if(registro.getNumToken() == 35){
                tipoAtual = casaTokenConstante("");
                procL(tipoAtual);
            }
            else if(registro.getNumToken() == TRUE || registro.getNumToken() == FALSE){
                casaTokenBooleano();
                procL("booleano");
            }
            else {
                tipoAtual = casaTokenId("");
                procL(tipoAtual);
            }
        }
        else if(tipo.equals("parenteses")){
            if(registro.getNumToken() == 35){
                tipoAtual = casaTokenConstante("");
                procL(tipoAtual);
            }
            else if(registro.getNumToken() > 35){
                tipoAtual = casaTokenId("");
                procL(tipoAtual);
            }
            else if(registro.getNumToken() == NOT){
                casaToken(NOT);
                procL("not");
            }
            else if(registro.getNumToken() == ABREPAREN){
                casaToken(ABREPAREN);
                procL("parenteses");
                casaToken(FECHAPAREN);
            }
            else {
                casaTokenBooleano();
                procL("booleano");
            }
        }
        else if(tipo.equals("not")){
            if(registro.getNumToken() == 35){
                tipoAtual = casaTokenConstante("");
                procL(tipoAtual);
            }
            else if(registro.getNumToken() > 35){
                tipoAtual = casaTokenId("");
                procL(tipoAtual);
            }
            else if(registro.getNumToken() == TRUE || registro.getNumToken() == FALSE){
                casaTokenBooleano();
                procL("booleano");
            }
            else if(registro.getNumToken() == ABREPAREN){
                casaToken(ABREPAREN);
                procL("parenteses");
                casaToken(FECHAPAREN);
            }
        }
        else if(tipo.equals("parenteses com string")){
            if(registro.getNumToken() == 35){
                casaTokenConstante("string");
            }
            else if(registro.getNumToken() > 35){
                casaTokenId("string");
            }
            else if(registro.getNumToken() == ABREPAREN){
                casaToken(ABREPAREN);
                procL("parenteses com string");
                casaToken(FECHAPAREN);
            }
        }
        else if(tipo.equals("booleano")){
            if(registro.getNumToken() == OR || registro.getNumToken() == AND){
                procK();
            }
            else if(registro.getNumToken() == IGUAL){
                if(registro.getNumToken() == 35){
                    tipoAtual = casaTokenConstante("");
                    procL(tipoAtual);
                }
                else if(registro.getNumToken() > 35){
                    tipoAtual = casaTokenId("");
                    if(tipoAtual.equals("booleano")){
                        procK();
                    }
                    else {
                        procL(tipoAtual);
                    }
                }
                else if(registro.getNumToken() == ABREPAREN){
                    casaToken(ABREPAREN);
                    procL("parenteses");
                    casaToken(FECHAPAREN);
                }
                else if(registro.getNumToken() == TRUE || registro.getNumToken() == FALSE){
                    casaTokenBooleano();
                    procL("booleano");
                }
                else if(registro.getNumToken() == NOT){
                    casaToken(NOT);
                    procL("not");
                }
            }
            else if(registro.getNumToken() == DIFERENTE){
                if(registro.getNumToken() == 35){
                    tipoAtual = casaTokenConstante("");
                    procL(tipoAtual);
                }
                else if(registro.getNumToken() > 35){
                    tipoAtual = casaTokenId("");
                    if(tipoAtual.equals("booleano")){
                        procK();
                    }
                    else {
                        procL(tipoAtual);
                    }
                }
                else if(registro.getNumToken() == TRUE || registro.getNumToken() == FALSE){
                    casaTokenBooleano();
                    procL("booleano");
                }
                else if(registro.getNumToken() == ABREPAREN){
                    casaToken(ABREPAREN);
                    procL("parenteses");
                    casaToken(FECHAPAREN);
                }
                else if(registro.getNumToken() == NOT){
                    casaToken(NOT);
                    procL("not");
                }
            }
        }
        else if(tipo.equals("string")){
            if(registro.getNumToken() == IGUAL){
                casaToken(IGUAL);
                if(registro.getNumToken() == 35){
                    tipoAtual = casaTokenConstante(tipo);
                    procK();
                }
                else if(registro.getNumToken() > 35){
                    tipoAtual = casaTokenId(tipo);
                    procK();
                }
                else if(registro.getNumToken() == ABREPAREN){
                    casaToken(ABREPAREN);
                    procL("parenteses com string");
                    casaToken(FECHAPAREN);
                }
            }
        }
        else if(tipo.equals("inteiro") || tipo.equals("byte")){
            procB(tipo);
            procK();
        }
        

    }

    public void procB(String tipo) throws IOException{
        String tipoAtual;
        if(registro.getNumToken() == MAIOR){
            casaToken(MAIOR);
            if(registro.getNumToken() == 35){
                casaTokenConstante(tipo);
            }
            else if(registro.getNumToken() > 35){
                casaTokenId(tipo);
            }
            else if(registro.getNumToken() == ABREPAREN){
                casaToken(ABREPAREN);
                procL("parenteses");
                casaToken(FECHAPAREN);
            }
            else if(registro.getNumToken() == NOT){
                casaToken(NOT);
                procL("not");
            }
        }
        else if(registro.getNumToken() == MENOR){
            casaToken(MENOR);
            if(registro.getNumToken() == 35){
                casaTokenConstante(tipo);
            }
            else if(registro.getNumToken() > 35){
                casaTokenId(tipo);
            }
            else if(registro.getNumToken() == ABREPAREN){
                casaToken(ABREPAREN);
                procL("parenteses");
                casaToken(FECHAPAREN);
            }
            else if(registro.getNumToken() == NOT){
                casaToken(NOT);
                procL("not");
            }
        }
        else if(registro.getNumToken() == MAIORIGUAL){
            casaToken(MAIORIGUAL);
            if(registro.getNumToken() == 35){
                casaTokenConstante(tipo);
            }
            else if(registro.getNumToken() > 35){
                casaTokenId(tipo);
            }
            else if(registro.getNumToken() == ABREPAREN){
                casaToken(ABREPAREN);
                procL("parenteses");
                casaToken(FECHAPAREN);
            }
            else if(registro.getNumToken() == NOT){
                casaToken(NOT);
                procL("not");
            }
        }
        else if(registro.getNumToken() == MENORIGUAL){
            casaToken(MENORIGUAL);
            if(registro.getNumToken() == 35){
                casaTokenConstante(tipo);
            }
            else if(registro.getNumToken() > 35){
                casaTokenId(tipo);
            }
            else if(registro.getNumToken() == ABREPAREN){
                casaToken(ABREPAREN);
                procL("parenteses");
                casaToken(FECHAPAREN);
            }
            else if(registro.getNumToken() == NOT){
                casaToken(NOT);
                procL("not");
            }
        }
        else if(registro.getNumToken() == DIFERENTE){
            casaToken(DIFERENTE);
            if(registro.getNumToken() == 35){
                casaTokenConstante(tipo);
            }
            else if(registro.getNumToken() > 35){
                casaTokenId(tipo);
            }
            else if(registro.getNumToken() == ABREPAREN){
                casaToken(ABREPAREN);
                procL("parenteses");
                casaToken(FECHAPAREN);
            }
            else if(registro.getNumToken() == NOT){
                casaToken(NOT);
                procL("not");
            }
        }
        else if(registro.getNumToken() == IGUAL){
            casaToken(IGUAL);
            if(registro.getNumToken() == 35){
                casaTokenConstante(tipo);
            }
            else if(registro.getNumToken() > 35){
                casaTokenId(tipo);
            }
            else if(registro.getNumToken() == ABREPAREN){
                casaToken(ABREPAREN);
                procL("parenteses");
                casaToken(FECHAPAREN);
            }
            else if(registro.getNumToken() == NOT){
                casaToken(NOT);
                procL("not");
            }
        }
    }

    public void procC() throws IOException{ //é o processo responsável pelos comandos da linguagem
        if(registro.getNumToken() == WHILE){ //comando while
            casaToken(WHILE);
            procW();
        }
        else if(registro.getNumToken() == IF){ //comando if
            casaToken(IF);
            procI();
        }
        else if(registro.getNumToken() == READLN){ //comando readln
            casaToken(READLN);
            casaToken(ABREPAREN);
            casaTokenId("");
            casaToken(FECHAPAREN);
            casaToken(PONTOVIRG);
        }
        else if(registro.getNumToken() == WRITE){ //comando write
            casaToken(WRITE);
            casaToken(ABREPAREN);
            procV("writeln", "a");
            while(registro.getNumToken() == VIRGULA){
                casaToken(VIRGULA);
                procV("writeln", "a");
            }
            casaToken(FECHAPAREN);
            casaToken(PONTOVIRG);
        }
        else if(registro.getNumToken() == WRITELN){ //comando writeln
            casaToken(WRITELN);
            casaToken(ABREPAREN);
            procV("writeln", "a");
            while(registro.getNumToken() == VIRGULA){
                casaToken(VIRGULA);
                procV("writeln", "a");
            }
            casaToken(FECHAPAREN);
            casaToken(PONTOVIRG);
        }
        else if(registro.getNumToken() == PONTOVIRG){
            casaToken(PONTOVIRG);
        }
        else if(registro.getNumToken() != PONTOVIRG){ //não é comando nulo, logo pode ser uma atribuição
            
            String tipo = casaTokenId("");
            casaToken(ATRIBUI);
            if(tipo.equals("booleano")){
                if(registro.getNumToken() == NOT){
                    casaToken(NOT);
                    procL("not");
                }
                else if(registro.getNumToken() == ABREPAREN){
                    casaToken(ABREPAREN);
                    procL("parenteses");
                    casaToken(FECHAPAREN);
                }
                else {
                    procL("constante");
                }
            }
            else {
                procV(tipo, "a");
            }
            casaToken(PONTOVIRG);
        }
    }

    public void procW() throws IOException{ //comando while
        casaToken(ABREPAREN);
        procL("parenteses");
        casaToken(FECHAPAREN);
        if(registro.getNumToken() == BEGIN){ //inicio de bloco de while
            casaToken(BEGIN);
            while(registro.getNumToken() != END){
                procC();
            }
            casaToken(END);
        }
        else {
            procC();
        }
    }

    public void procI() throws IOException{ //comando if
        casaToken(ABREPAREN);
        procL("parenteses");
        casaToken(FECHAPAREN);
        casaToken(THEN);
        if(registro.getNumToken() == BEGIN){ //inicio de bloco de if
            casaToken(BEGIN);
            while(registro.getNumToken() != END){
                procC();
            }
            casaToken(END);
            if(registro.getNumToken() == ELSE){ //tem else depois do bloco de if
                casaToken(ELSE);
                if(registro.getNumToken() == BEGIN){ //inicio de bloco de else
                    casaToken(BEGIN);
                    while(registro.getNumToken() != END){
                        procC();
                    }
                    casaToken(END);
                }
                else { //não tem bloco de else
                    procC();
                }
            }
        }
        else { //não tem bloco de if
            procC(); 
            if(registro.getNumToken() == ELSE){ //tem else
                casaToken(ELSE);
                if(registro.getNumToken() == BEGIN){ //inicio de bloco de else
                    casaToken(BEGIN);
                    while(registro.getNumToken() != END){
                        procC();
                    }
                    casaToken(END);
                }
                else { //não tem bloco de else
                    procC();
                }
            }
        }
    }

}
