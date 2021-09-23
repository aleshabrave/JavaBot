package handlers;

import java.util.Locale;
import java.util.function.Function;
import java.util.regex.Pattern;

public class MessageHandler implements Handler<Context, Integer> {
    private final Function<Context, Integer> action;
    private String pattern;

    public MessageHandler(String pattern, Function<Context, Integer> action) {
        this.pattern = pattern;
        this.action = action;
    }

    public Boolean is(String str) {
        var s = str.toLowerCase(Locale.ROOT);
        return pattern.toLowerCase(Locale.ROOT).equals(s);
        //return Pattern.matches(pattern, str);
    }

    public Integer apply(Context context) {
        return action.apply(context);
    }

    @Override
    public void execute(Context context) { }
}
