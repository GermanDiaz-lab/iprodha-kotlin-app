package com.example.test2

import android.net.Uri

class FotoModel(
    var fotoid: Int = 0,
    var nombrefoto: String = "",
    var fotouri: Uri,
    var obraid: Int,
    var convid: Int,
    var syncfoto: Int = 0,
    var fotolatitud: String = "",
    var fotolongitud: String = "",
    var fechafoto: String = ""
)
