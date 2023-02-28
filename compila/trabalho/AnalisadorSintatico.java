import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class AnalisadorSintatico {
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

    public AnalisadorSintatico() throws IOException{ //cria uma instancia do analisador lexico para fazer a analise léxica no resto do código
        anLex = new AnalisadorLexico();
        registro = anLex.automato(true, ' ');

        if (registro.getLexema() != "") { //se o lexema não for vazio, então há algo no programa, logo chamar o início da gramática
            procS();
            if (registro.getLexema() != "") { //como o procS faz todo o trabalho de analisador sintatico, se ainda tiver um token após ele ser executado, então é um erro
                System.out.println(registro.getContLinha() + ":token não esperado  [" + registro.getLexema() + "].");
                System.exit(0);
            }
        } 
        else { // se o lexema é vazio, então o arquivo está vazio e chegou no fim
            System.out.println(registro.getContLinha() + ":fim de arquivo não esperado.");
            System.exit(0);
        }
    } 

    public void casaToken(byte tokenRecebido) throws IOException{ //compara o token recebido com o token que deveria ter aparecido de acordo com a gramática
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

    public void casaTokenId() throws IOException{ //verifica se é um id, e prossegue
        if((byte) registro.getNumToken() > 35){ //> 35 porque é a partir do 36 que são armazenados os ids
            if(registro.getNumToken() == (byte) 65535){
                System.out.println(registro.getContLinha() + ":fim de arquivo nao esperado.");
			    System.exit(0);
            }
            else { //é um id, então chama o próximo caractere
                registro = anLex.automato(registro.getConcluido(), registro.getC());
            }
        }
        else { //não é um id, como deveria ser
            System.out.println(registro.getContLinha() + ":token nao esperado [" + registro.getLexema() + "].");
            System.exit(0);
        }
    }

    public String casaTokenConstante() throws IOException{ //verifica se é uma constante e retorna qual tipo de constante é
        String qualConstante = "";
        String lexema = registro.getLexema();
        if (registro.getNumToken() == (byte) 65535) {
            System.out.println(registro.getContLinha() + ":fim de arquivo não esperado.");
            System.exit(0);
        }
        else if((int) lexema.charAt(0) == 39 && ((int) lexema.charAt(lexema.length() - 1)) == 39){ //verifica se é uma string e se for prossegue
            registro = anLex.automato(registro.getConcluido(), registro.getC());
            qualConstante = "string";
        }
        else if(lexema.charAt(0) == '0'){ //verifica se começa com 0
            boolean digito = true;
            for(int i = 0; i < lexema.length() && digito; i++){ //se começar com 0, vamos ver se o resto são números também
                digito = anLex.digito(lexema.charAt(i));
            }

            if(digito){ //se for dígito, prossegue para o próximo caractere
                registro = anLex.automato(registro.getConcluido(), registro.getC());
                qualConstante = "digito";
            }
            else { //não é composto só por números, então pode ser um hexadecimal
                if(
                    lexema.length() == 4 &&
                    lexema.charAt(1) == 'h' &&
                    anLex.hexadecimal(lexema.charAt(2)) &&
                    anLex.hexadecimal(lexema.charAt(3))
                ){
                    registro = anLex.automato(registro.getConcluido(), registro.getC()); //como é um hexadecimal, prossegue
                    qualConstante = "hexadecimal";
                }
            }
        }

        return qualConstante;
    }

    public void procS() throws IOException{ // primeiro processo -> começa com declaração, depois main, depois comandos, depois end
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
            casaTokenId();
            while(registro.getNumToken() == VIRGULA){
                casaToken(VIRGULA);
                casaTokenId();
            }
            if(registro.getNumToken() == ATRIBUI){
                casaToken(ATRIBUI);
                procV();
                casaToken(PONTOVIRG);
            }

        }
        else if(registro.getNumToken() == BOOLEAN){
            casaToken(BOOLEAN);
            casaTokenId();
            while(registro.getNumToken() == VIRGULA){
                casaToken(VIRGULA);
                casaTokenId();
            }
            if(registro.getNumToken() == ATRIBUI){
                casaToken(ATRIBUI);
                procV();
                casaToken(PONTOVIRG);
            }
        }
        else if(registro.getNumToken() == BYTE){
            casaToken(BYTE);
            casaTokenId();
            while(registro.getNumToken() == VIRGULA){
                casaToken(VIRGULA);
                casaTokenId();
            }
            if(registro.getNumToken() == ATRIBUI){
                casaToken(ATRIBUI);
                procV();
                casaToken(PONTOVIRG);
            }
        }
        else if(registro.getNumToken() == STRING){
            casaToken(STRING);
            casaTokenId();
            while(registro.getNumToken() == VIRGULA){
                casaToken(VIRGULA);
                casaTokenId();
            }
            if(registro.getNumToken() == ATRIBUI){
                casaToken(ATRIBUI);
                procV();
                casaToken(PONTOVIRG);
            }
        }
        else if(registro.getNumToken() == CONST){
            casaToken(CONST);
            casaTokenId();
            if(registro.getNumToken() == ATRIBUI){
                casaToken(IGUAL);
                procV();
                casaToken(PONTOVIRG);
            }
        }
    }

    public void procV() throws IOException{ //possibilidades de atribuições à variáveis
        if(registro.getNumToken() == MENOS){ //começa com uma constante negativa
            casaToken(MENOS);
            casaTokenConstante();
        }
        else if(registro.getNumToken() == MAIS){ //começa com uma constante explicitamente positiva
            casaToken(MAIS);
            casaTokenConstante();
        }
        else if(registro.getNumToken() == CONSTANTE){ //é uma constante
            procA();
        }
        else { //pode começar com not, então é uma expressão lógica
            procL();
        }
    }

    public void procA() throws IOException{ //verifica se é uma constante string e sua possibilidade de concatenação, ou números e suas operações de +, -, /, *
        String constante = casaTokenConstante();
        if(constante.equals("string")){ //se for uma constante string, então só pode fazer concatenação
            if(registro.getNumToken() == MAIS){
                casaToken(MAIS);
                casaTokenConstante();
                while(registro.getNumToken() == MAIS){ 
                    casaToken(MAIS);
                    casaTokenConstante();
                }
            }
        }
        else if(registro.getNumToken() == MAIS){ // operação de soma
            casaToken(MAIS);
            procA();
        }
        else if(registro.getNumToken() == MENOS){ //operação de subtração
            casaToken(MENOS);
            procA();
        }
        else if(registro.getNumToken() == MULT){ //operação de multiplicação
            casaToken(MULT);
            procA();
        }
        else if(registro.getNumToken() == DIVI){ //operação de divisão
            casaToken(DIVI);
            procA();
        }
        else { //se não é string nem sinal de operação, então é uma expressão lógica
            procL();
        }
    }

    public void procK() throws IOException{ //concatena OR e AND enquanto tiver OR e AND
        while(registro.getNumToken() == OR || registro.getNumToken() == AND){
            if(registro.getNumToken() == OR){
                casaToken(OR);
                procL();
            }
            else {
                casaToken(AND);
                procL();
            }
        }
    }

    public void procL() throws IOException{ //expressões lógicas
        if(registro.getNumToken() == NOT){ //começa com negação
            casaToken(NOT);
            procL();
        }
        else if(registro.getNumToken() == TRUE){ //é só um true
            casaToken(TRUE);
            procK();
        }
        else if(registro.getNumToken() == FALSE){// é só um false
            casaToken(FALSE);
            procK();
        }
        else if(registro.getNumToken() == CONSTANTE){ //é uma constante, logo é uma comparação entre constantes
            casaTokenConstante();
            if(registro.getNumToken() == MAIOR){
                casaToken(MAIOR);
                if(registro.getNumToken() == CONSTANTE){
                    casaTokenConstante();
                    if(registro.getNumToken() == OR){
                        procK();
                    }
                    else if(registro.getNumToken() == AND){
                        procK();
                    }
                    else {
                        procL();
                    }
                }
                else {
                    procL();
                }
            }
            else if(registro.getNumToken() == MENOR){
                casaToken(MENOR);
                if(registro.getNumToken() == CONSTANTE){
                    casaTokenConstante();
                    if(registro.getNumToken() == OR){
                        procK();
                    }
                    else if(registro.getNumToken() == AND){
                        procK();
                    }
                    else {
                        procL();
                    }
                }
                else {
                    procL();
                }
            }
            else if(registro.getNumToken() == IGUAL){
                casaToken(IGUAL);
                if(registro.getNumToken() == CONSTANTE){
                    casaTokenConstante();
                    if(registro.getNumToken() == OR){
                        procK();
                    }
                    else if(registro.getNumToken() == AND){
                        procK();
                    }
                    else {
                        procL();
                    }
                }
                else {
                    procL();
                }
            }
            else if(registro.getNumToken() == DIFERENTE){
                casaToken(DIFERENTE);
                if(registro.getNumToken() == CONSTANTE){
                    casaTokenConstante();
                    if(registro.getNumToken() == OR){
                        procK();
                    }
                    else if(registro.getNumToken() == AND){
                        procK();
                    }
                    else {
                        procL();
                    }
                }
                else {
                    procL();
                }
            }
            else if(registro.getNumToken() == MAIORIGUAL){
                casaToken(MAIORIGUAL);
                if(registro.getNumToken() == CONSTANTE){
                    casaTokenConstante();
                    if(registro.getNumToken() == OR){
                        procK();
                    }
                    else if(registro.getNumToken() == AND){
                        procK();
                    }
                    else {
                        procL();
                    }
                }
                else {
                    procL();
                }
            }
            else if(registro.getNumToken() == MENORIGUAL){
                casaToken(MENORIGUAL);
                if(registro.getNumToken() == CONSTANTE){
                    casaTokenConstante();
                    if(registro.getNumToken() == OR){
                        procK();
                    }
                    else if(registro.getNumToken() == AND){
                        procK();
                    }
                    else {
                        procL();
                    }
                }
                else {
                    procL();
                }
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
            casaTokenId();
            casaToken(FECHAPAREN);
            casaToken(PONTOVIRG);
        }
        else if(registro.getNumToken() == WRITE){ //comando write
            casaToken(WRITE);
            casaToken(ABREPAREN);
            casaTokenConstante();
            while(registro.getNumToken() == VIRGULA){
                casaToken(VIRGULA);
                casaTokenConstante();
            }
            casaToken(FECHAPAREN);
            casaToken(PONTOVIRG);
        }
        else if(registro.getNumToken() == WRITELN){ //comando write
            casaToken(WRITELN);
            casaToken(ABREPAREN);
            casaTokenConstante();
            while(registro.getNumToken() == VIRGULA){
                casaToken(VIRGULA);
                casaTokenConstante();
            }
            casaToken(FECHAPAREN);
            casaToken(PONTOVIRG);
        }
        else if(registro.getNumToken() == PONTOVIRG){
            casaToken(PONTOVIRG);
        }
        else if(registro.getNumToken() != PONTOVIRG){ //não é comando nulo, logo pode ser uma atribuição
            casaTokenId();
            casaToken(ATRIBUI);
            procV();
            casaToken(PONTOVIRG);
        }
    }

    public void procW() throws IOException{ //comando while
        casaToken(ABREPAREN);
        procL();
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
        procL();
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