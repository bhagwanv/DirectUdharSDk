package com.sk.directudhar.data

enum class SequenceEnumClass(val sequence:Int ) {
    PHONE_VERIFICATION(10032),
    APPLY_LOAN(14),
    PAN_CARD(3),
    ADHAR_CARD(5),
    CIBIL_SCORE(10037),
    E_MANDATE(6),
    E_AGREEMENT(9),
    SUCCESS(12),
    APPRAVAL_PENDING(17),
    MY_ACCOUNT(18),
    BUSINESS_DETAILS(2);

    companion object {
        infix fun from(value: Int): SequenceEnumClass? = SequenceEnumClass.values().firstOrNull {
            it.sequence == value
        }
    }
}