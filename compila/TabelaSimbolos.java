import java.util.HashMap;

class TabelaSimbolos{
    private static HashMap<String, Simbolo> token = new HashMap<String, Simbolo>();

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
    private int n = 36;

    public TabelaSimbolos(){
        token.put("const", new Simbolo(CONST, "const"));
        token.put("integer", new Simbolo(INTEGER, "integer"));
        token.put("byte", new Simbolo(BYTE, "byte"));
        token.put("string", new Simbolo(STRING, "string"));
        token.put("boolean", new Simbolo(BOOLEAN, "boolean"));
        token.put("while", new Simbolo(WHILE, "while"));
        token.put("if", new Simbolo(IF, "if"));
        token.put("else", new Simbolo(ELSE, "else"));
        token.put("and", new Simbolo(AND, "and"));
        token.put("or", new Simbolo(OR, "or"));
        token.put("not", new Simbolo(NOT, "not"));
        token.put("=", new Simbolo(ATRIBUI, "="));
        token.put("==", new Simbolo(IGUAL, "=="));
        token.put("<=", new Simbolo(MENORIGUAL, "<="));
        token.put(">=", new Simbolo(MAIORIGUAL, ">="));
        token.put(">", new Simbolo(MAIOR, ">"));
        token.put("<", new Simbolo(MENOR, "<"));
        token.put("!=", new Simbolo(DIFERENTE, "!="));
        token.put("(", new Simbolo(ABREPAREN, "("));
        token.put(")", new Simbolo(FECHAPAREN, ")"));
        token.put(",", new Simbolo(VIRGULA, ","));
        token.put(";", new Simbolo(PONTOVIRG, ";"));
        token.put("+", new Simbolo(MAIS, "+"));
        token.put("-", new Simbolo(MENOS, "-"));
        token.put("*", new Simbolo(MULT, "*"));
        token.put("/", new Simbolo(DIVI, "/"));
        token.put("begin", new Simbolo(BEGIN, "begin"));
        token.put("end", new Simbolo(END, "end"));
        token.put("then", new Simbolo(THEN, "then"));
        token.put("readln", new Simbolo(READLN, "readln"));
        token.put("main", new Simbolo(MAIN, "main"));
        token.put("write", new Simbolo(WRITE, "write"));
        token.put("writeln", new Simbolo(WRITELN, "writeln"));
        token.put("true", new Simbolo(TRUE, "true"));
        token.put("false", new Simbolo(FALSE, "false"));
        token.put("constante", new Simbolo(CONSTANTE, "constante"));
    }

    public byte pesquisa(String lexema){
        Simbolo a = token.get(lexema);
        return a.getToken();
    }

    public Simbolo inserir(String lexema, String classe, String tipo){
        Simbolo simbolo = new Simbolo((byte) n++, lexema, classe, tipo);
        token.put(lexema, simbolo);
        return token.get(lexema);
    }

    public boolean existe(String lexema){
        return token.containsKey(lexema);
    }

    public Simbolo getSimbolo(String lexema){
        Simbolo a = token.get(lexema);
        return a;
    }

}