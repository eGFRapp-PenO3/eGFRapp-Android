package be.kulak.peo.egfr;

/**
 * Created by elias on 02/12/16.
 */

public class Result {
    public String formula;
    public String hint;
    public String value;
    public String unit;

    public Result(String key, double result){
        String[] formulaHint = MainActivity.ResultStrings.get(key).split(":");
        this.formula = formulaHint[0];
        this.hint = (formulaHint.length > 1) ? formulaHint[1] : "";
        this.value = String.format("%.1f", result);
        this.unit = (key.contains("CG") ? "mL/min" : "mL/min/1.73 m²");
    }
}
