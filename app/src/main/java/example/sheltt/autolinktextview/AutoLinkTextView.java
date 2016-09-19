package example.sheltt.autolinktextview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AutoLinkTextView
 *
 * Created by tanase on 9/19/16.
 */
public class AutoLinkTextView extends TextView {

    private String regex = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

    private OnURLClickListener listener;

    // Default constructor when inflating from XML file
    public AutoLinkTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    // Default constructor override
    public AutoLinkTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnURLClickListener(OnURLClickListener onURLClickListener){
        this.listener = onURLClickListener;
    }

    public String getRegex(){
        return regex;
    }

    public void setRegex(String regex){
        this.regex = regex;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        String str = text.toString();

        final String regex = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

        SpannableString spannableString = new SpannableString(str);
        Matcher urlMatcher = Pattern.compile(regex).matcher(str);
        while (urlMatcher.find()) {
            String url = urlMatcher.group();
            int start = urlMatcher.start();
            int end = urlMatcher.end();
            spannableString.setSpan(new GoToURLSpan(url), start, end, 0);
        }

        super.setText(spannableString, type);
        super.setMovementMethod(new LinkMovementMethod());
    }

    /**
     * TextViewのテキスト内のリンクを表現するクラス.
     */
    private class GoToURLSpan extends ClickableSpan {

        @NonNull
        private final String url;

        public GoToURLSpan(@NonNull String url) {
            this.url = url;
        }

        public void onClick(View view) {

            if(listener != null){
                listener.onClick(view, this);
                return;
            }

            try {
                Uri webPage = Uri.parse(url); //http:<URL> or https:<URL>
                Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
                view.getContext().startActivity(intent);
            } catch (Exception e) {
                Log.w("AutoLinkTextView", "invalid url:" + url);
            }
        }
    }

}
