package eu.nets.javazone.service;


import java.util.regex.Pattern;

public class BalanceValidator {

    private static final Pattern ACCOUNT_PATTERN = Pattern.compile("[0-9]{11}");
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("[0-9]+");

    public boolean validate(String betalingsTransaksjon) {

        String[] parts = betalingsTransaksjon.trim().split(";");

        return parts.length == 3
                && ACCOUNT_PATTERN.matcher(parts[0]).matches()
                && ACCOUNT_PATTERN.matcher(parts[1]).matches()
                && AMOUNT_PATTERN.matcher(parts[2]).matches();

    }
}
