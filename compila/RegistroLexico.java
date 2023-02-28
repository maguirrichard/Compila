class RegistroLexico{
    private int numToken;
    private String lexema;
    private int cont_linha;
    private Simbolo endereco;
    private String tipo;
    private char c;
    private boolean concluido;

    public RegistroLexico(byte token, String lexema, int cont_linha, char c, boolean concluido){
        this.numToken = token;
        this.lexema = lexema;
        this.cont_linha = cont_linha;
        this.c = c;
        this.concluido = concluido;
    }

    public RegistroLexico(byte token, String lexema, int cont_linha, char c, boolean concluido, Simbolo endereco){
        this.numToken = token;
        this.lexema = lexema;
        this.cont_linha = cont_linha;
        this.c = c;
        this.concluido = concluido;
        this.endereco = endereco;
    }

    public RegistroLexico(byte token, String lexema, int cont_linha, char c, boolean concluido, String tipo){
        this.numToken = token;
        this.lexema = lexema;
        this.cont_linha = cont_linha;
        this.c = c;
        this.concluido = concluido;
        this.tipo = tipo;
    }

    public int getNumToken(){
        return this.numToken;
    }
    
    public String getLexema(){
        return this.lexema;
    }
    
    public int getContLinha(){
        return this.cont_linha;
    }

    public char getC(){
        return this.c;
    }

    public boolean getConcluido(){
        return this.concluido;
    }

    public Simbolo getEndereco(){
        return this.endereco;
    }

}