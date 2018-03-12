package util;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Helper methods for IP addresses.
 */
public class IpUtils {
    /**
     * A logger.
     */
    private static final Logger LOG = Logger.getLogger(IpUtils.class);

    /**
     * Creating an instance is not allowed.
     */
    private IpUtils() {
    }

    /**
     * Clone is not allowed.
     *
     * @return never return something
     *
     * @throws CloneNotSupportedException in any case
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        throw new CloneNotSupportedException();
    }

    /**
     * Check if an IP addresses format and value is valid.
     *
     * @param ip the IP address e.g. "198.168.1.23"
     *
     * @return true if the IP address is valid, else false
     */
    public static boolean validIp(String ip) {
        LOG.trace("ip=[" + ip + ']');

        try {
            if (ip == null || ip.isEmpty()) {
                return false;
            }

            String[] parts = ip.split("\\.");
            if (parts.length != 4) {
                return false;
            }

            for (String s : parts) {
                int i = Integer.parseInt(s);
                if ((i < 0) || (i > 255)) {
                    return false;
                }
            }
            if (ip.endsWith(".")) {
                return false;
            }

            return true;
        }
        catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * Check if an IP address range is valid.
     *
     * @param start the starting IP address of the range definition
     * @param end   the ending IP address of the range definition
     *
     * @return true if the range is valid, else false
     */
    public static boolean validIpRange(String start, String end) {
        LOG.trace("start=[" + start + "] end=[" + end + ']');

        if (!validIp(start)) {
            return false;
        }
        if (!validIp(end)) {
            return false;
        }

        try {
            String[] startParts = start.split("\\.");
            String[] endParts = end.split("\\.");

            if (Integer.parseInt(startParts[0]) > Integer.parseInt(endParts[0])) {
                return false;
            }
            if (Integer.parseInt(startParts[1]) > Integer.parseInt(endParts[1])) {
                return false;
            }
            if (Integer.parseInt(startParts[2]) > Integer.parseInt(endParts[2])) {
                return false;
            }
            if (Integer.parseInt(startParts[3]) > Integer.parseInt(endParts[3])) {
                return false;
            }
        }
        catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }

    /**
     * Create a list of IP addresses in the given range.
     *
     * @param start the starting IP address of the range definition
     * @param end   the ending IP address of the range definition
     *
     * @return a list of IP address strings. The list is empty if the given IP range is invalid
     */
    public static List<String> createIpRange(String start, String end) {
        LOG.trace("start=[" + start + "] end=[" + end + ']');

        List<String> list = new ArrayList<>();

        if (!validIpRange(start, end)) {
            return list;
        }

        for (long val = ipToLong(start); val <= ipToLong(end); ++val) {
            list.add(longToIp(val));
        }
        return list;
    }

    /**
     * Convert an IP address to a hex string.
     *
     * @param ipAddress Input IP address
     *
     * @return the IP address in hex form
     */
    public static String toHex(String ipAddress) {
        LOG.trace("ipAddress=[" + ipAddress + ']');

        return Long.toHexString(ipToLong(ipAddress));
    }

    /**
     * Convert an IP address to a number.
     *
     * @param ipAddress Input IP address
     *
     * @return the IP address as a number
     */
    public static long ipToLong(String ipAddress) {
        LOG.trace("ipAddress=[" + ipAddress + ']');

        long result = 0;
        String[] atoms = ipAddress.split("\\.");

        for (int i = 3; i >= 0; i--) {
            result |= (Long.parseLong(atoms[3 - i]) << (i * 8));
        }

        return result & 0xFFFFFFFF;
    }

    /**
     * Convert an IP address to a String.
     *
     * @param ip Input IP address
     *
     * @return the IP address as a String
     */
    public static String longToIp(long ip) {
        LOG.trace("ip=[" + ip + ']');

        StringBuilder sb = new StringBuilder(15);

        for (int i = 0; i < 4; i++) {
            sb.insert(0, Long.toString(ip & 0xff));

            if (i < 3) {
                sb.insert(0, '.');
            }

            ip >>= 8;
        }

        return sb.toString();
    }
}
