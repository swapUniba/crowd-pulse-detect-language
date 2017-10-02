package com.github.frapontillo.pulse.crowd.detectlanguage;

import com.github.frapontillo.pulse.crowd.data.entity.Message;
import com.github.frapontillo.pulse.rx.PulseSubscriber;
import com.github.frapontillo.pulse.spi.IPlugin;
import com.github.frapontillo.pulse.util.StringUtil;
import rx.Observable;
import rx.Subscriber;

/**
 * Rx {@link rx.Observable.Operator} for categorizing the tags inside a {@link Message}.
 *
 * @author Francesco Pontillo
 */
public abstract class ILanguageDetectorOperator implements Observable.Operator<Message, Message> {
    private IPlugin plugin;

    public ILanguageDetectorOperator(IPlugin plugin) {
        this.plugin = plugin;
    }

    @Override public Subscriber<? super Message> call(Subscriber<? super Message> subscriber) {
        return new PulseSubscriber<Message>(subscriber) {
            @Override public void onNext(Message message) {
                plugin.reportElementAsStarted(message.getId());
                if (StringUtil.isNullOrEmpty(message.getLanguage())) {
                    message.setLanguage(getLanguage(message));
                }
                plugin.reportElementAsEnded(message.getId());
                subscriber.onNext(message);
            }

            @Override public void onCompleted() {
                plugin.reportPluginAsCompleted();
                super.onCompleted();
            }

            @Override public void onError(Throwable e) {
                plugin.reportPluginAsErrored();
                super.onError(e);
            }
        };
    }

    /**
     * Actual language retrieval for the given {@link Message}.
     *
     * @param message The {@link Message} to process.
     *
     * @return A ISO-compliant {@link String} representing the most probable language for the {@link
     * Message}.
     */
    public abstract String getLanguage(Message message);

}
