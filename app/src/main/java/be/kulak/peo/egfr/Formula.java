package be.kulak.peo.egfr;

import java.util.Arrays;
import java.util.List;

/**
 * Created by elias on 03/05/17.
 */

class Formula {

    List<String> validKeys
            = Arrays.asList("fas","fasl","fasc","ckdepi","s","mdrd","bis1","lm","cg");

    private Formula(String formulaKey, double value){
        assert isValidKey(formulaKey);
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
        return formulaKey != null && validKeys.contains(formulaKey);
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
