package ru.lifelaboratory.finopolis

import android.text.Editable

var LOG_TAG: String = "QiwiLifeLaboratory"
var SERVER: String = "http://10.62.100.119/"
var MEMORY: String = "qiwi_memory"
var MEMORY_USER: String = "user_memory"


public fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)