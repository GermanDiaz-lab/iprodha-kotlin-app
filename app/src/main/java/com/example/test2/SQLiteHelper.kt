package com.example.test2

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.net.toUri

class SQLiteHelper (context: Context) : SQLiteOpenHelper (context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "iprodha.db"
        private const val TBL_OBRA = "tbl_obra"
        private const val TBL_FOTO = "tbl_foto"
        private const val ID = "id"
        private const val FOTOID = "fotoid"
        private const val NOMBREFOTO = "nombrefoto"
        private const val FOTOURI = "fotouri"
        private const val OBRAID = "obraid"
        private const val OBRA = "obra"
        private const val ITEMS = "items"
        private const val USUARIO = "usuario"
        private const val TBL_USUARIO = "tbl_usuario"
        private const val USUARIOID = "usuarioid"
        private const val USUARIOEMAIL = "usuarioemail"
        private const val USUARIONOMBRE = "usuarionombre"
        private const val SYNCOBRA = "syncobra"
        private const val SYNCFOTO = "syncfoto"
        private const val DOCREF = "docref"
        private const val FOTOURL = "fotourl"
        private const val FOTOLATITUD = "fotolatitud"
        private const val FOTOLONGITUD = "fotolongitud"
        private const val TBL_TEMP = "tbl_temp"
        private const val TEMPURI = "tempuri"
        private const val IDTEMP = "idtemp"
        private const val TEMPGEO = "tempgeo"
        private const val TEMPLAT = "templat"
        private const val TEMPLONG = "templong"
        private const val TBL_CONV = "tbl_conv"
        private const val SYNCCONV = "syncconv"
        private const val CONV = "conv"
        private const val TBL_INFORME = "tbl_informe"
        private const val IDINFORME = "id"
        private const val USUARIOINFORME = "usuarioinforme"
        private const val SITJUR = "sitjur"
        private const val EXP = "exp"
        private const val ESTADODEUD = "estadodeud"
        private const val NOTDEUD = "notdeud"
        private const val TITULAR = "titular"
        private const val NOTA = "nota"
        private const val FECHA = "fecha"
        private const val LOCALIDAD = "localidad"
        private const val LEGAJO = "legajo"
        private const val OCUPANTE = "ocupante"
        private const val OCUPANTEDNI = "ocupantedni"
        private const val RESIDEDESDE = "residedesde"
        private const val TIPOLOGIA = "tipologia"
        private const val ESTADO = "estado"
        private const val TITULO = "titulo"
        private const val SITHAB = "sithab"
        private const val IDENTVIV = "identviv"
        private const val AMPLIACION = "ampliacion"
        private const val OBSERVACION = "observacion"
        private const val ANTECEDENTEIPRODHA = "antecedenteiprodha"
        private const val ANTECEDENTEVIV = "antecedenteviv"
        private const val ANTECEDENTEARREGLO = "antecedentearreglo"
        private const val ANTECEDENTELOTE = "antecedentelote"
        private const val ANTECEDENTEMV = "antecedentemv"
        private const val ANTECEDENTEVR = "antecedentevr"
        private const val COMOACCEDE = "comoaccede"
        private const val OTROSINICIO = "otrosinicio"
        private const val VALIDOGRUPO = "validogrupo"
        private const val TITULARDNI = "titulardni"
        private const val ESTADOCIVILTITULAR = "estadociviltitular"
        private const val ESTADOCIVILCONYUGE = "estadocivilconyuge"
        private const val DNICONYUGE = "dniconyuge"
        private const val DNIHIJOS = "dnihijos"
        private const val ACTADEMATRIMONIO = "actadematrimonio"
        private const val UNIONCONVIVENCIAL = "unionconvivencial"
        private const val PARTIDASNAC = "partidasnac"
        private const val CONTACTOTELEFONICO = "contactotelefonico"
        private const val CORREOELECTRONICO = "correoelectronico"
        private const val OBSERVACIONGRUPO = "observaciongrupo"
        private const val ESCOLARIDAD = "escolaridad"
        private const val OBRASOCIAL = "obrasocial"
        private const val TIENECUD = "tienecud"
        private const val VIGENCIACUD = "vigenciacud"
        private const val DISCAPACIDAD = "discapacidad"
        private const val DIAGNOSTICO = "diagnostico"
        private const val SILLADERUEDAS = "silladeruedas"
        private const val MULETAS = "muletas"
        private const val ANDADOR = "andador"
        private const val OTROSMOVILIDAD = "otrosmovilidad"
        private const val OBSERVACIONSALUD = "observacionsalud"
        private const val TRANSPLANTADO = "trasnsplantado"
        private const val CARNET = "carnet"
        private const val LISTADEESPERA = "listadeespera"
        private const val OBSERVACIONTRASPLANTE = "observaciontrasplante"
        private const val INGRESO1 = "ingreso1"
        private const val INGRESO2 = "ingreso2"
        private const val INGRESO3 = "ingreso3"
        private const val INGRESO4 = "ingreso4"
        private const val NOMBREINGRESO1 = "nombreingreso1"
        private const val NOMBREINGRESO2 = "nombreingreso2"
        private const val NOMBREINGRESO3 = "nombreingreso3"
        private const val NOMBREINGRESO4 = "nombreingreso4"
        private const val CATEGORIALABORAL1 = "categorialaboral1"
        private const val CATEGORIALABORAL2 = "categorialaboral2"
        private const val CATEGORIALABORAL3 = "categorialaboral3"
        private const val CATEGORIALABORAL4 = "categorialaboral4"
        private const val OCUPACION1 = "ocupacion1"
        private const val OCUPACION2 = "ocupacion2"
        private const val OCUPACION3 = "ocupacion3"
        private const val OCUPACION4 = "ocupacion4"
        private const val TELEFONO1 = "telefono1"
        private const val TELEFONO2 = "telefono2"
        private const val TELEFONO3 = "telefono3"
        private const val TELEFONO4 = "telefono4"
        private const val DOMICILIO1 = "domicilio1"
        private const val DOMICILIO2 = "domicilio2"
        private const val DOMICILIO3 = "domicilio3"
        private const val DOMICILIO4 = "domicilio4"
        private const val LUGAR1 = "lugar1"
        private const val LUGAR2 = "lugar2"
        private const val LUGAR3 = "lugar3"
        private const val LUGAR4 = "lugar4"
        private const val FECHADELRECIBO1 = "fechadelrecibo1"
        private const val FECHADELRECIBO2 = "fechadelrecibo2"
        private const val FECHADELRECIBO3 = "fechadelrecibo3"
        private const val FECHADELRECIBO4 = "fechadelrecibo4"
        private const val PRINCIPALPAGADOR = "principalpagador"
        private const val DNIPAGADOR = "dnipagador"
        private const val INGRESOSPAGADOR = "ingresospagador"
        private const val CATLABORALPAGADOR = "catlaboralpagador"
        private const val GARANTE = "garante"
        private const val DNIGARANTE = "dnigarante"
        private const val INGRESOSGARANTE = "ingresosgarante"
        private const val CATLABORALGARANTE = "catlaboralgarante"
        private const val DECLARACIONJURADA = "declaracionjurada"
        private const val TITULARPRESTAMOS = "titularprestamos"
        private const val PRESTAMOMONTO = "prestamomonto"
        private const val PLAZO = "plazo"
        private const val CONSUMOMENSUAL = "consumomensual"
        private const val TARJETAS = "tarjetas"
        private const val TIPODETARJETA = "tipodetarjeta"
        private const val OBSERVACIONESSITECO = "observacionessiteco"
        private const val LUZ = "luz"
        private const val MONTOLUZ = "montoluz"
        private const val LUZFORMAL = "luzformal"
        private const val AGUA = "agua"
        private const val AGUAMONTO = "aguamonto"
        private const val AGUAFORMAL = "aguaformal"
        private const val CABLE = "cable"
        private const val CABLEMONTO = "cablemonto"
        private const val CABLEFORMAL = "cableformal"
        private const val INTERNET = "internet"
        private const val INTERNETMONTO = "internetmonto"
        private const val INTERNETFORMAL = "internetformal"
        private const val OBSERVACIONESSERVICIOS = "observacionesservicios"
        private const val CUOTASOCIAL = "cuotasocial"
        private const val CUOTAPROVISORIA = "cuotaprovisoria"
        private const val CUOTAESTANDAR = "cuotaestandar"
        private const val PLANVENTA = "planventa"
        private const val PLANALQUILER = "planalquiler"
        private const val OTROSMODALIDAD = "otrosmodalidad"
        private const val PLAZOPRESENTACION = "plazopresentacion"
        private const val OBSERVACIONESPLAZO = "observacionesplazo"
        private const val FECHAFOTO = "fechafoto"
        private const val INFORMELATITUD = "informelatitud"
        private const val INFORMELONGITUD = "informelongitud"
        private const val TBL_FIRMA = "tbl_firma"
        private const val FIRMAID = "firmaid"
        private const val NOMBREFIRMA = "nombrefirma"
        private const val FIRMAURI = "firmauri"
        private const val INFORMEDNI = "informedni"
        private const val FIRMAURL = "firmaurl"
        private const val DESCRIPCION = "descripcion"
        private const val CONVID = "convid"


    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTblConv = ("CREATE TABLE " + TBL_CONV + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + CONV + " TEXT,"
                + DESCRIPCION + " TEXT," + ITEMS + " INTEGER," + USUARIO + " TEXT," + SYNCCONV + " INTEGER," + DOCREF + " TEXT" + ");")
        val createTblObra = ("CREATE TABLE " + TBL_OBRA + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + OBRA + " TEXT,"
                + DESCRIPCION + " TEXT," + ITEMS + " INTEGER," + USUARIO + " TEXT," + SYNCOBRA + " INTEGER," + DOCREF + " TEXT" + ");")
        val createTblFoto = ("CREATE TABLE " + TBL_FOTO + "(" + FOTOID + " INTEGER PRIMARY KEY AUTOINCREMENT," + NOMBREFOTO + " TEXT,"
                + FOTOURI + " TEXT," + OBRAID + " INTEGER," + CONVID + " INTEGER," + SYNCFOTO + " INTEGER," + FOTOURL + " TEXT," + FOTOLATITUD
                + " TEXT," + FOTOLONGITUD + " TEXT," + FECHAFOTO + " TEXT" + ");")
        val createTblUsuario = ("CREATE TABLE " + TBL_USUARIO + "(" + USUARIOID + " INTEGER," + USUARIOEMAIL + " TEXT,"
                + USUARIONOMBRE + " TEXT" + ");")
        val createTblTemp = ("CREATE TABLE " + TBL_TEMP + "(" + IDTEMP + " TEXT," + TEMPURI + " TEXT,"
                + TEMPGEO + " TEXT," + TEMPLAT + " TEXT," + TEMPLONG + " TEXT" + ");")
        val createTblFirma = ("CREATE TABLE " + TBL_FIRMA + "(" + NOMBREFIRMA + " TEXT,"
                + FIRMAURI + " TEXT," + INFORMEDNI + " TEXT," + FIRMAURL + " TEXT" + ");")

        val createTblInforme = ("CREATE TABLE " + TBL_INFORME + "("
                + IDINFORME + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + USUARIOINFORME + " TEXT,"
                + SITJUR + " TEXT,"
                + EXP + " TEXT,"
                + ESTADODEUD + " TEXT,"
                + NOTDEUD + " TEXT,"
                + TITULAR + " TEXT,"
                + NOTA + " TEXT,"
                + FECHA + " TEXT,"
                + LOCALIDAD + " TEXT,"
                + LEGAJO + " TEXT,"
                + OCUPANTE + " TEXT,"
                + OCUPANTEDNI + " TEXT,"
                + RESIDEDESDE + " TEXT,"
                + TIPOLOGIA + " TEXT,"
                + ESTADO + " TEXT,"
                + TITULO + " TEXT,"
                + SITHAB + " TEXT,"
                + IDENTVIV + " TEXT,"
                + AMPLIACION + " TEXT,"
                + OBSERVACION + " TEXT,"
                + ANTECEDENTEIPRODHA + " TEXT,"
                + ANTECEDENTEVIV + " TEXT,"
                + ANTECEDENTEARREGLO + " TEXT,"
                + ANTECEDENTELOTE + " TEXT,"
                + ANTECEDENTEMV + " TEXT,"
                + ANTECEDENTEVR + " TEXT,"
                + COMOACCEDE + " TEXT,"
                + OTROSINICIO + " TEXT,"
                + VALIDOGRUPO + " TEXT,"
                + TITULARDNI + " TEXT,"
                + ESTADOCIVILTITULAR + " TEXT,"
                + ESTADOCIVILCONYUGE + " TEXT,"
                + DNICONYUGE + " TEXT,"
                + DNIHIJOS + " TEXT,"
                + ACTADEMATRIMONIO + " TEXT,"
                + UNIONCONVIVENCIAL + " TEXT,"
                + PARTIDASNAC + " TEXT,"
                + CONTACTOTELEFONICO + " TEXT,"
                + CORREOELECTRONICO + " TEXT,"
                + OBSERVACIONGRUPO + " TEXT,"
                + ESCOLARIDAD + " TEXT,"
                + OBRASOCIAL + " TEXT,"
                + TIENECUD + " TEXT,"
                + VIGENCIACUD + " TEXT,"
                + DISCAPACIDAD + " TEXT,"
                + DIAGNOSTICO + " TEXT,"
                + SILLADERUEDAS + " TEXT,"
                + MULETAS + " TEXT,"
                + ANDADOR + " TEXT,"
                + OTROSMOVILIDAD + " TEXT,"
                + OBSERVACIONSALUD + " TEXT,"
                + TRANSPLANTADO + " TEXT,"
                + CARNET + " TEXT,"
                + LISTADEESPERA + " TEXT,"
                + OBSERVACIONTRASPLANTE + " TEXT,"
                + INGRESO1 + " TEXT,"
                + INGRESO2 + " TEXT,"
                + INGRESO3 + " TEXT,"
                + INGRESO4 + " TEXT,"
                + NOMBREINGRESO1 + " TEXT,"
                + NOMBREINGRESO2 + " TEXT,"
                + NOMBREINGRESO3 + " TEXT,"
                + NOMBREINGRESO4 + " TEXT,"
                + CATEGORIALABORAL1 + " TEXT,"
                + CATEGORIALABORAL2 + " TEXT,"
                + CATEGORIALABORAL3 + " TEXT,"
                + CATEGORIALABORAL4 + " TEXT,"
                + OCUPACION1 + " TEXT,"
                + OCUPACION2 + " TEXT,"
                + OCUPACION3 + " TEXT,"
                + OCUPACION4 + " TEXT,"
                + TELEFONO1 + " TEXT,"
                + TELEFONO2 + " TEXT,"
                + TELEFONO3 + " TEXT,"
                + TELEFONO4 + " TEXT,"
                + DOMICILIO1 + " TEXT,"
                + DOMICILIO2 + " TEXT,"
                + DOMICILIO3 + " TEXT,"
                + DOMICILIO4 + " TEXT,"
                + LUGAR1 + " TEXT,"
                + LUGAR2 + " TEXT,"
                + LUGAR3 + " TEXT,"
                + LUGAR4 + " TEXT,"
                + FECHADELRECIBO1 + " TEXT,"
                + FECHADELRECIBO2 + " TEXT,"
                + FECHADELRECIBO3 + " TEXT,"
                + FECHADELRECIBO4 + " TEXT,"
                + PRINCIPALPAGADOR + " TEXT,"
                + DNIPAGADOR + " TEXT,"
                + INGRESOSPAGADOR + " TEXT,"
                + CATLABORALPAGADOR + " TEXT,"
                + GARANTE + " TEXT,"
                + DNIGARANTE + " TEXT,"
                + INGRESOSGARANTE + " TEXT,"
                + CATLABORALGARANTE + " TEXT,"
                + DECLARACIONJURADA + " TEXT,"
                + TITULARPRESTAMOS + " TEXT,"
                + PRESTAMOMONTO + " TEXT,"
                + PLAZO + " TEXT,"
                + CONSUMOMENSUAL + " TEXT,"
                + TARJETAS + " TEXT,"
                + TIPODETARJETA + " TEXT,"
                + OBSERVACIONESSITECO + " TEXT,"
                + LUZ + " TEXT,"
                + MONTOLUZ + " TEXT,"
                + LUZFORMAL + " TEXT,"
                + AGUA + " TEXT,"
                + AGUAMONTO + " TEXT,"
                + AGUAFORMAL + " TEXT,"
                + CABLE + " TEXT,"
                + CABLEMONTO + " TEXT,"
                + CABLEFORMAL + " TEXT,"
                + INTERNET + " TEXT,"
                + INTERNETMONTO + " TEXT,"
                + INTERNETFORMAL + " TEXT,"
                + OBSERVACIONESSERVICIOS + " TEXT,"
                + CUOTASOCIAL + " TEXT,"
                + CUOTAPROVISORIA + " TEXT,"
                + CUOTAESTANDAR + " TEXT,"
                + PLANVENTA + " TEXT,"
                + PLANALQUILER + " TEXT,"
                + OTROSMODALIDAD + " TEXT,"
                + PLAZOPRESENTACION + " TEXT,"
                + OBSERVACIONESPLAZO + " TEXT,"
                + INFORMELATITUD + " TEXT,"
                + INFORMELONGITUD + " TEXT"
                + ")")


        db?.execSQL(createTblObra)
        db?.execSQL(createTblFoto)
        db?.execSQL(createTblUsuario)
        db?.execSQL(createTblTemp)
        db?.execSQL(createTblConv)
        db?.execSQL(createTblInforme)
        db?.execSQL(createTblFirma)


    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TBL_OBRA")
        db.execSQL("DROP TABLE IF EXISTS $TBL_CONV")
        db.execSQL("DROP TABLE IF EXISTS $TBL_FOTO")
        db.execSQL("DROP TABLE IF EXISTS $TBL_USUARIO")
        db.execSQL("DROP TABLE IF EXISTS $TBL_TEMP")
        db.execSQL("DROP TABLE IF EXISTS $TBL_FIRMA")

        onCreate(db)
    }

    fun inserFirma(firma: FirmaModel): Long {
        val db = this.writableDatabase
        val firmauri = firma.firmauri.toString()

        val contentValues = ContentValues()
        contentValues.put(NOMBREFIRMA, firma.nombrefirma)
        contentValues.put(FIRMAURI, firmauri)
        contentValues.put(INFORMEDNI, firma.informedni)
        contentValues.put(FIRMAURL, firma.firmaurl)


        val success = db.insert(TBL_FIRMA, null, contentValues)
        db.close()
        return success
    }

    fun getFirma(informedni: String): ArrayList<FirmaModel>{
        val stdList: ArrayList<FirmaModel> = ArrayList()
        val selectQuery = "SELECT $NOMBREFIRMA, $FIRMAURL, $FIRMAURI FROM $TBL_FIRMA WHERE $INFORMEDNI = $informedni"
        val db = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var nombrefirma: String
        var firmaurl: String
        var firmauri: String
        if(cursor.moveToFirst()){
            do{
                nombrefirma = cursor.getString(cursor.getColumnIndex("nombrefirma"))
                firmaurl = cursor.getString(cursor.getColumnIndex("firmaurl"))
                firmauri = cursor.getString(cursor.getColumnIndex("firmauri"))


                val std = FirmaModel(nombrefirma = nombrefirma, firmaurl = firmaurl, firmauri = firmauri.toUri())
                stdList.add(std)
            } while (cursor.moveToNext())
        }
        cursor.close()

        return stdList
    }

    fun insertInforme(informe: InformeModel): Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(USUARIOINFORME, informe.usuario)
        contentValues.put(SITJUR, informe.sitjur)
        contentValues.put(EXP, informe.exp)
        contentValues.put(ESTADODEUD, informe.estadodeud)
        contentValues.put(NOTDEUD, informe.notdeud)
        contentValues.put(TITULAR, informe.titular)
        contentValues.put(NOTA, informe.nota)
        contentValues.put(FECHA, informe.fecha)
        contentValues.put(LOCALIDAD, informe.localidad)
        contentValues.put(LEGAJO, informe.legajo)
        contentValues.put(OCUPANTE, informe.ocupante)
        contentValues.put(OCUPANTEDNI, informe.ocupantedni)
        contentValues.put(RESIDEDESDE, informe.residedesde)
        contentValues.put(TIPOLOGIA, informe.tipologia)
        contentValues.put(ESTADO, informe.estado)
        contentValues.put(TITULO, informe.titulo)
        contentValues.put(SITHAB, informe.sithab)
        contentValues.put(IDENTVIV, informe.identviv)
        contentValues.put(AMPLIACION, informe.ampliacion)
        contentValues.put(OBSERVACION, informe.observacion)
        contentValues.put(ANTECEDENTEIPRODHA, informe.antecedenteiprodha)
        contentValues.put(ANTECEDENTEVIV, informe.antecedenteviv)
        contentValues.put(ANTECEDENTEARREGLO, informe.antecedentearreglo)
        contentValues.put(ANTECEDENTELOTE, informe.antecedentelote)
        contentValues.put(ANTECEDENTEMV, informe.antecedentemv)
        contentValues.put(ANTECEDENTEVR, informe.antecedentevr)
        contentValues.put(COMOACCEDE, informe.comoaccede)
        contentValues.put(OTROSINICIO, informe.otrosinicio)
        contentValues.put(VALIDOGRUPO, informe.validogrupo)
        contentValues.put(TITULARDNI, informe.titulardni)
        contentValues.put(ESTADOCIVILTITULAR, informe.estadociviltitular)
        contentValues.put(ESTADOCIVILCONYUGE, informe.estadocivilconyuge)
        contentValues.put(DNICONYUGE, informe.dniconyuge)
        contentValues.put(DNIHIJOS, informe.dnihijos)
        contentValues.put(ACTADEMATRIMONIO, informe.actadematrimonio)
        contentValues.put(UNIONCONVIVENCIAL, informe.unionconvivencial)
        contentValues.put(PARTIDASNAC, informe.partidasnac)
        contentValues.put(CONTACTOTELEFONICO, informe.contactotelefonico)
        contentValues.put(CORREOELECTRONICO, informe.correoelectronico)
        contentValues.put(OBSERVACIONGRUPO, informe.observaciongrupo)
        contentValues.put(ESCOLARIDAD, informe.escolaridad)
        contentValues.put(OBRASOCIAL, informe.obrasocial)
        contentValues.put(TIENECUD, informe.tienecud)
        contentValues.put(VIGENCIACUD, informe.vigenciacud)
        contentValues.put(DISCAPACIDAD, informe.discapacidad)
        contentValues.put(DIAGNOSTICO, informe.diagnostico)
        contentValues.put(SILLADERUEDAS, informe.silladeruedas)
        contentValues.put(MULETAS, informe.muletas)
        contentValues.put(ANDADOR, informe.andador)
        contentValues.put(OTROSMOVILIDAD, informe.otrosmovilidad)
        contentValues.put(OBSERVACIONSALUD, informe.observacionsalud)
        contentValues.put(TRANSPLANTADO, informe.trasnsplantado)
        contentValues.put(CARNET, informe.carnet)
        contentValues.put(LISTADEESPERA, informe.listadeespera)
        contentValues.put(OBSERVACIONTRASPLANTE, informe.observaciontrasplante)
        contentValues.put(INGRESO1, informe.ingreso1)
        contentValues.put(INGRESO2, informe.ingreso2)
        contentValues.put(INGRESO3, informe.ingreso3)
        contentValues.put(INGRESO4, informe.ingreso4)
        contentValues.put(NOMBREINGRESO1, informe.nombreingreso1)
        contentValues.put(NOMBREINGRESO2, informe.nombreingreso2)
        contentValues.put(NOMBREINGRESO3, informe.nombreingreso3)
        contentValues.put(NOMBREINGRESO4, informe.nombreingreso4)
        contentValues.put(CATEGORIALABORAL1, informe.categorialaboral1)
        contentValues.put(CATEGORIALABORAL2, informe.categorialaboral2)
        contentValues.put(CATEGORIALABORAL3, informe.categorialaboral3)
        contentValues.put(CATEGORIALABORAL4, informe.categorialaboral4)
        contentValues.put(OCUPACION1, informe.ocupacion1)
        contentValues.put(OCUPACION2, informe.ocupacion2)
        contentValues.put(OCUPACION3, informe.ocupacion3)
        contentValues.put(OCUPACION4, informe.ocupacion4)
        contentValues.put(TELEFONO1, informe.telefono1)
        contentValues.put(TELEFONO2, informe.telefono2)
        contentValues.put(TELEFONO3, informe.telefono3)
        contentValues.put(TELEFONO4, informe.telefono4)
        contentValues.put(DOMICILIO1, informe.domicilio1)
        contentValues.put(DOMICILIO2, informe.domicilio2)
        contentValues.put(DOMICILIO3, informe.domicilio3)
        contentValues.put(DOMICILIO4, informe.domicilio4)
        contentValues.put(LUGAR1, informe.lugar1)
        contentValues.put(LUGAR2, informe.lugar2)
        contentValues.put(LUGAR3, informe.lugar3)
        contentValues.put(LUGAR4, informe.lugar4)
        contentValues.put(FECHADELRECIBO1, informe.fechadelrecibo1)
        contentValues.put(FECHADELRECIBO2, informe.fechadelrecibo2)
        contentValues.put(FECHADELRECIBO3, informe.fechadelrecibo3)
        contentValues.put(FECHADELRECIBO4, informe.fechadelrecibo4)
        contentValues.put(PRINCIPALPAGADOR, informe.principalpagador)
        contentValues.put(DNIPAGADOR, informe.dnipagador)
        contentValues.put(INGRESOSPAGADOR, informe.ingresospagador)
        contentValues.put(CATLABORALPAGADOR, informe.catlaboralpagador)
        contentValues.put(GARANTE, informe.garante)
        contentValues.put(DNIGARANTE, informe.dnigarante)
        contentValues.put(INGRESOSGARANTE, informe.ingresosgarante)
        contentValues.put(CATLABORALGARANTE, informe.catlaboralgarante)
        contentValues.put(DECLARACIONJURADA, informe.declaracionjurada)
        contentValues.put(TITULARPRESTAMOS, informe.titularprestamos)
        contentValues.put(PRESTAMOMONTO, informe.prestamomonto)
        contentValues.put(PLAZO, informe.plazo)
        contentValues.put(CONSUMOMENSUAL, informe.consumomensual)
        contentValues.put(TARJETAS, informe.tarjetas)
        contentValues.put(TIPODETARJETA, informe.tipodetarjeta)
        contentValues.put(OBSERVACIONESSITECO, informe.observacionessiteco)
        contentValues.put(LUZ, informe.luz)
        contentValues.put(MONTOLUZ, informe.montoluz)
        contentValues.put(LUZFORMAL, informe.luzformal)
        contentValues.put(AGUA, informe.agua)
        contentValues.put(AGUAMONTO, informe.aguamonto)
        contentValues.put(AGUAFORMAL, informe.aguaformal)
        contentValues.put(CABLE, informe.cable)
        contentValues.put(CABLEMONTO, informe.cablemonto)
        contentValues.put(CABLEFORMAL, informe.cableformal)
        contentValues.put(INTERNET, informe.internet)
        contentValues.put(INTERNETMONTO, informe.internetmonto)
        contentValues.put(INTERNETFORMAL, informe.internetformal)
        contentValues.put(OBSERVACIONESSERVICIOS, informe.observacionesservicios)
        contentValues.put(CUOTASOCIAL, informe.cuotasocial)
        contentValues.put(CUOTAPROVISORIA, informe.cuotaprovisoria)
        contentValues.put(CUOTAESTANDAR, informe.cuotaestandar)
        contentValues.put(PLANVENTA, informe.planventa)
        contentValues.put(PLANALQUILER, informe.planalquiler)
        contentValues.put(OTROSMODALIDAD, informe.otrosmodalidad)
        contentValues.put(PLAZOPRESENTACION, informe.plazopresentacion)
        contentValues.put(OBSERVACIONESPLAZO, informe.observacionesplazo)
        contentValues.put(INFORMELATITUD, informe.informelatitud)
        contentValues.put(INFORMELONGITUD, informe.informelongitud)

        val success = db.insert(TBL_INFORME, null, contentValues)
        db.close()
        return success
    }

    fun getInforme(informedni: String): ArrayList<InformeModel>{
        val stdList: ArrayList<InformeModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_INFORME WHERE $TITULARDNI = $informedni"
        val db = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        if (cursor.moveToFirst()) {
            do {
                val informe = InformeModel(
                    usuario = cursor.getString(cursor.getColumnIndex(USUARIOINFORME)),
                    sitjur = cursor.getString(cursor.getColumnIndex(SITJUR)),
                    exp = cursor.getString(cursor.getColumnIndex(EXP)),
                    estadodeud = cursor.getString(cursor.getColumnIndex(ESTADODEUD)),
                    notdeud = cursor.getString(cursor.getColumnIndex(NOTDEUD)),
                    titular = cursor.getString(cursor.getColumnIndex(TITULAR)),
                    nota = cursor.getString(cursor.getColumnIndex(NOTA)),
                    fecha = cursor.getString(cursor.getColumnIndex(FECHA)),
                    localidad = cursor.getString(cursor.getColumnIndex(LOCALIDAD)),
                    legajo = cursor.getString(cursor.getColumnIndex(LEGAJO)),
                    ocupante = cursor.getString(cursor.getColumnIndex(OCUPANTE)),
                    ocupantedni = cursor.getString(cursor.getColumnIndex(OCUPANTEDNI)),
                    residedesde = cursor.getString(cursor.getColumnIndex(RESIDEDESDE)),
                    tipologia = cursor.getString(cursor.getColumnIndex(TIPOLOGIA)),
                    estado = cursor.getString(cursor.getColumnIndex(ESTADO)),
                    titulo = cursor.getString(cursor.getColumnIndex(TITULO)),
                    sithab = cursor.getString(cursor.getColumnIndex(SITHAB)),
                    identviv = cursor.getString(cursor.getColumnIndex(IDENTVIV)),
                    ampliacion = cursor.getString(cursor.getColumnIndex(AMPLIACION)),
                    observacion = cursor.getString(cursor.getColumnIndex(OBSERVACION)),
                    antecedenteiprodha = cursor.getString(cursor.getColumnIndex(ANTECEDENTEIPRODHA)),
                    antecedenteviv = cursor.getString(cursor.getColumnIndex(ANTECEDENTEVIV)),
                    antecedentearreglo = cursor.getString(cursor.getColumnIndex(ANTECEDENTEARREGLO)),
                    antecedentelote = cursor.getString(cursor.getColumnIndex(ANTECEDENTELOTE)),
                    antecedentemv = cursor.getString(cursor.getColumnIndex(ANTECEDENTEMV)),
                    antecedentevr = cursor.getString(cursor.getColumnIndex(ANTECEDENTEVR)),
                    comoaccede = cursor.getString(cursor.getColumnIndex(COMOACCEDE)),
                    otrosinicio = cursor.getString(cursor.getColumnIndex(OTROSINICIO)),
                    validogrupo = cursor.getString(cursor.getColumnIndex(VALIDOGRUPO)),
                    titulardni = cursor.getString(cursor.getColumnIndex(TITULARDNI)),
                    estadociviltitular = cursor.getString(cursor.getColumnIndex(ESTADOCIVILTITULAR)),
                    estadocivilconyuge = cursor.getString(cursor.getColumnIndex(ESTADOCIVILCONYUGE)),
                    dniconyuge = cursor.getString(cursor.getColumnIndex(DNICONYUGE)),
                    dnihijos = cursor.getString(cursor.getColumnIndex(DNIHIJOS)),
                    actadematrimonio = cursor.getString(cursor.getColumnIndex(ACTADEMATRIMONIO)),
                    unionconvivencial = cursor.getString(cursor.getColumnIndex(UNIONCONVIVENCIAL)),
                    partidasnac = cursor.getString(cursor.getColumnIndex(PARTIDASNAC)),
                    contactotelefonico = cursor.getString(cursor.getColumnIndex(CONTACTOTELEFONICO)),
                    correoelectronico = cursor.getString(cursor.getColumnIndex(CORREOELECTRONICO)),
                    observaciongrupo = cursor.getString(cursor.getColumnIndex(OBSERVACIONGRUPO)),
                    escolaridad = cursor.getString(cursor.getColumnIndex(ESCOLARIDAD)),
                    obrasocial = cursor.getString(cursor.getColumnIndex(OBRASOCIAL)),
                    tienecud = cursor.getString(cursor.getColumnIndex(TIENECUD)),
                    vigenciacud = cursor.getString(cursor.getColumnIndex(VIGENCIACUD)),
                    discapacidad = cursor.getString(cursor.getColumnIndex(DISCAPACIDAD)),
                    diagnostico = cursor.getString(cursor.getColumnIndex(DIAGNOSTICO)),
                    silladeruedas = cursor.getString(cursor.getColumnIndex(SILLADERUEDAS)),
                    muletas = cursor.getString(cursor.getColumnIndex(MULETAS)),
                    andador = cursor.getString(cursor.getColumnIndex(ANDADOR)),
                    otrosmovilidad = cursor.getString(cursor.getColumnIndex(OTROSMOVILIDAD)),
                    observacionsalud = cursor.getString(cursor.getColumnIndex(OBSERVACIONSALUD)),
                    trasnsplantado = cursor.getString(cursor.getColumnIndex(TRANSPLANTADO)),
                    carnet = cursor.getString(cursor.getColumnIndex(CARNET)),
                    listadeespera = cursor.getString(cursor.getColumnIndex(LISTADEESPERA)),
                    observaciontrasplante = cursor.getString(cursor.getColumnIndex(OBSERVACIONTRASPLANTE)),
                    ingreso1 = cursor.getString(cursor.getColumnIndex(INGRESO1)),
                    ingreso2 = cursor.getString(cursor.getColumnIndex(INGRESO2)),
                    ingreso3 = cursor.getString(cursor.getColumnIndex(INGRESO3)),
                    ingreso4 = cursor.getString(cursor.getColumnIndex(INGRESO4)),
                    nombreingreso1 = cursor.getString(cursor.getColumnIndex(NOMBREINGRESO1)),
                    nombreingreso2 = cursor.getString(cursor.getColumnIndex(NOMBREINGRESO2)),
                    nombreingreso3 = cursor.getString(cursor.getColumnIndex(NOMBREINGRESO3)),
                    nombreingreso4 = cursor.getString(cursor.getColumnIndex(NOMBREINGRESO4)),
                    categorialaboral1 = cursor.getString(cursor.getColumnIndex(CATEGORIALABORAL1)),
                    categorialaboral2 = cursor.getString(cursor.getColumnIndex(CATEGORIALABORAL2)),
                    categorialaboral3 = cursor.getString(cursor.getColumnIndex(CATEGORIALABORAL3)),
                    categorialaboral4 = cursor.getString(cursor.getColumnIndex(CATEGORIALABORAL4)),
                    ocupacion1 = cursor.getString(cursor.getColumnIndex(OCUPACION1)),
                    ocupacion2 = cursor.getString(cursor.getColumnIndex(OCUPACION2)),
                    ocupacion3 = cursor.getString(cursor.getColumnIndex(OCUPACION3)),
                    ocupacion4 = cursor.getString(cursor.getColumnIndex(OCUPACION4)),
                    telefono1 = cursor.getString(cursor.getColumnIndex(TELEFONO1)),
                    telefono2 = cursor.getString(cursor.getColumnIndex(TELEFONO2)),
                    telefono3 = cursor.getString(cursor.getColumnIndex(TELEFONO3)),
                    telefono4 = cursor.getString(cursor.getColumnIndex(TELEFONO4)),
                    domicilio1 = cursor.getString(cursor.getColumnIndex(DOMICILIO1)),
                    domicilio2 = cursor.getString(cursor.getColumnIndex(DOMICILIO2)),
                    domicilio3 = cursor.getString(cursor.getColumnIndex(DOMICILIO3)),
                    domicilio4 = cursor.getString(cursor.getColumnIndex(DOMICILIO4)),
                    lugar1 = cursor.getString(cursor.getColumnIndex(LUGAR1)),
                    lugar2 = cursor.getString(cursor.getColumnIndex(LUGAR2)),
                    lugar3 = cursor.getString(cursor.getColumnIndex(LUGAR3)),
                    lugar4 = cursor.getString(cursor.getColumnIndex(LUGAR4)),
                    fechadelrecibo1 = cursor.getString(cursor.getColumnIndex(FECHADELRECIBO1)),
                    fechadelrecibo2 = cursor.getString(cursor.getColumnIndex(FECHADELRECIBO2)),
                    fechadelrecibo3 = cursor.getString(cursor.getColumnIndex(FECHADELRECIBO3)),
                    fechadelrecibo4 = cursor.getString(cursor.getColumnIndex(FECHADELRECIBO4)),
                    principalpagador = cursor.getString(cursor.getColumnIndex(PRINCIPALPAGADOR)),
                    dnipagador = cursor.getString(cursor.getColumnIndex(DNIPAGADOR)),
                    ingresospagador = cursor.getString(cursor.getColumnIndex(INGRESOSGARANTE)),
                    catlaboralgarante = cursor.getString(cursor.getColumnIndex(CATLABORALGARANTE)),
                    declaracionjurada = cursor.getString(cursor.getColumnIndex(DECLARACIONJURADA)),
                    titularprestamos = cursor.getString(cursor.getColumnIndex(TITULARPRESTAMOS)),
                    prestamomonto = cursor.getString(cursor.getColumnIndex(PRESTAMOMONTO)),
                    plazo = cursor.getString(cursor.getColumnIndex(PLAZO)),
                    consumomensual = cursor.getString(cursor.getColumnIndex(CONSUMOMENSUAL)),
                    tarjetas = cursor.getString(cursor.getColumnIndex(TARJETAS)),
                    tipodetarjeta = cursor.getString(cursor.getColumnIndex(TIPODETARJETA)),
                    observacionessiteco = cursor.getString(cursor.getColumnIndex(OBSERVACIONESSITECO)),
                    luz = cursor.getString(cursor.getColumnIndex(LUZ)),
                    montoluz = cursor.getString(cursor.getColumnIndex(MONTOLUZ)),
                    luzformal = cursor.getString(cursor.getColumnIndex(LUZFORMAL)),
                    agua = cursor.getString(cursor.getColumnIndex(AGUA)),
                    aguamonto = cursor.getString(cursor.getColumnIndex(AGUAMONTO)),
                    aguaformal = cursor.getString(cursor.getColumnIndex(AGUAFORMAL)),
                    cable = cursor.getString(cursor.getColumnIndex(CABLE)),
                    cablemonto = cursor.getString(cursor.getColumnIndex(CABLEMONTO)),
                    cableformal = cursor.getString(cursor.getColumnIndex(CABLEFORMAL)),
                    internet = cursor.getString(cursor.getColumnIndex(INTERNET)),
                    internetmonto = cursor.getString(cursor.getColumnIndex(INTERNETMONTO)),
                    internetformal = cursor.getString(cursor.getColumnIndex(INTERNETFORMAL)),
                    observacionesservicios = cursor.getString(cursor.getColumnIndex(OBSERVACIONESSERVICIOS)),
                    cuotasocial = cursor.getString(cursor.getColumnIndex(CUOTASOCIAL)),
                    cuotaprovisoria = cursor.getString(cursor.getColumnIndex(CUOTAPROVISORIA)),
                    cuotaestandar = cursor.getString(cursor.getColumnIndex(CUOTAESTANDAR)),
                    planventa = cursor.getString(cursor.getColumnIndex(PLANVENTA)),
                    planalquiler = cursor.getString(cursor.getColumnIndex(PLANALQUILER)),
                    otrosmodalidad = cursor.getString(cursor.getColumnIndex(OTROSMODALIDAD)),
                    plazopresentacion = cursor.getString(cursor.getColumnIndex(PLAZOPRESENTACION)),
                    observacionesplazo = cursor.getString(cursor.getColumnIndex(OBSERVACIONESPLAZO)),
                    informelatitud = cursor.getString(cursor.getColumnIndex(INFORMELATITUD)),
                    informelongitud = cursor.getString(cursor.getColumnIndex(INFORMELONGITUD))
                )
                stdList.add(informe)
            } while (cursor.moveToNext())
        }
        cursor.close()

        return stdList
    }

    fun borrarInforme (titulardni: String): Int {
        val db = this.writableDatabase


        val delete = db.delete(TBL_INFORME, "titulardni=" + titulardni, null)
        db.close()
        return delete
    }

    fun getAllInformesForRV(): ArrayList<InformeModel>{
        val stdList: ArrayList<InformeModel> = ArrayList()
        val selectQuery = "SELECT $TITULARDNI, $USUARIOINFORME FROM $TBL_INFORME ORDER BY $TITULARDNI ASC;"
        val db = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var titulardni: String
        var usuario: String
        if(cursor.moveToFirst()){
            do{
                titulardni = cursor.getString(cursor.getColumnIndex("titulardni"))
                usuario = cursor.getString(cursor.getColumnIndex("usuarioinforme"))

                val std = InformeModel(titulardni = titulardni, usuario = usuario)
                stdList.add(std)
            } while (cursor.moveToNext())
        }
        cursor.close()

        return stdList
    }

    fun insertFoto (foto: FotoModel): Long {
        val fotouri = foto.fotouri.toString()

        val contentValues = ContentValues()
        contentValues.put(NOMBREFOTO, foto.nombrefoto)
        contentValues.put(FOTOURI, fotouri)
        contentValues.put(OBRAID, foto.obraid)
        contentValues.put(CONVID, foto.convid)
        contentValues.put(SYNCFOTO, foto.syncfoto)
        contentValues.put(FOTOLATITUD, foto.fotolatitud)
        contentValues.put(FOTOLONGITUD, foto.fotolongitud)
        contentValues.put(FECHAFOTO, foto.fechafoto)


        return this.writableDatabase.use { db ->
            val success = db.insert(TBL_FOTO, null, contentValues)
            success
        }
    }

    fun insertTemp (uri: String, tempgeo: String, templat: String, templong: String): Long{
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(IDTEMP, "1")
        contentValues.put(TEMPURI, uri)
        contentValues.put(TEMPGEO, tempgeo)
        contentValues.put(TEMPLAT, templat)
        contentValues.put(TEMPLONG, templong)

        val success = db.insert(TBL_TEMP, null, contentValues)
        db.close()
        return success
    }

    fun getTemp(): ArrayList<TempData>{
        val tempList: ArrayList<TempData> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_TEMP"
        val db = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var tempgeo: String
        var templat: String
        var templong: String
        var tempuri: String


        if(cursor.moveToFirst()){
            do{
                tempgeo = cursor.getString(cursor.getColumnIndex("tempgeo"))
                templat = cursor.getString(cursor.getColumnIndex("templat"))
                templong = cursor.getString(cursor.getColumnIndex("templong"))
                tempuri = cursor.getString(cursor.getColumnIndex("tempuri"))

                val temp = TempData(tempgeo = tempgeo, templat = templat, templong = templong, tempuri = tempuri )
                tempList.add(temp)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return tempList
    }



    fun borrarTemp (): Int{
        val db = this.writableDatabase


        val delete = db.delete(TBL_TEMP, "idtemp=" + "1", null)

        return delete
    }

    fun insertUsuario (usuario: UsuarioModel): Long{
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(USUARIOID, usuario.usuarioid)
        contentValues.put(USUARIOEMAIL, usuario.usuarioemail)
        contentValues.put(USUARIONOMBRE, usuario.usuarionombre)

        val success = db.insert(TBL_USUARIO, null, contentValues)
        db.close()
        return success
    }

    fun getUsuario (): ArrayList<UsuarioModel>{
        val usrList: ArrayList<UsuarioModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_USUARIO"
        val db = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var usuarioid: Int
        var usuarioemail: String
        var usuarionombre: String

        if(cursor.moveToFirst()){
            do{
                usuarioid = cursor.getInt(cursor.getColumnIndex("usuarioid"))
                usuarioemail = cursor.getString(cursor.getColumnIndex("usuarioemail"))
                usuarionombre = cursor.getString(cursor.getColumnIndex("usuarionombre"))

                val usr = UsuarioModel(usuarioid = usuarioid, usuarioemail = usuarioemail, usuarionombre = usuarionombre)
                usrList.add(usr)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return usrList
    }

    fun borrarUsuario(): Int {
        val db = this.writableDatabase


        return db.delete(TBL_USUARIO, "usuarioid=" + "1", null)
    }

    fun insertObra (std: ObraModel): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(OBRA, std.obra)
        contentValues.put(DESCRIPCION, std.descripcion)
        contentValues.put(ITEMS, std.items)
        contentValues.put(USUARIO, std.usuario)

        val success = db.insert(TBL_OBRA, null, contentValues)
        db.close()
        return success
    }



    fun insertConv (std: ObraModel): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(CONV, std.obra)
        contentValues.put(DESCRIPCION, std.descripcion)
        contentValues.put(ITEMS, std.items)
        contentValues.put(USUARIO, std.usuario)

        val success = db.insert(TBL_CONV, null, contentValues)
        db.close()
        return success
    }

    fun updateObraNumero (obraOriginal: ObraModel, obraModificada: String): Int {
        val db = this.writableDatabase

        val cv = ContentValues()
        cv.put(OBRA, obraModificada)
        cv.put(DESCRIPCION, obraOriginal.descripcion)

        val update = db.update(TBL_OBRA, cv, "id=" + obraOriginal.id, null)
        db.close()
        return update
    }

    fun updateConvNumero (obraOriginal: ObraModel, obraModificada: String): Int {
        val db = this.writableDatabase

        val cv = ContentValues()
        cv.put(CONV, obraModificada)
        cv.put(DESCRIPCION, obraOriginal.descripcion)

        val update = db.update(TBL_CONV, cv, "id=" + obraOriginal.id, null)
        db.close()
        return update
    }

    fun updateObraItems (obraOriginal: ObraModel): Int {
        val db = this.writableDatabase

        val cv = ContentValues()
        cv.put(ITEMS, obraOriginal.items)

        val update = db.update(TBL_OBRA, cv, "id=" + obraOriginal.id, null)
        db.close()
        return update
    }

    fun updateConvItems (obraOriginal: ObraModel): Int {
        val db = this.writableDatabase

        val cv = ContentValues()
        cv.put(ITEMS, obraOriginal.items)

        val update = db.update(TBL_CONV, cv, "id=" + obraOriginal.id, null)
        db.close()
        return update
    }

    fun borrarObraItems (obraOriginal: ObraModel): Int {
        val db = this.writableDatabase


        val delete = db.delete(TBL_OBRA, "id=" + obraOriginal.id, null)
        db.close()
        return delete
    }

    fun borrarAllFoto (obraid: String): Int {
        val db = this.writableDatabase


        val delete = db.delete(TBL_FOTO, "obraid=" + obraid, null)
        db.close()
        return delete
    }

    fun borrarAllFotoConv (convid: String): Int {
        val db = this.writableDatabase


        val delete = db.delete(TBL_FOTO, "convid=" + convid, null)
        db.close()
        return delete
    }

    fun borrarFoto (fotoid: String): Int {
        val db = this.writableDatabase


        val delete = db.delete(TBL_FOTO, "fotoid=" + fotoid, null)
        db.close()
        return delete
    }

    fun borrarConvItems (obraOriginal: ObraModel): Int {
        val db = this.writableDatabase


        val delete = db.delete(TBL_CONV, "id=" + obraOriginal.id, null)
        db.close()
        return delete
    }

    fun getAllObras(): ArrayList<ObraModel>{
        val stdList: ArrayList<ObraModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_OBRA ORDER BY $ID ASC;"
        val db = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var id: Int
        var obra: String
        var descripcion: String
        var items: Int
        var usuario:String
        var syncobra:Int
        if(cursor.moveToFirst()){
            do{
                id = cursor.getInt(cursor.getColumnIndex("id"))
                obra = cursor.getString(cursor.getColumnIndex("obra"))
                descripcion = cursor.getString(cursor.getColumnIndex("descripcion"))
                items = cursor.getInt(cursor.getColumnIndex("items"))
                usuario = cursor.getString(cursor.getColumnIndex("usuario"))
                syncobra = cursor.getInt(cursor.getColumnIndex("syncobra"))


                val std = ObraModel(id = id, obra = obra, descripcion = descripcion, items = items, usuario = usuario, syncobra = syncobra)
                stdList.add(std)
            } while (cursor.moveToNext())
        }
        cursor.close()

        return stdList
    }

    fun getAllConv(): ArrayList<ObraModel>{
        val stdList: ArrayList<ObraModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_CONV ORDER BY $ID ASC;"
        val db = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var id: Int
        var conv: String
        var descripcion: String
        var items: Int
        var usuario:String
        var syncconv:Int
        if(cursor.moveToFirst()){
            do{
                id = cursor.getInt(cursor.getColumnIndex("id"))
                conv = cursor.getString(cursor.getColumnIndex("conv"))
                descripcion = cursor.getString(cursor.getColumnIndex("descripcion"))
                items = cursor.getInt(cursor.getColumnIndex("items"))
                usuario = cursor.getString(cursor.getColumnIndex("usuario"))
                syncconv = cursor.getInt(cursor.getColumnIndex("syncconv"))


                val std = ObraModel(id = id, obra = conv, descripcion = descripcion, items = items, usuario = usuario, syncobra = syncconv)
                stdList.add(std)
            } while (cursor.moveToNext())
        }
        cursor.close()

        return stdList
    }

    fun updateSyncObra(obraid: String, documentReferenceId: String): Int{
        val db = this.writableDatabase

        val cv = ContentValues()
        cv.put(SYNCOBRA, 1)
        cv.put(DOCREF, documentReferenceId)

        val update = db.update(TBL_OBRA, cv, "id=$obraid", null)
        db.close()
        return update
    }

    fun updateSyncConv(obraid: String, documentReferenceId: String): Int{
        val db = this.writableDatabase

        val cv = ContentValues()
        cv.put(SYNCCONV, 1)
        cv.put(DOCREF, documentReferenceId)

        val update = db.update(TBL_CONV, cv, "id=$obraid", null)
        db.close()
        return update
    }

    fun updateSyncFoto(fotoid: String): Int{
        val db = this.writableDatabase

        val cv = ContentValues()
        cv.put(SYNCFOTO, 1)

        val update = db.update(TBL_FOTO, cv, "fotoid=$fotoid", null)
        db.close()
        return update
    }

    fun getObra(obraid: String): ArrayList<ObraModel>{
        val stdList: ArrayList<ObraModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_OBRA WHERE $ID = $obraid"
        val db = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var id: Int
        var obra: String
        var descripcion: String
        var items: Int
        var usuario:String
        var syncobra:Int
        if(cursor.moveToFirst()){
            do{
                id = cursor.getInt(cursor.getColumnIndex("id"))
                obra = cursor.getString(cursor.getColumnIndex("obra"))
                descripcion = cursor.getString(cursor.getColumnIndex("descripcion"))
                items = cursor.getInt(cursor.getColumnIndex("items"))
                usuario = cursor.getString(cursor.getColumnIndex("usuario"))
                syncobra = cursor.getInt(cursor.getColumnIndex("syncobra"))


                val std = ObraModel(id = id, obra = obra, descripcion = descripcion, items = items, usuario = usuario, syncobra = syncobra)
                stdList.add(std)
            } while (cursor.moveToNext())
        }
        cursor.close()

        return stdList
    }

    fun getConv(obraid: String): ArrayList<ObraModel>{
        val stdList: ArrayList<ObraModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_CONV WHERE $ID = $obraid"
        val db = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var id: Int
        var conv: String
        var descripcion: String
        var items: Int
        var usuario:String
        var syncconv:Int
        if(cursor.moveToFirst()){
            do{
                id = cursor.getInt(cursor.getColumnIndex("id"))
                conv = cursor.getString(cursor.getColumnIndex("conv"))
                descripcion = cursor.getString(cursor.getColumnIndex("descripcion"))
                items = cursor.getInt(cursor.getColumnIndex("items"))
                usuario = cursor.getString(cursor.getColumnIndex("usuario"))
                syncconv = cursor.getInt(cursor.getColumnIndex("syncconv"))


                val std = ObraModel(id = id, obra = conv, descripcion = descripcion, items = items, usuario = usuario, syncobra = syncconv)
                stdList.add(std)
            } while (cursor.moveToNext())
        }
        cursor.close()

        return stdList
    }

    fun getAllFotosConv(convid: String): ArrayList<FotoModel>{
        val fotoList: ArrayList<FotoModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_FOTO WHERE $CONVID = $convid"
        val db = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var fotoid: Int
        var nombrefoto: String
        var fotouri: String
        var convid: Int
        var syncfoto: Int
        if(cursor.moveToFirst()){
            do{
                fotoid = cursor.getInt(cursor.getColumnIndex("fotoid"))
                nombrefoto = cursor.getString(cursor.getColumnIndex("nombrefoto"))
                fotouri = cursor.getString(cursor.getColumnIndex("fotouri"))
                convid = cursor.getInt(cursor.getColumnIndex("convid"))
                syncfoto = cursor.getInt(cursor.getColumnIndex("syncfoto"))

                val std = FotoModel(fotoid = fotoid, nombrefoto = nombrefoto, fotouri = fotouri.toUri(), obraid = 0, convid = convid, syncfoto = syncfoto)
                fotoList.add(std)
            } while (cursor.moveToNext())
        }
        cursor.close()

        return fotoList
    }

    fun getAllFotos(obraid: String): ArrayList<FotoModel>{
        val fotoList: ArrayList<FotoModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_FOTO WHERE $OBRAID = $obraid"
        val db = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var fotoid: Int
        var nombrefoto: String
        var fotouri: String
        var obraidint: Int
        var syncfoto: Int
        if(cursor.moveToFirst()){
            do{
                fotoid = cursor.getInt(cursor.getColumnIndex("fotoid"))
                nombrefoto = cursor.getString(cursor.getColumnIndex("nombrefoto"))
                fotouri = cursor.getString(cursor.getColumnIndex("fotouri"))
                obraidint = cursor.getInt(cursor.getColumnIndex("obraid"))
                syncfoto = cursor.getInt(cursor.getColumnIndex("syncfoto"))

                val std = FotoModel(fotoid = fotoid, nombrefoto = nombrefoto, fotouri = fotouri.toUri(), obraid = obraidint, convid = 0, syncfoto = syncfoto)
                fotoList.add(std)
            } while (cursor.moveToNext())
        }
        cursor.close()

        return fotoList
    }

    fun getNumeroDeFotos(): ArrayList<FotoSizeNumber> {
        val fotoSizeList: ArrayList<FotoSizeNumber> = ArrayList()
        val selectQuery = "SELECT $FOTOID, $NOMBREFOTO, $OBRAID, $SYNCFOTO, $FOTOLATITUD, $FOTOLONGITUD FROM $TBL_FOTO WHERE $SYNCFOTO = 0;"
        val db = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var fotoid: Int
        var nombrefoto: String
        var obraidint: Int
        var syncfoto: Int
        var fotolatitud: String
        var fotolongitud: String
        if(cursor.moveToFirst()){
            do{
                fotoid = cursor.getInt(cursor.getColumnIndex("fotoid"))
                nombrefoto = cursor.getString(cursor.getColumnIndex("nombrefoto"))
                obraidint = cursor.getInt(cursor.getColumnIndex("obraid"))
                syncfoto = cursor.getInt(cursor.getColumnIndex("syncfoto"))
                fotolatitud = cursor.getString(cursor.getColumnIndex("fotolatitud"))
                fotolongitud = cursor.getString(cursor.getColumnIndex("fotolongitud"))

                val std = FotoSizeNumber(fotoid = fotoid, nombrefoto = nombrefoto, obraid = obraidint, syncfoto = syncfoto, fotolatitud = fotolatitud, fotolongitud = fotolongitud)
                fotoSizeList.add(std)
            } while (cursor.moveToNext())
        }
        cursor.close()

        return fotoSizeList
    }

    fun getFoto(fotoidasubir: String): ArrayList<FotoModel>{
        val fotoList: ArrayList<FotoModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_FOTO WHERE $FOTOID = $fotoidasubir"
        val db = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception){
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var fotoid: Int
        var nombrefoto: String
        var fotouri: String
        var obraidint: Int
        var convid: Int
        var syncfoto: Int
        var fotolatitud: String
        var fotolongitud: String
        var fechafoto: String
        if(cursor.moveToFirst()){
            do{
                fotoid = cursor.getInt(cursor.getColumnIndex("fotoid"))
                nombrefoto = cursor.getString(cursor.getColumnIndex("nombrefoto"))
                fotouri = cursor.getString(cursor.getColumnIndex("fotouri"))
                obraidint = cursor.getInt(cursor.getColumnIndex("obraid"))
                convid = cursor.getInt(cursor.getColumnIndex("convid"))
                syncfoto = cursor.getInt(cursor.getColumnIndex("syncfoto"))
                fotolatitud = cursor.getString(cursor.getColumnIndex("fotolatitud"))
                fotolongitud = cursor.getString(cursor.getColumnIndex("fotolongitud"))
                fechafoto = cursor.getString(cursor.getColumnIndex("fechafoto"))

                val std = FotoModel(fotoid = fotoid, nombrefoto = nombrefoto, fotouri = fotouri.toUri(), obraid = obraidint, convid = convid, syncfoto = syncfoto, fotolatitud = fotolatitud, fotolongitud = fotolongitud, fechafoto = fechafoto)
                fotoList.add(std)
            } while (cursor.moveToNext())
        }
        cursor.close()

        return fotoList
    }



}