package be.kulak.peo.egfr;

/**
 * Created by elias on 03/05/17.
 */

enum Formula {
    FAS("fas"), FASL("fasl"), FASC("fasc"),
    CKDEPI("ckdepi"), S("s"), MDRD("mdrd"),
    BIS1("bis1"), LM("lm"), CG("cg");

    private Formula(String formulaKey, double value){
        setKey(formulaKey);
        setHint(formulaKey);
        setDescription(formulaKey);
        setValue(value);
    }

    private static String formulaKey;

    private void setKey(String formulaKey){
        this.formulaKey = formulaKey;
    }

    public String getKey(String formulaKey){
        return this.formulaKey;
    }

    public boolean isValidKey(String formulaKey){
        return formulaKey != null && formulaKey.matches("[a-z]");
    }

    private static String hint;

    public void setHint(String formulaKey){
        this.hint = MainActivity.ResultStrings.get(formulaKey)[0];
    }

    public String getHint(){
        return this.hint;
    }

    private static String desc;

    public void setDescription(String formulaKey){
        try {
            this.desc = MainActivity.ResultStrings.get(formulaKey)[1];
        } catch (IndexOutOfBoundsException e){
            this.desc = "";
        }
    }

    public String getDescription(){
        return this.desc;
    }

    private static double value;

    public void setValue(double value){
        this.value = value;
    }
}
