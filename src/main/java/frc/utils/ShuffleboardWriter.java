package frc.utils;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilderImpl;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ShuffleboardWriter {
    public static Map<String, ShuffleboardWriter> instances = new HashMap<>();

    static {
        HAL.report(43, 0);
    }

    private final NetworkTable table;
    private final Map<String, Data> tablesToData = new HashMap<>();

    private ShuffleboardWriter(String tableKey) {
        table = NetworkTableInstance.getDefault().getTable(tableKey);
    }

    public static ShuffleboardWriter getInstance(String tableKey) {
        if (!instances.containsKey(tableKey)) {
            instances.put(tableKey, new ShuffleboardWriter(tableKey));
        }
        return instances.get(tableKey);
    }

    public synchronized void putData(String key, Sendable data) {
        Data sddata = tablesToData.get(key);
        if (sddata == null || sddata.m_sendable != data) {
            if (sddata != null) {
                sddata.m_builder.stopListeners();
            }

            sddata = new Data(data);
            tablesToData.put(key, sddata);
            NetworkTable dataTable = table.getSubTable(key);
            sddata.m_builder.setTable(dataTable);
            data.initSendable(sddata.m_builder);
            sddata.m_builder.updateTable();
            sddata.m_builder.startListeners();
            dataTable.getEntry(".name").setString(key);
        }

    }

    public void putData(Sendable value) {
        putData(value.getName(), value);
    }

    public synchronized Sendable getData(String key) {
        Data data = tablesToData.get(key);
        if (data == null) {
            throw new IllegalArgumentException("ShuffleboardWriter data does not exist with key: " + key);
        } else {
            return data.m_sendable;
        }
    }

    public NetworkTableEntry getEntry(String key) {
        return table.getEntry(key);
    }

    public boolean containsKey(String key) {
        return table.containsKey(key);
    }

    public Set<String> getKeys(int types) {
        return table.getKeys(types);
    }

    public Set<String> getKeys() {
        return table.getKeys();
    }

    public void setPersistent(String key) {
        getEntry(key).setPersistent();
    }

    public void clearPersistent(String key) {
        getEntry(key).clearPersistent();
    }

    public boolean isPersistent(String key) {
        return getEntry(key).isPersistent();
    }

    public void setFlags(String key, int flags) {
        getEntry(key).setFlags(flags);
    }

    public void clearFlags(String key, int flags) {
        getEntry(key).clearFlags(flags);
    }

    public int getFlags(String key) {
        return getEntry(key).getFlags();
    }

    public void delete(String key) {
        table.delete(key);
    }

    public boolean putBoolean(String key, boolean value) {
        return getEntry(key).setBoolean(value);
    }

    public boolean setDefaultBoolean(String key, boolean defaultValue) {
        return getEntry(key).setDefaultBoolean(defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return getEntry(key).getBoolean(defaultValue);
    }

    public boolean putNumber(String key, double value) {
        return getEntry(key).setDouble(value);
    }

    public boolean setDefaultNumber(String key, double defaultValue) {
        return getEntry(key).setDefaultDouble(defaultValue);
    }

    public double getNumber(String key, double defaultValue) {
        return getEntry(key).getDouble(defaultValue);
    }

    public boolean putString(String key, String value) {
        return getEntry(key).setString(value);
    }

    public boolean setDefaultString(String key, String defaultValue) {
        return getEntry(key).setDefaultString(defaultValue);
    }

    public String getString(String key, String defaultValue) {
        return getEntry(key).getString(defaultValue);
    }

    public boolean putBooleanArray(String key, boolean[] value) {
        return getEntry(key).setBooleanArray(value);
    }

    public boolean putBooleanArray(String key, Boolean[] value) {
        return getEntry(key).setBooleanArray(value);
    }

    public boolean setDefaultBooleanArray(String key, boolean[] defaultValue) {
        return getEntry(key).setDefaultBooleanArray(defaultValue);
    }

    public boolean setDefaultBooleanArray(String key, Boolean[] defaultValue) {
        return getEntry(key).setDefaultBooleanArray(defaultValue);
    }

    public boolean[] getBooleanArray(String key, boolean[] defaultValue) {
        return getEntry(key).getBooleanArray(defaultValue);
    }

    public Boolean[] getBooleanArray(String key, Boolean[] defaultValue) {
        return getEntry(key).getBooleanArray(defaultValue);
    }

    public boolean putNumberArray(String key, double[] value) {
        return getEntry(key).setDoubleArray(value);
    }

    public boolean putNumberArray(String key, Double[] value) {
        return getEntry(key).setNumberArray(value);
    }

    public boolean setDefaultNumberArray(String key, double[] defaultValue) {
        return getEntry(key).setDefaultDoubleArray(defaultValue);
    }

    public boolean setDefaultNumberArray(String key, Double[] defaultValue) {
        return getEntry(key).setDefaultNumberArray(defaultValue);
    }

    public double[] getNumberArray(String key, double[] defaultValue) {
        return getEntry(key).getDoubleArray(defaultValue);
    }

    public Double[] getNumberArray(String key, Double[] defaultValue) {
        return getEntry(key).getDoubleArray(defaultValue);
    }

    public boolean putStringArray(String key, String[] value) {
        return getEntry(key).setStringArray(value);
    }

    public boolean setDefaultStringArray(String key, String[] defaultValue) {
        return getEntry(key).setDefaultStringArray(defaultValue);
    }

    public String[] getStringArray(String key, String[] defaultValue) {
        return getEntry(key).getStringArray(defaultValue);
    }

    public boolean putRaw(String key, byte[] value) {
        return getEntry(key).setRaw(value);
    }

    public boolean putRaw(String key, ByteBuffer value, int len) {
        return getEntry(key).setRaw(value, len);
    }

    public boolean setDefaultRaw(String key, byte[] defaultValue) {
        return getEntry(key).setDefaultRaw(defaultValue);
    }

    public byte[] getRaw(String key, byte[] defaultValue) {
        return getEntry(key).getRaw(defaultValue);
    }

    public synchronized void updateValues() {
        Iterator var0 = tablesToData.values().iterator();

        while (var0.hasNext()) {
            Data data = (Data) var0.next();
            data.m_builder.updateTable();
        }
    }

    private static class Data {
        final Sendable m_sendable;
        final SendableBuilderImpl m_builder = new SendableBuilderImpl();

        Data(Sendable sendable) {
            this.m_sendable = sendable;
        }
    }
}
