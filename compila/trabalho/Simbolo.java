public class Simbolo{
    private byte token;
    private String lexema;

    Simbolo(byte token, String lexema){
        this.token = token;
        this.lexema = lexema;
    }

    public byte getToken(){
        return this.token;
    }
    
    public String getLexema(){
        return this.lexema;
    }

}