package br.com.astrosoft.pedidosMesaCredito.model

import br.com.astrosoft.framework.util.SystemUtils.getResourceAsStream
import br.com.astrosoft.framework.util.SystemUtils.readStream
import br.com.astrosoft.pedidosMesaCredito.model.beans.Contrato
import net.sf.jasperreports.engine.JasperCompileManager
import net.sf.jasperreports.engine.JasperExportManager
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource

class RelatorioContrato(val contrato: Contrato) {
  fun build(): ByteArray {
    val jasperFile = "/jasper/MyReports/contratoVenda.jasper"
    val jasperInputStream = readStream(jasperFile) ?: throw Exception("Relatorio não encontrado")
    val parameter = hashMapOf<String, Any>()
    val collection = JRBeanCollectionDataSource(listOf(contrato.copy()))

    val print = JasperFillManager.fillReport(jasperInputStream, parameter, collection)
    
    return JasperExportManager.exportReportToPdf(print) ?: ByteArray(0)
  }
}