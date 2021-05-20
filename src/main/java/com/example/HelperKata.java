package com.example;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.BonusType.validateBono;


public class HelperKata {

    protected static String errorMessage = null;
    protected static String dateValidated = null;
    protected static String bonoEnviado = null;
    protected static String ANTERIOR_BONO = null;
    protected static String bonoForObject = null;
    private static final  String EMPTY_STRING = "";
    private static Set<String> codes = new HashSet<>();
    private static  AtomicInteger counter = new AtomicInteger(0);

    public static Flux<CouponDetailDto> getListFromBase64File(final String fileBase64) {
        String characterSeparated = FileCSVEnum.CHARACTER_DEFAULT.getId();
        return  createFluxFrom(fileBase64).map(coupon -> optionalManipulation(generateCoupon(coupon.split(characterSeparated))));
    }

    public static CouponDetailDto optionalManipulation(Coupon cuopon){
        return Optional.of(cuopon)
                .filter(HelperKata::couponIsEmpty).map(coup -> createEmptyDTO())
                .orElseGet(() -> createValidatedDTO());
    }

    public static CouponDetailDto createEmptyDTO(){
        errorMessage = ExperienceErrorsEnum.FILE_ERROR_COLUMN_EMPTY.toString();
        return createCouponDTO();
    }

    public static CouponDetailDto createValidatedDTO(){
        validateDuplicate();
        return createCouponDTO();
    }

    private static Flux<String> createFluxFrom(String fileBase64) {
        return Flux.using(
                () -> new BufferedReader(new InputStreamReader(
                        new ByteArrayInputStream(decodeBase64(fileBase64))
                )).lines().skip(1),
                Flux::fromStream,
                Stream::close
        );
    }

    private static CouponDetailDto createCouponDTO(){
        return CouponDetailDto.aCouponDetailDto()
                .withCode(validateBono())
                .withDueDate(dateValidated)
                .withNumberLine(counter.incrementAndGet())
                .withMessageError(errorMessage)
                .withTotalLinesFile(1)
                .build();
    }

    public static Boolean couponIsEmpty(Coupon coupon){
        return coupon.getCode().equals(EMPTY_STRING) || coupon.getDate().equals(EMPTY_STRING);
    }

   public static void validateDuplicate(){
        if (!codes.add(bonoEnviado)){
            dateValidated = null;
            errorMessage = ExperienceErrorsEnum.FILE_ERROR_CODE_DUPLICATE.toString();
        }
    }

    public static Coupon generateCoupon(String[] details){
        var flux = Flux.just(details);
        Coupon coupon = new Coupon(flux.blockFirst(),flux.blockLast());
        return coupon;
    }

    private static byte[] decodeBase64(final String fileBase64) {
        return Base64.getDecoder().decode(fileBase64);

    }

    public static boolean validateDateRegex(String dateForValidate) {
        String regex = FileCSVEnum.PATTERN_DATE_DEFAULT.getId();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(dateForValidate);
        return matcher.matches();
    }

    public static boolean validateDateIsMinor(String dateForValidate) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(FileCSVEnum.PATTERN_SIMPLE_DATE_FORMAT.getId());
            Date dateActual = sdf.parse(sdf.format(new Date()));
            Date dateCompare = sdf.parse(dateForValidate);
            return dateCompare.compareTo(dateActual) <= 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

}
