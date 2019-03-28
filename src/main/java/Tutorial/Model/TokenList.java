package Tutorial.Model;

import java.util.Arrays;
import java.util.List;

public class TokenList {
    private List tokenList;

    public TokenList(){
        this.tokenList = Arrays.asList("abc", "def", "efg", "default");
    }

    public List getTokenList() {
        return this.tokenList;
    }
}
