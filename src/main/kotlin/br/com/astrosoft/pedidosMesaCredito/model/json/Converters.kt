package br.com.astrosoft.pedidosMesaCredito.model.json

import com.beust.klaxon.Converter
import com.beust.klaxon.JsonValue
import com.beust.klaxon.Klaxon
import com.beust.klaxon.KlaxonException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Target(AnnotationTarget.FIELD)
annotation class KlaxonDateTime

val dateTimeConverter = object : Converter {
  override fun canConvert(cls: Class<*>) = cls == LocalDateTime::class.java

  override fun fromJson(jv: JsonValue) = if (jv.string != null) {
    LocalDateTime.parse(jv.string, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
  }
  else {
    throw KlaxonException("Couldn't parse date: ${jv.string}")
  }

  override fun toJson(o: Any) = """ { "date" : $o } """
}

@Target(AnnotationTarget.FIELD)
annotation class KlaxonDate

val dateConverter = object : Converter {
  override fun canConvert(cls: Class<*>) = cls == LocalDate::class.java

  override fun fromJson(jv: JsonValue) = if (jv.string != null) {
    LocalDateTime.parse(jv.string, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
  }
  else {
    throw KlaxonException("Couldn't parse date: ${jv.string}")
  }

  override fun toJson(o: Any) = """ { "date" : $o } """
}

val klaxon =
        Klaxon().fieldConverter(KlaxonDateTime::class, dateTimeConverter)
          .fieldConverter(KlaxonDate::class, dateConverter)

private fun <T> Klaxon.convert(k: kotlin.reflect.KClass<*>,
                               fromJson: (JsonValue) -> T,
                               toJson: (T) -> String,
                               isUnion: Boolean = false) = this.converter(object : Converter {
  @Suppress("UNCHECKED_CAST")
  override fun toJson(value: Any) = toJson(value as T)
  override fun fromJson(jv: JsonValue) = fromJson(jv) as Any
  override fun canConvert(cls: Class<*>) = cls == k.java || (isUnion && cls.superclass == k.java)
})