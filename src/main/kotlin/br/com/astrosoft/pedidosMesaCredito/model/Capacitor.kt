package br.com.astrosoft.pedidosMesaCredito.model

import br.com.astrosoft.framework.util.DB
import br.com.astrosoft.framework.viewmodel.EViewModelFail
import br.com.astrosoft.pedidosMesaCredito.model.json.InputCapacitor
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import kong.unirest.Unirest
import okhttp3.ConnectionSpec
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request.Builder
import okhttp3.RequestBody
import java.io.IOException


class Capacitor {
  companion object {
    private val db = DB("saci")
    val ACCESS_KEY = db.accessKey
    val SECRET_KEY = db.secretKey
    private val COMPANY_ID = db.companyId
    private val COST_CENTER = db.costCenter
    private val JSON = MediaType.parse("application/json")
    private val URL = "https://app.capacitor.digital/api/rules/PoliticaCreditoCDCPF"


    fun execute(input: InputCapacitor, tentativas: Int): JsonObject? {
      for (i in 0..tentativas) {
        println("Tentativa $i")
        try {
          return execute(input)
        } catch (e: Throwable) {
        }
      }
      return execute(input)
    }

    fun execute(input: InputCapacitor): JsonObject? {
      return try {
        val client = OkHttpClient.Builder()
                .connectionSpecs(listOf(ConnectionSpec.MODERN_TLS,
                                        ConnectionSpec.COMPATIBLE_TLS)) //    .connectTimeout(2, TimeUnit.SECONDS)
                //    .readTimeout(2, TimeUnit.SECONDS)
                //    .writeTimeout(2, TimeUnit.SECONDS)
                .build()
        val jsonInput = input.toJons()
        println(jsonInput)
        val body = RequestBody.create(JSON, jsonInput)
        val request = Builder().url(URL)
                .addHeader("Content-Type", "application/json")
                .addHeader("Cookie",
                           "AWSALB=RmDAnNSB3/3XlGqlXbKJBlaC0uQK6zFDXozzBlPObBWqshGH02dvClyZBqJ3PRv1GcXUkzfU2fSWCh+yA4kKagLw81tShYSwaLeGpHT8U0JOo/QPset4SHPEotQF; AWSALBCORS=RmDAnNSB3/3XlGqlXbKJBlaC0uQK6zFDXozzBlPObBWqshGH02dvClyZBqJ3PRv1GcXUkzfU2fSWCh+yA4kKagLw81tShYSwaLeGpHT8U0JOo/QPset4SHPEotQF")
                .addHeader("AccessKey", ACCESS_KEY)
                .addHeader("SecretKey", SECRET_KEY) //.addHeader("X-Billing-Company-Id", COMPANY_ID)
                // .addHeader("X-Billing-Cost-Center", COST_CENTER.toString())
                .post(body)
                .build()

        val output = client.newCall(request).execute().use { response ->
          if (!response.isSuccessful) throw IOException("Unexpected code $response")

          response.body()?.string()
        } ?: return null
        println(output)
        Parser.default().parse(output.byteInputStream()) as? JsonObject
      } catch (e: Throwable) {
        e.printStackTrace()
        throw EViewModelFail(e.message ?: "Erro com o capacitor")
      }
    }

    fun executeUnirest(input: InputCapacitor) {
      val jsonInput = input.toJons()
      val response = Unirest.post(URL)
              .header("Content-Type", "application/json")
              .header("AccessKey", ACCESS_KEY)
              .header("SecretKey", SECRET_KEY)
              .header("Cookie",
                      "AWSALB=RmDAnNSB3/3XlGqlXbKJBlaC0uQK6zFDXozzBlPObBWqshGH02dvClyZBqJ3PRv1GcXUkzfU2fSWCh+yA4kKagLw81tShYSwaLeGpHT8U0JOo/QPset4SHPEotQF; AWSALBCORS=RmDAnNSB3/3XlGqlXbKJBlaC0uQK6zFDXozzBlPObBWqshGH02dvClyZBqJ3PRv1GcXUkzfU2fSWCh+yA4kKagLw81tShYSwaLeGpHT8U0JOo/QPset4SHPEotQF")
              .body(jsonInput)
              .asString()
    }

    /*
        fun executeMap(
          input: InputCapacitor, response: (JsonObject) -> Unit,
          failure: (

            Call,
            IOException,
                   ) -> Unit,
                      ) {
          val jsonInput = input.toJons()
          val body = RequestBody.create(JSON, jsonInput)
          val request = Builder().url(URL)
                  .addHeader("Content-Type", "application/json")
                  .addHeader("AccessKey", ACCESS_KEY)
                  .addHeader("SecretKey", SECRET_KEY)
                  .addHeader("X-Billing-Company-Id", COMPANY_ID)
                  .addHeader("X-Billing-Cost-Center", COST_CENTER.toString())
                  .post(body)
                  .build()

          client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
              failure(call, e)
            }

            override fun onResponse(call: Call, response: Response) {
              response.use {
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                val output = response.body()?.string() ?: ""
                val jsonObject = Parser.default().parse(output.byteInputStream()) as? JsonObject
                if (jsonObject != null) response(jsonObject)
              }
            }
          })
        }
    */
    fun link(requestIdentification: String?): String {
      val accessKey = ACCESS_KEY
      val secretKey = SECRET_KEY
      val url = "https://app.capacitor.digital/pages/relatorio-detalhado-execucao-regras/"
      val paramAccessKey = "AccessKey=$accessKey"
      val paramSecretKey = "SecretKey=$secretKey"
      val paramPolitica = "processId=PoliticaCreditoCDCPF"
      val paramId = "requestIdentification=$requestIdentification"
      return "$url?$paramAccessKey&$paramSecretKey&$paramPolitica&$paramId"
    }
  }
}

/*
fun main() {
  val home = System.getenv("HOME")
  val fileName = System.getenv("EBEAN_PROPS") ?: "$home/ebean.pintos.properties"
  System.setProperty("ebean.props.file", fileName)
  val input = InputCapacitor(
    documento = "56621787391", dataNascimento = LocalDate.of(1974, 11, 8), renda = 10900.86,
    nome = "IVANEY VIEIRA DE SALES", consultaBacen = true
  )
  val output = Capacitor.execute(input)
  println(output)
}*/