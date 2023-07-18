package com.sk.directudhar.data

enum class SequenceEnumClass(val sequence:Int ) {
    APPLY_LOAN(2),
    PAN_CARD(3),
    ADHAR_CARD(4),
    CIBIL_SCORE(5),
    E_MANDATE(9),
    E_AGREEMENT(10),
    SUCCESS(11),
    APPRAVAL_PENDING(12),
    MY_ACCOUNT(13);


    companion object {
        infix fun from(value: Int): SequenceEnumClass? = SequenceEnumClass.values().firstOrNull {
            it.sequence == value
        }
    }
}