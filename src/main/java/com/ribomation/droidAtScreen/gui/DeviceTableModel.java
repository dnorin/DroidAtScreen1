package com.ribomation.droidAtScreen.gui;

import com.ribomation.droidAtScreen.Language;
import com.ribomation.droidAtScreen.Settings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

/**
 * Table model for "known" devices.
 * <p/>
 * User: Jens Created: 2012-03-26, 22:01
 */
public class DeviceTableModel extends AbstractTableModel {

    private static final int NAME = 0;
    private static final int TYPE = 1;
    private static final int SERNO = 2;
    private static final int STATE = 3;
    private static final int SHOW = 4;

    private final List<DeviceFrame> devices = new ArrayList<>();
    private final Logger log;
    Preferences applicationPreferences;
    Language language;

    public DeviceTableModel() {
        log = Logger.getLogger(this.getClass());
        this.applicationPreferences = Preferences.userNodeForPackage(Settings.class);
        loadLanguage();
    }

    /**
     * Number of columns of the device table.
     *
     * <pre>
     *   Name  Type  Ser.No  State  Show/Hide
     * </pre>
     *
     * @return
     */
    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public int getRowCount() {
        return devices.size();
    }

    @Override
    public String getColumnName(int col) {
        switch (col) {
            case NAME:
                return getString("name");
            case TYPE:
                return getString("type");
            case SERNO:
                return getString("serial_no");
            case STATE:
                return getString("state");
            case SHOW:
                return getString("visible");
        }
        return "";
    }

    @Override
    public Class<?> getColumnClass(int col) {
        switch (col) {
            case NAME:
                return String.class;
            case TYPE:
                return String.class;
            case SERNO:
                return String.class;
            case STATE:
                return String.class;
            case SHOW:
                return Boolean.class;
        }
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int col) {
        DeviceFrame dev = devices.get(row);
        if (dev != null) {
            switch (col) {
                case NAME:
                    return dev.getDevice().getName();
                case TYPE:
                    return dev.getDevice().isEmulator() ? "EMU" : "DEV";
                case SERNO:
                    return dev.getDevice().getDevice().getSerialNumber();
                case STATE:
                    return dev.getDevice().getState();
                case SHOW:
                    return dev.isVisible();
            }
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return col == SHOW;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
//		log.debug(String.format("setValueAt [%d,%d] %s", row, col, value));

        DeviceFrame dev = devices.get(row);
        if (dev == null) {
            return;
        }

        if (col == SHOW) {
            boolean newValue = (Boolean) value;
            if (!dev.isVisible() && newValue) { //show
                dev.pack();
                dev.setVisible(true);
            }
            if (dev.isVisible() && !newValue) { //hide
                dev.stopRetriever();
                dev.setVisible(false);
            }
        }

        fireTableCellUpdated(row, col);
    }

    public synchronized void add(DeviceFrame dev) {
        devices.add(dev);
        Collections.sort(devices);
        fireTableDataChanged();
    }

    public synchronized void remove(DeviceFrame dev) {
        devices.remove(dev);
        fireTableDataChanged();
    }

    public synchronized void removeAll() {
        devices.clear();
        fireTableDataChanged();
    }

    public void refresh() {
        fireTableDataChanged();
    }

    public synchronized List<DeviceFrame> getDevices() {
        return devices;
    }

    public DeviceFrame getDevice(String name) {
        for (DeviceFrame device : devices) {
            if (device.getName().equals(name)) {
                return device;
            }
        }
        return null;
    }

    private void loadLanguage() {
        String lang = applicationPreferences.get("language", "english");
        language = new Language(lang);

    }

    private String getString(String keyLabel) {
        return language.getProperty(keyLabel);
    }
}
