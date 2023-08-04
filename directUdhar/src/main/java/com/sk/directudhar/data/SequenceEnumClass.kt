package com.sk.directudhar.data

enum class SequenceEnumClass(val sequence:Int ) {
    APPLY_LOAN(2),
    PAN_CARD(3),
    ADHAR_CARD(5),
    CIBIL_SCORE(14),
    E_MANDATE(6),
    E_AGREEMENT(9),
    SUCCESS(12),
    APPRAVAL_PENDING(17),
    MY_ACCOUNT(18);

    companion object {
        infix fun from(value: Int): SequenceEnumClass? = SequenceEnumClass.values().firstOrNull {
            it.sequence == value
        }
    }
}