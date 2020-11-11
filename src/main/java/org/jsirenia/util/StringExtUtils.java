package org.jsirenia.util;

import org.apache.commons.lang3.StringUtils;

/**
 */
public class StringExtUtils {

    public static String overlayPhoneNo(String phoneNo) {
        if (phoneNo == null) {
            return "";
        }
        int len = phoneNo.length();
        String replace = StringUtils.repeat("*", len - 3 - 4);
        return StringUtils.overlay(phoneNo, replace, 3, len - 4);
    }

    public static String overlayCertNo(String certNo) {
        if (certNo == null) {
            return "";
        }
        int len = certNo.length();
        String replace = StringUtils.repeat("*", len - 3 - 4);
        return StringUtils.overlay(certNo, replace, 3, len - 4);
    }

    public static String tailBankcard(String bankCard) {
        if (bankCard == null) {
            return "";
        }
        return StringUtils.overlay(bankCard, "", 0, bankCard.length() - 4);
    }

    public static String overlayCustName(String custName) {
        if (custName == null) {
            return "";
        }
        int len = custName.length();
        String replace = StringUtils.repeat("*", len - 1);
        return StringUtils.overlay(custName, replace, 1, len);
    }

    public static String overlayAddress(String addr) {
        if (addr == null) {
            return "";
        }
        int len = addr.length();
        String replace = StringUtils.repeat("*", 5);
        return StringUtils.overlay(addr, replace, 2, len - 1);
    }
}
