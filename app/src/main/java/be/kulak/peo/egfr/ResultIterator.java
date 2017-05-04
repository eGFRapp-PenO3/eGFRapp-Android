package be.kulak.peo.egfr;

import java.util.Iterator;

/**
 * Created by elias on 04/05/17.
 */

public class ResultIterator<Formula> implements Iterator<be.kulak.peo.egfr.Formula> {

    Result result;
    int position;
    int nextPos;
    String[] enabledFormulae;

    public ResultIterator(Result result, String[] enabledFormulae) {
        this.enabledFormulae = enabledFormulae;
        this.result = result;
    }

    @Override
    public boolean hasNext() {
        int nextPos = position + 1;
        while(nextPos < enabledFormulae.length){
            if(result.getElementWithKey(enabledFormulae[nextPos]).getValue()!=-1){
                this.nextPos = nextPos;
                return true;
            }
        }
        return false;
    }

    @Override
    public be.kulak.peo.egfr.Formula next() {
        return result.getElementWithKey(enabledFormulae[nextPos]);
    }

    @Override
    public void remove() throws UnsupportedOperationException{
        throw new UnsupportedOperationException();
    }
}
