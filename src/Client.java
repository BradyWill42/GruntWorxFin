public class Client {
    private String fullInput;
    private String name;
    private boolean expedite;
    private String tradeType;
    private boolean broken = false;
    private boolean equals;
    
    public Client(String fullInput){
        this.fullInput = fullInput;
    }

    public Client(String name, boolean expedite, String tradeType){
        this.name = name;
        this.expedite = expedite;
        this.tradeType = tradeType;
    }

    public Client(){    
        name = "";
        expedite = false;
        tradeType = "n";
    }

    /**    
     * Returns a clients name.
     * @return String name of client
     */
    public String getName(){
        return name;
    }

    /**    
     * Returns if a client is expedited.
     * @return boolean of client's expedited status
     */
    public boolean isExpedited(){
        return expedite;
    }

    /**    
     * Returns a clients trade type.
     * @return String letter of trade type
     */
    public String getTradeType(){
        return tradeType;
    }

    /**    
     * Returns if the client is broken.
     * @return boolean of client's broken status
     */
    public boolean isBroken(){
        return broken;
    }

    /**    
     * Gets whether the imput for the client has an equals sign.
     * @return boolean of whether "=" sign exists in clients full input
     */
    public boolean isEquals(){
        return equals;
    }

    /**    
     * Sets a clients name.
     * @return void
     */
    public void setName(String name){
        this.name = name;
    }

    /**    
     * Sets whether the client is expedited or not. If expedited, the expedite boolean will be true.
     * @return void
     */
    public void setExpedite(boolean expedite){
        this.expedite = expedite;
    }

    /**    
     * Sets trade type for the client.
     * @return void
     */
    public void setTradeType(String tradeType){
        this.tradeType = tradeType;
    }

    /**    
     * Sets a clients broken status. If broken, the broken boolean will be true
     * @return void
     */
    public void setBroken(boolean broken){
        this.broken = broken;
    }

    
    /**
     * Using input from InputGrunt.txt file, assigns codes to each client in the input file.     
     * "=" sign denotes start of code sequence.     
     * First letter after denotes expedited(e) or not(n).     
     * Second letter denotes add-on trades(a), summary trades(s), or no trades(n).     
     * @return void
     */
    public void readFullInput(){
        String codes = "";
        String tempName = "";
        for(int i = 0; i < fullInput.length() + 1; i++){
            if(fullInput.substring(i, i+1).equals("=")){
                codes = fullInput.substring(i + 1);
                equals = true;
                if(codes.length() == 2){
                    if(codes.substring(0,1).equals("e")){
                        expedite = true;
                    } else {
                        expedite = false;
                    }

                    if(codes.substring(1,2).equals("a")){
                        tradeType = "a";
                    } else if(codes.substring(1,2).equals("s")){
                        tradeType = "s";
                    } else {
                        tradeType = "n";
                    }

                } else {
                    broken = true;
                }
                break;
            }
            tempName += fullInput.substring(i, i+1);

        }
        name = tempName;
    }

    /**    
     * @return String name of client
     */
    public String toString(){
        return name;
    }


    
}
