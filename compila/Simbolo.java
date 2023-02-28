class Simbolo{
    private byte token;
    private String lexema;
    private String classe;
    private String tipo;

    Simbolo(byte token, String lexema){
        this.token = token;
        this.lexema = lexema;
    }

    Simbolo(byte token, String lexema, String classe, String tipo){
        this.token = token;
        this.lexema = lexema;
        this.classe = classe;
        this.tipo = tipo;
    }

    public byte getToken(){
        return this.token;
    }
    
    public String getLexema(){
        return this.lexema;
    }

    public String getClasse(){
        return this.classe;
    }

    public String getTipo(){
        return this.tipo;
    }

    public void setClasse(String classe){
        this.classe = classe;
    }

    public void setTipo(String tipo){
        this.tipo = tipo;
    }

}