package com.example;

public class BonusType {

    public static String validateBono(){

        if (previousBonusValidate()) {
            HelperKata.ANTERIOR_BONO = typeBono(HelperKata.bonoEnviado);
            conditionals();
        } else if (HelperKata.ANTERIOR_BONO.equals(typeBono(HelperKata.bonoEnviado))) {
            HelperKata.bonoEnviado = HelperKata.bonoEnviado;
        } else if (!HelperKata.ANTERIOR_BONO.equals(typeBono(HelperKata.bonoEnviado))) {
            HelperKata.bonoEnviado = null;
        }
        return HelperKata.bonoEnviado;
    }

    public static String conditionals(){
        if (HelperKata.ANTERIOR_BONO == "") {
            HelperKata.bonoForObject = null;
        } else {
            HelperKata.bonoForObject = HelperKata.bonoEnviado;
        }
        return HelperKata.bonoForObject;
    }

    public static Boolean previousBonusValidate(){
        return HelperKata.ANTERIOR_BONO == null || HelperKata.ANTERIOR_BONO.equals("");
    }

    public static String typeBono(String bonoIn) {
        String typeBono = "";
        if (validateBousType(bonoIn)) {
            typeBono = ValidateCouponEnum.EAN_13.getTypeOfEnum();
        }
        if (validateBonusTypeEan39(bonoIn)) {
            typeBono = ValidateCouponEnum.EAN_39.getTypeOfEnum();

        }
        else {
            typeBono = ValidateCouponEnum.ALPHANUMERIC.getTypeOfEnum();
        }
        return typeBono;
    }

    public static Boolean validateBousType(String bonoIn){
        return bonoIn.chars().allMatch(Character::isDigit)
                && lengthTypeBonus(bonoIn);
    }

    private static Boolean lengthTypeBonus(String bonoIn){
        return bonoIn.length() >= 12
                && bonoIn.length() <= 13;
    }

    public static Boolean validateBonusTypeEan39(String bonoIn){
        return bonoIn.startsWith("*")
                && lengthBonusTypeEan39(bonoIn);
    }

    private static Boolean lengthBonusTypeEan39(String bonoIn){
        return bonoIn.replace("*", "").length() >= 1
                && bonoIn.replace("*", "").length() <= 43;
    }

}
