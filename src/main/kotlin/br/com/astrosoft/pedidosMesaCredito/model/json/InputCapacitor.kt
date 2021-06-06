package br.com.astrosoft.pedidosMesaCredito.model.json

import java.time.LocalDate

data class InputCapacitor(
  val documento: String,
  val nome: String,
  val dataNascimento: LocalDate?,
  val renda: Double,
  val consultaBacen: Boolean
) {
  fun toJons(): String {
    return """
      {
         "proponente": {
            "nome": "$nome",
            "documento": "$documento", 
            "dataNascimento": "$dataNascimento",
            "renda": $renda
         },
         "indicadores": {
            "clienteAutorizaConsultaBacen": "${if (consultaBacen) "SIM" else "NAO"}"
         }
      }
    """
  }
}