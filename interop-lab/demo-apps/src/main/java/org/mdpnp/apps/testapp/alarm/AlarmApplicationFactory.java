package org.mdpnp.apps.testapp.alarm;

import java.awt.Component;
import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import javax.swing.JPanel;

import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.apps.testapp.export.DataCollector;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.context.ApplicationContext;

import com.rti.dds.subscription.Subscriber;

public class AlarmApplicationFactory implements IceApplicationProvider {
    private final IceApplicationProvider.AppType AlarmApplication =
            new IceApplicationProvider.AppType("Alarm History", "NOALARM", (URL)AlarmApplicationFactory.class.getResource("alarm.png"), 0.75);

    @Override
    public IceApplicationProvider.AppType getAppType() {
        return AlarmApplication;

    }

    @Override
    public IceApplicationProvider.IceApp create(ApplicationContext parentContext) throws IOException {

        final Subscriber subscriber = (Subscriber)parentContext.getBean("subscriber");

        final EventLoop eventLoop = (EventLoop)parentContext.getBean("eventLoop");
        
        final AlarmHistoryModel model = new AlarmHistoryModel(ice.PatientAlertTopic.VALUE, ice.TechnicalAlertTopic.VALUE);

        FXMLLoader loader = new FXMLLoader(AlarmApplication.class.getResource("AlarmApplication.fxml"));
        
        final Parent ui = loader.load();
        
        final AlarmApplication controller = ((AlarmApplication)loader.getController());
        
        controller.setModel(model.getContents());

        return new IceApplicationProvider.IceApp() {

            @Override
            public IceApplicationProvider.AppType getDescriptor() {
                return AlarmApplication;
            }

            @Override
            public Parent getUI() {
                return ui;
            }

            @Override
            public void activate(ApplicationContext context) {
                model.start(subscriber, eventLoop);
            }

            @Override
            public void stop() {
                model.stop();
            }

            @Override
            public void destroy() throws Exception {
                controller.stop();
            }
        };
    }


    @SuppressWarnings("serial")
    public static abstract class PersisterUI extends JPanel implements DataCollector.DataSampleEventListener  {

        public abstract String getName();

        public abstract void stop() throws Exception;

        public abstract boolean start() throws Exception;
    }

}
